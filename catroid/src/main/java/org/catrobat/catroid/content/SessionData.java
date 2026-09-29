package org.catrobat.catroid.content;

import java.util.concurrent.ConcurrentHashMap;

public final class SessionData {

    private static final ConcurrentHashMap<String, Object> VALUES = new ConcurrentHashMap<>();

    private SessionData() {
    }

    public static void set(String name, Object value) {
        if (name == null || name.isEmpty()) {
            return;
        }
        if (value == null) {
            VALUES.remove(name);
            return;
        }
        VALUES.put(name, value);
    }

    public static Object get(String name) {
        if (name == null) {
            return 0.0;
        }
        Object value = VALUES.get(name);
        return value == null ? 0.0 : value;
    }

    public static boolean exists(String name) {
        return name != null && VALUES.containsKey(name);
    }

    public static void remove(String name) {
        if (name != null) {
            VALUES.remove(name);
        }
    }

    public static void clear() {
        VALUES.clear();
    }

    public static double asDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
}
