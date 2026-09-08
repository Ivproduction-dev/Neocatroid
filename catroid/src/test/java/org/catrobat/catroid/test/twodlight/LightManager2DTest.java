package org.catrobat.catroid.test.twodlight;

import org.catrobat.catroid.twodlight.Light2D;
import org.catrobat.catroid.twodlight.LightManager2D;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class LightManager2DTest {

    private LightManager2D manager;
    private Map<String, float[]> spritePositions;

    private final LightManager2D.SpritePositionProvider provider = new LightManager2D.SpritePositionProvider() {
        @Override
        public float[] getSpritePosition(String spriteName) {
            return spritePositions.get(spriteName);
        }
    };

    @Before
    public void setUp() {
        manager = new LightManager2D();
        spritePositions = new HashMap<>();
    }

    @Test
    public void createGetRemoveLight() {
        assertNull(manager.getLight("torch"));
        Light2D light = manager.createOrUpdateLight("torch", 10f, 20f, 300f, 1f, 0xFF8800);
        assertNotNull(light);
        assertEquals("torch", light.getId());
        assertEquals(10f, light.getX(), 0.001f);
        assertEquals(20f, light.getY(), 0.001f);
        assertEquals(300f, light.getRadius(), 0.001f);
        assertEquals(1f, light.getIntensity(), 0.001f);
        assertEquals(0xFF8800, light.getColor());
        assertEquals(1, manager.getLightCount());
        assertTrue(manager.removeLight("torch"));
        assertNull(manager.getLight("torch"));
        assertFalse(manager.removeLight("torch"));
    }

    @Test
    public void createWithEmptyIdIsRejected() {
        assertNull(manager.createOrUpdateLight("", 0f, 0f, 100f, 1f, 0xFFFFFF));
        assertNull(manager.createOrUpdateLight(null, 0f, 0f, 100f, 1f, 0xFFFFFF));
        assertEquals(0, manager.getLightCount());
    }

    @Test
    public void enableDisableAndDelete() {
        manager.createOrUpdateLight("lamp", 0f, 0f, 200f, 1f, 0xFFFFFF);
        manager.update(0f, 0f, 500f, 500f, provider);
        assertEquals(1, manager.getActiveLights().size());

        manager.setEnabled("lamp", false);
        manager.update(0f, 0f, 500f, 500f, provider);
        assertTrue(manager.getActiveLights().isEmpty());
        assertFalse(manager.hasLights());

        manager.setEnabled("lamp", true);
        assertFalse(manager.getLight("lamp").isEnabled() == false);
    }

    @Test
    public void offScreenLightsAreCulled() {
        manager.createOrUpdateLight("near", 0f, 0f, 200f, 1f, 0xFFFFFF);
        manager.createOrUpdateLight("far", 5000f, 5000f, 200f, 1f, 0xFFFFFF);
        List<Light2D> active = manager.update(0f, 0f, 400f, 300f, provider);
        assertEquals(1, active.size());
        assertEquals("near", active.get(0).getId());
    }

    @Test
    public void bigFarLightBeatsSmallNearLight() {
        manager.createOrUpdateLight("small", 100f, 0f, 50f, 1f, 0xFFFFFF);
        manager.createOrUpdateLight("big", 900f, 0f, 2000f, 1f, 0xFFFFFF);
        manager.setMaxActiveLights(1);
        List<Light2D> active = manager.update(0f, 0f, 2000f, 2000f, provider);
        assertEquals(1, active.size());
        assertEquals("big", active.get(0).getId());
    }

    @Test
    public void activeSetIsCachedWithoutChanges() {
        manager.createOrUpdateLight("a", 0f, 0f, 200f, 1f, 0xFFFFFF);
        List<Light2D> first = manager.update(0f, 0f, 400f, 300f, provider);
        long version = manager.getVersion();
        List<Light2D> second = manager.update(0f, 0f, 400f, 300f, provider);
        assertTrue(first == second || manager.getVersion() == version);
        assertEquals(1, second.size());
    }

    @Test
    public void staticDynamicSplit() {
        manager.createOrUpdateLight("fixed", 0f, 0f, 200f, 1f, 0xFFFFFF);
        manager.createOrUpdateLight("moving", 500f, 0f, 200f, 1f, 0xFFFFFF);
        spritePositions.put("hero", new float[] {500f, 0f});
        manager.attachToSprite("moving", "hero");
        for (int i = 0; i < 40; i++) {
            manager.update(0f, 0f, 2000f, 2000f, provider);
        }
        assertTrue(manager.getLight("fixed").isStatic());
        assertFalse(manager.getLight("moving").isStatic());

        spritePositions.put("hero", new float[] {510f, 0f});
        manager.update(0f, 0f, 2000f, 2000f, provider);
        assertEquals(510f, manager.getLight("moving").getX(), 0.001f);

        manager.detachFromSprite("moving");
        for (int i = 0; i < 40; i++) {
            manager.update(0f, 0f, 2000f, 2000f, provider);
        }
        assertTrue(manager.getLight("moving").isStatic());
    }

    @Test
    public void manyLightsUseGridPath() {
        for (int i = 0; i < 300; i++) {
            float x = (i % 20) * 200f - 2000f;
            float y = (i / 20) * 200f - 1500f;
            manager.createOrUpdateLight("light" + i, x, y, 120f, 1f, 0xFFFFFF);
        }
        for (int i = 0; i < 40; i++) {
            manager.update(0f, 0f, 2000f, 2000f, provider);
        }
        List<Light2D> active = manager.update(0f, 0f, 400f, 300f, provider);
        assertFalse(active.isEmpty());
        assertTrue(active.size() <= manager.getMaxActiveLights());
        for (Light2D light : active) {
            assertTrue(light.intersectsView(0f, 0f, 501f));
        }
    }

    @Test
    public void shadowCapSelectsStrongest() {
        manager.setMaxShadowLights(1);
        manager.setMaxActiveLights(10);
        manager.createOrUpdateLight("weak", 50f, 0f, 100f, 0.2f, 0xFFFFFF);
        manager.createOrUpdateLight("strong", 400f, 0f, 800f, 1f, 0xFFFFFF);
        manager.setShadowsEnabled("weak", true);
        manager.setShadowsEnabled("strong", true);
        manager.update(0f, 0f, 2000f, 2000f, provider);
        List<Light2D> shadows = manager.getShadowLights();
        assertEquals(1, shadows.size());
        assertEquals("strong", shadows.get(0).getId());
    }

    @Test
    public void shadowTogglePerSprite() {
        assertTrue(manager.isSpriteShadowCasting("wall"));
        manager.setSpriteShadowCasting("wall", false);
        assertFalse(manager.isSpriteShadowCasting("wall"));
        manager.setSpriteShadowCasting("wall", true);
        assertTrue(manager.isSpriteShadowCasting("wall"));
    }

    @Test
    public void propertiesAndAmbient() {
        manager.createOrUpdateLight("lamp", 0f, 0f, 200f, 1f, 0xFFFFFF);
        assertTrue(manager.setProperty("lamp", LightManager2D.PROP_RADIUS, 500f));
        assertTrue(manager.setProperty("lamp", LightManager2D.PROP_INTENSITY, 0.5f));
        assertTrue(manager.setProperty("lamp", LightManager2D.PROP_X, 30f));
        assertTrue(manager.setProperty("lamp", LightManager2D.PROP_Y, 40f));
        assertTrue(manager.setProperty("lamp", LightManager2D.PROP_AMBIENT, 0.3f));
        assertTrue(manager.setColor("lamp", 0x112233));
        Light2D light = manager.getLight("lamp");
        assertEquals(500f, light.getRadius(), 0.001f);
        assertEquals(0.5f, light.getIntensity(), 0.001f);
        assertEquals(30f, light.getX(), 0.001f);
        assertEquals(40f, light.getY(), 0.001f);
        assertEquals(0x112233, light.getColor());
        assertEquals(0.3f, manager.getAmbient(), 0.001f);
        assertFalse(manager.setProperty("nope", LightManager2D.PROP_RADIUS, 1f));
        assertFalse(manager.setProperty("lamp", 999, 1f));
    }

    @Test
    public void clearResetsEverything() {
        manager.createOrUpdateLight("a", 0f, 0f, 200f, 1f, 0xFFFFFF);
        manager.setSpriteShadowCasting("wall", false);
        manager.setAmbient(0.5f);
        manager.update(0f, 0f, 400f, 300f, provider);
        manager.clear();
        assertEquals(0, manager.getLightCount());
        assertTrue(manager.getActiveLights().isEmpty());
        assertTrue(manager.isSpriteShadowCasting("wall"));
    }
}
