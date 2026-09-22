package org.catrobat.catroid.neo3d;

public class Neo3DLight {

    public enum Type {
        DIRECTIONAL,
        POINT,
        SPOT
    }

    private final Type type;
    private final float[] color = {1f, 1f, 1f};
    private float intensity;
    private final float[] direction = {0f, -1f, 0f};
    private final float[] position = {0f, 3f, 0f};
    private float range = -1f;
    private float innerConeDeg = 25f;
    private float outerConeDeg = 40f;
    private boolean castShadows = false;

    public Neo3DLight(Type type, float intensity) {
        this.type = type;
        this.intensity = intensity;
    }

    public static Neo3DLight directional(float intensityLux) {
        return new Neo3DLight(Type.DIRECTIONAL, intensityLux);
    }

    public static Neo3DLight point(float intensity) {
        return new Neo3DLight(Type.POINT, intensity);
    }

    public static Neo3DLight spot(float intensity) {
        return new Neo3DLight(Type.SPOT, intensity);
    }

    public Type getType() {
        return type;
    }

    public float[] getColor() {
        return Neo3DMath.vec3Copy(color);
    }

    public void setColor(float r, float g, float b) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float[] getDirection() {
        return Neo3DMath.vec3Copy(direction);
    }

    public void setDirection(float x, float y, float z) {
        direction[0] = x;
        direction[1] = y;
        direction[2] = z;
    }

    public float[] getPosition() {
        return Neo3DMath.vec3Copy(position);
    }

    public void setPosition(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getInnerConeDeg() {
        return innerConeDeg;
    }

    public float getOuterConeDeg() {
        return outerConeDeg;
    }

    public void setConeDeg(float innerDeg, float outerDeg) {
        this.innerConeDeg = innerDeg;
        this.outerConeDeg = outerDeg;
    }

    public boolean isCastShadows() {
        return castShadows;
    }

    public void setCastShadows(boolean castShadows) {
        this.castShadows = castShadows;
    }
}
