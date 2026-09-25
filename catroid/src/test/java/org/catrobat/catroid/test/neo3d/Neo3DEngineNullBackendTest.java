package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.Neo3DEngine;
import org.catrobat.catroid.neo3d.Neo3DFacade;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.backend.Neo3DNullBackend;
import org.catrobat.catroid.neo3d.demo.Neo3DEmbeddedCube;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Neo3DEngineNullBackendTest {

    @After
    public void tearDown() {
        Neo3DFacade.uninstall();
    }

    @Test
    public void engineLifecycleOnNullBackend() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        assertEquals("Null", engine.getBackend().getName());
        Neo3DNullBackend backend = (Neo3DNullBackend) engine.getBackend();

        Neo3DScene scene = engine.createScene("s1");
        assertTrue(backend.hasScene(scene.getId()));
        Neo3DGameObject obj = engine.createObject(scene.getId(), "box");
        assertTrue(backend.hasObject(scene.getId(), obj.getId()));

        engine.setObjectModelBytes(scene.getId(), obj.getId(), "embedded:cube",
                Neo3DEmbeddedCube.buildDefaultGlb());
        assertEquals(1, engine.getAssetManager().getRefCount("embedded:cube"));

        for (int i = 0; i < 5; i++) {
            engine.update(scene.getId(), 1f / 60f, 1000000L * i);
        }
        assertEquals(5, backend.getStats().framesRendered);
        assertTrue(engine.getFpsEma() > 0f);

        assertTrue(engine.removeObject(scene.getId(), obj.getId()));
        assertFalse(backend.hasObject(scene.getId(), obj.getId()));
        assertEquals(0, engine.getAssetManager().getRefCount("embedded:cube"));

        assertTrue(engine.deleteScene(scene.getId()));
        assertFalse(backend.hasScene(scene.getId()));
        engine.dispose();
        assertTrue(engine.isDisposed());
    }

    @Test
    public void facadeDelegatesWithoutTouchingV1() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        assertTrue(Neo3DFacade.isReady());

        String sceneId = Neo3DFacade.facadeCreateScene("facade-scene");
        String objId = Neo3DFacade.facadeCreateObject(sceneId, "facade-box");
        Neo3DFacade.facadeSetPosition(sceneId, objId, 1f, 2f, 3f);
        Neo3DFacade.facadeSetRotation(sceneId, objId, 45f, 0f, 0f);
        Neo3DFacade.facadeSetScale(sceneId, objId, 2f, 2f, 2f);
        Neo3DFacade.facadeSetModelBytes(sceneId, objId, "embedded:cube",
                Neo3DEmbeddedCube.buildDefaultGlb());
        float ms = Neo3DFacade.facadeUpdate(sceneId, 1f / 60f, System.nanoTime());
        assertTrue(ms >= 0f);

        Neo3DGameObject obj = engine.getScene(sceneId).getObject(objId);
        assertNotNull(obj);
        assertEquals(1f, obj.getTransform().getPosition()[0], 1e-6f);

        assertTrue(Neo3DFacade.facadeRemoveObject(sceneId, objId));
        assertNull(engine.getScene(sceneId).getObject(objId));
        assertTrue(Neo3DFacade.facadeDeleteScene(sceneId));
        engine.dispose();
        Neo3DFacade.uninstall();
        assertFalse(Neo3DFacade.isReady());
    }

    @Test
    public void firstPersonTouchLookClampsPitch() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DScene scene = engine.createScene("camera-scene");
        Neo3DFacade.install(engine);
        String cameraId = Neo3DFacade.facadeSetMainCameraPosition(
                scene.getId(), 0f, 0f, 5f);

        Neo3DFacade.facadeSetCameraTouchLook(scene.getId(),
                Neo3DEngine.CameraTouchMode.FIRST_PERSON.ordinal(), 1f, -30f, 45f);
        assertTrue(Neo3DFacade.facadeDragCameraLook(scene.getId(), 0f, 100f));

        Neo3DGameObject camera = engine.getScene(scene.getId()).getObject(cameraId);
        assertNotNull(camera);
        assertEquals(-30f, camera.getTransform().getEulerDeg()[1], 1e-4f);
        assertTrue(camera.getCamera().isUseTransformOrientation());
        engine.dispose();
    }

    @Test
    public void freeTouchLookAllowsFullPitch() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        Neo3DScene scene = engine.createScene("free-camera-scene");
        String cameraId = Neo3DFacade.facadeSetMainCameraPosition(
                scene.getId(), 0f, 0f, 5f);

        engine.setCameraTouchLook(scene.getId(),
                Neo3DEngine.CameraTouchMode.FREE.ordinal(), 1f, -30f, 30f);
        assertTrue(engine.dragCameraLook(scene.getId(), 0f, 100f));

        Neo3DGameObject camera = engine.getScene(scene.getId()).getObject(cameraId);
        assertNotNull(camera);
        assertEquals(-100f, camera.getTransform().getEulerDeg()[1], 1e-4f);
        engine.dispose();
    }

    @Test
    public void disabledTouchLookDoesNotRotate() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        Neo3DScene scene = engine.createScene("disabled-camera-scene");
        String cameraId = Neo3DFacade.facadeSetMainCameraPosition(
                scene.getId(), 0f, 0f, 5f);

        engine.setCameraTouchLook(scene.getId(),
                Neo3DEngine.CameraTouchMode.DISABLED.ordinal(), 1f, -30f, 30f);
        assertFalse(engine.dragCameraLook(scene.getId(), 10f, 10f));
        assertEquals(0f, engine.getScene(scene.getId()).getObject(cameraId)
                .getTransform().getEulerDeg()[0], 1e-4f);
        engine.dispose();
    }

    @Test
    public void cameraFollowTracksTargetWithOffset() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        Neo3DScene scene = engine.createScene("follow-scene");
        Neo3DGameObject target = engine.createObject(scene.getId(), "hero");
        target.getTransform().setPosition(1f, 2f, 3f);
        String cameraId = Neo3DFacade.facadeSetMainCameraPosition(
                scene.getId(), 0f, 0f, 0f);

        engine.setCameraFollow(scene.getId(), "hero", 0f, 3f, 5f, false);
        engine.update(scene.getId(), 1f / 60f, System.nanoTime());

        float[] cameraPos = engine.getScene(scene.getId()).getObject(cameraId)
                .getTransform().getPosition();
        assertEquals(1f, cameraPos[0], 1e-4f);
        assertEquals(5f, cameraPos[1], 1e-4f);
        assertEquals(8f, cameraPos[2], 1e-4f);
        engine.dispose();
    }

    @Test
    public void cameraFollowWithLookAtRotatesTowardTarget() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        Neo3DScene scene = engine.createScene("follow-look-scene");
        Neo3DGameObject target = engine.createObject(scene.getId(), "hero");
        target.getTransform().setPosition(0f, 0f, 0f);
        String cameraId = Neo3DFacade.facadeSetMainCameraPosition(
                scene.getId(), 0f, 3f, 4f);

        engine.setCameraFollow(scene.getId(), "hero", 0f, 3f, 4f, true);
        engine.update(scene.getId(), 1f / 60f, System.nanoTime());

        Neo3DGameObject camera = engine.getScene(scene.getId()).getObject(cameraId);
        assertTrue(camera.getCamera().isUseTransformOrientation());
        assertEquals(0f, camera.getTransform().getEulerDeg()[0], 1f);
        assertEquals(-36.87f, camera.getTransform().getEulerDeg()[1], 1f);
        engine.dispose();
    }

    @Test
    public void pointCameraMoveForwardTurnVisibleAndClear() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        Neo3DScene scene = engine.createScene("gameplay-scene");
        Neo3DGameObject box = engine.createObject(scene.getId(), "box");
        Neo3DGameObject ball = engine.createObject(scene.getId(), "ball");
        ball.getTransform().setPosition(5f, 0f, 0f);
        String cameraId = Neo3DFacade.facadeSetMainCameraPosition(
                scene.getId(), 0f, 0f, 5f);

        assertTrue(Neo3DFacade.facadePointMainCameraAt(scene.getId(), "ball"));
        Neo3DGameObject camera = engine.getScene(scene.getId()).getObject(cameraId);
        assertEquals(-45f, camera.getTransform().getEulerDeg()[0], 1f);
        assertEquals(0f, camera.getTransform().getEulerDeg()[1], 1f);

        assertTrue(engine.turnObjectToward(scene.getId(), box.getId(), "ball"));
        assertEquals(-90f, box.getTransform().getEulerDeg()[0], 1f);

        assertTrue(engine.moveObjectForward(scene.getId(), box.getId(), 2f));
        assertEquals(2f, box.getTransform().getPosition()[0], 1e-4f);

        assertTrue(engine.setObjectVisible(scene.getId(), box.getId(), false));
        assertFalse(box.isVisible());

        assertEquals(2, engine.clearObjects(scene.getId()));
        assertEquals(1, scene.getObjectCount());
        assertNotNull(scene.getObject(cameraId));
        engine.dispose();
    }

    @Test
    public void duplicateNamesGetUniqueSuffixes() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DScene scene = engine.createScene("dup-scene");

        Neo3DGameObject first = engine.createObject(scene.getId(), "myCube");
        Neo3DGameObject second = engine.createObject(scene.getId(), "myCube");
        Neo3DGameObject third = engine.createObject(scene.getId(), "myCube");

        assertEquals("myCube", first.getName());
        assertEquals("myCube (2)", second.getName());
        assertEquals("myCube (3)", third.getName());
        assertEquals(3, scene.getAllObjects().size());
        engine.dispose();
        assertTrue(engine.isDisposed());
    }
}
