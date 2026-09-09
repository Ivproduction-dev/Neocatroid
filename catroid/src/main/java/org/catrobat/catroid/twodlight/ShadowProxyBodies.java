package org.catrobat.catroid.twodlight;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShadowProxyBodies {

    private static final float MIN_HALF_SIZE = 0.5f;

    public interface SpriteInfo {
        Sprite sprite();
        String name();
        float centerX();
        float centerY();
        float halfWidth();
        float halfHeight();
        float angleDegrees();
        boolean isVisible();
        boolean isBackground();
        boolean hasRealBody();
    }

    private static class Proxy {
        Body body;
        float halfWidth;
        float halfHeight;
    }

    private final Map<Sprite, Proxy> proxies = new HashMap<>();
    private World lastWorld;

    public void sync(World world, List<SpriteInfo> infos) {
        if (world == null) {
            return;
        }
        if (world != lastWorld) {
            proxies.clear();
            lastWorld = world;
        }
        Set<Sprite> seen = new HashSet<>();
        float ratio = PhysicsWorld.RATIO;
        for (SpriteInfo info : infos) {
            if (info == null || info.sprite() == null || !info.isVisible()
                    || info.isBackground() || info.hasRealBody()
                    || info.halfWidth() < MIN_HALF_SIZE || info.halfHeight() < MIN_HALF_SIZE) {
                continue;
            }
            Sprite sprite = info.sprite();
            seen.add(sprite);
            Proxy proxy = proxies.get(sprite);
            if (proxy == null || proxy.body == null) {
                Body body = createProxyBody(world, sprite, info, ratio);
                if (body == null) {
                    continue;
                }
                proxy = new Proxy();
                proxy.body = body;
                proxy.halfWidth = info.halfWidth();
                proxy.halfHeight = info.halfHeight();
                proxies.put(sprite, proxy);
            } else {
                if (Math.abs(proxy.halfWidth - info.halfWidth()) > 1f
                        || Math.abs(proxy.halfHeight - info.halfHeight()) > 1f) {
                    reshapeProxyBody(proxy, info, ratio);
                    proxy.halfWidth = info.halfWidth();
                    proxy.halfHeight = info.halfHeight();
                }
                try {
                    proxy.body.setTransform(
                            new Vector2(info.centerX() / ratio, info.centerY() / ratio),
                            (float) Math.toRadians(info.angleDegrees()));
                } catch (Exception e) {
                    continue;
                }
            }
        }
        List<Sprite> stale = new ArrayList<>();
        for (Map.Entry<Sprite, Proxy> entry : proxies.entrySet()) {
            if (!seen.contains(entry.getKey())) {
                stale.add(entry.getKey());
            }
        }
        for (Sprite sprite : stale) {
            Proxy proxy = proxies.remove(sprite);
            if (proxy != null && proxy.body != null) {
                try {
                    world.destroyBody(proxy.body);
                } catch (Exception e) {
                    // already gone with an old world, drop the reference
                }
            }
        }
    }

    public void dropAll() {
        proxies.clear();
        lastWorld = null;
    }

    public void destroyAll() {
        if (lastWorld == null) {
            proxies.clear();
            return;
        }
        for (Proxy proxy : proxies.values()) {
            if (proxy != null && proxy.body != null) {
                try {
                    lastWorld.destroyBody(proxy.body);
                } catch (Exception e) {
                    // already gone, drop the reference
                }
            }
        }
        proxies.clear();
        lastWorld = null;
    }

    public int getProxyCount() {
        return proxies.size();
    }

    private Body createProxyBody(World world, Sprite sprite, SpriteInfo info, float ratio) {
        try {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            def.position.set(info.centerX() / ratio, info.centerY() / ratio);
            def.angle = (float) Math.toRadians(info.angleDegrees());
            Body body = world.createBody(def);
            attachProxyFixture(body, info, ratio);
            body.setUserData(sprite);
            return body;
        } catch (Exception e) {
            return null;
        }
    }

    private void reshapeProxyBody(Proxy proxy, SpriteInfo info, float ratio) {
        try {
            ArrayList<Fixture> fixtures = new ArrayList<>();
            for (Fixture fixture : proxy.body.getFixtureList()) {
                fixtures.add(fixture);
            }
            for (Fixture fixture : fixtures) {
                proxy.body.destroyFixture(fixture);
            }
            attachProxyFixture(proxy.body, info, ratio);
        } catch (Exception e) {
            // keep the old shape on failure
        }
    }

    private void attachProxyFixture(Body body, SpriteInfo info, float ratio) {
        PolygonShape shape = new PolygonShape();
        try {
            shape.setAsBox(info.halfWidth() / ratio, info.halfHeight() / ratio);
            Fixture fixture = body.createFixture(shape, 0f);
            Filter filter = fixture.getFilterData();
            filter.categoryBits = 0x0000;
            filter.maskBits = 0x0000;
            fixture.setFilterData(filter);
        } finally {
            shape.dispose();
        }
    }
}
