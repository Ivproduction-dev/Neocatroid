package org.catrobat.catroid.telemetry

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.catrobat.catroid.BuildConfig
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

object TelemetryManager {

    private const val TAG = "TelemetryManager"
    private const val PREFS_NAME = "telemetry_prefs"
    private const val KEY_INSTALL_UUID = "telemetry_id"
    private const val COLLECTION_NAME = "telemetry"
    private const val TELEMETRY_APP_NAME = "telemetry"

    private const val FIREBASE_APPLICATION_ID = "1:910379908682:android:27ae2aa6d6fa5f2a2270b3"
    private const val FIREBASE_API_KEY = "AIzaSyCtR88-Jlj-7Vm63g6dBA7lmgp3xmmudZY"
    private const val FIREBASE_PROJECT_ID = "privacy-neocatroid"
    private const val FIREBASE_GCM_SENDER_ID = "910379908682"

    private val sessionId = UUID.randomUUID().toString()
    private var launchStartElapsedMs = 0L
    private val sentForProcess = AtomicBoolean(false)

    @JvmStatic
    fun recordLaunchStart() {
        launchStartElapsedMs = SystemClock.elapsedRealtime()
    }

    @JvmStatic
    fun onLaunchCompleted(context: Context) {
        if (!sentForProcess.compareAndSet(false, true)) {
            return
        }
        val launchDurationMs = if (launchStartElapsedMs > 0L) {
            SystemClock.elapsedRealtime() - launchStartElapsedMs
        } else {
            0L
        }
        sendTelemetry(context, launchDurationMs)
    }

    fun sessionId(): String = sessionId

    fun installId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_INSTALL_UUID, null)
        if (!existing.isNullOrEmpty()) {
            return existing
        }
        val newUuid = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_INSTALL_UUID, newUuid).apply()
        return newUuid
    }

    internal fun deviceName(): String {
        val manufacturer = Build.MANUFACTURER.orEmpty().trim()
        val model = Build.MODEL.orEmpty().trim()
        val combined = listOf(manufacturer, model).filter { it.isNotEmpty() }.joinToString(" ")
        return combined.ifBlank { model.ifBlank { "unknown" } }.replace('/', '_')
    }

    internal fun deviceKey(context: Context): String {
        val safe = deviceName()
            .replace(Regex("[^\\p{L}\\p{N} ._-]"), "_")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(48)
            .ifBlank { "device" }
        return "$safe-${installId(context).take(8)}"
    }

    internal fun cpuName(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manufacturer = Build.SOC_MANUFACTURER.orEmpty()
            val model = Build.SOC_MODEL.orEmpty()
            listOf(manufacturer, model)
                .filter { it.isNotBlank() }
                .joinToString(" ")
        } else {
            Build.HARDWARE.orEmpty()
        }
    }

    internal fun screenResolution(context: Context): String {
        @Suppress("DEPRECATION")
        return try {
            val metrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bounds = windowManager.maximumWindowMetrics.bounds
                "${bounds.width()}x${bounds.height()}"
            } else {
                windowManager.defaultDisplay.getRealMetrics(metrics)
                "${metrics.widthPixels}x${metrics.heightPixels}"
            }
        } catch (e: Exception) {
            ""
        }
    }

    internal fun isoTime(timeMs: Long): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US).format(Date(timeMs))
    }

    internal fun dateKeyOf(timeMs: Long): String {
        return LocalDate.ofInstant(java.time.Instant.ofEpochMilli(timeMs), ZoneId.systemDefault()).toString()
    }

    internal fun buildDeviceFields(context: Context): LinkedHashMap<String, Any> {
        val now = System.currentTimeMillis()
        return linkedMapOf(
            "install_id" to installId(context),
            "session_id" to sessionId,
            "timestamp_ms" to now,
            "time_iso" to isoTime(now),
            "date" to dateKeyOf(now),
            "timezone" to TimeZone.getDefault().id,
            "locale" to Locale.getDefault().toLanguageTag(),
            "device_name" to deviceName(),
            "device_model" to Build.MODEL.orEmpty(),
            "manufacturer" to Build.MANUFACTURER.orEmpty(),
            "brand" to Build.BRAND.orEmpty(),
            "device" to Build.DEVICE.orEmpty(),
            "board" to Build.BOARD.orEmpty(),
            "hardware" to Build.HARDWARE.orEmpty(),
            "android_version" to Build.VERSION.RELEASE,
            "sdk_int" to Build.VERSION.SDK_INT,
            "cpu" to cpuName(),
            "abi" to Build.SUPPORTED_ABIS.joinToString(","),
            "screen" to screenResolution(context),
            "app_version" to BuildConfig.VERSION_NAME,
            "version_code" to BuildConfig.VERSION_CODE,
            "package_name" to BuildConfig.APPLICATION_ID,
            "flavor" to BuildConfig.FLAVOR,
            "build_type" to BuildConfig.BUILD_TYPE
        )
    }

    private fun sendTelemetry(context: Context, launchDurationMs: Long) {
        try {
            val firestore = getTelemetryFirestore(context) ?: return
            val deviceKey = deviceKey(context)
            val date = dateKeyOf(System.currentTimeMillis())
            val data = buildDeviceFields(context)
            data["launch_duration"] = launchDurationMs
            data["last_seen"] = date
            firestore.collection(COLLECTION_NAME)
                .document(date)
                .collection(deviceKey)
                .document(sessionId)
                .set(data, SetOptions.merge())
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "telemetry send failed", task.exception)
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "telemetry send failed", e)
        }
    }

    internal fun getTelemetryFirestore(context: Context): FirebaseFirestore? {
        val app = getTelemetryApp(context) ?: return null
        return FirebaseFirestore.getInstance(app)
    }

    internal fun getTelemetryApp(context: Context): FirebaseApp? {
        return try {
            FirebaseApp.getApps(context).firstOrNull { it.name == TELEMETRY_APP_NAME }?.let { return it }
            val options = FirebaseOptions.Builder()
                .setApplicationId(FIREBASE_APPLICATION_ID)
                .setApiKey(FIREBASE_API_KEY)
                .setProjectId(FIREBASE_PROJECT_ID)
                .setGcmSenderId(FIREBASE_GCM_SENDER_ID)
                .build()
            FirebaseApp.initializeApp(context, options, TELEMETRY_APP_NAME)
        } catch (e: Exception) {
            Log.w(TAG, "telemetry firebase app init failed", e)
            null
        }
    }
}
