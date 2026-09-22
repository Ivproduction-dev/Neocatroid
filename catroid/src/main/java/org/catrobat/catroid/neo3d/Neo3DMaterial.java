package org.catrobat.catroid.neo3d;

public class Neo3DMaterial {

    public enum AlphaMode {
        OPAQUE,
        MASKED,
        BLEND
    }

    private final float[] baseColor = {1f, 1f, 1f, 1f};
    private float metallic = 0f;
    private float roughness = 0.8f;
    private final float[] emissiveColor = {0f, 0f, 0f};
    private float emissiveIntensity = 1f;

    private String baseColorTexturePath;
    private String metallicRoughnessTexturePath;
    private String normalTexturePath;
    private String emissiveTexturePath;
    private float normalScale = 1f;
    private float uvScaleU = 1f;
    private float uvScaleV = 1f;

    private boolean doubleSided = false;
    private AlphaMode alphaMode = AlphaMode.OPAQUE;

    public float[] getBaseColor() {
        return new float[]{baseColor[0], baseColor[1], baseColor[2], baseColor[3]};
    }

    public void setBaseColor(float r, float g, float b, float a) {
        baseColor[0] = r;
        baseColor[1] = g;
        baseColor[2] = b;
        baseColor[3] = a;
    }

    public float getMetallic() {
        return metallic;
    }

    public void setMetallic(float metallic) {
        this.metallic = metallic;
    }

    public float getRoughness() {
        return roughness;
    }

    public void setRoughness(float roughness) {
        this.roughness = roughness;
    }

    public float[] getEmissiveColor() {
        return Neo3DMath.vec3Copy(emissiveColor);
    }

    public void setEmissiveColor(float r, float g, float b) {
        emissiveColor[0] = r;
        emissiveColor[1] = g;
        emissiveColor[2] = b;
    }

    public float getEmissiveIntensity() {
        return emissiveIntensity;
    }

    public void setEmissiveIntensity(float emissiveIntensity) {
        this.emissiveIntensity = emissiveIntensity;
    }

    public String getBaseColorTexturePath() {
        return baseColorTexturePath;
    }

    public void setBaseColorTexturePath(String path) {
        this.baseColorTexturePath = path;
    }

    public String getMetallicRoughnessTexturePath() {
        return metallicRoughnessTexturePath;
    }

    public void setMetallicRoughnessTexturePath(String path) {
        this.metallicRoughnessTexturePath = path;
    }

    public String getNormalTexturePath() {
        return normalTexturePath;
    }

    public void setNormalTexturePath(String path) {
        this.normalTexturePath = path;
    }

    public String getEmissiveTexturePath() {
        return emissiveTexturePath;
    }

    public void setEmissiveTexturePath(String path) {
        this.emissiveTexturePath = path;
    }

    public float getNormalScale() {
        return normalScale;
    }

    public void setNormalScale(float normalScale) {
        this.normalScale = normalScale;
    }

    public float getUvScaleU() {
        return uvScaleU;
    }

    public float getUvScaleV() {
        return uvScaleV;
    }

    public void setUvScale(float u, float v) {
        this.uvScaleU = u;
        this.uvScaleV = v;
    }

    public boolean isDoubleSided() {
        return doubleSided;
    }

    public void setDoubleSided(boolean doubleSided) {
        this.doubleSided = doubleSided;
    }

    public AlphaMode getAlphaMode() {
        return alphaMode;
    }

    public void setAlphaMode(AlphaMode alphaMode) {
        this.alphaMode = alphaMode;
    }
}
