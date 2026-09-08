package org.catrobat.catroid.twodlight;

public class Light2D {

    static final int STATIC_STABLE_FRAMES = 30;
    private static final float MOVE_EPSILON_SQ = 0.25f;

    private final String id;
    private float x;
    private float y;
    private float radius;
    private float intensity;
    private int color;
    private boolean enabled = true;
    private boolean shadowsEnabled;
    private String followSpriteName;
    private float lastX;
    private float lastY;
    private int stableFrames = STATIC_STABLE_FRAMES;
    private boolean positionInitialized;

    public Light2D(String id, float x, float y, float radius, float intensity, int color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = Math.max(0f, radius);
        this.intensity = Math.max(0f, intensity);
        this.color = color & 0xFFFFFF;
        this.lastX = x;
        this.lastY = y;
        this.positionInitialized = true;
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public float getIntensity() {
        return intensity;
    }

    public int getColor() {
        return color;
    }

    public float getRed() {
        return ((color >> 16) & 0xFF) / 255f;
    }

    public float getGreen() {
        return ((color >> 8) & 0xFF) / 255f;
    }

    public float getBlue() {
        return (color & 0xFF) / 255f;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isShadowsEnabled() {
        return shadowsEnabled;
    }

    public String getFollowSpriteName() {
        return followSpriteName;
    }

    public boolean isStatic() {
        return followSpriteName == null && stableFrames >= STATIC_STABLE_FRAMES;
    }

    void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    void setRadius(float radius) {
        this.radius = Math.max(0f, radius);
    }

    void setIntensity(float intensity) {
        this.intensity = Math.max(0f, intensity);
    }

    void setColor(int color) {
        this.color = color & 0xFFFFFF;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void setShadowsEnabled(boolean shadowsEnabled) {
        this.shadowsEnabled = shadowsEnabled;
    }

    void setFollowSpriteName(String followSpriteName) {
        this.followSpriteName = followSpriteName;
        this.stableFrames = 0;
    }

    void trackStability() {
        if (!positionInitialized) {
            lastX = x;
            lastY = y;
            positionInitialized = true;
            stableFrames = 0;
            return;
        }
        float dx = x - lastX;
        float dy = y - lastY;
        if (dx * dx + dy * dy > MOVE_EPSILON_SQ) {
            stableFrames = 0;
        } else if (stableFrames < STATIC_STABLE_FRAMES) {
            stableFrames++;
        }
        lastX = x;
        lastY = y;
    }

    float contributionScore(float dx, float dy) {
        return intensity * radius * radius / (1f + dx * dx + dy * dy);
    }

    public boolean intersectsView(float camX, float camY, float viewRadius) {
        float dx = x - camX;
        float dy = y - camY;
        float reach = radius + viewRadius;
        return dx * dx + dy * dy <= reach * reach;
    }
}
