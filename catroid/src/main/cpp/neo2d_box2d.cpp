#include <jni.h>

#include <box2d/box2d.h>
#include <box2d/math_functions.h>

#include <cmath>
#include <cstdint>
#include <cstring>
#include <mutex>
#include <set>
#include <vector>

namespace {

constexpr int kMaxContactPairs = 1024;
constexpr int kMaxRayHits = 32;
constexpr int kMaxBodyShapes = 64;

std::mutex gWorldsMutex;
std::set<uint32_t> gLiveWorlds;

bool IsLiveWorld(b2WorldId worldId) {
    if (B2_IS_NULL(worldId)) {
        return false;
    }
    uint32_t key = b2StoreWorldId(worldId);
    std::lock_guard<std::mutex> lock(gWorldsMutex);
    return gLiveWorlds.find(key) != gLiveWorlds.end();
}

b2WorldId LoadWorld(jlong handle) {
    if (handle <= 0 || handle > 0xFFFFFFFFLL) {
        return b2_nullWorldId;
    }
    b2WorldId worldId = b2LoadWorldId(static_cast<uint32_t>(handle));
    return IsLiveWorld(worldId) ? worldId : b2_nullWorldId;
}

b2BodyId LoadBody(jlong handle) {
    if (handle == 0) {
        return b2_nullBodyId;
    }
    b2BodyId bodyId = b2LoadBodyId(static_cast<uint64_t>(handle));
    if (B2_IS_NULL(bodyId) || !b2Body_IsValid(bodyId)) {
        return b2_nullBodyId;
    }
    return bodyId;
}

b2JointId LoadJoint(jlong handle) {
    if (handle == 0) {
        return b2_nullJointId;
    }
    b2JointId jointId = b2LoadJointId(static_cast<uint64_t>(handle));
    if (B2_IS_NULL(jointId) || !b2Joint_IsValid(jointId)) {
        return b2_nullJointId;
    }
    return jointId;
}

float Finite(float value, float fallback) {
    return std::isfinite(value) ? value : fallback;
}

b2ShapeDef DefaultShapeDef(float density, float friction, float restitution,
        uint64_t categoryBits, uint64_t maskBits, bool isSensor) {
    b2ShapeDef def = b2DefaultShapeDef();
    def.density = density > 0.0f && std::isfinite(density) ? density : 1.0f;
    def.material.friction = friction >= 0.0f && std::isfinite(friction) ? friction : 0.2f;
    def.material.restitution = restitution >= 0.0f && std::isfinite(restitution)
            ? restitution : 0.0f;
    def.filter.categoryBits = categoryBits;
    def.filter.maskBits = maskBits;
    def.isSensor = isSensor;
    def.enableContactEvents = true;
    return def;
}

void DestroyAllShapes(b2BodyId bodyId) {
    b2ShapeId shapes[kMaxBodyShapes];
    int count = b2Body_GetShapes(bodyId, shapes, kMaxBodyShapes);
    for (int i = 0; i < count; i++) {
        if (b2Shape_IsValid(shapes[i])) {
            b2DestroyShape(shapes[i], false);
        }
    }
}

struct RayHit {
    uint64_t bodyBits;
    float fraction;
    float px;
    float py;
    float nx;
    float ny;
};

struct RayCollect {
    RayHit hits[kMaxRayHits];
    int count = 0;
};

float RayCollectFcn(b2ShapeId shapeId, b2Vec2 point, b2Vec2 normal, float fraction,
        void* context) {
    RayCollect* collect = static_cast<RayCollect*>(context);
    if (collect->count < kMaxRayHits && b2Shape_IsValid(shapeId)) {
        b2BodyId bodyId = b2Shape_GetBody(shapeId);
        if (B2_IS_NON_NULL(bodyId)) {
            RayHit& hit = collect->hits[collect->count++];
            hit.bodyBits = b2StoreBodyId(bodyId);
            hit.fraction = fraction;
            hit.px = point.x;
            hit.py = point.y;
            hit.nx = normal.x;
            hit.ny = normal.y;
        }
    }
    return 1.0f;
}

}  // namespace

extern "C" JNIEXPORT jlong JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nCreateWorld(
        JNIEnv*, jclass, jfloat gravityX, jfloat gravityY) {
    try {
        b2WorldDef def = b2DefaultWorldDef();
        def.gravity = {Finite(gravityX, 0.0f), Finite(gravityY, -10.0f)};
        b2WorldId worldId = b2CreateWorld(&def);
        if (B2_IS_NULL(worldId)) {
            return 0;
        }
        uint32_t key = b2StoreWorldId(worldId);
        std::lock_guard<std::mutex> lock(gWorldsMutex);
        gLiveWorlds.insert(key);
        return static_cast<jlong>(key);
    } catch (...) {
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nDestroyWorld(
        JNIEnv*, jclass, jlong worldHandle) {
    b2WorldId worldId = LoadWorld(worldHandle);
    if (B2_IS_NULL(worldId)) {
        return;
    }
    uint32_t key = b2StoreWorldId(worldId);
    {
        std::lock_guard<std::mutex> lock(gWorldsMutex);
        gLiveWorlds.erase(key);
    }
    b2DestroyWorld(worldId);
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nStep(
        JNIEnv*, jclass, jlong worldHandle, jfloat deltaSeconds, jint subSteps) {
    b2WorldId worldId = LoadWorld(worldHandle);
    if (B2_IS_NULL(worldId) || !std::isfinite(deltaSeconds) || deltaSeconds <= 0.0f) {
        return;
    }
    int steps = subSteps < 1 ? 1 : (subSteps > 8 ? 8 : subSteps);
    b2World_Step(worldId, deltaSeconds, steps);
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetGravity(
        JNIEnv*, jclass, jlong worldHandle, jfloat x, jfloat y) {
    b2WorldId worldId = LoadWorld(worldHandle);
    if (!B2_IS_NULL(worldId)) {
        b2World_SetGravity(worldId, {Finite(x, 0.0f), Finite(y, -10.0f)});
    }
}

extern "C" JNIEXPORT jlong JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nCreateBody(
        JNIEnv*, jclass, jlong worldHandle, jint type, jfloat x, jfloat y,
        jfloat angleRad, jboolean fixedRotation, jboolean bullet, jfloat gravityScale,
        jfloat linearDamping, jfloat angularDamping) {
    b2WorldId worldId = LoadWorld(worldHandle);
    if (B2_IS_NULL(worldId)) {
        return 0;
    }
    b2BodyDef def = b2DefaultBodyDef();
    def.type = type == 0 ? b2_staticBody : (type == 1 ? b2_kinematicBody : b2_dynamicBody);
    def.position = {Finite(x, 0.0f), Finite(y, 0.0f)};
    def.rotation = b2MakeRot(std::isfinite(angleRad) ? angleRad : 0.0f);
    def.fixedRotation = fixedRotation == JNI_TRUE;
    def.isBullet = bullet == JNI_TRUE;
    def.gravityScale = std::isfinite(gravityScale) ? gravityScale : 1.0f;
    def.linearDamping = linearDamping >= 0.0f && std::isfinite(linearDamping)
            ? linearDamping : 0.0f;
    def.angularDamping = angularDamping >= 0.0f && std::isfinite(angularDamping)
            ? angularDamping : 0.0f;
    b2BodyId bodyId = b2CreateBody(worldId, &def);
    if (B2_IS_NULL(bodyId)) {
        return 0;
    }
    return static_cast<jlong>(b2StoreBodyId(bodyId));
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nDestroyBody(
        JNIEnv*, jclass, jlong bodyHandle) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        b2DestroyBody(bodyId);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetTransform(
        JNIEnv*, jclass, jlong bodyHandle, jfloat x, jfloat y, jfloat angleRad) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        b2Body_SetTransform(bodyId, {Finite(x, 0.0f), Finite(y, 0.0f)},
                b2MakeRot(std::isfinite(angleRad) ? angleRad : 0.0f));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nGetTransform(
        JNIEnv* env, jclass, jlong bodyHandle, jfloatArray outputArray) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId) || outputArray == nullptr
            || env->GetArrayLength(outputArray) < 3) {
        return;
    }
    b2Vec2 position = b2Body_GetPosition(bodyId);
    b2Rot rotation = b2Body_GetRotation(bodyId);
    jfloat* output = env->GetFloatArrayElements(outputArray, nullptr);
    if (output != nullptr) {
        output[0] = position.x;
        output[1] = position.y;
        output[2] = b2Rot_GetAngle(rotation);
        env->ReleaseFloatArrayElements(outputArray, output, 0);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetLinearVelocity(
        JNIEnv*, jclass, jlong bodyHandle, jfloat x, jfloat y) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        b2Body_SetLinearVelocity(bodyId, {Finite(x, 0.0f), Finite(y, 0.0f)});
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nGetLinearVelocity(
        JNIEnv* env, jclass, jlong bodyHandle, jfloatArray outputArray) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId) || outputArray == nullptr
            || env->GetArrayLength(outputArray) < 2) {
        return;
    }
    b2Vec2 velocity = b2Body_GetLinearVelocity(bodyId);
    jfloat* output = env->GetFloatArrayElements(outputArray, nullptr);
    if (output != nullptr) {
        output[0] = velocity.x;
        output[1] = velocity.y;
        env->ReleaseFloatArrayElements(outputArray, output, 0);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetAngularVelocity(
        JNIEnv*, jclass, jlong bodyHandle, jfloat velocity) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId) && std::isfinite(velocity)) {
        b2Body_SetAngularVelocity(bodyId, velocity);
    }
}

extern "C" JNIEXPORT jfloat JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nGetAngularVelocity(
        JNIEnv*, jclass, jlong bodyHandle) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId)) {
        return 0.0f;
    }
    return b2Body_GetAngularVelocity(bodyId);
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetType(
        JNIEnv*, jclass, jlong bodyHandle, jint type) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        b2Body_SetType(bodyId,
                type == 0 ? b2_staticBody : (type == 1 ? b2_kinematicBody : b2_dynamicBody));
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nApplyForce(
        JNIEnv*, jclass, jlong bodyHandle, jfloat fx, jfloat fy, jfloat px, jfloat py) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        b2Body_ApplyForce(bodyId, {Finite(fx, 0.0f), Finite(fy, 0.0f)},
                {Finite(px, 0.0f), Finite(py, 0.0f)}, true);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nApplyImpulse(
        JNIEnv*, jclass, jlong bodyHandle, jfloat ix, jfloat iy, jfloat px, jfloat py) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        b2Body_ApplyLinearImpulse(bodyId, {Finite(ix, 0.0f), Finite(iy, 0.0f)},
                {Finite(px, 0.0f), Finite(py, 0.0f)}, true);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nApplyTorque(
        JNIEnv*, jclass, jlong bodyHandle, jfloat torque) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId) && std::isfinite(torque)) {
        b2Body_ApplyTorque(bodyId, torque, true);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nApplyAngularImpulse(
        JNIEnv*, jclass, jlong bodyHandle, jfloat impulse) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId) && std::isfinite(impulse)) {
        b2Body_ApplyAngularImpulse(bodyId, impulse, true);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetGravityScale(
        JNIEnv*, jclass, jlong bodyHandle, jfloat scale) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId) && std::isfinite(scale)) {
        b2Body_SetGravityScale(bodyId, scale);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetFixedRotation(
        JNIEnv*, jclass, jlong bodyHandle, jboolean fixed) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        b2Body_SetFixedRotation(bodyId, fixed == JNI_TRUE);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetBullet(
        JNIEnv*, jclass, jlong bodyHandle, jboolean bullet) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        b2Body_SetBullet(bodyId, bullet == JNI_TRUE);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetLinearDamping(
        JNIEnv*, jclass, jlong bodyHandle, jfloat damping) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId) && std::isfinite(damping) && damping >= 0.0f) {
        b2Body_SetLinearDamping(bodyId, damping);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetAngularDamping(
        JNIEnv*, jclass, jlong bodyHandle, jfloat damping) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId) && std::isfinite(damping) && damping >= 0.0f) {
        b2Body_SetAngularDamping(bodyId, damping);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetBodyFilter(
        JNIEnv*, jclass, jlong bodyHandle, jlong categoryBits, jlong maskBits) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId)) {
        return;
    }
    b2ShapeId shapes[kMaxBodyShapes];
    int count = b2Body_GetShapes(bodyId, shapes, kMaxBodyShapes);
    for (int i = 0; i < count; i++) {
        if (!b2Shape_IsValid(shapes[i])) {
            continue;
        }
        b2Filter filter = b2Shape_GetFilter(shapes[i]);
        filter.categoryBits = static_cast<uint64_t>(categoryBits);
        filter.maskBits = static_cast<uint64_t>(maskBits);
        b2Shape_SetFilter(shapes[i], filter);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nGetWorldCenter(
        JNIEnv* env, jclass, jlong bodyHandle, jfloatArray outputArray) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId) || outputArray == nullptr
            || env->GetArrayLength(outputArray) < 2) {
        return;
    }
    b2Vec2 center = b2Body_GetWorldCenterOfMass(bodyId);
    jfloat* output = env->GetFloatArrayElements(outputArray, nullptr);
    if (output != nullptr) {
        output[0] = center.x;
        output[1] = center.y;
        env->ReleaseFloatArrayElements(outputArray, output, 0);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nClearShapes(
        JNIEnv*, jclass, jlong bodyHandle) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (!B2_IS_NULL(bodyId)) {
        DestroyAllShapes(bodyId);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nAddPolygon(
        JNIEnv* env, jclass, jlong bodyHandle, jfloat density, jfloat friction,
        jfloat restitution, jlong categoryBits, jlong maskBits, jboolean isSensor,
        jfloatArray verticesArray) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId) || verticesArray == nullptr) {
        return;
    }
    jsize length = env->GetArrayLength(verticesArray);
    if (length < 6 || length % 2 != 0) {
        return;
    }
    int pointCount = length / 2;
    if (pointCount > B2_MAX_POLYGON_VERTICES) {
        pointCount = B2_MAX_POLYGON_VERTICES;
    }
    jfloat* vertices = env->GetFloatArrayElements(verticesArray, nullptr);
    if (vertices == nullptr) {
        return;
    }
    b2Vec2 points[B2_MAX_POLYGON_VERTICES];
    for (int i = 0; i < pointCount; i++) {
        points[i] = {Finite(vertices[2 * i], 0.0f), Finite(vertices[2 * i + 1], 0.0f)};
    }
    env->ReleaseFloatArrayElements(verticesArray, vertices, JNI_ABORT);
    b2Hull hull = b2ComputeHull(points, pointCount);
    if (hull.count < 3) {
        return;
    }
    b2Polygon polygon = b2MakePolygon(&hull, 0.0f);
    b2ShapeDef def = DefaultShapeDef(density, friction, restitution,
            static_cast<uint64_t>(categoryBits), static_cast<uint64_t>(maskBits),
            isSensor == JNI_TRUE);
    b2CreatePolygonShape(bodyId, &def, &polygon);
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nAddCircle(
        JNIEnv*, jclass, jlong bodyHandle, jfloat density, jfloat friction,
        jfloat restitution, jlong categoryBits, jlong maskBits, jboolean isSensor,
        jfloat cx, jfloat cy, jfloat radius) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId) || !std::isfinite(radius) || radius <= 0.0f) {
        return;
    }
    b2ShapeDef def = DefaultShapeDef(density, friction, restitution,
            static_cast<uint64_t>(categoryBits), static_cast<uint64_t>(maskBits),
            isSensor == JNI_TRUE);
    b2Circle circle = {{Finite(cx, 0.0f), Finite(cy, 0.0f)}, radius};
    b2CreateCircleShape(bodyId, &def, &circle);
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nAddSegment(
        JNIEnv*, jclass, jlong bodyHandle, jfloat density, jfloat friction,
        jfloat restitution, jlong categoryBits, jlong maskBits, jboolean isSensor,
        jfloat x1, jfloat y1, jfloat x2, jfloat y2) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId)) {
        return;
    }
    b2ShapeDef def = DefaultShapeDef(density, friction, restitution,
            static_cast<uint64_t>(categoryBits), static_cast<uint64_t>(maskBits),
            isSensor == JNI_TRUE);
    b2Segment segment = {{Finite(x1, 0.0f), Finite(y1, 0.0f)},
            {Finite(x2, 0.0f), Finite(y2, 0.0f)}};
    b2CreateSegmentShape(bodyId, &def, &segment);
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetShapesSurface(
        JNIEnv*, jclass, jlong bodyHandle, jfloat friction, jfloat restitution) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId)) {
        return;
    }
    b2ShapeId shapes[kMaxBodyShapes];
    int count = b2Body_GetShapes(bodyId, shapes, kMaxBodyShapes);
    for (int i = 0; i < count; i++) {
        if (!b2Shape_IsValid(shapes[i])) {
            continue;
        }
        if (std::isfinite(friction) && friction >= 0.0f) {
            b2Shape_SetFriction(shapes[i], friction);
        }
        if (std::isfinite(restitution) && restitution >= 0.0f) {
            b2Shape_SetRestitution(shapes[i], restitution);
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nSetShapesDensity(
        JNIEnv*, jclass, jlong bodyHandle, jfloat density) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId) || !std::isfinite(density) || density <= 0.0f) {
        return;
    }
    b2ShapeId shapes[kMaxBodyShapes];
    int count = b2Body_GetShapes(bodyId, shapes, kMaxBodyShapes);
    for (int i = 0; i < count; i++) {
        if (b2Shape_IsValid(shapes[i])) {
            b2Shape_SetDensity(shapes[i], density, true);
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nGetAABB(
        JNIEnv* env, jclass, jlong bodyHandle, jfloatArray outputArray) {
    b2BodyId bodyId = LoadBody(bodyHandle);
    if (B2_IS_NULL(bodyId) || outputArray == nullptr
            || env->GetArrayLength(outputArray) < 4) {
        return;
    }
    b2AABB aabb = b2Body_ComputeAABB(bodyId);
    jfloat* output = env->GetFloatArrayElements(outputArray, nullptr);
    if (output != nullptr) {
        output[0] = aabb.lowerBound.x;
        output[1] = aabb.lowerBound.y;
        output[2] = aabb.upperBound.x;
        output[3] = aabb.upperBound.y;
        env->ReleaseFloatArrayElements(outputArray, output, 0);
    }
}

extern "C" JNIEXPORT jlongArray JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nPollContactEvents(
        JNIEnv* env, jclass, jlong worldHandle) {
    std::vector<uint64_t> pairs;
    b2WorldId worldId = LoadWorld(worldHandle);
    if (!B2_IS_NULL(worldId)) {
        b2ContactEvents contactEvents = b2World_GetContactEvents(worldId);
        b2SensorEvents sensorEvents = b2World_GetSensorEvents(worldId);
        int total = contactEvents.beginCount + contactEvents.endCount
                + sensorEvents.beginCount + sensorEvents.endCount;
        if (total > kMaxContactPairs) {
            total = kMaxContactPairs;
        }
        pairs.reserve(static_cast<size_t>(total) * 2 + 2);
        pairs.push_back(static_cast<uint64_t>(contactEvents.beginCount
                + sensorEvents.beginCount));
        pairs.push_back(static_cast<uint64_t>(contactEvents.endCount
                + sensorEvents.endCount));
        int emitted = 0;
        auto emit = [&](b2ShapeId shapeA, b2ShapeId shapeB) {
            if (emitted >= kMaxContactPairs || !b2Shape_IsValid(shapeA)
                    || !b2Shape_IsValid(shapeB)) {
                return;
            }
            b2BodyId bodyA = b2Shape_GetBody(shapeA);
            b2BodyId bodyB = b2Shape_GetBody(shapeB);
            if (B2_IS_NULL(bodyA) || B2_IS_NULL(bodyB)) {
                return;
            }
            pairs.push_back(b2StoreBodyId(bodyA));
            pairs.push_back(b2StoreBodyId(bodyB));
            emitted++;
        };
        for (int i = 0; i < contactEvents.beginCount && emitted < kMaxContactPairs; i++) {
            emit(contactEvents.beginEvents[i].shapeIdA, contactEvents.beginEvents[i].shapeIdB);
        }
        for (int i = 0; i < sensorEvents.beginCount && emitted < kMaxContactPairs; i++) {
            emit(sensorEvents.beginEvents[i].sensorShapeId,
                    sensorEvents.beginEvents[i].visitorShapeId);
        }
        for (int i = 0; i < contactEvents.endCount && emitted < kMaxContactPairs; i++) {
            emit(contactEvents.endEvents[i].shapeIdA, contactEvents.endEvents[i].shapeIdB);
        }
        for (int i = 0; i < sensorEvents.endCount && emitted < kMaxContactPairs; i++) {
            emit(sensorEvents.endEvents[i].sensorShapeId,
                    sensorEvents.endEvents[i].visitorShapeId);
        }
    }
    jlongArray result = env->NewLongArray(static_cast<jsize>(pairs.size()));
    if (result != nullptr && !pairs.empty()) {
        env->SetLongArrayRegion(result, 0, static_cast<jsize>(pairs.size()),
                reinterpret_cast<const jlong*>(pairs.data()));
    }
    return result;
}

extern "C" JNIEXPORT jlongArray JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nCastRay(
        JNIEnv* env, jclass, jlong worldHandle, jfloat ox, jfloat oy, jfloat tx, jfloat ty) {
    std::vector<uint64_t> output;
    output.push_back(0);
    b2WorldId worldId = LoadWorld(worldHandle);
    if (!B2_IS_NULL(worldId)) {
        b2QueryFilter filter = b2DefaultQueryFilter();
        RayCollect collect;
        b2World_CastRay(worldId, {Finite(ox, 0.0f), Finite(oy, 0.0f)},
                {Finite(tx, 0.0f), Finite(ty, 0.0f)}, filter, RayCollectFcn, &collect);
        output[0] = static_cast<uint64_t>(collect.count);
        for (int i = 0; i < collect.count; i++) {
            const RayHit& hit = collect.hits[i];
            output.push_back(hit.bodyBits);
            uint32_t fractionBits;
            std::memcpy(&fractionBits, &hit.fraction, sizeof(float));
            output.push_back(static_cast<uint64_t>(fractionBits));
            uint32_t pxBits;
            std::memcpy(&pxBits, &hit.px, sizeof(float));
            output.push_back(static_cast<uint64_t>(pxBits));
            uint32_t pyBits;
            std::memcpy(&pyBits, &hit.py, sizeof(float));
            output.push_back(static_cast<uint64_t>(pyBits));
            uint32_t nxBits;
            std::memcpy(&nxBits, &hit.nx, sizeof(float));
            output.push_back(static_cast<uint64_t>(nxBits));
            uint32_t nyBits;
            std::memcpy(&nyBits, &hit.ny, sizeof(float));
            output.push_back(static_cast<uint64_t>(nyBits));
        }
    }
    jlongArray result = env->NewLongArray(static_cast<jsize>(output.size()));
    if (result != nullptr && !output.empty()) {
        env->SetLongArrayRegion(result, 0, static_cast<jsize>(output.size()),
                reinterpret_cast<const jlong*>(output.data()));
    }
    return result;
}

extern "C" JNIEXPORT jlong JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nCreateDistanceJoint(
        JNIEnv*, jclass, jlong worldHandle, jlong bodyA, jlong bodyB,
        jfloat anchorAx, jfloat anchorAy, jfloat anchorBx, jfloat anchorBy,
        jfloat length, jfloat hertz, jfloat dampingRatio, jboolean collideConnected) {
    b2WorldId worldId = LoadWorld(worldHandle);
    b2BodyId idA = LoadBody(bodyA);
    b2BodyId idB = LoadBody(bodyB);
    if (B2_IS_NULL(worldId) || B2_IS_NULL(idA) || B2_IS_NULL(idB)) {
        return 0;
    }
    b2DistanceJointDef def = b2DefaultDistanceJointDef();
    def.bodyIdA = idA;
    def.bodyIdB = idB;
    def.localAnchorA = {Finite(anchorAx, 0.0f), Finite(anchorAy, 0.0f)};
    def.localAnchorB = {Finite(anchorBx, 0.0f), Finite(anchorBy, 0.0f)};
    def.length = std::isfinite(length) && length > 0.0f ? length : 1.0f;
    def.enableSpring = std::isfinite(hertz) && hertz > 0.0f;
    def.hertz = def.enableSpring ? hertz : 0.0f;
    def.dampingRatio = std::isfinite(dampingRatio) && dampingRatio >= 0.0f ? dampingRatio : 0.0f;
    def.collideConnected = collideConnected == JNI_TRUE;
    b2JointId jointId = b2CreateDistanceJoint(worldId, &def);
    if (B2_IS_NULL(jointId)) {
        return 0;
    }
    return static_cast<jlong>(b2StoreJointId(jointId));
}

extern "C" JNIEXPORT jlong JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nCreateRevoluteJoint(
        JNIEnv*, jclass, jlong worldHandle, jlong bodyA, jlong bodyB,
        jfloat anchorAx, jfloat anchorAy, jfloat anchorBx, jfloat anchorBy,
        jboolean collideConnected) {
    b2WorldId worldId = LoadWorld(worldHandle);
    b2BodyId idA = LoadBody(bodyA);
    b2BodyId idB = LoadBody(bodyB);
    if (B2_IS_NULL(worldId) || B2_IS_NULL(idA) || B2_IS_NULL(idB)) {
        return 0;
    }
    b2RevoluteJointDef def = b2DefaultRevoluteJointDef();
    def.bodyIdA = idA;
    def.bodyIdB = idB;
    def.localAnchorA = {Finite(anchorAx, 0.0f), Finite(anchorAy, 0.0f)};
    def.localAnchorB = {Finite(anchorBx, 0.0f), Finite(anchorBy, 0.0f)};
    def.collideConnected = collideConnected == JNI_TRUE;
    b2JointId jointId = b2CreateRevoluteJoint(worldId, &def);
    if (B2_IS_NULL(jointId)) {
        return 0;
    }
    return static_cast<jlong>(b2StoreJointId(jointId));
}

extern "C" JNIEXPORT jlong JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nCreatePrismaticJoint(
        JNIEnv*, jclass, jlong worldHandle, jlong bodyA, jlong bodyB,
        jfloat anchorAx, jfloat anchorAy, jfloat anchorBx, jfloat anchorBy,
        jfloat axisX, jfloat axisY, jboolean collideConnected) {
    b2WorldId worldId = LoadWorld(worldHandle);
    b2BodyId idA = LoadBody(bodyA);
    b2BodyId idB = LoadBody(bodyB);
    if (B2_IS_NULL(worldId) || B2_IS_NULL(idA) || B2_IS_NULL(idB)) {
        return 0;
    }
    b2PrismaticJointDef def = b2DefaultPrismaticJointDef();
    def.bodyIdA = idA;
    def.bodyIdB = idB;
    def.localAnchorA = {Finite(anchorAx, 0.0f), Finite(anchorAy, 0.0f)};
    def.localAnchorB = {Finite(anchorBx, 0.0f), Finite(anchorBy, 0.0f)};
    float axisLength = std::sqrt(axisX * axisX + axisY * axisY);
    if (axisLength > 1e-8f && std::isfinite(axisLength)) {
        def.localAxisA = {axisX / axisLength, axisY / axisLength};
    } else {
        def.localAxisA = {1.0f, 0.0f};
    }
    def.collideConnected = collideConnected == JNI_TRUE;
    b2JointId jointId = b2CreatePrismaticJoint(worldId, &def);
    if (B2_IS_NULL(jointId)) {
        return 0;
    }
    return static_cast<jlong>(b2StoreJointId(jointId));
}

extern "C" JNIEXPORT jlong JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nCreateWeldJoint(
        JNIEnv*, jclass, jlong worldHandle, jlong bodyA, jlong bodyB,
        jfloat anchorAx, jfloat anchorAy, jfloat anchorBx, jfloat anchorBy,
        jboolean collideConnected) {
    b2WorldId worldId = LoadWorld(worldHandle);
    b2BodyId idA = LoadBody(bodyA);
    b2BodyId idB = LoadBody(bodyB);
    if (B2_IS_NULL(worldId) || B2_IS_NULL(idA) || B2_IS_NULL(idB)) {
        return 0;
    }
    b2WeldJointDef def = b2DefaultWeldJointDef();
    def.bodyIdA = idA;
    def.bodyIdB = idB;
    def.localAnchorA = {Finite(anchorAx, 0.0f), Finite(anchorAy, 0.0f)};
    def.localAnchorB = {Finite(anchorBx, 0.0f), Finite(anchorBy, 0.0f)};
    def.collideConnected = collideConnected == JNI_TRUE;
    b2JointId jointId = b2CreateWeldJoint(worldId, &def);
    if (B2_IS_NULL(jointId)) {
        return 0;
    }
    return static_cast<jlong>(b2StoreJointId(jointId));
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_physics_v2_Box2D31Bridge_nDestroyJoint(
        JNIEnv*, jclass, jlong jointHandle) {
    b2JointId jointId = LoadJoint(jointHandle);
    if (!B2_IS_NULL(jointId)) {
        b2DestroyJoint(jointId);
    }
}
