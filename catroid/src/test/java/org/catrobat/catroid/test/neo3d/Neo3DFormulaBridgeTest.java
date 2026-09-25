package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.Neo3DEngine;
import org.catrobat.catroid.neo3d.Neo3DFacade;
import org.catrobat.catroid.neo3d.Neo3DFormulaBridge;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DPhysicsBody;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Neo3DFormulaBridgeTest {

    @After
    public void tearDown() {
        Neo3DFacade.uninstall();
    }

    @Test
    public void readPositionRotationAndScaleByName() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        Neo3DScene scene = engine.createScene("formula-scene");
        Neo3DGameObject obj = engine.createObject(scene.getId(), "myCube");
        obj.getTransform().setPosition(1f, 2f, 3f);
        obj.getTransform().setRotationEulerDeg(10f, 20f, 30f);
        obj.getTransform().setScale(2f, 3f, 4f);

        assertEquals(1.0, Neo3DFormulaBridge.getX("myCube"), 1e-6);
        assertEquals(2.0, Neo3DFormulaBridge.getY("myCube"), 1e-6);
        assertEquals(3.0, Neo3DFormulaBridge.getZ("myCube"), 1e-6);
        assertEquals(10.0, Neo3DFormulaBridge.getYaw("myCube"), 1e-4);
        assertEquals(20.0, Neo3DFormulaBridge.getPitch("myCube"), 1e-4);
        assertEquals(30.0, Neo3DFormulaBridge.getRoll("myCube"), 1e-4);
        assertEquals(2.0, Neo3DFormulaBridge.getScaleX("myCube"), 1e-6);
        assertEquals(3.0, Neo3DFormulaBridge.getScaleY("myCube"), 1e-6);
        assertEquals(4.0, Neo3DFormulaBridge.getScaleZ("myCube"), 1e-6);
        engine.dispose();
    }

    @Test
    public void bodyCountReflectsPhysicsBackend() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        Neo3DScene scene = engine.createScene("formula-scene");
        assertEquals(0.0, Neo3DFormulaBridge.getBodyCount(), 0.0);
        Neo3DGameObject obj = engine.createObject(scene.getId(), "myCube");
        assertEquals(0.0, Neo3DFormulaBridge.getBodyCount(), 0.0);
        Neo3DFacade.facadeSetPhysicsBody(scene.getId(), obj.getId(),
                new Neo3DPhysicsBody(Neo3DPhysicsBody.MotionType.DYNAMIC,
                        Neo3DPhysicsBody.ShapeType.AUTO, 1f));
        assertEquals(1.0, Neo3DFormulaBridge.getBodyCount(), 0.0);
        engine.dispose();
    }

    @Test
    public void distanceSpeedAndExists() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        Neo3DScene scene = engine.createScene("gameplay-formula-scene");
        Neo3DGameObject first = engine.createObject(scene.getId(), "first");
        first.getTransform().setPosition(0f, 0f, 0f);
        Neo3DGameObject second = engine.createObject(scene.getId(), "second");
        second.getTransform().setPosition(3f, 4f, 0f);

        assertEquals(5.0, Neo3DFormulaBridge.getDistance("first", "second"), 1e-6);
        assertEquals(0.0, Neo3DFormulaBridge.getDistance("first", "missing"), 0.0);
        assertEquals(0.0, Neo3DFormulaBridge.getSpeed("first"), 0.0);
        assertEquals(1.0, Neo3DFormulaBridge.getExists("first"), 0.0);
        assertEquals(0.0, Neo3DFormulaBridge.getExists("missing"), 0.0);
        engine.dispose();
    }

    @Test
    public void unknownObjectAndMissingEngineReturnDefaults() {
        Neo3DEngine engine = Neo3DEngine.create(null, Neo3DEngine.BackendType.NULL);
        Neo3DFacade.install(engine);
        engine.createScene("formula-scene");

        assertEquals(0.0, Neo3DFormulaBridge.getX("missing"), 0.0);
        assertEquals(0.0, Neo3DFormulaBridge.getYaw("missing"), 0.0);
        assertEquals(1.0, Neo3DFormulaBridge.getScaleX("missing"), 0.0);
        engine.dispose();

        Neo3DFacade.uninstall();
        assertEquals(0.0, Neo3DFormulaBridge.getX("myCube"), 0.0);
        assertEquals(1.0, Neo3DFormulaBridge.getScaleZ("myCube"), 0.0);
    }
}
