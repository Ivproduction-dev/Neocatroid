package org.catrobat.catroid.neo3d;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import org.catrobat.catroid.neo3d.assets.Neo3DAssetManager;
import org.catrobat.catroid.neo3d.backend.INeo3DBackend;
import org.catrobat.catroid.neo3d.backend.MainThreadRenderDispatcher;
import org.catrobat.catroid.neo3d.backend.DirectRenderDispatcher;
import org.catrobat.catroid.neo3d.backend.Neo3DFilamentBackend;
import org.catrobat.catroid.neo3d.backend.Neo3DNullBackend;
import org.catrobat.catroid.neo3d.backend.RenderDispatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Neo3DEngine {

    private static final String TAG = "Neo3D-V2";

    public enum BackendType {
        AUTO,
        FILAMENT,
        NULL
    }

    private final INeo3DBackend backend;
    private final RenderDispatcher dispatcher;
    private final Neo3DAssetManager assetManager = new Neo3DAssetManager();
    private final Map<String, Neo3DScene> scenes = new LinkedHashMap<>();
    private boolean disposed;
    private String activeSceneId;

    private float fpsEma;
    private float frameMsEma;
    private long updateCount;
    private int viewWidth = 1;
    private int viewHeight = 1;

    private Neo3DEngine(INeo3DBackend backend, RenderDispatcher dispatcher) {
        this.backend = backend;
        this.dispatcher = dispatcher;
    }

    public static Neo3DEngine create(Context context, BackendType type) {
        if (type != BackendType.NULL) {
            Neo3DFilamentBackend filament = new Neo3DFilamentBackend();
            if (type == BackendType.AUTO && !filament.isAvailable()) {
                Log.i(TAG, "Filament not available, falling back to Null backend");
            } else {
                try {
                    return createWithBackend(filament, context);
                } catch (Exception | LinkageError e) {
                    Log.e(TAG, "Filament backend failed, falling back to Null backend", e);
                    try {
                        filament.dispose();
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return createWithBackend(new Neo3DNullBackend(), context);
    }

    private static Neo3DEngine createWithBackend(INeo3DBackend backend, Context context) {
        backend.initialize(context);
        if (!backend.isInitialized()) {
            throw new IllegalStateException("Backend failed to initialize: " + backend.getName());
        }
        RenderDispatcher dispatcher = backend instanceof Neo3DFilamentBackend
                ? new MainThreadRenderDispatcher()
                : new DirectRenderDispatcher();
        Neo3DEngine engine = new Neo3DEngine(backend, dispatcher);
        Log.i(TAG, "Neo3DEngine created with backend=" + backend.getName());
        return engine;
    }

    public INeo3DBackend getBackend() {
        return backend;
    }

    public int getLoadedModelCount() {
        return backend.getLoadedModelCount();
    }

    public Neo3DAssetManager getAssetManager() {
        return assetManager;
    }

    public Neo3DScene createScene(String name) {
        checkAlive();
        Neo3DScene scene = new Neo3DScene(name);
        scenes.put(scene.getId(), scene);
        dispatcher.dispatch(() -> {
            backend.loadScene(scene);
            backend.applySkybox(scene.getId(), scene.getSkybox());
        });
        activeSceneId = scene.getId();
        return scene;
    }

    public Neo3DScene registerScene(Neo3DScene scene) {
        checkAlive();
        scenes.put(scene.getId(), scene);
        dispatcher.dispatch(() -> {
            backend.loadScene(scene);
            backend.applySkybox(scene.getId(), scene.getSkybox());
            for (Neo3DGameObject obj : scene.getAllObjects()) {
                if (obj.getModelPath() != null) {
                    onModelPathAssigned(scene, obj);
                }
            }
        });
        activeSceneId = scene.getId();
        return scene;
    }

    public boolean deleteScene(String sceneId) {
        Neo3DScene scene = scenes.remove(sceneId);
        if (scene == null) {
            return false;
        }
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            releaseObjectAssets(obj);
        }
        dispatcher.dispatch(() -> backend.unloadScene(sceneId));
        if (sceneId.equals(activeSceneId)) {
            activeSceneId = null;
        }
        return true;
    }

    public Neo3DScene getScene(String sceneId) {
        return scenes.get(sceneId);
    }

    public String getActiveSceneId() {
        return activeSceneId;
    }

    public Collection<Neo3DScene> getAllScenes() {
        return Collections.unmodifiableCollection(scenes.values());
    }

    public Neo3DGameObject createObject(String sceneId, String name) {
        Neo3DScene scene = requireScene(sceneId);
        Neo3DGameObject existing = scene.findByName(name);
        if (existing != null) {
            removeObject(sceneId, existing.getId());
        }
        Neo3DGameObject obj = scene.createObject(name);
        dispatcher.dispatch(() -> backend.syncObject(sceneId, obj));
        return obj;
    }

    public boolean removeObject(String sceneId, String objectId) {
        Neo3DScene scene = scenes.get(sceneId);
        if (scene == null) {
            return false;
        }
        Neo3DGameObject obj = scene.getObject(objectId);
        if (obj == null) {
            return false;
        }
        releaseObjectAssets(obj);
        dispatcher.dispatch(() -> backend.removeObject(sceneId, objectId));
        return scene.removeObject(objectId);
    }

    public void syncObject(String sceneId, String objectId) {
        Neo3DScene scene = requireScene(sceneId);
        Neo3DGameObject obj = scene.getObject(objectId);
        if (obj != null) {
            dispatcher.dispatch(() -> backend.syncObject(sceneId, obj));
        }
    }

    public void syncScene(String sceneId) {
        Neo3DScene scene = requireScene(sceneId);
        dispatcher.dispatch(() -> {
            backend.applySkybox(sceneId, scene.getSkybox());
            for (Neo3DGameObject obj : scene.getAllObjects()) {
                backend.syncObject(sceneId, obj);
            }
        });
    }

    public void setObjectModelBytes(String sceneId, String objectId, String assetKey,
            byte[] glbBytes) {
        Neo3DScene scene = requireScene(sceneId);
        Neo3DGameObject obj = scene.getObject(objectId);
        if (obj == null) {
            return;
        }
        releaseObjectAssets(obj);
        if (glbBytes == null) {
            obj.setModelPath(null);
            dispatcher.dispatch(() -> backend.syncObject(sceneId, obj));
            return;
        }
        long startMs = System.currentTimeMillis();
        assetManager.acquire(Neo3DAssetManager.AssetKind.MODEL, assetKey, 0, glbBytes.length);
        long loadMs = System.currentTimeMillis() - startMs;
        obj.setModelPath(assetKey);
        if (Neo3DAssetManager.hasUnsupportedGlbExtension(glbBytes)) {
            Log.w(TAG, "Model " + assetKey + " uses unsupported glTF extensions "
                    + "(draco/meshopt/basis); backend will try anyway, took " + loadMs + "ms");
        }
        dispatcher.dispatch(() -> backend.loadModelBytes(sceneId, objectId, glbBytes, assetKey));
    }

    private void onModelPathAssigned(Neo3DScene scene, Neo3DGameObject obj) {
        if (obj.getModelPath() != null) {
            assetManager.acquire(Neo3DAssetManager.AssetKind.MODEL, obj.getModelPath(), 0, 0);
        }
    }

    private void releaseObjectAssets(Neo3DGameObject obj) {
        if (obj.getModelPath() != null) {
            assetManager.release(obj.getModelPath());
        }
    }

    public float update(String sceneId, float deltaSec, long frameTimeNanos) {
        checkAlive();
        Neo3DScene scene = scenes.get(sceneId);
        if (scene != null) {
            for (Neo3DGameObject obj : scene.getAllObjects()) {
                if (obj.getAnimationState() != null) {
                    obj.getAnimationState().tick(deltaSec);
                }
            }
            dispatcher.dispatch(() -> backend.tickAnimations(sceneId, deltaSec));
        }
        if (backend instanceof Neo3DFilamentBackend && backend.getLoadedModelCount() == 0) {
            return 0f;
        }
        dispatcher.dispatch(() -> backend.renderFrame(deltaSec, frameTimeNanos));
        float frameMs = backend.getStats().lastFrameMs;
        updateCount++;
        if (deltaSec > 0f) {
            float fps = 1f / deltaSec;
            fpsEma = updateCount == 1 ? fps : fpsEma * 0.95f + fps * 0.05f;
        }
        frameMsEma = updateCount == 1 ? frameMs : frameMsEma * 0.95f + frameMs * 0.05f;
        return frameMs;
    }

    public float getFpsEma() {
        return fpsEma;
    }

    public float getFrameMsEma() {
        return frameMsEma;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public void resize(int width, int height) {
        viewWidth = Math.max(1, width);
        viewHeight = Math.max(1, height);
        dispatcher.dispatch(() -> backend.resize(width, height));
    }

    public String pickObject(String sceneId, float touchX, float touchY) {
        Neo3DScene scene = scenes.get(sceneId);
        if (scene == null) {
            return null;
        }
        Neo3DGameObject cameraObject = null;
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getCamera() != null && obj.getCamera().isMainCamera() && obj.isActive()) {
                cameraObject = obj;
                break;
            }
        }
        return Neo3DPicker.pickObject(scene, cameraObject, viewWidth, viewHeight, touchX,
                touchY);
    }

    public void attachSurfaceView(SurfaceView view) {
        if (backend instanceof Neo3DFilamentBackend) {
            Neo3DFilamentBackend filamentBackend = (Neo3DFilamentBackend) backend;
            dispatcher.dispatch(() -> filamentBackend.attachSurfaceView(view));
        }
    }

    public void detachSurfaceView() {
        if (backend instanceof Neo3DFilamentBackend) {
            Neo3DFilamentBackend filamentBackend = (Neo3DFilamentBackend) backend;
            dispatcher.dispatch(() -> filamentBackend.detachSurfaceView());
        }
    }

    public void onResume() {
        dispatcher.dispatch(() -> backend.onResume());
    }

    public void onPause() {
        dispatcher.dispatch(() -> backend.onPause());
    }

    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        try {
            for (String sceneId : new java.util.ArrayList<>(scenes.keySet())) {
                deleteScene(sceneId);
            }
        } catch (Throwable t) {
            Log.e(TAG, "dispose: deleteScenes failed", t);
        }
        try {
            backend.dispose();
        } catch (Throwable t) {
            Log.e(TAG, "dispose: backend failed", t);
        }
        Log.i(TAG, "Neo3DEngine disposed");
    }

    public boolean isDisposed() {
        return disposed;
    }

    private Neo3DScene requireScene(String sceneId) {
        checkAlive();
        Neo3DScene scene = scenes.get(sceneId);
        if (scene == null) {
            throw new IllegalArgumentException("Unknown scene: " + sceneId);
        }
        return scene;
    }

    private void checkAlive() {
        if (disposed) {
            throw new IllegalStateException("Neo3DEngine already disposed");
        }
    }
}
