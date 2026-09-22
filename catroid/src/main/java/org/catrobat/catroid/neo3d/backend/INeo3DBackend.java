package org.catrobat.catroid.neo3d.backend;

import android.content.Context;

import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.Neo3DSkybox;

public interface INeo3DBackend {

    String getName();

    boolean isAvailable();

    void initialize(Context context);

    boolean isInitialized();

    void loadScene(Neo3DScene scene);

    void unloadScene(String sceneId);

    void syncObject(String sceneId, Neo3DGameObject obj);

    void removeObject(String sceneId, String objectId);

    void loadModelBytes(String sceneId, String objectId, byte[] data, String sourceName);

    void applySkybox(String sceneId, Neo3DSkybox skybox);

    void tickAnimations(String sceneId, float deltaSec);

    void resize(int width, int height);

    void renderFrame(float deltaSec, long frameTimeNanos);

    void onResume();

    void onPause();

    BackendStats getStats();

    int getLoadedModelCount();

    void dispose();

    class BackendStats {
        public long framesRendered;
        public float lastFrameMs;
        public float avgFrameMs;
        public int liveScenes;
        public int liveObjects;
        public String detail = "";
    }
}
