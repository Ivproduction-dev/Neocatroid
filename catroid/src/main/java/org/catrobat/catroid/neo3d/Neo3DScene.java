package org.catrobat.catroid.neo3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Neo3DScene {

    private final String id;
    private String name;
    private final Map<String, Neo3DGameObject> objectsById = new LinkedHashMap<>();
    private final Neo3DSkybox skybox = new Neo3DSkybox();
    private final Neo3DShadowSettings shadowSettings = new Neo3DShadowSettings();
    private float ambientIntensity = 1f;

    public Neo3DScene(String name) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Neo3DSkybox getSkybox() {
        return skybox;
    }

    public Neo3DShadowSettings getShadowSettings() {
        return shadowSettings;
    }

    public float getAmbientIntensity() {
        return ambientIntensity;
    }

    public void setAmbientIntensity(float ambientIntensity) {
        this.ambientIntensity = ambientIntensity;
    }

    public Neo3DGameObject createObject(String name) {
        Neo3DGameObject obj = new Neo3DGameObject(name);
        objectsById.put(obj.getId(), obj);
        return obj;
    }

    public Neo3DGameObject getObject(String objectId) {
        return objectsById.get(objectId);
    }

    public Neo3DGameObject findByName(String name) {
        for (Neo3DGameObject obj : objectsById.values()) {
            if (obj.getName().equals(name)) {
                return obj;
            }
        }
        return null;
    }

    public Collection<Neo3DGameObject> getAllObjects() {
        return Collections.unmodifiableCollection(objectsById.values());
    }

    public int getObjectCount() {
        return objectsById.size();
    }

    public boolean removeObject(String objectId) {
        Neo3DGameObject obj = objectsById.get(objectId);
        if (obj == null) {
            return false;
        }
        List<Neo3DTransform> childrenCopy =
                new ArrayList<>(obj.getTransform().getChildren());
        for (Neo3DTransform child : childrenCopy) {
            child.detach();
        }
        obj.getTransform().detach();
        objectsById.remove(objectId);
        return true;
    }

    public boolean setParent(String childId, String parentId) {
        Neo3DGameObject child = objectsById.get(childId);
        if (child == null) {
            return false;
        }
        if (parentId == null) {
            child.getTransform().detach();
            return true;
        }
        Neo3DGameObject parent = objectsById.get(parentId);
        if (parent == null || parent == child || isDescendantOf(parent, child)) {
            return false;
        }
        child.getTransform().attachTo(parent.getTransform());
        return true;
    }

    private boolean isDescendantOf(Neo3DGameObject node, Neo3DGameObject ancestor) {
        Neo3DTransform t = node.getTransform().getParent();
        while (t != null) {
            if (t.getOwner() == ancestor) {
                return true;
            }
            t = t.getParent();
        }
        return false;
    }

    public void clear() {
        for (Neo3DGameObject obj : objectsById.values()) {
            obj.getTransform().detach();
        }
        objectsById.clear();
    }
}
