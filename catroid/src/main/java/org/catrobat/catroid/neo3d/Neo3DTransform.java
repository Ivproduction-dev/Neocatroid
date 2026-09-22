package org.catrobat.catroid.neo3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Neo3DTransform {

    private final Neo3DGameObject owner;
    private final float[] position = {0f, 0f, 0f};
    private final float[] eulerDeg = {0f, 0f, 0f};
    private float[] quaternion = Neo3DMath.quatIdentity();
    private final float[] scale = {1f, 1f, 1f};

    private Neo3DTransform parent;
    private final List<Neo3DTransform> children = new ArrayList<>();

    private boolean dirty = true;
    private float[] cachedLocalMatrix = Neo3DMath.mat4Identity();
    private float[] cachedWorldMatrix = Neo3DMath.mat4Identity();

    Neo3DTransform(Neo3DGameObject owner) {
        this.owner = owner;
    }

    public Neo3DGameObject getOwner() {
        return owner;
    }

    public float[] getPosition() {
        return Neo3DMath.vec3Copy(position);
    }

    public void setPosition(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
        markDirty();
    }

    public float[] getEulerDeg() {
        return new float[]{eulerDeg[0], eulerDeg[1], eulerDeg[2]};
    }

    public void setRotationEulerDeg(float yawDeg, float pitchDeg, float rollDeg) {
        eulerDeg[0] = yawDeg;
        eulerDeg[1] = pitchDeg;
        eulerDeg[2] = rollDeg;
        quaternion = Neo3DMath.quatFromEulerDeg(yawDeg, pitchDeg, rollDeg);
        markDirty();
    }

    public float[] getQuaternion() {
        return new float[]{quaternion[0], quaternion[1], quaternion[2], quaternion[3]};
    }

    public void setQuaternion(float x, float y, float z, float w) {
        quaternion = Neo3DMath.quatNormalize(new float[]{x, y, z, w});
        markDirty();
    }

    public float[] getScale() {
        return Neo3DMath.vec3Copy(scale);
    }

    public void setScale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
        markDirty();
    }

    public Neo3DTransform getParent() {
        return parent;
    }

    public List<Neo3DTransform> getChildren() {
        return Collections.unmodifiableList(children);
    }

    void attachTo(Neo3DTransform newParent) {
        if (parent != null) {
            parent.children.remove(this);
        }
        parent = newParent;
        if (newParent != null) {
            newParent.children.add(this);
        }
        markDirty();
    }

    void detach() {
        attachTo(null);
    }

    private void markDirty() {
        if (!dirty) {
            dirty = true;
            for (Neo3DTransform child : children) {
                child.markDirty();
            }
        } else {
            for (Neo3DTransform child : children) {
                child.markDirty();
            }
        }
    }

    public float[] getLocalMatrix() {
        if (dirty) {
            recompute();
        }
        return cachedLocalMatrix.clone();
    }

    public float[] getWorldMatrix() {
        if (dirty) {
            recompute();
        }
        return cachedWorldMatrix.clone();
    }

    public boolean isDirty() {
        return dirty;
    }

    private void recompute() {
        cachedLocalMatrix = Neo3DMath.mat4FromTRS(position, quaternion, scale);
        if (parent != null) {
            if (parent.dirty) {
                parent.recompute();
            }
            cachedWorldMatrix = Neo3DMath.mat4Mul(parent.cachedWorldMatrix, cachedLocalMatrix);
        } else {
            cachedWorldMatrix = cachedLocalMatrix.clone();
        }
        dirty = false;
    }
}
