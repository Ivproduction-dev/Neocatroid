package org.catrobat.catroid.neo3d;

import android.util.Log;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;

public final class Neo3DSceneLoader {

    private static final String TAG = "Neo3DSceneLoader";

    private Neo3DSceneLoader() {
    }

    public static void loadScene(Project project, Scene projectScene, String sceneId) {
        if (projectScene == null || sceneId == null) {
            return;
        }
        if (!Neo3DFacade.isReady()) {
            return;
        }
        Neo3DEngine engine = Neo3DFacade.getEngineForTest();
        if (engine == null) {
            return;
        }
        Neo3DScene neoScene = engine.getScene(sceneId);
        if (neoScene == null) {
            return;
        }

        float[] skyColor = projectScene.getNeo3DSkyColor();
        neoScene.getSkybox().setMode(Neo3DSkybox.Mode.COLOR);
        neoScene.getSkybox().setColorA(skyColor[0], skyColor[1], skyColor[2], 1f);
        neoScene.getSkybox().setIblIntensity(projectScene.getNeo3DIblIntensity());
        neoScene.getShadowSettings().setEnabled(projectScene.isNeo3DShadowsEnabled());

        if (projectScene.getNeo3DObjects() == null || projectScene.getNeo3DObjects().isEmpty()) {
            engine.syncScene(sceneId);
            return;
        }

        for (Neo3DPersistedObject data : projectScene.getNeo3DObjects()) {
            if (data == null || data.name == null) {
                continue;
            }
            Neo3DGameObject obj = neoScene.createObject(data.name);
            obj.setActive(data.active);
            obj.setVisible(data.visible);
            obj.getTransform().setPosition(data.x, data.y, data.z);
            obj.getTransform().setScale(data.scaleX, data.scaleY, data.scaleZ);
            obj.getTransform().setRotationEulerDeg(data.yawDeg, data.pitchDeg, data.rollDeg);

            if (data.camera) {
                Neo3DCamera camera = new Neo3DCamera();
                camera.setMainCamera(data.cameraMain);
                camera.setUseTransformOrientation(data.cameraUsesTransformOrientation);
                camera.setFovDeg(data.cameraFov);
                camera.setNear(data.cameraNear);
                camera.setFar(data.cameraFar);
                camera.setExposure(data.cameraAperture, data.cameraShutterSpeed, data.cameraSensitivity);
                camera.setLookAtTarget(data.cameraTargetX, data.cameraTargetY, data.cameraTargetZ);
                obj.setCamera(camera);
            }

            if (data.lightType != null) {
                Neo3DLight.Type type;
                try {
                    type = Neo3DLight.Type.valueOf(data.lightType);
                } catch (Exception ignored) {
                    type = Neo3DLight.Type.POINT;
                }
                Neo3DLight light = new Neo3DLight(type, data.lightIntensity);
                light.setColor(data.lightRed, data.lightGreen, data.lightBlue);
                light.setRange(data.lightRange);
                light.setCastShadows(data.lightShadows);
                light.setDirection(data.lightDirectionX, data.lightDirectionY, data.lightDirectionZ);
                light.setConeDeg(data.lightInnerConeDeg, data.lightOuterConeDeg);
                obj.setLight(light);
            }

            obj.setCustomMaterial(Neo3DCustomMaterial.pbrFactors(
                    new float[]{data.colorRed, data.colorGreen, data.colorBlue, data.colorAlpha},
                    data.metallic, data.roughness,
                    new float[]{data.emissiveRed, data.emissiveGreen, data.emissiveBlue}));

            if (data.physicsMotionType >= 0) {
                Neo3DPhysicsBody body = new Neo3DPhysicsBody(
                        data.physicsMotionType < Neo3DPhysicsBody.MotionType.values().length
                                ? Neo3DPhysicsBody.MotionType.values()[data.physicsMotionType]
                                : Neo3DPhysicsBody.MotionType.NONE,
                        data.physicsShapeType < Neo3DPhysicsBody.ShapeType.values().length
                                ? Neo3DPhysicsBody.ShapeType.values()[data.physicsShapeType]
                                : Neo3DPhysicsBody.ShapeType.AUTO,
                        data.physicsMass);
                body.setFriction(data.physicsFriction);
                body.setRestitution(data.physicsRestitution);
                body.setGravityFactor(data.physicsGravityFactor);
                body.setLinearDamping(data.physicsLinearDamping);
                body.setAngularDamping(data.physicsAngularDamping);
                body.setContinuousCollision(data.physicsContinuousCollision);
                body.setShapeScaleX(data.physicsShapeScaleX);
                body.setShapeScaleY(data.physicsShapeScaleY);
                body.setShapeScaleZ(data.physicsShapeScaleZ);
                obj.setPhysicsBody(body);
            }

            if (data.modelAssetKey != null && !data.modelAssetKey.isEmpty() && project != null) {
                try {
                    File f = project.getFile(data.modelAssetKey);
                    if (f != null && f.exists()) {
                        byte[] bytes = Files.readAllBytes(f.toPath());
                        String lowerKey = data.modelAssetKey.toLowerCase(Locale.ROOT);
                        if (lowerKey.endsWith(".obj")) {
                            bytes = Neo3DPrimitiveMeshes.convertObjToGlb(bytes,
                                    new float[]{data.colorRed, data.colorGreen, data.colorBlue, data.colorAlpha});
                        } else if (lowerKey.endsWith(".stl")) {
                            bytes = Neo3DPrimitiveMeshes.convertStlToGlb(bytes,
                                    new float[]{data.colorRed, data.colorGreen, data.colorBlue, data.colorAlpha});
                        }
                        engine.setObjectModelBytes(sceneId, obj.getId(), data.modelAssetKey, bytes);
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "Failed loading model: " + data.modelAssetKey, t);
                }
            } else if (data.primitiveKind >= 0) {
                float[] col = {data.colorRed, data.colorGreen, data.colorBlue, data.colorAlpha};
                byte[] primBytes = null;
                String primKey = null;
                if (data.primitiveKind == 0) {
                    primBytes = Neo3DPrimitiveMeshes.buildCube(1f, col);
                    primKey = Neo3DPrimitiveMeshes.ASSET_KEY_CUBE;
                } else if (data.primitiveKind == 1) {
                    primBytes = Neo3DPrimitiveMeshes.buildSphere(0.5f, 20, 32, col);
                    primKey = Neo3DPrimitiveMeshes.ASSET_KEY_SPHERE;
                } else if (data.primitiveKind == 2) {
                    primBytes = Neo3DPrimitiveMeshes.buildCylinder(0.5f, 1f, 32, col);
                    primKey = Neo3DPrimitiveMeshes.ASSET_KEY_CYLINDER;
                }
                if (primBytes != null) {
                    engine.setObjectModelBytes(sceneId, obj.getId(), primKey, primBytes);
                }
            }
            engine.syncObject(sceneId, obj.getId());
        }

        for (Neo3DPersistedObject data : projectScene.getNeo3DObjects()) {
            if (data == null || data.parentName == null || data.parentName.isEmpty()) {
                continue;
            }
            Neo3DGameObject child = neoScene.findByName(data.name);
            Neo3DGameObject parent = neoScene.findByName(data.parentName);
            if (child != null && parent != null) {
                neoScene.setParent(child.getId(), parent.getId());
            }
        }

        engine.syncScene(sceneId);
    }
}
