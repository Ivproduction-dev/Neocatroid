package org.catrobat.catroid.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.BatteryManager
import android.telephony.TelephonyManager

object EmulatorRisk {

    enum class Signal {
        BOARD, DEVICE, PRODUCT, HARDWARE, FINGERPRINT,
        BATTERY, CYCLE_COUNT, SENSORS, IMEI, SERIAL, BOOTLOADER, CPU_INFO
    }

    fun supportedSignals(apiLevel: Int): Map<Signal, String> = buildMap {
        put(Signal.BOARD, "API 1, no permission")
        put(Signal.DEVICE, "API 1, no permission")
        put(Signal.PRODUCT, "API 1, no permission")
        put(Signal.HARDWARE, "API 1, no permission")
        put(Signal.FINGERPRINT, "API 1, no permission")
        put(Signal.BATTERY, "ACTION_BATTERY_CHANGED sticky intent, no permission")
        put(Signal.SENSORS, "SensorManager, no permission")
        put(Signal.BOOTLOADER, "best effort via SystemProperties, no permission")
        put(Signal.CPU_INFO, "best effort via /proc/cpuinfo, no permission")
        if (apiLevel >= 34) {
            put(Signal.CYCLE_COUNT, "API 34, no permission")
        }
        put(Signal.IMEI, "API 26+, READ_PHONE_STATE; identifiers restricted since Android 10")
        put(Signal.SERIAL, "API 26+, READ_PRIVILEGED_PHONE_STATE; ordinary apps get unknown")
    }

    data class RawSignals(
        val board: String?,
        val device: String?,
        val product: String?,
        val manufacturer: String?,
        val model: String?,
        val fingerprint: String?,
        val hardware: String?,
        val bootloader: String?,
        val bootloaderStatus: String?,
        val batteryPresent: Boolean?,
        val batteryLevelPercent: Int,
        val batteryTemperatureDeciC: Int,
        val batteryCycleCount: Int,
        val batteryStatus: Int,
        val sensorCount: Int,
        val hasAccelerometer: Boolean,
        val hasGyroscope: Boolean,
        val hasLight: Boolean,
        val hasProximity: Boolean,
        val hasMagnetic: Boolean,
        val imeiReadable: Boolean,
        val imeiValue: String?,
        val serialReadable: Boolean,
        val serialValue: String?,
        val cpuInfo: String?,
        val apiLevel: Int = 31,
        val cycleCountSupported: Boolean = false,
        val skippedSignals: List<String> = emptyList()
    )

    data class Mark(
        val weight: Int,
        val reason: String
    )

    data class Result(
        val risk: Int,
        val apiLevel: Int,
        val suspiciousTotal: Int,
        val positiveTotal: Int,
        val suspicious: List<Mark>,
        val positive: List<Mark>,
        val skippedSignals: List<String>,
        val raw: Map<String, String>
    )

    private val STRONG_EMU_MARKERS = listOf(
        "qemu", "goldfish", "ranchu", "vbox", "bochs", "vmware", "hyperv",
        "emulator", "android_sdk", "google_sdk", "sdk_gphone", "sdk_phone",
        "gphone", "droid4x", "genymotion", "bluestacks", "nox", "memu", "windroy"
    )
    private val WEAK_EMU_MARKERS = listOf(
        "generic", "unknown", "sdk", "test-keys"
    )
    private val CHIPSET_REGEX = Regex(
        "mt\\d{4,5}|sm\\d{4,5}|sdm[0-9]{3,4}|exynos|kirin|tensor|gs1[0-9]{2}|" +
            "qcom|qualcomm|mediatek|unisoc|spreadtrum|bcm[0-9]{4}|hi[0-9]{3,4}|" +
            "helio|snapdragon|mt6769|mt6789|mt6853|mt6893",
        RegexOption.IGNORE_CASE
    )

    fun read(context: Context?): RawSignals {
        val api = Build.VERSION.SDK_INT
        val supported = supportedSignals(api)
        val skipped = mutableListOf<String>()
        val cycleCountSupported = supported.containsKey(Signal.CYCLE_COUNT)
        if (!cycleCountSupported) {
            skipped += "cycleCount skipped: requires API 34, device API $api"
        }

        val battery = readBattery(context, cycleCountSupported)
        if (battery == null) {
            skipped += "battery skipped: ACTION_BATTERY_CHANGED sticky intent unavailable"
        }

        val sensors = readSensors(context)
        if (sensors == null) {
            skipped += "sensors skipped: SENSOR_SERVICE unavailable"
        } else if (sensors.count == 0) {
            skipped += "sensors skipped: 0 reported; TV/tablet context is not scored"
        }

        val (imeiReadable, imeiValue, imeiSkipReason) = readImei(context)
        imeiSkipReason?.let { skipped += it }

        val (serialReadable, serialValue, serialSkipReason) = readSerial()
        serialSkipReason?.let { skipped += it }

        val bootloaderStatus = bootloaderState()
        if (bootloaderStatus == null) {
            skipped += "bootloader skipped: ro.boot.* unavailable via SystemProperties"
        }

        val cpuInfo = readCpuInfo()
        if (cpuInfo == null) {
            skipped += "cpuInfo skipped: /proc/cpuinfo unavailable"
        }

        return RawSignals(
            board = Build.BOARD?.takeIf { it.isNotBlank() },
            device = Build.DEVICE?.takeIf { it.isNotBlank() },
            product = Build.PRODUCT?.takeIf { it.isNotBlank() },
            manufacturer = Build.MANUFACTURER?.takeIf { it.isNotBlank() },
            model = Build.MODEL?.takeIf { it.isNotBlank() },
            fingerprint = Build.FINGERPRINT?.takeIf { it.isNotBlank() },
            hardware = Build.HARDWARE?.takeIf { it.isNotBlank() },
            bootloader = Build.BOOTLOADER?.takeIf { it.isNotBlank() },
            bootloaderStatus = bootloaderStatus,
            batteryPresent = battery?.present,
            batteryLevelPercent = battery?.levelPercent ?: -1,
            batteryTemperatureDeciC = battery?.temperature ?: -1,
            batteryCycleCount = battery?.cycleCount ?: -1,
            batteryStatus = battery?.status ?: -1,
            sensorCount = sensors?.count ?: -1,
            hasAccelerometer = sensors?.accelerometer ?: false,
            hasGyroscope = sensors?.gyroscope ?: false,
            hasLight = sensors?.light ?: false,
            hasProximity = sensors?.proximity ?: false,
            hasMagnetic = sensors?.magnetic ?: false,
            imeiReadable = imeiReadable,
            imeiValue = imeiValue,
            serialReadable = serialReadable,
            serialValue = serialValue,
            cpuInfo = cpuInfo,
            apiLevel = api,
            cycleCountSupported = cycleCountSupported,
            skippedSignals = skipped
        )
    }

    fun risk(context: Context?): Result = assess(read(context))

    fun assess(raw: RawSignals): Result {
        val suspicious = mutableListOf<Mark>()
        val positive = mutableListOf<Mark>()

        addDeviceIdentity(raw, suspicious, positive)

        addFingerprint(raw.fingerprint, suspicious)
        addHardware(raw.hardware, suspicious)
        addCpu(raw.cpuInfo, suspicious, positive)
        addBattery(raw, suspicious, positive)
        addSensors(raw, suspicious, positive)
        addImei(raw, suspicious, positive)
        addSerial(raw, suspicious, positive)
        addBootloader(raw.bootloaderStatus, positive)

        val s = suspicious.sumOf { it.weight }
        val p = positive.sumOf { it.weight }
        val risk = when {
            s == 0 -> 0
            p == 0 && s >= 4 -> 3
            s < p -> 1
            else -> 2
        }

        return Result(
            risk = risk,
            apiLevel = raw.apiLevel,
            suspiciousTotal = s,
            positiveTotal = p,
            suspicious = suspicious,
            positive = positive,
            skippedSignals = raw.skippedSignals,
            raw = rawMap(raw)
        )
    }

    private fun addDeviceIdentity(
        raw: RawSignals,
        suspicious: MutableList<Mark>,
        positive: MutableList<Mark>
    ) {
        val candidates = listOfNotNull(raw.board, raw.device, raw.product)
        if (candidates.isEmpty()) return
        val lowered = candidates.map { it.lowercase() }
        val strong = STRONG_EMU_MARKERS.firstOrNull { marker -> lowered.any { it.contains(marker) } }
        if (strong != null) {
            val source = candidates[lowered.indexOfFirst { it.contains(strong) }]
            suspicious += Mark(2, "device identity '$source' matches strong emulator marker '$strong'")
            return
        }
        val weak = WEAK_EMU_MARKERS.firstOrNull { marker -> lowered.any { it.contains(marker) } }
        if (weak != null) {
            val source = candidates[lowered.indexOfFirst { it.contains(weak) }]
            suspicious += Mark(1, "device identity '$source' matches weak emulator marker '$weak'")
            return
        }
        positive += Mark(2, "device identity '${raw.board ?: raw.device ?: raw.product}' does not match emulator markers")
    }

    private fun addFingerprint(fingerprint: String?, target: MutableList<Mark>) {
        if (fingerprint == null) return
        val lower = fingerprint.lowercase()
        val strong = STRONG_EMU_MARKERS.firstOrNull { lower.contains(it) }
        if (strong != null) {
            target += Mark(2, "fingerprint '$fingerprint' matches strong emulator marker '$strong'")
            return
        }
        val weak = WEAK_EMU_MARKERS.firstOrNull { lower.contains(it) }
        if (weak != null) {
            target += Mark(1, "fingerprint '$fingerprint' matches weak emulator marker '$weak'")
        }
    }

    private fun addHardware(hardware: String?, target: MutableList<Mark>) {
        if (hardware == null) return
        val lower = hardware.lowercase()
        val strong = STRONG_EMU_MARKERS.firstOrNull { lower.contains(it) }
        if (strong != null) {
            target += Mark(2, "hardware '$hardware' matches strong emulator marker '$strong'")
            return
        }
        val weak = WEAK_EMU_MARKERS.firstOrNull { lower.contains(it) }
        if (weak != null) {
            target += Mark(1, "hardware '$hardware' matches weak emulator marker '$weak'")
        }
    }

    private fun addCpu(cpuInfo: String?, suspicious: MutableList<Mark>, positive: MutableList<Mark>) {
        if (cpuInfo == null) return
        val lower = cpuInfo.lowercase()
        val hypervisor = listOf("qemu", "virtualbox", "vmware", "bochs", "hyperv", "hyper-v", "xen", "kvm")
            .firstOrNull { lower.contains(it) }
        if (hypervisor != null) {
            suspicious += Mark(2, "cpuinfo matches hypervisor '$hypervisor'")
            return
        }
        if (CHIPSET_REGEX.containsMatchIn(cpuInfo)) {
            val chip = CHIPSET_REGEX.find(cpuInfo)?.value ?: "chipset"
            positive += Mark(1, "cpuinfo matches known chipset '$chip'")
            return
        }
        if (lower.contains("generic") || lower.contains("unknown") || lower.isBlank()) {
            suspicious += Mark(1, "cpuinfo is generic or unknown")
        }
    }

    private fun addBattery(raw: RawSignals, suspicious: MutableList<Mark>, positive: MutableList<Mark>) {
        if (raw.batteryPresent == false) {
            suspicious += Mark(2, "battery absent or not reported")
            return
        }
        if (raw.batteryPresent == null) return
        if (raw.batteryTemperatureDeciC > 0) {
            positive += Mark(2, "battery reports temperature ${raw.batteryTemperatureDeciC / 10}°C")
            return
        }
        if (raw.cycleCountSupported &&
            raw.batteryLevelPercent == 100 &&
            raw.batteryCycleCount < 0
        ) {
            suspicious += Mark(2, "battery pinned at 100% with no temperature and no cycle count")
        }
    }

    private fun addSensors(raw: RawSignals, suspicious: MutableList<Mark>, positive: MutableList<Mark>) {
        if (raw.sensorCount < 0) return
        if (raw.sensorCount == 0) return
        val motion = raw.hasAccelerometer || raw.hasGyroscope
        val ambient = raw.hasLight || raw.hasProximity || raw.hasMagnetic
        if (motion && ambient && raw.sensorCount >= 3) {
            positive += Mark(2, "sensors present: ${raw.sensorCount} total (accel=${raw.hasAccelerometer}, gyro=${raw.hasGyroscope})")
        }
    }

    private fun imeiLooksInvalid(value: String?): Boolean {
        if (value == null) return true
        val trimmed = value.trim().lowercase()
        if (trimmed.isEmpty() || trimmed == "unknown" || trimmed == "null" || trimmed == "none") return true
        if (trimmed.startsWith("004999")) return true
        if (trimmed.contains("emulator") || trimmed.contains("5554")) return true
        val digits = trimmed.filter { it.isDigit() }
        if (digits.isNotEmpty() && (digits.all { it == '0' } || digits.length < 14)) return true
        return false
    }

    private fun serialLooksPlaceholder(value: String?): Boolean {
        if (value == null) return true
        val trimmed = value.trim().lowercase()
        if (trimmed.isEmpty() || trimmed == "unknown" || trimmed == "null" || trimmed == "none") return true
        if (trimmed.contains("emulator")) return true
        val digits = trimmed.filter { it.isDigit() }
        if (digits.isNotEmpty() && digits.all { it == '0' }) return true
        return false
    }

    private fun addImei(raw: RawSignals, suspicious: MutableList<Mark>, positive: MutableList<Mark>) {
        if (!raw.imeiReadable) return
        if (imeiLooksInvalid(raw.imeiValue)) {
            suspicious += Mark(1, "imei missing or placeholder (${raw.imeiValue ?: "empty"})")
        } else {
            positive += Mark(1, "imei present and not a placeholder")
        }
    }

    private fun addSerial(raw: RawSignals, suspicious: MutableList<Mark>, positive: MutableList<Mark>) {
        if (!raw.serialReadable) return
        if (serialLooksPlaceholder(raw.serialValue)) {
            suspicious += Mark(1, "serial missing or placeholder (${raw.serialValue ?: "empty"})")
        } else {
            positive += Mark(1, "serial present and not a placeholder")
        }
    }

    private fun addBootloader(status: String?, positive: MutableList<Mark>) {
        val value = status?.substringAfter('=', status)?.trim()?.lowercase()
        if (value.isNullOrEmpty() || value == "unknown") return
        positive += Mark(1, "bootloader state readable: $status")
    }

    private fun rawMap(raw: RawSignals): Map<String, String> = mapOf(
        "apiLevel" to raw.apiLevel.toString(),
        "cycleCountSupported" to raw.cycleCountSupported.toString(),
        "board" to (raw.board ?: "null"),
        "device" to (raw.device ?: "null"),
        "product" to (raw.product ?: "null"),
        "manufacturer" to (raw.manufacturer ?: "null"),
        "model" to (raw.model ?: "null"),
        "fingerprint" to (raw.fingerprint ?: "null"),
        "hardware" to (raw.hardware ?: "null"),
        "bootloader" to (raw.bootloader ?: "null"),
        "bootloaderStatus" to (raw.bootloaderStatus ?: "null"),
        "batteryPresent" to raw.batteryPresent.toString(),
        "batteryLevelPercent" to raw.batteryLevelPercent.toString(),
        "batteryTemperatureDeciC" to raw.batteryTemperatureDeciC.toString(),
        "batteryCycleCount" to raw.batteryCycleCount.toString(),
        "batteryStatus" to raw.batteryStatus.toString(),
        "sensorCount" to raw.sensorCount.toString(),
        "hasAccelerometer" to raw.hasAccelerometer.toString(),
        "hasGyroscope" to raw.hasGyroscope.toString(),
        "hasLight" to raw.hasLight.toString(),
        "hasProximity" to raw.hasProximity.toString(),
        "hasMagnetic" to raw.hasMagnetic.toString(),
        "imeiReadable" to raw.imeiReadable.toString(),
        "imeiValue" to (raw.imeiValue ?: "null"),
        "serialReadable" to raw.serialReadable.toString(),
        "serialValue" to (raw.serialValue ?: "null"),
        "cpuInfo" to (raw.cpuInfo ?: "null")
    )

    private class BatterySnapshot(
        val present: Boolean,
        val levelPercent: Int,
        val temperature: Int,
        val cycleCount: Int,
        val status: Int
    )

    private class SensorSnapshot(
        val count: Int,
        val accelerometer: Boolean,
        val gyroscope: Boolean,
        val light: Boolean,
        val proximity: Boolean,
        val magnetic: Boolean
    )

    private fun readBattery(context: Context?, cycleCountSupported: Boolean): BatterySnapshot? {
        if (context == null) return null
        return try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val intent = if (Build.VERSION.SDK_INT >= 34) {
                context.registerReceiver(null, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("DEPRECATION")
                context.registerReceiver(null, filter)
            } ?: return null
            val present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, true)
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percent = if (level >= 0 && scale > 0) level * 100 / scale else -1
            val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val cycleCount = if (cycleCountSupported) {
                intent.getIntExtra(EXTRA_CYCLE_COUNT_COMPAT, -1)
            } else {
                -1
            }
            BatterySnapshot(present, percent, temperature, cycleCount, status)
        } catch (t: Throwable) {
            null
        }
    }

    private const val EXTRA_CYCLE_COUNT_COMPAT = "android.os.extra.CYCLE_COUNT"

    private fun readSensors(context: Context?): SensorSnapshot? {
        if (context == null) return null
        return try {
            val manager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager ?: return null
            val sensors = manager.getSensorList(Sensor.TYPE_ALL)
            SensorSnapshot(
                count = sensors.size,
                accelerometer = sensors.any { it.type == Sensor.TYPE_ACCELEROMETER },
                gyroscope = sensors.any { it.type == Sensor.TYPE_GYROSCOPE },
                light = sensors.any { it.type == Sensor.TYPE_LIGHT },
                proximity = sensors.any { it.type == Sensor.TYPE_PROXIMITY },
                magnetic = sensors.any { it.type == Sensor.TYPE_MAGNETIC_FIELD }
            )
        } catch (t: Throwable) {
            null
        }
    }

    private fun readImei(context: Context?): Triple<Boolean, String?, String?> {
        if (context == null) return Triple(false, null, null)
        return try {
            if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                return Triple(false, null, "imei skipped: no FEATURE_TELEPHONY (tablet/tv context is not scored)")
            }
            val granted = context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) {
                return Triple(false, null, "imei skipped: requires READ_PHONE_STATE")
            }
            val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
                ?: return Triple(false, null, "imei skipped: TELEPHONY_SERVICE unavailable")
            @Suppress("DEPRECATION")
            val id = try {
                manager.imei ?: manager.deviceId
            } catch (t: Throwable) {
                null
            }
            if (id.isNullOrBlank() ||
                id.equals("unknown", ignoreCase = true) ||
                id.equals("null", ignoreCase = true) ||
                id.equals("none", ignoreCase = true)
            ) {
                Triple(false, null, "imei skipped: identifier unavailable or unknown")
            } else {
                Triple(true, id, null)
            }
        } catch (t: Throwable) {
            Triple(false, null, "imei skipped: read failed")
        }
    }

    private fun readSerial(): Triple<Boolean, String?, String?> {
        return try {
            val method = Build::class.java.getMethod("getSerial")
            val value = method.invoke(null) as? String
            val serial = value?.trim()
            if (serial.isNullOrEmpty() ||
                serial.equals("unknown", ignoreCase = true) ||
                serial.equals("null", ignoreCase = true) ||
                serial.equals("none", ignoreCase = true)
            ) {
                Triple(false, null, "serial skipped: requires READ_PRIVILEGED_PHONE_STATE (ordinary apps get unknown)")
            } else {
                Triple(true, serial, null)
            }
        } catch (t: Throwable) {
            val legacy = try {
                @Suppress("DEPRECATION")
                Build.SERIAL?.trim()
            } catch (t2: Throwable) {
                null
            }
            if (legacy.isNullOrEmpty() || legacy.equals("unknown", ignoreCase = true)) {
                Triple(false, null, "serial skipped: requires READ_PRIVILEGED_PHONE_STATE (ordinary apps get unknown)")
            } else {
                Triple(true, legacy, null)
            }
        }
    }

    private fun bootloaderState(): String? {
        val candidates = listOf(
            "ro.boot.verifiedbootstate",
            "ro.boot.flash.locked",
            "ro.boot.vbmeta.device_state",
            "ro.boot.veritymode"
        )
        for (key in candidates) {
            val value = getSystemProperty(key)?.trim()?.lowercase()
            if (!value.isNullOrEmpty()) return "$key=$value"
        }
        return null
    }

    private fun readCpuInfo(): String? {
        return try {
            val file = java.io.File("/proc/cpuinfo")
            if (!file.exists()) return null
            file.bufferedReader().use { reader ->
                val builder = StringBuilder(8192)
                val buffer = CharArray(8192)
                while (true) {
                    val read = reader.read(buffer)
                    if (read < 0) break
                    builder.append(buffer, 0, read)
                    if (builder.length >= 16384) break
                }
                builder.toString()
            }
        } catch (t: Throwable) {
            null
        }
    }

    private fun getSystemProperty(key: String): String? {
        return try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java)
            val result = get.invoke(null, key)
            result as? String
        } catch (t: Throwable) {
            null
        }
    }
}
