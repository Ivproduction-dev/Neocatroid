package org.catrobat.catroid.physics.v2;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.IPhysicsObject;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;

import java.util.ArrayList;
import java.util.List;

public class V2PhysicsObject implements IPhysicsObject {

    public static final class ShapeDesc {
        public static final int POLYGON = 0;
        public static final int CIRCLE = 1;

        public final int kind;
        public final float[] data;

        public ShapeDesc(int kind, float[] data) {
            this.kind = kind;
            this.data = data;
        }

        public float area() {
            if (kind == CIRCLE && data.length >= 3) {
                return (float) (Math.PI * data[2] * data[2]);
            }
            if (kind == POLYGON && data.length >= 6) {
                float sum = 0f;
                int n = data.length / 2;
                for (int i = 0; i < n; i++) {
                    float x0 = data[2 * i];
                    float y0 = data[2 * i + 1];
                    float x1 = data[2 * ((i + 1) % n)];
                    float y1 = data[2 * ((i + 1) % n) + 1];
                    sum += x0 * y1 - x1 * y0;
                }
                return Math.abs(sum) * 0.5f;
            }
            return 0f;
        }
    }

    private final V2PhysicsWorld world;
    private final Sprite sprite;
    private long bodyHandle;
    private boolean disposed;

    private final List<ShapeDesc> shapeDescs = new ArrayList<>();
    private Type type;
    private float mass = PhysicsObject.DEFAULT_MASS;
    private float circumference;
    private boolean ifOnEdgeBounce;
    private boolean sensor;

    private short collisionMaskRecord = PhysicsWorld.MASK_PHYSICSOBJECT;
    private short categoryMaskRecord = PhysicsWorld.CATEGORY_PHYSICSOBJECT;
    private boolean nonCollidingActive = false;

    private float density = PhysicsObject.DEFAULT_DENSITY;
    private float friction = PhysicsObject.DEFAULT_FRICTION;
    private float restitution = PhysicsObject.DEFAULT_BOUNCE_FACTOR;

    private final Vector2 velocityRecord = new Vector2();
    private float rotationSpeedRecord;
    private float gravityScaleRecord;
    private Type savedType = Type.NONE;

    V2PhysicsObject(V2PhysicsWorld world, Sprite sprite) {
        this.world = world;
        this.sprite = sprite;
        if (V2PhysicsWorld.isNativeLoaded() && world.getWorldHandle() != 0) {
            bodyHandle = Box2D31Bridge.nCreateBody(world.getWorldHandle(), 2, 0f, 0f, 0f,
                    false, false, 1f, 0f, 0f);
        }
        setType(Type.NONE);
    }

    long getBodyHandle() {
        return bodyHandle;
    }

    private boolean alive() {
        return !disposed && bodyHandle != 0;
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        if (bodyHandle != 0) {
            Box2D31Bridge.nDestroyBody(bodyHandle);
            bodyHandle = 0;
        }
        shapeDescs.clear();
    }

    @Override
    public void copyTo(IPhysicsObject destination) {
        destination.setType(getType());
        destination.setPosition(getPosition());
        destination.setDirection(getDirection());
        destination.setMass(getMass());
        destination.setRotationSpeed(getRotationSpeed());
        destination.setBounceFactor(getBounceFactor());
        destination.setFriction(getFriction());
        destination.setVelocity(getVelocity());
        if (destination instanceof V2PhysicsObject) {
            ((V2PhysicsObject) destination).setShapeDescs(new ArrayList<>(shapeDescs),
                    density, friction, restitution, categoryMaskRecord, sensor);
        }
    }

    @Override
    public void setShape(Object shapes) {
        if (shapes instanceof List) {
            List<?> list = (List<?>) shapes;
            List<ShapeDesc> descs = new ArrayList<>(list.size());
            for (Object item : list) {
                if (item instanceof ShapeDesc) {
                    descs.add((ShapeDesc) item);
                }
            }
            setShapeDescs(descs, density, friction, restitution, categoryMaskRecord, sensor);
            return;
        }
        if (shapes instanceof com.badlogic.gdx.physics.box2d.Shape[]) {
            setShapeDescs(convertShapes((com.badlogic.gdx.physics.box2d.Shape[]) shapes),
                    density, friction, restitution, categoryMaskRecord, sensor);
            return;
        }
        setShapeDescs(new ArrayList<ShapeDesc>(), density, friction, restitution,
                categoryMaskRecord, sensor);
    }

    void setShapeDescs(List<ShapeDesc> descs, float density, float friction,
            float restitution, short categoryBits, boolean sensor) {
        shapeDescs.clear();
        shapeDescs.addAll(descs);
        if (!alive()) {
            return;
        }
        Box2D31Bridge.nClearShapes(bodyHandle);
        for (ShapeDesc desc : shapeDescs) {
            if (desc.kind == ShapeDesc.CIRCLE && desc.data.length >= 3) {
                Box2D31Bridge.nAddCircle(bodyHandle, density, friction, restitution,
                        categoryBits & 0xFFFFL, collisionMaskRecord & 0xFFFFL, sensor,
                        desc.data[0], desc.data[1], desc.data[2]);
            } else if (desc.kind == ShapeDesc.POLYGON && desc.data.length >= 6) {
                Box2D31Bridge.nAddPolygon(bodyHandle, density, friction, restitution,
                        categoryBits & 0xFFFFL, collisionMaskRecord & 0xFFFFL, sensor,
                        desc.data);
            }
        }
        calculateCircumference();
    }

    public static List<ShapeDesc> convertShapes(
            com.badlogic.gdx.physics.box2d.Shape[] shapes) {
        List<ShapeDesc> descs = new ArrayList<>();
        if (shapes == null) {
            return descs;
        }
        for (com.badlogic.gdx.physics.box2d.Shape shape : shapes) {
            if (shape instanceof com.badlogic.gdx.physics.box2d.CircleShape) {
                com.badlogic.gdx.physics.box2d.CircleShape circle =
                        (com.badlogic.gdx.physics.box2d.CircleShape) shape;
                Vector2 center = circle.getPosition();
                descs.add(new ShapeDesc(ShapeDesc.CIRCLE,
                        new float[]{center.x, center.y, circle.getRadius()}));
            } else if (shape instanceof com.badlogic.gdx.physics.box2d.PolygonShape) {
                com.badlogic.gdx.physics.box2d.PolygonShape poly =
                        (com.badlogic.gdx.physics.box2d.PolygonShape) shape;
                int count = poly.getVertexCount();
                if (count < 3) {
                    continue;
                }
                float[] verts = new float[count * 2];
                Vector2 tmp = new Vector2();
                for (int i = 0; i < count; i++) {
                    poly.getVertex(i, tmp);
                    verts[2 * i] = tmp.x;
                    verts[2 * i + 1] = tmp.y;
                }
                descs.add(new ShapeDesc(ShapeDesc.POLYGON, verts));
            }
        }
        return descs;
    }

    private void calculateCircumference() {
        if (shapeDescs.isEmpty()) {
            circumference = 0;
            return;
        }
        circumference = PhysicsWorldConverter.convertNormalToBox2dCoordinate(
                getBoundaryBoxDimensions().len() / 2.0f);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        if (this.type == type) {
            return;
        }
        this.type = type;
        if (!alive()) {
            return;
        }
        switch (type) {
            case DYNAMIC:
                Box2D31Bridge.nSetType(bodyHandle, 2);
                setGravityScaleInternal(1.0f);
                setMass(mass);
                collisionMaskRecord = PhysicsWorld.MASK_PHYSICSOBJECT;
                nonCollidingActive = false;
                break;
            case FIXED:
                Box2D31Bridge.nSetType(bodyHandle, 0);
                setGravityScaleInternal(0.0f);
                collisionMaskRecord = PhysicsWorld.MASK_PHYSICSOBJECT;
                nonCollidingActive = false;
                break;
            case NONE:
                Box2D31Bridge.nSetType(bodyHandle, 0);
                setGravityScaleInternal(0.0f);
                collisionMaskRecord = PhysicsWorld.MASK_NO_COLLISION;
                nonCollidingActive = true;
                break;
        }
        calculateCircumference();
        applyCollisionBits(categoryMaskRecord, collisionMaskRecord);
    }

    private void setGravityScaleInternal(float scale) {
        gravityScaleRecord = scale;
        if (alive()) {
            world.setBodyGravityScale(bodyHandle, scale);
        }
    }

    private void applyCollisionBits(short categoryBits, short maskBits) {
        categoryMaskRecord = categoryBits;
        if (!alive()) {
            return;
        }
        world.setBodyCollisionBits(bodyHandle, categoryBits, maskBits);
        updateNonCollidingState();
    }

    private void updateNonCollidingState() {
        if (sprite == null) {
            return;
        }
        Object look = sprite.look;
        if (look instanceof PhysicsLook) {
            PhysicsLook physicsLook = (PhysicsLook) look;
            if (physicsLook.isPhysicsObject(this)) {
                physicsLook.setNonColliding(isNonColliding());
            }
        }
    }

    private float[] readTransform() {
        float[] out = new float[3];
        if (alive()) {
            Box2D31Bridge.nGetTransform(bodyHandle, out);
        }
        return out;
    }

    @Override
    public float getDirection() {
        return PhysicsWorldConverter.convertBox2dToNormalAngle(readTransform()[2]);
    }

    @Override
    public void setDirection(float degrees) {
        float[] t = readTransform();
        if (alive()) {
            Box2D31Bridge.nSetTransform(bodyHandle, t[0], t[1],
                    PhysicsWorldConverter.convertNormalToBox2dAngle(degrees));
        }
    }

    @Override
    public float getX() {
        return PhysicsWorldConverter.convertBox2dToNormalCoordinate(readTransform()[0]);
    }

    @Override
    public float getY() {
        return PhysicsWorldConverter.convertBox2dToNormalCoordinate(readTransform()[1]);
    }

    @Override
    public Vector2 getMassCenter() {
        float[] center = new float[2];
        if (alive()) {
            Box2D31Bridge.nGetWorldCenter(bodyHandle, center);
        }
        return new Vector2(center[0], center[1]);
    }

    @Override
    public float getCircumference() {
        return PhysicsWorldConverter.convertBox2dToNormalCoordinate(circumference);
    }

    @Override
    public Vector2 getPosition() {
        float[] t = readTransform();
        return PhysicsWorldConverter.convertBox2dToNormalVector(new Vector2(t[0], t[1]));
    }

    @Override
    public void setX(float x) {
        float[] t = readTransform();
        if (alive()) {
            Box2D31Bridge.nSetTransform(bodyHandle,
                    PhysicsWorldConverter.convertNormalToBox2dCoordinate(x), t[1], t[2]);
        }
    }

    @Override
    public void setY(float y) {
        float[] t = readTransform();
        if (alive()) {
            Box2D31Bridge.nSetTransform(bodyHandle, t[0],
                    PhysicsWorldConverter.convertNormalToBox2dCoordinate(y), t[2]);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        float[] t = readTransform();
        if (alive()) {
            Box2D31Bridge.nSetTransform(bodyHandle,
                    PhysicsWorldConverter.convertNormalToBox2dCoordinate(x),
                    PhysicsWorldConverter.convertNormalToBox2dCoordinate(y), t[2]);
        }
    }

    @Override
    public void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

    @Override
    public float getRotationSpeed() {
        if (!alive()) {
            return 0f;
        }
        return (float) Math.toDegrees(Box2D31Bridge.nGetAngularVelocity(bodyHandle));
    }

    @Override
    public void setRotationSpeed(float degreesPerSecond) {
        if (alive()) {
            Box2D31Bridge.nSetAngularVelocity(bodyHandle,
                    (float) Math.toRadians(degreesPerSecond));
        }
    }

    @Override
    public Vector2 getVelocity() {
        float[] out = new float[2];
        if (alive()) {
            Box2D31Bridge.nGetLinearVelocity(bodyHandle, out);
        }
        return PhysicsWorldConverter.convertBox2dToNormalVector(new Vector2(out[0], out[1]));
    }

    @Override
    public void setVelocity(float x, float y) {
        if (alive()) {
            Box2D31Bridge.nSetLinearVelocity(bodyHandle,
                    PhysicsWorldConverter.convertNormalToBox2dCoordinate(x),
                    PhysicsWorldConverter.convertNormalToBox2dCoordinate(y));
        }
    }

    @Override
    public void setVelocity(Vector2 velocity) {
        setVelocity(velocity.x, velocity.y);
    }

    @Override
    public void setAngularVelocity(float radiansPerSecond) {
        if (alive()) {
            Box2D31Bridge.nSetAngularVelocity(bodyHandle, radiansPerSecond);
        }
    }

    @Override
    public void setBullet(boolean bullet) {
        if (alive()) {
            world.setBodyBullet(bodyHandle, bullet);
        }
    }

    @Override
    public void setFixedRotation(boolean fixed) {
        if (alive()) {
            world.setBodyFixedRotation(bodyHandle, fixed);
        }
    }

    @Override
    public void setGravityScale(float scale) {
        setGravityScaleInternal(scale);
    }

    @Override
    public float getGravityScale() {
        return gravityScaleRecord;
    }

    @Override
    public void setSensor(boolean sensor) {
        if (this.sensor == sensor) {
            return;
        }
        this.sensor = sensor;
        world.noteSensorBody(bodyHandle, sensor);
        setShapeDescs(new ArrayList<>(shapeDescs), density, friction, restitution,
                categoryMaskRecord, sensor);
    }

    @Override
    public float getMass() {
        return mass;
    }

    @Override
    public float getBounceFactor() {
        return restitution;
    }

    @Override
    public void setMass(float mass) {
        this.mass = Math.max(mass, PhysicsObject.MIN_MASS);
        if (type == Type.FIXED || type == Type.NONE) {
            return;
        }
        float area = 0f;
        for (ShapeDesc desc : shapeDescs) {
            area += desc.area();
        }
        if (area <= 0f) {
            return;
        }
        setDensity(this.mass / area);
    }

    @Override
    public void setDensity(float density) {
        if (density < PhysicsObject.MIN_DENSITY) {
            density = PhysicsObject.MIN_DENSITY;
        }
        this.density = density;
        if (alive()) {
            Box2D31Bridge.nSetShapesDensity(bodyHandle, density);
        }
    }

    @Override
    public float getFriction() {
        return friction;
    }

    @Override
    public void setFriction(float friction) {
        if (friction < PhysicsObject.MIN_FRICTION) {
            friction = PhysicsObject.MIN_FRICTION;
        }
        if (friction > PhysicsObject.MAX_FRICTION) {
            friction = PhysicsObject.MAX_FRICTION;
        }
        this.friction = friction;
        if (alive()) {
            Box2D31Bridge.nSetShapesSurface(bodyHandle, friction, -1f);
        }
    }

    @Override
    public void setBounceFactor(float bounceFactor) {
        if (bounceFactor < PhysicsObject.MIN_BOUNCE_FACTOR) {
            bounceFactor = PhysicsObject.MIN_BOUNCE_FACTOR;
        }
        this.restitution = bounceFactor;
        if (alive()) {
            Box2D31Bridge.nSetShapesSurface(bodyHandle, -1f, bounceFactor);
        }
    }

    @Override
    public void setIfOnEdgeBounce(boolean bounce, Sprite sprite) {
        if (ifOnEdgeBounce == bounce) {
            return;
        }
        ifOnEdgeBounce = bounce;
        short maskBits;
        if (bounce) {
            maskBits = PhysicsWorld.MASK_TO_BOUNCE;
            world.setBodyUserData(bodyHandle, sprite);
        } else {
            maskBits = PhysicsWorld.MASK_PHYSICSOBJECT;
        }
        applyCollisionBits(categoryMaskRecord, maskBits);
    }

    @Override
    public void getBoundaryBox(Vector2 lowerLeft, Vector2 upperRight) {
        float[] aabb = new float[4];
        if (alive()) {
            Box2D31Bridge.nGetAABB(bodyHandle, aabb);
        }
        lowerLeft.x = PhysicsWorldConverter.convertBox2dToNormalCoordinate(aabb[0]);
        lowerLeft.y = PhysicsWorldConverter.convertBox2dToNormalCoordinate(aabb[1]);
        upperRight.x = PhysicsWorldConverter.convertBox2dToNormalCoordinate(aabb[2]);
        upperRight.y = PhysicsWorldConverter.convertBox2dToNormalCoordinate(aabb[3]);
    }

    @Override
    public Vector2 getBoundaryBoxDimensions() {
        float[] aabb = new float[4];
        if (alive()) {
            Box2D31Bridge.nGetAABB(bodyHandle, aabb);
        }
        float width = PhysicsWorldConverter.convertBox2dToNormalCoordinate(
                Math.abs(aabb[2] - aabb[0])) + 1.0f;
        float height = PhysicsWorldConverter.convertBox2dToNormalCoordinate(
                Math.abs(aabb[3] - aabb[1])) + 1.0f;
        return new Vector2(width, height);
    }

    @Override
    public void setLinearDamping(float damping) {
        if (damping < 0) {
            damping = 0;
        }
        if (alive()) {
            world.setBodyLinearDamping(bodyHandle, damping);
        }
    }

    @Override
    public void setAngularDamping(float damping) {
        if (damping < 0) {
            damping = 0;
        }
        if (alive()) {
            world.setBodyAngularDamping(bodyHandle, damping);
        }
    }

    @Override
    public void activateHangup() {
        velocityRecord.set(getVelocity());
        rotationSpeedRecord = getRotationSpeed();
        gravityScaleRecord = getGravityScale();
        setGravityScale(0);
        setVelocity(0, 0);
        setRotationSpeed(0);
    }

    @Override
    public void deactivateHangup(boolean record) {
        if (record) {
            setGravityScale(gravityScaleRecord);
            setVelocity(velocityRecord.x, velocityRecord.y);
            setRotationSpeed(rotationSpeedRecord);
        } else {
            setGravityScale(gravityScaleRecord);
        }
    }

    @Override
    public void activateNonColliding(boolean updateState) {
        nonCollidingActive = true;
        applyCollisionBits(categoryMaskRecord, PhysicsWorld.MASK_NO_COLLISION);
        if (!updateState) {
            return;
        }
        updateNonCollidingState();
    }

    @Override
    public void deactivateNonColliding(boolean record, boolean updateState) {
        if (record) {
            nonCollidingActive = false;
            applyCollisionBits(categoryMaskRecord, collisionMaskRecord);
            if (updateState) {
                updateNonCollidingState();
            }
        }
    }

    @Override
    public void setCollisionMaskRecord(short mask) {
        collisionMaskRecord = mask;
        nonCollidingActive = mask == PhysicsWorld.MASK_NO_COLLISION;
    }

    @Override
    public void activateFixed() {
        savedType = getType();
        setType(Type.FIXED);
    }

    @Override
    public void deactivateFixed(boolean record) {
        if (record) {
            setType(savedType);
        }
    }

    @Override
    public boolean isNonColliding() {
        return nonCollidingActive;
    }
}
