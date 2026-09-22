package org.catrobat.catroid.neo3d.backend;

import android.content.Context;

import org.catrobat.catroid.neo3d.Neo3DAnimationClip;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.Neo3DSkybox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Neo3DNullBackend implements INeo3DBackend {

    private boolean initialized;
    private final Set<String> scenes = new HashSet<>();
    private final Map<String, Set<String>> objectsByScene = new HashMap<>();
    private final Map<String, Float> animationTimeByObject = new HashMap<>();
    private final List<String> callLog = new ArrayList<>();
    private final BackendStats stats = new BackendStats();
    private int width;
    private int height;
    private boolean paused;

    @Override
    public String getName() {
        return "Null";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void initialize(Context context) {
        initialized = true;
        callLog.add("initialize");
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void loadScene(Neo3DScene scene) {
        requireInit();
        scenes.add(scene.getId());
        objectsByScene.put(scene.getId(), new HashSet<String>());
        stats.liveScenes = scenes.size();
        callLog.add("loadScene:" + scene.getId());
    }

    @Override
    public void unloadScene(String sceneId) {
        scenes.remove(sceneId);
        Set<String> objs = objectsByScene.remove(sceneId);
        if (objs != null) {
            for (String id : objs) {
                animationTimeByObject.remove(sceneId + "/" + id);
            }
        }
        stats.liveScenes = scenes.size();
        stats.liveObjects = countObjects();
        callLog.add("unloadScene:" + sceneId);
    }

    @Override
    public void syncObject(String sceneId, Neo3DGameObject obj) {
        requireInit();
        Set<String> objs = objectsByScene.get(sceneId);
        if (objs == null) {
            return;
        }
        objs.add(obj.getId());
        stats.liveObjects = countObjects();
        if (obj.getModelPath() != null && obj.getAnimationState() != null) {
            animationTimeByObject.put(sceneId + "/" + obj.getId(),
                    obj.getAnimationState().getTimeSec());
        }
        callLog.add("syncObject:" + sceneId + "/" + obj.getId());
    }

    @Override
    public void removeObject(String sceneId, String objectId) {
        Set<String> objs = objectsByScene.get(sceneId);
        if (objs != null) {
            objs.remove(objectId);
        }
        animationTimeByObject.remove(sceneId + "/" + objectId);
        stats.liveObjects = countObjects();
        callLog.add("removeObject:" + sceneId + "/" + objectId);
    }

    @Override
    public void loadModelBytes(String sceneId, String objectId, byte[] data, String sourceName) {
        callLog.add("loadModelBytes:" + sceneId + "/" + objectId + ":"
                + (data == null ? 0 : data.length));
    }

    @Override
    public void applySkybox(String sceneId, Neo3DSkybox skybox) {
        callLog.add("applySkybox:" + sceneId + ":" + skybox.getMode());
    }

    @Override
    public void tickAnimations(String sceneId, float deltaSec) {
        Set<String> objs = objectsByScene.get(sceneId);
        if (objs == null) {
            return;
        }
        for (String id : objs) {
            String key = sceneId + "/" + id;
            Float t = animationTimeByObject.get(key);
            if (t != null) {
                animationTimeByObject.put(key, t + deltaSec);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        callLog.add("resize:" + width + "x" + height);
    }

    @Override
    public void renderFrame(float deltaSec, long frameTimeNanos) {
        long start = System.nanoTime();
        float dummy = 0f;
        for (int i = 0; i < 256; i++) {
            dummy += (float) Math.sin(i * 0.01f);
        }
        if (dummy == 12345.678f) {
            callLog.add("impossible");
        }
        long elapsedMs = (System.nanoTime() - start) / 1000000L;
        stats.framesRendered++;
        stats.lastFrameMs = elapsedMs;
        if (stats.avgFrameMs == 0f) {
            stats.avgFrameMs = elapsedMs;
        } else {
            stats.avgFrameMs = stats.avgFrameMs * 0.9f + elapsedMs * 0.1f;
        }
        stats.detail = width + "x" + height + (paused ? " paused" : "");
    }

    @Override
    public void onResume() {
        paused = false;
    }

    @Override
    public void onPause() {
        paused = true;
    }

    @Override
    public BackendStats getStats() {
        return stats;
    }

    @Override
    public synchronized int getLoadedModelCount() {
        return 0;
    }

    @Override
    public void dispose() {
        scenes.clear();
        objectsByScene.clear();
        animationTimeByObject.clear();
        stats.liveScenes = 0;
        stats.liveObjects = 0;
        initialized = false;
        callLog.add("dispose");
    }

    public List<String> getCallLog() {
        return new ArrayList<>(callLog);
    }

    public boolean hasScene(String sceneId) {
        return scenes.contains(sceneId);
    }

    public boolean hasObject(String sceneId, String objectId) {
        Set<String> objs = objectsByScene.get(sceneId);
        return objs != null && objs.contains(objectId);
    }

    private int countObjects() {
        int n = 0;
        for (Set<String> s : objectsByScene.values()) {
            n += s.size();
        }
        return n;
    }

    private void requireInit() {
        if (!initialized) {
            throw new IllegalStateException("Neo3DNullBackend not initialized");
        }
    }
}
