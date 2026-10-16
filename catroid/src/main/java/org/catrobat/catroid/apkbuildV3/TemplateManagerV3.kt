package org.catrobat.catroid.apkbuildV3

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
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

    data class TemplateCacheStatus(val cached: Boolean, val sizeBytes: Long)

    enum class TemplateFailure { NO_SPACE, NETWORK, BAD_FILE }

    sealed interface TemplateOutcome {
        data class Ready(val file: File, val updated: Boolean) : TemplateOutcome
        data class Failed(val failure: TemplateFailure, val detail: String, val cachedFile: File?) : TemplateOutcome
    }

    private data class FetchResult(val success: Boolean, val etag: String, val detail: String)

    fun getCacheStatus(context: Context): TemplateCacheStatus {
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
        val cached = getCachedTemplate(context)
        val validCache = if (cached != null && isValidTemplateApk(cached) && matchesPinnedSize(context, cached)) cached else null
        if (!force && validCache != null) return TemplateOutcome.Ready(validCache, false)
        return downloadTemplate(context, validCache, force, onProgress)
    }

    fun prepareBaseApk(
        context: Context,
        workDir: File,
        onProgress: ((Float, String) -> Unit)? = null
    ): File {
        workDir.mkdirs()
        val target = File(workDir, "v3_base.apk")
        val reasons = mutableListOf<String>()

        val cached = getCachedTemplate(context)
        if (cached != null && isValidTemplateApk(cached) && matchesPinnedHash(context, cached)) {
            if (copyValidTemplate(cached, target)) {
                Log.d(TAG, "Использую кэшированный шаблон (${cached.length() / (1024 * 1024)} MB)")
                return target
            }
            reasons += "кэшированный шаблон не является корректным APK"
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
                reasons += "скачанный шаблон не является корректным APK"
            }
        } else {
            val failure = outcome as TemplateOutcome.Failed
            reasons += "не удалось скачать шаблон с GitHub (${failure.failure}: ${failure.detail})"
        }

        throw IllegalStateException(
            "Базовый шаблон не найден. Причины: " + reasons.joinToString("; ")
        )
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
                Log.e(TAG, "Скачанный файл не является корректным APK")
                tmp.delete()
                return TemplateOutcome.Failed(TemplateFailure.BAD_FILE, "", previousCache)
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
        return runCatching {
            java.util.zip.ZipFile(file).use { zip ->
                zip.getEntry("AndroidManifest.xml") != null && zip.getEntry("classes.dex") != null
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
