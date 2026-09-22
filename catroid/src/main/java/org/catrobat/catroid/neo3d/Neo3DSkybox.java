package org.catrobat.catroid.neo3d;

public class Neo3DSkybox {

    public enum Mode {
        COLOR,
        GRADIENT,
        KTX_FILE
    }

    private Mode mode = Mode.COLOR;
    private final float[] colorA = {0.16f, 0.29f, 0.42f, 1f};
    private final float[] colorB = {0.62f, 0.72f, 0.82f, 1f};
    private String ktxPath;
    private float iblIntensity = 30000f;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public float[] getColorA() {
        return new float[]{colorA[0], colorA[1], colorA[2], colorA[3]};
    }

    public void setColorA(float r, float g, float b, float a) {
        colorA[0] = r;
        colorA[1] = g;
        colorA[2] = b;
        colorA[3] = a;
    }

    public float[] getColorB() {
        return new float[]{colorB[0], colorB[1], colorB[2], colorB[3]};
    }

    public void setColorB(float r, float g, float b, float a) {
        colorB[0] = r;
        colorB[1] = g;
        colorB[2] = b;
        colorB[3] = a;
    }

    public String getKtxPath() {
        return ktxPath;
    }

    public void setKtxPath(String ktxPath) {
        this.ktxPath = ktxPath;
    }

    public float getIblIntensity() {
        return iblIntensity;
    }

    public void setIblIntensity(float iblIntensity) {
        this.iblIntensity = iblIntensity;
    }
}
