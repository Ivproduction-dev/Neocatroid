package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.Neo3DCamera;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DMath;
import org.catrobat.catroid.neo3d.Neo3DPicker;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class Neo3DPickerTest {

    private static Neo3DScene sceneWithCamera() {
        Neo3DScene scene = new Neo3DScene("pick");
        Neo3DGameObject cam = scene.createObject("cam");
        cam.getTransform().setPosition(0f, 0f, 5f);
        cam.setCamera(new Neo3DCamera());
        return scene;
    }

    @Test
    public void lookAtCentersOrigin() {
        float[] view = Neo3DMath.mat4LookAt(new float[]{0f, 0f, 5f}, new float[]{0f, 0f, 0f},
                new float[]{0f, 1f, 0f});
        float[] proj = Neo3DMath.mat4Perspective(60f, 800f / 600f, 0.1f, 1000f);
        float[] screen = Neo3DMath.projectToScreen(new float[]{0f, 0f, 0f},
                Neo3DMath.mat4Mul(proj, view), 800f, 600f);
        assertNotNull(screen);
        assertEquals(400f, screen[0], 1f);
        assertEquals(300f, screen[1], 1f);
    }

    @Test
    public void behindCameraReturnsNull() {
        float[] view = Neo3DMath.mat4LookAt(new float[]{0f, 0f, 5f}, new float[]{0f, 0f, 0f},
                new float[]{0f, 1f, 0f});
        float[] proj = Neo3DMath.mat4Perspective(60f, 800f / 600f, 0.1f, 1000f);
        assertNull(Neo3DMath.projectToScreen(new float[]{0f, 0f, 10f},
                Neo3DMath.mat4Mul(proj, view), 800f, 600f));
    }

    @Test
    public void picksCenteredObject() {
        Neo3DScene scene = sceneWithCamera();
        Neo3DGameObject box = scene.createObject("box");
        Neo3DGameObject cam = scene.findByName("cam");
        String picked = Neo3DPicker.pickObject(scene, cam, 800f, 600f, 400f, 300f);
        assertEquals(box.getId(), picked);
    }

    @Test
    public void transformOrientedCameraUsesItsRotationForPicking() {
        Neo3DScene scene = new Neo3DScene("rotated-pick");
        Neo3DGameObject cam = scene.createObject("cam");
        cam.getTransform().setPosition(0f, 0f, 5f);
        cam.setCamera(new Neo3DCamera());
        cam.getCamera().setUseTransformOrientation(true);
        Neo3DGameObject box = scene.createObject("box");

        assertEquals(box.getId(), Neo3DPicker.pickObject(scene, cam, 800f, 600f, 400f, 300f));

        cam.getTransform().setRotationEulerDeg(180f, 0f, 0f);
        assertNull(Neo3DPicker.pickObject(scene, cam, 800f, 600f, 400f, 300f));
    }

    @Test
    public void farTouchPicksNothing() {
        Neo3DScene scene = sceneWithCamera();
        scene.createObject("box");
        Neo3DGameObject cam = scene.findByName("cam");
        assertNull(Neo3DPicker.pickObject(scene, cam, 800f, 600f, 10f, 10f));
    }

    @Test
    public void invisibleObjectSkipped() {
        Neo3DScene scene = sceneWithCamera();
        Neo3DGameObject box = scene.createObject("box");
        box.setVisible(false);
        Neo3DGameObject cam = scene.findByName("cam");
        assertNull(Neo3DPicker.pickObject(scene, cam, 800f, 600f, 400f, 300f));
    }

    @Test
    public void nearestWins() {
        Neo3DScene scene = sceneWithCamera();
        Neo3DGameObject far = scene.createObject("far");
        far.getTransform().setPosition(3f, 0f, 0f);
        Neo3DGameObject near = scene.createObject("near");
        near.getTransform().setPosition(0f, 0f, 4f);
        Neo3DGameObject cam = scene.findByName("cam");
        String picked = Neo3DPicker.pickObject(scene, cam, 800f, 600f, 400f, 300f);
        assertEquals(near.getId(), picked);
    }
}
