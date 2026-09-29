package org.catrobat.catroid.test.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.IPhysicsObject;
import org.catrobat.catroid.physics.IPhysicsWorld;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorldFactory;
import org.catrobat.catroid.physics.v2.V2PhysicsObject;
import org.catrobat.catroid.physics.v2.V2PhysicsWorld;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class V2PhysicsBackendTest {

    private Project newV2Project() {
        Project project = new Project(MockUtil.mockContextForProject(), "Project");
        project.setPhysicsVersion(Project.PHYSICS_BOX2D_3X);
        return project;
    }

    @Test
    public void degradedWorldSurvivesWithoutNativeLibrary() {
        IPhysicsWorld world = PhysicsWorldFactory.create(1280, 720, newV2Project());
        try {
            Sprite sprite = new Sprite("hero");
            IPhysicsObject object = world.getPhysicsObject(sprite);
            assertTrue(object instanceof V2PhysicsObject);
            assertTrue(world.hasPhysicsObject(sprite));
            object.setType(PhysicsObject.Type.DYNAMIC);
            object.setPosition(10f, 20f);
            object.setVelocity(1f, 2f);
            object.setMass(5f);
            world.setGravity(0f, -10f);
            world.step(1f / 60f);
            world.dispose();
        } finally {
            world.dispose();
        }
    }

    @Test
    public void shapeAreaMath() {
        V2PhysicsObject.ShapeDesc square = new V2PhysicsObject.ShapeDesc(
                V2PhysicsObject.ShapeDesc.POLYGON, new float[]{0f, 0f, 2f, 0f, 2f, 2f, 0f, 2f});
        assertEquals(4.0, square.area(), 1e-6);
        V2PhysicsObject.ShapeDesc circle = new V2PhysicsObject.ShapeDesc(
                V2PhysicsObject.ShapeDesc.CIRCLE, new float[]{0f, 0f, 1f});
        assertEquals(Math.PI, circle.area(), 1e-6);
    }

    @Test
    public void convertsGdxShapesToDescriptors() {
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(1f, 2f);
        CircleShape circle = new CircleShape();
        circle.setRadius(3f);
        try {
            List<V2PhysicsObject.ShapeDesc> descs = V2PhysicsObject.convertShapes(
                    new com.badlogic.gdx.physics.box2d.Shape[]{poly, circle});
            assertEquals(2, descs.size());
            assertEquals(V2PhysicsObject.ShapeDesc.POLYGON, descs.get(0).kind);
            assertEquals(8.0f, descs.get(0).area(), 1e-4);
            assertEquals(V2PhysicsObject.ShapeDesc.CIRCLE, descs.get(1).kind);
        } finally {
            poly.dispose();
            circle.dispose();
        }
    }

    @Test
    public void v2WorldExposesNativeHandleObject() {
        V2PhysicsWorld world = (V2PhysicsWorld) PhysicsWorldFactory.create(
                1280, 720, newV2Project());
        try {
            assertTrue(world.getNativeWorld() instanceof Long);
            assertEquals(new Vector2(1280f * 3f, 720f * 2f), world.getActiveArea());
        } finally {
            world.dispose();
        }
    }
}
