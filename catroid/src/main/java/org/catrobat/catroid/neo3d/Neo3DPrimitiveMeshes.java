package org.catrobat.catroid.neo3d;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class Neo3DPrimitiveMeshes {

    public static final String ASSET_KEY_CUBE = "embedded:neo-cube";
    public static final String ASSET_KEY_SPHERE = "embedded:neo-sphere";
    public static final String ASSET_KEY_CYLINDER = "embedded:neo-cylinder";

    private Neo3DPrimitiveMeshes() {
    }

    public static byte[] buildCube(float size, float[] baseColorRgba) {
        float h = size / 2f;
        float[][] faces = {
                {h, -h, -h, h, h, -h, h, h, h, h, -h, h},
                {-h, -h, h, -h, h, h, -h, h, -h, -h, -h, -h},
                {-h, h, -h, -h, h, h, h, h, h, h, h, -h},
                {-h, -h, h, -h, -h, -h, h, -h, -h, h, -h, h},
                {-h, -h, h, h, -h, h, h, h, h, -h, h, h},
                {h, -h, -h, -h, -h, -h, -h, h, -h, h, h, -h},
        };
        float[][] normals = {
                {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
        };
        List<Float> pos = new ArrayList<>(72);
        List<Float> nor = new ArrayList<>(72);
        List<Float> uv = new ArrayList<>(48);
        List<Integer> idx = new ArrayList<>(36);
        float[] quadUv = {0, 0, 1, 0, 1, 1, 0, 1};
        for (int f = 0; f < 6; f++) {
            int base = f * 4;
            for (int v = 0; v < 4; v++) {
                pos.add(faces[f][v * 3]);
                pos.add(faces[f][v * 3 + 1]);
                pos.add(faces[f][v * 3 + 2]);
                nor.add(normals[f][0]);
                nor.add(normals[f][1]);
                nor.add(normals[f][2]);
                uv.add(quadUv[v * 2]);
                uv.add(quadUv[v * 2 + 1]);
            }
            idx.add(base);
            idx.add(base + 1);
            idx.add(base + 2);
            idx.add(base);
            idx.add(base + 2);
            idx.add(base + 3);
        }
        return packGlb("NeoCube", pos, nor, uv, idx, baseColorRgba);
    }

    public static byte[] buildSphere(float radius, int latBands, int lonBands,
            float[] baseColorRgba) {
        int lat = Math.max(2, latBands);
        int lon = Math.max(3, lonBands);
        List<Float> pos = new ArrayList<>((lat + 1) * (lon + 1) * 3);
        List<Float> nor = new ArrayList<>((lat + 1) * (lon + 1) * 3);
        List<Float> uv = new ArrayList<>((lat + 1) * (lon + 1) * 2);
        List<Integer> idx = new ArrayList<>(lat * lon * 6);
        for (int i = 0; i <= lat; i++) {
            float theta = (float) (i * Math.PI / lat);
            float sinT = (float) Math.sin(theta);
            float cosT = (float) Math.cos(theta);
            for (int j = 0; j <= lon; j++) {
                float phi = (float) (j * 2.0 * Math.PI / lon);
                float x = sinT * (float) Math.cos(phi);
                float y = cosT;
                float z = sinT * (float) Math.sin(phi);
                pos.add(x * radius);
                pos.add(y * radius);
                pos.add(z * radius);
                nor.add(x);
                nor.add(y);
                nor.add(z);
                uv.add((float) j / lon);
                uv.add(1f - (float) i / lat);
            }
        }
        for (int i = 0; i < lat; i++) {
            for (int j = 0; j < lon; j++) {
                int a = i * (lon + 1) + j;
                int b = a + lon + 1;
                idx.add(a);
                idx.add(a + 1);
                idx.add(b);
                idx.add(b);
                idx.add(a + 1);
                idx.add(b + 1);
            }
        }
        return packGlb("NeoSphere", pos, nor, uv, idx, baseColorRgba);
    }

    public static byte[] buildCylinder(float radius, float height, int segments,
            float[] baseColorRgba) {
        int seg = Math.max(3, segments);
        float h = height / 2f;
        List<Float> pos = new ArrayList<>();
        List<Float> nor = new ArrayList<>();
        List<Float> uv = new ArrayList<>();
        List<Integer> idx = new ArrayList<>();
        for (int j = 0; j <= seg; j++) {
            float phi = (float) (j * 2.0 * Math.PI / seg);
            float x = (float) Math.cos(phi);
            float z = (float) Math.sin(phi);
            pos.add(x * radius);
            pos.add(-h);
            pos.add(z * radius);
            nor.add(x);
            nor.add(0f);
            nor.add(z);
            uv.add((float) j / seg);
            uv.add(0f);
            pos.add(x * radius);
            pos.add(h);
            pos.add(z * radius);
            nor.add(x);
            nor.add(0f);
            nor.add(z);
            uv.add((float) j / seg);
            uv.add(1f);
        }
        for (int j = 0; j < seg; j++) {
            int b0 = j * 2;
            int t0 = j * 2 + 1;
            int b1 = j * 2 + 2;
            int t1 = j * 2 + 3;
            idx.add(b0);
            idx.add(t0);
            idx.add(b1);
            idx.add(b1);
            idx.add(t0);
            idx.add(t1);
        }
        int topCenter = pos.size() / 3;
        pos.add(0f);
        pos.add(h);
        pos.add(0f);
        nor.add(0f);
        nor.add(1f);
        nor.add(0f);
        uv.add(0.5f);
        uv.add(0.5f);
        int topRing = pos.size() / 3;
        for (int j = 0; j <= seg; j++) {
            float phi = (float) (j * 2.0 * Math.PI / seg);
            float x = (float) Math.cos(phi);
            float z = (float) Math.sin(phi);
            pos.add(x * radius);
            pos.add(h);
            pos.add(z * radius);
            nor.add(0f);
            nor.add(1f);
            nor.add(0f);
            uv.add(x * 0.5f + 0.5f);
            uv.add(z * 0.5f + 0.5f);
        }
        for (int j = 0; j < seg; j++) {
            idx.add(topCenter);
            idx.add(topRing + j + 1);
            idx.add(topRing + j);
        }
        int bottomCenter = pos.size() / 3;
        pos.add(0f);
        pos.add(-h);
        pos.add(0f);
        nor.add(0f);
        nor.add(-1f);
        nor.add(0f);
        uv.add(0.5f);
        uv.add(0.5f);
        int bottomRing = pos.size() / 3;
        for (int j = 0; j <= seg; j++) {
            float phi = (float) (j * 2.0 * Math.PI / seg);
            float x = (float) Math.cos(phi);
            float z = (float) Math.sin(phi);
            pos.add(x * radius);
            pos.add(-h);
            pos.add(z * radius);
            nor.add(0f);
            nor.add(-1f);
            nor.add(0f);
            uv.add(x * 0.5f + 0.5f);
            uv.add(z * 0.5f + 0.5f);
        }
        for (int j = 0; j < seg; j++) {
            idx.add(bottomCenter);
            idx.add(bottomRing + j);
            idx.add(bottomRing + j + 1);
        }
        return packGlb("NeoCylinder", pos, nor, uv, idx, baseColorRgba);
    }

    static byte[] packGlb(String name, List<Float> pos, List<Float> nor, List<Float> uv,
            List<Integer> idx, float[] baseColorRgba) {
        int vertCount = pos.size() / 3;
        int maxIndex = 0;
        for (int v : idx) {
            if (v > maxIndex) {
                maxIndex = v;
            }
        }
        boolean useShort = maxIndex < 65536;
        int indexByteSize = useShort ? 2 : 4;
        int posBytes = vertCount * 12;
        int norBytes = vertCount * 12;
        int uvBytes = vertCount * 8;
        int idxBytes = idx.size() * indexByteSize;
        int binLen = posBytes + norBytes + uvBytes + idxBytes;
        ByteBuffer bin = ByteBuffer.allocate(binLen).order(ByteOrder.LITTLE_ENDIAN);
        for (float v : pos) {
            bin.putFloat(v);
        }
        for (float v : nor) {
            bin.putFloat(v);
        }
        for (float v : uv) {
            bin.putFloat(v);
        }
        for (int v : idx) {
            if (useShort) {
                bin.putShort((short) v);
            } else {
                bin.putInt(v);
            }
        }
        byte[] binBytes = bin.array();
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float maxZ = -Float.MAX_VALUE;
        for (int i = 0; i < vertCount; i++) {
            float x = pos.get(i * 3);
            float y = pos.get(i * 3 + 1);
            float z = pos.get(i * 3 + 2);
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }
        float r = baseColorRgba[0];
        float g = baseColorRgba[1];
        float b = baseColorRgba[2];
        float a = baseColorRgba.length > 3 ? baseColorRgba[3] : 1f;
        int indexType = useShort ? 5123 : 5125;
        String json = "{"
                + "\"asset\":{\"version\":\"2.0\",\"generator\":\"NeoCatroid-3D-V2\"},"
                + "\"scene\":0,\"scenes\":[{\"nodes\":[0]}],"
                + "\"nodes\":[{\"mesh\":0,\"name\":\"" + name + "\"}],"
                + "\"meshes\":[{\"primitives\":[{"
                + "\"attributes\":{\"POSITION\":0,\"NORMAL\":1,\"TEXCOORD_0\":2},"
                + "\"indices\":3,\"material\":0"
                + "}]}],"
                + "\"materials\":[{"
                + "\"name\":\"" + name + "Mat\","
                + "\"pbrMetallicRoughness\":{"
                + "\"baseColorFactor\":[" + r + "," + g + "," + b + "," + a + "],"
                + "\"metallicFactor\":0.1,\"roughnessFactor\":0.6"
                + "}}],"
                + "\"buffers\":[{\"byteLength\":" + binLen + "}],"
                + "\"bufferViews\":["
                + "{\"buffer\":0,\"byteOffset\":0,\"byteLength\":" + posBytes + ",\"target\":34962},"
                + "{\"buffer\":0,\"byteOffset\":" + posBytes + ",\"byteLength\":" + norBytes
                + ",\"target\":34962},"
                + "{\"buffer\":0,\"byteOffset\":" + (posBytes + norBytes) + ",\"byteLength\":"
                + uvBytes + ",\"target\":34962},"
                + "{\"buffer\":0,\"byteOffset\":" + (posBytes + norBytes + uvBytes)
                + ",\"byteLength\":" + idxBytes + ",\"target\":34963}"
                + "],"
                + "\"accessors\":["
                + "{\"bufferView\":0,\"componentType\":5126,\"count\":" + vertCount
                + ",\"type\":\"VEC3\","
                + "\"max\":[" + maxX + "," + maxY + "," + maxZ + "],\"min\":[" + minX + ","
                + minY + "," + minZ + "]},"
                + "{\"bufferView\":1,\"componentType\":5126,\"count\":" + vertCount
                + ",\"type\":\"VEC3\"},"
                + "{\"bufferView\":2,\"componentType\":5126,\"count\":" + vertCount
                + ",\"type\":\"VEC2\"},"
                + "{\"bufferView\":3,\"componentType\":" + indexType + ",\"count\":" + idx.size()
                + ",\"type\":\"SCALAR\"}"
                + "]"
                + "}";

        byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
        int jsonPadded = ((jsonBytes.length + 3) / 4) * 4;
        int binPadded = ((binBytes.length + 3) / 4) * 4;
        int totalLen = 12 + 8 + jsonPadded + 8 + binPadded;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(totalLen);
            ByteBuffer header = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
            header.put((byte) 'g');
            header.put((byte) 'l');
            header.put((byte) 'T');
            header.put((byte) 'F');
            header.putInt(2);
            header.putInt(totalLen);
            out.write(header.array(), 0, 12);
            writeChunk(out, jsonBytes, jsonPadded, 0x4E4F534A, (byte) 0x20);
            writeChunk(out, binBytes, binPadded, 0x004E4942, (byte) 0x00);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("GLB packing failed", e);
        }
    }

    private static void writeChunk(ByteArrayOutputStream out, byte[] data, int paddedLen,
            int chunkType, byte pad) throws Exception {
        ByteBuffer header = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        header.putInt(paddedLen);
        header.putInt(chunkType);
        out.write(header.array(), 0, 8);
        out.write(data, 0, data.length);
        for (int i = data.length; i < paddedLen; i++) {
            out.write(pad);
        }
    }
}
