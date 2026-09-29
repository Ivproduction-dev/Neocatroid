package org.catrobat.catroid.neo3d;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Neo3DSoundEngine {

    private static final String TAG = "Neo3D-Sound";
    private static final int MAX_STREAMS = 8;
    private static final float MIN_RATE = 0.5f;
    private static final float MAX_RATE = 2.0f;

    private static SoundPool soundPool;
    private static final Map<String, ActiveSound> active = new LinkedHashMap<>();

    private static final class ActiveSound {
        final String key;
        final String objectName;
        final float[] fixedPos;
        final float volume;
        final float rate;
        final boolean loop;
        final float maxDistance;
        int soundId;
        int streamId;
        boolean paused = true;

        ActiveSound(String key, String objectName, float[] fixedPos, float volume, float rate,
                boolean loop, float maxDistance) {
            this.key = key;
            this.objectName = objectName;
            this.fixedPos = fixedPos;
            this.volume = volume;
            this.rate = rate;
            this.loop = loop;
            this.maxDistance = maxDistance;
        }
    }

    private Neo3DSoundEngine() {
    }

    public static float[] computeGains(float gain, float pan) {
        float clampedPan = Math.max(-1f, Math.min(1f, pan));
        float left = gain * (1f - clampedPan);
        float right = gain * (1f + clampedPan);
        return new float[]{Math.max(0f, Math.min(1f, left)),
                Math.max(0f, Math.min(1f, right))};
    }

    public static float rateFromTonePercent(float tonePercent) {
        if (Float.isNaN(tonePercent) || Float.isInfinite(tonePercent)) {
            return 1f;
        }
        return Math.max(MIN_RATE, Math.min(MAX_RATE, tonePercent / 100f));
    }

    public static float gainFromVolumePercent(float volumePercent) {
        if (Float.isNaN(volumePercent) || Float.isInfinite(volumePercent)) {
            return 1f;
        }
        return Math.max(0f, Math.min(1f, volumePercent / 100f));
    }

    public static synchronized void play(String key, String filePath, String objectName,
            float[] fixedPos, float volumePercent, float tonePercent, boolean loop,
            float maxDistance) {
        if (key == null || key.isEmpty() || filePath == null || filePath.isEmpty()) {
            return;
        }
        stopLocked(key);
        ensurePool();
        if (soundPool == null) {
            Log.w(TAG, "no SoundPool, skipping '" + key + "'");
            return;
        }
        final ActiveSound sound = new ActiveSound(key, objectName, fixedPos,
                gainFromVolumePercent(volumePercent), rateFromTonePercent(tonePercent), loop,
                maxDistance > 0f && !Float.isNaN(maxDistance) && !Float.isInfinite(maxDistance)
                        ? maxDistance : 250f);
        active.put(key, sound);
        try {
            final int soundId = soundPool.load(filePath, 1);
            sound.soundId = soundId;
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool pool, int sampleId, int status) {
                    synchronized (Neo3DSoundEngine.class) {
                        ActiveSound current = active.get(key);
                        if (current != sound || status != 0) {
                            pool.unload(sampleId);
                            return;
                        }
                        try {
                            current.streamId = pool.play(sampleId, 0f, 0f, 1,
                                    current.loop ? -1 : 1, current.rate);
                            current.paused = false;
                        } catch (Throwable t) {
                            Log.e(TAG, "play failed: '" + key + "'", t);
                        }
                    }
                }
            });
        } catch (Throwable t) {
            Log.e(TAG, "load failed: '" + key + "'", t);
            active.remove(key);
        }
    }

    public static synchronized void stop(String key) {
        stopLocked(key);
    }

    public static synchronized void stopAll() {
        for (String key : new java.util.ArrayList<>(active.keySet())) {
            stopLocked(key);
        }
    }

    private static void stopLocked(String key) {
        ActiveSound sound = active.remove(key);
        if (sound == null || soundPool == null) {
            return;
        }
        try {
            if (sound.streamId != 0) {
                soundPool.stop(sound.streamId);
            }
            if (sound.soundId != 0) {
                soundPool.unload(sound.soundId);
            }
        } catch (Throwable t) {
            Log.e(TAG, "stop failed: '" + key + "'", t);
        }
    }

    public static void update(Neo3DEngine engine, String sceneId) {
        if (engine == null || sceneId == null) {
            return;
        }
        Neo3DScene scene;
        try {
            scene = engine.getScene(sceneId);
        } catch (Throwable t) {
            return;
        }
        if (scene == null) {
            return;
        }
        Neo3DGameObject cameraObject = null;
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getCamera() != null && obj.getCamera().isMainCamera() && obj.isActive()) {
                cameraObject = obj;
                break;
            }
        }
        if (cameraObject == null) {
            return;
        }
        float[] eye = worldPosition(cameraObject);
        float[] forward = cameraForward(cameraObject);
        float[] up = cameraUp(cameraObject);
        float[] right = cross(forward, up);
        synchronized (Neo3DSoundEngine.class) {
            Iterator<Map.Entry<String, ActiveSound>> it = active.entrySet().iterator();
            while (it.hasNext()) {
                ActiveSound sound = it.next().getValue();
                float[] source = resolveSource(scene, sound);
                if (source == null) {
                    stopLocked(sound.key);
                    it.remove();
                    continue;
                }
                float dx = source[0] - eye[0];
                float dy = source[1] - eye[1];
                float dz = source[2] - eye[2];
                float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (sound.streamId == 0) {
                    continue;
                }
                try {
                    if (dist > sound.maxDistance) {
                        if (!sound.paused) {
                            soundPool.pause(sound.streamId);
                            sound.paused = true;
                        }
                        continue;
                    }
                    if (sound.paused) {
                        soundPool.resume(sound.streamId);
                        sound.paused = false;
                    }
                    float gain = sound.volume * Math.max(0f, 1f - dist / sound.maxDistance);
                    float pan = 0f;
                    if (dist > 1e-4f) {
                        float rx = dx / dist;
                        float ry = dy / dist;
                        float rz = dz / dist;
                        pan = rx * right[0] + ry * right[1] + rz * right[2];
                    }
                    float[] gains = computeGains(gain, pan);
                    soundPool.setVolume(sound.streamId, gains[0], gains[1]);
                    if (!sound.loop) {
                        soundPool.setRate(sound.streamId, sound.rate);
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "update failed: '" + sound.key + "'", t);
                }
            }
        }
    }

    private static float[] resolveSource(Neo3DScene scene, ActiveSound sound) {
        if (sound.objectName != null && !sound.objectName.isEmpty()) {
            Neo3DGameObject obj = scene.findByName(sound.objectName);
            if (obj == null || !obj.isActive()) {
                return null;
            }
            return worldPosition(obj);
        }
        return sound.fixedPos;
    }

    private static float[] worldPosition(Neo3DGameObject obj) {
        float[] world = obj.getTransform().getWorldMatrix();
        return new float[]{world[12], world[13], world[14]};
    }

    private static float[] cameraForward(Neo3DGameObject cameraObject) {
        Neo3DCamera camera = cameraObject.getCamera();
        float[] eye = worldPosition(cameraObject);
        if (camera != null && !camera.isUseTransformOrientation()) {
            float[] target = camera.getLookAtTarget();
            float dx = target[0] - eye[0];
            float dy = target[1] - eye[1];
            float dz = target[2] - eye[2];
            float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len > 1e-6f) {
                return new float[]{dx / len, dy / len, dz / len};
            }
            return new float[]{0f, 0f, -1f};
        }
        float[] world = cameraObject.getTransform().getWorldMatrix();
        return new float[]{-world[8], -world[9], -world[10]};
    }

    private static float[] cameraUp(Neo3DGameObject cameraObject) {
        Neo3DCamera camera = cameraObject.getCamera();
        if (camera != null && !camera.isUseTransformOrientation()) {
            return camera.getUp();
        }
        float[] world = cameraObject.getTransform().getWorldMatrix();
        return new float[]{world[4], world[5], world[6]};
    }

    private static float[] cross(float[] a, float[] b) {
        return new float[]{a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]};
    }

    private static void ensurePool() {
        if (soundPool != null) {
            return;
        }
        try {
            Context context = org.catrobat.catroid.CatroidApplication.getAppContext();
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS)
                    .setAudioAttributes(attributes)
                    .build();
        } catch (Throwable t) {
            Log.e(TAG, "SoundPool init failed", t);
            soundPool = null;
        }
    }

    public static synchronized void release() {
        stopAll();
        if (soundPool != null) {
            try {
                soundPool.release();
            } catch (Throwable t) {
                Log.e(TAG, "release failed", t);
            }
            soundPool = null;
        }
    }

    public static synchronized int getActiveCount() {
        return active.size();
    }

    static synchronized void clearForTest() {
        active.clear();
    }
}
