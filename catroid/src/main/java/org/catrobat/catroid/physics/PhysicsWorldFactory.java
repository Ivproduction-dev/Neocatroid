package org.catrobat.catroid.physics;

import org.catrobat.catroid.content.Project;

public final class PhysicsWorldFactory {

    private PhysicsWorldFactory() {
    }

    public static IPhysicsWorld create(int width, int height, Project project) {
        int version = project == null ? Project.PHYSICS_BOX2D_2X : project.getPhysicsVersion();
        if (version == Project.PHYSICS_BOX2D_3X) {
            return new org.catrobat.catroid.physics.v2.V2PhysicsWorld(
                    PhysicsWorld.DEFAULT_GRAVITY.x, PhysicsWorld.DEFAULT_GRAVITY.y,
                    PhysicsWorld.DEFAULT_ACTIVE_AREA_WIDTH_FACTOR,
                    PhysicsWorld.DEFAULT_ACTIVE_AREA_HEIGHT_FACTOR, width, height);
        }
        if (project == null) {
            return new PhysicsWorld(width, height);
        }
        return new PhysicsWorld(width, height, project);
    }
}
