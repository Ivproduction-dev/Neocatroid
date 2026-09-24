package org.catrobat.catroid.neo3d.physics;

import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DPhysicsPose;

import java.util.List;

public interface INeo3DPhysicsBackend {
    String getName();

    boolean initialize();

    boolean isInitialized();

    void registerScene(String sceneId);

    void unregisterScene(String sceneId);

    void syncObject(String sceneId, Neo3DGameObject object);

    void removeObject(String sceneId, String objectId);

    void setObjectTransform(String sceneId, Neo3DGameObject object);

    void setLinearVelocity(String sceneId, String objectId, float x, float y, float z);

    void addImpulse(String sceneId, String objectId, float x, float y, float z);

    void setGravity(float x, float y, float z);

    List<Neo3DPhysicsPose> update(String sceneId, float deltaSec);

    int getBodyCount(String sceneId);

    int getTotalBodyCount();

    void dispose();
}
