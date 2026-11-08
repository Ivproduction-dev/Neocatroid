package org.catrobat.catroid.apkbuildV3

import android.content.Context
import android.os.StatFs
import android.util.Log
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApkBuilderV3Engine {
    private const val TAG = "ApkBuilderV3Engine"
    private const val TEMPLATE_RUNTIME_APK = "template_runtime.apk"
    private const val BUILD_HEADROOM_MB = 256L
    private const val TEMPLATE_SIZE_ESTIMATE_MB = 300L
    private const val APK_COPY_COUNT = 6L

    suspend fun build(
        context: Context,
        projectDir: File,
        config: ApkBuilderV3Config,
        listener: BuildProgressListener
    ): AssemblyResult = withContext(Dispatchers.IO) {
        var tempDir: File? = null
        var keyMaterial: ByteArray? = null
        var pepperBytes: ByteArray? = null
        try {
            listener.onProgress(0f, "Initializing build environment...", "")

            tempDir = File(context.cacheDir, "apk_v3_${System.currentTimeMillis()}")
            tempDir.mkdirs()

            val projectSize = projectDir.sizeRecursively()
            val templateBytes = TemplateManagerV3.getCacheStatus(context).sizeBytes
                .takeIf { it > 0 } ?: (TEMPLATE_SIZE_ESTIMATE_MB * 1024 * 1024)
            val neededSpace = projectSize * 3 + templateBytes * APK_COPY_COUNT +
                BUILD_HEADROOM_MB * 1024 * 1024
            val diskSpaceError = checkDiskSpace(tempDir, neededSpace)
            if (diskSpaceError != null) {
                return@withContext AssemblyResult.Failure(diskSpaceError)
            }

            listener.onProgress(5f, "Loading and serializing project...", "")

            val currentProject = ProjectManager.getInstance().currentProject
            val project = if (currentProject?.directory?.canonicalFile == projectDir.canonicalFile) {
                currentProject
            } else {
                XstreamSerializer.getInstance().loadProject(projectDir, context)
            }

            if (project == null || project.sceneList.isEmpty()) {
                return@withContext AssemblyResult.Failure("Project is empty — no scenes to build.")
            }

            listener.onProgress(10f, "Packaging project resources...", Constants.CODE_XML_FILE_NAME)

            val stagingDir = File(tempDir, "staging")
            val payloadZip = File(tempDir, "payload_raw.zip")
            stageProjectPayload(project, projectDir, stagingDir, payloadZip, context) { file ->
                listener.onProgress(10f, "Packaging project resources...", file)
            }

            listener.onProgress(30f, "Generating encryption keys...", "")

            if (config.packageName.isBlank()) {
                return@withContext AssemblyResult.Failure("Package name is empty.")
            }

            val material = KeyMaterialV4.generate()
            keyMaterial = material

            val certSha256 = SigningKeyManager.ensureCertificateSha256(context) { file, alias, password ->
                V3ApkAssembler.generateKeystore(file, alias, password)
            }
            val pepper = generatePerBuildPepper(certSha256, config.packageName, config.versionCode.toLong())
            pepperBytes = pepper
            NativeKeyVault.setPepperOverride(pepper)
            val salt = ByteArray(ProjectEncryptorV4.SALT_SIZE).also { SecureRandom().nextBytes(it) }
            val tag = ByteArray(NativeKeyVault.TAG_SIZE).also { SecureRandom().nextBytes(it) }
            val keyBlob = NativeKeyVault.wrapForBuild(
                certSha256 = certSha256,
                packageName = config.packageName,
                versionCode = config.versionCode.toLong(),
                keyMaterial = material,
                salt = salt,
                tag = tag
            )
            val payloadAssetName = randomPayloadAssetName()

            listener.onProgress(35f, "Encrypting project payload (V4 format)...", payloadZip.name)

            val encryptedPayload = File(tempDir, "payload.bin")
            ProjectEncryptorV4.encrypt(
                sourceFile = payloadZip,
                destFile = encryptedPayload,
                keyMaterial = material,
                salt = salt,
                keyBlob = keyBlob,
                onProgress = { p ->
                    listener.onProgress(35f + p * 15f, "Encrypting project payload...", payloadZip.name)
                }
            )

            listener.onProgress(50f, "Verifying payload integrity...", encryptedPayload.name)

            if (!IntegrityValidatorV4.validate(encryptedPayload, material)) {
                return@withContext AssemblyResult.Failure("Payload integrity check failed. " +
                        "The encrypted project may be corrupted.")
            }
            if (!IntegrityValidatorV4.verifyKeyBlob(
                    keyBlob = keyBlob,
                    keyMaterial = material,
                    certSha256 = certSha256,
                    packageName = config.packageName,
                    versionCode = config.versionCode.toLong()
                )
            ) {
                return@withContext AssemblyResult.Failure("Key vault integrity check failed.")
            }

            payloadZip.delete()
            stagingDir.deleteRecursively()

            listener.onProgress(55f, "Preparing runtime template...", TEMPLATE_RUNTIME_APK)

            val assembledApk = V3ApkAssembler.assemble(
                context = context,
                config = config,
                encryptedPayload = encryptedPayload,
                payloadAssetName = payloadAssetName,
                keyMaterial = material,
                certSha256 = certSha256,
                workDir = tempDir,
                firebaseConfig = config.firebaseConfig,
                nativePepper = pepper
            ) { p, file ->
                listener.onProgress(55f + p * 43f, "Assembling APK...", file)
            }

            listener.onProgress(98f, "Finalizing...", assembledApk.name)

            val safeName = config.appName
                .replace(" ", "_")
                .replace(Regex("""[\\/:*?"<>|]"""), "_")
                .trim('_', '.')
                .ifBlank { "app" }

            val resultFile = File(context.cacheDir, "${safeName}.apk")
            if (resultFile.exists()) resultFile.delete()
            listener.onProgress(99f, "Finalizing...", resultFile.name)
            assembledApk.copyTo(resultFile, overwrite = true)

            tempDir.deleteRecursively()

            listener.onProgress(100f, "Done!", resultFile.name)

            Log.i(TAG, "Build complete: ${resultFile.absolutePath} " +
                    "(${resultFile.length() / (1024 * 1024)} MB)")

            AssemblyResult.Success(
                apkFile = resultFile,
                keyFileNames = emptyList(),
                templateType = config.templateType,
                totalSizeBytes = resultFile.length()
            )

        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "Out of memory during build", e)
            tempDir?.deleteRecursively()
            AssemblyResult.Failure(
                "Not enough memory to build this project. " +
                        "Close other applications and try again, or use Light Template."
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Build failed", e)
            tempDir?.deleteRecursively()
            AssemblyResult.Failure(
                e.message ?: "Unknown build error: ${e.javaClass.simpleName}",
                e
            )
        } finally {
            tempDir?.deleteRecursively()
            KeyMaterialV4.zero(keyMaterial)
            pepperBytes?.fill(0)
        }
    }

    private fun randomPayloadAssetName(): String {
        val nameBytes = ByteArray(8).also { SecureRandom().nextBytes(it) }
        return "p" + nameBytes.joinToString("") { "%02x".format(it) } + ".bin"
    }

    private fun stageProjectPayload(
        project: Project,
        projectDir: File,
        stagingDir: File,
        payloadZip: File,
        context: Context,
        onFile: (String) -> Unit
    ) {
        stagingDir.mkdirs()
        Log.d(TAG, "stageProjectPayload: start projectDir=${projectDir.absolutePath}")

        val xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n"
        val projectXml = xmlHeader + XstreamSerializer.getInstance().getXmlAsStringFromProject(project)
        File(stagingDir, Constants.CODE_XML_FILE_NAME).writeText(projectXml)
        onFile(Constants.CODE_XML_FILE_NAME)

        copyDirRecursive(File(projectDir, "files"), File(stagingDir, "files"), onFile)

        val allScenes = project.sceneList.toMutableList()
        if (project.hasGlobalScene() && project.globalScene != null &&
            allScenes.none { it.name == project.globalScene.name }
        ) {
            allScenes.add(project.globalScene)
        }
        for (scene in allScenes) {
            val sceneDirName = scene.getDirectory().name
            copyDirRecursive(File(projectDir, "$sceneDirName/images"), File(stagingDir, "$sceneDirName/images"), onFile)
            copyDirRecursive(File(projectDir, "$sceneDirName/sounds"), File(stagingDir, "$sceneDirName/sounds"), onFile)
            for (shot in listOf("automatic_screenshot.png", "manual_screenshot.png")) {
                val src = File(projectDir, "$sceneDirName/$shot")
                if (src.isFile) {
                    onFile("$sceneDirName/$shot")
                    MemoryAwarePipeline.copyFile(src, File(stagingDir, "$sceneDirName/$shot"))
                }
            }
        }
        Log.d(TAG, "stageProjectPayload: staged ${allScenes.size} scenes")

        ZipArchiver().zipDedup(payloadZip, stagingDir.listFiles() ?: emptyArray())
        onFile(payloadZip.name)

        Log.d(TAG, "Project staged: ${payloadZip.length() / (1024 * 1024)} MB")
    }

    private fun copyDir(src: File, dst: File, onFile: (String) -> Unit) {
        copyDirRecursive(src, dst, onFile)
    }

    private fun copyDirRecursive(src: File, dst: File, onFile: (String) -> Unit) {
        if (!src.exists()) return
        if (src.isFile) {
            onFile(src.name)
            MemoryAwarePipeline.copyFile(src, File(dst, src.name))
            return
        }
        if (!src.isDirectory) return
        dst.mkdirs()
        src.walkTopDown().forEach { file ->
            if (file == src || file.isDirectory) return@forEach
            val rel = file.relativeTo(src).path.replace('\\', '/')
            onFile("${src.name}/$rel")
            MemoryAwarePipeline.copyFile(file, File(dst, rel))
        }
    }

    private fun checkDiskSpace(dir: File, neededBytes: Long): String? {
        return try {
            val stat = StatFs(dir.absolutePath)
            val usable = stat.blockSizeLong * stat.availableBlocksLong
            if (usable < neededBytes) {
                "Insufficient disk space. Need ~${neededBytes / (1024 * 1024)} MB, available: ${usable / (1024 * 1024)} MB."
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun File.sizeRecursively(): Long {
        if (!exists()) return 0L
        if (isFile) return length()
        return walkTopDown().filter { it.isFile }.map { it.length() }.sum()
    }

    private fun generatePerBuildPepper(certSha256: ByteArray, packageName: String, versionCode: Long): ByteArray {
        val random = SecureRandom()
        val buildId = ByteArray(16).also { random.nextBytes(it) }
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(certSha256)
        digest.update(packageName.toByteArray(StandardCharsets.UTF_8))
        val versionBytes = ByteArray(8)
        for (i in 0..7) {
            versionBytes[i] = (versionCode shr (8 * (7 - i))).toByte()
        }
        digest.update(versionBytes)
        digest.update(buildId)
        return digest.digest()
    }
}
