package org.catrobat.catroid.twodlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LightManager2D {

    public static final int PROP_RADIUS = 0;
    public static final int PROP_INTENSITY = 1;
    public static final int PROP_COLOR = 2;
    public static final int PROP_X = 3;
    public static final int PROP_Y = 4;
    public static final int PROP_AMBIENT = 5;

    public static final int DEFAULT_MAX_ACTIVE_LIGHTS = 8;
    public static final int DEFAULT_MAX_SHADOW_LIGHTS = 2;
    public static final int DESKTOP_MAX_ACTIVE_LIGHTS = 16;
    public static final int DESKTOP_MAX_SHADOW_LIGHTS = 4;

    static final int GRID_THRESHOLD = 64;
    static final float GRID_CELL_SIZE = 256f;
    static final int RECOMPUTE_FALLBACK_FRAMES = 15;
    static final float MIN_RADIUS = 1f;

    public interface SpritePositionProvider {
        float[] getSpritePosition(String spriteName);
    }

    private final Map<String, Light2D> lights = new LinkedHashMap<>();
    private final Map<Long, List<Light2D>> grid = new HashMap<>();
    private final List<Light2D> activeCache = new ArrayList<>();
    private final List<Light2D> shadowCache = new ArrayList<>();
    private final Set<String> noShadowSprites = new HashSet<>();

    private boolean structuralDirty = true;
    private boolean orderDirty = true;
    private int lastStaticCount = -1;
    private boolean hasCamera;
    private float lastCamX;
    private float lastCamY;
    private float lastHalfW;
    private float lastHalfH;
    private int frameCounter;
    private float maxStaticRadius;

    private int maxActiveLights = DEFAULT_MAX_ACTIVE_LIGHTS;
    private int maxShadowLights = DEFAULT_MAX_SHADOW_LIGHTS;
    private float ambient = 0.15f;
    private long version;

    public Light2D createOrUpdateLight(String id, float x, float y, float radius, float intensity, int color) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Light2D light = lights.get(id);
        if (light == null) {
            light = new Light2D(id, x, y, radius, intensity, color);
            lights.put(id, light);
        } else {
            light.setPosition(x, y);
            light.setRadius(radius);
            light.setIntensity(intensity);
            light.setColor(color);
        }
        markDirty();
        return light;
    }

    public boolean removeLight(String id) {
        if (lights.remove(id) != null) {
            markDirty();
            return true;
        }
        return false;
    }

    public Light2D getLight(String id) {
        return lights.get(id);
    }

    public int getLightCount() {
        return lights.size();
    }

    public boolean setEnabled(String id, boolean enabled) {
        Light2D light = lights.get(id);
        if (light == null) {
            return false;
        }
        light.setEnabled(enabled);
        markDirty();
        return true;
    }

    public boolean setShadowsEnabled(String id, boolean shadowsEnabled) {
        Light2D light = lights.get(id);
        if (light == null) {
            return false;
        }
        light.setShadowsEnabled(shadowsEnabled);
        markDirty();
        return true;
    }

    public boolean setProperty(String id, int property, float value) {
        Light2D light = lights.get(id);
        if (light == null) {
            return false;
        }
        switch (property) {
            case PROP_RADIUS:
                light.setRadius(value);
                break;
            case PROP_INTENSITY:
                light.setIntensity(value);
                break;
            case PROP_X:
                light.setPosition(value, light.getY());
                break;
            case PROP_Y:
                light.setPosition(light.getX(), value);
                break;
            case PROP_AMBIENT:
                setAmbient(value);
                return true;
            default:
                return false;
        }
        markDirty();
        return true;
    }

    public boolean setColor(String id, int color) {
        Light2D light = lights.get(id);
        if (light == null) {
            return false;
        }
        light.setColor(color);
        markDirty();
        return true;
    }

    public boolean attachToSprite(String id, String spriteName) {
        Light2D light = lights.get(id);
        if (light == null) {
            return false;
        }
        light.setFollowSpriteName(spriteName);
        markDirty();
        return true;
    }

    public boolean detachFromSprite(String id) {
        Light2D light = lights.get(id);
        if (light == null) {
            return false;
        }
        light.setFollowSpriteName(null);
        markDirty();
        return true;
    }

    public void setSpriteShadowCasting(String spriteName, boolean castsShadow) {
        if (spriteName == null) {
            return;
        }
        if (castsShadow) {
            noShadowSprites.remove(spriteName);
        } else {
            noShadowSprites.add(spriteName);
        }
    }

    public boolean isSpriteShadowCasting(String spriteName) {
        return !noShadowSprites.contains(spriteName);
    }

    public Set<String> getNoShadowSprites() {
        return Collections.unmodifiableSet(noShadowSprites);
    }

    public void setAmbient(float ambient) {
        this.ambient = Math.max(0f, Math.min(1f, ambient));
    }

    public float getAmbient() {
        return ambient;
    }

    public void setMaxActiveLights(int max) {
        int clamped = Math.max(0, max);
        if (clamped == maxActiveLights) {
            return;
        }
        maxActiveLights = clamped;
        orderDirty = true;
    }

    public void setMaxShadowLights(int max) {
        int clamped = Math.max(0, max);
        if (clamped == maxShadowLights) {
            return;
        }
        maxShadowLights = clamped;
        orderDirty = true;
    }

    public int getMaxActiveLights() {
        return maxActiveLights;
    }

    public int getMaxShadowLights() {
        return maxShadowLights;
    }

    public boolean hasLights() {
        for (Light2D light : lights.values()) {
            if (light.isEnabled() && light.getIntensity() > 0f && light.getRadius() >= MIN_RADIUS) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        lights.clear();
        grid.clear();
        activeCache.clear();
        shadowCache.clear();
        noShadowSprites.clear();
        ambient = 0.15f;
        hasCamera = false;
        markDirty();
    }

    public long getVersion() {
        return version;
    }

    public static LightManager2D forActiveStage() {
        try {
            if (org.catrobat.catroid.stage.StageActivity.activeStageActivity == null) {
                return null;
            }
            org.catrobat.catroid.stage.StageActivity activity =
                    org.catrobat.catroid.stage.StageActivity.activeStageActivity.get();
            if (activity == null || activity.stageListener == null) {
                return null;
            }
            if (activity.stageListener.lightManager2D == null) {
                activity.stageListener.lightManager2D = new LightManager2D();
            }
            return activity.stageListener.lightManager2D;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Light2D> getActiveLights() {
        return Collections.unmodifiableList(activeCache);
    }

    public List<Light2D> getShadowLights() {
        return Collections.unmodifiableList(shadowCache);
    }

    public List<Light2D> update(float camX, float camY, float halfW, float halfH,
            SpritePositionProvider provider) {
        frameCounter++;
        resolveFollowPositions(provider);
        if (countStaticLights() != lastStaticCount) {
            structuralDirty = true;
        }

        boolean cameraMoved = !hasCamera || movedBeyondThreshold(camX, camY, halfW, halfH);
        boolean fallback = frameCounter % RECOMPUTE_FALLBACK_FRAMES == 0;
        if (!structuralDirty && !orderDirty && !cameraMoved && !fallback) {
            return activeCache;
        }
        if (structuralDirty) {
            rebuildGrid();
            structuralDirty = false;
            lastStaticCount = countStaticLightsSilent();
        }
        recomputeActive(camX, camY, halfW, halfH);
        orderDirty = false;
        lastCamX = camX;
        lastCamY = camY;
        lastHalfW = halfW;
        lastHalfH = halfH;
        hasCamera = true;
        version++;
        return activeCache;
    }

    private void markDirty() {
        structuralDirty = true;
        orderDirty = true;
    }

    private void resolveFollowPositions(SpritePositionProvider provider) {
        if (provider == null) {
            return;
        }
        for (Light2D light : lights.values()) {
            String target = light.getFollowSpriteName();
            if (target == null || target.isEmpty()) {
                continue;
            }
            float[] pos = provider.getSpritePosition(target);
            if (pos != null && pos.length >= 2) {
                light.setPosition(pos[0], pos[1]);
            }
        }
    }

    private int countStaticLights() {
        int count = 0;
        for (Light2D light : lights.values()) {
            light.trackStability();
            if (light.isStatic()) {
                count++;
            }
        }
        return count;
    }

    private boolean movedBeyondThreshold(float camX, float camY, float halfW, float halfH) {
        float dx = camX - lastCamX;
        float dy = camY - lastCamY;
        float viewRadius = (float) Math.sqrt(halfW * halfW + halfH * halfH);
        float threshold = Math.max(8f, viewRadius * 0.05f);
        if (dx * dx + dy * dy > threshold * threshold) {
            return true;
        }
        float dw = halfW - lastHalfW;
        float dh = halfH - lastHalfH;
        return dw * dw + dh * dh > threshold * threshold;
    }

    private int countStaticLightsSilent() {
        int count = 0;
        for (Light2D light : lights.values()) {
            if (light.isStatic()) {
                count++;
            }
        }
        return count;
    }

    private void rebuildGrid() {
        grid.clear();
        maxStaticRadius = 0f;
        for (Light2D light : lights.values()) {
            if (!light.isStatic() || !light.isEnabled()) {
                continue;
            }
            float radius = Math.max(light.getRadius(), MIN_RADIUS);
            if (radius > maxStaticRadius) {
                maxStaticRadius = radius;
            }
            int minCx = cellOf(light.getX() - radius);
            int maxCx = cellOf(light.getX() + radius);
            int minCy = cellOf(light.getY() - radius);
            int maxCy = cellOf(light.getY() + radius);
            for (int cx = minCx; cx <= maxCx; cx++) {
                for (int cy = minCy; cy <= maxCy; cy++) {
                    long key = cellKey(cx, cy);
                    List<Light2D> cell = grid.get(key);
                    if (cell == null) {
                        cell = new ArrayList<>();
                        grid.put(key, cell);
                    }
                    cell.add(light);
                }
            }
        }
    }

    private void recomputeActive(float camX, float camY, float halfW, float halfH) {
        activeCache.clear();
        shadowCache.clear();
        float viewRadius = (float) Math.sqrt(halfW * halfW + halfH * halfH);
        List<Light2D> candidates = collectCandidates(camX, camY, halfW, halfH, viewRadius);
        Collections.sort(candidates, new Comparator<Light2D>() {
            @Override
            public int compare(Light2D first, Light2D second) {
                float firstDx = first.getX() - camX;
                float firstDy = first.getY() - camY;
                float secondDx = second.getX() - camX;
                float secondDy = second.getY() - camY;
                return Float.compare(
                        second.contributionScore(secondDx, secondDy),
                        first.contributionScore(firstDx, firstDy));
            }
        });
        int activeLimit = Math.min(candidates.size(), maxActiveLights);
        for (int i = 0; i < activeLimit; i++) {
            activeCache.add(candidates.get(i));
        }
        int shadowsAdded = 0;
        for (Light2D light : activeCache) {
            if (shadowsAdded >= maxShadowLights) {
                break;
            }
            if (light.isShadowsEnabled()) {
                shadowCache.add(light);
                shadowsAdded++;
            }
        }
    }

    private List<Light2D> collectCandidates(float camX, float camY, float halfW, float halfH,
            float viewRadius) {
        List<Light2D> candidates = new ArrayList<>();
        if (lights.size() > GRID_THRESHOLD && !grid.isEmpty()) {
            float margin = Math.max(maxStaticRadius, MIN_RADIUS);
            int minCx = cellOf(camX - halfW - margin);
            int maxCx = cellOf(camX + halfW + margin);
            int minCy = cellOf(camY - halfH - margin);
            int maxCy = cellOf(camY + halfH + margin);
            Set<Light2D> seen = new HashSet<>();
            for (int cx = minCx; cx <= maxCx; cx++) {
                for (int cy = minCy; cy <= maxCy; cy++) {
                    List<Light2D> cell = grid.get(cellKey(cx, cy));
                    if (cell == null) {
                        continue;
                    }
                    for (Light2D light : cell) {
                        if (seen.add(light) && isRenderable(light)
                                && light.intersectsView(camX, camY, viewRadius)) {
                            candidates.add(light);
                        }
                    }
                }
            }
            for (Light2D light : lights.values()) {
                if (!light.isStatic() && isRenderable(light)
                        && light.intersectsView(camX, camY, viewRadius)
                        && !seen.contains(light)) {
                    candidates.add(light);
                }
            }
            return candidates;
        }
        for (Light2D light : lights.values()) {
            if (isRenderable(light) && light.intersectsView(camX, camY, viewRadius)) {
                candidates.add(light);
            }
        }
        return candidates;
    }

    private boolean isRenderable(Light2D light) {
        return light.isEnabled() && light.getIntensity() > 0f && light.getRadius() >= MIN_RADIUS;
    }

    private int cellOf(float coordinate) {
        return (int) Math.floor(coordinate / GRID_CELL_SIZE);
    }

    private long cellKey(int cx, int cy) {
        return (((long) cx) << 32) ^ (cy & 0xffffffffL);
    }
}
