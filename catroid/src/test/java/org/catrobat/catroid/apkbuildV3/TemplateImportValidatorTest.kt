package org.catrobat.catroid.apkbuildV3

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class TemplateImportValidatorTest {

    private val appSignature = "AA11BB22CC33DD44"
    private val templateSignature = appSignature
    private val foreignSignature = "DEADBEEF00000000"
    private val validHash = "a1b2c3d4e5f6a7b8a1b2c3d4e5f6a7b8a1b2c3d4e5f6a7b8a1b2c3d4e5f6a7b8"

    private fun validator(
        appSignature: String? = this.appSignature,
        versionCode: Int? = 100,
        signatureFor: (File) -> String? = { templateSignature }
    ): TemplateImportValidator = TemplateImportValidator(
        appSignatureHex = appSignature,
        signatureExtractor = signatureFor,
        versionCodeReader = { versionCode },
        minVersionCode = 1
    )

    private fun zipWith(
        dir: File,
        name: String,
        withManifest: Boolean = true,
        withDex: Boolean = true,
        withMarker: Boolean = true,
        extraEntries: List<String> = emptyList()
    ): File {
        val file = File(dir, name)
        ZipOutputStream(FileOutputStream(file)).use { out ->
            if (withManifest) putEntry(out, "AndroidManifest.xml", "binary-manifest-placeholder")
            if (withDex) putEntry(out, "classes.dex", "dex-bytes")
            if (withMarker) putEntry(out, "assets/apk_runtime_v4.marker", "marker")
            extraEntries.forEach { putEntry(out, it, "extra") }
        }
        return file
    }

    private fun putEntry(out: ZipOutputStream, name: String, content: String) {
        out.putNextEntry(ZipEntry(name))
        out.write(content.toByteArray())
        out.closeEntry()
    }

    private fun garbageFile(dir: File, name: String): File {
        val file = File(dir, name)
        FileOutputStream(file).use { it.write(ByteArray(128) { (it * 31).toByte() }) }
        return file
    }

    // ------------------------------------------------------------------
    // parseSha256FileContent
    // ------------------------------------------------------------------

    @Test
    fun parse_bareLowercaseHex() {
        assertEquals(validHash, TemplateImportValidator.parseSha256FileContent(validHash))
    }

    @Test
    fun parse_bareUppercaseHex_normalizedToLowercase() {
        assertEquals(
            validHash,
            TemplateImportValidator.parseSha256FileContent(validHash.uppercase(Locale.US))
        )
    }

    @Test
    fun parse_sha256sumFormat_withDoubleSpaceAndFilename() {
        assertEquals(
            validHash,
            TemplateImportValidator.parseSha256FileContent("$validHash  template_runtime.apk\n")
        )
    }

    @Test
    fun parse_sha256sumFormat_singleSpace() {
        assertEquals(
            validHash,
            TemplateImportValidator.parseSha256FileContent("$validHash template_runtime.apk")
        )
    }

    @Test
    fun parse_sha256Prefix() {
        assertEquals(validHash, TemplateImportValidator.parseSha256FileContent("SHA256: $validHash"))
    }

    @Test
    fun parse_shaDash256Prefix_lowercase() {
        assertEquals(validHash, TemplateImportValidator.parseSha256FileContent("sha-256: $validHash"))
    }

    @Test
    fun parse_surroundingWhitespaceAndNewlines() {
        assertEquals(
            validHash,
            TemplateImportValidator.parseSha256FileContent("\n\n  $validHash  \r\n\r\n")
        )
    }

    @Test
    fun parse_utf8Bom() {
        assertEquals(
            validHash,
            TemplateImportValidator.parseSha256FileContent("\uFEFF$validHash")
        )
    }

    @Test
    fun parse_hashInsideJunkText() {
        assertEquals(
            validHash,
            TemplateImportValidator.parseSha256FileContent("Файл скачан успешно\n$validHash\nСпасибо!")
        )
    }

    @Test
    fun parse_emptyContent_returnsNull() {
        assertNull(TemplateImportValidator.parseSha256FileContent(""))
        assertNull(TemplateImportValidator.parseSha256FileContent("   \n  \n"))
        assertNull(TemplateImportValidator.parseSha256FileContent(null))
    }

    @Test
    fun parse_tooShortHash_returnsNull() {
        assertNull(TemplateImportValidator.parseSha256FileContent(validHash.substring(0, 63)))
    }

    @Test
    fun parse_tooLongHash_returnsNull() {
        assertNull(TemplateImportValidator.parseSha256FileContent(validHash + "ab"))
    }

    @Test
    fun parse_nonHexHash_returnsNull() {
        assertNull(
            TemplateImportValidator.parseSha256FileContent(
                "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz"
            )
        )
    }

    @Test
    fun parse_twoDifferentHashes_returnsNull() {
        val other = validHash.replaceFirst("a", "b")
        assertNull(
            TemplateImportValidator.parseSha256FileContent("$validHash\n$other")
        )
    }

    @Test
    fun parse_twoIdenticalHashes_returnsOne() {
        assertEquals(
            validHash,
            TemplateImportValidator.parseSha256FileContent("$validHash\n$validHash")
        )
    }

    @Test
    fun parse_windowsLineEndings() {
        assertEquals(
            validHash,
            TemplateImportValidator.parseSha256FileContent("SHA256: $validHash\r\n")
        )
    }

    // ------------------------------------------------------------------
    // hashMatches
    // ------------------------------------------------------------------

    @Test
    fun hashMatches_identical() {
        assertTrue(TemplateImportValidator.hashMatches(validHash, validHash))
    }

    @Test
    fun hashMatches_caseInsensitive() {
        assertTrue(
            TemplateImportValidator.hashMatches(
                validHash.uppercase(Locale.US),
                validHash.lowercase(Locale.US)
            )
        )
    }

    @Test
    fun hashMatches_surroundingWhitespace() {
        assertTrue(TemplateImportValidator.hashMatches("  $validHash\n", " $validHash "))
    }

    @Test
    fun hashMatches_differentHashes() {
        val other = validHash.replaceFirst("a", "b")
        assertFalse(TemplateImportValidator.hashMatches(validHash, other))
    }

    @Test
    fun hashMatches_nullOrEmpty() {
        assertFalse(TemplateImportValidator.hashMatches(null, validHash))
        assertFalse(TemplateImportValidator.hashMatches(validHash, null))
        assertFalse(TemplateImportValidator.hashMatches("", ""))
    }

    // ------------------------------------------------------------------
    // looksLikeSha256File
    // ------------------------------------------------------------------

    @Test
    fun looksLike_validContent() {
        assertTrue(TemplateImportValidator.looksLikeSha256File("SHA256: $validHash"))
    }

    @Test
    fun looksLike_invalidContent() {
        assertFalse(TemplateImportValidator.looksLikeSha256File("привет"))
        assertFalse(TemplateImportValidator.looksLikeSha256File(null))
    }

    // ------------------------------------------------------------------
    // compareSignatures
    // ------------------------------------------------------------------

    @Test
    fun compareSignatures_equal() {
        assertTrue(TemplateImportValidator.compareSignatures(appSignature, appSignature))
    }

    @Test
    fun compareSignatures_caseInsensitive() {
        assertTrue(
            TemplateImportValidator.compareSignatures(
                appSignature.uppercase(Locale.US),
                appSignature.lowercase(Locale.US)
            )
        )
    }

    @Test
    fun compareSignatures_multipleCerts_anyOrder() {
        val ours = "$appSignature,$foreignSignature"
        assertTrue(TemplateImportValidator.compareSignatures(ours, "$foreignSignature,$appSignature"))
    }

    @Test
    fun compareSignatures_different() {
        assertFalse(TemplateImportValidator.compareSignatures(appSignature, foreignSignature))
    }

    @Test
    fun compareSignatures_nullOrBlank() {
        assertFalse(TemplateImportValidator.compareSignatures(null, appSignature))
        assertFalse(TemplateImportValidator.compareSignatures(appSignature, null))
        assertFalse(TemplateImportValidator.compareSignatures("", " "))
    }

    @Test
    fun compareSignatures_extraWhitespace() {
        assertTrue(TemplateImportValidator.compareSignatures(" $appSignature ", "$appSignature,"))
    }

    // ------------------------------------------------------------------
    // validateTemplateFile — structural checks on real zip files
    // ------------------------------------------------------------------

    @Test
    fun validate_completeTemplate_passes() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        assertNull(validator().validateTemplateFile(file, appSignature))
        dir.deleteRecursively()
    }

    @Test
    fun validate_missingMarker_rejectedWithMarkerReason() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk", withMarker = false)
        val rejection = validator().validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.MISSING_RUNTIME_MARKER, rejection!!.reason)
        dir.deleteRecursively()
    }

    @Test
    fun validate_missingDex_rejectedWithMarkerReason() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk", withDex = false)
        val rejection = validator().validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.MISSING_RUNTIME_MARKER, rejection!!.reason)
        dir.deleteRecursively()
    }

    @Test
    fun validate_missingManifest_rejectedAsNotApk() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk", withManifest = false)
        val rejection = validator().validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.NOT_APK, rejection!!.reason)
        dir.deleteRecursively()
    }

    @Test
    fun validate_garbageFile_rejectedAsNotApk() {
        val dir = createTempDir()
        val file = garbageFile(dir, "template.apk")
        val rejection = validator().validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.NOT_APK, rejection!!.reason)
        dir.deleteRecursively()
    }

    @Test
    fun validate_extraEntries_doNotBreak() {
        val dir = createTempDir()
        val file = zipWith(
            dir, "template.apk",
            extraEntries = listOf("res/values.xml", "META-INF/MANIFEST.MF", "lib/arm64-v8a/libapk_crypto.so")
        )
        assertNull(validator().validateTemplateFile(file, appSignature))
        dir.deleteRecursively()
    }

    // ------------------------------------------------------------------
    // validateTemplateFile — version checks
    // ------------------------------------------------------------------

    @Test
    fun validate_versionZero_rejectedAsTooOld() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        val rejection = validator(versionCode = 0).validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.TOO_OLD, rejection!!.reason)
        dir.deleteRecursively()
    }

    @Test
    fun validate_versionUnreadable_rejectedAsTooOld() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        val rejection = validator(versionCode = null).validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.TOO_OLD, rejection!!.reason)
        dir.deleteRecursively()
    }

    @Test
    fun validate_versionExactlyMin_passes() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        assertNull(validator(versionCode = 1).validateTemplateFile(file, appSignature))
        dir.deleteRecursively()
    }

    @Test
    fun validate_versionBelowMin_rejected() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        val rejection = TemplateImportValidator(
            appSignatureHex = appSignature,
            signatureExtractor = { templateSignature },
            versionCodeReader = { 0 },
            minVersionCode = 50
        ).validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.TOO_OLD, rejection!!.reason)
        dir.deleteRecursively()
    }

    // ------------------------------------------------------------------
    // validateTemplateFile — signature checks
    // ------------------------------------------------------------------

    @Test
    fun validate_matchingSignature_passes() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        assertNull(validator().validateTemplateFile(file, appSignature))
        dir.deleteRecursively()
    }

    @Test
    fun validate_foreignSignature_rejected() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        val rejection = validator(signatureFor = { foreignSignature }).validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.SIGNATURE_MISMATCH, rejection!!.reason)
        dir.deleteRecursively()
    }

    @Test
    fun validate_unreadableSignature_rejected() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        val rejection = validator(signatureFor = { null }).validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.SIGNATURE_MISMATCH, rejection!!.reason)
        dir.deleteRecursively()
    }

    @Test
    fun validate_nullAppSignature_skipsSignatureCheck() {
        val dir = createTempDir()
        val file = zipWith(dir, "template.apk")
        assertNull(validator(appSignature = null, signatureFor = { foreignSignature }).validateTemplateFile(file, null))
        dir.deleteRecursively()
    }

    // ------------------------------------------------------------------
    // validateTemplateFile — file presence
    // ------------------------------------------------------------------

    @Test
    fun validate_missingFile_rejectedAsNotApk() {
        val dir = createTempDir()
        val file = File(dir, "does_not_exist.apk")
        val rejection = validator().validateTemplateFile(file, appSignature)
        assertNotNull(rejection)
        assertEquals(TemplateImportRejectReason.NOT_APK, rejection!!.reason)
        dir.deleteRecursively()
    }

    // ------------------------------------------------------------------
    // Hash-file sniffing used by the import flow to tell files apart
    // ------------------------------------------------------------------

    @Test
    fun sniff_apkFileIsNotHashFile() {
        val dir = createTempDir()
        val apk = zipWith(dir, "template.apk")
        val content = apk.readText()
        assertFalse(TemplateImportValidator.looksLikeSha256File(content))
        dir.deleteRecursively()
    }

    @Test
    fun sniff_hashFileIsRecognized() {
        val dir = createTempDir()
        val hashFile = File(dir, "template_runtime.apk.sha256")
        hashFile.writeText(validHash)
        assertTrue(TemplateImportValidator.looksLikeSha256File(hashFile.readText()))
        dir.deleteRecursively()
    }
}
