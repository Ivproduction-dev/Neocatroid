package org.catrobat.catroid.neo3d;

public final class Neo3DFormulaBridge {

    private Neo3DFormulaBridge() {
    }

    private static Neo3DScene activeScene() {
        if (!Neo3DFacade.isReady()) {
            return null;
        }
        try {
            Neo3DEngine engine = Neo3DFacade.getEngineForTest();
            if (engine == null) {
                return null;
            }
            String sceneId = engine.getActiveSceneId();
            return sceneId == null ? null : engine.getScene(sceneId);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static Neo3DGameObject findObject(String name) {
        if (name == null) {
            return null;
        }
        Neo3DScene scene = activeScene();
        if (scene == null) {
            return null;
        }
        try {
            return scene.findByName(name);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public static Double getX(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getPosition()[0] : 0.0;
    }

    public static Double getY(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getPosition()[1] : 0.0;
    }

    public static Double getZ(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getPosition()[2] : 0.0;
    }

    public static Double getYaw(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getEulerDeg()[0] : 0.0;
    }

    public static Double getPitch(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getEulerDeg()[1] : 0.0;
    }

    public static Double getRoll(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getEulerDeg()[2] : 0.0;
    }

    public static Double getScaleX(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getScale()[0] : 1.0;
    }

    public static Double getScaleY(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getScale()[1] : 1.0;
    }

    public static Double getScaleZ(String name) {
        Neo3DGameObject obj = findObject(name);
        return obj != null ? (double) obj.getTransform().getScale()[2] : 1.0;
    }

    public static Double getDistance(String firstName, String secondName) {
        Neo3DGameObject first = findObject(firstName);
        Neo3DGameObject second = findObject(secondName);
        if (first == null || second == null) {
            return 0.0;
        }
        return (double) Neo3DMath.distance3(first.getTransform().getPosition(),
                second.getTransform().getPosition());
    }

    public static Double getSpeed(String name) {
        Neo3DGameObject obj = findObject(name);
        if (obj == null || !Neo3DFacade.isReady()) {
            return 0.0;
        }
        try {
            Neo3DEngine engine = Neo3DFacade.getEngineForTest();
            String sceneId = engine == null ? null : engine.getActiveSceneId();
            if (engine == null || sceneId == null) {
                return 0.0;
            }
            float[] velocity = engine.getPhysicsBackend().getLinearVelocity(sceneId,
                    obj.getId());
            if (velocity == null) {
                return 0.0;
            }
            return (double) Neo3DMath.distance3(velocity, new float[]{0f, 0f, 0f});
        } catch (RuntimeException e) {
            return 0.0;
        }
    }

    public static Double getExists(String name) {
        return findObject(name) != null ? 1.0 : 0.0;
    }

    public static Double getBodyCount() {
        if (!Neo3DFacade.isReady()) {
            return 0.0;
        }
        try {
            Neo3DEngine engine = Neo3DFacade.getEngineForTest();
            if (engine == null) {
                return 0.0;
            }
            String sceneId = engine.getActiveSceneId();
            if (sceneId == null) {
                return 0.0;
            }
            return (double) engine.getPhysicsBackend().getBodyCount(sceneId);
        } catch (RuntimeException e) {
            return 0.0;
        }
    }
}
