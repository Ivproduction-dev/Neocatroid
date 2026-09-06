package org.catrobat.catroid.apkbuildV3.runtime

import android.content.Context
import android.util.Log
import org.catrobat.catroid.apkbuildV3.DynamicKeyManager
import org.catrobat.catroid.apkbuildV3.IntegrityValidator
import org.catrobat.catroid.apkbuildV3.ProjectEncryptorV3
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.DedupManifestApplier
import org.catrobat.catroid.io.ZipArchiver
import java.io.File

class ProjectLoaderV3(private val context: Context) {
    private val tag = "ProjectLoaderV3"
    private val payloadAssetName = "project.ncv3"

    data class FullProjectResult(
        val project: Project,
        val projectDir: File
    )

    @Volatile
    var lastError: String? = null
        private set

    private fun fail(stage: String, msg: String): Nothing? {
        lastError = "$stage: $msg"
        Log.e(tag, lastError!!)
        return null
    }

    fun loadFull(cacheDir: File, onProgress: ((Float) -> Unit)? = null): FullProjectResult? {
        lastError = null
        return try {
            onProgress?.invoke(0f)
            val key = resolveKey() ?: return fail("key", "no dynamic key in assets")
            onProgress?.invoke(0.1f)

            val encryptedFile = File(cacheDir, payloadAssetName)
            try {
                context.assets.open(payloadAssetName).use { input ->
                    encryptedFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (t: Throwable) {
                return fail("payload", "asset $payloadAssetName missing/unreadable (${t.javaClass.simpleName}: ${t.message})")
            }
            onProgress?.invoke(0.2f)

            if (!IntegrityValidator.validate(encryptedFile, key, cacheDir)) {
                val size = runCatching { encryptedFile.length() }.getOrDefault(-1)
                encryptedFile.delete()
                return fail("integrity", "hash mismatch or undecryptable (size=$size)")
            }
            onProgress?.invoke(0.3f)

            val decryptedZip = File(cacheDir, "project_decrypted.zip")
            if (!ProjectEncryptorV3.decryptAll(encryptedFile, key, decryptedZip)) {
                encryptedFile.delete()
                return fail("decrypt", "AES-GCM failed (wrong key or corrupted payload)")
            }
            encryptedFile.delete()
            onProgress?.invoke(0.6f)

            val extractDir = File(cacheDir, "project_extracted").apply {
                deleteRecursively()
                mkdirs()
            }
            try {
                ZipArchiver().unzip(decryptedZip, extractDir)
            } catch (t: Throwable) {
                decryptedZip.delete()
                return fail("unzip", "${t.javaClass.simpleName}: ${t.message}")
            }
            decryptedZip.delete()
            onProgress?.invoke(0.8f)

            val restored = runCatching { DedupManifestApplier.apply(extractDir) }.getOrDefault(0)
            if (restored > 0) {
                Log.i(tag, "Restored $restored deduplicated file(s)")
            }

            val project = try {
                XstreamSerializer.getInstance().loadProject(extractDir, context)
            } catch (t: Throwable) {
                return fail("parse", "${t.javaClass.simpleName}: ${t.message}")
            } ?: return fail("parse", "loadProject returned null (no code.xml?)")
            onProgress?.invoke(1f)

            Log.i(tag, "Full project loaded: ${project.name} (${project.sceneList.size} scenes)")
            FullProjectResult(project, extractDir)
        } catch (t: Throwable) {
            fail("fatal", "${t.javaClass.simpleName}: ${t.message}")
        }
    }

    fun loadLight(cacheDir: File): ProjectMetadata? {
        lastError = null
        return try {
            val key = resolveKey() ?: return fail("key", "no dynamic key in assets")

            val encryptedFile = File(cacheDir, payloadAssetName)
            try {
                context.assets.open(payloadAssetName).use { input ->
                    encryptedFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (t: Throwable) {
                return fail("payload", "asset $payloadAssetName missing/unreadable (${t.javaClass.simpleName}: ${t.message})")
            }

            if (!IntegrityValidator.validate(encryptedFile, key, cacheDir)) {
                val size = runCatching { encryptedFile.length() }.getOrDefault(-1)
                encryptedFile.delete()
                return fail("integrity", "hash mismatch or undecryptable (size=$size)")
            }

            val decryptedZip = File(cacheDir, "project_light_decrypted.zip")
            if (!ProjectEncryptorV3.decryptAll(encryptedFile, key, decryptedZip)) {
                encryptedFile.delete()
                return fail("decrypt", "AES-GCM failed (wrong key or corrupted payload)")
            }

            val extractDir = File(cacheDir, "project_light").apply {
                deleteRecursively()
                mkdirs()
            }

            try {
                ZipArchiver().unzip(decryptedZip, extractDir)
            } catch (t: Throwable) {
                return fail("unzip", "${t.javaClass.simpleName}: ${t.message}")
            }

            val restored = runCatching { DedupManifestApplier.apply(extractDir) }.getOrDefault(0)
            if (restored > 0) {
                Log.i(tag, "Restored $restored deduplicated file(s) (light load)")
            }

            val project = try {
                XstreamSerializer.getInstance().loadProject(extractDir, context)
            } catch (t: Throwable) {
                return fail("parse", "${t.javaClass.simpleName}: ${t.message}")
            } ?: return fail("parse", "loadProject returned null (no code.xml?)")

            val metadata = ProjectMetadata(
                project = project,
                projectDir = extractDir,
                encryptedPayloadFile = encryptedFile,
                key = key
            )

            Log.i(tag, "Light project metadata loaded: ${project.name} (${project.sceneList.size} scenes)")

            metadata
        } catch (t: Throwable) {
            fail("fatal", "${t.javaClass.simpleName}: ${t.message}")
        }
    }

    private fun resolveKey(): ByteArray? {
        val dynamicKey = try {
            DynamicKeyResolver.resolveKey(context)
        } catch (t: Throwable) {
            lastError = "key: resolver crashed (${t.javaClass.simpleName}: ${t.message})"
            Log.e(tag, lastError!!, t)
            return null
        }
        if (dynamicKey != null) {
            Log.i(tag, "Using dynamic key (${dynamicKey.size} bytes)")
            return dynamicKey
        }

        lastError = "key: no dynamic key in assets"
        Log.e(tag, lastError!!)
        return null
    }

    data class ProjectMetadata(
        val project: Project,
        val projectDir: File,
        val encryptedPayloadFile: File,
        val key: ByteArray
    )
}