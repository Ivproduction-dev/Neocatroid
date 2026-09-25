package org.catrobat.catroid.neo3d.physics;

final class JoltNativeBridge {
    private JoltNativeBridge() {
    }

    static native long nCreateWorld(int workers, float gravityX, float gravityY, float gravityZ);

    static native void nDestroyWorld(long worldHandle);

    static native long nCreateBody(long worldHandle, int motionType, int shapeType,
            float halfX, float halfY, float halfZ, float radius, float halfHeight,
            float mass, float friction, float restitution, float gravityFactor,
            float linearDamping, float angularDamping, boolean continuousCollision,
            float[] transform);

    static native void nDestroyBody(long worldHandle, long bodyId);

    static native void nSetTransform(long worldHandle, long bodyId, float[] transform);

    static native void nSetLinearVelocity(long worldHandle, long bodyId,
            float x, float y, float z);

    static native void nGetLinearVelocity(long worldHandle, long bodyId, float[] output);

    static native long[] nGetActiveContacts(long worldHandle);

    static native void nAddImpulse(long worldHandle, long bodyId, float x, float y, float z);

    static native void nSetGravity(long worldHandle, float x, float y, float z);

    static native int nUpdate(long worldHandle, float deltaSeconds, long[] bodyIds,
            float[] output);
}
