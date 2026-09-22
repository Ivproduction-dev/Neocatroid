package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.Neo3DPrimitiveMeshes;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Neo3DPrimitiveMeshesTest {

    private static JSONObject parseGlb(byte[] glb) throws Exception {
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
        int binHeaderOff = 20 + jsonLen;
        assertEquals(0x004E4942, buf.getInt(binHeaderOff + 4));
        assertEquals(glb.length, binHeaderOff + 8 + buf.getInt(binHeaderOff));
        return root;
    }

    private static void checkMesh(JSONObject root, int vertCount, int indexCount)
            throws Exception {
        JSONArray accessors = root.getJSONArray("accessors");
        assertEquals(vertCount, accessors.getJSONObject(0).getInt("count"));
        assertEquals(vertCount, accessors.getJSONObject(1).getInt("count"));
        assertEquals(vertCount, accessors.getJSONObject(2).getInt("count"));
        assertEquals(indexCount, accessors.getJSONObject(3).getInt("count"));
        assertEquals(1, root.getJSONArray("materials").length());
        assertEquals(1, root.getJSONArray("meshes").length());
    }

    @Test
    public void cubeGlbIsValid() throws Exception {
        JSONObject root = parseGlb(Neo3DPrimitiveMeshes.buildCube(2f,
                new float[]{0.75f, 0.78f, 0.82f, 1f}));
        checkMesh(root, 24, 36);
    }

    @Test
    public void sphereGlbIsValid() throws Exception {
        JSONObject root = parseGlb(Neo3DPrimitiveMeshes.buildSphere(1f, 12, 24,
                new float[]{0.85f, 0.55f, 0.25f, 1f}));
        checkMesh(root, 13 * 25, 12 * 24 * 6);
        JSONObject pos = root.getJSONArray("accessors").getJSONObject(0);
        JSONArray max = pos.getJSONArray("max");
        assertTrue(Math.abs(max.getDouble(0) - 1.0) < 0.01);
    }

    @Test
    public void cylinderGlbIsValid() throws Exception {
        JSONObject root = parseGlb(Neo3DPrimitiveMeshes.buildCylinder(1f, 2f, 24,
                new float[]{0.25f, 0.65f, 0.6f, 1f}));
        int verts = 25 * 2 + 26 * 2;
        checkMesh(root, verts, 24 * 6 + 24 * 3 * 2);
    }
}
