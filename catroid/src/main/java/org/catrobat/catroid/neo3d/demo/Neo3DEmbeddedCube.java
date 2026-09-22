package org.catrobat.catroid.neo3d.demo;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public final class Neo3DEmbeddedCube {

    public static final String ASSET_KEY = "embedded:cube";

    private Neo3DEmbeddedCube() {
    }

    public static byte[] buildGlb(float size, float[] baseColorRgba) {
        float h = size / 2f;
        float r = baseColorRgba[0];
        float g = baseColorRgba[1];
        float b = baseColorRgba[2];
        float a = baseColorRgba.length > 3 ? baseColorRgba[3] : 1f;

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
        float[] uv = {0, 0, 1, 0, 1, 1, 0, 1};

        ByteBuffer bin = ByteBuffer.allocate(840).order(ByteOrder.LITTLE_ENDIAN);
        for (int f = 0; f < 6; f++) {
            for (int v = 0; v < 4; v++) {
                bin.putFloat(faces[f][v * 3]);
                bin.putFloat(faces[f][v * 3 + 1]);
                bin.putFloat(faces[f][v * 3 + 2]);
            }
        }
        for (int f = 0; f < 6; f++) {
            for (int v = 0; v < 4; v++) {
                bin.putFloat(normals[f][0]);
                bin.putFloat(normals[f][1]);
                bin.putFloat(normals[f][2]);
            }
        }
        for (int f = 0; f < 6; f++) {
            for (int v = 0; v < 4; v++) {
                bin.putFloat(uv[v * 2]);
                bin.putFloat(uv[v * 2 + 1]);
            }
        }
        for (int f = 0; f < 6; f++) {
            int base = f * 4;
            short[] idx = {(short) base, (short) (base + 1), (short) (base + 2),
                    (short) base, (short) (base + 2), (short) (base + 3)};
            for (short s : idx) {
                bin.putShort(s);
            }
        }
        byte[] binBytes = bin.array();

        String json = "{"
                + "\"asset\":{\"version\":\"2.0\",\"generator\":\"NeoCatroid-3D-V2\"},"
                + "\"scene\":0,\"scenes\":[{\"nodes\":[0]}],"
                + "\"nodes\":[{\"mesh\":0,\"name\":\"V2Cube\"}],"
                + "\"meshes\":[{\"primitives\":[{"
                + "\"attributes\":{\"POSITION\":0,\"NORMAL\":1,\"TEXCOORD_0\":2},"
                + "\"indices\":3,\"material\":0"
                + "}]}],"
                + "\"materials\":[{"
                + "\"name\":\"V2CubeMat\","
                + "\"pbrMetallicRoughness\":{"
                + "\"baseColorFactor\":[" + r + "," + g + "," + b + "," + a + "],"
                + "\"metallicFactor\":0.1,\"roughnessFactor\":0.6"
                + "}}],"
                + "\"buffers\":[{\"byteLength\":840}],"
                + "\"bufferViews\":["
                + "{\"buffer\":0,\"byteOffset\":0,\"byteLength\":288,\"target\":34962},"
                + "{\"buffer\":0,\"byteOffset\":288,\"byteLength\":288,\"target\":34962},"
                + "{\"buffer\":0,\"byteOffset\":576,\"byteLength\":192,\"target\":34962},"
                + "{\"buffer\":0,\"byteOffset\":768,\"byteLength\":72,\"target\":34963}"
                + "],"
                + "\"accessors\":["
                + "{\"bufferView\":0,\"componentType\":5126,\"count\":24,\"type\":\"VEC3\","
                + "\"max\":[" + h + "," + h + "," + h + "],\"min\":[" + (-h) + "," + (-h) + "," + (-h) + "]},"
                + "{\"bufferView\":1,\"componentType\":5126,\"count\":24,\"type\":\"VEC3\"},"
                + "{\"bufferView\":2,\"componentType\":5126,\"count\":24,\"type\":\"VEC2\"},"
                + "{\"bufferView\":3,\"componentType\":5123,\"count\":36,\"type\":\"SCALAR\"}"
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

    public static byte[] buildDefaultGlb() {
        return buildGlb(2f, new float[]{0.75f, 0.78f, 0.82f, 1f});
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
