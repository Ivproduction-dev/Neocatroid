package org.catrobat.catroid.neo3d;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

public final class Neo3DPrimitiveMeshes {

    public static final String ASSET_KEY_CUBE = "embedded:neo-cube";
    public static final String ASSET_KEY_SPHERE = "embedded:neo-sphere";
    public static final String ASSET_KEY_CYLINDER = "embedded:neo-cylinder";
    public static final String ASSET_KEY_EDITOR_GRID = "embedded:neo-editor-grid";

    public static byte[] buildGrid(float extent, float cell, float thickness,
            float[] baseColorRgba) {
        float safeExtent = Math.max(1f, extent);
        float safeCell = Math.max(0.1f, cell);
        float halfThick = Math.max(0.001f, thickness) * 0.5f;
        int linesPerAxis = (int) (safeExtent * 2f / safeCell) + 1;
        List<Float> pos = new ArrayList<>(linesPerAxis * 2 * 4 * 3);
        List<Float> nor = new ArrayList<>(linesPerAxis * 2 * 4 * 3);
        List<Float> uv = new ArrayList<>(linesPerAxis * 2 * 4 * 2);
        List<Integer> idx = new ArrayList<>(linesPerAxis * 2 * 6);
        float[] quadUv = {0, 0, 1, 0, 1, 1, 0, 1};
        for (int i = 0; i < linesPerAxis; i++) {
            float coord = -safeExtent + i * safeCell;
            addGridQuad(pos, nor, uv, idx, quadUv,
                    -safeExtent, coord - halfThick, safeExtent, coord + halfThick);
            addGridQuad(pos, nor, uv, idx, quadUv,
                    coord - halfThick, -safeExtent, coord + halfThick, safeExtent);
        }
        return packGlb("NeoEditorGrid", pos, nor, uv, idx, baseColorRgba);
    }

    private static void addGridQuad(List<Float> pos, List<Float> nor, List<Float> uv,
            List<Integer> idx, float[] quadUv,
            float minX, float minZ, float maxX, float maxZ) {
        int base = pos.size() / 3;
        float[] xs = {minX, maxX, maxX, minX};
        float[] zs = {minZ, minZ, maxZ, maxZ};
        for (int v = 0; v < 4; v++) {
            pos.add(xs[v]);
            pos.add(0f);
            pos.add(zs[v]);
            nor.add(0f);
            nor.add(1f);
            nor.add(0f);
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

    public static byte[] convertObjToGlb(byte[] objBytes, float[] color) {
        List<float[]> sourcePositions = new ArrayList<>();
        List<float[]> sourceNormals = new ArrayList<>();
        List<float[]> sourceUvs = new ArrayList<>();
        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> uvs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(objBytes), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                String[] tokens = trimmed.split("\\s+");
                if ("v".equals(tokens[0]) && tokens.length >= 4) {
                    sourcePositions.add(new float[]{Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])});
                } else if ("vn".equals(tokens[0]) && tokens.length >= 4) {
                    sourceNormals.add(new float[]{Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])});
                } else if ("vt".equals(tokens[0]) && tokens.length >= 3) {
                    sourceUvs.add(new float[]{Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])});
                } else if ("f".equals(tokens[0]) && tokens.length >= 4) {
                    ObjCorner first = parseObjCorner(tokens[1], sourcePositions.size(),
                            sourceUvs.size(), sourceNormals.size());
                    ObjCorner previous = parseObjCorner(tokens[2], sourcePositions.size(),
                            sourceUvs.size(), sourceNormals.size());
                    for (int cornerIndex = 3; cornerIndex < tokens.length; cornerIndex++) {
                        ObjCorner next = parseObjCorner(tokens[cornerIndex], sourcePositions.size(),
                                sourceUvs.size(), sourceNormals.size());
                        addObjTriangle(first, previous, next, sourcePositions, sourceNormals,
                                sourceUvs, positions, normals, uvs, indices);
                        previous = next;
                        if (indices.size() > 300000) {
                            throw new IllegalArgumentException("OBJ has too many triangles");
                        }
                    }
                }
            }
        } catch (java.io.IOException | NumberFormatException e) {
            throw new IllegalArgumentException("Invalid OBJ model", e);
        }
        if (positions.isEmpty()) throw new IllegalArgumentException("OBJ contains no faces");
        return packGlb("ImportedOBJ", positions, normals, uvs, indices, color);
    }

    public static byte[] convertStlToGlb(byte[] stlBytes, float[] color) {
        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> uvs = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        if (stlBytes.length >= 84) {
            ByteBuffer input = ByteBuffer.wrap(stlBytes).order(ByteOrder.LITTLE_ENDIAN);
            long triangles = Integer.toUnsignedLong(input.getInt(80));
            if (triangles <= 100000 && 84L + triangles * 50L == stlBytes.length) {
                input.position(84);
                for (int i = 0; i < triangles; i++) {
                    float[] normal = normalize(new float[]{input.getFloat(), input.getFloat(),
                            input.getFloat()});
                    float[][] vertices = new float[3][3];
                    for (int vertex = 0; vertex < 3; vertex++) {
                        vertices[vertex][0] = input.getFloat();
                        vertices[vertex][1] = input.getFloat();
                        vertices[vertex][2] = input.getFloat();
                    }
                    input.getShort();
                    appendStlTriangle(vertices, normal, positions, normals, uvs, indices);
                }
            }
        }
        if (positions.isEmpty()) {
            String text = new String(stlBytes, StandardCharsets.US_ASCII);
            float[] facetNormal = {0f, 1f, 0f};
            List<float[]> vertices = new ArrayList<>(3);
            for (String line : text.split("\\r?\\n")) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length >= 5 && "facet".equals(tokens[0])
                        && "normal".equals(tokens[1])) {
                    facetNormal = normalize(new float[]{Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4])});
                } else if (tokens.length >= 4 && "vertex".equals(tokens[0])) {
                    vertices.add(new float[]{Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])});
                    if (vertices.size() == 3) {
                        appendStlTriangle(vertices.toArray(new float[0][0]), facetNormal,
                                positions, normals, uvs, indices);
                        vertices.clear();
                        if (indices.size() >= 300000) break;
                    }
                }
            }
        }
        if (positions.isEmpty()) throw new IllegalArgumentException("STL contains no triangles");
        return packGlb("ImportedSTL", positions, normals, uvs, indices, color);
    }

    private static void appendStlTriangle(float[][] vertices, float[] normal,
            List<Float> positions, List<Float> normals, List<Float> uvs,
            List<Integer> indices) {
        float[] faceNormal = normalize(normal);
        for (float[] vertex : vertices) {
            positions.add(vertex[0]); positions.add(vertex[1]); positions.add(vertex[2]);
            normals.add(faceNormal[0]); normals.add(faceNormal[1]); normals.add(faceNormal[2]);
            uvs.add(0f); uvs.add(0f);
            indices.add(indices.size());
        }
    }

    private static ObjCorner parseObjCorner(String token, int positionCount, int uvCount,
            int normalCount) {
        String[] parts = token.split("/", -1);
        int position = objIndex(parts[0], positionCount);
        int uv = parts.length > 1 && !parts[1].isEmpty() ? objIndex(parts[1], uvCount) : -1;
        int normal = parts.length > 2 && !parts[2].isEmpty()
                ? objIndex(parts[2], normalCount) : -1;
        if (position < 0 || position >= positionCount
                || uv >= uvCount || normal >= normalCount) {
            throw new IllegalArgumentException("OBJ face references a missing vertex");
        }
        return new ObjCorner(position, uv, normal);
    }

    private static int objIndex(String value, int count) {
        int index = Integer.parseInt(value);
        return index > 0 ? index - 1 : count + index;
    }

    private static void addObjTriangle(ObjCorner a, ObjCorner b, ObjCorner c,
            List<float[]> sourcePositions, List<float[]> sourceNormals, List<float[]> sourceUvs,
            List<Float> positions, List<Float> normals, List<Float> uvs,
            List<Integer> indices) {
        float[] pa = sourcePositions.get(a.position);
        float[] pb = sourcePositions.get(b.position);
        float[] pc = sourcePositions.get(c.position);
        float[] faceNormal = normalize(cross(pb[0] - pa[0], pb[1] - pa[1], pb[2] - pa[2],
                pc[0] - pa[0], pc[1] - pa[1], pc[2] - pa[2]));
        ObjCorner[] corners = {a, b, c};
        for (ObjCorner corner : corners) {
            float[] position = sourcePositions.get(corner.position);
            float[] normal = corner.normal >= 0
                    ? normalize(sourceNormals.get(corner.normal)) : faceNormal;
            float[] texture = corner.uv >= 0 ? sourceUvs.get(corner.uv) : new float[]{0f, 0f};
            positions.add(position[0]); positions.add(position[1]); positions.add(position[2]);
            normals.add(normal[0]); normals.add(normal[1]); normals.add(normal[2]);
            uvs.add(texture[0]); uvs.add(texture[1]);
            indices.add(indices.size());
        }
    }

    private static float[] cross(float ax, float ay, float az, float bx, float by, float bz) {
        return new float[]{ay * bz - az * by, az * bx - ax * bz, ax * by - ay * bx};
    }

    private static float[] normalize(float[] vector) {
        float length = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]
                + vector[2] * vector[2]);
        if (length < 1.0e-6f) return new float[]{0f, 1f, 0f};
        return new float[]{vector[0] / length, vector[1] / length, vector[2] / length};
    }

    private static final class ObjCorner {
        final int position;
        final int uv;
        final int normal;

        ObjCorner(int position, int uv, int normal) {
            this.position = position;
            this.uv = uv;
            this.normal = normal;
        }
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
