package org.catrobat.catroid.fast2d.mirror;

import android.util.Log;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.fast2d.FastTwoDManager;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class MirrorScene {
    private static final String TAG = "MIRROR";

    public static volatile boolean ENABLED = false;

    private static final MirrorScene INSTANCE = new MirrorScene();

    public static MirrorScene get() {
        return INSTANCE;
    }

    public static boolean suppressClassicDraw(Sprite sprite) {
        return ENABLED && sprite != null && get().nodes.containsKey(sprite);
    }

    private final Map<Sprite, MirrorNode> nodes = new IdentityHashMap<>();
    private int passStamp;
    private long syncNsTotal;
    private int syncFrames;
    private int lastRenderCalls;
    private long renderCallsPerSecond;
    private long lastLogMs;

    private MirrorScene() {
    }

    public void sync(List<Sprite> sprites, FastTwoDManager manager, OrthographicCamera stageCamera,
            SpriteBatch batch) {
        if (!ENABLED || manager == null || sprites == null) {
            return;
        }
        long startNs = System.nanoTime();
        manager.copyCameraFrom(stageCamera);
        passStamp++;
        for (Sprite sprite : sprites) {
            if (sprite == null) {
                continue;
            }
            MirrorNode node = nodes.get(sprite);
            if (node == null) {
                node = new MirrorNode(sprite);
                nodes.put(sprite, node);
            }
            node.seenStamp = passStamp;
            node.sync(manager);
        }
        Iterator<Map.Entry<Sprite, MirrorNode>> it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Sprite, MirrorNode> entry = it.next();
            if (entry.getValue().seenStamp != passStamp) {
                entry.getValue().drop(manager);
                it.remove();
            }
        }
        syncNsTotal += System.nanoTime() - startNs;
        syncFrames++;
        long nowMs = System.currentTimeMillis();
        if (nowMs - lastLogMs >= 1000) {
            double avgSyncMs = syncFrames == 0 ? 0 : (syncNsTotal / (double) syncFrames) / 1_000_000.0;
            Log.i(TAG, String.format("entities: %d | sync: %.3fms | renderCalls/s: %d",
                    nodes.size(), avgSyncMs, renderCallsPerSecond));
            syncNsTotal = 0;
            syncFrames = 0;
            renderCallsPerSecond = 0;
            lastLogMs = nowMs;
        }
    }

    public void sampleRenderCalls(SpriteBatch batch) {
        if (!ENABLED || batch == null) {
            return;
        }
        int renderCalls = batch.renderCalls;
        if (renderCalls >= lastRenderCalls) {
            renderCallsPerSecond += renderCalls - lastRenderCalls;
        } else {
            renderCallsPerSecond += renderCalls;
        }
        lastRenderCalls = renderCalls;
    }

    public void clear(FastTwoDManager manager) {
        if (manager != null) {
            for (MirrorNode node : nodes.values()) {
                node.drop(manager);
            }
        }
        nodes.clear();
    }
}
