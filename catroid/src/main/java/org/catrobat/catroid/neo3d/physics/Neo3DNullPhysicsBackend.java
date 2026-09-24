package org.catrobat.catroid.neo3d.physics;

import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DPhysicsBody;
import org.catrobat.catroid.neo3d.Neo3DPhysicsPose;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Neo3DNullPhysicsBackend implements INeo3DPhysicsBackend {
    private final Set<String> scenes = new HashSet<>();
    private final Map<String, Set<String>> objectsByScene = new HashMap<>();
    private float gravityX;
    private float gravityY = -9.81f;
    private float gravityZ;

    @Override
    public String getName() {
        return "Null";
    }

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public synchronized void registerScene(String sceneId) {
        scenes.add(sceneId);
        objectsByScene.putIfAbsent(sceneId, new HashSet<>());
    }

    @Override
    public synchronized void unregisterScene(String sceneId) {
        scenes.remove(sceneId);
        objectsByScene.remove(sceneId);
    }

    @Override
    public synchronized void syncObject(String sceneId, Neo3DGameObject object) {
        if (sceneId == null || object == null) {
            return;
        }
        Set<String> objects = objectsByScene.computeIfAbsent(sceneId, key -> new HashSet<>());
        Neo3DPhysicsBody body = object.getPhysicsBody();
        if (body == null || body.getMotionType() == Neo3DPhysicsBody.MotionType.NONE) {
            objects.remove(object.getId());
        } else {
            objects.add(object.getId());
        }
    }

    @Override
    public synchronized void removeObject(String sceneId, String objectId) {
        Set<String> objects = objectsByScene.get(sceneId);
        if (objects != null) {
            objects.remove(objectId);
        }
    }

    @Override
    public void setObjectTransform(String sceneId, Neo3DGameObject object) {
    }

    @Override
    public void setLinearVelocity(String sceneId, String objectId, float x, float y, float z) {
    }

    @Override
    public void addImpulse(String sceneId, String objectId, float x, float y, float z) {
    }

    @Override
    public synchronized void setGravity(float x, float y, float z) {
        gravityX = x;
        gravityY = y;
        gravityZ = z;
    }

    @Override
    public synchronized List<Neo3DPhysicsPose> update(String sceneId, float deltaSec) {
        return Collections.emptyList();
    }

    @Override
    public synchronized int getBodyCount(String sceneId) {
        Set<String> objects = objectsByScene.get(sceneId);
        return objects == null ? 0 : objects.size();
    }

    @Override
    public synchronized int getTotalBodyCount() {
        int count = 0;
        for (Set<String> objects : objectsByScene.values()) {
            count += objects.size();
        }
        return count;
    }

    public synchronized float[] getGravity() {
        return new float[]{gravityX, gravityY, gravityZ};
    }

    @Override
    public synchronized void dispose() {
        scenes.clear();
        objectsByScene.clear();
    }
}
