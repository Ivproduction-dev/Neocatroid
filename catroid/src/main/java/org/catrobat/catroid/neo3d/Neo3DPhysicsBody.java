package org.catrobat.catroid.neo3d;

import java.io.Serializable;

public final class Neo3DPhysicsBody implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MotionType {
        NONE,
        STATIC,
        KINEMATIC,
        DYNAMIC
    }

    public enum ShapeType {
        AUTO,
        BOX,
        SPHERE,
        CAPSULE,
        CYLINDER
    }

    private MotionType motionType = MotionType.NONE;
    private ShapeType shapeType = ShapeType.AUTO;
    private float mass = 1f;
    private float friction = 0.5f;
    private float restitution = 0.1f;
    private float gravityFactor = 1f;
    private float linearDamping = 0.05f;
    private float angularDamping = 0.1f;
    private boolean continuousCollision;

    public Neo3DPhysicsBody() {
    }

    public Neo3DPhysicsBody(MotionType motionType, ShapeType shapeType, float mass) {
        setMotionType(motionType);
        setShapeType(shapeType);
        setMass(mass);
    }

    public Neo3DPhysicsBody copy() {
        Neo3DPhysicsBody copy = new Neo3DPhysicsBody(motionType, shapeType, mass);
        copy.friction = friction;
        copy.restitution = restitution;
        copy.gravityFactor = gravityFactor;
        copy.linearDamping = linearDamping;
        copy.angularDamping = angularDamping;
        copy.continuousCollision = continuousCollision;
        return copy;
    }

    public MotionType getMotionType() {
        return motionType;
    }

    public void setMotionType(MotionType motionType) {
        this.motionType = motionType == null ? MotionType.NONE : motionType;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType == null ? ShapeType.AUTO : shapeType;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = clamp(mass, 0.001f, 1000000f);
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = clamp(friction, 0f, 2f);
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = clamp(restitution, 0f, 1f);
    }

    public float getGravityFactor() {
        return gravityFactor;
    }

    public void setGravityFactor(float gravityFactor) {
        this.gravityFactor = clamp(gravityFactor, 0f, 100f);
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = clamp(linearDamping, 0f, 10f);
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = clamp(angularDamping, 0f, 10f);
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    private static float clamp(float value, float min, float max) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }
}
