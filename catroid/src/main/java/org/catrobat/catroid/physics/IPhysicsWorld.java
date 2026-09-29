package org.catrobat.catroid.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;

import java.util.Set;

public interface IPhysicsWorld {

    Vector2 getActiveArea();

    void setActiveAreaCenter(float x, float y);

    float getActiveAreaCenterX();

    float getActiveAreaCenterY();

    void setBounceOnce(Sprite sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier boundaryBoxIdentifier);

    void step(float deltaTime);

    void dispose();

    void render(Matrix4 perspectiveMatrix);

    boolean createPrismaticJoint(String jointId, Sprite spriteA, Sprite spriteB, Vector2 worldAnchor, Vector2 worldAxis);

    boolean createRevoluteJoint(String jointId, Sprite spriteA, Sprite spriteB, Vector2 worldAnchorPoint);

    boolean createDistanceJoint(String jointId, Sprite spriteA, Sprite spriteB, Float length, Float frequency, Float damping);

    boolean createWeldJoint(String jointId, Sprite spriteA, Sprite spriteB, Vector2 worldAnchorPoint);

    boolean createPulleyJoint(String jointId, Sprite spriteA, Sprite spriteB, Vector2 groundAnchorA, Vector2 groundAnchorB, float ratio);

    boolean createGearJoint(String jointId, String jointAId, String jointBId, float ratio);

    void destroyJoint(String jointId);

    void applyForce(Sprite sprite, Vector2 force, Vector2 point);

    void applyImpulse(Sprite sprite, Vector2 impulse, Vector2 point);

    void applyTorque(Sprite sprite, float torque);

    void applyAngularImpulse(Sprite sprite, float impulse);

    void performRayCast(String rayId, Vector2 start, Vector2 end);

    PhysicsWorld.RayCastResult getRayCastResult(String rayId);

    float castLightRay(float startX, float startY, float endX, float endY,
            Set<String> ignoredSpriteNames, float[] outHitPoint);

    void setGravity(float x, float y);

    Vector2 getGravity();

    void changeLook(IPhysicsObject physicsObject, Look look);

    IPhysicsObject getPhysicsObject(Sprite sprite);

    boolean hasPhysicsObject(Sprite sprite);

    Object getNativeWorld();

    void bouncedOnEdge(Sprite sprite, PhysicsBoundaryBox.BoundaryBoxIdentifier boundaryBoxIdentifier);
}
