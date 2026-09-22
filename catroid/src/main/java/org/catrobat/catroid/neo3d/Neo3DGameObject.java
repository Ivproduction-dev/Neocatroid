package org.catrobat.catroid.neo3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Neo3DGameObject {

    private final String id;
    private String name;
    private boolean active = true;
    private final Neo3DTransform transform;

    private String modelPath;
    private boolean visible = true;

    private Neo3DCamera camera;
    private Neo3DLight light;
    private Neo3DMaterial material;
    private Neo3DCustomMaterial customMaterial;
    private final List<Neo3DAnimationClip> animationClips = new ArrayList<>();
    private Neo3DAnimationClip.State animationState;

    public Neo3DGameObject(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.transform = new Neo3DTransform(this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Neo3DTransform getTransform() {
        return transform;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Neo3DCamera getCamera() {
        return camera;
    }

    public void setCamera(Neo3DCamera camera) {
        this.camera = camera;
    }

    public Neo3DLight getLight() {
        return light;
    }

    public void setLight(Neo3DLight light) {
        this.light = light;
    }

    public Neo3DMaterial getMaterial() {
        return material;
    }

    public void setMaterial(Neo3DMaterial material) {
        this.material = material;
    }

    public Neo3DCustomMaterial getCustomMaterial() {
        return customMaterial;
    }

    public void setCustomMaterial(Neo3DCustomMaterial customMaterial) {
        this.customMaterial = customMaterial;
    }

    public List<Neo3DAnimationClip> getAnimationClips() {
        return Collections.unmodifiableList(animationClips);
    }

    public void addAnimationClip(Neo3DAnimationClip clip) {
        animationClips.add(clip);
        if (animationState == null) {
            animationState = new Neo3DAnimationClip.State(clip);
        }
    }

    public void playAnimation(String clipName) {
        for (Neo3DAnimationClip clip : animationClips) {
            if (clip.getName().equals(clipName)) {
                animationState = new Neo3DAnimationClip.State(clip);
                animationState.setPlaying(true);
                return;
            }
        }
    }

    public Neo3DAnimationClip.State getAnimationState() {
        return animationState;
    }

    public void stopAnimation() {
        if (animationState != null) {
            animationState.setPlaying(false);
        }
    }
}
