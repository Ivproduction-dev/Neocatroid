package org.catrobat.catroid.neo3d.backend;

import android.content.Context;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import com.google.android.filament.Camera;
import com.google.android.filament.Engine;
import com.google.android.filament.EntityManager;
import com.google.android.filament.IndirectLight;
import com.google.android.filament.LightManager;
import com.google.android.filament.MaterialInstance;
import com.google.android.filament.Renderer;
import com.google.android.filament.Scene;
import com.google.android.filament.Skybox;
import com.google.android.filament.SwapChain;
import com.google.android.filament.TransformManager;
import com.google.android.filament.View;
import com.google.android.filament.Viewport;
import com.google.android.filament.android.UiHelper;
import com.google.android.filament.gltfio.Animator;
import com.google.android.filament.gltfio.AssetLoader;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.android.filament.gltfio.FilamentInstance;
import com.google.android.filament.gltfio.Gltfio;
import com.google.android.filament.gltfio.ResourceLoader;
import com.google.android.filament.gltfio.UbershaderProvider;

import org.catrobat.catroid.neo3d.Neo3DCamera;
import org.catrobat.catroid.neo3d.Neo3DCustomMaterial;
import org.catrobat.catroid.neo3d.Neo3DGameObject;
import org.catrobat.catroid.neo3d.Neo3DLight;
import org.catrobat.catroid.neo3d.Neo3DMath;
import org.catrobat.catroid.neo3d.Neo3DScene;
import org.catrobat.catroid.neo3d.Neo3DShadowSettings;
import org.catrobat.catroid.neo3d.Neo3DSkybox;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Neo3DFilamentBackend implements INeo3DBackend {

    private static final String TAG = "Neo3D-V2";

    private Engine engine;
    private Renderer renderer;
    private AssetLoader assetLoader;
    private UbershaderProvider materialProvider;
    private ResourceLoader resourceLoader;
    private UiHelper uiHelper;
    private SwapChain swapChain;
    private boolean initialized;
    private boolean paused;
    private boolean gltfioReady;
    private int viewWidth = 1;
    private int viewHeight = 1;
    private String activeSceneId;
    private final BackendStats stats = new BackendStats();

    private final Map<String, Neo3DScene> scenes = new HashMap<>();
    private final Map<String, FilamentScene> nativeScenes = new HashMap<>();

    private static class FilamentScene {
        Scene scene;
        View view;
        Camera camera;
        int cameraEntity;
        Skybox skybox;
        IndirectLight ibl;
        final Map<String, Integer> lightEntities = new HashMap<>();
        final Map<String, ModelEntry> models = new HashMap<>();
    }

    private static class ModelEntry {
        FilamentAsset asset;
        ByteBuffer sourceBuffer;
        boolean addedToScene;
    }

    @Override
    public String getName() {
        return "Filament";
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("com.google.android.filament.Engine");
            return true;
        } catch (Throwable t) {
            Log.w(TAG, "Filament classes not available: " + t.getMessage());
            return false;
        }
    }

    @Override
    public void initialize(Context context) {
        if (initialized) {
            return;
        }
        try {
            Gltfio.init();
            gltfioReady = true;
        } catch (Throwable t) {
            Log.e(TAG, "Gltfio.init() failed, model loading disabled", t);
            gltfioReady = false;
        }
        try {
            engine = Engine.create();
            renderer = engine.createRenderer();
            materialProvider = new UbershaderProvider(engine);
            assetLoader = new AssetLoader(engine, materialProvider, EntityManager.get());
            resourceLoader = new ResourceLoader(engine);
            uiHelper = new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);
            uiHelper.setRenderCallback(new UiHelper.RendererCallback() {
                @Override
                public void onNativeWindowChanged(Surface surface) {
                    try {
                        if (swapChain != null) {
                            engine.destroySwapChain(swapChain);
                        }
                        swapChain = engine.createSwapChain(surface, uiHelper.getSwapChainFlags());
                    } catch (Throwable t) {
                        Log.e(TAG, "createSwapChain failed", t);
                    }
                }

                @Override
                public void onDetachedFromSurface() {
                    try {
                        if (swapChain != null) {
                            engine.destroySwapChain(swapChain);
                            engine.flushAndWait();
                            swapChain = null;
                        }
                    } catch (Throwable t) {
                        Log.e(TAG, "destroySwapChain failed", t);
                    }
                }

                @Override
                public void onResized(int width, int height) {
                    resize(width, height);
                }
            });
            initialized = true;
            Log.i(TAG, "Filament backend initialized, backend=" + engine.getBackend());
        } catch (Throwable t) {
            Log.e(TAG, "Filament Engine.create() failed", t);
            initialized = false;
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized && engine != null;
    }

    public void attachSurfaceView(SurfaceView surfaceView) {
        if (uiHelper != null && surfaceView != null) {
            uiHelper.attachTo(surfaceView);
        }
    }

    public void detachSurfaceView() {
        if (uiHelper != null) {
            try {
                uiHelper.detach();
            } catch (Throwable t) {
                Log.e(TAG, "uiHelper.detach failed", t);
            }
        }
    }

    @Override
    public void loadScene(Neo3DScene scene) {
        requireInit();
        if (nativeScenes.containsKey(scene.getId())) {
            return;
        }
        try {
            FilamentScene fs = new FilamentScene();
            fs.scene = engine.createScene();
            fs.view = engine.createView();
            fs.view.setViewport(new Viewport(0, 0, viewWidth, viewHeight));
            fs.cameraEntity = EntityManager.get().create();
            fs.camera = engine.createCamera(fs.cameraEntity);
            fs.view.setScene(fs.scene);
            fs.view.setCamera(fs.camera);
            applyShadowSettings(fs, scene.getShadowSettings());
            applySkyboxNative(fs, scene.getSkybox());
            scenes.put(scene.getId(), scene);
            nativeScenes.put(scene.getId(), fs);
            activeSceneId = scene.getId();
            stats.liveScenes = nativeScenes.size();
            for (Neo3DGameObject obj : scene.getAllObjects()) {
                syncObject(scene.getId(), obj);
            }
            Log.i(TAG, "Scene loaded: " + scene.getName() + " (" + scene.getObjectCount()
                    + " objects)");
        } catch (Throwable t) {
            Log.e(TAG, "loadScene failed: " + scene.getName(), t);
        }
    }

    @Override
    public void unloadScene(String sceneId) {
        FilamentScene fs = nativeScenes.remove(sceneId);
        scenes.remove(sceneId);
        if (fs == null) {
            return;
        }
        try {
            for (ModelEntry entry : fs.models.values()) {
                destroyModelEntry(fs, entry);
            }
            fs.models.clear();
            for (int lightEntity : fs.lightEntities.values()) {
                destroyEntitySafe(lightEntity);
            }
            fs.lightEntities.clear();
            if (fs.ibl != null) {
                engine.destroyIndirectLight(fs.ibl);
                fs.ibl = null;
            }
            if (fs.skybox != null) {
                engine.destroySkybox(fs.skybox);
                fs.skybox = null;
            }
            engine.destroyCameraComponent(fs.cameraEntity);
            EntityManager.get().destroy(fs.cameraEntity);
            engine.destroyView(fs.view);
            engine.destroyScene(fs.scene);
        } catch (Throwable t) {
            Log.e(TAG, "unloadScene failed: " + sceneId, t);
        }
        if (sceneId.equals(activeSceneId)) {
            activeSceneId = nativeScenes.isEmpty() ? null : nativeScenes.keySet().iterator().next();
        }
        stats.liveScenes = nativeScenes.size();
        stats.liveObjects = countLiveObjects();
    }

    @Override
    public void syncObject(String sceneId, Neo3DGameObject obj) {
        FilamentScene fs = nativeScenes.get(sceneId);
        if (fs == null || obj == null) {
            return;
        }
        try {
            syncTransform(fs, obj);
            syncLight(sceneId, fs, obj);
            syncCamera(fs, obj);
            applyCustomMaterial(fs, obj);
            syncVisibility(fs, obj);
        } catch (Throwable t) {
            Log.e(TAG, "syncObject failed: " + obj.getName(), t);
        }
        stats.liveObjects = countLiveObjects();
    }

    private void syncTransform(FilamentScene fs, Neo3DGameObject obj) {
        ModelEntry entry = fs.models.get(obj.getId());
        if (entry == null || entry.asset == null) {
            return;
        }
        try {
            float[] world = obj.getTransform().getWorldMatrix();
            TransformManager tm = engine.getTransformManager();
            int root = entry.asset.getRoot();
            tm.setTransform(tm.getInstance(root), world);
        } catch (Throwable t) {
            Log.e(TAG, "syncTransform failed: " + obj.getName(), t);
        }
    }

    private void syncLight(String sceneId, FilamentScene fs, Neo3DGameObject obj) {
        Integer oldEntity = fs.lightEntities.remove(obj.getId());
        if (oldEntity != null) {
            destroyEntitySafe(oldEntity);
        }
        Neo3DLight light = obj.getLight();
        if (light == null || !obj.isActive()) {
            return;
        }
        try {
            LightManager.Builder builder;
            switch (light.getType()) {
                case POINT:
                    builder = new LightManager.Builder(LightManager.Type.POINT);
                    break;
                case SPOT:
                    builder = new LightManager.Builder(LightManager.Type.SPOT);
                    break;
                case DIRECTIONAL:
                default:
                    builder = new LightManager.Builder(LightManager.Type.DIRECTIONAL);
                    break;
            }
            float[] color = light.getColor();
            builder.color(color[0], color[1], color[2]);
            builder.intensity(light.getIntensity());
            builder.castShadows(light.isCastShadows());
            if (light.getType() == Neo3DLight.Type.POINT
                    || light.getType() == Neo3DLight.Type.SPOT) {
                float[] world = obj.getTransform().getWorldMatrix();
                builder.position(world[12], world[13], world[14]);
                if (light.getRange() > 0f) {
                    builder.falloff(light.getRange());
                }
            }
            if (light.getType() == Neo3DLight.Type.DIRECTIONAL
                    || light.getType() == Neo3DLight.Type.SPOT) {
                float[] world = obj.getTransform().getWorldMatrix();
                float[] dir = light.getDirection();
                float dx = world[0] * dir[0] + world[4] * dir[1] + world[8] * dir[2];
                float dy = world[1] * dir[0] + world[5] * dir[1] + world[9] * dir[2];
                float dz = world[2] * dir[0] + world[6] * dir[1] + world[10] * dir[2];
                float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (len > 1e-8f) {
                    builder.direction(dx / len, dy / len, dz / len);
                } else {
                    builder.direction(dir[0], dir[1], dir[2]);
                }
            }
            if (light.getType() == Neo3DLight.Type.SPOT) {
                builder.spotLightCone(
                        Neo3DMath.degToRad(light.getInnerConeDeg()),
                        Neo3DMath.degToRad(light.getOuterConeDeg()));
            }
            if (light.isCastShadows()) {
                Neo3DScene scene = scenes.get(sceneId);
                LightManager.ShadowOptions options = new LightManager.ShadowOptions();
                if (scene != null) {
                    options.mapSize = scene.getShadowSettings().getMapSize();
                    options.constantBias = scene.getShadowSettings().getBias();
                    options.normalBias = scene.getShadowSettings().getNormalBias();
                }
                builder.shadowOptions(options);
            }
            int entity = EntityManager.get().create();
            builder.build(engine, entity);
            fs.scene.addEntity(entity);
            fs.lightEntities.put(obj.getId(), entity);
        } catch (Throwable t) {
            Log.e(TAG, "syncLight failed: " + obj.getName(), t);
        }
    }

    private void syncCamera(FilamentScene fs, Neo3DGameObject obj) {
        Neo3DCamera cam = obj.getCamera();
        if (cam == null || !cam.isMainCamera() || !obj.isActive()) {
            return;
        }
        try {
            TransformManager tm = engine.getTransformManager();
            if (tm.getInstance(fs.cameraEntity) == 0) {
                tm.create(fs.cameraEntity);
            }
            float[] world = obj.getTransform().getWorldMatrix();
            float[] target = cam.getLookAtTarget();
            float[] up = cam.getUp();
            fs.camera.lookAt(world[12], world[13], world[14],
                    target[0], target[1], target[2],
                    up[0], up[1], up[2]);
            float aspect = viewHeight > 0 ? (float) viewWidth / viewHeight : 1f;
            fs.camera.setProjection(cam.getFovDeg(), aspect, cam.getNear(), cam.getFar(),
                    Camera.Fov.VERTICAL);
            fs.camera.setExposure(cam.getAperture(), cam.getShutterSpeed(), cam.getSensitivity());
        } catch (Throwable t) {
            Log.e(TAG, "syncCamera failed: " + obj.getName(), t);
        }
    }

    private void syncVisibility(FilamentScene fs, Neo3DGameObject obj) {
        ModelEntry entry = fs.models.get(obj.getId());
        if (entry == null || entry.asset == null) {
            return;
        }
        boolean shouldShow = obj.isVisible() && obj.isActive();
        if (shouldShow && !entry.addedToScene) {
            fs.scene.addEntities(entry.asset.getEntities());
            entry.addedToScene = true;
        } else if (!shouldShow && entry.addedToScene) {
            fs.scene.removeEntities(entry.asset.getEntities());
            entry.addedToScene = false;
        }
    }

    @Override
    public void removeObject(String sceneId, String objectId) {
        FilamentScene fs = nativeScenes.get(sceneId);
        if (fs == null) {
            return;
        }
        try {
            ModelEntry entry = fs.models.remove(objectId);
            if (entry != null) {
                destroyModelEntry(fs, entry);
            }
            Integer lightEntity = fs.lightEntities.remove(objectId);
            if (lightEntity != null) {
                destroyEntitySafe(lightEntity);
            }
        } catch (Throwable t) {
            Log.e(TAG, "removeObject failed: " + objectId, t);
        }
        stats.liveObjects = countLiveObjects();
    }

    private void destroyModelEntry(FilamentScene fs, ModelEntry entry) {
        if (entry == null || entry.asset == null) {
            return;
        }
        try {
            if (entry.addedToScene) {
                fs.scene.removeEntities(entry.asset.getEntities());
            }
            assetLoader.destroyAsset(entry.asset);
        } catch (Throwable t) {
            Log.e(TAG, "destroyModelEntry failed", t);
        }
        entry.asset = null;
        entry.sourceBuffer = null;
    }

    private void destroyEntitySafe(int entity) {
        try {
            engine.destroyEntity(entity);
            EntityManager.get().destroy(entity);
        } catch (Throwable t) {
            Log.e(TAG, "destroyEntity failed: " + entity, t);
        }
    }

    @Override
    public void loadModelBytes(String sceneId, String objectId, byte[] data, String sourceName) {
        FilamentScene fs = nativeScenes.get(sceneId);
        Neo3DScene scene = scenes.get(sceneId);
        if (fs == null || scene == null) {
            Log.w(TAG, "loadModelBytes: unknown scene " + sceneId);
            return;
        }
        if (!gltfioReady || assetLoader == null) {
            Log.w(TAG, "loadModelBytes: gltfio not ready, skipping " + sourceName);
            return;
        }
        if (data == null || data.length < 20) {
            Log.w(TAG, "loadModelBytes: empty data for " + sourceName + ", skipping");
            return;
        }
        try {
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
            buffer.put(data);
            buffer.flip();
            FilamentAsset asset = assetLoader.createAsset(buffer);
            if (asset == null) {
                Log.w(TAG, "createAsset returned null for " + sourceName
                        + " (unsupported or corrupt glTF?)");
                return;
            }
            ModelEntry old = fs.models.remove(objectId);
            if (old != null) {
                destroyModelEntry(fs, old);
            }
            resourceLoader.loadResources(asset);
            asset.releaseSourceData();
            ModelEntry entry = new ModelEntry();
            entry.asset = asset;
            entry.addedToScene = false;
            fs.models.put(objectId, entry);
            Neo3DGameObject obj = scene.getObject(objectId);
            if (obj != null) {
                logAnimationInfo(obj, asset);
                syncTransform(fs, obj);
                applyCustomMaterial(fs, obj);
                syncVisibility(fs, obj);
            } else {
                fs.scene.addEntities(asset.getEntities());
                entry.addedToScene = true;
            }
            Log.i(TAG, "Model loaded: " + sourceName + " (" + data.length + " bytes, "
                    + asset.getEntities().length + " entities)");
        } catch (Throwable t) {
            Log.e(TAG, "loadModelBytes failed: " + sourceName, t);
        }
        stats.liveObjects = countLiveObjects();
    }

    private void logAnimationInfo(Neo3DGameObject obj, FilamentAsset asset) {
        try {
            Animator animator = asset.getInstance().getAnimator();
            int count = animator.getAnimationCount();
            if (count == 0) {
                return;
            }
            StringBuilder names = new StringBuilder();
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    names.append(", ");
                }
                names.append(animator.getAnimationName(i))
                        .append("(").append(animator.getAnimationDuration(i)).append("s)");
            }
            Log.i(TAG, "Skeletal animations for " + obj.getName() + ": " + names);
        } catch (Throwable t) {
            Log.w(TAG, "animation introspection failed: " + obj.getName(), t);
        }
    }

    private void applyCustomMaterial(FilamentScene fs, Neo3DGameObject obj) {
        Neo3DCustomMaterial custom = obj.getCustomMaterial();
        ModelEntry entry = fs.models.get(obj.getId());
        if (custom == null || entry == null || entry.asset == null) {
            return;
        }
        try {
            MaterialInstance[] instances = entry.asset.getInstance().getMaterialInstances();
            for (MaterialInstance mi : instances) {
                for (Map.Entry<String, Float> e : custom.getFloatParams().entrySet()) {
                    try {
                        mi.setParameter(e.getKey(), e.getValue());
                    } catch (Throwable ignored) {
                        Log.w(TAG, "Unknown float param '" + e.getKey() + "' (best-effort)");
                    }
                }
                for (Map.Entry<String, float[]> e : custom.getVecParams().entrySet()) {
                    float[] v = e.getValue();
                    try {
                        if (v.length >= 4) {
                            mi.setParameter(e.getKey(), v[0], v[1], v[2], v[3]);
                        } else if (v.length == 3) {
                            mi.setParameter(e.getKey(), v[0], v[1], v[2]);
                        }
                    } catch (Throwable ignored) {
                        Log.w(TAG, "Unknown vec param '" + e.getKey() + "' (best-effort)");
                    }
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "applyCustomMaterial failed: " + obj.getName(), t);
        }
    }

    @Override
    public void applySkybox(String sceneId, Neo3DSkybox skybox) {
        FilamentScene fs = nativeScenes.get(sceneId);
        if (fs == null || skybox == null) {
            return;
        }
        try {
            applySkyboxNative(fs, skybox);
        } catch (Throwable t) {
            Log.e(TAG, "applySkybox failed", t);
        }
    }

    private void applySkyboxNative(FilamentScene fs, Neo3DSkybox skybox) {
        if (fs.skybox != null) {
            engine.destroySkybox(fs.skybox);
            fs.skybox = null;
        }
        if (fs.ibl != null) {
            engine.destroyIndirectLight(fs.ibl);
            fs.ibl = null;
        }
        float[] color = skybox.getColorA();
        switch (skybox.getMode()) {
            case KTX_FILE:
                Log.w(TAG, "KTX_FILE skybox not implemented in prototype (needs KTX1Loader); "
                        + "using solid color fallback: " + skybox.getKtxPath());
                break;
            case COLOR:
            default:
                break;
        }
        fs.skybox = new Skybox.Builder().color(color[0], color[1], color[2], color[3]).build(engine);
        Log.i(TAG, "skybox applied, valid=" + engine.isValidSkybox(fs.skybox));
        fs.scene.setSkybox(fs.skybox);
        float grey = 1f;
        float[] bands = new float[27];
        bands[0] = grey;
        bands[1] = grey;
        bands[2] = grey;
        fs.ibl = new IndirectLight.Builder()
                .irradiance(3, bands)
                .intensity(skybox.getIblIntensity())
                .build(engine);
        fs.scene.setIndirectLight(fs.ibl);
    }

    private void applyShadowSettings(FilamentScene fs, Neo3DShadowSettings settings) {
        fs.view.setShadowingEnabled(settings.isEnabled());
        fs.view.setPostProcessingEnabled(true);
    }

    @Override
    public void tickAnimations(String sceneId, float deltaSec) {
        FilamentScene fs = nativeScenes.get(sceneId);
        Neo3DScene scene = scenes.get(sceneId);
        if (fs == null || scene == null) {
            return;
        }
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getAnimationState() == null || !obj.getAnimationState().isPlaying()) {
                continue;
            }
            ModelEntry entry = fs.models.get(obj.getId());
            if (entry == null || entry.asset == null) {
                continue;
            }
            try {
                Animator animator = entry.asset.getInstance().getAnimator();
                if (animator.getAnimationCount() == 0) {
                    continue;
                }
                int index = resolveAnimationIndex(animator, obj.getAnimationState().getClipName());
                animator.applyAnimation(index, obj.getAnimationState().getTimeSec());
                animator.updateBoneMatrices();
            } catch (Throwable t) {
                Log.e(TAG, "tickAnimations failed: " + obj.getName(), t);
            }
        }
    }

    private int resolveAnimationIndex(Animator animator, String clipName) {
        if (clipName != null) {
            try {
                int count = animator.getAnimationCount();
                for (int i = 0; i < count; i++) {
                    if (clipName.equals(animator.getAnimationName(i))) {
                        return i;
                    }
                }
            } catch (Throwable ignored) {
            }
        }
        return 0;
    }

    @Override
    public void resize(int width, int height) {
        viewWidth = Math.max(1, width);
        viewHeight = Math.max(1, height);
        Viewport viewport = new Viewport(0, 0, viewWidth, viewHeight);
        for (FilamentScene fs : nativeScenes.values()) {
            if (fs.view != null) {
                try {
                    fs.view.setViewport(viewport);
                } catch (Throwable t) {
                    Log.e(TAG, "resize: setViewport failed", t);
                }
            }
        }
        resyncActiveCamera();
    }

    private void resyncActiveCamera() {
        if (activeSceneId == null) {
            return;
        }
        FilamentScene fs = nativeScenes.get(activeSceneId);
        Neo3DScene scene = scenes.get(activeSceneId);
        if (fs == null || scene == null) {
            return;
        }
        for (Neo3DGameObject obj : scene.getAllObjects()) {
            if (obj.getCamera() != null && obj.getCamera().isMainCamera() && obj.isActive()) {
                syncCamera(fs, obj);
                break;
            }
        }
    }

    @Override
    public void renderFrame(float deltaSec, long frameTimeNanos) {
        if (!isInitialized() || paused) {
            return;
        }
        FilamentScene fs = activeSceneId == null ? null : nativeScenes.get(activeSceneId);
        if (fs == null || swapChain == null || uiHelper == null || !uiHelper.isReadyToRender()) {
            return;
        }
        long frameStartNs = System.nanoTime();
        try {
            if (renderer.beginFrame(swapChain, frameStartNs)) {
                renderer.render(fs.view);
                renderer.endFrame();
                stats.framesRendered++;
            }
        } catch (Throwable t) {
            Log.e(TAG, "renderFrame failed", t);
        }
        float frameMs = (System.nanoTime() - frameStartNs) / 1000000f;
        stats.lastFrameMs = frameMs;
        if (stats.avgFrameMs == 0f) {
            stats.avgFrameMs = frameMs;
        } else {
            stats.avgFrameMs = stats.avgFrameMs * 0.95f + frameMs * 0.05f;
        }
    }

    @Override
    public void onResume() {
        paused = false;
    }

    @Override
    public void onPause() {
        paused = true;
    }

    @Override
    public BackendStats getStats() {
        stats.liveScenes = nativeScenes.size();
        stats.liveObjects = countLiveObjects();
        stats.detail = viewWidth + "x" + viewHeight + (paused ? " paused" : "");
        return stats;
    }

    @Override
    public synchronized int getLoadedModelCount() {
        int n = 0;
        for (FilamentScene fs : nativeScenes.values()) {
            for (ModelEntry entry : fs.models.values()) {
                if (entry != null && entry.asset != null) {
                    n++;
                }
            }
        }
        return n;
    }

    @Override
    public void dispose() {
        try {
            for (String sceneId : new java.util.ArrayList<>(nativeScenes.keySet())) {
                unloadScene(sceneId);
            }
        } catch (Throwable t) {
            Log.e(TAG, "dispose: unloadScenes failed", t);
        }
        try {
            detachSurfaceView();
        } catch (Throwable ignored) {
        }
        if (resourceLoader != null) {
            try {
                resourceLoader.destroy();
            } catch (Throwable t) {
                Log.e(TAG, "resourceLoader.destroy failed", t);
            }
            resourceLoader = null;
        }
        if (assetLoader != null) {
            try {
                assetLoader.destroy();
            } catch (Throwable t) {
                Log.e(TAG, "assetLoader.destroy failed", t);
            }
            assetLoader = null;
        }
        if (materialProvider != null) {
            try {
                materialProvider.destroyMaterials();
                materialProvider.destroy();
            } catch (Throwable t) {
                Log.e(TAG, "materialProvider destroy failed", t);
            }
            materialProvider = null;
        }
        if (renderer != null && engine != null) {
            try {
                engine.destroyRenderer(renderer);
            } catch (Throwable t) {
                Log.e(TAG, "destroyRenderer failed", t);
            }
            renderer = null;
        }
        if (engine != null) {
            try {
                engine.destroy();
            } catch (Throwable t) {
                Log.e(TAG, "engine.destroy failed", t);
            }
            engine = null;
        }
        swapChain = null;
        uiHelper = null;
        initialized = false;
        Log.i(TAG, "Filament backend disposed");
    }

    private int countLiveObjects() {
        int n = 0;
        for (FilamentScene fs : nativeScenes.values()) {
            n += fs.models.size() + fs.lightEntities.size();
        }
        return n;
    }

    private void requireInit() {
        if (!isInitialized()) {
            throw new IllegalStateException("Neo3DFilamentBackend not initialized");
        }
    }
}
