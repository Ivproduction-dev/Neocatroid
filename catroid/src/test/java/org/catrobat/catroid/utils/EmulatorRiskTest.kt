package org.catrobat.catroid.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmulatorRiskTest {

    private fun stockPhone() = EmulatorRisk.RawSignals(
        board = "dew",
        device = "dew",
        product = "dew",
        manufacturer = "realme",
        model = "RMX3785",
        fingerprint = "realme/dew/dew:13/TP1A.220905.001/6517049_00001:release-keys",
        hardware = "mt6769",
        bootloader = "unlocked",
        bootloaderStatus = "ro.boot.vbmeta.device_state=unlocked",
        batteryPresent = true,
        batteryLevelPercent = 65,
        batteryTemperatureDeciC = 245,
        batteryCycleCount = 120,
        batteryStatus = 3,
        sensorCount = 18,
        hasAccelerometer = true,
        hasGyroscope = true,
        hasLight = true,
        hasProximity = true,
        hasMagnetic = true,
        imeiReadable = true,
        imeiValue = "867123456789012",
        serialReadable = true,
        serialValue = "A1B2C3D4E5F6",
        cpuInfo = "Processor\t: MediaTek MT6769V/CU\nmodel name\t: ARMv8 Processor rev 4 (v8l)",
        apiLevel = 34,
        cycleCountSupported = true
    )

    private fun aospGsi() = EmulatorRisk.RawSignals(
        board = "generic_arm64",
        device = "generic_arm64",
        product = "gsi_arm64_x86_arm",
        manufacturer = "Android",
        model = "GSI arm64",
        fingerprint = "Android/gsi_arm64_x86_arm/generic_arm64:16/BP2A.250605.031/userdebug/test-keys",
        hardware = "mt6769",
        bootloader = "unknown",
        bootloaderStatus = "ro.boot.verifiedbootstate=orange",
        batteryPresent = true,
        batteryLevelPercent = 80,
        batteryTemperatureDeciC = 260,
        batteryCycleCount = -1,
        batteryStatus = 3,
        sensorCount = 18,
        hasAccelerometer = true,
        hasGyroscope = true,
        hasLight = true,
        hasProximity = true,
        hasMagnetic = true,
        imeiReadable = false,
        imeiValue = null,
        serialReadable = false,
        serialValue = null,
        cpuInfo = "Processor\t: MediaTek MT6769V/CU",
        apiLevel = 34,
        cycleCountSupported = true,
        skippedSignals = listOf("imei skipped: requires READ_PHONE_STATE")
    )

    private fun unlockedRootedPhone() = stockPhone().copy(
        bootloaderStatus = "ro.boot.flash.locked=0",
        fingerprint = "realme/dew/dew:13/TP1A.220905.001/6517049_00001:userdebug/test-keys"
    )

    private fun emulatorNoBatteryNoSensors() = EmulatorRisk.RawSignals(
        board = "goldfish",
        device = "generic_x86_64",
        product = "sdk_gphone64_x86_64",
        manufacturer = "Google",
        model = "sdk_gphone64_x86_64",
        fingerprint = "google/sdk_gphone64_x86_64/generic_x86_64:15/AP1A.240505.004/eng.root.20240601.161530:userdebug/test-keys",
        hardware = "ranchu",
        bootloader = "unknown",
        bootloaderStatus = null,
        batteryPresent = true,
        batteryLevelPercent = 100,
        batteryTemperatureDeciC = 0,
        batteryCycleCount = -1,
        batteryStatus = 2,
        sensorCount = 0,
        hasAccelerometer = false,
        hasGyroscope = false,
        hasLight = false,
        hasProximity = false,
        hasMagnetic = false,
        imeiReadable = false,
        imeiValue = null,
        serialReadable = false,
        serialValue = null,
        cpuInfo = "model name\t: QEMU Virtual CPU version 2.5+",
        apiLevel = 34,
        cycleCountSupported = true,
        skippedSignals = listOf(
            "bootloader skipped: ro.boot.* unavailable via SystemProperties",
            "sensors skipped: 0 reported; TV/tablet context is not scored"
        )
    )

    private fun googleReleaseKeysGsi() = EmulatorRisk.RawSignals(
        board = "generic_arm64",
        device = "generic_arm64",
        product = "gsi_arm64_arm64",
        manufacturer = "Android",
        model = "generic_arm64",
        fingerprint = "Android/gsi_arm64_arm64/generic_arm64:16/BP2A.250605.031/repo_release:user/release-keys",
        hardware = "mt6769",
        bootloader = "unknown",
        bootloaderStatus = "ro.boot.verifiedbootstate=orange",
        batteryPresent = true,
        batteryLevelPercent = 77,
        batteryTemperatureDeciC = 280,
        batteryCycleCount = 95,
        batteryStatus = 3,
        sensorCount = 18,
        hasAccelerometer = true,
        hasGyroscope = true,
        hasLight = true,
        hasProximity = true,
        hasMagnetic = true,
        imeiReadable = false,
        imeiValue = null,
        serialReadable = false,
        serialValue = null,
        cpuInfo = "Processor\t: MediaTek MT6769V/CU",
        apiLevel = 35,
        cycleCountSupported = true,
        skippedSignals = listOf("imei skipped: requires READ_PHONE_STATE")
    )

    private fun tabletNoImei() = EmulatorRisk.RawSignals(
        board = "ginkgo",
        device = "ginkgo",
        product = "ginkgo",
        manufacturer = "Xiaomi",
        model = "Redmi Note 8",
        fingerprint = "Xiaomi/ginkgo/ginkgo:10/QKQ1.190825.002/V11.0.10.0.QCOMIXM:release-keys",
        hardware = "qcom",
        bootloader = "unknown",
        bootloaderStatus = "ro.boot.verifiedbootstate=green",
        batteryPresent = true,
        batteryLevelPercent = 92,
        batteryTemperatureDeciC = 300,
        batteryCycleCount = -1,
        batteryStatus = 2,
        sensorCount = 14,
        hasAccelerometer = true,
        hasGyroscope = true,
        hasLight = true,
        hasProximity = true,
        hasMagnetic = true,
        imeiReadable = false,
        imeiValue = null,
        serialReadable = true,
        serialValue = "9b8f3a2c1d7e",
        cpuInfo = "Processor\t: Qualcomm Technologies QSDM636 Snapdragon 636",
        apiLevel = 31,
        cycleCountSupported = false,
        skippedSignals = listOf(
            "cycleCount skipped: requires API 34, device API 31",
            "imei skipped: no FEATURE_TELEPHONY (tablet/tv context is not scored)"
        )
    )

    @Test
    fun stockPhoneYieldsZeroRisk() {
        val result = EmulatorRisk.assess(stockPhone())
        println(result)
        assertEquals(0, result.risk)
        assertTrue(result.suspicious.isEmpty())
    }

    @Test
    fun aospGsiDoesNotExceedRisk1() {
        val result = EmulatorRisk.assess(aospGsi())
        println(result)
        assertTrue("AOSP GSI with live battery/sensors must be <= 1, got ${result.risk}", result.risk <= 1)
    }

    @Test
    fun unlockedRootedPhoneDoesNotExceedRisk1() {
        val result = EmulatorRisk.assess(unlockedRootedPhone())
        println(result)
        assertTrue("unlocked rooted phone must be <= 1, got ${result.risk}", result.risk <= 1)
    }

    @Test
    fun emulatorIsHighRisk() {
        val result = EmulatorRisk.assess(emulatorNoBatteryNoSensors())
        println(result)
        assertEquals(3, result.risk)
    }

    @Test
    fun tabletWithoutImeiHasZeroRisk() {
        val result = EmulatorRisk.assess(tabletNoImei())
        println(result)
        assertEquals(0, result.risk)
    }

    @Test
    fun alphanumericSerialIsPositiveNotSuspicious() {
        val result = EmulatorRisk.assess(stockPhone().copy(serialValue = "A1B2C3D4E5F6", serialReadable = true))
        println(result)
        assertEquals(0, result.risk)
        assertTrue(result.positive.any { it.weight == 1 && it.reason.startsWith("serial") })
        assertTrue(result.suspicious.none { it.reason.startsWith("serial") })
    }

    @Test
    fun unknownSerialWithoutPermissionIsNeutral() {
        val result = EmulatorRisk.assess(stockPhone().copy(serialReadable = false, serialValue = null))
        println(result)
        assertEquals(0, result.risk)
        assertTrue(result.suspicious.none { it.reason.startsWith("serial") })
        assertTrue(result.positive.none { it.reason.startsWith("serial") })
    }

    @Test
    fun engBuildTypeIsNotSuspicious() {
        val result = EmulatorRisk.assess(
            stockPhone().copy(fingerprint = "realme/dew/dew:13/TP1A.220905.001/eng.user:release-keys")
        )
        println(result)
        assertEquals(0, result.risk)
        assertTrue(result.suspicious.none { it.reason.contains("eng") })
    }

    @Test
    fun api31WithoutCycleCountHasNoCycleSuspicion() {
        val result = EmulatorRisk.assess(
            stockPhone().copy(
                apiLevel = 31,
                cycleCountSupported = false,
                batteryCycleCount = -1,
                batteryLevelPercent = 100,
                batteryTemperatureDeciC = 0,
                skippedSignals = listOf("cycleCount skipped: requires API 34, device API 31")
            )
        )
        println(result)
        assertEquals(0, result.risk)
        assertEquals(31, result.apiLevel)
        assertTrue(result.skippedSignals.contains("cycleCount skipped: requires API 34, device API 31"))
        assertTrue(result.suspicious.none { it.reason.contains("cycle") })
    }

    @Test
    fun api34WithCycleCountIsScored() {
        val withCycles = EmulatorRisk.assess(
            stockPhone().copy(
                apiLevel = 34,
                cycleCountSupported = true,
                batteryCycleCount = 120,
                batteryLevelPercent = 100,
                batteryTemperatureDeciC = 0
            )
        )
        println(withCycles)
        assertEquals(0, withCycles.risk)

        val withoutCycles = EmulatorRisk.assess(
            stockPhone().copy(
                apiLevel = 34,
                cycleCountSupported = true,
                batteryCycleCount = -1,
                batteryLevelPercent = 100,
                batteryTemperatureDeciC = 0
            )
        )
        println(withoutCycles)
        assertTrue(withoutCycles.suspicious.any { it.reason.contains("pinned at 100%") })
        assertTrue(withoutCycles.risk <= 1)
    }
}
