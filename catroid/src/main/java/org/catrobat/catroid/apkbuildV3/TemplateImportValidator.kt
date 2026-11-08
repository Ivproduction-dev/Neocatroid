package org.catrobat.catroid.apkbuildV3

import java.io.File
import java.util.Locale
import java.util.zip.ZipFile

enum class TemplateImportRejectReason {
    NOT_APK,
    MISSING_RUNTIME_MARKER,
    TOO_OLD,
    SIGNATURE_MISMATCH,
    HASH_FILE_INVALID,
    HASH_MISMATCH,
    NO_SPACE,
    IO_ERROR
}

data class TemplateImportRejection(
    val reason: TemplateImportRejectReason,
    val detail: String
)

class TemplateImportValidator(
    private val appSignatureHex: String? = null,
    private val signatureExtractor: ((File) -> String?)? = null,
    private val versionCodeReader: ((File) -> Int?)? = null,
    private val minVersionCode: Int = 1
) {

    fun hasRequiredEntries(file: File): Boolean {
        if (!file.exists() || file.length() < 4) return false
        return runCatching {
            java.util.zip.ZipFile(file).use { zip ->
                zip.getEntry(ENTRY_MANIFEST) != null &&
                    zip.getEntry(ENTRY_DEX) != null &&
                    zip.getEntry(ENTRY_RUNTIME_MARKER) != null
            }
        }.getOrDefault(false)
    }

    fun missingEntries(file: File): List<String> {
        if (!file.exists() || file.length() < 4) {
            return listOf(ENTRY_MANIFEST, ENTRY_DEX, ENTRY_RUNTIME_MARKER)
        }
        return runCatching {
            java.util.zip.ZipFile(file).use { zip ->
                listOf(ENTRY_MANIFEST, ENTRY_DEX, ENTRY_RUNTIME_MARKER).filter { zip.getEntry(it) == null }
            }
        }.getOrDefault(listOf(ENTRY_MANIFEST, ENTRY_DEX, ENTRY_RUNTIME_MARKER))
    }

    fun readVersionCode(file: File): Int? {
        return versionCodeReader?.invoke(file) ?: runCatching {
            com.reandroid.apk.ApkModule.loadApkFile(file).use { module ->
                module.setLoadDefaultFramework(false)
                module.androidManifest.versionCode
            }
        }.getOrNull()
    }

    fun extractSignature(file: File): String? {
        return signatureExtractor?.invoke(file)
    }

    fun validateTemplateFile(file: File, appSignatureHex: String?): TemplateImportRejection? {
        val effectiveAppSignature = appSignatureHex ?: this.appSignatureHex
        val missing = missingEntries(file)
        if (ENTRY_MANIFEST in missing) {
            return TemplateImportRejection(TemplateImportRejectReason.NOT_APK, "missing: $missing")
        }
        if (missing.isNotEmpty()) {
            return TemplateImportRejection(
                TemplateImportRejectReason.MISSING_RUNTIME_MARKER,
                "missing: $missing"
            )
        }

        val version = readVersionCode(file)
        if (version == null || version < minVersionCode) {
            return TemplateImportRejection(
                TemplateImportRejectReason.TOO_OLD,
                "versionCode=$version, required>=$minVersionCode"
            )
        }

        if (effectiveAppSignature != null) {
            val archiveSignature = extractSignature(file)
            if (!compareSignatures(effectiveAppSignature, archiveSignature)) {
                return TemplateImportRejection(
                    TemplateImportRejectReason.SIGNATURE_MISMATCH,
                    "archive signature does not match the app signature"
                )
            }
        }
        return null
    }

    companion object {

        const val ENTRY_MANIFEST = "AndroidManifest.xml"
        const val ENTRY_DEX = "classes.dex"
        const val ENTRY_RUNTIME_MARKER = "assets/apk_runtime_v4.marker"

        private val HASH_REGEX = Regex("^[0-9a-fA-F]{64}$")

        fun parseSha256FileContent(content: String?): String? {
            if (content.isNullOrBlank()) return null
            val normalized = content.replace("\uFEFF", "")
            val lines = normalized.lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            val candidates = mutableListOf<String>()
            for (line in lines) {
                val token = line
                    .removePrefix("SHA256:")
                    .removePrefix("sha256:")
                    .removePrefix("SHA-256:")
                    .removePrefix("sha-256:")
                    .trim()
                    .split(Regex("\\s+"))
                    .firstOrNull { it.isNotEmpty() }
                    ?: continue
                if (HASH_REGEX.matches(token)) {
                    candidates.add(token.lowercase(Locale.US))
                }
            }
            if (candidates.isEmpty()) return null
            val distinct = candidates.distinct()
            if (distinct.size != 1) return null
            return distinct.single()
        }

        fun hashMatches(expectedFromFile: String?, actualFileHash: String?): Boolean {
            val expected = expectedFromFile?.trim()?.lowercase(Locale.US)
            val actual = actualFileHash?.trim()?.lowercase(Locale.US)
            if (expected.isNullOrEmpty() || actual.isNullOrEmpty()) return false
            return expected == actual
        }

        fun compareSignatures(signatureA: String?, signatureB: String?): Boolean {
            if (signatureA.isNullOrBlank() || signatureB.isNullOrBlank()) return false
            val a = signatureA.split(',').map { it.trim().lowercase(Locale.US) }.filter { it.isNotEmpty() }.sorted()
            val b = signatureB.split(',').map { it.trim().lowercase(Locale.US) }.filter { it.isNotEmpty() }.sorted()
            if (a.isEmpty() || b.isEmpty()) return false
            return a == b
        }

        fun looksLikeSha256File(content: String?): Boolean = parseSha256FileContent(content) != null
    }
}
