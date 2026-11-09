package org.catrobat.catroid.ui

import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.tasks.Tasks
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.telemetry.TelemetryManager
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import java.util.concurrent.TimeUnit

object CrashReporter {

    private const val TAG = "CrashReporter"
    private const val SETTINGS_CRASH_REPORTS = "setting_enable_crash_reports"
    private const val REPORT_DIR_NAME = "crashReports"
    private const val MAX_LOGCAT_LINES = 1000
    private const val MAX_STACKTRACE_CHARS = 100_000
    private const val MAX_LOGCAT_CHARS = 90_000
    private const val MAX_MESSAGE_CHARS = 4_096
    private const val MAX_REPORT_CHARS = 200_000
    private const val COLLECTION_NAME = "crashes"
    private const val REPORT_FILE_PREFIX = "crash_"
    private const val REPORT_FILE_SUFFIX = ".txt"
    private const val CURRENT_SCHEMA_VERSION = 2

    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    fun isCrashReportingEnabled(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(SETTINGS_CRASH_REPORTS, true)
    }

    fun collectLogcat(): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-v", "threadtime", "*:E"))
            val text = process.inputStream.use { it.readBytes().toString(Charsets.UTF_8) }
            process.waitFor()
            val lines = text.lines()
            if (lines.size > MAX_LOGCAT_LINES) {
                lines.takeLast(MAX_LOGCAT_LINES).joinToString("\n")
            } else {
                text
            }
        } catch (e: Exception) {
            Log.w(TAG, "logcat collection failed", e)
            "logcat unavailable: ${e.message}"
        }
    }

    fun buildReport(context: Context, thread: Thread, throwable: Throwable, logcat: String): String {
        val now = System.currentTimeMillis()
        val deviceFields = TelemetryManager.buildDeviceFields(context)
        val exceptionMessage = (throwable.message ?: "").take(MAX_MESSAGE_CHARS)
        val buildWriter = StringWriter()
        val buildPrint = PrintWriter(buildWriter)
        buildPrint.println("TIME: " + timeFormat.format(Date(now)))
        buildPrint.println("TIME_MS: " + now)
        buildPrint.println("TIME_ISO: " + TelemetryManager.isoTime(now))
        buildPrint.println("TIMEZONE: " + TimeZone.getDefault().id)
        buildPrint.println("DATE: " + TelemetryManager.dateKeyOf(now))
        buildPrint.println("APP_VERSION: " + BuildConfig.VERSION_NAME)
        buildPrint.println("VERSION_CODE: " + BuildConfig.VERSION_CODE)
        buildPrint.println("FLAVOR: " + BuildConfig.FLAVOR)
        buildPrint.println("BUILD_TYPE: " + BuildConfig.BUILD_TYPE)
        buildPrint.println("PACKAGE_NAME: " + BuildConfig.APPLICATION_ID)
        buildPrint.println("ANDROID_VERSION: " + Build.VERSION.RELEASE)
        buildPrint.println("SDK_INT: " + Build.VERSION.SDK_INT)
        buildPrint.println("MANUFACTURER: " + Build.MANUFACTURER)
        buildPrint.println("BRAND: " + Build.BRAND)
        buildPrint.println("DEVICE: " + Build.DEVICE)
        buildPrint.println("BOARD: " + Build.BOARD)
        buildPrint.println("HARDWARE: " + Build.HARDWARE)
        buildPrint.println("DEVICE_NAME: " + deviceFields["device_name"])
        buildPrint.println("DEVICE_MODEL: " + Build.MODEL)
        buildPrint.println("CPU: " + deviceFields["cpu"])
        buildPrint.println("ABI: " + Build.SUPPORTED_ABIS.joinToString(","))
        buildPrint.println("SCREEN: " + deviceFields["screen"])
        buildPrint.println("LOCALE: " + Locale.getDefault().toLanguageTag())
        buildPrint.println("INSTALL_ID: " + deviceFields["install_id"])
        buildPrint.println("SESSION_ID: " + deviceFields["session_id"])
        buildPrint.println("THREAD: " + thread.name)
        buildPrint.println("MAIN_THREAD: " + (thread == android.os.Looper.getMainLooper().thread))
        buildPrint.println("EXCEPTION: " + throwable.javaClass.name + ": " + exceptionMessage)
        buildPrint.println("===== STACK TRACE =====")
        val stackWriter = StringWriter()
        val stackPrint = PrintWriter(stackWriter)
        throwable.printStackTrace(stackPrint)
        stackPrint.flush()
        val stackTrace = stackWriter.toString().take(MAX_STACKTRACE_CHARS)
        buildPrint.println(stackTrace)
        buildPrint.println("===== LOGCAT (E/F) =====")
        buildPrint.println(logcat)
        buildPrint.flush()
        return buildWriter.toString().take(MAX_REPORT_CHARS)
    }

    fun saveReport(context: Context, report: String): File? {
        return try {
            val dir = File(context.cacheDir, REPORT_DIR_NAME)
            if (!dir.exists() && !dir.mkdirs()) {
                return null
            }
            val file = File(dir, REPORT_FILE_PREFIX + UUID.randomUUID() + REPORT_FILE_SUFFIX)
            file.writeText(report)
            file
        } catch (e: Exception) {
            Log.w(TAG, "failed to save crash report", e)
            null
        }
    }

    private fun crashIdFromFile(file: File): String {
        val name = file.nameWithoutExtension.removePrefix(REPORT_FILE_PREFIX)
        return name.ifBlank { UUID.randomUUID().toString() }
    }

    private fun parseHeader(report: String): Map<String, String> {
        val headerEnd = report.indexOf("===== STACK TRACE =====")
        val section = if (headerEnd < 0) report else report.substring(0, headerEnd)
        val result = linkedMapOf<String, String>()
        for (line in section.lineSequence()) {
            val idx = line.indexOf(": ")
            if (idx > 0) {
                result[line.substring(0, idx)] = line.substring(idx + 2)
            }
        }
        return result
    }

    private fun extractSection(report: String, startMarker: String, endMarker: String? = null): String {
        val start = report.indexOf(startMarker)
        if (start < 0) {
            return ""
        }
        val contentStart = start + startMarker.length
        val end = if (endMarker != null) report.indexOf(endMarker, contentStart) else -1
        val content = if (end < 0) report.substring(contentStart) else report.substring(contentStart, end)
        return content.trim()
    }

    private fun buildFirestoreData(context: Context, file: File, report: String): LinkedHashMap<String, Any> {
        val header = parseHeader(report)
        val stackTrace = extractSection(report, "===== STACK TRACE =====", "===== LOGCAT (E/F) =====")
            .take(MAX_STACKTRACE_CHARS)
        val logcat = extractSection(report, "===== LOGCAT (E/F) =====")
            .take(MAX_LOGCAT_CHARS)
        val timestamp = header["TIME_MS"]?.toLongOrNull() ?: file.lastModified()
        val exceptionField = header["EXCEPTION"].orEmpty()
        val separator = exceptionField.indexOf(": ")
        val exceptionType = if (separator > 0) exceptionField.substring(0, separator) else exceptionField
        val exceptionMessage = if (separator > 0) exceptionField.substring(separator + 2) else ""
        val date = header["DATE"]?.ifBlank { null } ?: TelemetryManager.dateKeyOf(timestamp)
        return linkedMapOf(
            "schema_version" to CURRENT_SCHEMA_VERSION,
            "crash_id" to crashIdFromFile(file),
            "install_id" to (header["INSTALL_ID"]?.ifBlank { null } ?: TelemetryManager.installId(context)),
            "session_id" to (header["SESSION_ID"]?.ifBlank { null } ?: TelemetryManager.sessionId()),
            "timestamp_ms" to timestamp,
            "time_iso" to (header["TIME_ISO"]?.ifBlank { null } ?: TelemetryManager.isoTime(timestamp)),
            "date" to date,
            "timezone" to header["TIMEZONE"].orEmpty(),
            "locale" to header["LOCALE"].orEmpty(),
            "device_name" to (header["DEVICE_NAME"] ?: TelemetryManager.deviceName()),
            "device_model" to header["DEVICE_MODEL"].orEmpty(),
            "manufacturer" to header["MANUFACTURER"].orEmpty(),
            "brand" to header["BRAND"].orEmpty(),
            "device" to header["DEVICE"].orEmpty(),
            "board" to header["BOARD"].orEmpty(),
            "hardware" to header["HARDWARE"].orEmpty(),
            "android_version" to header["ANDROID_VERSION"].orEmpty(),
            "sdk_int" to (header["SDK_INT"]?.toIntOrNull() ?: Build.VERSION.SDK_INT),
            "cpu" to header["CPU"].orEmpty(),
            "abi" to header["ABI"].orEmpty(),
            "screen" to header["SCREEN"].orEmpty(),
            "app_version" to header["APP_VERSION"].orEmpty(),
            "version_code" to (header["VERSION_CODE"]?.toIntOrNull() ?: BuildConfig.VERSION_CODE),
            "package_name" to header["PACKAGE_NAME"].orEmpty(),
            "flavor" to header["FLAVOR"].orEmpty(),
            "build_type" to header["BUILD_TYPE"].orEmpty(),
            "thread" to header["THREAD"].orEmpty(),
            "is_main_thread" to (header["MAIN_THREAD"].orEmpty() == "true"),
            "exception_type" to exceptionType,
            "exception_message" to exceptionMessage.take(MAX_MESSAGE_CHARS),
            "stacktrace" to stackTrace,
            "logcat" to logcat
        )
    }

    fun sendToFirestore(context: Context, file: File) {
        try {
            if (!isCrashReportingEnabled(context)) {
                return
            }
            val firestore = TelemetryManager.getTelemetryFirestore(context) ?: return
            val report = file.readText()
            val data = buildFirestoreData(context, file, report)
            val date = data["date"] as String
            val deviceKey = TelemetryManager.deviceKey(context)
            val crashId = data["crash_id"] as String
            firestore.collection(COLLECTION_NAME)
                .document(date)
                .collection(deviceKey)
                .document(crashId)
                .set(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        file.delete()
                    } else {
                        Log.w(TAG, "crash report send failed", task.exception)
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "crash report send failed", e)
        }
    }

    fun sendToFirestoreSync(context: Context, file: File, timeoutMs: Long = 4000): Boolean {
        try {
            if (!isCrashReportingEnabled(context)) {
                return false
            }
            if (!file.exists()) {
                return false
            }
            val firestore = TelemetryManager.getTelemetryFirestore(context) ?: return false
            val report = try {
                file.readText()
            } catch (e: Exception) {
                Log.w(TAG, "sync: failed to read report", e)
                return false
            }
            val data = buildFirestoreData(context, file, report)
            val date = data["date"] as String
            val deviceKey = TelemetryManager.deviceKey(context)
            val crashId = data["crash_id"] as String
            val task = firestore.collection(COLLECTION_NAME)
                .document(date)
                .collection(deviceKey)
                .document(crashId)
                .set(data)
            Tasks.await(task, timeoutMs, TimeUnit.MILLISECONDS)
            if (task.isSuccessful) {
                file.delete()
                Log.i(TAG, "sync crash report sent")
                return true
            } else {
                Log.w(TAG, "sync crash report failed", task.exception)
                return false
            }
        } catch (e: Exception) {
            if (e is InterruptedException) {
                Thread.currentThread().interrupt()
            }
            Log.w(TAG, "sync crash report failed: ${e.message}", e)
            return false
        }
    }

    fun sendPendingReports(context: Context) {
        try {
            val dir = File(context.cacheDir, REPORT_DIR_NAME)
            if (!dir.exists()) {
                return
            }
            dir.listFiles()
                ?.filter { it.isFile && it.name.startsWith(REPORT_FILE_PREFIX) && it.name.endsWith(REPORT_FILE_SUFFIX) }
                ?.forEach { sendToFirestore(context, it) }
        } catch (e: Exception) {
            Log.w(TAG, "pending crash reports check failed", e)
        }
    }
}
