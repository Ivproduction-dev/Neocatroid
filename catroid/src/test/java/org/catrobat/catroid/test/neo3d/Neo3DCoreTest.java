package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.Neo3DAnimationClip;
import org.catrobat.catroid.neo3d.Neo3DCamera;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DLight;
import org.catrobat.catroid.neo3d.Neo3DMaterial;
import org.catrobat.catroid.neo3d.Neo3DMath;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Neo3DCoreTest {

    @Test
    public void mathIdentityAndTranslation() {
        float[] id = Neo3DMath.mat4Identity();
        assertEquals(1f, id[0], 0f);
        assertEquals(1f, id[15], 0f);
        assertEquals(0f, id[12], 0f);
        float[] t = Neo3DMath.mat4Mul(id, Neo3DMath.mat4Translation(new float[]{1, 2, 3}));
        assertEquals(1f, t[12], 1e-6f);
        assertEquals(2f, t[13], 1e-6f);
        assertEquals(3f, t[14], 1e-6f);
    }

    @Test
    public void mathYaw90RotatesXAxisToMinusZ() {
        float[] q = Neo3DMath.quatFromEulerDeg(90f, 0f, 0f);
        float[] m = Neo3DMath.mat4FromTRS(new float[]{0, 0, 0}, q, new float[]{1, 1, 1});
        assertEquals(0f, m[0], 1e-5f);
        assertEquals(0f, m[1], 1e-5f);
        assertEquals(-1f, m[2], 1e-5f);
    }

    @Test
    public void sceneCrudAndHierarchy() {
        Neo3DScene scene = new Neo3DScene("test");
        Neo3DGameObject parent = scene.createObject("parent");
        Neo3DGameObject child = scene.createObject("child");
        assertEquals(2, scene.getObjectCount());
        assertTrue(scene.setParent(child.getId(), parent.getId()));
        assertEquals(parent.getTransform(), child.getTransform().getParent());
        assertEquals(1, parent.getTransform().getChildren().size());

        parent.getTransform().setPosition(10f, 0f, 0f);
        child.getTransform().setPosition(1f, 0f, 0f);
        float[] world = child.getTransform().getWorldMatrix();
        assertEquals(11f, world[12], 1e-4f);

        assertFalse(scene.setParent(parent.getId(), child.getId()));
        assertFalse(scene.setParent(parent.getId(), parent.getId()));
        assertFalse(scene.setParent("nope", parent.getId()));

        assertTrue(scene.removeObject(parent.getId()));
        assertNull(child.getTransform().getParent());
        assertNotNull(scene.getObject(child.getId()));
        assertEquals(1, scene.getObjectCount());
    }

    @Test
    public void componentSlots() {
        Neo3DGameObject obj = new Neo3DScene("s").createObject("o");
        assertNull(obj.getCamera());
        Neo3DCamera cam = new Neo3DCamera();
        cam.setFovDeg(70f);
        obj.setCamera(cam);
        assertEquals(70f, obj.getCamera().getFovDeg(), 0f);

        Neo3DLight light = Neo3DLight.spot(1000f);
        light.setConeDeg(10f, 20f);
        obj.setLight(light);
        assertEquals(Neo3DLight.Type.SPOT, obj.getLight().getType());

        Neo3DMaterial mat = new Neo3DMaterial();
        mat.setRoughness(0.5f);
        mat.setNormalTexturePath("textures/n.png");
        obj.setMaterial(mat);
        assertEquals("textures/n.png", obj.getMaterial().getNormalTexturePath());
    }

    @Test
    public void animationStateTicksAndStops() {
        Neo3DAnimationClip clip = new Neo3DAnimationClip("Idle", 2f);
        Neo3DAnimationClip.State state = new Neo3DAnimationClip.State(clip);
        state.tick(1f);
        assertEquals(1f, state.getTimeSec(), 1e-6f);
        state.tick(1.5f);
        assertTrue(state.isPlaying());
        assertEquals(0.5f, state.getTimeSec(), 1e-6f);

        clip.setLoop(false);
        Neo3DAnimationClip.State once = new Neo3DAnimationClip.State(clip);
        once.tick(5f);
        assertEquals(2f, once.getTimeSec(), 1e-6f);
        assertFalse(once.isPlaying());

        Neo3DGameObject obj = new Neo3DScene("s").createObject("o");
        obj.addAnimationClip(clip);
        obj.playAnimation("Idle");
        assertNotNull(obj.getAnimationState());
        obj.playAnimation("Missing");
        assertEquals("Idle", obj.getAnimationState().getClipName());
    }
}
