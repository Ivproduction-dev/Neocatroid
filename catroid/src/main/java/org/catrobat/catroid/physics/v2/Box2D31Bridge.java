package org.catrobat.catroid.physics.v2;

final class Box2D31Bridge {
    private Box2D31Bridge() {
    }

    static native long nCreateWorld(float gravityX, float gravityY);

    static native void nDestroyWorld(long worldHandle);

    static native void nStep(long worldHandle, float deltaSeconds, int subSteps);

    static native void nSetGravity(long worldHandle, float x, float y);

    static native long nCreateBody(long worldHandle, int type, float x, float y,
            float angleRad, boolean fixedRotation, boolean bullet, float gravityScale,
            float linearDamping, float angularDamping);

    static native void nDestroyBody(long bodyHandle);

    static native void nSetTransform(long bodyHandle, float x, float y, float angleRad);

    static native void nGetTransform(long bodyHandle, float[] output);

    static native void nGetWorldCenter(long bodyHandle, float[] output);

    static native void nSetLinearVelocity(long bodyHandle, float x, float y);

    static native void nGetLinearVelocity(long bodyHandle, float[] output);

    static native void nSetAngularVelocity(long bodyHandle, float velocity);

    static native float nGetAngularVelocity(long bodyHandle);

    static native void nSetType(long bodyHandle, int type);

    static native void nApplyForce(long bodyHandle, float fx, float fy, float px, float py);

    static native void nApplyImpulse(long bodyHandle, float ix, float iy, float px, float py);

    static native void nApplyTorque(long bodyHandle, float torque);

    static native void nApplyAngularImpulse(long bodyHandle, float impulse);

    static native void nSetGravityScale(long bodyHandle, float scale);

    static native void nSetFixedRotation(long bodyHandle, boolean fixed);

    static native void nSetBullet(long bodyHandle, boolean bullet);

    static native void nSetLinearDamping(long bodyHandle, float damping);

    static native void nSetAngularDamping(long bodyHandle, float damping);

    static native void nSetBodyFilter(long bodyHandle, long categoryBits, long maskBits);

    static native void nClearShapes(long bodyHandle);

    static native void nAddPolygon(long bodyHandle, float density, float friction,
            float restitution, long categoryBits, long maskBits, boolean isSensor,
            float[] vertices);

    static native void nAddCircle(long bodyHandle, float density, float friction,
            float restitution, long categoryBits, long maskBits, boolean isSensor,
            float cx, float cy, float radius);

    static native void nAddSegment(long bodyHandle, float density, float friction,
            float restitution, long categoryBits, long maskBits, boolean isSensor,
            float x1, float y1, float x2, float y2);

    static native void nSetShapesDensity(long bodyHandle, float density);

    static native void nSetShapesSurface(long bodyHandle, float friction, float restitution);

    static native void nGetAABB(long bodyHandle, float[] output);

    static native long[] nPollContactEvents(long worldHandle);

    static native long[] nCastRay(long worldHandle, float ox, float oy, float tx, float ty);

    static native long nCreateDistanceJoint(long worldHandle, long bodyA, long bodyB,
            float anchorAx, float anchorAy, float anchorBx, float anchorBy,
            float length, float hertz, float dampingRatio, boolean collideConnected);

    static native long nCreateRevoluteJoint(long worldHandle, long bodyA, long bodyB,
            float anchorAx, float anchorAy, float anchorBx, float anchorBy,
            boolean collideConnected);

    static native long nCreatePrismaticJoint(long worldHandle, long bodyA, long bodyB,
            float anchorAx, float anchorAy, float anchorBx, float anchorBy,
            float axisX, float axisY, boolean collideConnected);

    static native long nCreateWeldJoint(long worldHandle, long bodyA, long bodyB,
            float anchorAx, float anchorAy, float anchorBx, float anchorBy,
            boolean collideConnected);

    static native void nDestroyJoint(long jointHandle);
}
