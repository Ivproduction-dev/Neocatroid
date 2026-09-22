package org.catrobat.catroid.neo3d;

public final class Neo3DPicker {

    public static final float DEFAULT_THRESHOLD_PX = 64f;

    private Neo3DPicker() {
    }

    public static String pickObject(Neo3DScene scene, Neo3DGameObject cameraObject,
            float viewportWidth, float viewportHeight, float touchX, float touchY,
            float thresholdPx) {
        if (scene == null || cameraObject == null || cameraObject.getCamera() == null) {
            return null;
        }
        if (viewportWidth <= 0f || viewportHeight <= 0f) {
            return null;
        }
        float[] world = cameraObject.getTransform().getWorldMatrix();
        float[] eye = {world[12], world[13], world[14]};
        Neo3DCamera camera = cameraObject.getCamera();
        float[] view = Neo3DMath.mat4LookAt(eye, camera.getLookAtTarget(), camera.getUp());
        float[] proj = Neo3DMath.mat4Perspective(camera.getFovDeg(),
                viewportWidth / viewportHeight, camera.getNear(), camera.getFar());
        float[] viewProj = Neo3DMath.mat4Mul(proj, view);
        String bestId = null;
        float bestDistSq = thresholdPx * thresholdPx;
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj == null || obj == cameraObject || !obj.isActive() || !obj.isVisible()) {
                continue;
            }
            float[] objWorld = obj.getTransform().getWorldMatrix();
            float[] center = {objWorld[12], objWorld[13], objWorld[14]};
            float[] screen = Neo3DMath.projectToScreen(center, viewProj, viewportWidth,
                    viewportHeight);
            if (screen == null) {
                continue;
            }
            float dx = screen[0] - touchX;
            float dy = screen[1] - touchY;
            float distSq = dx * dx + dy * dy;
            if (distSq <= bestDistSq) {
                bestDistSq = distSq;
                bestId = obj.getId();
            }
        }
        return bestId;
    }

    public static String pickObject(Neo3DScene scene, Neo3DGameObject cameraObject,
            float viewportWidth, float viewportHeight, float touchX, float touchY) {
        return pickObject(scene, cameraObject, viewportWidth, viewportHeight, touchX, touchY,
                DEFAULT_THRESHOLD_PX);
    }
}
