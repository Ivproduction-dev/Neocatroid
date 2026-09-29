package org.catrobat.catroid.physics;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.Sprite;

public interface IPhysicsObject {

    void dispose();

    void copyTo(IPhysicsObject destination);

    void setShape(Object shapes);

    void setLinearDamping(float damping);

    void setAngularDamping(float damping);

    PhysicsObject.Type getType();

    void setType(PhysicsObject.Type type);

    float getDirection();

    void setDirection(float degrees);

    float getX();

    float getY();

    Vector2 getMassCenter();

    float getCircumference();

    Vector2 getPosition();

    void setX(float x);

    void setY(float y);

    void setPosition(float x, float y);

    void setPosition(Vector2 position);

    float getRotationSpeed();

    void setRotationSpeed(float degreesPerSecond);

    Vector2 getVelocity();

    void setVelocity(float x, float y);

    void setVelocity(Vector2 velocity);

    void setAngularVelocity(float radiansPerSecond);

    void setBullet(boolean bullet);

    void setFixedRotation(boolean fixed);

    void setGravityScale(float scale);

    float getGravityScale();

    void setSensor(boolean sensor);

    float getMass();

    float getBounceFactor();

    void setMass(float mass);

    void setDensity(float density);

    float getFriction();

    void setFriction(float friction);

    void setBounceFactor(float bounceFactor);

    void setIfOnEdgeBounce(boolean bounce, Sprite sprite);

    void getBoundaryBox(Vector2 lowerLeft, Vector2 upperRight);

    Vector2 getBoundaryBoxDimensions();

    void activateHangup();

    void deactivateHangup(boolean record);

    void activateNonColliding(boolean updateState);

    void deactivateNonColliding(boolean record, boolean updateState);

    void setCollisionMaskRecord(short mask);

    void activateFixed();

    void deactivateFixed(boolean record);

    boolean isNonColliding();
}
