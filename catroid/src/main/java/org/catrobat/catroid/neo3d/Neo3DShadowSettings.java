package org.catrobat.catroid.neo3d;

public class Neo3DShadowSettings {

    private boolean enabled = true;
    private int mapSize = 1024;
    private float bias = 0.0005f;
    private float normalBias = 0.5f;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMapSize() {
        return mapSize;
    }

    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }

    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
    }

    public float getNormalBias() {
        return normalBias;
    }

    public void setNormalBias(float normalBias) {
        this.normalBias = normalBias;
    }
}
