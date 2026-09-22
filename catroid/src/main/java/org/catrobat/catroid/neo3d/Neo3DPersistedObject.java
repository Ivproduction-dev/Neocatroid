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

    public Neo3DPersistedObject() {
    }

    public Neo3DPersistedObject(String name) {
        this.name = name;
    }
}
