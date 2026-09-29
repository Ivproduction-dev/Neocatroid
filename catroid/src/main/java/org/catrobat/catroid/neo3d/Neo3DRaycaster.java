package org.catrobat.catroid.neo3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Neo3DRaycaster {

    public static final class Target {
        public final String name;
        public final float x;
        public final float y;
        public final float z;
        public final float radius;

        public Target(String name, float x, float y, float z, float radius) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.radius = radius;
        }
    }

    public static final class Hit {
        public final String name;
        public final float distance;
        public final float x;
        public final float y;
        public final float z;

        public Hit(String name, float distance, float x, float y, float z) {
            this.name = name;
            this.distance = distance;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private Neo3DRaycaster() {
    }

    public static List<Hit> cast(float ox, float oy, float oz, float dx, float dy, float dz,
            float maxDistance, List<Target> targets, int maxHits) {
        List<Hit> hits = new ArrayList<>();
        if (targets == null || targets.isEmpty() || maxHits <= 0 || maxDistance <= 0f) {
            return hits;
        }
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-8f || Float.isNaN(len) || Float.isInfinite(len)) {
            return hits;
        }
        float nx = dx / len;
        float ny = dy / len;
        float nz = dz / len;
        for (Target target : targets) {
            if (target == null || target.name == null || target.radius <= 0f) {
                continue;
            }
            float cx = target.x - ox;
            float cy = target.y - oy;
            float cz = target.z - oz;
            float t = cx * nx + cy * ny + cz * nz;
            if (t < 0f || t > maxDistance) {
                continue;
            }
            float d2 = cx * cx + cy * cy + cz * cz - t * t;
            float r2 = target.radius * target.radius;
            if (d2 > r2) {
                continue;
            }
            float hitDist = t - (float) Math.sqrt(Math.max(0f, r2 - d2));
            if (hitDist < 0f || hitDist > maxDistance) {
                continue;
            }
            hits.add(new Hit(target.name, hitDist, ox + nx * hitDist, oy + ny * hitDist,
                    oz + nz * hitDist));
        }
        Collections.sort(hits, new Comparator<Hit>() {
            @Override
            public int compare(Hit a, Hit b) {
                return Float.compare(a.distance, b.distance);
            }
        });
        if (hits.size() > maxHits) {
            return new ArrayList<>(hits.subList(0, maxHits));
        }
        return hits;
    }

    public static float boundingRadius(float scaleX, float scaleY, float scaleZ) {
        float ax = Math.abs(scaleX);
        float ay = Math.abs(scaleY);
        float az = Math.abs(scaleZ);
        float radius = (float) Math.sqrt(ax * ax + ay * ay + az * az);
        return Math.max(0.05f, radius);
    }
}
