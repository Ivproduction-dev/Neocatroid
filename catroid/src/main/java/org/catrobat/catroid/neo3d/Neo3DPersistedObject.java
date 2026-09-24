package org.catrobat.catroid.neo3d;

import java.io.Serializable;

public class Neo3DPersistedObject implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public String modelAssetKey;
    public int primitiveKind = -1;
    public float x;
    public float y;
    public float z;
    public float scaleX = 1f;
    public float scaleY = 1f;
    public float scaleZ = 1f;
    public float yawDeg;
    public float pitchDeg;
    public float rollDeg;
    public float quaternionX;
    public float quaternionY;
    public float quaternionZ;
    public float quaternionW = 1f;
    public int physicsMotionType = -1;
    public int physicsShapeType;
    public float physicsMass = 1f;
    public float physicsFriction = 0.5f;
    public float physicsRestitution = 0.1f;
    public float physicsGravityFactor = 1f;
    public float physicsLinearDamping = 0.05f;
    public float physicsAngularDamping = 0.1f;
    public boolean physicsContinuousCollision;

    public Neo3DPersistedObject() {
    }

    public Neo3DPersistedObject(String name) {
        this.name = name;
    }
}
