package org.catrobat.catroid.apkbuildV3.runtime

import android.content.Context
import android.util.Log
import org.catrobat.catroid.apkbuildV3.KeyMaterialV4
import org.catrobat.catroid.apkbuildV3.NativeKeyVault
import org.catrobat.catroid.apkbuildV3.ProjectEncryptorV4
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.DedupManifestApplier
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import java.io.File

class ProjectLoaderV4(private val context: Context) {
    private val tag = "ProjectLoaderV4"
    private val payloadNameRegex = Regex("p[0-9a-f]{16}\\.bin")

    data class FullProjectResult(
        val project: Project,
        val projectDir: File
    )

    data class ProjectMetadata(
        val project: Project,
        val projectDir: File
    )

    @Volatile
    var lastError: String? = null
        private set

    fun loadFull(cacheDir: File, onProgress: ((Float) -> Unit)? = null): FullProjectResult? {
        val metadata = load(cacheDir, "project_extracted", onProgress) ?: return null
        return FullProjectResult(metadata.project, metadata.projectDir)
    }

    fun loadLight(cacheDir: File): ProjectMetadata? {
        return load(cacheDir, "project_light", null)
    }

    private fun load(cacheDir: File, extractDirName: String, onProgress: ((Float) -> Unit)?): ProjectMetadata? {
        lastError = null
        var extractDir: File? = null
        var keyMaterial: ByteArray? = null
        return try {
            onProgress?.invoke(0f)
            cleanStaleArtifacts(cacheDir)

            val payloadAssetName = findPayloadAsset()
                ?: return fail("payload", "no p*.bin payload in assets")
            onProgress?.invoke(0.1f)

            val prefix = try {
                context.assets.open(payloadAssetName).use { input ->
                    val parsedHeader = ProjectEncryptorV4.readHeader(input)
                    val parsedBlob = ProjectEncryptorV4.readKeyBlob(input)
                    parsedHeader to parsedBlob
                }
            } catch (t: Throwable) {
                return fail("payload", "asset $payloadAssetName unreadable (${t.javaClass.simpleName}: ${t.message})")
            }
            val header = prefix.first
            val keyBlob = prefix.second
            onProgress?.invoke(0.2f)

            val material = NativeKeyVault.unwrapForRuntime(context, keyBlob)
                ?: return fail("key", "key vault rejected this build (pepper or binding mismatch)")
            keyMaterial = material
            onProgress?.invoke(0.25f)

            val dir = File(cacheDir, extractDirName).apply {
                deleteRecursively()
                mkdirs()
            }
            extractDir = dir

            try {
                context.assets.open(payloadAssetName).use { input ->
                    ProjectEncryptorV4.skipFully(input, ProjectEncryptorV4.DATA_OFFSET.toLong())
                    val stream = ProjectEncryptorV4.openDecryptingStream(input, material, header)
                    ZipArchiver().unzip(stream, dir)
                }
            } catch (t: Throwable) {
                extractDir?.deleteRecursively()
                return fail("unzip", "${t.javaClass.simpleName}: ${t.message}")
            }
            onProgress?.invoke(0.75f)

            val restored = runCatching { DedupManifestApplier.apply(dir) }.getOrDefault(0)
            if (restored > 0) {
                Log.i(tag, "Restored $restored deduplicated file(s)")
            }

            val project = try {
                XstreamSerializer.getInstance().loadProject(dir, context)
            } catch (t: Throwable) {
                extractDir?.deleteRecursively()
                return fail("parse", "${t.javaClass.simpleName}: ${t.message}")
            } ?: run {
                extractDir?.deleteRecursively()
                return fail("parse", "loadProject returned null")
            }
            onProgress?.invoke(0.9f)

            File(dir, Constants.CODE_XML_FILE_NAME).delete()

            onProgress?.invoke(1f)
            Log.i(tag, "Project loaded: ${project.name} (${project.sceneList.size} scenes)")
            ProjectMetadata(project, dir)
        } catch (t: Throwable) {
            extractDir?.deleteRecursively()
            fail("fatal", "${t.javaClass.simpleName}: ${t.message}")
        } finally {
            KeyMaterialV4.zero(keyMaterial)
        }
    }

    private fun findPayloadAsset(): String? {
        val names = try {
            context.assets.list("") ?: return null
        } catch (t: Throwable) {
            return null
        }
        return names.filter { payloadNameRegex.matches(it) }.sorted().firstOrNull()
    }

    private fun cleanStaleArtifacts(cacheDir: File) {
        val lockFile = File(cacheDir, ".cleanup.lock")
        if (!lockFile.createNewFile()) {
            Log.d(tag, "Cleanup already in progress, skipping")
            return
        }
        try {
            for (name in listOf(
                "project.ncv3",
                "project_decrypted.zip",
                "project_light_decrypted.zip"
            )) {
                runCatching { File(cacheDir, name).delete() }
            }
        } finally {
            lockFile.delete()
        }
    }

    private fun fail(stage: String, message: String): Nothing? {
        lastError = "$stage: $message"
        Log.e(tag, lastError!!)
        return null
    }
}
