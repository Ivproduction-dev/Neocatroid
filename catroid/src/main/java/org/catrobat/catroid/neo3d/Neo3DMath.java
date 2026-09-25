package org.catrobat.catroid.neo3d;

public final class Neo3DMath {

    private Neo3DMath() {
    }

    public static float degToRad(float deg) {
        return deg * (float) Math.PI / 180f;
    }

    public static float[] vec3(float x, float y, float z) {
        return new float[]{x, y, z};
    }

    public static float[] vec3Copy(float[] v) {
        return new float[]{v[0], v[1], v[2]};
    }

    public static float[] quatIdentity() {
        return new float[]{0f, 0f, 0f, 1f};
    }

    public static float[] quatNormalize(float[] q) {
        float len = (float) Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
        if (len < 1e-8f) {
            return quatIdentity();
        }
        return new float[]{q[0] / len, q[1] / len, q[2] / len, q[3] / len};
    }

    public static float[] quatFromEulerDeg(float yawDeg, float pitchDeg, float rollDeg) {
        float hy = degToRad(yawDeg) * 0.5f;
        float hx = degToRad(pitchDeg) * 0.5f;
        float hz = degToRad(rollDeg) * 0.5f;
        float cy = (float) Math.cos(hy);
        float sy = (float) Math.sin(hy);
        float cx = (float) Math.cos(hx);
        float sx = (float) Math.sin(hx);
        float cz = (float) Math.cos(hz);
        float sz = (float) Math.sin(hz);
        float[] qy = {0f, sy, 0f, cy};
        float[] qx = {sx, 0f, 0f, cx};
        float[] qz = {0f, 0f, sz, cz};
        return quatNormalize(quatMul(quatMul(qy, qx), qz));
    }

    public static float[] quatMul(float[] a, float[] b) {
        return new float[]{
                a[3] * b[0] + a[0] * b[3] + a[1] * b[2] - a[2] * b[1],
                a[3] * b[1] - a[0] * b[2] + a[1] * b[3] + a[2] * b[0],
                a[3] * b[2] + a[0] * b[1] - a[1] * b[0] + a[2] * b[3],
                a[3] * b[3] - a[0] * b[0] - a[1] * b[1] - a[2] * b[2]
        };
    }

    public static float[] mat4Identity() {
        float[] m = new float[16];
        m[0] = 1f;
        m[5] = 1f;
        m[10] = 1f;
        m[15] = 1f;
        return m;
    }

    public static float[] mat4Mul(float[] a, float[] b) {
        float[] out = new float[16];
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                out[col * 4 + row]
                        = a[row] * b[col * 4]
                        + a[4 + row] * b[col * 4 + 1]
                        + a[8 + row] * b[col * 4 + 2]
                        + a[12 + row] * b[col * 4 + 3];
            }
        }
        return out;
    }

    public static float[] mat4FromTRS(float[] pos, float[] quat, float[] scale) {
        float x = quat[0];
        float y = quat[1];
        float z = quat[2];
        float w = quat[3];
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;
        float sx = scale[0];
        float sy = scale[1];
        float sz = scale[2];
        float[] m = new float[16];
        m[0] = (1f - 2f * (yy + zz)) * sx;
        m[1] = (2f * (xy + wz)) * sx;
        m[2] = (2f * (xz - wy)) * sx;
        m[3] = 0f;
        m[4] = (2f * (xy - wz)) * sy;
        m[5] = (1f - 2f * (xx + zz)) * sy;
        m[6] = (2f * (yz + wx)) * sy;
        m[7] = 0f;
        m[8] = (2f * (xz + wy)) * sz;
        m[9] = (2f * (yz - wx)) * sz;
        m[10] = (1f - 2f * (xx + yy)) * sz;
        m[11] = 0f;
        m[12] = pos[0];
        m[13] = pos[1];
        m[14] = pos[2];
        m[15] = 1f;
        return m;
    }

    public static float[] mat4Translation(float[] pos) {
        float[] m = mat4Identity();
        m[12] = pos[0];
        m[13] = pos[1];
        m[14] = pos[2];
        return m;
    }

    public static float[] mat4LookAt(float[] eye, float[] target, float[] up) {
        float zx = eye[0] - target[0];
        float zy = eye[1] - target[1];
        float zz = eye[2] - target[2];
        float zl = (float) Math.sqrt(zx * zx + zy * zy + zz * zz);
        if (zl < 1e-8f) {
            return mat4Identity();
        }
        zx /= zl;
        zy /= zl;
        zz /= zl;
        float xx = up[1] * zz - up[2] * zy;
        float xy = up[2] * zx - up[0] * zz;
        float xz = up[0] * zy - up[1] * zx;
        float xl = (float) Math.sqrt(xx * xx + xy * xy + xz * xz);
        if (xl < 1e-8f) {
            return mat4Identity();
        }
        xx /= xl;
        xy /= xl;
        xz /= xl;
        float yx = zy * xz - zz * xy;
        float yy = zz * xx - zx * xz;
        float yz = zx * xy - zy * xx;
        float[] m = new float[16];
        m[0] = xx;
        m[1] = yx;
        m[2] = zx;
        m[3] = 0f;
        m[4] = xy;
        m[5] = yy;
        m[6] = zy;
        m[7] = 0f;
        m[8] = xz;
        m[9] = yz;
        m[10] = zz;
        m[11] = 0f;
        m[12] = -(xx * eye[0] + xy * eye[1] + xz * eye[2]);
        m[13] = -(yx * eye[0] + yy * eye[1] + yz * eye[2]);
        m[14] = -(zx * eye[0] + zy * eye[1] + zz * eye[2]);
        m[15] = 1f;
        return m;
    }

    public static float[] mat4Perspective(float fovYDeg, float aspect, float near, float far) {
        float f = 1f / (float) Math.tan(degToRad(fovYDeg) * 0.5f);
        float[] m = new float[16];
        m[0] = f / aspect;
        m[5] = f;
        m[10] = (far + near) / (near - far);
        m[11] = -1f;
        m[14] = (2f * far * near) / (near - far);
        return m;
    }

    public static float[] projectToScreen(float[] worldPos, float[] viewProjMatrix,
            float viewportWidth, float viewportHeight) {
        float x = viewProjMatrix[0] * worldPos[0] + viewProjMatrix[4] * worldPos[1]
                + viewProjMatrix[8] * worldPos[2] + viewProjMatrix[12];
        float y = viewProjMatrix[1] * worldPos[0] + viewProjMatrix[5] * worldPos[1]
                + viewProjMatrix[9] * worldPos[2] + viewProjMatrix[13];
        float w = viewProjMatrix[3] * worldPos[0] + viewProjMatrix[7] * worldPos[1]
                + viewProjMatrix[11] * worldPos[2] + viewProjMatrix[15];
        if (w <= 1e-8f) {
            return null;
        }
        float ndcX = x / w;
        float ndcY = y / w;
        return new float[]{(ndcX * 0.5f + 0.5f) * viewportWidth,
                (ndcY * 0.5f + 0.5f) * viewportHeight};
    }

    public static boolean epsilonEquals(float a, float b, float eps) {
        return Math.abs(a - b) <= eps;
    }

    public static float[] yawPitchToTarget(float[] eye, float[] target) {
        float dx = target[0] - eye[0];
        float dy = target[1] - eye[1];
        float dz = target[2] - eye[2];
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-8f) {
            return new float[]{0f, 0f};
        }
        dx /= len;
        dy /= len;
        dz /= len;
        float clamped = Math.max(-1f, Math.min(1f, dy));
        float pitchDeg = (float) Math.toDegrees(Math.asin(clamped));
        float yawDeg = (float) Math.toDegrees(Math.atan2(-dx, -dz));
        return new float[]{yawDeg, pitchDeg};
    }

    public static float distance3(float[] a, float[] b) {
        float dx = a[0] - b[0];
        float dy = a[1] - b[1];
        float dz = a[2] - b[2];
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
