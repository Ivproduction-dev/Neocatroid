package org.catrobat.catroid.test.physics;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.physics.IPhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldFactory;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class PhysicsWorldFactoryTest {

    @Test
    public void defaultVersionIsLegacy() {
        Project project = new Project(MockUtil.mockContextForProject(), "Project");
        assertEquals(Project.PHYSICS_BOX2D_2X, project.getPhysicsVersion());
    }

    @Test
    public void factoryCreatesLegacyWorldByDefault() {
        Project project = new Project(MockUtil.mockContextForProject(), "Project");
        IPhysicsWorld world = PhysicsWorldFactory.create(1280, 720, project);
        try {
            assertTrue(world instanceof PhysicsWorld);
        } finally {
            world.dispose();
        }
    }

    @Test
    public void factoryCreatesV2WorldForNewBackend() {
        Project project = new Project(MockUtil.mockContextForProject(), "Project");
        project.setPhysicsVersion(Project.PHYSICS_BOX2D_3X);
        assertEquals(Project.PHYSICS_BOX2D_3X, project.getPhysicsVersion());
        IPhysicsWorld world = PhysicsWorldFactory.create(1280, 720, project);
        try {
            assertTrue(world instanceof org.catrobat.catroid.physics.v2.V2PhysicsWorld);
        } finally {
            world.dispose();
        }
    }
}
