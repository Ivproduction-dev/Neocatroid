package org.catrobat.catroid.neo3d;

public final class Neo3DPhysicsPose {
    private final String sceneId;
    private final String objectId;
    private final float[] position;
    private final float[] rotation;

    public Neo3DPhysicsPose(String sceneId, String objectId, float[] position, float[] rotation) {
        this.sceneId = sceneId;
        this.objectId = objectId;
        this.position = position.clone();
        this.rotation = rotation.clone();
    }

    public String getSceneId() {
        return sceneId;
    }

    public String getObjectId() {
        return objectId;
    }

    public float[] getPosition() {
        return position.clone();
    }

    public float[] getRotation() {
        return rotation.clone();
    }
}
