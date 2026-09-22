package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.assets.Neo3DAssetManager;
import org.catrobat.catroid.neo3d.demo.Neo3DEmbeddedCube;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Neo3DAssetManagerTest {

    @Test
    public void refcountAcquireRelease() {
        Neo3DAssetManager manager = new Neo3DAssetManager();
        manager.acquire(Neo3DAssetManager.AssetKind.MODEL, "a.glb", 5, 1000);
        manager.acquire(Neo3DAssetManager.AssetKind.MODEL, "a.glb", 5, 1000);
        assertEquals(2, manager.getRefCount("a.glb"));
        assertEquals(1, manager.getEntryCount());
        assertFalse(manager.release("a.glb"));
        assertEquals(1, manager.getRefCount("a.glb"));
        assertTrue(manager.release("a.glb"));
        assertEquals(0, manager.getRefCount("a.glb"));
        assertEquals(1, manager.getEntryCount());
        assertFalse(manager.release("missing"));
    }

    @Test
    public void lruEvictsOnlyZeroRefcount() {
        Neo3DAssetManager manager = new Neo3DAssetManager(2, 2048);
        manager.acquire(Neo3DAssetManager.AssetKind.TEXTURE, "t1", 1, 10);
        manager.acquire(Neo3DAssetManager.AssetKind.TEXTURE, "t2", 1, 10);
        manager.release("t1");
        manager.acquire(Neo3DAssetManager.AssetKind.TEXTURE, "t3", 1, 10);
        assertEquals(0, manager.getRefCount("t1"));
        assertEquals(1, manager.getRefCount("t2"));
        assertEquals(1, manager.getRefCount("t3"));
        assertTrue(manager.getEvictions() >= 1);
    }

    @Test
    public void downsampleFactor() {
        Neo3DAssetManager manager = new Neo3DAssetManager();
        assertEquals(1, manager.downsampleFactorFor(1024, 1024));
        assertEquals(1, manager.downsampleFactorFor(2048, 1024));
        assertEquals(2, manager.downsampleFactorFor(4096, 2048));
        assertEquals(4, manager.downsampleFactorFor(8192, 100));
    }

    @Test
    public void embeddedCubeGlbContainerIsValid() throws Exception {
        byte[] glb = Neo3DEmbeddedCube.buildDefaultGlb();
        assertTrue(glb.length > 100);
        assertEquals('g', glb[0]);
        assertEquals('l', glb[1]);
        assertEquals('T', glb[2]);
        assertEquals('F', glb[3]);
        ByteBuffer buf = ByteBuffer.wrap(glb).order(ByteOrder.LITTLE_ENDIAN);
        assertEquals(2, buf.getInt(4));
        assertEquals(glb.length, buf.getInt(8));
        int jsonLen = buf.getInt(12);
        assertEquals(0x4E4F534A, buf.getInt(16));
        assertTrue(jsonLen > 0 && jsonLen % 4 == 0);
        String json = new String(glb, 20, jsonLen, "UTF-8").trim();
        JSONObject root = new JSONObject(json);
        assertEquals("2.0", root.getJSONObject("asset").getString("version"));
        assertEquals(24, root.getJSONArray("accessors").getJSONObject(0).getInt("count"));
        assertEquals(36, root.getJSONArray("accessors").getJSONObject(3).getInt("count"));
        assertEquals(1, root.getJSONArray("materials").length());
        int binHeaderOff = 20 + jsonLen;
        int binLen = buf.getInt(binHeaderOff);
        assertEquals(0x004E4942, buf.getInt(binHeaderOff + 4));
        assertEquals(840, binLen);
        assertEquals(binHeaderOff + 8 + binLen, glb.length);
        assertFalse(Neo3DAssetManager.hasUnsupportedGlbExtension(glb));
    }

    @Test
    public void preflightDetectsDracoMarker() {
        String fakeJson = "{\"extensionsUsed\":[\"KHR_draco_mesh_compression\"]}";
        byte[] jsonBytes = fakeJson.getBytes();
        int jsonPadded = ((jsonBytes.length + 3) / 4) * 4;
        ByteBuffer buf = ByteBuffer.allocate(20 + jsonPadded).order(ByteOrder.LITTLE_ENDIAN);
        buf.put((byte) 'g');
        buf.put((byte) 'l');
        buf.put((byte) 'T');
        buf.put((byte) 'F');
        buf.putInt(2);
        buf.putInt(20 + jsonPadded);
        buf.putInt(jsonPadded);
        buf.putInt(0x4E4F534A);
        buf.put(jsonBytes);
        while (buf.position() < buf.capacity()) {
            buf.put((byte) 0x20);
        }
        assertTrue(Neo3DAssetManager.hasUnsupportedGlbExtension(buf.array()));
    }
}
