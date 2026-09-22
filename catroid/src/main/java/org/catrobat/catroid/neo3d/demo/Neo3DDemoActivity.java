package org.catrobat.catroid.neo3d.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.catrobat.catroid.neo3d.Neo3DEngine;
import org.catrobat.catroid.neo3d.Neo3DFacade;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.backend.INeo3DBackend;
import org.catrobat.catroid.neo3d.backend.Neo3DFilamentBackend;

public class Neo3DDemoActivity extends Activity {

    private static final String TAG = "Neo3D-V2";
    private static final long STATS_INTERVAL_NS = 2_000_000_000L;

    private Neo3DEngine engine;
    private Neo3DScene scene;
    private Neo3DGameObject hero;
    private SurfaceView surfaceView;
    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (engine == null || engine.isDisposed()) {
                return;
            }
            long prevNs = lastFrameNs;
            float dt = prevNs == 0
                    ? 1f / 60f
                    : Math.min(0.1f, (frameTimeNanos - prevNs) / 1_000_000_000f);
            lastFrameNs = frameTimeNanos;
            try {
                if (prevNs != 0) {
                    long deltaNs = frameTimeNanos - prevNs;
                    if (deltaNs > 0) {
                        jitterCount++;
                        jitterSumNs += deltaNs;
                        if (deltaNs < jitterMinNs) {
                            jitterMinNs = deltaNs;
                        }
                        if (deltaNs > jitterMaxNs) {
                            jitterMaxNs = deltaNs;
                        }
                    }
                }
                if (hero != null) {
                    float[] euler = hero.getTransform().getEulerDeg();
                    hero.getTransform().setRotationEulerDeg(euler[0] + 20f * dt, euler[1], euler[2]);
                    engine.syncObject(scene.getId(), hero.getId());
                }
                engine.update(scene.getId(), dt, frameTimeNanos);
                framesSinceStats++;
                if (frameTimeNanos - lastStatsNs >= STATS_INTERVAL_NS) {
                    reportStats();
                    lastStatsNs = frameTimeNanos;
                    framesSinceStats = 0;
                }
            } catch (Throwable t) {
                Log.e(TAG, "V2 frame failed", t);
            }
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
    };
    private long lastFrameNs;
    private long lastStatsNs;
    private int framesSinceStats;
    private long jitterMinNs = Long.MAX_VALUE;
    private long jitterMaxNs;
    private long jitterSumNs;
    private int jitterCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FrameLayout root = new FrameLayout(this);
        surfaceView = new SurfaceView(this);
        root.addView(surfaceView,
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(root);

        long t0 = System.nanoTime();
        engine = Neo3DEngine.create(this, Neo3DEngine.BackendType.AUTO);
        Neo3DFacade.install(engine);
        scene = Neo3DDemoScene.buildDemoSpec();
        Neo3DDemoScene.ValidationResult validation = Neo3DDemoScene.validate(scene);
        Log.i(TAG, "Demo spec: " + validation);
        engine.registerScene(scene);
        hero = scene.findByName("V2_Hero");
        engine.setObjectModelBytes(scene.getId(), hero.getId(),
                Neo3DDemoScene.HERO_MODEL_KEY, Neo3DEmbeddedCube.buildDefaultGlb());
        Neo3DGameObject ground = scene.findByName("V2_Ground");
        engine.setObjectModelBytes(scene.getId(), ground.getId(),
                Neo3DDemoScene.GROUND_MODEL_KEY,
                Neo3DEmbeddedCube.buildGlb(2f, new float[]{0.3f, 0.34f, 0.4f, 1f}));
        long loadMs = (System.nanoTime() - t0) / 1000000L;

        INeo3DBackend backend = engine.getBackend();
        Log.i(TAG, "V2 demo ready: backend=" + backend.getName()
                + " initialized=" + backend.isInitialized()
                + " sceneLoadMs=" + loadMs
                + " objects=" + scene.getObjectCount());
        if (backend instanceof Neo3DFilamentBackend) {
            ((Neo3DFilamentBackend) backend).attachSurfaceView(surfaceView);
        }
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (engine != null && !engine.isDisposed()) {
                    engine.resize(width, height);
                    Log.i(TAG, "V2 surface: " + width + "x" + height);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        lastStatsNs = System.nanoTime();
        Choreographer.getInstance().postFrameCallback(frameCallback);
    }

    private void reportStats() {
        INeo3DBackend.BackendStats stats = engine.getBackend().getStats();
        Runtime rt = Runtime.getRuntime();
        long usedMb = (rt.totalMemory() - rt.freeMemory()) / 1048576L;
        float jitterAvgMs = jitterCount > 0 ? (float) jitterSumNs / jitterCount / 1000000f : 0f;
        float jitterMinMs = jitterMinNs == Long.MAX_VALUE ? 0f : (float) jitterMinNs / 1000000f;
        float jitterMaxMs = (float) jitterMaxNs / 1000000f;
        Log.i(TAG, String.format(
                "V2 stats: fpsEma=%.1f specMs=%.3f backendFrames=%d backendMs=%.3f(%s) mem=%dMB jitterAvg=%.2fms jitterMin=%.2fms jitterMax=%.2fms",
                engine.getFpsEma(), engine.getFrameMsEma(), stats.framesRendered,
                stats.avgFrameMs, stats.detail, usedMb, jitterAvgMs, jitterMinMs, jitterMaxMs));
        jitterMinNs = Long.MAX_VALUE;
        jitterMaxNs = 0;
        jitterSumNs = 0;
        jitterCount = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (engine != null && !engine.isDisposed()) {
            engine.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (engine != null && !engine.isDisposed()) {
            engine.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Choreographer.getInstance().removeFrameCallback(frameCallback);
        Neo3DFacade.uninstall();
        if (engine != null) {
            try {
                engine.dispose();
            } catch (Throwable t) {
                Log.e(TAG, "V2 dispose failed", t);
            }
            engine = null;
        }
        super.onDestroy();
    }
}
