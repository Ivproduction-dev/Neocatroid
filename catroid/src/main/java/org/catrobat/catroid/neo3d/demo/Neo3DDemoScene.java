package org.catrobat.catroid.neo3d.demo;

import org.catrobat.catroid.neo3d.Neo3DAnimationClip;
import org.catrobat.catroid.neo3d.Neo3DCamera;
import org.catrobat.catroid.neo3d.Neo3DCustomMaterial;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DLight;
import org.catrobat.catroid.neo3d.Neo3DMaterial;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.Neo3DSkybox;

import java.util.ArrayList;
import java.util.List;

public final class Neo3DDemoScene {

    public static final String SCENE_NAME = "Neo3D V2 Demo";
    public static final String HERO_MODEL_KEY = Neo3DEmbeddedCube.ASSET_KEY + "#hero";
    public static final String GROUND_MODEL_KEY = Neo3DEmbeddedCube.ASSET_KEY + "#ground";

    private Neo3DDemoScene() {
    }

    public static Neo3DScene buildDemoSpec() {
        Neo3DScene scene = new Neo3DScene(SCENE_NAME);

        Neo3DGameObject cameraRig = scene.createObject("V2_MainCamera");
        cameraRig.getTransform().setPosition(4.5f, 3.2f, 6.5f);
        Neo3DCamera camera = new Neo3DCamera();
        camera.setFovDeg(55f);
        camera.setNear(0.1f);
        camera.setFar(200f);
        camera.setLookAtTarget(0f, 0.8f, 0f);
        cameraRig.setCamera(camera);

        Neo3DGameObject ground = scene.createObject("V2_Ground");
        ground.getTransform().setPosition(0f, -1.02f, 0f);
        ground.getTransform().setScale(12f, 0.1f, 12f);
        ground.setModelPath(GROUND_MODEL_KEY);
        Neo3DMaterial groundMat = new Neo3DMaterial();
        groundMat.setBaseColor(0.23f, 0.28f, 0.33f, 1f);
        groundMat.setMetallic(0f);
        groundMat.setRoughness(0.95f);
        ground.setMaterial(groundMat);

        Neo3DGameObject hero = scene.createObject("V2_Hero");
        hero.getTransform().setPosition(0f, 0.8f, 0f);
        hero.setModelPath(HERO_MODEL_KEY);
        Neo3DMaterial heroMat = new Neo3DMaterial();
        heroMat.setBaseColor(0.85f, 0.55f, 0.25f, 1f);
        heroMat.setMetallic(0.35f);
        heroMat.setRoughness(0.45f);
        heroMat.setNormalTexturePath("textures/v2_demo_normal.png");
        heroMat.setNormalScale(1f);
        hero.setMaterial(heroMat);
        hero.setCustomMaterial(Neo3DCustomMaterial.pbrFactors(
                new float[]{1f, 0.9f, 0.75f, 1f}, 0.35f, 0.45f,
                new float[]{0.25f, 0.12f, 0.03f}));
        hero.addAnimationClip(new Neo3DAnimationClip("Idle", 2.4f));

        Neo3DGameObject sun = scene.createObject("V2_Sun");
        Neo3DLight sunLight = Neo3DLight.directional(60000f);
        sunLight.setDirection(-0.45f, -1f, -0.35f);
        sunLight.setColor(1f, 0.96f, 0.9f);
        sunLight.setCastShadows(false);
        sun.setLight(sunLight);

        Neo3DGameObject lamp = scene.createObject("V2_Lamp");
        lamp.getTransform().setPosition(2.5f, 3f, 1.5f);
        Neo3DLight point = Neo3DLight.point(20000f);
        point.setColor(1f, 0.75f, 0.5f);
        point.setRange(14f);
        lamp.setLight(point);

        Neo3DGameObject spot = scene.createObject("V2_Spot");
        spot.getTransform().setPosition(-3.5f, 4f, 2.5f);
        Neo3DLight spotLight = Neo3DLight.spot(50000f);
        spotLight.setColor(0.6f, 0.75f, 1f);
        spotLight.setDirection(0.55f, -0.75f, -0.4f);
        spotLight.setConeDeg(22f, 35f);
        spotLight.setRange(20f);
        spotLight.setCastShadows(false);
        spot.setLight(spotLight);

        scene.getSkybox().setMode(Neo3DSkybox.Mode.GRADIENT);
        scene.getSkybox().setColorA(0.16f, 0.29f, 0.42f, 1f);
        scene.getSkybox().setColorB(0.62f, 0.72f, 0.82f, 1f);
        scene.getSkybox().setIblIntensity(30000f);
        scene.getShadowSettings().setEnabled(true);
        scene.getShadowSettings().setMapSize(512);
        scene.setAmbientIntensity(1f);

        return scene;
    }

    public static ValidationResult validate(Neo3DScene scene) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        if (scene == null) {
            errors.add("scene is null");
            return new ValidationResult(errors, warnings);
        }
        if (scene.findByName("V2_MainCamera") == null) {
            errors.add("missing V2_MainCamera");
        }
        if (scene.findByName("V2_Hero") == null) {
            errors.add("missing V2_Hero");
        }
        int lightCount = 0;
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getLight() != null) {
                lightCount++;
            }
            if (obj.getMaterial() != null && obj.getMaterial().getNormalTexturePath() != null) {
                warnings.add("normal map not bundled (missing-tolerant path): "
                        + obj.getMaterial().getNormalTexturePath());
            }
            if (obj.getModelPath() != null && !obj.getModelPath().startsWith("embedded:")) {
                warnings.add("external model needs a file: " + obj.getModelPath());
            }
        }
        if (lightCount < 3) {
            errors.add("expected 3 lights, found " + lightCount);
        }
        return new ValidationResult(errors, warnings);
    }

    public static class ValidationResult {
        public final List<String> errors;
        public final List<String> warnings;

        ValidationResult(List<String> errors, List<String> warnings) {
            this.errors = errors;
            this.warnings = warnings;
        }

        public boolean isOk() {
            return errors.isEmpty();
        }

        @Override
        public String toString() {
            return "ValidationResult{errors=" + errors + ", warnings=" + warnings + "}";
        }
    }
}
