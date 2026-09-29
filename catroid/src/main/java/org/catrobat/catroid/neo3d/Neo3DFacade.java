package org.catrobat.catroid.neo3d;

public final class Neo3DFacade {

    private static Neo3DEngine engine;

    private Neo3DFacade() {
    }

    public static synchronized void install(Neo3DEngine engine) {
        Neo3DFacade.engine = engine;
    }

    public static synchronized void uninstall() {
        engine = null;
    }

    public static synchronized boolean isReady() {
        return engine != null && !engine.isDisposed();
    }

    private static Neo3DEngine require() {
        if (engine == null || engine.isDisposed()) {
            throw new IllegalStateException("Neo3DFacade: no live engine installed");
        }
        return engine;
    }

    public static String facadeCreateScene(String name) {
        return require().createScene(name).getId();
    }

    public static String facadeEnsureDefaultScene() {
        Neo3DEngine e = require();
        String activeId = e.getActiveSceneId();
        if (activeId != null && e.getScene(activeId) != null) {
            return activeId;
        }
        return e.createScene("Scene").getId();
    }

    public static String facadeFindObjectIdByName(String sceneId, String name) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.findByName(name);
        return obj == null ? null : obj.getId();
    }

    public static String facadeSetMainCameraPosition(String sceneId, float x, float y, float z) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        if (scene == null) {
            throw new IllegalArgumentException("Unknown scene: " + sceneId);
        }
        Neo3DGameObject mainCamera = null;
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getCamera() != null && obj.getCamera().isMainCamera() && obj.isActive()) {
                mainCamera = obj;
                break;
            }
        }
        if (mainCamera == null) {
            mainCamera = scene.createObject("MainCamera");
            mainCamera.setCamera(new Neo3DCamera());
        }
        mainCamera.getTransform().setPosition(x, y, z);
        e.syncObject(sceneId, mainCamera.getId());
        return mainCamera.getId();
    }

    public static boolean facadeDeleteScene(String sceneId) {
        return require().deleteScene(sceneId);
    }

    public static String facadeCreateObject(String sceneId, String name) {
        return require().createObject(sceneId, name).getId();
    }

    public static boolean facadeRemoveObject(String sceneId, String objectId) {
        return require().removeObject(sceneId, objectId);
    }

    public static void facadeSetPosition(String sceneId, String objectId, float x, float y,
            float z) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Unknown object: " + objectId);
        }
        obj.getTransform().setPosition(x, y, z);
        e.syncObject(sceneId, objectId);
    }

    public static void facadeSetRotation(String sceneId, String objectId, float yawDeg,
            float pitchDeg, float rollDeg) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Unknown object: " + objectId);
        }
        obj.getTransform().setRotationEulerDeg(yawDeg, pitchDeg, rollDeg);
        e.syncObject(sceneId, objectId);
    }

    public static void facadeSetScale(String sceneId, String objectId, float x, float y, float z) {
        require().setObjectScale(sceneId, objectId, x, y, z);
    }

    public static void facadeSetModelBytes(String sceneId, String objectId, String assetKey,
            byte[] glbBytes) {
        require().setObjectModelBytes(sceneId, objectId, assetKey, glbBytes);
    }

    public static String facadeSetMainCameraRotation(String sceneId, float yawDeg,
            float pitchDeg, float rollDeg) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        if (scene == null) {
            throw new IllegalArgumentException("Unknown scene: " + sceneId);
        }
        Neo3DGameObject mainCamera = null;
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getCamera() != null && obj.getCamera().isMainCamera() && obj.isActive()) {
                mainCamera = obj;
                break;
            }
        }
        if (mainCamera == null) {
            mainCamera = scene.createObject("MainCamera");
            mainCamera.setCamera(new Neo3DCamera());
        }
        mainCamera.getCamera().setUseTransformOrientation(true);
        mainCamera.getTransform().setRotationEulerDeg(yawDeg, pitchDeg, rollDeg);
        e.syncObject(sceneId, mainCamera.getId());
        return mainCamera.getId();
    }

    public static void facadeSetCameraTouchLook(String sceneId, int mode, float sensitivity,
            float minPitchDeg, float maxPitchDeg) {
        require().setCameraTouchLook(sceneId, mode, sensitivity, minPitchDeg, maxPitchDeg);
    }

    public static boolean facadeDragCameraLook(String sceneId, float dxPixels, float dyPixels) {
        return require().dragCameraLook(sceneId, dxPixels, dyPixels);
    }

    public static void facadeSetCameraFollow(String sceneId, String targetName, float offX,
            float offY, float offZ, boolean lookAt) {
        require().setCameraFollow(sceneId, targetName, offX, offY, offZ, lookAt);
    }

    public static void facadeClearCameraFollow(String sceneId) {
        require().clearCameraFollow(sceneId);
    }

    public static boolean facadePointMainCameraAt(String sceneId, String targetName) {
        return require().pointMainCameraAt(sceneId, targetName);
    }

    public static boolean facadeMoveObjectForward(String sceneId, String objectId,
            float distance) {
        return require().moveObjectForward(sceneId, objectId, distance);
    }

    public static boolean facadeTurnObjectToward(String sceneId, String objectId,
            String targetName) {
        return require().turnObjectToward(sceneId, objectId, targetName);
    }

    public static boolean facadeSetObjectVisible(String sceneId, String objectId,
            boolean visible) {
        return require().setObjectVisible(sceneId, objectId, visible);
    }

    public static int facadeClearObjects(String sceneId) {
        return require().clearObjects(sceneId);
    }

    public static String facadeRenameObject(String sceneId, String objectId, String newName) {
        return require().renameObject(sceneId, objectId, newName);
    }

    public static String facadeGetObjectParentName(String sceneId, String objectId) {
        return require().getObjectParentName(sceneId, objectId);
    }

    public static boolean facadeCopyObjectPosition(String sceneId, String objectId,
            String sourceName) {
        return require().copyObjectPosition(sceneId, objectId, sourceName);
    }

    public static boolean facadeSetObjectPositionToCamera(String sceneId, String objectId) {
        return require().setObjectPositionToCamera(sceneId, objectId);
    }

    public static boolean facadeSetCameraPositionToObject(String sceneId, String targetName) {
        return require().setCameraPositionToObject(sceneId, targetName);
    }

    public static boolean facadeTurnObjectToCamera(String sceneId, String objectId) {
        return require().turnObjectToCamera(sceneId, objectId);
    }

    public static void facadeSetRay(String sceneId, String rayName, String fromName,
            String towardName, float distance, int maxHits) {
        require().setRay(sceneId, rayName, fromName, towardName, distance, maxHits);
    }

    public static void facadeClearRay(String sceneId, String rayName) {
        require().clearRay(sceneId, rayName);
    }

    public static java.util.List<Neo3DRaycaster.Hit> facadeCastRay(String sceneId,
            String rayName) {
        return require().castRay(sceneId, rayName);
    }

    public static String facadeGetRayHitName(String sceneId, String rayName, int index) {
        java.util.List<Neo3DRaycaster.Hit> hits = require().getRayHits(sceneId, rayName);
        if (index < 0 || index >= hits.size() || hits.get(index).name == null) {
            return "";
        }
        return hits.get(index).name;
    }

    public static int facadeGetRayHitCount(String sceneId, String rayName) {
        return require().getRayHits(sceneId, rayName).size();
    }

    public static void facadeSetObjectVariable(String sceneId, String objectId, String key,
            Object value) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Unknown object: " + objectId);
        }
        obj.setVariable(key, value);
    }

    public static Object facadeGetObjectVariable(String sceneId, String name, String key) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null || name == null ? null : scene.findByName(name);
        if (obj == null) {
            return 0.0;
        }
        Object value = obj.getVariable(key);
        return value == null ? 0.0 : value;
    }

    public static void facadeSetPhysicsBody(String sceneId, String objectId,
            Neo3DPhysicsBody body) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Unknown object: " + objectId);
        }
        obj.setPhysicsBody(body == null ? null : body.copy());
        e.syncObject(sceneId, objectId);
    }

    public static void facadeSetLinearVelocity(String sceneId, String objectId, float x, float y,
            float z) {
        require().getPhysicsBackend().setLinearVelocity(sceneId, objectId, x, y, z);
    }

    public static void facadeAddImpulse(String sceneId, String objectId, float x, float y,
            float z) {
        require().getPhysicsBackend().addImpulse(sceneId, objectId, x, y, z);
    }

    public static void facadeSetGravity(float x, float y, float z) {
        require().getPhysicsBackend().setGravity(x, y, z);
    }

    public static int facadeGetPhysicsBodyCount(String sceneId) {
        return require().getPhysicsBackend().getBodyCount(sceneId);
    }

    public static float facadeUpdate(String sceneId, float deltaSec, long frameTimeNanos) {
        return require().update(sceneId, deltaSec, frameTimeNanos);
    }

    public static String facadePickObject(String sceneId, float touchX, float touchY) {
        return require().pickObject(sceneId, touchX, touchY);
    }

    public static Neo3DEngine getEngineForTest() {
        return engine;
    }

    public static float[] facadeGetObjectPosition(String sceneId, String objectId) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        return obj == null ? null : obj.getTransform().getPosition();
    }

    public static float[] facadeGetObjectPositionByName(String sceneId, String name) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null || name == null ? null : scene.findByName(name);
        return obj == null ? null : obj.getTransform().getPosition();
    }

    public static float[] facadeGetMainCameraPosition(String sceneId) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        if (scene == null) {
            return null;
        }
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getCamera() != null && obj.getCamera().isMainCamera() && obj.isActive()) {
                return obj.getTransform().getPosition();
            }
        }
        return null;
    }

    public static void facadeSetPhysicsCollision(String sceneId, String objectId,
            boolean collide) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Unknown object: " + objectId);
        }
        Neo3DPhysicsBody body = obj.getPhysicsBody();
        if (body == null) {
            android.util.Log.w("Neo3D", "SetCollision: no physics body on '"
                    + obj.getName() + "'");
            return;
        }
        body.setNoCollision(!collide);
        e.syncObject(sceneId, objectId);
    }

    public static String facadeGetObjectName(String sceneId, String objectId) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        return obj == null ? null : obj.getName();
    }
}
