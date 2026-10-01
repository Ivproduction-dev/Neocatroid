package org.catrobat.catroid.editor2;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.asynctask.ProjectSaverKt;
import org.catrobat.catroid.neo3d.Neo3DCamera;
import org.catrobat.catroid.neo3d.Neo3DCustomMaterial;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DLight;
import org.catrobat.catroid.neo3d.Neo3DPhysicsBody;
import org.catrobat.catroid.neo3d.Neo3DPrimitiveMeshes;
import org.catrobat.catroid.neo3d.Neo3DPrimitiveMeshes;
import org.catrobat.catroid.neo3d.Neo3DPersistedObject;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.Neo3DEngine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.nio.charset.StandardCharsets;

final class Neo3DEditorDocument {
    private static final int MAX_HISTORY = 50;

    private static final class EditorSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;
        ArrayList<Neo3DPersistedObject> objects;
        float skyRed;
        float skyGreen;
        float skyBlue;
        float iblIntensity;
        boolean shadowsEnabled;
    }

    private final Context context;
    private final Project project;
    private final Scene projectScene;
    private final Neo3DEngine engine;
    private final Neo3DScene scene;
    private final ExecutorService ioQueue = Executors.newSingleThreadExecutor();
    private final android.os.Handler mainHandler =
            new android.os.Handler(android.os.Looper.getMainLooper());
    private static final long SAVE_DEBOUNCE_MS = 300L;
    private Runnable pendingSave;
    private final Map<String, Neo3DPersistedObject> persistedById = new HashMap<>();
    private final Deque<byte[]> undoStack = new ArrayDeque<>();
    private final Deque<byte[]> redoStack = new ArrayDeque<>();
    private byte[] lastHistorySnapshot;
    private String cameraId;
    private String selectedId;
    private final java.util.Set<String> selectedIds = new java.util.LinkedHashSet<>();
    private final java.util.List<Neo3DPersistedObject> clipboard = new java.util.ArrayList<>();
    private Runnable modelLoadFailureListener;

    Neo3DEditorDocument(Context context) {
        this.context = context.getApplicationContext();
        project = ProjectManager.getInstance().getCurrentProject();
        projectScene = ProjectManager.getInstance().getCurrentlyEditedScene();
        engine = Neo3DEngine.create(context, Neo3DEngine.BackendType.FILAMENT);
        scene = new Neo3DScene(projectScene.getName());
        float[] skyColor = projectScene.getNeo3DSkyColor();
        scene.getSkybox().setColorA(skyColor[0], skyColor[1], skyColor[2], 1f);
        scene.getSkybox().setIblIntensity(projectScene.getNeo3DIblIntensity());
        scene.getShadowSettings().setEnabled(projectScene.isNeo3DShadowsEnabled());
        engine.registerScene(scene);
        loadObjects();
        ensureViewportCamera();
        Neo3DGameObject fillLight = scene.createObject("__editor_fill_light");
        fillLight.setLight(Neo3DLight.directional(60000f));
        fillLight.getTransform().setRotationEulerDeg(-35f, -25f, 0f);
        engine.syncObject(scene.getId(), fillLight.getId());
        ensureEditorGrid();
        if (cameraId != null) engine.syncObject(scene.getId(), cameraId);
        engine.syncScene(scene.getId());
        engine.setCameraTouchLook(scene.getId(), 0, 0.25f, -85f, 85f);
        lastHistorySnapshot = captureSnapshot();
    }

    Neo3DEngine engine() { return engine; }
    Neo3DScene scene() { return scene; }
    String selectedId() { return selectedId; }
    void select(String id) {
        selectedIds.clear();
        if (id != null) selectedIds.add(id);
        selectedId = id;
    }
    java.util.Set<String> selectedIds() { return new java.util.LinkedHashSet<>(selectedIds); }
    int selectionCount() { return selectedIds.size(); }
    boolean isSelected(String id) { return id != null && selectedIds.contains(id); }
    void toggleSelect(String id) {
        if (id == null) return;
        if (!selectedIds.remove(id)) selectedIds.add(id);
        selectedId = selectedIds.isEmpty() ? null : selectedIds.iterator().next();
    }
    void clearSelection() {
        selectedIds.clear();
        selectedId = null;
    }
    java.util.List<Neo3DGameObject> selectedObjects() {
        java.util.List<Neo3DGameObject> result = new java.util.ArrayList<>();
        for (String id : selectedIds) {
            Neo3DGameObject object = scene.getObject(id);
            if (object != null) result.add(object);
        }
        return result;
    }
    Neo3DGameObject selectedObject() {
        return selectedId == null ? null : scene.getObject(selectedId);
    }
    void copySelectedToClipboard() {
        clipboard.clear();
        for (String id : selectedIds) {
            Neo3DPersistedObject data = persistedById.get(id);
            if (data != null) {
                Neo3DPersistedObject copy = deepCopy(data);
                if (copy != null) clipboard.add(copy);
            }
        }
    }
    boolean hasClipboard() { return !clipboard.isEmpty(); }
    java.util.List<Neo3DGameObject> pasteClipboard() {
        java.util.List<Neo3DGameObject> pasted = new java.util.ArrayList<>();
        for (Neo3DPersistedObject buffered : clipboard) {
            Neo3DPersistedObject copy = deepCopy(buffered);
            if (copy == null) continue;
            copy.x += 1f;
            pasted.add(add(copy));
        }
        return pasted;
    }
    private Neo3DPersistedObject deepCopy(Neo3DPersistedObject source) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(bytes);
            output.writeObject(source);
            output.close();
            ObjectInputStream input = new ObjectInputStream(
                    new ByteArrayInputStream(bytes.toByteArray()));
            Neo3DPersistedObject copy = (Neo3DPersistedObject) input.readObject();
            input.close();
            return copy;
        } catch (IOException | ClassNotFoundException | RuntimeException e) {
            return null;
        }
    }
    Neo3DGameObject viewportCamera() { return scene.getObject(cameraId); }
    boolean canUndo() { return !undoStack.isEmpty(); }
    boolean canRedo() { return !redoStack.isEmpty(); }
    void setModelLoadFailureListener(Runnable listener) { modelLoadFailureListener = listener; }

    float[] skyColor() { return projectScene.getNeo3DSkyColor(); }
    float iblIntensity() { return projectScene.getNeo3DIblIntensity(); }
    boolean shadowsEnabled() { return projectScene.isNeo3DShadowsEnabled(); }

    void setSceneEnvironment(float red, float green, float blue, float intensity,
            boolean shadows) {
        projectScene.setNeo3DSkyColor(red, green, blue);
        projectScene.setNeo3DIblIntensity(intensity);
        projectScene.setNeo3DShadowsEnabled(shadows);
        scene.getSkybox().setMode(org.catrobat.catroid.neo3d.Neo3DSkybox.Mode.COLOR);
        scene.getSkybox().setColorA(red, green, blue, 1f);
        scene.getSkybox().setIblIntensity(intensity);
        scene.getShadowSettings().setEnabled(shadows);
        engine.syncScene(scene.getId());
        saveDebounced();
    }
    Neo3DPersistedObject data(String id) { return persistedById.get(id); }

    List<Neo3DGameObject> selectableObjects() {
        return objects();
    }

    List<Neo3DGameObject> objects() {
        List<Neo3DGameObject> result = new ArrayList<>();
        for (Neo3DGameObject object : scene.getAllObjects()) {
            if (persistedById.containsKey(object.getId())) result.add(object);
        }
        return result;
    }

    Neo3DGameObject addPrimitive(int kind) {
        String type = kind == 1 ? "SPHERE" : kind == 2 ? "CYLINDER" : "CUBE";
        Neo3DPersistedObject data = new Neo3DPersistedObject(typeName(type));
        data.objectType = type;
        data.primitiveKind = kind;
        return add(data);
    }

    Neo3DGameObject addCamera() {
        Neo3DPersistedObject data = new Neo3DPersistedObject("Camera");
        data.objectType = "CAMERA";
        data.camera = true;
        data.x = 0f;
        data.y = 0f;
        data.z = 6f;
        data.cameraUsesTransformOrientation = true;
        for (Neo3DPersistedObject existing : persistedById.values()) {
            if (existing.camera) {
                existing.cameraMain = false;
                Neo3DGameObject runtime = scene.findByName(existing.name);
                if (runtime != null && runtime.getCamera() != null) {
                    runtime.getCamera().setMainCamera(false);
                    engine.syncObject(scene.getId(), runtime.getId());
                }
            }
        }
        data.cameraMain = true;
        Neo3DGameObject object = add(data);
        cameraId = object.getId();
        return object;
    }

    Neo3DGameObject addLight(String type) {
        Neo3DPersistedObject data = new Neo3DPersistedObject(type + " Light");
        data.objectType = "LIGHT";
        data.lightType = type;
        data.lightIntensity = "DIRECTIONAL".equals(type) ? 60000f : 1200f;
        return add(data);
    }

    Neo3DGameObject addModel(String name, String assetKey) {
        Neo3DPersistedObject data = new Neo3DPersistedObject(name);
        data.objectType = "MODEL";
        data.modelAssetKey = assetKey;
        Neo3DGameObject object = add(data);
        return object;
    }

    String rename(String objectId, String name) {
        String result = engine.renameObject(scene.getId(), objectId, name);
        if (result != null) {
            Neo3DPersistedObject data = persistedById.get(objectId);
            if (data != null) data.name = result;
            saveDebounced();
        }
        return result;
    }

    boolean setParent(String objectId, String parentId) {
        boolean changed = scene.setParent(objectId, parentId == null || parentId.isEmpty()
                ? null : parentId);
        if (changed) {
            engine.syncScene(scene.getId());
            saveDebounced();
        }
        return changed;
    }

    void syncScene() {
        engine.syncScene(scene.getId());
    }

    Neo3DGameObject duplicate(String objectId) {
        Neo3DPersistedObject source = persistedById.get(objectId);
        if (source == null || source.camera || source.lightType != null) return null;
        Neo3DPersistedObject copy = new Neo3DPersistedObject(source.name + " Copy");
        copy.objectType = source.objectType;
        copy.modelAssetKey = source.modelAssetKey;
        copy.primitiveKind = source.primitiveKind;
        copy.x = source.x + 1f; copy.y = source.y; copy.z = source.z;
        copy.scaleX = source.scaleX; copy.scaleY = source.scaleY; copy.scaleZ = source.scaleZ;
        copy.yawDeg = source.yawDeg; copy.pitchDeg = source.pitchDeg; copy.rollDeg = source.rollDeg;
        copy.colorRed = source.colorRed; copy.colorGreen = source.colorGreen;
        copy.colorBlue = source.colorBlue; copy.colorAlpha = source.colorAlpha;
        copy.metallic = source.metallic; copy.roughness = source.roughness;
        copy.emissiveRed = source.emissiveRed; copy.emissiveGreen = source.emissiveGreen;
        copy.emissiveBlue = source.emissiveBlue;
        copy.physicsMotionType = source.physicsMotionType; copy.physicsShapeType = source.physicsShapeType;
        copy.physicsShapeScaleX = source.physicsShapeScaleX;
        copy.physicsShapeScaleY = source.physicsShapeScaleY;
        copy.physicsShapeScaleZ = source.physicsShapeScaleZ;
        copy.physicsMass = source.physicsMass; copy.physicsFriction = source.physicsFriction;
        copy.physicsRestitution = source.physicsRestitution;
        copy.visible = source.visible; copy.active = source.active;
        if (source.keyframes != null) {
            copy.keyframes = new java.util.ArrayList<>();
            for (Neo3DPersistedObject.Neo3DKeyframe frame : source.keyframes) {
                if (frame != null) copy.keyframes.add(frame.copy());
            }
        }
        copy.keyframeLoop = source.keyframeLoop;
        copy.physicsGravityFactor = source.physicsGravityFactor;
        copy.physicsLinearDamping = source.physicsLinearDamping;
        copy.physicsAngularDamping = source.physicsAngularDamping;
        copy.physicsContinuousCollision = source.physicsContinuousCollision;
        return add(copy);
    }

    boolean remove(String objectId) {
        Neo3DPersistedObject data = persistedById.remove(objectId);
        if (data == null || data.camera && cameraCount() <= 1) {
            if (data != null) persistedById.put(objectId, data);
            return false;
        }
        boolean removedMainCamera = data.camera && data.cameraMain;
        projectScene.getNeo3DObjects().remove(data);
        if (selectedIds.remove(objectId) && objectId.equals(selectedId)) {
            selectedId = selectedIds.isEmpty() ? null : selectedIds.iterator().next();
        }
        engine.removeObject(scene.getId(), objectId);
        for (Neo3DGameObject object : scene.getAllObjects()) {
            if (object.getTransform().getParent() != null
                    && object.getTransform().getParent().getOwner().getId().equals(objectId)) {
                scene.setParent(object.getId(), null);
            }
        }
        if (removedMainCamera) {
            cameraId = null;
            for (Neo3DGameObject object : objects()) {
                Neo3DPersistedObject candidate = data(object.getId());
                if (candidate.camera) {
                    candidate.cameraMain = true;
                    object.getCamera().setMainCamera(true);
                    cameraId = object.getId();
                    engine.syncObject(scene.getId(), cameraId);
                    break;
                }
            }
        }
        saveDebounced();
        return true;
    }

    void sync(String objectId) {
        Neo3DGameObject object = scene.getObject(objectId);
        Neo3DPersistedObject data = persistedById.get(objectId);
        if (object == null || data == null) return;
        applyMaterial(object, data);
        applyPhysics(object, data);
        engine.syncObject(scene.getId(), objectId);
        saveDebounced();
    }

    void save() {
        flushPendingSave();
        doSave(null);
    }

    void saveDebounced() {
        if (pendingSave != null) mainHandler.removeCallbacks(pendingSave);
        pendingSave = () -> {
            pendingSave = null;
            doSave(null);
        };
        mainHandler.postDelayed(pendingSave, SAVE_DEBOUNCE_MS);
    }

    private void flushPendingSave() {
        if (pendingSave != null) {
            mainHandler.removeCallbacks(pendingSave);
            pendingSave = null;
            doSave(null);
        }
    }

    void save(java.util.function.Consumer<Boolean> callback) {
        flushPendingSave();
        doSave(callback);
    }

    private void doSave(java.util.function.Consumer<Boolean> callback) {
        for (Map.Entry<String, Neo3DPersistedObject> entry : persistedById.entrySet()) {
            Neo3DGameObject object = scene.getObject(entry.getKey());
            if (object != null) copyRuntimeToData(object, entry.getValue());
        }
        byte[] currentSnapshot = captureSnapshot();
        if (lastHistorySnapshot != null
                && Arrays.equals(lastHistorySnapshot, currentSnapshot)) {
            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.accept(true));
            }
            return;
        }
        if (lastHistorySnapshot != null) {
            undoStack.push(lastHistorySnapshot);
            while (undoStack.size() > MAX_HISTORY) undoStack.removeLast();
            redoStack.clear();
        }
        lastHistorySnapshot = currentSnapshot;
        final byte[] recoveryBytes = currentSnapshot;
        ioQueue.execute(() -> {
            boolean saved;
            try {
                saved = ProjectSaverKt.saveProjectSerial(project, context);
            } catch (RuntimeException e) {
                saved = false;
            }
            writeRecoveryFile(recoveryBytes);
            if (callback != null) {
                boolean saveSucceeded = saved;
                new Handler(Looper.getMainLooper()).post(() -> callback.accept(saveSucceeded));
            }
        });
    }

    private File recoveryFile() {
        String base = projectScene.getName() == null ? "scene" : projectScene.getName();
        base = base.replaceAll("[^A-Za-z0-9_-]", "_");
        return new File(project.getFilesDir(), "neo3d_recovery_" + base + ".dat");
    }

    private void writeRecoveryFile(byte[] snapshot) {
        if (snapshot == null) return;
        try {
            File target = recoveryFile();
            File parent = target.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) return;
            File tmp = new File(parent, target.getName() + ".tmp");
            java.nio.file.Files.write(tmp.toPath(), snapshot);
            if (target.exists() && !target.delete()) {
                tmp.delete();
                return;
            }
            if (!tmp.renameTo(target)) {
                tmp.delete();
            }
        } catch (IOException | RuntimeException ignored) {
        }
    }

    boolean hasRecovery() {
        File file = recoveryFile();
        return file.exists() && file.length() > 0;
    }

    boolean restoreFromRecovery() {
        File file = recoveryFile();
        if (!file.exists() || file.length() == 0) return false;
        try {
            byte[] snapshot = java.nio.file.Files.readAllBytes(file.toPath());
            if (snapshot.length == 0) return false;
            restoreSnapshot(snapshot);
            saveWithoutHistory();
            discardRecovery();
            return true;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    void discardRecovery() {
        try {
            File file = recoveryFile();
            if (file.exists()) file.delete();
        } catch (RuntimeException ignored) {
        }
    }

    boolean undo() {
        flushPendingSave();
        if (undoStack.isEmpty()) return false;
        redoStack.push(captureSnapshot());
        restoreSnapshot(undoStack.pop());
        saveWithoutHistory();
        return true;
    }

    boolean redo() {
        flushPendingSave();
        if (redoStack.isEmpty()) return false;
        undoStack.push(captureSnapshot());
        restoreSnapshot(redoStack.pop());
        saveWithoutHistory();
        return true;
    }

    private void saveWithoutHistory() {
        final byte[] snapshot = lastHistorySnapshot;
        ioQueue.execute(() -> {
            try {
                ProjectSaverKt.saveProjectSerial(project, context);
            } catch (RuntimeException ignored) {
            }
            writeRecoveryFile(snapshot);
        });
    }

    private byte[] captureSnapshot() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(bytes);
            EditorSnapshot snapshot = new EditorSnapshot();
            snapshot.objects = new ArrayList<>(projectScene.getNeo3DObjects());
            float[] color = projectScene.getNeo3DSkyColor();
            snapshot.skyRed = color[0];
            snapshot.skyGreen = color[1];
            snapshot.skyBlue = color[2];
            snapshot.iblIntensity = projectScene.getNeo3DIblIntensity();
            snapshot.shadowsEnabled = projectScene.isNeo3DShadowsEnabled();
            output.writeObject(snapshot);
            output.close();
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to capture Neo3D edit history", e);
        }
    }

    private void restoreSnapshot(byte[] snapshot) {
        EditorSnapshot restored;
        try {
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(snapshot));
            restored = (EditorSnapshot) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Unable to restore Neo3D edit history", e);
        }
        String viewportOnlyCamera = cameraId != null && !persistedById.containsKey(cameraId)
                ? cameraId : null;
        for (String objectId : new ArrayList<>(persistedById.keySet())) {
            engine.removeObject(scene.getId(), objectId);
        }
        persistedById.clear();
        projectScene.getNeo3DObjects().clear();
        projectScene.getNeo3DObjects().addAll(restored.objects);
        projectScene.setNeo3DSkyColor(restored.skyRed, restored.skyGreen, restored.skyBlue);
        projectScene.setNeo3DIblIntensity(restored.iblIntensity);
        projectScene.setNeo3DShadowsEnabled(restored.shadowsEnabled);
        scene.getSkybox().setMode(org.catrobat.catroid.neo3d.Neo3DSkybox.Mode.COLOR);
        scene.getSkybox().setColorA(restored.skyRed, restored.skyGreen, restored.skyBlue, 1f);
        scene.getSkybox().setIblIntensity(restored.iblIntensity);
        scene.getShadowSettings().setEnabled(restored.shadowsEnabled);
        cameraId = viewportOnlyCamera;
        selectedId = null;
        selectedIds.clear();
        for (Neo3DPersistedObject data : restored.objects) {
            data.name = uniqueObjectName(data.name);
            Neo3DGameObject object = scene.createObject(data.name);
            persistedById.put(object.getId(), data);
            applyDataToRuntime(data, object);
            engine.syncObject(scene.getId(), object.getId());
            if (data.camera && data.cameraMain) cameraId = object.getId();
        }
        for (Neo3DGameObject object : objects()) {
            Neo3DPersistedObject data = data(object.getId());
            if (data.parentName == null) continue;
            Neo3DGameObject parent = scene.findByName(data.parentName);
            if (parent != null && persistedById.containsKey(parent.getId())) {
                scene.setParent(object.getId(), parent.getId());
            }
        }
        if (cameraId == null) ensureViewportCamera();
        engine.syncScene(scene.getId());
        lastHistorySnapshot = snapshot;
    }

    void dispose() {
        save();
        ioQueue.execute(engine::dispose);
        ioQueue.execute(this::discardRecovery);
        ioQueue.shutdown();
    }

    private Neo3DGameObject add(Neo3DPersistedObject data) {
        data.name = uniqueObjectName(data.name);
        Neo3DGameObject object = scene.createObject(data.name);
        persistedById.put(object.getId(), data);
        projectScene.getNeo3DObjects().add(data);
        applyDataToRuntime(data, object);
        if (data.camera) cameraId = object.getId();
        select(object.getId());
        saveDebounced();
        return object;
    }

    private void loadObjects() {
        for (Neo3DPersistedObject data : projectScene.getNeo3DObjects()) {
            if (data == null) continue;
            data.name = uniqueObjectName(data.name);
            Neo3DGameObject object = scene.createObject(data.name);
            persistedById.put(object.getId(), data);
            applyDataToRuntime(data, object);
            engine.syncObject(scene.getId(), object.getId());
            if (data.camera && data.cameraMain) cameraId = object.getId();
        }
        for (Neo3DGameObject object : scene.getAllObjects()) {
            Neo3DPersistedObject data = persistedById.get(object.getId());
            if (data == null || data.parentName == null) continue;
            Neo3DGameObject parent = scene.findByName(data.parentName);
            if (parent != null) scene.setParent(object.getId(), parent.getId());
        }
        boolean mainCameraFound = false;
        for (Neo3DGameObject object : scene.getAllObjects()) {
            Neo3DPersistedObject data = persistedById.get(object.getId());
            if (data == null || !data.camera) continue;
            if (data.cameraMain && !mainCameraFound) {
                mainCameraFound = true;
                cameraId = object.getId();
            } else if (data.cameraMain) {
                data.cameraMain = false;
                object.getCamera().setMainCamera(false);
            }
        }
    }

    private String gridObjectId;
    private boolean gridVisible = true;

    boolean isGridVisible() { return gridVisible; }

    void setGridVisible(boolean visible) {
        if (gridVisible == visible && scene.getObject(gridObjectId) != null) return;
        gridVisible = visible;
        if (visible) {
            ensureEditorGrid();
        } else if (gridObjectId != null) {
            engine.removeObject(scene.getId(), gridObjectId);
        }
        engine.syncScene(scene.getId());
    }

    private void ensureEditorGrid() {
        Neo3DGameObject existing = gridObjectId == null ? null : scene.getObject(gridObjectId);
        if (existing != null) {
            gridVisible = true;
            return;
        }
        try {
            byte[] gridBytes = Neo3DPrimitiveMeshes.buildGrid(
                    20f, 1f, 0.02f, new float[]{0.45f, 0.55f, 0.65f, 1f});
            Neo3DGameObject grid = scene.createObject("__editor_grid");
            gridObjectId = grid.getId();
            engine.syncObject(scene.getId(), gridObjectId);
            engine.setObjectModelBytes(scene.getId(), gridObjectId,
                    Neo3DPrimitiveMeshes.ASSET_KEY_EDITOR_GRID, gridBytes);
            gridVisible = true;
        } catch (RuntimeException e) {
            gridObjectId = null;
            gridVisible = false;
        }
    }

    private void ensureViewportCamera() {
        if (cameraId != null) return;
        Neo3DGameObject camera = scene.createObject("__editor_camera");
        camera.setCamera(new Neo3DCamera());
        camera.getTransform().setPosition(0f, 0f, 6f);
        camera.getCamera().setUseTransformOrientation(true);
        cameraId = camera.getId();
        engine.syncObject(scene.getId(), cameraId);
    }

    private void applyDataToRuntime(Neo3DPersistedObject data, Neo3DGameObject object) {
        object.setActive(data.active);
        object.setVisible(data.visible);
        object.getTransform().setPosition(data.x, data.y, data.z);
        object.getTransform().setScale(data.scaleX, data.scaleY, data.scaleZ);
        object.getTransform().setRotationEulerDeg(data.yawDeg, data.pitchDeg, data.rollDeg);
        if (data.camera) {
            Neo3DCamera camera = new Neo3DCamera();
            camera.setMainCamera(data.cameraMain);
            camera.setUseTransformOrientation(data.cameraUsesTransformOrientation);
            camera.setFovDeg(data.cameraFov); camera.setNear(data.cameraNear);
            camera.setFar(data.cameraFar);
            camera.setExposure(data.cameraAperture, data.cameraShutterSpeed,
                    data.cameraSensitivity);
            camera.setLookAtTarget(data.cameraTargetX, data.cameraTargetY, data.cameraTargetZ);
            object.setCamera(camera);
        }
        if (data.lightType != null) {
            Neo3DLight.Type type;
            try {
                type = Neo3DLight.Type.valueOf(data.lightType);
            } catch (IllegalArgumentException ignored) {
                type = Neo3DLight.Type.POINT;
                data.lightType = type.name();
            }
            Neo3DLight light = new Neo3DLight(type, data.lightIntensity);
            light.setColor(data.lightRed, data.lightGreen, data.lightBlue);
            light.setRange(data.lightRange); light.setCastShadows(data.lightShadows);
            light.setDirection(data.lightDirectionX, data.lightDirectionY,
                    data.lightDirectionZ);
            light.setConeDeg(data.lightInnerConeDeg, data.lightOuterConeDeg);
            object.setLight(light);
        }
        applyMaterial(object, data);
        applyPhysics(object, data);
        loadModel(object, data);
    }

    private void applyMaterial(Neo3DGameObject object, Neo3DPersistedObject data) {
        object.setCustomMaterial(Neo3DCustomMaterial.pbrFactors(
                new float[]{data.colorRed, data.colorGreen, data.colorBlue, data.colorAlpha},
                data.metallic, data.roughness,
                new float[]{data.emissiveRed, data.emissiveGreen, data.emissiveBlue}));
    }

    private void applyPhysics(Neo3DGameObject object, Neo3DPersistedObject data) {
        if (data.physicsMotionType < 0) {
            object.setPhysicsBody(null);
            return;
        }
        Neo3DPhysicsBody body = new Neo3DPhysicsBody(
                enumAt(Neo3DPhysicsBody.MotionType.values(), data.physicsMotionType,
                        Neo3DPhysicsBody.MotionType.NONE),
                enumAt(Neo3DPhysicsBody.ShapeType.values(), data.physicsShapeType,
                        Neo3DPhysicsBody.ShapeType.AUTO), data.physicsMass);
        body.setFriction(data.physicsFriction); body.setRestitution(data.physicsRestitution);
        body.setGravityFactor(data.physicsGravityFactor);
        body.setLinearDamping(data.physicsLinearDamping);
        body.setAngularDamping(data.physicsAngularDamping);
        body.setContinuousCollision(data.physicsContinuousCollision);
        body.setShapeScaleX(data.physicsShapeScaleX);
        body.setShapeScaleY(data.physicsShapeScaleY);
        body.setShapeScaleZ(data.physicsShapeScaleZ);
        object.setPhysicsBody(body);
    }

    private void loadModel(Neo3DGameObject object, Neo3DPersistedObject data) {
        String key = data.modelAssetKey;
        if (key != null && !key.isEmpty()) {
            String assetKey = key;
            String objectId = object.getId();
            ioQueue.execute(() -> {
                try {
                    byte[] modelBytes = Files.readAllBytes(project.getFile(assetKey).toPath());
                    Map<String, byte[]> resources = modelResources(assetKey, modelBytes);
                    if (assetKey.toLowerCase(java.util.Locale.ROOT).endsWith(".obj")) {
                        modelBytes = Neo3DPrimitiveMeshes.convertObjToGlb(modelBytes,
                                new float[]{data.colorRed, data.colorGreen,
                                        data.colorBlue, data.colorAlpha});
                    } else if (assetKey.toLowerCase(java.util.Locale.ROOT).endsWith(".stl")) {
                        modelBytes = Neo3DPrimitiveMeshes.convertStlToGlb(modelBytes,
                                new float[]{data.colorRed, data.colorGreen,
                                        data.colorBlue, data.colorAlpha});
                    }
                    if (scene.getObject(objectId) != null) {
                        engine.setObjectModelBytes(scene.getId(), objectId, assetKey, modelBytes,
                                resources);
                    }
                } catch (IOException | JSONException | IllegalArgumentException e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (modelLoadFailureListener != null) modelLoadFailureListener.run();
                    });
                } catch (OutOfMemoryError e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (modelLoadFailureListener != null) modelLoadFailureListener.run();
                    });
                }
            });
            return;
        }
        byte[] bytes = primitiveModel(data);
        if (bytes == null) return;
        key = primitiveKey(data.primitiveKind);
        engine.setObjectModelBytes(scene.getId(), object.getId(), key, bytes);
    }

    private Map<String, byte[]> modelResources(String modelKey, byte[] modelBytes)
            throws IOException, JSONException {
        if (!modelKey.toLowerCase(java.util.Locale.ROOT).endsWith(".gltf")) {
            return java.util.Collections.emptyMap();
        }
        JSONObject document = new JSONObject(new String(modelBytes, StandardCharsets.UTF_8));
        JSONArray sections = new JSONArray();
        sections.put(document.optJSONArray("buffers"));
        sections.put(document.optJSONArray("images"));
        Map<String, byte[]> resources = new LinkedHashMap<>();
        File modelDirectory = project.getFile(modelKey).getParentFile();
        String root = modelDirectory.getCanonicalPath() + File.separator;
        for (int section = 0; section < sections.length(); section++) {
            JSONArray entries = sections.optJSONArray(section);
            if (entries == null) continue;
            for (int i = 0; i < entries.length(); i++) {
                String uri = entries.getJSONObject(i).optString("uri", "");
                if (uri.isEmpty()) continue;
                if (uri.startsWith("data:")) {
                    int comma = uri.indexOf(',');
                    if (comma < 0 || !uri.substring(0, comma).endsWith(";base64")) {
                        throw new IOException("Unsupported embedded glTF resource");
                    }
                    resources.put(uri, Base64.decode(uri.substring(comma + 1), Base64.DEFAULT));
                    continue;
                }
                String relative = Uri.decode(uri);
                if (relative.startsWith("/") || relative.contains("\\")
                        || relative.contains(":") || relative.contains("..")) {
                    throw new IOException("Invalid glTF resource path");
                }
                File resource = new File(modelDirectory, relative).getCanonicalFile();
                if (!resource.getPath().startsWith(root) || !resource.isFile()) {
                    throw new IOException("Missing glTF resource: " + relative);
                }
                resources.put(uri, Files.readAllBytes(resource.toPath()));
            }
        }
        return resources;
    }

    private byte[] primitiveModel(Neo3DPersistedObject data) {
        float[] color = {data.colorRed, data.colorGreen, data.colorBlue, data.colorAlpha};
        if (data.primitiveKind == 0) return Neo3DPrimitiveMeshes.buildCube(1f, color);
        if (data.primitiveKind == 1) return Neo3DPrimitiveMeshes.buildSphere(0.5f, 20, 32, color);
        if (data.primitiveKind == 2) return Neo3DPrimitiveMeshes.buildCylinder(0.5f, 1f, 32, color);
        return null;
    }

    private void copyRuntimeToData(Neo3DGameObject object, Neo3DPersistedObject data) {
        data.name = object.getName();
        float[] position = object.getTransform().getPosition();
        float[] scale = object.getTransform().getScale();
        float[] rotation = object.getTransform().getEulerDeg();
        data.x = position[0]; data.y = position[1]; data.z = position[2];
        data.scaleX = scale[0]; data.scaleY = scale[1]; data.scaleZ = scale[2];
        data.yawDeg = rotation[0]; data.pitchDeg = rotation[1]; data.rollDeg = rotation[2];
        data.parentName = engine.getObjectParentName(scene.getId(), object.getId());
        data.active = object.isActive(); data.visible = object.isVisible();
        if (object.getCamera() != null) {
            Neo3DCamera camera = object.getCamera();
            data.cameraMain = camera.isMainCamera();
            data.cameraUsesTransformOrientation = camera.isUseTransformOrientation();
            data.cameraFov = camera.getFovDeg(); data.cameraNear = camera.getNear();
            data.cameraFar = camera.getFar();
            data.cameraAperture = camera.getAperture();
            data.cameraShutterSpeed = camera.getShutterSpeed();
            data.cameraSensitivity = camera.getSensitivity();
            float[] target = camera.getLookAtTarget();
            data.cameraTargetX = target[0]; data.cameraTargetY = target[1];
            data.cameraTargetZ = target[2];
        }
        if (object.getLight() != null) {
            Neo3DLight light = object.getLight();
            data.lightType = light.getType().name(); data.lightIntensity = light.getIntensity();
            float[] color = light.getColor();
            data.lightRed = color[0]; data.lightGreen = color[1]; data.lightBlue = color[2];
            data.lightRange = light.getRange(); data.lightShadows = light.isCastShadows();
            float[] direction = light.getDirection();
            data.lightDirectionX = direction[0]; data.lightDirectionY = direction[1];
            data.lightDirectionZ = direction[2];
            data.lightInnerConeDeg = light.getInnerConeDeg();
            data.lightOuterConeDeg = light.getOuterConeDeg();
        }
    }

    private String typeName(String type) {
        if ("SPHERE".equals(type)) return "Sphere";
        if ("CYLINDER".equals(type)) return "Cylinder";
        return "Cube";
    }

    private String uniqueObjectName(String requestedName) {
        String base = requestedName == null || requestedName.trim().isEmpty()
                ? "Object" : requestedName.trim();
        String candidate = base;
        int suffix = 2;
        while (scene.findByName(candidate) != null) {
            candidate = base + " (" + suffix++ + ")";
        }
        return candidate;
    }

    private String primitiveKey(int kind) {
        if (kind == 1) return Neo3DPrimitiveMeshes.ASSET_KEY_SPHERE;
        if (kind == 2) return Neo3DPrimitiveMeshes.ASSET_KEY_CYLINDER;
        return Neo3DPrimitiveMeshes.ASSET_KEY_CUBE;
    }

    private int cameraCount() {
        int count = 0;
        for (Neo3DPersistedObject data : persistedById.values()) {
            if (data.camera) count++;
        }
        return count;
    }

    private static <T> T enumAt(T[] values, int index, T fallback) {
        return index >= 0 && index < values.length ? values[index] : fallback;
    }
}
