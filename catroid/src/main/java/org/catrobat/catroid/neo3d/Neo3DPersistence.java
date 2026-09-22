package org.catrobat.catroid.neo3d;

import org.catrobat.catroid.content.Scene;

import java.util.ArrayList;
import java.util.List;

public final class Neo3DPersistence {

    private Neo3DPersistence() {
    }

    public static void saveToScene(Scene catroidScene, Neo3DEngine engine, String neoSceneId) {
        if (catroidScene == null || engine == null || neoSceneId == null) {
            return;
        }
        Neo3DScene neoScene = engine.getScene(neoSceneId);
        if (neoScene == null) {
            return;
        }
        List<Neo3DPersistedObject> out = new ArrayList<>();
        for (Neo3DGameObject obj : neoScene.getAllObjects()) {
            Neo3DPersistedObject p = new Neo3DPersistedObject(obj.getName());
            p.modelAssetKey = obj.getModelPath();
            if (Neo3DPrimitiveMeshes.ASSET_KEY_CUBE.equals(p.modelAssetKey)) {
                p.primitiveKind = 0;
            } else if (Neo3DPrimitiveMeshes.ASSET_KEY_SPHERE.equals(p.modelAssetKey)) {
                p.primitiveKind = 1;
            } else if (Neo3DPrimitiveMeshes.ASSET_KEY_CYLINDER.equals(p.modelAssetKey)) {
                p.primitiveKind = 2;
            }
            float[] pos = obj.getTransform().getPosition();
            p.x = pos[0];
            p.y = pos[1];
            p.z = pos[2];
            float[] scale = obj.getTransform().getScale();
            p.scaleX = scale[0];
            p.scaleY = scale[1];
            p.scaleZ = scale[2];
            float[] euler = obj.getTransform().getEulerDeg();
            p.yawDeg = euler[0];
            p.pitchDeg = euler[1];
            p.rollDeg = euler[2];
            if (obj.getCamera() != null && obj.getCamera().isMainCamera()) {
                p.primitiveKind = -2;
            }
            out.add(p);
        }
        catroidScene.getNeo3DObjects().clear();
        catroidScene.getNeo3DObjects().addAll(out);
    }

    public static void restoreToEngine(Scene catroidScene, Neo3DEngine engine, String neoSceneId) {
        if (catroidScene == null || engine == null || neoSceneId == null) {
            return;
        }
        List<Neo3DPersistedObject> persisted = catroidScene.getNeo3DObjects();
        if (persisted == null || persisted.isEmpty()) {
            return;
        }
        for (Neo3DPersistedObject p : persisted) {
            if (p == null || p.name == null || p.name.isEmpty()) {
                continue;
            }
            try {
                if (p.primitiveKind == -2) {
                    Neo3DFacade.facadeSetMainCameraPosition(neoSceneId, p.x, p.y, p.z);
                    continue;
                }
                String objectId = Neo3DFacade.facadeCreateObject(neoSceneId, p.name);
                Neo3DFacade.facadeSetPosition(neoSceneId, objectId, p.x, p.y, p.z);
                Neo3DScene neoScene = engine.getScene(neoSceneId);
                Neo3DGameObject obj = neoScene == null ? null : neoScene.getObject(objectId);
                if (obj != null) {
                    obj.getTransform().setScale(p.scaleX, p.scaleY, p.scaleZ);
                    obj.getTransform().setRotationEulerDeg(p.yawDeg, p.pitchDeg, p.rollDeg);
                    engine.syncObject(neoSceneId, objectId);
                }
                if (p.primitiveKind == 0) {
                    byte[] bytes = Neo3DPrimitiveMeshes.buildCube(2f,
                            new float[]{0.75f, 0.78f, 0.82f, 1f});
                    Neo3DFacade.facadeSetModelBytes(neoSceneId, objectId,
                            Neo3DPrimitiveMeshes.ASSET_KEY_CUBE, bytes);
                } else if (p.primitiveKind == 1) {
                    byte[] bytes = Neo3DPrimitiveMeshes.buildSphere(1f, 12, 24,
                            new float[]{0.85f, 0.55f, 0.25f, 1f});
                    Neo3DFacade.facadeSetModelBytes(neoSceneId, objectId,
                            Neo3DPrimitiveMeshes.ASSET_KEY_SPHERE, bytes);
                } else if (p.primitiveKind == 2) {
                    byte[] bytes = Neo3DPrimitiveMeshes.buildCylinder(1f, 2f, 24,
                            new float[]{0.25f, 0.65f, 0.6f, 1f});
                    Neo3DFacade.facadeSetModelBytes(neoSceneId, objectId,
                            Neo3DPrimitiveMeshes.ASSET_KEY_CYLINDER, bytes);
                } else if (p.modelAssetKey != null && !p.modelAssetKey.isEmpty()
                        && !"embedded:cube".equals(p.modelAssetKey)) {
                    // custom GLB will be recreated via Load brick at runtime, skip here
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
