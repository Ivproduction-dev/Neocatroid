#include <jni.h>

#include <Jolt/Jolt.h>
#include <Jolt/Core/Factory.h>
#include <Jolt/Core/JobSystemThreadPool.h>
#include <Jolt/Core/TempAllocator.h>
#include <Jolt/Physics/Body/BodyCreationSettings.h>
#include <Jolt/Physics/Collision/BroadPhase/BroadPhaseLayerInterfaceTable.h>
#include <Jolt/Physics/Collision/BroadPhase/ObjectVsBroadPhaseLayerFilterTable.h>
#include <Jolt/Physics/Collision/ContactListener.h>
#include <Jolt/Physics/Collision/ObjectLayerPairFilterTable.h>
#include <Jolt/Physics/Collision/Shape/BoxShape.h>
#include <Jolt/Physics/Collision/Shape/CapsuleShape.h>
#include <Jolt/Physics/Collision/Shape/CylinderShape.h>
#include <Jolt/Physics/Collision/Shape/SphereShape.h>
#include <Jolt/Physics/PhysicsSystem.h>
#include <Jolt/RegisterTypes.h>

#include <algorithm>
#include <cmath>
#include <cstdint>
#include <limits>
#include <memory>
#include <mutex>
#include <set>
#include <thread>
#include <unordered_map>
#include <vector>

namespace {

constexpr uint32_t kMaxBodies = 5000;
constexpr uint32_t kMaxBodyPairs = 65536;
constexpr uint32_t kMaxContacts = 20480;
constexpr uint32_t kMovingLayer = 0;
constexpr uint32_t kStaticLayer = 1;
constexpr float kFixedStep = 1.0f / 60.0f;
constexpr int kMaxSteps = 5;
constexpr float kPoseEpsilon = 0.00001f;

std::shared_ptr<JPH::JobSystemThreadPool> gJobSystem;

void InitializeJolt(int workers) {
    static std::once_flag once;
    std::call_once(once, [workers]() {
        JPH::RegisterDefaultAllocator();
        JPH::Factory::sInstance = new JPH::Factory();
        JPH::RegisterTypes();
        int threadCount = std::max(1, std::min(3, workers));
        gJobSystem = std::make_shared<JPH::JobSystemThreadPool>(
                JPH::cMaxPhysicsJobs, JPH::cMaxPhysicsBarriers, threadCount);
    });
}

struct BodyRecord {
    JPH::EMotionType motionType;
    JPH::RVec3 targetPosition;
    JPH::Quat targetRotation = JPH::Quat::sIdentity();
    JPH::RVec3 lastPosition;
    JPH::Quat lastRotation = JPH::Quat::sIdentity();
};

class ContactTracker : public JPH::ContactListener {
public:
    void OnContactAdded(const JPH::Body &body1, const JPH::Body &body2,
            const JPH::ContactManifold &, JPH::ContactSettings &) override {
        InsertPair(body1.GetID(), body2.GetID());
    }

    void OnContactPersisted(const JPH::Body &body1, const JPH::Body &body2,
            const JPH::ContactManifold &, JPH::ContactSettings &) override {
        InsertPair(body1.GetID(), body2.GetID());
    }

    void OnContactRemoved(const JPH::SubShapeIDPair &pair) override {
        std::lock_guard<std::mutex> lock(mMutex);
        mActive.erase(PairKey(pair.GetBody1ID(), pair.GetBody2ID()));
    }

    void EraseBody(uint32_t key) {
        std::lock_guard<std::mutex> lock(mMutex);
        for (auto it = mActive.begin(); it != mActive.end();) {
            uint32_t first = static_cast<uint32_t>(*it >> 32);
            uint32_t second = static_cast<uint32_t>(*it & 0xFFFFFFFFULL);
            if (first == key || second == key) {
                it = mActive.erase(it);
            } else {
                ++it;
            }
        }
    }

    std::vector<uint64_t> Snapshot() {
        std::lock_guard<std::mutex> lock(mMutex);
        return std::vector<uint64_t>(mActive.begin(), mActive.end());
    }

private:
    void InsertPair(JPH::BodyID id1, JPH::BodyID id2) {
        std::lock_guard<std::mutex> lock(mMutex);
        mActive.insert(PairKey(id1, id2));
    }

    static uint64_t PairKey(JPH::BodyID id1, JPH::BodyID id2) {
        uint32_t first = id1.GetIndexAndSequenceNumber();
        uint32_t second = id2.GetIndexAndSequenceNumber();
        if (first > second) {
            std::swap(first, second);
        }
        return (static_cast<uint64_t>(first) << 32) | second;
    }

    std::mutex mMutex;
    std::set<uint64_t> mActive;
};

class World {
public:
    World(float gravityX, float gravityY, float gravityZ)
        : mTempAllocator(10 * 1024 * 1024) {
        mBroadPhaseLayers = std::make_unique<JPH::BroadPhaseLayerInterfaceTable>(2, 1);
        mBroadPhaseLayers->MapObjectToBroadPhaseLayer(kMovingLayer,
                JPH::BroadPhaseLayer(0));
        mBroadPhaseLayers->MapObjectToBroadPhaseLayer(kStaticLayer,
                JPH::BroadPhaseLayer(0));
        mObjectLayers = std::make_unique<JPH::ObjectLayerPairFilterTable>(2);
        mObjectLayers->EnableCollision(kMovingLayer, kMovingLayer);
        mObjectLayers->EnableCollision(kMovingLayer, kStaticLayer);
        mObjectLayers->DisableCollision(kStaticLayer, kStaticLayer);
        mObjectVsBroadPhaseLayers =
                std::make_unique<JPH::ObjectVsBroadPhaseLayerFilterTable>(
                        *mBroadPhaseLayers, 1, *mObjectLayers, 2);
        mPhysicsSystem.Init(kMaxBodies, 0, kMaxBodyPairs, kMaxContacts,
                *mBroadPhaseLayers, *mObjectVsBroadPhaseLayers, *mObjectLayers);
        mPhysicsSystem.SetGravity(JPH::Vec3(gravityX, gravityY, gravityZ));
        mPhysicsSystem.SetContactListener(&mContactTracker);
        mBodyInterface = &mPhysicsSystem.GetBodyInterface();
    }

    ~World() {
        for (const auto &entry : mBodies) {
            DestroyBody(entry.first);
        }
        mBodies.clear();
    }

    int64_t CreateBody(int motionTypeValue, int shapeType, float halfX, float halfY,
            float halfZ, float radius, float halfHeight, float mass, float friction,
            float restitution, float gravityFactor, float linearDamping,
            float angularDamping, bool continuousCollision, const jfloat *transform) {
        JPH::EMotionType motionType;
        switch (motionTypeValue) {
            case 1:
                motionType = JPH::EMotionType::Static;
                break;
            case 2:
                motionType = JPH::EMotionType::Kinematic;
                break;
            case 3:
                motionType = JPH::EMotionType::Dynamic;
                break;
            default:
                return -1;
        }
        halfX = PositiveFinite(halfX, 0.05f);
        halfY = PositiveFinite(halfY, 0.05f);
        halfZ = PositiveFinite(halfZ, 0.05f);
        radius = PositiveFinite(radius, 0.05f);
        halfHeight = PositiveFinite(halfHeight, 0.005f);
        mass = std::max(0.001f, PositiveFinite(mass, 1.0f));
        JPH::ShapeRefC shape;
        switch (shapeType) {
            case 1: {
                float convexRadius = std::min({0.05f, halfX * 0.25f, halfY * 0.25f,
                        halfZ * 0.25f});
                shape = new JPH::BoxShape(JPH::Vec3(halfX, halfY, halfZ), convexRadius);
                break;
            }
            case 2:
                shape = new JPH::SphereShape(radius);
                break;
            case 3:
                shape = new JPH::CapsuleShape(halfHeight, radius);
                break;
            case 4: {
                float convexRadius = std::min({0.05f, radius * 0.25f, halfHeight * 0.25f});
                shape = new JPH::CylinderShape(halfHeight, radius, convexRadius);
                break;
            }
            default:
                return -1;
        }
        JPH::RVec3 position(transform[0], transform[1], transform[2]);
        JPH::Quat rotation(Normalize(transform[3], transform[4], transform[5], transform[6]));
        uint32_t objectLayer = motionType == JPH::EMotionType::Static
                ? kStaticLayer : kMovingLayer;
        JPH::BodyCreationSettings settings(shape, position, rotation, motionType, objectLayer);
        settings.mFriction = std::max(0.0f, PositiveFinite(friction, 0.5f));
        settings.mRestitution = std::clamp(PositiveFinite(restitution, 0.1f), 0.0f, 1.0f);
        settings.mGravityFactor = std::max(0.0f, PositiveFinite(gravityFactor, 1.0f));
        settings.mLinearDamping = std::max(0.0f, PositiveFinite(linearDamping, 0.05f));
        settings.mAngularDamping = std::max(0.0f, PositiveFinite(angularDamping, 0.1f));
        settings.mAllowDynamicOrKinematic = true;
        settings.mCollideKinematicVsNonDynamic = true;
        settings.mMotionQuality = continuousCollision
                ? JPH::EMotionQuality::LinearCast : JPH::EMotionQuality::Discrete;
        if (motionType == JPH::EMotionType::Dynamic) {
            settings.mMassPropertiesOverride.mMass = mass;
            settings.mOverrideMassProperties = JPH::EOverrideMassProperties::CalculateInertia;
        }
        JPH::EActivation activation = motionType == JPH::EMotionType::Static
                ? JPH::EActivation::DontActivate : JPH::EActivation::Activate;
        JPH::BodyID bodyId = mBodyInterface->CreateAndAddBody(settings, activation);
        if (bodyId.IsInvalid()) {
            return -1;
        }
        uint32_t key = bodyId.GetIndexAndSequenceNumber();
        BodyRecord record;
        record.motionType = motionType;
        record.targetPosition = position;
        record.targetRotation = rotation;
        record.lastPosition = position;
        record.lastRotation = rotation;
        mBodies[key] = record;
        return key;
    }

    void DestroyBody(uint32_t key) {
        auto iterator = mBodies.find(key);
        if (iterator == mBodies.end()) {
            return;
        }
        JPH::BodyID bodyId(key);
        if (mBodyInterface->IsAdded(bodyId)) {
            mBodyInterface->RemoveBody(bodyId);
            mBodyInterface->DestroyBody(bodyId);
        }
        mBodies.erase(iterator);
        mContactTracker.EraseBody(key);
    }

    void GetLinearVelocity(uint32_t key, float *out) {
        out[0] = 0.0f;
        out[1] = 0.0f;
        out[2] = 0.0f;
        auto iterator = mBodies.find(key);
        if (iterator == mBodies.end()) {
            return;
        }
        JPH::BodyID bodyId(key);
        if (!mBodyInterface->IsAdded(bodyId)) {
            return;
        }
        JPH::Vec3 velocity = mBodyInterface->GetLinearVelocity(bodyId);
        float vx = velocity.GetX();
        float vy = velocity.GetY();
        float vz = velocity.GetZ();
        if (std::isfinite(vx) && std::isfinite(vy) && std::isfinite(vz)) {
            out[0] = vx;
            out[1] = vy;
            out[2] = vz;
        }
    }

    std::vector<int64_t> GetActiveContacts() {
        std::vector<uint64_t> snapshot = mContactTracker.Snapshot();
        std::vector<int64_t> result;
        result.reserve(snapshot.size() * 2);
        for (uint64_t pair : snapshot) {
            uint32_t first = static_cast<uint32_t>(pair >> 32);
            uint32_t second = static_cast<uint32_t>(pair & 0xFFFFFFFFULL);
            if (mBodies.find(first) == mBodies.end()
                    || mBodies.find(second) == mBodies.end()) {
                continue;
            }
            result.push_back(static_cast<int64_t>(first));
            result.push_back(static_cast<int64_t>(second));
        }
        return result;
    }

    void SetTransform(uint32_t key, const jfloat *transform) {
        auto iterator = mBodies.find(key);
        if (iterator == mBodies.end()) {
            return;
        }
        BodyRecord &record = iterator->second;
        JPH::RVec3 position(transform[0], transform[1], transform[2]);
        JPH::Quat rotation(Normalize(transform[3], transform[4], transform[5], transform[6]));
        record.targetPosition = position;
        record.targetRotation = rotation;
        if (record.motionType == JPH::EMotionType::Kinematic) {
            return;
        }
        JPH::EActivation activation = record.motionType == JPH::EMotionType::Static
                ? JPH::EActivation::DontActivate : JPH::EActivation::Activate;
        mBodyInterface->SetPositionAndRotationWhenChanged(
                JPH::BodyID(key), position, rotation, activation);
        record.lastPosition = position;
        record.lastRotation = rotation;
    }

    void SetLinearVelocity(uint32_t key, float x, float y, float z) {
        auto iterator = mBodies.find(key);
        if (iterator == mBodies.end()
                || iterator->second.motionType != JPH::EMotionType::Dynamic) {
            return;
        }
        JPH::BodyID bodyId(key);
        mBodyInterface->ActivateBody(bodyId);
        mBodyInterface->SetLinearVelocity(bodyId,
                JPH::Vec3(Finite(x), Finite(y), Finite(z)));
    }

    void AddImpulse(uint32_t key, float x, float y, float z) {
        auto iterator = mBodies.find(key);
        if (iterator == mBodies.end()
                || iterator->second.motionType != JPH::EMotionType::Dynamic) {
            return;
        }
        JPH::BodyID bodyId(key);
        mBodyInterface->ActivateBody(bodyId);
        mBodyInterface->AddImpulse(bodyId,
                JPH::Vec3(Finite(x), Finite(y), Finite(z)));
    }

    void SetGravity(float x, float y, float z) {
        mPhysicsSystem.SetGravity(JPH::Vec3(Finite(x), Finite(y), Finite(z)));
    }

    int Update(float deltaSeconds, const int64_t *bodyKeys, size_t bodyCount,
            jfloat *output, jsize outputLength) {
        if (!std::isfinite(deltaSeconds) || deltaSeconds <= 0.0f || bodyCount == 0) {
            return 0;
        }
        mAccumulator += std::min(deltaSeconds, kFixedStep * kMaxSteps);
        int steps = 0;
        while (mAccumulator >= kFixedStep && steps < kMaxSteps) {
            for (const auto &entry : mBodies) {
                if (entry.second.motionType == JPH::EMotionType::Kinematic) {
                    mBodyInterface->MoveKinematic(JPH::BodyID(entry.first),
                            entry.second.targetPosition, entry.second.targetRotation,
                            kFixedStep);
                }
            }
            mPhysicsSystem.Update(kFixedStep, 1, &mTempAllocator, gJobSystem.get());
            mAccumulator -= kFixedStep;
            steps++;
        }
        if (steps == kMaxSteps && mAccumulator >= kFixedStep) {
            mAccumulator = std::fmod(mAccumulator, kFixedStep);
        }
        if (steps == 0) {
            return 0;
        }
        int changed = 0;
        for (size_t index = 0; index < bodyCount; index++) {
            uint32_t key = static_cast<uint32_t>(bodyKeys[index]);
            auto iterator = mBodies.find(key);
            if (iterator == mBodies.end()) {
                continue;
            }
            JPH::BodyID bodyId(key);
            if (!mBodyInterface->IsAdded(bodyId)) {
                continue;
            }
            JPH::RVec3 position;
            JPH::Quat rotation;
            mBodyInterface->GetPositionAndRotation(bodyId, position, rotation);
            BodyRecord &record = iterator->second;
            if (!PoseChanged(record, position, rotation)) {
                continue;
            }
            if ((changed + 1) * 8 > outputLength) {
                return -1;
            }
            float *target = output + changed * 8;
            target[0] = static_cast<float>(index);
            target[1] = position.GetX();
            target[2] = position.GetY();
            target[3] = position.GetZ();
            target[4] = rotation.GetX();
            target[5] = rotation.GetY();
            target[6] = rotation.GetZ();
            target[7] = rotation.GetW();
            record.lastPosition = position;
            record.lastRotation = rotation;
            changed++;
        }
        return changed;
    }

private:
    static float Finite(float value) {
        return std::isfinite(value) ? value : 0.0f;
    }

    static float PositiveFinite(float value, float fallback) {
        return std::isfinite(value) && value > 0.0f ? value : fallback;
    }

    static JPH::Quat Normalize(float x, float y, float z, float w) {
        JPH::Quat rotation(x, y, z, w);
        if (rotation.LengthSq() < 1.0e-12f || !std::isfinite(rotation.LengthSq())) {
            return JPH::Quat::sIdentity();
        }
        return rotation.Normalized();
    }

    static bool PoseChanged(const BodyRecord &record, const JPH::RVec3 &position,
            const JPH::Quat &rotation) {
        return std::abs(record.lastPosition.GetX() - position.GetX()) > kPoseEpsilon
                || std::abs(record.lastPosition.GetY() - position.GetY()) > kPoseEpsilon
                || std::abs(record.lastPosition.GetZ() - position.GetZ()) > kPoseEpsilon
                || std::abs(record.lastRotation.GetX() - rotation.GetX()) > kPoseEpsilon
                || std::abs(record.lastRotation.GetY() - rotation.GetY()) > kPoseEpsilon
                || std::abs(record.lastRotation.GetZ() - rotation.GetZ()) > kPoseEpsilon
                || std::abs(record.lastRotation.GetW() - rotation.GetW()) > kPoseEpsilon;
    }

    std::unique_ptr<JPH::BroadPhaseLayerInterfaceTable> mBroadPhaseLayers;
    std::unique_ptr<JPH::ObjectLayerPairFilterTable> mObjectLayers;
    std::unique_ptr<JPH::ObjectVsBroadPhaseLayerFilterTable> mObjectVsBroadPhaseLayers;
    ContactTracker mContactTracker;
    JPH::PhysicsSystem mPhysicsSystem;
    JPH::TempAllocatorImplWithMallocFallback mTempAllocator;
    JPH::BodyInterface *mBodyInterface;
    std::unordered_map<uint32_t, BodyRecord> mBodies;
    float mAccumulator = 0.0f;
};

World *FromHandle(jlong handle) {
    return reinterpret_cast<World *>(handle);
}

uint32_t ToKey(jlong bodyId) {
    if (bodyId < 0 || bodyId > std::numeric_limits<uint32_t>::max()) {
        return JPH::BodyID::cInvalidBodyID;
    }
    return static_cast<uint32_t>(bodyId);
}

}

extern "C" JNIEXPORT jlong JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nCreateWorld(
        JNIEnv *, jclass, jint workers, jfloat gravityX, jfloat gravityY, jfloat gravityZ) {
    try {
        InitializeJolt(workers);
        return reinterpret_cast<jlong>(new World(gravityX, gravityY, gravityZ));
    } catch (...) {
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nDestroyWorld(
        JNIEnv *, jclass, jlong handle) {
    delete FromHandle(handle);
}

extern "C" JNIEXPORT jlong JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nCreateBody(
        JNIEnv *env, jclass, jlong worldHandle, jint motionType, jint shapeType,
        jfloat halfX, jfloat halfY, jfloat halfZ, jfloat radius, jfloat halfHeight,
        jfloat mass, jfloat friction, jfloat restitution, jfloat gravityFactor,
        jfloat linearDamping, jfloat angularDamping, jboolean continuousCollision,
        jfloatArray transformArray) {
    World *world = FromHandle(worldHandle);
    jfloat *transform = env->GetFloatArrayElements(transformArray, nullptr);
    if (world == nullptr || transform == nullptr) {
        if (transform != nullptr) {
            env->ReleaseFloatArrayElements(transformArray, transform, JNI_ABORT);
        }
        return -1;
    }
    int64_t bodyId = world->CreateBody(motionType, shapeType, halfX, halfY, halfZ, radius,
            halfHeight, mass, friction, restitution, gravityFactor, linearDamping,
            angularDamping, continuousCollision == JNI_TRUE, transform);
    env->ReleaseFloatArrayElements(transformArray, transform, JNI_ABORT);
    return bodyId;
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nDestroyBody(
        JNIEnv *, jclass, jlong worldHandle, jlong bodyId) {
    World *world = FromHandle(worldHandle);
    uint32_t key = ToKey(bodyId);
    if (world != nullptr && key != JPH::BodyID::cInvalidBodyID) {
        world->DestroyBody(key);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nSetTransform(
        JNIEnv *env, jclass, jlong worldHandle, jlong bodyId, jfloatArray transformArray) {
    World *world = FromHandle(worldHandle);
    uint32_t key = ToKey(bodyId);
    jfloat *transform = env->GetFloatArrayElements(transformArray, nullptr);
    if (world != nullptr && key != JPH::BodyID::cInvalidBodyID && transform != nullptr) {
        world->SetTransform(key, transform);
    }
    if (transform != nullptr) {
        env->ReleaseFloatArrayElements(transformArray, transform, JNI_ABORT);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nSetLinearVelocity(
        JNIEnv *, jclass, jlong worldHandle, jlong bodyId,
        jfloat x, jfloat y, jfloat z) {
    World *world = FromHandle(worldHandle);
    uint32_t key = ToKey(bodyId);
    if (world != nullptr && key != JPH::BodyID::cInvalidBodyID) {
        world->SetLinearVelocity(key, x, y, z);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nAddImpulse(
        JNIEnv *, jclass, jlong worldHandle, jlong bodyId,
        jfloat x, jfloat y, jfloat z) {
    World *world = FromHandle(worldHandle);
    uint32_t key = ToKey(bodyId);
    if (world != nullptr && key != JPH::BodyID::cInvalidBodyID) {
        world->AddImpulse(key, x, y, z);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nGetLinearVelocity(
        JNIEnv *env, jclass, jlong worldHandle, jlong bodyId, jfloatArray outputArray) {
    World *world = FromHandle(worldHandle);
    uint32_t key = ToKey(bodyId);
    if (world == nullptr || key == JPH::BodyID::cInvalidBodyID || outputArray == nullptr
            || env->GetArrayLength(outputArray) < 3) {
        return;
    }
    jfloat *output = env->GetFloatArrayElements(outputArray, nullptr);
    if (output != nullptr) {
        world->GetLinearVelocity(key, output);
        env->ReleaseFloatArrayElements(outputArray, output, 0);
    }
}

extern "C" JNIEXPORT jlongArray JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nGetActiveContacts(
        JNIEnv *env, jclass, jlong worldHandle) {
    World *world = FromHandle(worldHandle);
    std::vector<int64_t> contacts;
    if (world != nullptr) {
        contacts = world->GetActiveContacts();
    }
    jlongArray result = env->NewLongArray(static_cast<jsize>(contacts.size()));
    if (result != nullptr && !contacts.empty()) {
        env->SetLongArrayRegion(result, 0, static_cast<jsize>(contacts.size()),
                reinterpret_cast<const jlong *>(contacts.data()));
    }
    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nSetGravity(
        JNIEnv *, jclass, jlong worldHandle, jfloat x, jfloat y, jfloat z) {
    World *world = FromHandle(worldHandle);
    if (world != nullptr) {
        world->SetGravity(x, y, z);
    }
}

extern "C" JNIEXPORT jint JNICALL
Java_org_catrobat_catroid_neo3d_physics_JoltNativeBridge_nUpdate(
        JNIEnv *env, jclass, jlong worldHandle, jfloat deltaSeconds,
        jlongArray bodyIdsArray, jfloatArray outputArray) {
    World *world = FromHandle(worldHandle);
    jsize bodyCount = env->GetArrayLength(bodyIdsArray);
    jlong *bodyIds = env->GetLongArrayElements(bodyIdsArray, nullptr);
    jfloat *output = env->GetFloatArrayElements(outputArray, nullptr);
    jint result = -1;
    if (world != nullptr && bodyIds != nullptr && output != nullptr) {
        std::vector<int64_t> ids(bodyIds, bodyIds + bodyCount);
        result = world->Update(deltaSeconds, ids.data(), ids.size(), output,
                env->GetArrayLength(outputArray));
    }
    if (bodyIds != nullptr) {
        env->ReleaseLongArrayElements(bodyIdsArray, bodyIds, JNI_ABORT);
    }
    if (output != nullptr) {
        env->ReleaseFloatArrayElements(outputArray, output, 0);
    }
    return result;
}
