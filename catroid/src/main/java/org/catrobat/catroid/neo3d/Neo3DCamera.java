package org.catrobat.catroid.neo3d;

public class Neo3DCamera {

    private float fovDeg = 60f;
    private float near = 0.1f;
    private float far = 1000f;
    private float aperture = 16f;
    private float shutterSpeed = 1f / 125f;
    private float sensitivity = 100f;
    private final float[] lookAtTarget = {0f, 0f, 0f};
    private final float[] up = {0f, 1f, 0f};
    private boolean mainCamera = true;
    private boolean useTransformOrientation;

    public float getFovDeg() {
        return fovDeg;
    }

    public void setFovDeg(float fovDeg) {
        this.fovDeg = fovDeg;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
    }

    public float getAperture() {
        return aperture;
    }

    public void setAperture(float aperture) {
        this.aperture = aperture;
    }

    public float getShutterSpeed() {
        return shutterSpeed;
    }

    public void setShutterSpeed(float shutterSpeed) {
        this.shutterSpeed = shutterSpeed;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setExposure(float aperture, float shutterSpeed, float sensitivity) {
        this.aperture = aperture;
        this.shutterSpeed = shutterSpeed;
        this.sensitivity = sensitivity;
    }

    public float[] getLookAtTarget() {
        return Neo3DMath.vec3Copy(lookAtTarget);
    }

    public void setLookAtTarget(float x, float y, float z) {
        lookAtTarget[0] = x;
        lookAtTarget[1] = y;
        lookAtTarget[2] = z;
    }

    public float[] getUp() {
        return Neo3DMath.vec3Copy(up);
    }

    public void setUp(float x, float y, float z) {
        up[0] = x;
        up[1] = y;
        up[2] = z;
    }

    public boolean isUseTransformOrientation() {
        return useTransformOrientation;
    }

    public void setUseTransformOrientation(boolean useTransformOrientation) {
        this.useTransformOrientation = useTransformOrientation;
    }

    public boolean isMainCamera() {
        return mainCamera;
    }

    public void setMainCamera(boolean mainCamera) {
        this.mainCamera = mainCamera;
    }
}
