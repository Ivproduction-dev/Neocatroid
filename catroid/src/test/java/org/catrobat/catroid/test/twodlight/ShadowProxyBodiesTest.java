package org.catrobat.catroid.test.twodlight;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.twodlight.ShadowProxyBodies;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ShadowProxyBodiesTest {

    private World world;
    private ShadowProxyBodies proxies;

    private static class Info implements ShadowProxyBodies.SpriteInfo {
        final Sprite sprite = new Sprite("s");
        String name = "s";
        float cx;
        float cy;
        float hw = 50f;
        float hh = 50f;
        float angle;
        boolean visible = true;
        boolean background;
        boolean realBody;

        Info(String name, float cx, float cy) {
            this.name = name;
            this.cx = cx;
            this.cy = cy;
        }

        @Override
        public Sprite sprite() {
            return sprite;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public float centerX() {
            return cx;
        }

        @Override
        public float centerY() {
            return cy;
        }

        @Override
        public float halfWidth() {
            return hw;
        }

        @Override
        public float halfHeight() {
            return hh;
        }

        @Override
        public float angleDegrees() {
            return angle;
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public boolean isBackground() {
            return background;
        }

        @Override
        public boolean hasRealBody() {
            return realBody;
        }
    }

    @Before
    public void setUp() {
        world = new World(new Vector2(0f, 0f), true);
        proxies = new ShadowProxyBodies();
    }

    @After
    public void tearDown() {
        world.dispose();
    }

    private int bodyCount() {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        return bodies.size;
    }

    @Test
    public void createsProxyForVisibleSprite() {
        List<ShadowProxyBodies.SpriteInfo> infos = new ArrayList<>();
        infos.add(new Info("wall", 100f, 0f));
        proxies.sync(world, infos);
        assertEquals(1, proxies.getProxyCount());
        assertEquals(1, bodyCount());
    }

    @Test
    public void skipsBackgroundInvisibleRealBodyAndDegenerate() {
        List<ShadowProxyBodies.SpriteInfo> infos = new ArrayList<>();
        Info background = new Info("bg", 0f, 0f);
        background.background = true;
        Info invisible = new Info("hidden", 200f, 0f);
        invisible.visible = false;
        Info physical = new Info("hero", 300f, 0f);
        physical.realBody = true;
        Info degenerate = new Info("dot", 400f, 0f);
        degenerate.hw = 0f;
        degenerate.hh = 0f;
        infos.add(background);
        infos.add(invisible);
        infos.add(physical);
        infos.add(degenerate);
        proxies.sync(world, infos);
        assertEquals(0, proxies.getProxyCount());
        assertEquals(0, bodyCount());
    }

    @Test
    public void proxyFollowsSpriteAndStaleAreRemoved() {
        Info info = new Info("box", 100f, 0f);
        List<ShadowProxyBodies.SpriteInfo> infos = new ArrayList<>();
        infos.add(info);
        proxies.sync(world, infos);
        assertEquals(1, bodyCount());

        info.cx = 200f;
        proxies.sync(world, infos);
        assertEquals(1, bodyCount());
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        assertEquals(20f, bodies.first().getPosition().x, 0.01f);

        proxies.sync(world, Collections.<ShadowProxyBodies.SpriteInfo>emptyList());
        assertEquals(0, proxies.getProxyCount());
        assertEquals(0, bodyCount());
    }

    @Test
    public void proxyDoesNotCollideButBlocksRays() {
        List<ShadowProxyBodies.SpriteInfo> infos = new ArrayList<>();
        infos.add(new Info("wall", 300f, 0f));
        proxies.sync(world, infos);

        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        assertEquals(1, bodies.size);
        assertEquals(0, bodies.first().getFixtureList().first().getFilterData().maskBits);
        assertTrue(bodies.first().getUserData() instanceof Sprite);

        final float[] fraction = {1f};
        world.rayCast(new com.badlogic.gdx.physics.box2d.RayCastCallback() {
            @Override
            public float reportRayFixture(com.badlogic.gdx.physics.box2d.Fixture fixture,
                    Vector2 point, Vector2 normal, float fractionValue) {
                fraction[0] = Math.min(fraction[0], fractionValue);
                return fractionValue;
            }
        }, new Vector2(0f, 0f), new Vector2(100f, 0f));
        assertTrue(fraction[0] < 1f);
    }

    @Test
    public void nullWorldIsIgnored() {
        List<ShadowProxyBodies.SpriteInfo> infos = new ArrayList<>();
        infos.add(new Info("wall", 100f, 0f));
        proxies.sync(null, infos);
        assertEquals(0, proxies.getProxyCount());
    }
}
