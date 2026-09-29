package org.catrobat.catroid.physics.v2;

import android.util.Log;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.CollidingSprites;
import org.catrobat.catroid.physics.IPhysicsObject;
import org.catrobat.catroid.physics.IPhysicsWorld;
import org.catrobat.catroid.physics.PhysicalCollision;
import org.catrobat.catroid.physics.PhysicsBoundaryBox;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.physics.shapebuilder.PhysicsShapeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class V2PhysicsWorld implements IPhysicsWorld {

    private static final String TAG = V2PhysicsWorld.class.getSimpleName();
    private static final float FIXED_TIMESTEP = 1.0f / 60.0f;
    private static final float MAX_FRAME_TIME = 1.0f / 30.0f;
    private static final int SUB_STEPS = 4;
    private static final float FRAME_SIZE = 200.0f;

    private static boolean nativeLoaded;

    static {
        try {
            System.loadLibrary("neo2d_box2d");
            nativeLoaded = true;
        } catch (Throwable t) {
            Log.e(TAG, "neo2d_box2d load failed", t);
            nativeLoaded = false;
        }
    }

    static boolean isNativeLoaded() {
        return nativeLoaded;
    }

    private boolean disposed;
    private long worldHandle;
    private final ConcurrentHashMap<Sprite, V2PhysicsObject> physicsObjects =
            new ConcurrentHashMap<>();
    private final List<Sprite> activeVerticalBounces =
            Collections.synchronizedList(new ArrayList<>());
    private final List<Sprite> activeHorizontalBounces =
            Collections.synchronizedList(new ArrayList<>());
    private final ConcurrentHashMap<String, Long> joints = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Object> bodyUserData = new ConcurrentHashMap<>();
    private final Set<Long> sensorBodies =
            Collections.newSetFromMap(new ConcurrentHashMap<Long, Boolean>());
    private final List<Long> boundaryBodies = Collections.synchronizedList(new ArrayList<>());

    private final Map<CollidingSprites, PhysicalCollision> collidingSpritesToCollisionMap =
            new HashMap<>();
    private final Queue<Runnable> deferredEvents = new ConcurrentLinkedQueue<>();

    private final float activeAreaWidthFactor;
    private final float activeAreaHeightFactor;
    private final Vector2 activeArea;
    private final Vector2 activeAreaCenter = new Vector2(0.0f, 0.0f);
    private final Vector2 gravity = new Vector2(PhysicsWorld.DEFAULT_GRAVITY);
    private float accumulator;

    private final Map<String, PhysicsWorld.RayCastResult> rayCastResults =
            new ConcurrentHashMap<>();

    public V2PhysicsWorld(float gravityX, float gravityY) {
        this(gravityX, gravityY, PhysicsWorld.DEFAULT_ACTIVE_AREA_WIDTH_FACTOR,
                PhysicsWorld.DEFAULT_ACTIVE_AREA_HEIGHT_FACTOR, 1920, 1080);
    }

    public V2PhysicsWorld(float gravityX, float gravityY, float areaWidthFactor,
            float areaHeightFactor, int width, int height) {
        activeAreaWidthFactor = areaWidthFactor;
        activeAreaHeightFactor = areaHeightFactor;
        activeArea = new Vector2(width * activeAreaWidthFactor, height * activeAreaHeightFactor);
        gravity.set(gravityX, gravityY);
        if (nativeLoaded) {
            worldHandle = Box2D31Bridge.nCreateWorld(gravityX, gravityY);
        }
        createBoundaryBox(width, height);
    }

    long getWorldHandle() {
        return worldHandle;
    }

    private boolean alive() {
        return nativeLoaded && !disposed && worldHandle != 0;
    }

    private void createBoundaryBox(int width, int height) {
        if (!alive()) {
            return;
        }
        float boxWidth = PhysicsWorldConverter.convertNormalToBox2dCoordinate(width);
        float boxHeight = PhysicsWorldConverter.convertNormalToBox2dCoordinate(height);
        float element = PhysicsWorldConverter.convertNormalToBox2dCoordinate(FRAME_SIZE);
        float half = element / 2.0f;
        addBoundarySide(0f, boxHeight / 2f + half, boxWidth, element,
                PhysicsBoundaryBox.BoundaryBoxIdentifier.BBI_HORIZONTAL);
        addBoundarySide(0f, -boxHeight / 2f - half, boxWidth, element,
                PhysicsBoundaryBox.BoundaryBoxIdentifier.BBI_HORIZONTAL);
        addBoundarySide(-boxWidth / 2f - half, 0f, element, boxHeight,
                PhysicsBoundaryBox.BoundaryBoxIdentifier.BBI_VERTICAL);
        addBoundarySide(boxWidth / 2f + half, 0f, element, boxHeight,
                PhysicsBoundaryBox.BoundaryBoxIdentifier.BBI_VERTICAL);
    }

    private void addBoundarySide(float cx, float cy, float w, float h,
            PhysicsBoundaryBox.BoundaryBoxIdentifier identifier) {
        long body = Box2D31Bridge.nCreateBody(worldHandle, 0, 0f, 0f, 0f,
                false, false, 0f, 0f, 0f);
        if (body == 0) {
            return;
        }
        float hx = w / 2f;
        float hy = h / 2f;
        Box2D31Bridge.nAddPolygon(body, 0f, 0f, 0f, PhysicsWorld.CATEGORY_BOUNDARYBOX,
                PhysicsWorld.MASK_BOUNDARYBOX, false,
                new float[]{cx - hx, cy - hy, cx + hx, cy - hy, cx + hx, cy + hy,
                        cx - hx, cy + hy});
        bodyUserData.put(body, identifier);
        boundaryBodies.add(body);
    }

    void setBodyUserData(long bodyHandle, Object data) {
        if (data == null) {
            bodyUserData.remove(bodyHandle);
        } else {
            bodyUserData.put(bodyHandle, data);
        }
    }

    void noteSensorBody(long bodyHandle, boolean sensor) {
        if (sensor) {
            sensorBodies.add(bodyHandle);
        } else {
            sensorBodies.remove(bodyHandle);
        }
    }

    void setBodyGravityScale(long bodyHandle, float scale) {
        if (alive()) {
            Box2D31Bridge.nSetGravityScale(bodyHandle, scale);
        }
    }

    void setBodyBullet(long bodyHandle, boolean bullet) {
        if (alive()) {
            Box2D31Bridge.nSetBullet(bodyHandle, bullet);
        }
    }

    void setBodyFixedRotation(long bodyHandle, boolean fixed) {
        if (alive()) {
            Box2D31Bridge.nSetFixedRotation(bodyHandle, fixed);
        }
    }

    void setBodyLinearDamping(long bodyHandle, float damping) {
        if (alive()) {
            Box2D31Bridge.nSetLinearDamping(bodyHandle, damping);
        }
    }

    void setBodyAngularDamping(long bodyHandle, float damping) {
        if (alive()) {
            Box2D31Bridge.nSetAngularDamping(bodyHandle, damping);
        }
    }

    void setBodyCollisionBits(long bodyHandle, short categoryBits, short maskBits) {
        if (alive()) {
            Box2D31Bridge.nSetBodyFilter(bodyHandle, categoryBits & 0xFFFFL,
                    maskBits & 0xFFFFL);
        }
    }

    @Override
    public Vector2 getActiveArea() {
        return activeArea;
    }

    @Override
    public void setActiveAreaCenter(float x, float y) {
        activeAreaCenter.set(x, y);
    }

    @Override
    public float getActiveAreaCenterX() {
        return activeAreaCenter.x;
    }

    @Override
    public float getActiveAreaCenterY() {
        return activeAreaCenter.y;
    }

    @Override
    public void setBounceOnce(Sprite sprite,
            PhysicsBoundaryBox.BoundaryBoxIdentifier boundaryBoxIdentifier) {
        IPhysicsObject object = physicsObjects.get(sprite);
        if (object == null) {
            return;
        }
        object.setIfOnEdgeBounce(true, sprite);
        switch (boundaryBoxIdentifier) {
            case BBI_HORIZONTAL:
                activeHorizontalBounces.add(sprite);
                break;
            case BBI_VERTICAL:
                activeVerticalBounces.add(sprite);
                break;
        }
    }

    @Override
    public void step(float deltaTime) {
        if (!alive()) {
            return;
        }
        accumulator += Math.min(deltaTime, MAX_FRAME_TIME);
        if (accumulator > MAX_FRAME_TIME * 5) {
            accumulator = MAX_FRAME_TIME * 5;
        }
        while (accumulator >= FIXED_TIMESTEP) {
            try {
                Box2D31Bridge.nStep(worldHandle, FIXED_TIMESTEP, SUB_STEPS);
            } catch (Exception exception) {
                Log.e(TAG, "Box2D 3.x step exception: " + Log.getStackTraceString(exception));
            }
            accumulator -= FIXED_TIMESTEP;
        }
        pollContactEvents();
        flushDeferredEvents();
    }

    private void pollContactEvents() {
        long[] pairs;
        try {
            pairs = Box2D31Bridge.nPollContactEvents(worldHandle);
        } catch (Exception e) {
            return;
        }
        if (pairs == null || pairs.length < 2) {
            return;
        }
        int beginCount = (int) pairs[0];
        int endCount = (int) pairs[1];
        int index = 2;
        for (int i = 0; i < beginCount && index + 1 < pairs.length; i++) {
            applyBeginContact(pairs[index], pairs[index + 1]);
            index += 2;
        }
        for (int i = 0; i < endCount && index + 1 < pairs.length; i++) {
            applyEndContact(pairs[index], pairs[index + 1]);
            index += 2;
        }
    }

    private void applyBeginContact(long bodyA, long bodyB) {
        Object dataA = bodyUserData.get(bodyA);
        Object dataB = bodyUserData.get(bodyB);
        if (dataA instanceof Sprite
                && dataB instanceof PhysicsBoundaryBox.BoundaryBoxIdentifier) {
            Sprite sprite = (Sprite) dataA;
            PhysicsBoundaryBox.BoundaryBoxIdentifier boxId =
                    (PhysicsBoundaryBox.BoundaryBoxIdentifier) dataB;
            deferredEvents.add(() -> bouncedOnEdge(sprite, boxId));
        } else if (dataA instanceof PhysicsBoundaryBox.BoundaryBoxIdentifier
                && dataB instanceof Sprite) {
            Sprite sprite = (Sprite) dataB;
            PhysicsBoundaryBox.BoundaryBoxIdentifier boxId =
                    (PhysicsBoundaryBox.BoundaryBoxIdentifier) dataA;
            deferredEvents.add(() -> bouncedOnEdge(sprite, boxId));
        } else if (dataA instanceof Sprite && dataB instanceof Sprite) {
            registerContact((Sprite) dataA, (Sprite) dataB);
        }
    }

    private void applyEndContact(long bodyA, long bodyB) {
        Object dataA = bodyUserData.get(bodyA);
        Object dataB = bodyUserData.get(bodyB);
        if (dataA instanceof Sprite && dataB instanceof Sprite) {
            Sprite sprite1 = (Sprite) dataA;
            Sprite sprite2 = (Sprite) dataB;
            deferredEvents.add(() -> unregisterContact(sprite1, sprite2));
        }
    }

    private void registerContact(Sprite sprite1, Sprite sprite2) {
        CollidingSprites collidingSprites = new CollidingSprites(sprite1, sprite2);
        if (!collidingSpritesToCollisionMap.containsKey(collidingSprites)) {
            collidingSpritesToCollisionMap.put(collidingSprites,
                    new PhysicalCollision(collidingSprites));
        }
        collidingSpritesToCollisionMap.get(collidingSprites).increaseContactCounter();
    }

    private void unregisterContact(Sprite sprite1, Sprite sprite2) {
        CollidingSprites collidingSprites = new CollidingSprites(sprite1, sprite2);
        if (collidingSpritesToCollisionMap.containsKey(collidingSprites)) {
            PhysicalCollision physicalCollision =
                    collidingSpritesToCollisionMap.get(collidingSprites);
            physicalCollision.decreaseContactCounter();
            if (physicalCollision.getContactCounter() == 0) {
                physicalCollision.sendBounceOffEvents();
                collidingSpritesToCollisionMap.remove(collidingSprites);
            }
        }
    }

    private void flushDeferredEvents() {
        Runnable event;
        while ((event = deferredEvents.poll()) != null) {
            event.run();
        }
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        for (Long joint : new ArrayList<>(joints.values())) {
            try {
                Box2D31Bridge.nDestroyJoint(joint);
            } catch (Exception ignored) {
            }
        }
        joints.clear();
        for (V2PhysicsObject object : new ArrayList<>(physicsObjects.values())) {
            object.dispose();
        }
        physicsObjects.clear();
        bodyUserData.clear();
        sensorBodies.clear();
        boundaryBodies.clear();
        activeVerticalBounces.clear();
        activeHorizontalBounces.clear();
        if (nativeLoaded && worldHandle != 0) {
            Box2D31Bridge.nDestroyWorld(worldHandle);
            worldHandle = 0;
        }
    }

    @Override
    public void render(Matrix4 perspectiveMatrix) {
    }

    private V2PhysicsObject findObject(Sprite spriteA) {
        IPhysicsObject object = physicsObjects.get(spriteA);
        return object instanceof V2PhysicsObject ? (V2PhysicsObject) object : null;
    }

    private float[] readTransform(long bodyHandle) {
        float[] out = new float[3];
        Box2D31Bridge.nGetTransform(bodyHandle, out);
        return out;
    }

    private float[] toLocal(long bodyHandle, float worldX, float worldY) {
        float[] t = readTransform(bodyHandle);
        float dx = worldX - t[0];
        float dy = worldY - t[1];
        float cos = (float) Math.cos(-t[2]);
        float sin = (float) Math.sin(-t[2]);
        return new float[]{dx * cos - dy * sin, dx * sin + dy * cos};
    }

    private long createJoint(String jointId, long jointHandle) {
        if (jointId == null || jointId.isEmpty() || joints.containsKey(jointId)
                || jointHandle == 0) {
            if (jointHandle != 0) {
                Box2D31Bridge.nDestroyJoint(jointHandle);
            }
            return 0;
        }
        joints.put(jointId, jointHandle);
        return jointHandle;
    }

    @Override
    public boolean createPrismaticJoint(String jointId, Sprite spriteA, Sprite spriteB,
            Vector2 worldAnchor, Vector2 worldAxis) {
        V2PhysicsObject objA = findObject(spriteA);
        V2PhysicsObject objB = findObject(spriteB);
        if (!alive() || objA == null || objB == null) {
            return false;
        }
        Vector2 anchor = PhysicsWorldConverter.convertCatroidToBox2dVector(worldAnchor);
        Vector2 axis = PhysicsWorldConverter.convertCatroidToBox2dVector(worldAxis);
        float[] localA = toLocal(objA.getBodyHandle(), anchor.x, anchor.y);
        float[] localB = toLocal(objB.getBodyHandle(), anchor.x, anchor.y);
        return createJoint(jointId, Box2D31Bridge.nCreatePrismaticJoint(worldHandle,
                objA.getBodyHandle(), objB.getBodyHandle(),
                localA[0], localA[1], localB[0], localB[1], axis.x, axis.y, false)) != 0;
    }

    @Override
    public boolean createRevoluteJoint(String jointId, Sprite spriteA, Sprite spriteB,
            Vector2 worldAnchorPoint) {
        V2PhysicsObject objA = findObject(spriteA);
        V2PhysicsObject objB = findObject(spriteB);
        if (!alive() || objA == null || objB == null) {
            return false;
        }
        Vector2 anchor = PhysicsWorldConverter.convertCatroidToBox2dVector(worldAnchorPoint);
        float[] localA = toLocal(objA.getBodyHandle(), anchor.x, anchor.y);
        float[] localB = toLocal(objB.getBodyHandle(), anchor.x, anchor.y);
        return createJoint(jointId, Box2D31Bridge.nCreateRevoluteJoint(worldHandle,
                objA.getBodyHandle(), objB.getBodyHandle(),
                localA[0], localA[1], localB[0], localB[1], false)) != 0;
    }

    @Override
    public boolean createDistanceJoint(String jointId, Sprite spriteA, Sprite spriteB,
            Float length, Float frequency, Float damping) {
        V2PhysicsObject objA = findObject(spriteA);
        V2PhysicsObject objB = findObject(spriteB);
        if (!alive() || objA == null || objB == null) {
            return false;
        }
        float[] posA = readTransform(objA.getBodyHandle());
        float[] posB = readTransform(objB.getBodyHandle());
        float jointLength = length != null
                ? PhysicsWorldConverter.convertNormalToBox2dCoordinate(length)
                : PhysicsWorldConverter.convertNormalToBox2dCoordinate(
                        new Vector2(posB[0] - posA[0], posB[1] - posA[1]).len());
        float hertz = frequency != null ? frequency : 0f;
        float dampingRatio = damping != null ? damping : 0f;
        return createJoint(jointId, Box2D31Bridge.nCreateDistanceJoint(worldHandle,
                objA.getBodyHandle(), objB.getBodyHandle(), 0f, 0f, 0f, 0f,
                jointLength, hertz, dampingRatio, false)) != 0;
    }

    @Override
    public boolean createWeldJoint(String jointId, Sprite spriteA, Sprite spriteB,
            Vector2 worldAnchorPoint) {
        V2PhysicsObject objA = findObject(spriteA);
        V2PhysicsObject objB = findObject(spriteB);
        if (!alive() || objA == null || objB == null) {
            return false;
        }
        Vector2 anchor = PhysicsWorldConverter.convertCatroidToBox2dVector(worldAnchorPoint);
        float[] localA = toLocal(objA.getBodyHandle(), anchor.x, anchor.y);
        float[] localB = toLocal(objB.getBodyHandle(), anchor.x, anchor.y);
        return createJoint(jointId, Box2D31Bridge.nCreateWeldJoint(worldHandle,
                objA.getBodyHandle(), objB.getBodyHandle(),
                localA[0], localA[1], localB[0], localB[1], false)) != 0;
    }

    @Override
    public boolean createPulleyJoint(String jointId, Sprite spriteA, Sprite spriteB,
            Vector2 groundAnchorA, Vector2 groundAnchorB, float ratio) {
        Log.w(TAG, "Pulley joints are not supported by the Box2D 3.x backend");
        return false;
    }

    @Override
    public boolean createGearJoint(String jointId, String jointAId, String jointBId,
            float ratio) {
        Log.w(TAG, "Gear joints are not supported by the Box2D 3.x backend");
        return false;
    }

    @Override
    public void destroyJoint(String jointId) {
        if (jointId == null) {
            return;
        }
        Long joint = joints.remove(jointId);
        if (joint != null && alive()) {
            try {
                Box2D31Bridge.nDestroyJoint(joint);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void applyForce(Sprite sprite, Vector2 force, Vector2 point) {
        V2PhysicsObject object = findObject(sprite);
        if (!alive() || object == null) {
            return;
        }
        Vector2 forceB2d = PhysicsWorldConverter.convertCatroidToBox2dVector(force);
        Vector2 pointB2d = PhysicsWorldConverter.convertCatroidToBox2dVector(point);
        Box2D31Bridge.nApplyForce(object.getBodyHandle(), forceB2d.x, forceB2d.y,
                pointB2d.x, pointB2d.y);
    }

    @Override
    public void applyImpulse(Sprite sprite, Vector2 impulse, Vector2 point) {
        V2PhysicsObject object = findObject(sprite);
        if (!alive() || object == null) {
            return;
        }
        Vector2 impulseB2d = PhysicsWorldConverter.convertCatroidToBox2dVector(impulse);
        Vector2 pointB2d = PhysicsWorldConverter.convertCatroidToBox2dVector(point);
        Box2D31Bridge.nApplyImpulse(object.getBodyHandle(), impulseB2d.x, impulseB2d.y,
                pointB2d.x, pointB2d.y);
    }

    @Override
    public void applyTorque(Sprite sprite, float torque) {
        V2PhysicsObject object = findObject(sprite);
        if (alive() && object != null) {
            Box2D31Bridge.nApplyTorque(object.getBodyHandle(), torque);
        }
    }

    @Override
    public void applyAngularImpulse(Sprite sprite, float impulse) {
        V2PhysicsObject object = findObject(sprite);
        if (alive() && object != null) {
            Box2D31Bridge.nApplyAngularImpulse(object.getBodyHandle(), impulse);
        }
    }

    @Override
    public void performRayCast(String rayId, Vector2 start, Vector2 end) {
        PhysicsWorld.RayCastResult result = new PhysicsWorld.RayCastResult();
        result.hitFraction = -1f;
        if (alive() && rayId != null) {
            Vector2 startB2d = PhysicsWorldConverter.convertCatroidToBox2d(start);
            Vector2 endB2d = PhysicsWorldConverter.convertCatroidToBox2d(end);
            float[] translation = {endB2d.x - startB2d.x, endB2d.y - startB2d.y};
            long[] hits = Box2D31Bridge.nCastRay(worldHandle, startB2d.x, startB2d.y,
                    translation[0], translation[1]);
            if (hits != null && hits.length > 0) {
                int count = (int) hits[0];
                float bestFraction = Float.MAX_VALUE;
                for (int i = 0; i < count; i++) {
                    int base = 1 + i * 6;
                    if (base + 5 >= hits.length) {
                        break;
                    }
                    float fraction = Float.intBitsToFloat((int) hits[base + 1]);
                    if (fraction < bestFraction) {
                        bestFraction = fraction;
                        result.hasHit = true;
                        Object data = bodyUserData.get(hits[base]);
                        if (data instanceof Sprite) {
                            result.hitSprite = (Sprite) data;
                        }
                        result.hitPoint.set(PhysicsWorldConverter.convertBox2dToNormalCoordinate(
                                Float.intBitsToFloat((int) hits[base + 2])),
                                PhysicsWorldConverter.convertBox2dToNormalCoordinate(
                                        Float.intBitsToFloat((int) hits[base + 3])));
                        result.hitNormal.set(Float.intBitsToFloat((int) hits[base + 4]),
                                Float.intBitsToFloat((int) hits[base + 5]));
                        result.hitFraction = fraction;
                    }
                }
            }
            rayCastResults.put(rayId, result);
        }
    }

    @Override
    public PhysicsWorld.RayCastResult getRayCastResult(String rayId) {
        return rayCastResults.get(rayId);
    }

    @Override
    public float castLightRay(float startX, float startY, float endX, float endY,
            Set<String> ignoredSpriteNames, float[] outHitPoint) {
        float fallback = 1f;
        if (!alive()) {
            fillLightRayFallback(outHitPoint, endX, endY);
            return fallback;
        }
        Vector2 startB2d = PhysicsWorldConverter.convertCatroidToBox2dVector(
                new Vector2(startX, startY));
        Vector2 endB2d = PhysicsWorldConverter.convertCatroidToBox2dVector(
                new Vector2(endX, endY));
        long[] hits = Box2D31Bridge.nCastRay(worldHandle, startB2d.x, startB2d.y,
                endB2d.x - startB2d.x, endB2d.y - startB2d.y);
        float closestFraction = 1f;
        boolean hasHit = false;
        float hitX = endX;
        float hitY = endY;
        if (hits != null && hits.length > 0) {
            int count = (int) hits[0];
            for (int i = 0; i < count; i++) {
                int base = 1 + i * 6;
                if (base + 5 >= hits.length) {
                    break;
                }
                Object data = bodyUserData.get(hits[base]);
                boolean sensor = sensorBodies.contains(hits[base]);
                if (PhysicsWorld.shouldIgnoreLightRayFixture(sensor, data,
                        ignoredSpriteNames)) {
                    continue;
                }
                float fraction = Float.intBitsToFloat((int) hits[base + 1]);
                if (!hasHit || fraction < closestFraction) {
                    hasHit = true;
                    closestFraction = fraction;
                    hitX = PhysicsWorldConverter.convertBox2dToNormalCoordinate(
                            Float.intBitsToFloat((int) hits[base + 2]));
                    hitY = PhysicsWorldConverter.convertBox2dToNormalCoordinate(
                            Float.intBitsToFloat((int) hits[base + 3]));
                }
            }
        }
        if (outHitPoint != null && outHitPoint.length >= 2) {
            outHitPoint[0] = hitX;
            outHitPoint[1] = hitY;
        }
        return hasHit ? Math.max(0f, Math.min(1f, closestFraction)) : 1f;
    }

    private static void fillLightRayFallback(float[] outHitPoint, float endX, float endY) {
        if (outHitPoint != null && outHitPoint.length >= 2) {
            outHitPoint[0] = endX;
            outHitPoint[1] = endY;
        }
    }

    @Override
    public void setGravity(float x, float y) {
        gravity.set(x, y);
        if (alive()) {
            Vector2 gravityB2d = PhysicsWorldConverter.convertCatroidToBox2dVector(
                    new Vector2(x, y));
            Box2D31Bridge.nSetGravity(worldHandle, gravityB2d.x, gravityB2d.y);
        }
    }

    @Override
    public Vector2 getGravity() {
        return new Vector2(gravity);
    }

    @Override
    public void changeLook(IPhysicsObject physicsObject, Look look) {
        if (!(physicsObject instanceof V2PhysicsObject)) {
            return;
        }
        List<V2PhysicsObject.ShapeDesc> descs = new ArrayList<>();
        if (look != null && look.getLookData() != null && look.getLookData().getFile() != null) {
            com.badlogic.gdx.physics.box2d.Shape[] shapes =
                    PhysicsShapeBuilder.getInstance().getScaledShapes(look.getLookData(),
                            look.getSizeInUserInterfaceDimensionUnit() / 100f);
            descs = V2PhysicsObject.convertShapes(shapes);
        }
        ((V2PhysicsObject) physicsObject).setShapeDescs(descs,
                PhysicsObject.DEFAULT_DENSITY, PhysicsObject.DEFAULT_FRICTION,
                PhysicsObject.DEFAULT_BOUNCE_FACTOR,
                PhysicsWorld.CATEGORY_PHYSICSOBJECT, false);
    }

    @Override
    public IPhysicsObject getPhysicsObject(Sprite sprite) {
        Objects.requireNonNull(sprite, "sprite must not be null");
        V2PhysicsObject object = physicsObjects.get(sprite);
        if (object == null) {
            object = new V2PhysicsObject(this, sprite);
            physicsObjects.put(sprite, object);
            setBodyUserData(object.getBodyHandle(), sprite);
        }
        return object;
    }

    @Override
    public boolean hasPhysicsObject(Sprite sprite) {
        return sprite != null && physicsObjects.containsKey(sprite);
    }

    @Override
    public Object getNativeWorld() {
        return worldHandle;
    }

    @Override
    public void bouncedOnEdge(Sprite sprite,
            PhysicsBoundaryBox.BoundaryBoxIdentifier boundaryBoxIdentifier) {
        IPhysicsObject object = physicsObjects.get(sprite);
        if (object == null) {
            return;
        }
        switch (boundaryBoxIdentifier) {
            case BBI_HORIZONTAL:
                if (activeHorizontalBounces.remove(sprite)
                        && !activeVerticalBounces.contains(sprite)) {
                    object.setIfOnEdgeBounce(false, sprite);
                    PhysicalCollision.fireBounceOffEvent(sprite, null);
                }
                break;
            case BBI_VERTICAL:
                if (activeVerticalBounces.remove(sprite)
                        && !activeHorizontalBounces.contains(sprite)) {
                    object.setIfOnEdgeBounce(false, sprite);
                    PhysicalCollision.fireBounceOffEvent(sprite, null);
                }
                break;
        }
    }
}
