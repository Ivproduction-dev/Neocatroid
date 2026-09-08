package org.catrobat.catroid.test.twodlight;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class LightRayFilterTest {

    @Test
    public void sensorsAreAlwaysIgnored() {
        assertTrue(PhysicsWorld.shouldIgnoreLightRayFixture(true, new Sprite("wall"),
                Collections.<String>emptySet()));
        assertTrue(PhysicsWorld.shouldIgnoreLightRayFixture(true, null, null));
    }

    @Test
    public void listedSpritesAreIgnored() {
        Set<String> ignored = new HashSet<>();
        ignored.add("ghost");
        assertTrue(PhysicsWorld.shouldIgnoreLightRayFixture(false, new Sprite("ghost"), ignored));
        assertFalse(PhysicsWorld.shouldIgnoreLightRayFixture(false, new Sprite("wall"), ignored));
    }

    @Test
    public void ordinaryBodiesAreKept() {
        assertFalse(PhysicsWorld.shouldIgnoreLightRayFixture(false, new Sprite("wall"),
                Collections.<String>emptySet()));
        assertFalse(PhysicsWorld.shouldIgnoreLightRayFixture(false, new Object(), null));
        assertFalse(PhysicsWorld.shouldIgnoreLightRayFixture(false, null, null));
    }
}
