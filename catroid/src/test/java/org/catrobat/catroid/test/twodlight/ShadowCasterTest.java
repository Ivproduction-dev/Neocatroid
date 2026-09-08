package org.catrobat.catroid.test.twodlight;

import org.catrobat.catroid.twodlight.LightRaycaster;
import org.catrobat.catroid.twodlight.ShadowCaster;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ShadowCasterTest {

    private ShadowCaster caster;

    private static class WallRaycaster implements LightRaycaster {
        @Override
        public float castRay(float startX, float startY, float endX, float endY,
                Set<String> ignoredSpriteNames, float[] outHitPoint) {
            float dx = endX - startX;
            if (dx <= 0f) {
                if (outHitPoint != null && outHitPoint.length >= 2) {
                    outHitPoint[0] = endX;
                    outHitPoint[1] = endY;
                }
                return 1f;
            }
            float wallX = 300f;
            float t = (wallX - startX) / dx;
            if (t < 0f || t > 1f) {
                if (outHitPoint != null && outHitPoint.length >= 2) {
                    outHitPoint[0] = endX;
                    outHitPoint[1] = endY;
                }
                return 1f;
            }
            if (outHitPoint != null && outHitPoint.length >= 2) {
                outHitPoint[0] = wallX;
                outHitPoint[1] = startY + (endY - startY) * t;
            }
            return t;
        }
    }

    @Before
    public void setUp() {
        caster = new ShadowCaster();
    }

    @Test
    public void nullRaycasterGivesFullCircle() {
        ShadowCaster.ShadowFan fan = caster.computeFan(0f, 0f, 500f, null,
                Collections.<String>emptySet(), 32);
        assertEquals(32, fan.rayCount);
        assertFalse(fan.anyHit);
        for (int i = 0; i <= 32; i++) {
            float dist = (float) Math.sqrt(fan.ringX[i] * fan.ringX[i]
                    + fan.ringY[i] * fan.ringY[i]);
            assertEquals(500f, dist, 5f);
        }
    }

    @Test
    public void wallBlocksRaysBehindIt() {
        ShadowCaster.ShadowFan fan = caster.computeFan(0f, 0f, 1000f, new WallRaycaster(),
                Collections.<String>emptySet(), 64);
        assertTrue(fan.anyHit);
        float nearestBlocked = Float.MAX_VALUE;
        float farthestOpen = 0f;
        for (int i = 0; i <= 64; i++) {
            float dist = (float) Math.sqrt(fan.ringX[i] * fan.ringX[i]
                    + fan.ringY[i] * fan.ringY[i]);
            float angle = (float) Math.atan2(fan.ringY[i], fan.ringX[i]);
            if (Math.abs(angle) < 0.4f) {
                nearestBlocked = Math.min(nearestBlocked, dist);
            } else if (Math.abs(angle) > 2.5f) {
                farthestOpen = Math.max(farthestOpen, dist);
            }
        }
        assertEquals(300f, nearestBlocked, 5f);
        assertEquals(1000f, farthestOpen, 5f);
    }

    @Test
    public void zeroRadiusGivesDegenerateFan() {
        ShadowCaster.ShadowFan fan = caster.computeFan(10f, 20f, 0f, new WallRaycaster(),
                Collections.<String>emptySet(), 8);
        assertFalse(fan.anyHit);
        assertEquals(10f, fan.ringX[0], 0.001f);
        assertEquals(20f, fan.ringY[0], 0.001f);
    }

    @Test
    public void failingRaycasterFallsBackToFullCircle() {
        LightRaycaster broken = new LightRaycaster() {
            @Override
            public float castRay(float startX, float startY, float endX, float endY,
                    Set<String> ignoredSpriteNames, float[] outHitPoint) {
                throw new RuntimeException("box2d locked");
            }
        };
        ShadowCaster.ShadowFan fan = caster.computeFan(0f, 0f, 400f, broken,
                Collections.<String>emptySet(), 16);
        assertFalse(fan.anyHit);
        assertEquals(400f, fan.ringX[0], 1f);
    }

    @Test
    public void wallPositionMatchesRaycasterOutput() {
        final Set<String> seen = new HashSet<>();
        LightRaycaster recording = new LightRaycaster() {
            @Override
            public float castRay(float startX, float startY, float endX, float endY,
                    Set<String> ignoredSpriteNames, float[] outHitPoint) {
                seen.add(endX + "," + endY);
                if (outHitPoint != null && outHitPoint.length >= 2) {
                    outHitPoint[0] = endX;
                    outHitPoint[1] = endY;
                }
                return 1f;
            }
        };
        caster.computeFan(0f, 0f, 100f, recording, Collections.<String>emptySet(), 8);
        assertEquals(9, seen.size());
    }
}
