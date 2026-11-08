package org.catrobat.catroid.apkbuildV3

import android.content.Context
import android.util.Log
import com.android.apksig.ApkVerifier
import com.reandroid.apk.ApkModule
import okhttp3.OkHttpClient
import okhttp3.Request
import org.catrobat.catroid.BuildConfig
import java.io.File
import java.util.concurrent.TimeUnit

object TemplateManagerV3 {
    private const val TAG = "TemplateManagerV3"
    private const val TEMPLATE_RUNTIME_URL =
        "https://raw.githubusercontent.com/Ivproduction-dev/Neocatroid-Template/main/template_runtime.apk"
    private const val TEMPLATE_RUNTIME_MEDIA_URL =
        "https://media.githubusercontent.com/media/Ivproduction-dev/Neocatroid-Template/main/template_runtime.apk"
    private const val LFS_POINTER_PREFIX = "version https://git-lfs.github.com/spec/v1"
    private const val CACHE_DIR_NAME = "v3_template"
    private const val CACHE_FILE_NAME = "template_runtime_v3.apk"
    private const val ETAG_FILE_NAME = "template_runtime_v3.apk.etag"
    private const val DOWNLOAD_BUFFER_SIZE = 64 * 1024
    private const val PIN_PREFS_NAME = "v3_template_pins"
    private const val PIN_KEY = "template_sha256"
    private const val PIN_SIZE_KEY = "template_size"
    private const val TEMPLATE_MARKER = "apk_runtime_v4.marker"
    private const val LOCAL_TEMPLATE_NAME = "template_runtime.apk"
    private const val MIN_TEMPLATE_VERSION_CODE = 1

    data class TemplateCacheStatus(val cached: Boolean, val sizeBytes: Long)

    sealed interface TemplateImportResult {
        data class Success(val file: File, val repinned: Boolean) : TemplateImportResult
        data class Rejected(val reason: TemplateImportRejectReason, val detail: String) : TemplateImportResult
    }

    enum class TemplateFailure { NO_SPACE, NETWORK, BAD_FILE }

    sealed interface TemplateOutcome {
        data class Ready(val file: File, val updated: Boolean) : TemplateOutcome
        data class Failed(val failure: TemplateFailure, val detail: String, val cachedFile: File?) : TemplateOutcome
    }

    private data class FetchResult(val success: Boolean, val etag: String, val detail: String)

    fun getCacheStatus(context: Context): TemplateCacheStatus {
        localTemplateOverride(context)?.let { return TemplateCacheStatus(true, it.length()) }
        val cached = getCachedTemplate(context)
        return if (cached != null && isValidTemplateApk(cached) && matchesPinnedSize(context, cached)) {
            TemplateCacheStatus(true, cached.length())
        } else {
            TemplateCacheStatus(false, 0L)
        }
    }

    fun ensureCachedTemplate(
        context: Context,
        force: Boolean = false,
        onProgress: ((Float, String) -> Unit)? = null
    ): TemplateOutcome {
        localTemplateOverride(context)?.let { return TemplateOutcome.Ready(it, false) }
        val cached = getCachedTemplate(context)
        val validCache = if (cached != null && isValidTemplateApk(cached) && matchesPinnedSize(context, cached)) cached else null
        if (!force && validCache != null) return TemplateOutcome.Ready(validCache, false)
        return downloadTemplate(context, validCache, force || validCache == null, onProgress)
    }

    fun beginTemplateImport(context: Context, templateUri: android.net.Uri): TemplateImportResult {
        val cacheDir = File(context.filesDir, CACHE_DIR_NAME)
        cacheDir.mkdirs()
        val tmpTemplate = File(cacheDir, "$CACHE_FILE_NAME.import")

        try {
            val input = context.contentResolver.openInputStream(templateUri)
            if (input == null) {
                tmpTemplate.delete()
                return TemplateImportResult.Rejected(TemplateImportRejectReason.IO_ERROR, "template stream is not readable")
            }
            input.use { stream ->
                tmpTemplate.outputStream().use { output ->
                    stream.copyTo(output, DOWNLOAD_BUFFER_SIZE)
                }
            }
        } catch (e: Exception) {
            tmpTemplate.delete()
            return TemplateImportResult.Rejected(TemplateImportRejectReason.IO_ERROR, e.message ?: "copy failed")
        }

        if (!hasEnoughSpace(cacheDir, tmpTemplate.length())) {
            tmpTemplate.delete()
            return TemplateImportResult.Rejected(TemplateImportRejectReason.NO_SPACE, "not enough space in app storage")
        }

        val appSignature = extractAppSignature(context)
        val archiveSignature = extractArchiveSignature(context, tmpTemplate)
        if (!TemplateImportValidator.compareSignatures(appSignature, archiveSignature)) {
            tmpTemplate.delete()
            return TemplateImportResult.Rejected(
                TemplateImportRejectReason.SIGNATURE_MISMATCH,
                "template signature does not match the installed app signature"
            )
        }

        val validator = TemplateImportValidator(minVersionCode = MIN_TEMPLATE_VERSION_CODE)
        val missing = validator.missingEntries(tmpTemplate)
        if (missing.contains(TemplateImportValidator.ENTRY_MANIFEST)) {
            tmpTemplate.delete()
            return TemplateImportResult.Rejected(TemplateImportRejectReason.NOT_APK, "missing: $missing")
        }
        if (missing.isNotEmpty()) {
            tmpTemplate.delete()
            return TemplateImportResult.Rejected(
                TemplateImportRejectReason.MISSING_RUNTIME_MARKER,
                "missing: $missing"
            )
        }

        val version = readVersionCode(tmpTemplate)
        if (version == null || version < MIN_TEMPLATE_VERSION_CODE) {
            tmpTemplate.delete()
            return TemplateImportResult.Rejected(
                TemplateImportRejectReason.TOO_OLD,
                "versionCode=$version, required>=$MIN_TEMPLATE_VERSION_CODE"
            )
        }

        return TemplateImportResult.Success(tmpTemplate, false)
    }

    fun finishTemplateImport(
        context: Context,
        pendingTemplate: File,
        hashUri: android.net.Uri
    ): TemplateImportResult {
        val cacheDir = File(context.filesDir, CACHE_DIR_NAME)
        try {
            val hashContent = context.contentResolver.openInputStream(hashUri)?.use { stream ->
                stream.readBytes().toString(Charsets.UTF_8)
            }
            if (hashContent == null) {
                return TemplateImportResult.Rejected(TemplateImportRejectReason.HASH_FILE_INVALID, "hash stream is not readable")
            }

            val expectedHash = TemplateImportValidator.parseSha256FileContent(hashContent)
            if (expectedHash == null) {
                return TemplateImportResult.Rejected(
                    TemplateImportRejectReason.HASH_FILE_INVALID,
                    "file does not contain a valid sha256 value"
                )
            }

            val actualHash = sha256Hex(pendingTemplate)
            if (!TemplateImportValidator.hashMatches(expectedHash, actualHash)) {
                return TemplateImportResult.Rejected(
                    TemplateImportRejectReason.HASH_MISMATCH,
                    "expected=$expectedHash actual=$actualHash"
                )
            }

            if (!hasEnoughSpace(cacheDir, pendingTemplate.length())) {
                return TemplateImportResult.Rejected(TemplateImportRejectReason.NO_SPACE, "not enough space in app storage")
            }

            val target = File(cacheDir, CACHE_FILE_NAME)
            val copied = copyValidTemplate(pendingTemplate, File(cacheDir, "$CACHE_FILE_NAME.verify"))
            if (!copied) {
                return TemplateImportResult.Rejected(TemplateImportRejectReason.IO_ERROR, "final validation failed")
            }
            val verified = File(cacheDir, "$CACHE_FILE_NAME.verify")
            if (!verified.renameTo(target)) {
                verified.delete()
                return TemplateImportResult.Rejected(TemplateImportRejectReason.IO_ERROR, "cannot move verified template into cache")
            }

            checkAndPinHash(context, target, repin = true)

            return TemplateImportResult.Success(target, true)
        } catch (e: Exception) {
            return TemplateImportResult.Rejected(TemplateImportRejectReason.IO_ERROR, e.message ?: "import failed")
        } finally {
            pendingTemplate.delete()
        }
    }

    fun cancelTemplateImport(pendingTemplate: File?) {
        pendingTemplate?.delete()
    }

    private fun extractArchiveSignature(context: Context, file: File): String? {
        val pm = context.packageManager
        val info = if (android.os.Build.VERSION.SDK_INT >= 28) {
            pm.getPackageArchiveInfo(file.absolutePath, android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES)
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageArchiveInfo(file.absolutePath, android.content.pm.PackageManager.GET_SIGNATURES)
        }
        val signatures = info?.signingInfo?.apkContentsSigners ?: info?.signatures ?: return null
        return signatures.mapNotNull { signature ->
            sha256Hex(signature.toByteArray())
        }.joinToString(",")
    }

    private fun extractAppSignature(context: Context): String? {
        val pm = context.packageManager
        val info = if (android.os.Build.VERSION.SDK_INT >= 28) {
            pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES)
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.GET_SIGNATURES)
        }
        val signatures = info?.signingInfo?.apkContentsSigners ?: info?.signatures ?: return null
        return signatures.mapNotNull { signature ->
            sha256Hex(signature.toByteArray())
        }.joinToString(",")
    }

    private fun readVersionCode(file: File): Int? {
        return runCatching {
            com.reandroid.apk.ApkModule.loadApkFile(file).use { module ->
                module.setLoadDefaultFramework(false)
                module.androidManifest.versionCode
            }
        }.getOrNull()
    }

    private fun sha256Hex(bytes: ByteArray): String? {
        return runCatching {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            digest.digest(bytes).joinToString("") { "%02x".format(it) }
        }.getOrNull()
    }

    fun prepareBaseApk(
        context: Context,
        workDir: File,
        onProgress: ((Float, String) -> Unit)? = null
    ): File {
        workDir.mkdirs()
        val target = File(workDir, "v3_base.apk")
        val reasons = mutableListOf<String>()

        val local = localTemplateOverride(context)
        if (local != null) {
            if (copyValidTemplate(local, target)) {
                Log.i(TAG, "Базовый шаблон: локальный файл ${local.absolutePath} (${local.length() / (1024 * 1024)} MB)")
                return target
            }
            reasons += "локальный шаблон не скопирован"
        }

        val cached = getCachedTemplate(context)
        if (cached != null && isValidTemplateApk(cached) && matchesPinnedHash(context, cached)) {
            if (copyValidTemplate(cached, target)) {
                Log.d(TAG, "Использую кэшированный шаблон (${cached.length() / (1024 * 1024)} MB)")
                return target
            }
            reasons += "кэшированный шаблон устарел или повреждён"
        }

        val outcome = downloadTemplate(context, null, true) { p, msg ->
            onProgress?.invoke(p, msg)
        }
        val downloaded = (outcome as? TemplateOutcome.Ready)?.file
        if (downloaded != null) {
            if (!hasEnoughSpace(workDir, downloaded.length() + 64L * 1024 * 1024)) {
                reasons += "недостаточно места в ${workDir.absolutePath} для копирования шаблона"
            } else if (isValidTemplateApk(downloaded) && copyValidTemplate(downloaded, target)) {
                Log.d(TAG, "Базовый шаблон: скачан с GitHub (${downloaded.length() / (1024 * 1024)} MB)")
                return target
            } else {
                reasons += "скачанный шаблон устарел или повреждён"
            }
        } else {
            val failure = outcome as TemplateOutcome.Failed
            reasons += "не удалось скачать шаблон с GitHub (${failure.failure}: ${failure.detail})"
        }

        throw IllegalStateException(
            "Базовый шаблон не найден. Причины: " + reasons.joinToString("; ")
        )
    }

    private fun localTemplateOverride(context: Context): File? {
        if (!BuildConfig.DEBUG) return null
        val candidates = listOfNotNull(
            context.getExternalFilesDir(null)?.let { File(it, LOCAL_TEMPLATE_NAME) },
            File(context.filesDir, LOCAL_TEMPLATE_NAME)
        )
        val found = candidates.firstOrNull { it.isFile && it.length() > 0 && isValidTemplateApk(it) }
        if (found != null) {
            Log.i(TAG, "Локальный шаблон: ${found.absolutePath}")
        }
        return found
    }

    private fun getCachedTemplate(context: Context): File? {
        val dir = File(context.filesDir, CACHE_DIR_NAME)
        val file = File(dir, CACHE_FILE_NAME)
        return if (file.exists() && file.length() > 0) file else null
    }

    private fun downloadTemplate(
        context: Context,
        previousCache: File?,
        repin: Boolean = false,
        onProgress: ((Float, String) -> Unit)?
    ): TemplateOutcome {
        val cacheDir = File(context.filesDir, CACHE_DIR_NAME)
        cacheDir.mkdirs()
        val tmp = File(cacheDir, "$CACHE_FILE_NAME.download")
        val finalFile = File(cacheDir, CACHE_FILE_NAME)
        val etagFile = File(cacheDir, ETAG_FILE_NAME)

        if (!hasEnoughSpace(cacheDir, 200L * 1024 * 1024 + 64L * 1024 * 1024)) {
            Log.e(TAG, "Недостаточно места для скачивания шаблона в ${cacheDir.absolutePath}")
            return TemplateOutcome.Failed(TemplateFailure.NO_SPACE, "", previousCache)
        }

        return try {
            var fetch = fetchToFile(client(15, 300), TEMPLATE_RUNTIME_URL, tmp, onProgress)
            if (!fetch.success) {
                tmp.delete()
                return TemplateOutcome.Failed(TemplateFailure.NETWORK, fetch.detail, previousCache)
            }
            if (isLfsPointer(tmp)) {
                Log.d(TAG, "raw URL вернул LFS-указатель, скачиваю с media.githubusercontent.com")
                tmp.delete()
                fetch = fetchToFile(client(15, 300), TEMPLATE_RUNTIME_MEDIA_URL, tmp, onProgress)
                if (!fetch.success) {
                    tmp.delete()
                    return TemplateOutcome.Failed(TemplateFailure.NETWORK, fetch.detail, previousCache)
                }
            }
            if (!isValidTemplateApk(tmp)) {
                Log.e(TAG, "Скачанный файл не является корректным APK или не содержит $TEMPLATE_MARKER")
                tmp.delete()
                return TemplateOutcome.Failed(TemplateFailure.BAD_FILE,
                    "template is outdated or invalid ($TEMPLATE_MARKER required)", previousCache)
            }
            if (!checkAndPinHash(context, tmp, repin)) {
                Log.e(TAG, "Хеш шаблона не совпал с закреплённым")
                tmp.delete()
                return TemplateOutcome.Failed(TemplateFailure.BAD_FILE, "hash mismatch", previousCache)
            }
            val storedEtag = runCatching { etagFile.readText().trim() }.getOrNull().orEmpty()
            if (previousCache != null && previousCache.exists() &&
                fetch.etag.isNotEmpty() && fetch.etag == storedEtag &&
                tmp.length() == previousCache.length()
            ) {
                tmp.delete()
                return TemplateOutcome.Ready(previousCache, false)
            }
            if (finalFile.exists()) finalFile.delete()
            if (!tmp.renameTo(finalFile)) {
                tmp.copyTo(finalFile, overwrite = true)
                tmp.delete()
            }
            runCatching { etagFile.writeText(fetch.etag) }
            TemplateOutcome.Ready(finalFile, true)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка скачивания шаблона", e)
            tmp.delete()
            TemplateOutcome.Failed(TemplateFailure.NETWORK, e.message.orEmpty(), previousCache)
        }
    }

    private fun fetchToFile(
        client: OkHttpClient,
        url: String,
        target: File,
        onProgress: ((Float, String) -> Unit)?
    ): FetchResult {
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    Log.e(TAG, "Скачивание $url: HTTP ${resp.code}")
                    return FetchResult(false, "", "HTTP ${resp.code}")
                }
                val body = resp.body ?: return FetchResult(false, "", "HTTP ${resp.code}")
                val total = body.contentLength()
                target.outputStream().use { out ->
                    body.byteStream().use { input ->
                        val buffer = ByteArray(DOWNLOAD_BUFFER_SIZE)
                        var downloaded = 0L
                        while (true) {
                            val read = input.read(buffer)
                            if (read < 0) break
                            out.write(buffer, 0, read)
                            downloaded += read
                            if (total > 0) {
                                onProgress?.invoke(
                                    (downloaded.toFloat() / total).coerceIn(0f, 1f),
                                    "Скачивание шаблона (${downloaded / (1024 * 1024)}/${total / (1024 * 1024)} MB)"
                                )
                            }
                        }
                    }
                }
                FetchResult(true, resp.header("ETag").orEmpty(), "")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка скачивания $url", e)
            FetchResult(false, "", e.message.orEmpty())
        }
    }

    private fun isLfsPointer(file: File): Boolean {
        return runCatching {
            file.inputStream().use { input ->
                val buf = ByteArray(128)
                val read = input.read(buf)
                read > 0 && String(buf, 0, read).startsWith(LFS_POINTER_PREFIX)
            }
        }.getOrDefault(false)
    }

    private fun client(connectSeconds: Long, readSeconds: Long): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(connectSeconds, TimeUnit.SECONDS)
            .readTimeout(readSeconds, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    private fun copyValidTemplate(source: File, target: File): Boolean {
        return runCatching {
            source.copyTo(target, overwrite = true)
            target.exists() && target.length() > 0 && isValidTemplateApk(target)
        }.getOrDefault(false)
    }

    private fun hasEnoughSpace(dir: File, neededBytes: Long): Boolean {
        return runCatching {
            val stat = android.os.StatFs(dir.absolutePath)
            val usable = stat.blockSizeLong * stat.availableBlocksLong
            usable >= neededBytes
        }.getOrDefault(true)
    }

    private fun isValidTemplateApk(file: File): Boolean {
        if (!file.exists() || file.length() < 4) return false
        return runCatching<Boolean> {
            java.util.zip.ZipFile(file).use { zip ->
                val hasManifest = zip.getEntry("AndroidManifest.xml") != null
                val hasDex = zip.getEntry("classes.dex") != null
                val hasMarker = zip.getEntry("assets/$TEMPLATE_MARKER") != null
                val sigValid = ApkVerifier.Builder(file).build().verify().isVerified
                val versionOk = runCatching<Boolean> {
                    ApkModule.loadApkFile(file).use { module ->
                        module.setLoadDefaultFramework(false)
                        module.androidManifest.versionCode >= MIN_TEMPLATE_VERSION_CODE
                    }
                }.getOrDefault(true)
                hasManifest && hasDex && hasMarker && sigValid && versionOk
            }
        }.getOrDefault(false)
    }

    private fun pinnedHash(context: Context): String {
        return runCatching {
            context.getSharedPreferences(PIN_PREFS_NAME, Context.MODE_PRIVATE)
                .getString(PIN_KEY, "").orEmpty()
        }.getOrDefault("")
    }

    private fun pinnedSize(context: Context): Long {
        return runCatching {
            context.getSharedPreferences(PIN_PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(PIN_SIZE_KEY, -1L)
        }.getOrDefault(-1L)
    }

    private fun matchesPinnedSize(context: Context, file: File): Boolean {
        val pinned = pinnedSize(context)
        return pinned < 0 || pinned == file.length()
    }

    private fun matchesPinnedHash(context: Context, file: File): Boolean {
        val pinned = pinnedHash(context)
        if (pinned.isEmpty()) return true
        return sha256Hex(file) == pinned
    }

    private fun checkAndPinHash(context: Context, file: File, repin: Boolean): Boolean {
        val actual = sha256Hex(file) ?: return false
        val pinned = pinnedHash(context)
        if (pinned.isEmpty() || repin) {
            runCatching {
                context.getSharedPreferences(PIN_PREFS_NAME, Context.MODE_PRIVATE).edit()
                    .putString(PIN_KEY, actual)
                    .putLong(PIN_SIZE_KEY, file.length())
                    .apply()
            }
            return true
        }
        return actual == pinned
    }

    private fun sha256Hex(file: File): String? {
        return runCatching {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            file.inputStream().use { input ->
                val buffer = ByteArray(DOWNLOAD_BUFFER_SIZE)
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    digest.update(buffer, 0, read)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        }.getOrNull()
    }
}
