package org.catrobat.catroid.apkbuildV3

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class NativePepperSlotTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun pepper(): ByteArray = ByteArray(NativeKeyVault.PEPPER_SIZE) { (it * 11 + 7).toByte() }

    @Test
    fun patchCount_detectsSentinelOccurrences() {
        val lib = ByteArray(300) { 0x11 } + NativePepperSlot.SENTINEL + ByteArray(200) { 0x22 }
        assertEquals(1, NativePepperSlot.patchCount(lib))
        assertEquals(0, NativePepperSlot.patchCount(ByteArray(300) { 0x33 }))

        val two = ByteArray(50) + NativePepperSlot.SENTINEL + ByteArray(20) + NativePepperSlot.SENTINEL
        assertEquals(2, NativePepperSlot.patchCount(two))
    }

    @Test
    fun patch_replacesSentinelWithPepper() {
        val head = ByteArray(300) { 0x11 }
        val tail = ByteArray(100) { 0x22 }
        val lib = head + NativePepperSlot.SENTINEL + tail
        val patched = NativePepperSlot.patch(lib, pepper())
        assertArrayEquals(head, patched.copyOfRange(0, head.size))
        assertArrayEquals(tail, patched.copyOfRange(head.size + NativePepperSlot.SENTINEL.size, patched.size))
        assertArrayEquals(pepper(), patched.copyOfRange(head.size, head.size + NativeKeyVault.PEPPER_SIZE))
        assertEquals(0, NativePepperSlot.patchCount(patched))
        assertEquals(lib.size, patched.size)
    }

    @Test
    fun patch_rejectsMissingOrDuplicatedSlot() {
        val zero = ByteArray(512) { 0x44 }
        try {
            NativePepperSlot.patch(zero, pepper())
            throw AssertionError("expected exception for missing slot")
        } catch (_: IllegalArgumentException) {
        }
        val two = NativePepperSlot.SENTINEL + ByteArray(8) + NativePepperSlot.SENTINEL
        try {
            NativePepperSlot.patch(two, pepper())
            throw AssertionError("expected exception for duplicated slot")
        } catch (_: IllegalArgumentException) {
        }
    }

    @Test
    fun patch_rejectsWrongPepperSize() {
        val lib = ByteArray(10) + NativePepperSlot.SENTINEL
        try {
            NativePepperSlot.patch(lib, ByteArray(16))
            throw AssertionError("expected exception for short pepper")
        } catch (_: IllegalArgumentException) {
        }
    }

    @Test
    fun pepperLibRegex_matchesExpectedEntries() {
        assertTrue(NativePepperSlot.PEPPER_LIB_REGEX.matches("lib/arm64-v8a/libapk_crypto.so"))
        assertTrue(NativePepperSlot.PEPPER_LIB_REGEX.matches("lib/armeabi-v7a/libapk_crypto.so"))
        assertTrue(NativePepperSlot.PEPPER_LIB_REGEX.matches("lib/x86_64/libapk_crypto.so"))
        assertFalse(NativePepperSlot.PEPPER_LIB_REGEX.matches("libapk_crypto.so"))
        assertFalse(NativePepperSlot.PEPPER_LIB_REGEX.matches("lib/arm64-v8a/libother.so"))
        assertFalse(NativePepperSlot.PEPPER_LIB_REGEX.matches("assets/libapk_crypto.so"))
    }

    private fun writeStoredZip(target: File, entries: Map<String, ByteArray>) {
        ZipOutputStream(target.outputStream()).use { zos ->
            for ((name, bytes) in entries) {
                val crc32 = java.util.zip.CRC32().apply { update(bytes) }
                val entry = ZipEntry(name).apply {
                    method = ZipEntry.STORED
                    size = bytes.size.toLong()
                    compressedSize = bytes.size.toLong()
                    crc = crc32.value
                }
                zos.putNextEntry(entry)
                zos.write(bytes)
                zos.closeEntry()
            }
        }
    }

    private fun readZipEntry(apk: File, name: String): ByteArray {
        ZipFile(apk).use { zf ->
            val entry = zf.getEntry(name) ?: throw AssertionError("missing $name")
            return zf.getInputStream(entry).readBytes()
        }
    }

    private fun hasZipEntry(apk: File, name: String): Boolean {
        ZipFile(apk).use { zf -> return zf.getEntry(name) != null }
    }

    @Test
    fun normalize_patchesNativePepperSlot() {
        val work = File(tmp.root, "work").apply { mkdirs() }
        val pepper = pepper()
        val lib = ByteArray(400) { 0x51 } + NativePepperSlot.SENTINEL + ByteArray(64) { 0x62 }
        val input = File(work, "in.apk")
        writeStoredZip(
            input,
            linkedMapOf(
                "AndroidManifest.xml" to "manifest".toByteArray(),
                "classes.dex" to "dex".toByteArray(),
                "lib/arm64-v8a/libapk_crypto.so" to lib
            )
        )

        assertTrue(V3ApkAssembler.nativePepperAvailable(input))

        val output = File(work, "out.apk")
        V3ApkAssembler.normalizeApkForInstall(input, output, File(work, "tmp"), nativePepper = pepper)
        val patchedLib = readZipEntry(output, "lib/arm64-v8a/libapk_crypto.so")

        assertEquals(lib.size, patchedLib.size)
        assertArrayEquals(
            pepper,
            patchedLib.copyOfRange(400, 400 + NativeKeyVault.PEPPER_SIZE)
        )
        assertEquals(0, NativePepperSlot.patchCount(patchedLib))
        assertTrue(hasZipEntry(output, "AndroidManifest.xml"))
        assertTrue(hasZipEntry(output, "classes.dex"))
    }

    @Test
    fun normalize_withoutPepperLeavesNativeLibUntouched() {
        val work = File(tmp.root, "work2").apply { mkdirs() }
        val lib = ByteArray(400) { 0x51 } + NativePepperSlot.SENTINEL + ByteArray(64) { 0x62 }
        val input = File(work, "in.apk")
        writeStoredZip(
            input,
            linkedMapOf(
                "AndroidManifest.xml" to "manifest".toByteArray(),
                "classes.dex" to "dex".toByteArray(),
                "lib/arm64-v8a/libapk_crypto.so" to lib
            )
        )
        val output = File(work, "out.apk")
        V3ApkAssembler.normalizeApkForInstall(input, output, File(work, "tmp"))
        assertArrayEquals(lib, readZipEntry(output, "lib/arm64-v8a/libapk_crypto.so"))
    }
}
