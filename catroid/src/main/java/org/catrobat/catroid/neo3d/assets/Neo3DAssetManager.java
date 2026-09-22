package org.catrobat.catroid.neo3d.assets;

import java.util.LinkedHashMap;
import java.util.Map;

public class Neo3DAssetManager {

    public enum AssetKind {
        MODEL,
        TEXTURE
    }

    public static class Handle {
        public final String key;
        public final AssetKind kind;
        public int refCount;
        public long loadTimeMs;
        public long sizeBytesEstimate;

        Handle(String key, AssetKind kind, long loadTimeMs, long sizeBytesEstimate) {
            this.key = key;
            this.kind = kind;
            this.refCount = 0;
            this.loadTimeMs = loadTimeMs;
            this.sizeBytesEstimate = sizeBytesEstimate;
        }
    }

    public static final String[] UNSUPPORTED_GLB_MARKERS = {
            "KHR_draco_mesh_compression",
            "EXT_meshopt_compression",
            "KHR_texture_basisu"
    };

    public static final int DEFAULT_MAX_TEXTURE_DIMENSION = 2048;
    public static final int DEFAULT_MAX_ENTRIES = 128;

    private final int maxEntries;
    private final int maxTextureDimension;
    private final LinkedHashMap<String, Handle> entries;
    private long totalAcquisitions;
    private long totalReleases;
    private long evictions;

    public Neo3DAssetManager() {
        this(DEFAULT_MAX_ENTRIES, DEFAULT_MAX_TEXTURE_DIMENSION);
    }

    public Neo3DAssetManager(int maxEntries, int maxTextureDimension) {
        this.maxEntries = maxEntries;
        this.maxTextureDimension = maxTextureDimension;
        this.entries = new LinkedHashMap<>(16, 0.75f, true);
    }

    public synchronized Handle acquire(AssetKind kind, String key, long loadTimeMs,
            long sizeBytesEstimate) {
        if (key == null) {
            return null;
        }
        Handle handle = entries.get(key);
        if (handle == null) {
            handle = new Handle(key, kind, loadTimeMs, sizeBytesEstimate);
            entries.put(key, handle);
        }
        handle.refCount++;
        totalAcquisitions++;
        evictIfNeeded();
        return handle;
    }

    public synchronized boolean release(String key) {
        Handle handle = entries.get(key);
        if (handle == null) {
            return false;
        }
        totalReleases++;
        handle.refCount = Math.max(0, handle.refCount - 1);
        return handle.refCount == 0;
    }

    public synchronized int getRefCount(String key) {
        Handle handle = entries.get(key);
        return handle == null ? 0 : handle.refCount;
    }

    public synchronized int getEntryCount() {
        return entries.size();
    }

    public synchronized long getEstimatedBytes() {
        long total = 0;
        for (Handle h : entries.values()) {
            total += h.sizeBytesEstimate * Math.max(1, h.refCount);
        }
        return total;
    }

    public synchronized long getTotalAcquisitions() {
        return totalAcquisitions;
    }

    public synchronized long getTotalReleases() {
        return totalReleases;
    }

    public synchronized long getEvictions() {
        return evictions;
    }

    private void evictIfNeeded() {
        while (entries.size() > maxEntries) {
            String eldestZeroRef = null;
            for (Map.Entry<String, Handle> e : entries.entrySet()) {
                if (e.getValue().refCount == 0) {
                    eldestZeroRef = e.getKey();
                    break;
                }
            }
            if (eldestZeroRef == null) {
                return;
            }
            entries.remove(eldestZeroRef);
            evictions++;
        }
    }

    public int downsampleFactorFor(int width, int height) {
        int longest = Math.max(width, height);
        if (longest <= maxTextureDimension || longest <= 0) {
            return 1;
        }
        int factor = 1;
        while (longest / factor > maxTextureDimension) {
            factor *= 2;
        }
        return factor;
    }

    public static boolean hasUnsupportedGlbExtension(byte[] glbBytes) {
        if (glbBytes == null || glbBytes.length < 20) {
            return false;
        }
        if (glbBytes[0] != 'g' || glbBytes[1] != 'l' || glbBytes[2] != 'T' || glbBytes[3] != 'F') {
            return false;
        }
        int jsonLen = readLe32(glbBytes, 12);
        if (jsonLen <= 0 || jsonLen > glbBytes.length - 20) {
            return false;
        }
        String json;
        try {
            json = new String(glbBytes, 20, jsonLen, "UTF-8");
        } catch (Exception e) {
            return false;
        }
        for (String marker : UNSUPPORTED_GLB_MARKERS) {
            if (json.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    private static int readLe32(byte[] b, int off) {
        return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8)
                | ((b[off + 2] & 0xFF) << 16) | ((b[off + 3] & 0xFF) << 24);
    }
}
