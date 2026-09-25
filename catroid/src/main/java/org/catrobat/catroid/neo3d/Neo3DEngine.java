package org.catrobat.catroid.neo3d;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

import org.catrobat.catroid.neo3d.assets.Neo3DAssetManager;
import org.catrobat.catroid.neo3d.backend.INeo3DBackend;
import org.catrobat.catroid.neo3d.physics.INeo3DPhysicsBackend;
import org.catrobat.catroid.neo3d.physics.JoltPhysicsBackend;
import org.catrobat.catroid.neo3d.physics.Neo3DNullPhysicsBackend;
import org.catrobat.catroid.neo3d.backend.MainThreadRenderDispatcher;
import org.catrobat.catroid.neo3d.backend.DirectRenderDispatcher;
import org.catrobat.catroid.neo3d.backend.Neo3DFilamentBackend;
import org.catrobat.catroid.neo3d.backend.Neo3DNullBackend;
import org.catrobat.catroid.neo3d.backend.RenderDispatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Neo3DEngine {

    private static final String TAG = "Neo3D-V2";

    public enum BackendType {
        AUTO,
        FILAMENT,
        NULL
    }

    public enum CameraTouchMode {
        FIRST_PERSON,
        FREE,
        DISABLED
    }

    private final INeo3DBackend backend;
    private final INeo3DPhysicsBackend physicsBackend;
    private final RenderDispatcher dispatcher;
    private final Neo3DAssetManager assetManager = new Neo3DAssetManager();
    private final Map<String, Neo3DScene> scenes = new LinkedHashMap<>();
    private final Map<String, TouchLookState> touchLookByScene = new LinkedHashMap<>();
    private final Map<String, FollowState> followByScene = new LinkedHashMap<>();
    private boolean disposed;
    private String activeSceneId;

    private float fpsEma;
    private float frameMsEma;
    private long updateCount;
    private int viewWidth = 1;
    private int viewHeight = 1;

    private Neo3DEngine(INeo3DBackend backend, INeo3DPhysicsBackend physicsBackend,
            RenderDispatcher dispatcher) {
        this.backend = backend;
        this.physicsBackend = physicsBackend;
        this.dispatcher = dispatcher;
    }

    public static Neo3DEngine create(Context context, BackendType type) {
        return create(context, type, null);
    }

    public static Neo3DEngine create(Context context, BackendType type,
            INeo3DPhysicsBackend physicsBackend) {
        INeo3DBackend renderBackend = null;
        if (type != BackendType.NULL) {
            Neo3DFilamentBackend filament = new Neo3DFilamentBackend();
            if (type == BackendType.AUTO && !filament.isAvailable()) {
                Log.i(TAG, "Filament not available, falling back to Null backend");
            } else {
                try {
                    return createWithBackend(filament, context, type != BackendType.NULL,
                            physicsBackend);
                } catch (Exception | LinkageError e) {
                    Log.e(TAG, "Filament backend failed, falling back to Null backend", e);
                    try {
                        filament.dispose();
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        renderBackend = new Neo3DNullBackend();
        return createWithBackend(renderBackend, context, false, physicsBackend);
    }

    private static Neo3DEngine createWithBackend(INeo3DBackend backend, Context context,
            boolean enableJolt, INeo3DPhysicsBackend requestedPhysicsBackend) {
        backend.initialize(context);
        if (!backend.isInitialized()) {
            throw new IllegalStateException("Backend failed to initialize: " + backend.getName());
        }
        INeo3DPhysicsBackend selectedPhysicsBackend = requestedPhysicsBackend;
        if (selectedPhysicsBackend == null && enableJolt) {
            JoltPhysicsBackend jolt = new JoltPhysicsBackend();
            if (jolt.initialize()) {
                selectedPhysicsBackend = jolt;
            } else {
                Log.w(TAG, "Jolt unavailable, Neo3D physics disabled");
            }
        }
        if (selectedPhysicsBackend == null) {
            selectedPhysicsBackend = new Neo3DNullPhysicsBackend();
        }
        if (!selectedPhysicsBackend.isInitialized()
                && !selectedPhysicsBackend.initialize()) {
            selectedPhysicsBackend = new Neo3DNullPhysicsBackend();
            selectedPhysicsBackend.initialize();
        }
        RenderDispatcher dispatcher = backend instanceof Neo3DFilamentBackend
                ? new MainThreadRenderDispatcher()
                : new DirectRenderDispatcher();
        Neo3DEngine engine = new Neo3DEngine(backend, selectedPhysicsBackend, dispatcher);
        Log.i(TAG, "Neo3DEngine created with backend=" + backend.getName()
                + " physics=" + selectedPhysicsBackend.getName());
        return engine;
    }

    public INeo3DBackend getBackend() {
        return backend;
    }

    public INeo3DPhysicsBackend getPhysicsBackend() {
        return physicsBackend;
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
        physicsBackend.registerScene(scene.getId());
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
        physicsBackend.registerScene(scene.getId());
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            syncPhysicsObject(scene.getId(), obj);
        }
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
        touchLookByScene.remove(sceneId);
        followByScene.remove(sceneId);
        physicsBackend.unregisterScene(sceneId);
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
        String baseName = name == null || name.isEmpty() ? "object" : name;
        String uniqueName = baseName;
        int counter = 2;
        while (scene.findByName(uniqueName) != null) {
            uniqueName = baseName + " (" + counter + ")";
            counter++;
        }
        Neo3DGameObject obj = scene.createObject(uniqueName);
        syncPhysicsObject(sceneId, obj);
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
        try {
            physicsBackend.removeObject(sceneId, objectId);
        } catch (Throwable t) {
            Log.e(TAG, "removeObject: physics failed", t);
        }
        dispatcher.dispatch(() -> backend.removeObject(sceneId, objectId));
        return scene.removeObject(objectId);
    }

    public void syncObject(String sceneId, String objectId) {
        Neo3DScene scene = requireScene(sceneId);
        Neo3DGameObject obj = scene.getObject(objectId);
        if (obj != null) {
            syncPhysicsObject(sceneId, obj);
            try {
                physicsBackend.setObjectTransform(sceneId, obj);
            } catch (Throwable t) {
                Log.e(TAG, "syncObject: physics transform failed", t);
            }
            dispatcher.dispatch(() -> backend.syncObject(sceneId, obj));
        }
    }

    private void syncPhysicsObject(String sceneId, Neo3DGameObject obj) {
        try {
            physicsBackend.syncObject(sceneId, obj);
        } catch (Throwable t) {
            Log.e(TAG, "syncPhysicsObject failed", t);
        }
    }

    public void syncScene(String sceneId) {
        Neo3DScene scene = requireScene(sceneId);
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            syncPhysicsObject(sceneId, obj);
        }
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
            syncPhysicsObject(sceneId, obj);
            dispatcher.dispatch(() -> backend.syncObject(sceneId, obj));
            return;
        }
        long startMs = System.currentTimeMillis();
        assetManager.acquire(Neo3DAssetManager.AssetKind.MODEL, assetKey, 0, glbBytes.length);
        long loadMs = System.currentTimeMillis() - startMs;
        obj.setModelPath(assetKey);
        syncPhysicsObject(sceneId, obj);
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
            applyPhysicsPoses(sceneId, scene, deltaSec);
            applyCameraFollow(sceneId, scene);
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

    private void applyPhysicsPoses(String sceneId, Neo3DScene scene, float deltaSec) {
        List<Neo3DPhysicsPose> poses;
        try {
            poses = physicsBackend.update(sceneId, deltaSec);
        } catch (Throwable t) {
            Log.e(TAG, "applyPhysicsPoses: update failed", t);
            return;
        }
        if (poses == null || poses.isEmpty()) {
            return;
        }
        final List<Neo3DPhysicsPose> applied = new java.util.ArrayList<>();
        for (Neo3DPhysicsPose pose : poses) {
            if (pose == null) {
                continue;
            }
            Neo3DGameObject obj = scene.getObject(pose.getObjectId());
            if (obj == null) {
                continue;
            }
            float[] position = pose.getPosition();
            float[] rotation = pose.getRotation();
            obj.getTransform().setPosition(position[0], position[1], position[2]);
            obj.getTransform().setQuaternion(rotation[0], rotation[1], rotation[2],
                    rotation[3]);
            applied.add(pose);
        }
        if (!applied.isEmpty()) {
            dispatcher.dispatch(() -> {
                for (Neo3DPhysicsPose pose : applied) {
                    Neo3DGameObject obj = scene.getObject(pose.getObjectId());
                    if (obj != null) {
                        backend.syncObject(sceneId, obj);
                    }
                }
            });
        }
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
        Neo3DGameObject cameraObject = findMainCameraObject(scene);
        return Neo3DPicker.pickObject(scene, cameraObject, viewWidth, viewHeight, touchX,
                touchY);
    }

    public void setCameraTouchLook(String sceneId, int mode, float sensitivity,
            float minPitchDeg, float maxPitchDeg) {
        if (Float.isNaN(sensitivity) || Float.isInfinite(sensitivity)) {
            sensitivity = 0.3f;
        }
        if (Float.isNaN(minPitchDeg) || Float.isInfinite(minPitchDeg)) {
            minPitchDeg = -60f;
        }
        if (Float.isNaN(maxPitchDeg) || Float.isInfinite(maxPitchDeg)) {
            maxPitchDeg = 60f;
        }
        TouchLookState state = touchLookByScene.get(sceneId);
        if (state == null) {
            state = new TouchLookState();
            touchLookByScene.put(sceneId, state);
        }
        CameraTouchMode[] modes = CameraTouchMode.values();
        state.mode = mode >= 0 && mode < modes.length
                ? modes[mode] : CameraTouchMode.FIRST_PERSON;
        state.sensitivity = sensitivity;
        float lowerPitch = Math.max(-89f, Math.min(minPitchDeg, maxPitchDeg));
        float upperPitch = Math.min(89f, Math.max(minPitchDeg, maxPitchDeg));
        state.minPitchDeg = lowerPitch;
        state.maxPitchDeg = upperPitch;
        state.hasPose = false;
    }

    public boolean dragCameraLook(String sceneId, float dxPixels, float dyPixels) {
        if (Float.isNaN(dxPixels) || Float.isNaN(dyPixels)
                || Float.isInfinite(dxPixels) || Float.isInfinite(dyPixels)) {
            return false;
        }
        TouchLookState state = touchLookByScene.get(sceneId);
        Neo3DScene scene = scenes.get(sceneId);
        if (state == null || state.mode == CameraTouchMode.DISABLED || scene == null) {
            return false;
        }
        Neo3DGameObject cameraObject = findMainCameraObject(scene);
        if (cameraObject == null) {
            return false;
        }
        cameraObject.getCamera().setUseTransformOrientation(true);
        if (!state.hasPose) {
            float[] euler = cameraObject.getTransform().getEulerDeg();
            state.yawDeg = euler[0];
            state.pitchDeg = euler[1];
            state.hasPose = true;
        }
        state.yawDeg -= dxPixels * state.sensitivity;
        state.pitchDeg -= dyPixels * state.sensitivity;
        if (state.mode == CameraTouchMode.FIRST_PERSON) {
            state.pitchDeg = Math.max(state.minPitchDeg,
                    Math.min(state.maxPitchDeg, state.pitchDeg));
        }
        state.yawDeg = normalizeAngle(state.yawDeg);
        state.pitchDeg = normalizeAngle(state.pitchDeg);
        float[] euler = cameraObject.getTransform().getEulerDeg();
        cameraObject.getTransform().setRotationEulerDeg(state.yawDeg, state.pitchDeg, euler[2]);
        syncObject(sceneId, cameraObject.getId());
        return true;
    }

    private static float normalizeAngle(float degrees) {
        float normalized = degrees % 360f;
        if (normalized > 180f) {
            normalized -= 360f;
        } else if (normalized < -180f) {
            normalized += 360f;
        }
        return normalized;
    }

    public void setCameraFollow(String sceneId, String targetName, float offX, float offY,
            float offZ, boolean lookAt) {
        if (targetName == null || targetName.isEmpty()) {
            followByScene.remove(sceneId);
            return;
        }
        FollowState state = followByScene.get(sceneId);
        if (state == null) {
            state = new FollowState();
            followByScene.put(sceneId, state);
        }
        state.targetName = targetName;
        state.offX = offX;
        state.offY = offY;
        state.offZ = offZ;
        state.lookAt = lookAt;
    }

    public void clearCameraFollow(String sceneId) {
        followByScene.remove(sceneId);
    }

    public boolean pointMainCameraAt(String sceneId, String targetName) {
        Neo3DScene scene = scenes.get(sceneId);
        if (scene == null || targetName == null || targetName.isEmpty()) {
            return false;
        }
        Neo3DGameObject cameraObject = findMainCameraObject(scene);
        Neo3DGameObject target = scene.findByName(targetName);
        if (cameraObject == null || target == null) {
            return false;
        }
        float[] eye = worldPosition(cameraObject);
        float[] aim = worldPosition(target);
        float[] yawPitch = Neo3DMath.yawPitchToTarget(eye, aim);
        float[] euler = cameraObject.getTransform().getEulerDeg();
        cameraObject.getTransform().setRotationEulerDeg(yawPitch[0], yawPitch[1], euler[2]);
        cameraObject.getCamera().setUseTransformOrientation(true);
        syncObject(sceneId, cameraObject.getId());
        return true;
    }

    public boolean moveObjectForward(String sceneId, String objectId, float distance) {
        if (Float.isNaN(distance) || Float.isInfinite(distance)) {
            return false;
        }
        Neo3DScene scene = scenes.get(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        if (obj == null) {
            return false;
        }
        float[] world = obj.getTransform().getWorldMatrix();
        float[] pos = obj.getTransform().getPosition();
        obj.getTransform().setPosition(pos[0] - world[8] * distance,
                pos[1] - world[9] * distance, pos[2] - world[10] * distance);
        syncObject(sceneId, objectId);
        return true;
    }

    public boolean turnObjectToward(String sceneId, String objectId, String targetName) {
        Neo3DScene scene = scenes.get(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        Neo3DGameObject target = scene == null || targetName == null ? null
                : scene.findByName(targetName);
        if (obj == null || target == null) {
            return false;
        }
        float[] eye = worldPosition(obj);
        float[] aim = worldPosition(target);
        float[] yawPitch = Neo3DMath.yawPitchToTarget(eye, aim);
        float[] euler = obj.getTransform().getEulerDeg();
        obj.getTransform().setRotationEulerDeg(yawPitch[0], yawPitch[1], euler[2]);
        syncObject(sceneId, objectId);
        return true;
    }

    public boolean setObjectVisible(String sceneId, String objectId, boolean visible) {
        Neo3DScene scene = scenes.get(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        if (obj == null) {
            return false;
        }
        obj.setVisible(visible);
        syncObject(sceneId, objectId);
        return true;
    }

    public int clearObjects(String sceneId) {
        Neo3DScene scene = scenes.get(sceneId);
        if (scene == null) {
            return 0;
        }
        int removed = 0;
        for (Neo3DGameObject obj : new java.util.ArrayList<>(scene.getAllObjects())) {
            if (obj.getCamera() != null && obj.getCamera().isMainCamera()) {
                continue;
            }
            if (removeObject(sceneId, obj.getId())) {
                removed++;
            }
        }
        return removed;
    }

    private void applyCameraFollow(String sceneId, Neo3DScene scene) {
        FollowState state = followByScene.get(sceneId);
        if (state == null) {
            return;
        }
        Neo3DGameObject cameraObject = findMainCameraObject(scene);
        Neo3DGameObject target = scene.findByName(state.targetName);
        if (cameraObject == null || target == null) {
            return;
        }
        float[] targetPos = worldPosition(target);
        cameraObject.getTransform().setPosition(targetPos[0] + state.offX,
                targetPos[1] + state.offY, targetPos[2] + state.offZ);
        if (state.lookAt) {
            float[] eye = worldPosition(cameraObject);
            float[] yawPitch = Neo3DMath.yawPitchToTarget(eye, targetPos);
            float[] euler = cameraObject.getTransform().getEulerDeg();
            cameraObject.getTransform().setRotationEulerDeg(yawPitch[0], yawPitch[1], euler[2]);
            cameraObject.getCamera().setUseTransformOrientation(true);
        }
        syncObject(sceneId, cameraObject.getId());
    }

    private static float[] worldPosition(Neo3DGameObject obj) {
        float[] world = obj.getTransform().getWorldMatrix();
        return new float[]{world[12], world[13], world[14]};
    }

    private Neo3DGameObject findMainCameraObject(Neo3DScene scene) {
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getCamera() != null && obj.getCamera().isMainCamera() && obj.isActive()) {
                return obj;
            }
        }
        return null;
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
        try {
            physicsBackend.dispose();
        } catch (Throwable t) {
            Log.e(TAG, "dispose: physics failed", t);
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

    private static final class FollowState {
        private String targetName = "";
        private float offX;
        private float offY;
        private float offZ;
        private boolean lookAt;
    }

    private static final class TouchLookState {
        private CameraTouchMode mode = CameraTouchMode.FIRST_PERSON;
        private float sensitivity = 0.3f;
        private float minPitchDeg = -60f;
        private float maxPitchDeg = 60f;
        private float yawDeg;
        private float pitchDeg;
        private boolean hasPose;
    }

    private void checkAlive() {
        if (disposed) {
            throw new IllegalStateException("Neo3DEngine already disposed");
        }
    }
}
