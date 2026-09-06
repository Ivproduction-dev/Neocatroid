package org.catrobat.catroid.apkbuildV3

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.security.SecureRandom
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class V3NormalizeRoundTripTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun storedEntry(zos: ZipOutputStream, name: String, data: ByteArray) {
        val e = ZipEntry(name)
        e.method = ZipEntry.STORED
        e.size = data.size.toLong()
        val crc = CRC32()
        crc.update(data)
        e.crc = crc.value
        zos.putNextEntry(e)
        zos.write(data)
        zos.closeEntry()
    }

    private fun deflatedEntry(zos: ZipOutputStream, name: String, data: ByteArray) {
        zos.putNextEntry(ZipEntry(name))
        zos.write(data)
        zos.closeEntry()
    }

    @Test
    fun normalize_preservesAllContentByteForByte() {
        val random = SecureRandom()
        val manifest = "<manifest package=\"org.test\"/>".toByteArray()
        val dex = ByteArray(300_000) { it.toByte() }
        val payload = ByteArray(300_000).also { random.nextBytes(it) }
        val key = "QUJDREVGR0g=".toByteArray()
        val icon = ByteArray(50_000) { (it * 31).toByte() }

        val input = File(tmp.root, "in.apk")
        ZipOutputStream(input.outputStream()).use { zos ->
            storedEntry(zos, "AndroidManifest.xml", manifest)
            deflatedEntry(zos, "classes.dex", dex)
            storedEntry(zos, "assets/project.ncv3", payload)
            deflatedEntry(zos, "assets/nk_0_abc123.nk", key)
            storedEntry(zos, "res/drawable/icon.png", icon)
            deflatedEntry(zos, "META-INF/MANIFEST.MF", "Manifest-Version: 1.0\n".toByteArray())
        }

        val output = File(tmp.root, "out.apk")
        val normTmp = File(tmp.root, "norm_tmp")
        V3ApkAssembler.normalizeApkForInstall(input, output, normTmp)
        V3ApkAssembler.assertNoDataDescriptors(output)

        ZipFile(output).use { zf ->
            assertNull("META-INF must be stripped", zf.getEntry("META-INF/MANIFEST.MF"))
            assertEquals(manifest.toList(), zf.getInputStream(zf.getEntry("AndroidManifest.xml")).readBytes().toList())
            assertEquals(dex.toList(), zf.getInputStream(zf.getEntry("classes.dex")).readBytes().toList())
            assertEquals(payload.toList(), zf.getInputStream(zf.getEntry("assets/project.ncv3")).readBytes().toList())
            assertEquals(key.toList(), zf.getInputStream(zf.getEntry("assets/nk_0_abc123.nk")).readBytes().toList())
            assertEquals(icon.toList(), zf.getInputStream(zf.getEntry("res/drawable/icon.png")).readBytes().toList())
        }
    }

    @Test
    fun verifyInstallable_rejectsApkWithoutPayloadOrKeys() {
        val input = File(tmp.root, "bare.apk")
        ZipOutputStream(input.outputStream()).use { zos ->
            storedEntry(zos, "AndroidManifest.xml", "<manifest/>".toByteArray())
            deflatedEntry(zos, "classes.dex", ByteArray(100))
        }
        val aligned = File(tmp.root, "bare_aligned.apk")
        V3ApkAssembler.normalizeApkForInstall(input, aligned, File(tmp.root, "t1"))

        var failed = 0
        try {
            V3ApkAssembler.verifyInstallable(aligned)
        } catch (e: IllegalArgumentException) {
            failed++
        }
        assertEquals("APK without payload/keys must be rejected", 1, failed)

        val withPayload = File(tmp.root, "full.apk")
        ZipOutputStream(withPayload.outputStream()).use { zos ->
            storedEntry(zos, "AndroidManifest.xml", "<manifest/>".toByteArray())
            deflatedEntry(zos, "classes.dex", ByteArray(100))
            deflatedEntry(zos, "assets/project.ncv3", ByteArray(1024) { 7 })
            deflatedEntry(zos, "assets/nk_1_deadbeef.nk", "a2V5".toByteArray())
        }
        val withPayloadAligned = File(tmp.root, "full_aligned.apk")
        V3ApkAssembler.normalizeApkForInstall(withPayload, withPayloadAligned, File(tmp.root, "t2"))
        try {
            V3ApkAssembler.verifyInstallable(withPayloadAligned)
        } catch (e: IllegalArgumentException) {
            throw AssertionError("structural asserts must pass, got: ${e.message}")
        } catch (e: Exception) {
            assertTrue(
                "only apksig parsing of the fake manifest may fail, got: ${e.javaClass.simpleName}",
                e.javaClass.name.startsWith("com.android.apksig.")
            )
        }
    }
}
