package org.catrobat.catroid.neo3d;

import java.io.Serializable;

public class Neo3DPersistedObject implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public String modelAssetKey;
    public String objectType = "MODEL";
    public String parentName;
    public int primitiveKind = -1;
    public boolean active = true;
    public boolean visible = true;
    public float x;
    public float y;
    public float z;
    public float scaleX = 1f;
    public float scaleY = 1f;
    public float scaleZ = 1f;
    public float yawDeg;
    public float pitchDeg;
    public float rollDeg;
    public float colorRed = 0.55f;
    public float colorGreen = 0.72f;
    public float colorBlue = 0.98f;
    public float colorAlpha = 1f;
    public float metallic;
    public float roughness = 0.8f;
    public float emissiveRed;
    public float emissiveGreen;
    public float emissiveBlue;
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
    public float physicsShapeScaleX = 1f;
    public float physicsShapeScaleY = 1f;
    public float physicsShapeScaleZ = 1f;
    public boolean keyframeLoop;
    public java.util.ArrayList<Neo3DKeyframe> keyframes;

    public static class Neo3DKeyframe implements Serializable {
        private static final long serialVersionUID = 1L;

        public float time;
        public float x;
        public float y;
        public float z;
        public float yawDeg;
        public float pitchDeg;
        public float rollDeg;
        public float scaleX = 1f;
        public float scaleY = 1f;
        public float scaleZ = 1f;

        public Neo3DKeyframe copy() {
            Neo3DKeyframe copy = new Neo3DKeyframe();
            copy.time = time;
            copy.x = x;
            copy.y = y;
            copy.z = z;
            copy.yawDeg = yawDeg;
            copy.pitchDeg = pitchDeg;
            copy.rollDeg = rollDeg;
            copy.scaleX = scaleX;
            copy.scaleY = scaleY;
            copy.scaleZ = scaleZ;
            return copy;
        }
    }
    public boolean cameraUsesTransformOrientation;
    public boolean camera;
    public boolean cameraMain = true;
    public float cameraFov = 60f;
    public float cameraNear = 0.1f;
    public float cameraFar = 1000f;
    public float cameraAperture = 16f;
    public float cameraShutterSpeed = 0.008f;
    public float cameraSensitivity = 100f;
    public float cameraTargetX;
    public float cameraTargetY;
    public float cameraTargetZ;
    public String lightType;
    public float lightIntensity = 40000f;
    public float lightRed = 1f;
    public float lightGreen = 1f;
    public float lightBlue = 1f;
    public float lightRange = 10f;
    public boolean lightShadows;
    public float lightDirectionX;
    public float lightDirectionY = -1f;
    public float lightDirectionZ;
    public float lightInnerConeDeg = 25f;
    public float lightOuterConeDeg = 40f;

    public Neo3DPersistedObject() {
    }

    public Neo3DPersistedObject(String name) {
        this.name = name;
    }
}
