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
}
