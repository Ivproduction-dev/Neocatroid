package org.catrobat.catroid.neo3d.physics;

import android.util.Log;

import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DMath;
import org.catrobat.catroid.neo3d.Neo3DPhysicsBody;
import org.catrobat.catroid.neo3d.Neo3DPhysicsPose;
import org.catrobat.catroid.neo3d.Neo3DPrimitiveMeshes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JoltPhysicsBackend implements INeo3DPhysicsBackend {
    private static final String TAG = "Neo3D-Jolt";
    private static final float MIN_EXTENT = 0.05f;

    private final Map<String, Long> worldsByScene = new LinkedHashMap<>();
    private final Map<String, Map<String, BodyHandle>> bodiesByScene = new LinkedHashMap<>();
    private boolean initialized;
    private float gravityX;
    private float gravityY = -9.81f;
    private float gravityZ;

    @Override
    public String getName() {
        return "Jolt";
    }

    @Override
    public boolean initialize() {
        if (initialized) {
            return true;
        }
        try {
            System.loadLibrary("neo3d_jolt");
            initialized = true;
            Log.i(TAG, "Jolt source runtime loaded");
            return true;
        } catch (Throwable throwable) {
            Log.e(TAG, "Jolt runtime load failed", throwable);
            dispose();
            return false;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public synchronized void registerScene(String sceneId) {
        if (!initialized || sceneId == null || worldsByScene.containsKey(sceneId)) {
            return;
        }
        int workers = Math.max(1, Math.min(3,
                Runtime.getRuntime().availableProcessors() - 1));
        long world = JoltNativeBridge.nCreateWorld(workers, gravityX, gravityY, gravityZ);
        if (world == 0L) {
            Log.e(TAG, "Unable to create Jolt world for " + sceneId);
            return;
        }
        worldsByScene.put(sceneId, world);
        bodiesByScene.put(sceneId, new LinkedHashMap<>());
    }

    @Override
    public synchronized void unregisterScene(String sceneId) {
        Map<String, BodyHandle> bodies = bodiesByScene.remove(sceneId);
        Long world = worldsByScene.remove(sceneId);
        if (bodies != null && world != null) {
            for (BodyHandle handle : bodies.values()) {
                JoltNativeBridge.nDestroyBody(world, handle.bodyId);
            }
            JoltNativeBridge.nDestroyWorld(world);
        }
    }

    @Override
    public synchronized void syncObject(String sceneId, Neo3DGameObject object) {
        if (!initialized || sceneId == null || object == null) {
            return;
        }
        registerScene(sceneId);
        Long world = worldsByScene.get(sceneId);
        Map<String, BodyHandle> bodies = bodiesByScene.get(sceneId);
        if (world == null || bodies == null) {
            return;
        }
        Neo3DPhysicsBody config = object.getPhysicsBody();
        if (config == null || config.getMotionType() == Neo3DPhysicsBody.MotionType.NONE) {
            removeObject(sceneId, object.getId());
            return;
        }
        Neo3DPhysicsBody bodyConfig = config.copy();
        int signature = configurationSignature(bodyConfig, object);
        BodyHandle current = bodies.get(object.getId());
        if (current != null && current.signature == signature) {
            return;
        }
        if (current != null) {
            removeObject(sceneId, object.getId());
        }
        BodyHandle created = createBody(world, object, bodyConfig, signature);
        if (created != null) {
            bodies.put(object.getId(), created);
        }
    }

    @Override
    public synchronized void removeObject(String sceneId, String objectId) {
        Map<String, BodyHandle> bodies = bodiesByScene.get(sceneId);
        Long world = worldsByScene.get(sceneId);
        BodyHandle handle = bodies == null ? null : bodies.remove(objectId);
        if (handle != null && world != null) {
            JoltNativeBridge.nDestroyBody(world, handle.bodyId);
        }
    }

    @Override
    public synchronized void setObjectTransform(String sceneId, Neo3DGameObject object) {
        BodyHandle handle = findBody(sceneId, object == null ? null : object.getId());
        Long world = worldsByScene.get(sceneId);
        if (handle == null || object == null || world == null) {
            return;
        }
        float[] matrix = object.getTransform().getWorldMatrix();
        float[] position = {matrix[12], matrix[13], matrix[14]};
        float[] rotation = rotationFromMatrix(matrix);
        JoltNativeBridge.nSetTransform(world, handle.bodyId,
                new float[]{position[0], position[1], position[2],
                        rotation[0], rotation[1], rotation[2], rotation[3]});
    }

    @Override
    public synchronized void setLinearVelocity(String sceneId, String objectId,
            float x, float y, float z) {
        BodyHandle handle = findBody(sceneId, objectId);
        Long world = worldsByScene.get(sceneId);
        if (handle != null && world != null
                && handle.motionType == Neo3DPhysicsBody.MotionType.DYNAMIC) {
            JoltNativeBridge.nSetLinearVelocity(world, handle.bodyId, x, y, z);
        }
    }

    @Override
    public synchronized void addImpulse(String sceneId, String objectId,
            float x, float y, float z) {
        BodyHandle handle = findBody(sceneId, objectId);
        Long world = worldsByScene.get(sceneId);
        if (handle != null && world != null
                && handle.motionType == Neo3DPhysicsBody.MotionType.DYNAMIC) {
            JoltNativeBridge.nAddImpulse(world, handle.bodyId, x, y, z);
        }
    }

    @Override
    public synchronized void setGravity(float x, float y, float z) {
        gravityX = finite(x);
        gravityY = finite(y);
        gravityZ = finite(z);
        for (Long world : worldsByScene.values()) {
            JoltNativeBridge.nSetGravity(world, gravityX, gravityY, gravityZ);
        }
    }

    @Override
    public synchronized List<Neo3DPhysicsPose> update(String sceneId, float deltaSec) {
        Map<String, BodyHandle> bodies = bodiesByScene.get(sceneId);
        Long world = worldsByScene.get(sceneId);
        if (!initialized || world == null || bodies == null || bodies.isEmpty()
                || Float.isNaN(deltaSec) || Float.isInfinite(deltaSec) || deltaSec <= 0f) {
            return Collections.emptyList();
        }
        List<BodyHandle> orderedHandles = new ArrayList<>(bodies.values());
        long[] bodyIds = new long[orderedHandles.size()];
        for (int index = 0; index < orderedHandles.size(); index++) {
            bodyIds[index] = orderedHandles.get(index).bodyId;
        }
        float[] output = new float[bodyIds.length * 8];
        int changed = JoltNativeBridge.nUpdate(world, deltaSec, bodyIds, output);
        if (changed <= 0) {
            return Collections.emptyList();
        }
        List<Neo3DPhysicsPose> poses = new ArrayList<>(changed);
        for (int index = 0; index < changed; index++) {
            int offset = index * 8;
            int bodyIndex = (int) output[offset];
            if (bodyIndex < 0 || bodyIndex >= orderedHandles.size()) {
                continue;
            }
            BodyHandle handle = orderedHandles.get(bodyIndex);
            poses.add(new Neo3DPhysicsPose(sceneId, handle.objectId,
                    new float[]{output[offset + 1], output[offset + 2], output[offset + 3]},
                    new float[]{output[offset + 4], output[offset + 5],
                            output[offset + 6], output[offset + 7]}));
        }
        return poses;
    }

    @Override
    public synchronized int getBodyCount(String sceneId) {
        Map<String, BodyHandle> bodies = bodiesByScene.get(sceneId);
        return bodies == null ? 0 : bodies.size();
    }

    @Override
    public synchronized int getTotalBodyCount() {
        int count = 0;
        for (Map<String, BodyHandle> bodies : bodiesByScene.values()) {
            count += bodies.size();
        }
        return count;
    }

    @Override
    public synchronized void dispose() {
        initialized = false;
        for (Map.Entry<String, Map<String, BodyHandle>> entry : bodiesByScene.entrySet()) {
            Long world = worldsByScene.get(entry.getKey());
            if (world != null) {
                for (BodyHandle handle : entry.getValue().values()) {
                    JoltNativeBridge.nDestroyBody(world, handle.bodyId);
                }
            }
        }
        for (Long world : worldsByScene.values()) {
            JoltNativeBridge.nDestroyWorld(world);
        }
        worldsByScene.clear();
        bodiesByScene.clear();
    }

    private BodyHandle createBody(long world, Neo3DGameObject object,
            Neo3DPhysicsBody config, int signature) {
        float[] matrix = object.getTransform().getWorldMatrix();
        float[] position = {matrix[12], matrix[13], matrix[14]};
        float[] rotation = rotationFromMatrix(matrix);
        float[] scale = object.getTransform().getScale();
        float sx = Math.max(MIN_EXTENT, Math.abs(scale[0]));
        float sy = Math.max(MIN_EXTENT, Math.abs(scale[1]));
        float sz = Math.max(MIN_EXTENT, Math.abs(scale[2]));
        boolean unitPrimitive = isUnitPrimitive(object.getModelPath());
        float baseHalf = unitPrimitive ? 1f : 0.5f;
        Neo3DPhysicsBody.ShapeType shapeType = resolveShapeType(
                config.getShapeType(), object);
        float halfX = baseHalf * sx;
        float halfY = baseHalf * sy;
        float halfZ = baseHalf * sz;
        float radius = baseHalf * Math.max(sx, Math.max(sy, sz));
        float halfHeight = Math.max(0.005f, baseHalf * sy - radius);
        int shapeValue;
        switch (shapeType) {
            case SPHERE:
                shapeValue = 2;
                break;
            case CAPSULE:
                shapeValue = 3;
                radius = Math.max(MIN_EXTENT, Math.min(baseHalf * Math.max(sx, sz), halfY));
                halfHeight = Math.max(0.005f, baseHalf * sy - radius);
                break;
            case CYLINDER:
                shapeValue = 4;
                radius = baseHalf * Math.max(sx, sz);
                halfHeight = Math.max(MIN_EXTENT, baseHalf * sy);
                break;
            case BOX:
            case AUTO:
            default:
                shapeValue = 1;
                break;
        }
        long bodyId = JoltNativeBridge.nCreateBody(world, config.getMotionType().ordinal(),
                shapeValue, halfX, halfY, halfZ, radius, halfHeight, config.getMass(),
                config.getFriction(), config.getRestitution(), config.getGravityFactor(),
                config.getLinearDamping(), config.getAngularDamping(),
                config.isContinuousCollision(),
                new float[]{position[0], position[1], position[2], rotation[0],
                        rotation[1], rotation[2], rotation[3]});
        if (bodyId < 0L) {
            Log.e(TAG, "Body creation failed for " + object.getName());
            return null;
        }
        return new BodyHandle(object.getId(), bodyId, signature, config.getMotionType());
    }

    private BodyHandle findBody(String sceneId, String objectId) {
        if (sceneId == null || objectId == null) {
            return null;
        }
        Map<String, BodyHandle> bodies = bodiesByScene.get(sceneId);
        return bodies == null ? null : bodies.get(objectId);
    }

    private static int configurationSignature(Neo3DPhysicsBody config, Neo3DGameObject object) {
        int hash = config.getMotionType().ordinal();
        hash = 31 * hash + config.getShapeType().ordinal();
        hash = 31 * hash + Float.floatToIntBits(config.getMass());
        hash = 31 * hash + Float.floatToIntBits(config.getFriction());
        hash = 31 * hash + Float.floatToIntBits(config.getRestitution());
        hash = 31 * hash + Float.floatToIntBits(config.getGravityFactor());
        hash = 31 * hash + Float.floatToIntBits(config.getLinearDamping());
        hash = 31 * hash + Float.floatToIntBits(config.getAngularDamping());
        hash = 31 * hash + Boolean.hashCode(config.isContinuousCollision());
        String modelPath = object.getModelPath();
        hash = 31 * hash + (modelPath == null ? 0 : modelPath.hashCode());
        for (float value : object.getTransform().getScale()) {
            hash = 31 * hash + Float.floatToIntBits(value);
        }
        return hash;
    }

    private static Neo3DPhysicsBody.ShapeType resolveShapeType(
            Neo3DPhysicsBody.ShapeType selected, Neo3DGameObject object) {
        if (selected != Neo3DPhysicsBody.ShapeType.AUTO) {
            return selected;
        }
        String modelPath = object.getModelPath();
        if (Neo3DPrimitiveMeshes.ASSET_KEY_SPHERE.equals(modelPath)) {
            return Neo3DPhysicsBody.ShapeType.SPHERE;
        }
        if (Neo3DPrimitiveMeshes.ASSET_KEY_CYLINDER.equals(modelPath)) {
            return Neo3DPhysicsBody.ShapeType.CYLINDER;
        }
        return Neo3DPhysicsBody.ShapeType.BOX;
    }

    private static boolean isUnitPrimitive(String modelPath) {
        return Neo3DPrimitiveMeshes.ASSET_KEY_CUBE.equals(modelPath)
                || Neo3DPrimitiveMeshes.ASSET_KEY_SPHERE.equals(modelPath)
                || Neo3DPrimitiveMeshes.ASSET_KEY_CYLINDER.equals(modelPath);
    }

    private static float[] rotationFromMatrix(float[] matrix) {
        float xAxisLength = length(matrix[0], matrix[1], matrix[2]);
        float yAxisLength = length(matrix[4], matrix[5], matrix[6]);
        float zAxisLength = length(matrix[8], matrix[9], matrix[10]);
        if (xAxisLength < 1e-6f || yAxisLength < 1e-6f || zAxisLength < 1e-6f) {
            return Neo3DMath.quatIdentity();
        }
        float r00 = matrix[0] / xAxisLength;
        float r10 = matrix[1] / xAxisLength;
        float r20 = matrix[2] / xAxisLength;
        float r01 = matrix[4] / yAxisLength;
        float r11 = matrix[5] / yAxisLength;
        float r21 = matrix[6] / yAxisLength;
        float r02 = matrix[8] / zAxisLength;
        float r12 = matrix[9] / zAxisLength;
        float r22 = matrix[10] / zAxisLength;
        float trace = r00 + r11 + r22;
        float x;
        float y;
        float z;
        float w;
        if (trace > 0f) {
            float value = (float) Math.sqrt(trace + 1f) * 2f;
            w = 0.25f * value;
            x = (r21 - r12) / value;
            y = (r02 - r20) / value;
            z = (r10 - r01) / value;
        } else if (r00 > r11 && r00 > r22) {
            float value = (float) Math.sqrt(1f + r00 - r11 - r22) * 2f;
            w = (r21 - r12) / value;
            x = 0.25f * value;
            y = (r01 + r10) / value;
            z = (r02 + r20) / value;
        } else if (r11 > r22) {
            float value = (float) Math.sqrt(1f + r11 - r00 - r22) * 2f;
            w = (r02 - r20) / value;
            x = (r01 + r10) / value;
            y = 0.25f * value;
            z = (r12 + r21) / value;
        } else {
            float value = (float) Math.sqrt(1f + r22 - r00 - r11) * 2f;
            w = (r10 - r01) / value;
            x = (r02 + r20) / value;
            y = (r12 + r21) / value;
            z = 0.25f * value;
        }
        return Neo3DMath.quatNormalize(new float[]{x, y, z, w});
    }

    private static float length(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    private static float finite(float value) {
        return Float.isNaN(value) || Float.isInfinite(value) ? 0f : value;
    }

    private static final class BodyHandle {
        private final String objectId;
        private final long bodyId;
        private final int signature;
        private final Neo3DPhysicsBody.MotionType motionType;

        private BodyHandle(String objectId, long bodyId, int signature,
                Neo3DPhysicsBody.MotionType motionType) {
            this.objectId = objectId;
            this.bodyId = bodyId;
            this.signature = signature;
            this.motionType = motionType;
        }
    }
}
