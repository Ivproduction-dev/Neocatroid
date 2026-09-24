/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.stage;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.MediaController;
import android.widget.VideoView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;

public class ChromaKeyVideoView extends VideoView {

	private static final String TAG = "ChromaKeyVideo";

	private static final String VERTEX_SHADER =
			"attribute vec4 aPosition;\n"
			+ "attribute vec2 aTexCoord;\n"
			+ "varying vec2 vTexCoord;\n"
			+ "uniform mat4 uSTMatrix;\n"
			+ "void main() {\n"
			+ "  gl_Position = aPosition;\n"
			+ "  vTexCoord = (uSTMatrix * vec4(aTexCoord, 0.0, 1.0)).xy;\n"
			+ "}\n";

	private static final String FRAGMENT_SHADER =
			"#extension GL_OES_EGL_image_external : require\n"
			+ "precision mediump float;\n"
			+ "varying vec2 vTexCoord;\n"
			+ "uniform samplerExternalOES sTexture;\n"
			+ "uniform vec3 uKeyColor;\n"
			+ "uniform float uThreshold;\n"
			+ "uniform float uSmoothing;\n"
			+ "void main() {\n"
			+ "  vec4 color = texture2D(sTexture, vTexCoord);\n"
			+ "  float dist = distance(color.rgb, uKeyColor);\n"
			+ "  float alpha = smoothstep(uThreshold, uThreshold + uSmoothing, dist);\n"
			+ "  gl_FragColor = vec4(color.rgb, color.a * alpha);\n"
			+ "}\n";

	private static final float SMOOTHING = 0.12f;
	private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

	private static final float[] QUAD_POSITIONS = {
			-1f, -1f,
			1f, -1f,
			-1f, 1f,
			1f, 1f
	};

	private static final float[] QUAD_TEXCOORDS = {
			0f, 0f,
			1f, 0f,
			0f, 1f,
			1f, 1f
	};

	private MediaPlayer mediaPlayer;
	private MediaPlayer.OnPreparedListener preparedListener;
	private MediaController mediaController;

	private HandlerThread glThread;
	private Handler glHandler;

	private EGLDisplay eglDisplay = EGL14.EGL_NO_DISPLAY;
	private EGLContext eglContext = EGL14.EGL_NO_CONTEXT;
	private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;

	private SurfaceTexture surfaceTexture;
	private Surface renderSurface;
	private int oesTextureId = -1;
	private int program = -1;
	private int positionHandle = -1;
	private int texCoordHandle = -1;
	private int textureHandle = -1;
	private int keyColorHandle = -1;
	private int thresholdHandle = -1;
	private int smoothingHandle = -1;
	private int stMatrixHandle = -1;
	private FloatBuffer positionBuffer;
	private FloatBuffer texCoordBuffer;
	private final float[] stMatrix = new float[16];

	private volatile boolean surfaceReady;
	private volatile boolean playerReleased = true;
	private volatile int viewWidth = 1;
	private volatile int viewHeight = 1;
	private volatile int videoWidth;
	private volatile int videoHeight;

	private String pendingPath;
	private Uri pendingUri;
	private Map<String, String> pendingHeaders;

	private float keyR;
	private float keyG;
	private float keyB;
	private volatile float keyThreshold;

	public ChromaKeyVideoView(Context context) {
		super(context);
		init();
	}

	public ChromaKeyVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ChromaKeyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		getHolder().addCallback(holderCallback);
		glThread = new HandlerThread("ChromaKeyGL");
		glThread.start();
		glHandler = new Handler(glThread.getLooper());
		positionBuffer = createFloatBuffer(QUAD_POSITIONS);
		texCoordBuffer = createFloatBuffer(QUAD_TEXCOORDS);
	}

	public void setKeyColor(int packedRgb) {
		keyR = ((packedRgb >> 16) & 0xFF) / 255f;
		keyG = ((packedRgb >> 8) & 0xFF) / 255f;
		keyB = (packedRgb & 0xFF) / 255f;
	}

	public void setKeyTolerance(float tolerance) {
		keyThreshold = Math.max(0f, tolerance);
	}

	private static FloatBuffer createFloatBuffer(float[] data) {
		FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		buffer.put(data);
		buffer.position(0);
		return buffer;
	}

	private final SurfaceHolder.Callback holderCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			viewWidth = Math.max(1, getWidth());
			viewHeight = Math.max(1, getHeight());
			glHandler.post(() -> initGl(holder.getSurface()));
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			viewWidth = Math.max(1, width);
			viewHeight = Math.max(1, height);
			glHandler.post(() -> clearTransparent());
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			glHandler.post(() -> releaseGl());
			releasePlayer();
		}
	};

	private void initGl(Surface surface) {
		releaseGl();
		try {
			eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
			int[] version = new int[2];
			EGL14.eglInitialize(eglDisplay, version, 0, version, 1);
			int[] configAttributes = {
					EGL14.EGL_RED_SIZE, 8,
					EGL14.EGL_GREEN_SIZE, 8,
					EGL14.EGL_BLUE_SIZE, 8,
					EGL14.EGL_ALPHA_SIZE, 8,
					EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
					EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT,
					EGL14.EGL_NONE
			};
			EGLConfig[] configs = new EGLConfig[1];
			int[] configCount = new int[1];
			EGL14.eglChooseConfig(eglDisplay, configAttributes, 0, configs, 0, 1, configCount, 0);
			int[] contextAttributes = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
			eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], EGL14.EGL_NO_CONTEXT,
					contextAttributes, 0);
			int[] surfaceAttributes = {EGL14.EGL_NONE};
			eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, configs[0], surface,
					surfaceAttributes, 0);
			EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
			program = linkProgram(VERTEX_SHADER, FRAGMENT_SHADER);
			positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
			texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord");
			textureHandle = GLES20.glGetUniformLocation(program, "sTexture");
			keyColorHandle = GLES20.glGetUniformLocation(program, "uKeyColor");
			thresholdHandle = GLES20.glGetUniformLocation(program, "uThreshold");
			smoothingHandle = GLES20.glGetUniformLocation(program, "uSmoothing");
			stMatrixHandle = GLES20.glGetUniformLocation(program, "uSTMatrix");
			int[] textures = new int[1];
			GLES20.glGenTextures(1, textures, 0);
			oesTextureId = textures[0];
			GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, oesTextureId);
			GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			surfaceTexture = new SurfaceTexture(oesTextureId);
			surfaceTexture.setOnFrameAvailableListener(
					texture -> glHandler.post(ChromaKeyVideoView.this::renderFrame),
					glHandler);
			renderSurface = new Surface(surfaceTexture);
			surfaceReady = true;
			clearTransparent();
			post(this::createPlayer);
		} catch (Throwable t) {
			Log.e(TAG, "initGl failed", t);
			releaseGl();
		}
	}

	private void clearTransparent() {
		if (eglDisplay == EGL14.EGL_NO_DISPLAY || eglSurface == EGL14.EGL_NO_SURFACE) {
			return;
		}
		try {
			EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
			GLES20.glClearColor(0f, 0f, 0f, 0f);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			EGL14.eglSwapBuffers(eglDisplay, eglSurface);
		} catch (Throwable t) {
			Log.e(TAG, "clearTransparent failed", t);
		}
	}

	private void renderFrame() {
		if (!surfaceReady || playerReleased) {
			return;
		}
		if (eglDisplay == EGL14.EGL_NO_DISPLAY || eglSurface == EGL14.EGL_NO_SURFACE
				|| surfaceTexture == null || program < 0) {
			return;
		}
		try {
			EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
			surfaceTexture.updateTexImage();
			surfaceTexture.getTransformMatrix(stMatrix);
			int surfaceW = viewWidth;
			int surfaceH = viewHeight;
			int viewportW = surfaceW;
			int viewportH = surfaceH;
			int viewportX = 0;
			int viewportY = 0;
			if (videoWidth > 0 && videoHeight > 0) {
				float videoAspect = (float) videoWidth / videoHeight;
				float surfaceAspect = (float) surfaceW / surfaceH;
				if (videoAspect > surfaceAspect) {
					viewportH = Math.max(1, (int) (surfaceW / videoAspect));
					viewportY = (surfaceH - viewportH) / 2;
				} else {
					viewportW = Math.max(1, (int) (surfaceH * videoAspect));
					viewportX = (surfaceW - viewportW) / 2;
				}
			}
			GLES20.glViewport(0, 0, surfaceW, surfaceH);
			GLES20.glClearColor(0f, 0f, 0f, 0f);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			GLES20.glViewport(viewportX, viewportY, viewportW, viewportH);
			GLES20.glUseProgram(program);
			GLES20.glEnableVertexAttribArray(positionHandle);
			GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0,
					positionBuffer);
			GLES20.glEnableVertexAttribArray(texCoordHandle);
			GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
					texCoordBuffer);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, oesTextureId);
			GLES20.glUniform1i(textureHandle, 0);
			GLES20.glUniform3f(keyColorHandle, keyR, keyG, keyB);
			GLES20.glUniform1f(thresholdHandle, keyThreshold);
			GLES20.glUniform1f(smoothingHandle, SMOOTHING);
			GLES20.glUniformMatrix4fv(stMatrixHandle, 1, false, stMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			GLES20.glDisableVertexAttribArray(positionHandle);
			GLES20.glDisableVertexAttribArray(texCoordHandle);
			GLES20.glViewport(0, 0, surfaceW, surfaceH);
			EGL14.eglSwapBuffers(eglDisplay, eglSurface);
		} catch (Throwable t) {
			Log.e(TAG, "renderFrame failed", t);
		}
	}

	private int compileShader(int type, String source) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, source);
		GLES20.glCompileShader(shader);
		int[] compiled = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0) {
			Log.e(TAG, "shader compile failed: " + GLES20.glGetShaderInfoLog(shader));
			GLES20.glDeleteShader(shader);
			return -1;
		}
		return shader;
	}

	private int linkProgram(String vertexSource, String fragmentSource) {
		int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		int linkedProgram = GLES20.glCreateProgram();
		GLES20.glAttachShader(linkedProgram, vertexShader);
		GLES20.glAttachShader(linkedProgram, fragmentShader);
		GLES20.glLinkProgram(linkedProgram);
		int[] linked = new int[1];
		GLES20.glGetProgramiv(linkedProgram, GLES20.GL_LINK_STATUS, linked, 0);
		if (linked[0] == 0) {
			Log.e(TAG, "program link failed: " + GLES20.glGetProgramInfoLog(linkedProgram));
			GLES20.glDeleteProgram(linkedProgram);
			return -1;
		}
		return linkedProgram;
	}

	private void releaseGl() {
		surfaceReady = false;
		try {
			if (renderSurface != null) {
				renderSurface.release();
				renderSurface = null;
			}
		} catch (Throwable ignored) {
		}
		try {
			if (surfaceTexture != null) {
				surfaceTexture.release();
				surfaceTexture = null;
			}
		} catch (Throwable ignored) {
		}
		try {
			if (oesTextureId >= 0) {
				EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
						EGL14.EGL_NO_CONTEXT);
				int[] textures = {oesTextureId};
				GLES20.glDeleteTextures(1, textures, 0);
				oesTextureId = -1;
			}
		} catch (Throwable ignored) {
		}
		try {
			if (program >= 0) {
				GLES20.glDeleteProgram(program);
				program = -1;
			}
		} catch (Throwable ignored) {
		}
		try {
			if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
				if (eglSurface != EGL14.EGL_NO_SURFACE) {
					EGL14.eglDestroySurface(eglDisplay, eglSurface);
					eglSurface = EGL14.EGL_NO_SURFACE;
				}
				if (eglContext != EGL14.EGL_NO_CONTEXT) {
					EGL14.eglDestroyContext(eglDisplay, eglContext);
					eglContext = EGL14.EGL_NO_CONTEXT;
				}
				EGL14.eglTerminate(eglDisplay);
				eglDisplay = EGL14.EGL_NO_DISPLAY;
			}
		} catch (Throwable ignored) {
		}
	}

	private void createPlayer() {
		if (!surfaceReady || renderSurface == null || !playerReleased) {
			return;
		}
		if (pendingPath == null && pendingUri == null) {
			return;
		}
		releasePlayer();
		try {
			MediaPlayer player = new MediaPlayer();
			player.setSurface(renderSurface);
			if (pendingUri != null) {
				if (pendingHeaders != null) {
					player.setDataSource(getContext(), pendingUri, pendingHeaders);
				} else {
					player.setDataSource(getContext(), pendingUri);
				}
			} else {
				player.setDataSource(pendingPath);
			}
			player.setOnPreparedListener(mp -> {
				videoWidth = mp.getVideoWidth();
				videoHeight = mp.getVideoHeight();
				playerReleased = false;
				syncSuperMediaPlayer(mp);
				if (preparedListener != null) {
					preparedListener.onPrepared(mp);
				}
			});
			player.setOnCompletionListener(mp -> glHandler.post(() -> clearTransparent()));
			player.prepareAsync();
			mediaPlayer = player;
			syncSuperMediaPlayer(player);
		} catch (Throwable t) {
			Log.e(TAG, "createPlayer failed", t);
			releasePlayer();
		}
	}

	private void syncSuperMediaPlayer(MediaPlayer player) {
		try {
			java.lang.reflect.Field field =
					VideoView.class.getDeclaredField("mMediaPlayer");
			field.setAccessible(true);
			field.set(this, player);
		} catch (Throwable ignored) {
		}
	}

	private void releasePlayer() {
		playerReleased = true;
		syncSuperMediaPlayer(null);
		try {
			if (mediaPlayer != null) {
				mediaPlayer.reset();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		} catch (Throwable ignored) {
		}
	}

	@Override
	public void setVideoPath(String path) {
		pendingPath = path;
		pendingUri = null;
		videoWidth = 0;
		videoHeight = 0;
		if (surfaceReady) {
			createPlayer();
		}
	}

	@Override
	public void setVideoURI(Uri uri) {
		pendingUri = uri;
		pendingPath = null;
		pendingHeaders = null;
		videoWidth = 0;
		videoHeight = 0;
		if (surfaceReady) {
			createPlayer();
		}
	}

	@Override
	public void setVideoURI(Uri uri, Map<String, String> headers) {
		pendingUri = uri;
		pendingPath = null;
		pendingHeaders = headers;
		videoWidth = 0;
		videoHeight = 0;
		if (surfaceReady) {
			createPlayer();
		}
	}

	@Override
	public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
		preparedListener = listener;
		if (mediaPlayer != null && !playerReleased) {
			try {
				listener.onPrepared(mediaPlayer);
			} catch (Throwable ignored) {
			}
		}
	}

	@Override
	public void setMediaController(MediaController controller) {
		mediaController = controller;
		if (mediaController != null) {
			mediaController.setMediaPlayer(this);
			mediaController.setAnchorView(this);
		}
	}

	@Override
	public void start() {
		try {
			if (mediaPlayer != null && !playerReleased) {
				mediaPlayer.start();
			}
		} catch (Throwable t) {
			Log.e(TAG, "start failed", t);
		}
	}

	@Override
	public void pause() {
		try {
			if (mediaPlayer != null && !playerReleased) {
				mediaPlayer.pause();
			}
		} catch (Throwable t) {
			Log.e(TAG, "pause failed", t);
		}
	}

	@Override
	public void seekTo(int msec) {
		try {
			if (mediaPlayer != null && !playerReleased) {
				mediaPlayer.seekTo(msec);
			}
		} catch (Throwable t) {
			Log.e(TAG, "seekTo failed", t);
		}
	}

	@Override
	public int getDuration() {
		try {
			if (mediaPlayer != null && !playerReleased) {
				return mediaPlayer.getDuration();
			}
		} catch (Throwable ignored) {
		}
		return -1;
	}

	@Override
	public int getCurrentPosition() {
		try {
			if (mediaPlayer != null && !playerReleased) {
				return mediaPlayer.getCurrentPosition();
			}
		} catch (Throwable ignored) {
		}
		return 0;
	}

	@Override
	public boolean isPlaying() {
		try {
			if (mediaPlayer != null && !playerReleased) {
				return mediaPlayer.isPlaying();
			}
		} catch (Throwable ignored) {
		}
		return false;
	}

	@Override
	public int getBufferPercentage() {
		return 100;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public void stopPlayback() {
		releasePlayer();
		glHandler.post(() -> clearTransparent());
	}

	@Override
	public void suspend() {
		releasePlayer();
	}
}
