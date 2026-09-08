package org.catrobat.catroid.twodlight;

import java.util.Set;

public class ShadowCaster {

    public static final int DEFAULT_RAY_COUNT = 64;
    public static final int LOW_END_RAY_COUNT = 48;

    public static class ShadowFan {
        public final float[] ringX;
        public final float[] ringY;
        public final int rayCount;
        public final boolean anyHit;

        ShadowFan(float[] ringX, float[] ringY, int rayCount, boolean anyHit) {
            this.ringX = ringX;
            this.ringY = ringY;
            this.rayCount = rayCount;
            this.anyHit = anyHit;
        }
    }

    private final float[] outHit = new float[2];

    public ShadowFan computeFan(float centerX, float centerY, float radius,
            LightRaycaster raycaster, Set<String> noShadowSprites, int rayCount) {
        if (rayCount <= 0) {
            rayCount = DEFAULT_RAY_COUNT;
        }
        float[] ringX = new float[rayCount + 1];
        float[] ringY = new float[rayCount + 1];
        boolean anyHit = false;
        if (raycaster == null || radius <= 0f) {
            fillCircle(centerX, centerY, radius, ringX, ringY, rayCount);
            return new ShadowFan(ringX, ringY, rayCount, false);
        }
        for (int i = 0; i <= rayCount; i++) {
            float angle = (float) (i * 2.0 * Math.PI / rayCount);
            float dirX = (float) Math.cos(angle);
            float dirY = (float) Math.sin(angle);
            float endX = centerX + dirX * radius;
            float endY = centerY + dirY * radius;
            float fraction = 1f;
            try {
                fraction = raycaster.castRay(centerX, centerY, endX, endY, noShadowSprites, outHit);
            } catch (Exception e) {
                fraction = 1f;
            }
            fraction = Math.max(0f, Math.min(1f, fraction));
            if (fraction < 1f) {
                anyHit = true;
                ringX[i] = outHit[0];
                ringY[i] = outHit[1];
            } else {
                ringX[i] = endX;
                ringY[i] = endY;
            }
        }
        return new ShadowFan(ringX, ringY, rayCount, anyHit);
    }

    public ShadowFan computeFan(float centerX, float centerY, float radius,
            LightRaycaster raycaster, Set<String> noShadowSprites) {
        return computeFan(centerX, centerY, radius, raycaster, noShadowSprites, DEFAULT_RAY_COUNT);
    }

    private void fillCircle(float centerX, float centerY, float radius, float[] ringX, float[] ringY,
            int rayCount) {
        for (int i = 0; i <= rayCount; i++) {
            float angle = (float) (i * 2.0 * Math.PI / rayCount);
            ringX[i] = centerX + (float) Math.cos(angle) * radius;
            ringY[i] = centerY + (float) Math.sin(angle) * radius;
        }
    }
}
