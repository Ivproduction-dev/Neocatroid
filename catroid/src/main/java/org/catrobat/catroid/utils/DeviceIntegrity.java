package org.catrobat.catroid.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.lang.reflect.Method;

public final class DeviceIntegrity {

    private static final String[] SU_PATHS = {
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/su/bin/su"
    };

    private static final String[] ROOT_PACKAGES = {
            "eu.chainfire.supersu",
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot"
    };

    private static final String GMS_PACKAGE = "com.google.android.gms";

    private DeviceIntegrity() {
    }

    public static double isRooted(Context context) {
        try {
            if (Build.TAGS != null && Build.TAGS.contains("test-keys")) {
                return 1.0;
            }
            for (String path : SU_PATHS) {
                if (new File(path).exists()) {
                    return 1.0;
                }
            }
            if (context != null) {
                PackageManager manager = context.getPackageManager();
                if (manager != null) {
                    for (String pkg : ROOT_PACKAGES) {
                        try {
                            manager.getPackageInfo(pkg, 0);
                            return 1.0;
                        } catch (PackageManager.NameNotFoundException ignored) {
                        }
                    }
                }
            }
            if ("1".equals(getSystemProperty("ro.debuggable"))
                    || "0".equals(getSystemProperty("ro.secure"))) {
                return 1.0;
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    public static double isBootloaderUnlocked() {
        try {
            if ("0".equals(getSystemProperty("ro.boot.flash.locked"))) {
                return 1.0;
            }
            if ("orange".equalsIgnoreCase(getSystemProperty("ro.boot.verifiedbootstate"))) {
                return 1.0;
            }
            if ("unlocked".equalsIgnoreCase(getSystemProperty("ro.boot.vbmeta.device_state"))) {
                return 1.0;
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    public static double isEmulator() {
        try {
            if (matches(Build.FINGERPRINT, "generic", "unknown", "vbox", "test-keys")) {
                return 1.0;
            }
            if (matches(Build.MODEL, "google_sdk", "emulator", "android sdk built for x86")) {
                return 1.0;
            }
            if (matches(Build.MANUFACTURER, "genymotion")) {
                return 1.0;
            }
            if (matches(Build.HARDWARE, "goldfish", "ranchu", "vbox86")) {
                return 1.0;
            }
            if (matches(Build.PRODUCT, "sdk", "emulator", "vbox86p", "google_sdk")) {
                return 1.0;
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    public static double isGmsInstalled(Context context) {
        return getGmsInfo(context) != null ? 1.0 : 0.0;
    }

    public static double isGmsSystemApp(Context context) {
        try {
            PackageInfo info = getGmsInfo(context);
            if (info == null || info.applicationInfo == null) {
                return 0.0;
            }
            int flags = info.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0
                    || (flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                return 1.0;
            }
        } catch (Exception ignored) {
        }
        return 0.0;
    }

    public static double playServicesStatus(Context context) {
        try {
            if (context == null) {
                return -1.0;
            }
            int code = com.google.android.gms.common.GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(context);
            return (double) code;
        } catch (Exception e) {
            return -1.0;
        }
    }

    private static PackageInfo getGmsInfo(Context context) {
        try {
            if (context == null || context.getPackageManager() == null) {
                return null;
            }
            return context.getPackageManager().getPackageInfo(GMS_PACKAGE, 0);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean matches(String value, String... parts) {
        if (value == null) {
            return false;
        }
        String lower = value.toLowerCase();
        for (String part : parts) {
            if (lower.contains(part)) {
                return true;
            }
        }
        return false;
    }

    private static String getSystemProperty(String key) {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);
            Object result = get.invoke(null, key);
            return result instanceof String ? (String) result : null;
        } catch (Exception e) {
            return null;
        }
    }
}
