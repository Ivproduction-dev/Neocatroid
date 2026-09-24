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
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Unknown object: " + objectId);
        }
        obj.getTransform().setScale(x, y, z);
        e.syncObject(sceneId, objectId);
    }

    public static void facadeSetModelBytes(String sceneId, String objectId, String assetKey,
            byte[] glbBytes) {
        require().setObjectModelBytes(sceneId, objectId, assetKey, glbBytes);
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

    public static String facadeGetObjectName(String sceneId, String objectId) {
        Neo3DEngine e = require();
        Neo3DScene scene = e.getScene(sceneId);
        Neo3DGameObject obj = scene == null ? null : scene.getObject(objectId);
        return obj == null ? null : obj.getName();
    }
}
