package org.catrobat.catroid.apkbuildV3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.reandroid.apk.ApkModule
import com.reandroid.archive.FileInputSource
import com.reandroid.arsc.chunk.xml.AndroidManifestBlock
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.io.Flushable
import java.io.OutputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.Security
import java.util.Date
import java.util.zip.CRC32
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import com.android.apksig.ApkSigner
import com.android.apksig.ApkSigner.SignerConfig
import com.android.apksig.ApkVerifier

object V3ApkAssembler {
    private const val TAG = "V3ApkAssembler"

    private const val ATTR_NAME = 0x01010003
    private const val ATTR_LABEL = 0x01010001
    private const val ATTR_DEBUGGABLE = 0x0101000f
    private const val ATTR_TEST_ONLY = 0x01010272
    private const val ATTR_EXPORTED = 0x01010010
    private const val ATTR_AUTHORITIES = 0x01010018
    private const val ATTR_CONFIG_CHANGES = 0x0101001f
    private const val ATTR_RESIZEABLE_ACTIVITY = 0x01010476
    private const val ATTR_SUPPORTS_PIP = 0x01010477

    private const val ACTION_MAIN = "android.intent.action.MAIN"
    private const val CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER"

    private const val RUNTIME_LOADER = "org.catrobat.catroid.apkbuildV3.runtime.RuntimeLoaderActivityV3"
    private const val ASSET_PAYLOAD = "project.ncv3"
    private const val FULL_MARKER = "template_v3_full.marker"
    private const val NEOUI_RUNTIME_MARKER = "neoui_runtime_v1.marker"
    private val PAYLOAD_NAME_REGEX = Regex("p[0-9a-f]{16}\\.bin")

    init {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    internal fun nativePepperAvailable(apk: File): Boolean {
        return ZipFile(apk).use { zf ->
            val libs = zf.entries().asSequence()
                .filter { NativePepperSlot.PEPPER_LIB_REGEX.matches(it.name) }
                .toList()
            if (libs.isEmpty()) {
                false
            } else {
                libs.all { entry ->
                    zf.getInputStream(entry).use { input ->
                        NativePepperSlot.patchCount(input.readBytes()) == 1
                    }
                }
            }
        }
    }

    fun assemble(
        context: Context,
        config: ApkBuilderV3Config,
        encryptedPayload: File,
        payloadAssetName: String,
        keyMaterial: ByteArray,
        certSha256: ByteArray,
        workDir: File,
        firebaseConfig: FirebaseConfig? = null,
        nativePepper: ByteArray? = null,
        onProgress: ((Float, String) -> Unit)? = null
    ): File {
        onProgress?.invoke(0f, "")
        workDir.mkdirs()

        require(config.packageName.isNotBlank()) { "Package name is empty" }

        val baseApk = locateBaseApk(context, workDir, onProgress)
        if (nativePepper == null || nativePepper.size != NativeKeyVault.PEPPER_SIZE) {
            throw IllegalStateException("Pepper is required for the build")
        }
        if (!nativePepperAvailable(baseApk)) {
            throw IllegalStateException(
                "Runtime template does not contain the pepper slot in libapk_crypto.so. " +
                    "Rebuild template_runtime.apk (copyTemplateApk) and update it before building."
            )
        }
        if (config.loadingScreenMode == "CUSTOM" && config.loadingScreenLayoutJson != null) {
            require(runCatching { ZipFile(baseApk).use { it.getEntry("assets/$NEOUI_RUNTIME_MARKER") != null } }.getOrDefault(false)) {
                "Runtime template does not support customized loading screens. Update template_runtime.apk."
            }
        }
        onProgress?.invoke(0.15f, baseApk.name)

        val injectedApk = File(workDir, "v3_injected.apk")
        injectAssetsV4(baseApk, injectedApk, encryptedPayload, payloadAssetName, config.templateType, workDir, config)
        onProgress?.invoke(0.4f, injectedApk.name)

        val patchedApk = File(workDir, "v3_patched.apk")
        patchManifest(injectedApk, patchedApk, config)
        onProgress?.invoke(0.55f, patchedApk.name)

        val resourcesApk = if (firebaseConfig != null || !config.admobAppId.isNullOrBlank()) {
            val rApk = File(workDir, "v3_resources.apk")
            injectRuntimeResources(patchedApk, rApk, firebaseConfig, config.admobAppId)
            rApk
        } else {
            patchedApk
        }
        onProgress?.invoke(0.7f, resourcesApk.name)

        val iconFile = config.iconFile
        val iconApk = if (iconFile != null) {
            val iApk = File(workDir, "v3_icon.apk")
            if (injectAppIcon(resourcesApk, iApk, iconFile)) iApk else resourcesApk
        } else {
            resourcesApk
        }
        onProgress?.invoke(0.8f, iconApk.name)

        onProgress?.invoke(0.85f, iconApk.name)

        val alignedApk = File(workDir, "v3_aligned.apk")
        normalizeApkForInstall(iconApk, alignedApk, File(workDir, "v3_norm_tmp"), nativePepper = nativePepper)
        onProgress?.invoke(0.9f, alignedApk.name)

        val signedApk = File(workDir, "v3_signed.apk")
        val stableKeystore = SigningKeyManager.ensureKeystore(context) { file, alias, password ->
            generateKeystore(file, alias, password)
        }
        val keystore = File(workDir, "v3_keystore.jks")
        stableKeystore.copyTo(keystore, overwrite = true)
        signApk(alignedApk, signedApk, keystore, SigningKeyManager.ALIAS,
            SigningKeyManager.keystorePassword(context), deleteKeystoreAfterUse = true)
        onProgress?.invoke(1f, signedApk.name)

        verifyInstallableV4(
            signedApk = signedApk,
            payloadAssetName = payloadAssetName,
            keyMaterial = keyMaterial,
            packageName = config.packageName,
            versionCode = config.versionCode.toLong(),
            certSha256 = certSha256
        )

        Log.i(TAG, "APK собран: ${signedApk.absolutePath} (${signedApk.length() / (1024 * 1024)} MB)")
        return signedApk
    }

    private fun locateBaseApk(
        context: Context,
        workDir: File,
        onProgress: ((Float, String) -> Unit)? = null
    ): File {
        return TemplateManagerV3.prepareBaseApk(context, workDir) { p, msg ->
            onProgress?.invoke(p * 0.15f, msg)
        }
    }

    internal fun injectAssetsV4(
        baseApk: File,
        outApk: File,
        payload: File,
        payloadAssetName: String,
        templateType: TemplateType,
        workDir: File,
        config: ApkBuilderV3Config? = null
    ) {
        ApkModule.loadApkFile(baseApk).use { module ->
            module.setLoadDefaultFramework(false)
            removeStaleProjectAssets(module)

            module.add(FileInputSource(payload, "assets/$payloadAssetName"))

            if (templateType == TemplateType.FULL) {
                val markerFile = File(workDir, FULL_MARKER)
                markerFile.writeText("FULL")
                module.add(FileInputSource(markerFile, "assets/$FULL_MARKER"))
            }

            if (config != null) {
                if (config.loadingScreenMode == "NO_UI") {
                    val marker = File(workDir, "no_ui.marker")
                    marker.writeText("NO_UI")
                    module.add(FileInputSource(marker, "assets/neoui/no_ui.marker"))
                } else if (config.loadingScreenMode == "CUSTOM" && config.loadingScreenLayoutJson != null) {
                    val layoutFile = File(workDir, "neoui_layout.json")
                    layoutFile.writeText(config.loadingScreenLayoutJson)
                    module.add(FileInputSource(layoutFile, "assets/neoui/layout.json"))

                    val bgFile = config.loadingScreenBgImage
                    if (bgFile != null && bgFile.exists()) {
                        module.add(FileInputSource(bgFile, "assets/neoui/background.png"))
                    }

                    val compilationLayoutJson = config.compilationScreenLayoutJson
                    if (compilationLayoutJson != null) {
                        val compilationLayoutFile = File(workDir, "neoui_compilation.json")
                        compilationLayoutFile.writeText(compilationLayoutJson)
                        module.add(FileInputSource(compilationLayoutFile, "assets/neoui/compilation.json"))

                        val compilationBg = config.compilationScreenBgImage
                        if (compilationBg != null && compilationBg.exists()) {
                            module.add(FileInputSource(compilationBg, "assets/neoui/compilation_bg.png"))
                        }
                    }
                }
            }

            module.writeApk(outApk)
        }
    }

    internal fun injectAssets(
        baseApk: File,
        outApk: File,
        payload: File,
        keyFileNames: List<String>,
        keyFileContents: List<String>,
        templateType: TemplateType,
        workDir: File
    ) {
        ApkModule.loadApkFile(baseApk).use { module ->
            module.setLoadDefaultFramework(false)
            removeStaleProjectAssets(module)

            module.add(FileInputSource(payload, "assets/$ASSET_PAYLOAD"))

            for ((idx, fileName) in keyFileNames.withIndex()) {
                val content = keyFileContents.getOrElse(idx) { "" }.toByteArray()
                val keyFile = File(workDir, fileName)
                keyFile.writeBytes(content)
                module.add(FileInputSource(keyFile, "assets/$fileName"))
            }

            if (templateType == TemplateType.FULL) {
                val markerFile = File(workDir, FULL_MARKER)
                markerFile.writeText("FULL")
                module.add(FileInputSource(markerFile, "assets/$FULL_MARKER"))
            }

            module.writeApk(outApk)
        }
    }

    private fun removeStaleProjectAssets(module: ApkModule) {
        val fixed = listOf(
            "assets/project",
            "assets/project.zip",
            "assets/$ASSET_PAYLOAD",
            "assets/pepper.key",
            "assets/$FULL_MARKER",
            "assets/neoui/no_ui.marker",
            "assets/neoui/layout.json",
            "assets/neoui/background.png",
            "assets/neoui/compilation.json",
            "assets/neoui/compilation_bg.png"
        )
        for (name in fixed) {
            runCatching { module.removeInputSource(name) }
            runCatching { module.removeDir(name) }
        }
        runCatching { module.removeDir("assets/neoui") }

        val stale = module.zipEntryMap.listInputSources().map { it.name }.filter { name ->
            (name.startsWith("assets/nk_") && name.endsWith(".nk")) ||
                (name.startsWith("assets/p") && name.endsWith(".bin") && PAYLOAD_NAME_REGEX.matches(name.removePrefix("assets/")))
        }
        for (name in stale) {
            runCatching { module.removeInputSource(name) }
            runCatching { module.zipEntryMap.remove(name) }
        }
    }

    internal fun injectAppIcon(inputApk: File, outputApk: File, iconFile: File): Boolean {
        if (!iconFile.exists()) {
            Log.w(TAG, "App icon file not found, keeping template icon: ${iconFile.absolutePath}")
            return false
        }
        if (!inputApk.exists() || inputApk.length() < 4) {
            Log.w(TAG, "Input APK missing, keeping template icon")
            return false
        }
        val tmpFile = File(outputApk.absolutePath + ".tmp")
        val preparedDir = File(outputApk.parentFile ?: File("."), "v3_icon_tmp")
        return try {
            if (outputApk.exists()) outputApk.delete()
            preparedDir.deleteRecursively()
            preparedDir.mkdirs()
            val rasterTargets = mutableListOf<String>()
            val adaptiveTargets = mutableListOf<String>()
            ApkModule.loadApkFile(inputApk).use { module ->
                module.setLoadDefaultFramework(false)
                val table = module.getTableBlock(false)
                if (table != null) {
                    for (pkg in table.listPackages()) {
                        for (spec in pkg.listSpecTypePairs()) {
                            if (spec.typeName != "mipmap") continue
                            val resources = spec.resources
                            while (resources.hasNext()) {
                                val resEntry = resources.next()
                                val resName = resEntry.name ?: continue
                                if (!resName.contains("ic_launcher")) continue
                                for (entry in resEntry) {
                                    val value = entry.resValue ?: continue
                                    val path = value.valueAsString ?: continue
                                    if (!path.startsWith("res/")) continue
                                    when {
                                        path.endsWith(".xml", ignoreCase = true) -> {
                                            entry.setNull(true)
                                            adaptiveTargets.add(path)
                                        }
                                        path.endsWith(".png", ignoreCase = true) ||
                                            path.endsWith(".webp", ignoreCase = true) -> rasterTargets.add(path)
                                    }
                                }
                            }
                        }
                    }
                }
                if (rasterTargets.isEmpty()) {
                    rasterTargets.addAll(module.zipEntryMap.listInputSources()
                        .map { it.name }
                        .filter { path ->
                            val fileName = path.substringAfterLast('/')
                            fileName.startsWith("ic_launcher") &&
                                (fileName.endsWith(".png", ignoreCase = true) ||
                                    fileName.endsWith(".webp", ignoreCase = true))
                        })
                }
                if (rasterTargets.isEmpty() && adaptiveTargets.isEmpty()) {
                    Log.w(TAG, "No launcher icon entries found, keeping template icon")
                    return false
                }

                for (path in adaptiveTargets.distinct()) {
                    runCatching { module.removeInputSource(path) }
                    runCatching { module.zipEntryMap.remove(path) }
                }

                val preparedBySize = HashMap<Int, File>()
                for (path in rasterTargets.distinct()) {
                    val size = iconSizeForPath(path)
                    val png = preparedBySize.getOrPut(size) {
                        val out = File(preparedDir, "icon_$size.png")
                        prepareIconPng(iconFile, size, out) ?: iconFile
                    }
                    runCatching { module.removeInputSource(path) }
                    runCatching { module.zipEntryMap.remove(path) }
                    module.add(FileInputSource(png, path))
                }
                module.writeApk(tmpFile)
            }
            if (!tmpFile.exists() || tmpFile.length() < 4) {
                Log.e(TAG, "Icon injection produced empty file")
                tmpFile.delete()
                return false
            }
            if (outputApk.exists()) outputApk.delete()
            if (!tmpFile.renameTo(outputApk)) {
                tmpFile.copyTo(outputApk, overwrite = true)
                tmpFile.delete()
            }
            Log.i(
                TAG,
                "App icon injected: ${rasterTargets.distinct().size} raster entries, " +
                    "${adaptiveTargets.distinct().size} adaptive entries removed"
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject app icon, keeping template icon", e)
            runCatching { tmpFile.delete() }
            runCatching { if (outputApk.exists() && outputApk.length() < 4) outputApk.delete() }
            false
        } finally {
            preparedDir.deleteRecursively()
        }
    }

    private fun iconSizeForPath(path: String): Int {
        return when {
            path.contains("mipmap-mdpi") -> 48
            path.contains("mipmap-hdpi") -> 72
            path.contains("mipmap-xhdpi") -> 96
            path.contains("mipmap-xxhdpi") -> 144
            path.contains("mipmap-xxxhdpi") -> 192
            else -> 192
        }
    }

    private fun prepareIconPng(source: File, size: Int, target: File): File? {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(source.absolutePath, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
            var sample = 1
            while (bounds.outWidth / (sample * 2) >= size && bounds.outHeight / (sample * 2) >= size) {
                sample *= 2
            }
            val options = BitmapFactory.Options().apply { inSampleSize = sample }
            val decoded = BitmapFactory.decodeFile(source.absolutePath, options) ?: return null
            val side = minOf(decoded.width, decoded.height)
            if (side <= 0) {
                decoded.recycle()
                return null
            }
            val cropped = Bitmap.createBitmap(
                decoded, (decoded.width - side) / 2, (decoded.height - side) / 2, side, side
            )
            val scaled = Bitmap.createScaledBitmap(cropped, size, size, true)
            target.outputStream().use { scaled.compress(Bitmap.CompressFormat.PNG, 100, it) }
            if (scaled !== cropped) scaled.recycle()
            if (cropped !== decoded) cropped.recycle()
            decoded.recycle()
            if (target.length() > 0) target else null
        } catch (e: Throwable) {
            Log.w(TAG, "Failed to prepare ${size}px icon: ${e.message}")
            null
        }
    }

    internal fun patchManifest(injectedApk: File, outApk: File, config: ApkBuilderV3Config) {
        try {
            ApkModule.loadApkFile(injectedApk).use { module ->
                module.setLoadDefaultFramework(false)
                val manifest = module.androidManifest

                if (config.packageName.isNotBlank()) {
                    applyPackageRename(manifest, config.packageName)
                }

                manifest.versionCode = config.versionCode
                manifest.versionName = config.versionName

                manifest.minSdkVersion = maxOf(config.minSdk, ApkBuilderV3Config.RUNTIME_MIN_SDK)
                manifest.targetSdkVersion = config.targetSdk

                val appElem = manifest.applicationElement
                appElem.getOrCreateAndroidAttribute("label", ATTR_LABEL)
                    .setValueAsString(config.appName)

                appElem.getOrCreateAndroidAttribute("debuggable", ATTR_DEBUGGABLE)
                    .setValueAsBoolean(config.debuggable)
                appElem.getOrCreateAndroidAttribute("testOnly", ATTR_TEST_ONLY)
                    .setValueAsBoolean(false)
                appElem.getOrCreateAndroidAttribute("resizeableActivity", ATTR_RESIZEABLE_ACTIVITY)
                    .setValueAsBoolean(config.allowSplitScreen)

                syncPermissions(manifest, config.permissions)

                makeRuntimeLoaderLauncher(manifest, config)

                module.refreshManifest()
                module.writeApk(outApk)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to patch manifest", e)
            throw e
        }
    }

    internal fun syncPermissions(manifest: AndroidManifestBlock, permissions: List<String>) {
        val root = manifest.manifestElement
        val dynamicReceiverPerm = "${manifest.packageName}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
        val hasDeclaredDynamicPerm = root?.listElements("permission")?.any {
            it.searchAttributeByResourceId(ATTR_NAME)?.valueAsString == dynamicReceiverPerm
        } == true

        if (root != null) {
            root.listElements("uses-permission").toList().forEach { it.removeSelf() }
        }
        for (perm in permissions.distinct()) {
            manifest.addUsesPermission(perm)
        }
        if (hasDeclaredDynamicPerm) {
            manifest.addUsesPermission(dynamicReceiverPerm)
        }
    }

    internal fun makeRuntimeLoaderLauncher(
        manifest: com.reandroid.arsc.chunk.xml.AndroidManifestBlock,
        config: ApkBuilderV3Config
    ) {
        val appElem = manifest.applicationElement

        val activities = appElem.listElements("activity")
        for (act in activities) {
            val filters = act.listElements("intent-filter")
            for (filter in filters) {
                val categories = filter.listElements("category")
                for (cat in categories) {
                    val nameAttr = cat.searchAttributeByResourceId(ATTR_NAME)
                    if (nameAttr?.valueAsString == CATEGORY_LAUNCHER) {
                        filter.removeSelf()
                        break
                    }
                }
            }
        }

        val loaderElem = appElem.createChildElement("activity")
        loaderElem.getOrCreateAndroidAttribute("name", ATTR_NAME)
            .setValueAsString(RUNTIME_LOADER)
        loaderElem.getOrCreateAndroidAttribute("exported", ATTR_EXPORTED)
            .setValueAsBoolean(true)

        if (config.allowPip) {
            loaderElem.getOrCreateAndroidAttribute("supportsPictureInPicture", ATTR_SUPPORTS_PIP)
                .setValueAsBoolean(true)
            val configChangesAttr = loaderElem.getOrCreateAndroidAttribute("configChanges", ATTR_CONFIG_CHANGES)
            val existing = configChangesAttr.valueAsString ?: ""
            val required = setOf("orientation", "screenSize", "smallestScreenSize", "screenLayout")
            val merged = (existing.split('|').map { it.trim() }.filter { it.isNotEmpty() }.toSet() + required)
                .joinToString("|")
            configChangesAttr.setValueAsString(merged)
        }

        val filter = loaderElem.createChildElement("intent-filter")
        val action = filter.createChildElement("action")
        action.getOrCreateAndroidAttribute("name", ATTR_NAME)
            .setValueAsString(ACTION_MAIN)
        val category = filter.createChildElement("category")
        category.getOrCreateAndroidAttribute("name", ATTR_NAME)
            .setValueAsString(CATEGORY_LAUNCHER)
    }

    internal fun applyPackageRename(manifest: AndroidManifestBlock, newPackage: String) {
        val oldPackage = manifest.packageName ?: return
        if (newPackage == oldPackage) return

        manifest.ensureFullClassNames()

        val appClassName = manifest.applicationClassName
        if (appClassName != null && appClassName.startsWith(".")) {
            manifest.applicationClassName = manifest.fullClassName(appClassName)
        }

        for (provider in manifest.listApplicationElementsByTag("provider")) {
            val attr = provider.searchAttributeByResourceId(ATTR_AUTHORITIES)
            val oldAuth = attr?.getValueString()
            if (oldAuth != null) {
                attr.setValueAsString(replacePackageInAuthority(oldAuth, oldPackage, newPackage))
            }
        }

        val manifestElem = manifest.manifestElement
        if (manifestElem != null) {
            for (perm in manifestElem.listElements("permission")) {
                val nameAttr = perm.searchAttributeByResourceId(ATTR_NAME)
                val oldName = nameAttr?.valueAsString ?: ""
                if (oldName.contains("DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION")) {
                    nameAttr?.valueAsString = "$newPackage.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
                } else if (oldName.startsWith("$oldPackage.")) {
                    nameAttr?.valueAsString = newPackage + oldName.removePrefix(oldPackage)
                }
            }

            for (usesPerm in manifestElem.listElements("uses-permission")) {
                val nameAttr = usesPerm.searchAttributeByResourceId(ATTR_NAME)
                val oldName = nameAttr?.valueAsString ?: ""
                if (oldName.contains("DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION")) {
                    nameAttr?.valueAsString = "$newPackage.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
                } else if (oldName.startsWith("$oldPackage.")) {
                    nameAttr?.valueAsString = newPackage + oldName.removePrefix(oldPackage)
                }
            }
        }

        manifest.packageName = newPackage
    }

    internal fun replacePackageInAuthority(authority: String, oldPackage: String, newPackage: String): String {
        return authority.split(';').joinToString(";") { segment ->
            when {
                segment == oldPackage -> newPackage
                segment.startsWith("$oldPackage.") -> newPackage + segment.substring(oldPackage.length)
                else -> segment
            }
        }
    }

    internal fun injectRuntimeResources(
        inputApk: File,
        outputApk: File,
        firebaseConfig: FirebaseConfig?,
        admobAppId: String?
    ) {
        try {
            ApkModule.loadApkFile(inputApk).use { module ->
                module.setLoadDefaultFramework(false)
                val table = module.getTableBlock(false)

                if (table == null) {
                    Log.w(TAG, "Cannot inject runtime resources: no TableBlock found")
                    module.writeApk(outputApk)
                    return@use
                }

                val replacements = LinkedHashMap<String, String>()
                if (firebaseConfig != null) {
                    replacements["google_app_id"] = firebaseConfig.mobileSdkAppId
                    replacements["gcm_defaultSenderId"] = firebaseConfig.projectNumber
                    replacements["google_api_key"] = firebaseConfig.apiKey
                    replacements["google_crash_reporting_api_key"] = firebaseConfig.apiKey
                    replacements["project_id"] = firebaseConfig.projectId
                    replacements["google_storage_bucket"] = firebaseConfig.storageBucket
                    replacements["firebase_database_url"] = firebaseConfig.databaseUrl
                    replacements["default_web_client_id"] = firebaseConfig.defaultWebClientId
                }
                if (!admobAppId.isNullOrBlank()) {
                    replacements["admob_application_id"] = admobAppId.trim()
                }

                var updatedCount = 0
                for ((resName, resValue) in replacements) {
                    if (resValue.isBlank()) {
                        Log.d(TAG, "Runtime resource '$resName' has empty value, skipping")
                        continue
                    }
                    try {
                        val entry = table.getEntry("string", resName, "")
                        if (entry != null) {
                            entry.resValue?.setValueAsString(resValue)
                            updatedCount++
                            Log.d(TAG, "Updated runtime resource: $resName = $resValue")
                        } else {
                            Log.w(TAG, "Runtime resource '$resName' not found in resources.arsc")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to update runtime resource '$resName'", e)
                    }
                }

                Log.i(TAG, "Runtime resources injected: $updatedCount resources updated")
                module.refreshTable()
                module.writeApk(outputApk)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject runtime resources", e)
            throw e
        }
    }

    internal fun normalizeApkForInstall(inputApk: File, outputApk: File, tmpDir: File,
                                        nativePepper: ByteArray? = null) {
        require(inputApk.exists() && inputApk.length() > 0) {
            "normalizeApkForInstall: входной APK отсутствует: ${inputApk.absolutePath}"
        }
        tmpDir.mkdirs()
        if (outputApk.exists()) outputApk.delete()
        val tmpOut = File(outputApk.absolutePath + ".tmp")
        if (tmpOut.exists()) tmpOut.delete()

        val expectedDexSha256 = computeDexSha256(inputApk)

        data class CdEntry(
            val name: String, val method: Int, val crc: Int,
            val compressedSize: Int, val uncompressedSize: Int, val localHeaderOffset: Int
        )
        val cdEntries = mutableListOf<CdEntry>()
        val seenNames = mutableSetOf<String>()
        var entryCount = 0

        try {
            ZipFile(inputApk).use { zf ->
                BufferedOutputStream(FileOutputStream(tmpOut)).use { rawOut ->
                    LittleEndianWriter(rawOut).use { w ->

                        fun writeLocalHeader(
                            name: String, compSize: Int, uncompSize: Int,
                            method: Int, crc: Long, extraPad: Int
                        ) {
                            val nameBytes = name.toByteArray(Charsets.UTF_8)
                            w.writeInt(0x04034b50)
                            w.writeShort(20)
                            w.writeShort(0)
                            w.writeShort(method)
                            w.writeShort(0)
                            w.writeShort(0)
                            w.writeInt(crc.toInt())
                            w.writeInt(compSize)
                            w.writeInt(uncompSize)
                            w.writeShort(nameBytes.size)
                            w.writeShort(extraPad)
                            w.writeBytes(nameBytes)
                            if (extraPad > 0) w.writeBytes(ByteArray(extraPad))
                        }

                        fun alignPad(name: String): Int {
                            val rawOffset = w.position + 30L + name.toByteArray().size
                            return ((4 - (rawOffset % 4)) % 4).toInt()
                        }

                        val buf = ByteArray(64 * 1024)
                        val entries = zf.entries()
                        while (entries.hasMoreElements()) {
                            val ze = entries.nextElement()
                            val name = ze.name
                            if (ze.isDirectory) continue
                            if (name.startsWith("META-INF/") || name == "META-INF") continue
                            if (!seenNames.add(name)) {
                                Log.d(TAG, "normalize: skip duplicate: $name")
                                continue
                            }
                            if (name == "META-INF/MANIFEST.MF" || name.endsWith(".SF") ||
                                name.endsWith(".RSA") || name.endsWith(".EC")
                            ) continue

                            val patchedLib: ByteArray? = if (nativePepper != null &&
                                NativePepperSlot.PEPPER_LIB_REGEX.matches(name)) {
                                NativePepperSlot.patch(zf.getInputStream(ze).readBytes(), nativePepper)
                            } else null

                            if (patchedLib != null) {
                                val entryCrc = CRC32().apply { update(patchedLib) }
                                val pad = alignPad(name)
                                val offset = w.position.toInt()
                                writeLocalHeader(name, patchedLib.size, patchedLib.size, ZipEntry.STORED,
                                    entryCrc.value, pad)
                                w.writeBytes(patchedLib)
                                cdEntries.add(
                                    CdEntry(name, ZipEntry.STORED, entryCrc.value.toInt(),
                                        patchedLib.size, patchedLib.size, offset)
                                )
                                entryCount++
                                continue
                            }

                            if (ze.method == ZipEntry.STORED) {
                                val size = ze.size
                                require(size >= 0 && size <= Int.MAX_VALUE) {
                                    "normalize: bad STORED size for $name: $size"
                                }
                                val crc = ze.crc
                                require(crc >= 0) { "normalize: bad CRC for $name" }
                                val pad = alignPad(name)
                                val offset = w.position.toInt()
                                writeLocalHeader(name, size.toInt(), size.toInt(), ZipEntry.STORED, crc, pad)
                                zf.getInputStream(ze).use { ins ->
                                    var len: Int
                                    while (ins.read(buf).also { len = it } != -1) {
                                        w.write(buf, 0, len)
                                    }
                                }
                                cdEntries.add(
                                    CdEntry(name, ZipEntry.STORED, crc.toInt(), size.toInt(), size.toInt(), offset)
                                )
                                entryCount++
                            } else {
                                val tmpEntry = File(tmpDir, "e_${entryCount}.bin")
                                val crc32 = CRC32()
                                var uncompSize = 0L
                                zf.getInputStream(ze).use { ins ->
                                    tmpEntry.outputStream().use { outs ->
                                        var len: Int
                                        while (ins.read(buf).also { len = it } != -1) {
                                            crc32.update(buf, 0, len)
                                            outs.write(buf, 0, len)
                                            uncompSize += len
                                        }
                                    }
                                }
                                require(uncompSize <= Int.MAX_VALUE) {
                                    "normalize: entry too large: $name ($uncompSize)"
                                }
                                val compOut = ByteArrayOutputStream(
                                    minOf(maxOf(64, uncompSize.toInt() / 2), 8 * 1024 * 1024)
                                )
                                val deflater = Deflater(Deflater.DEFAULT_COMPRESSION, true)
                                val outBuf = ByteArray(64 * 1024)
                                tmpEntry.inputStream().use { ins ->
                                    var len: Int
                                    while (ins.read(buf).also { len = it } != -1) {
                                        deflater.setInput(buf, 0, len)
                                        while (!deflater.needsInput()) {
                                            val n = deflater.deflate(outBuf)
                                            if (n > 0) compOut.write(outBuf, 0, n)
                                        }
                                    }
                                }
                                deflater.finish()
                                while (!deflater.finished()) {
                                    val n = deflater.deflate(outBuf)
                                    if (n > 0) compOut.write(outBuf, 0, n)
                                }
                                deflater.end()
                                tmpEntry.delete()
                                val comp = compOut.toByteArray()
                                val offset = w.position.toInt()
                                writeLocalHeader(
                                    name, comp.size, uncompSize.toInt(),
                                    ZipEntry.DEFLATED, crc32.value, 0
                                )
                                w.writeBytes(comp)
                                cdEntries.add(
                                    CdEntry(
                                        name, ZipEntry.DEFLATED, crc32.value.toInt(),
                                        comp.size, uncompSize.toInt(), offset
                                    )
                                )
                                entryCount++
                            }
                        }

                        val cdOffset = w.position.toInt()
                        for (ce in cdEntries) {
                            val nb = ce.name.toByteArray(Charsets.UTF_8)
                            w.writeInt(0x02014b50)
                            w.writeShort(20)
                            w.writeShort(20)
                            w.writeShort(0)
                            w.writeShort(ce.method)
                            w.writeShort(0)
                            w.writeShort(0)
                            w.writeInt(ce.crc)
                            w.writeInt(ce.compressedSize)
                            w.writeInt(ce.uncompressedSize)
                            w.writeShort(nb.size)
                            w.writeShort(0)
                            w.writeShort(0)
                            w.writeShort(0)
                            w.writeShort(0)
                            w.writeInt(0)
                            w.writeInt(ce.localHeaderOffset)
                            w.writeBytes(nb)
                        }
                        val cdSize = w.position.toInt() - cdOffset
                        w.writeInt(0x06054b50)
                        w.writeShort(0)
                        w.writeShort(0)
                        w.writeShort(cdEntries.size)
                        w.writeShort(cdEntries.size)
                        w.writeInt(cdSize)
                        w.writeInt(cdOffset)
                        w.writeShort(0)
                    }
                }
            }

            require(entryCount > 0) { "normalizeApkForInstall: нет записей в ${inputApk.name}" }
            ZipFile(tmpOut).use { zf ->
                requireNotNull(zf.getEntry("AndroidManifest.xml")) {
                    "normalizeApkForInstall: потерян AndroidManifest.xml"
                }
                val actualDexSha256 = computeDexSha256(tmpOut)
                require(actualDexSha256 != null) { "normalizeApkForInstall: classes.dex отсутствует после нормализации" }
                require(MessageDigest.isEqual(expectedDexSha256, actualDexSha256)) {
                    "normalizeApkForInstall: classes.dex изменён (SHA-256 mismatch)"
                }
            }
            assertNoDataDescriptors(tmpOut)
            if (outputApk.exists()) outputApk.delete()
            if (!tmpOut.renameTo(outputApk)) {
                tmpOut.copyTo(outputApk, overwrite = true)
                tmpOut.delete()
            }
            Log.i(TAG, "normalize: $entryCount entries, ${outputApk.length() / (1024 * 1024)} MB, без дескрипторов, DEX verified")
        } finally {
            runCatching { tmpDir.deleteRecursively() }
            runCatching { if (tmpOut.exists() && !outputApk.exists()) tmpOut.delete() }
        }
    }

    private class LittleEndianWriter(private val out: OutputStream) : Closeable, Flushable {
        var position: Long = 0; private set
        fun writeShort(v: Int) {
            out.write(v and 0xFF)
            out.write((v shr 8) and 0xFF)
            position += 2
        }
        fun writeInt(v: Int) {
            out.write(v and 0xFF)
            out.write((v shr 8) and 0xFF)
            out.write((v shr 16) and 0xFF)
            out.write((v shr 24) and 0xFF)
            position += 4
        }
        fun writeBytes(data: ByteArray) {
            out.write(data)
            position += data.size
        }
        fun write(data: ByteArray, offset: Int, length: Int) {
            out.write(data, offset, length)
            position += length
        }
        override fun close() = out.close()
        override fun flush() = (out as? Flushable)?.flush() ?: out.flush()
    }

    internal fun assertNoDataDescriptors(apk: File) {
        java.io.RandomAccessFile(apk, "r").use { raf ->
            val length = raf.length()
            val header = ByteArray(30)
            var pos = 0L
            var checked = 0
            while (pos + 30 <= length) {
                raf.seek(pos)
                raf.readFully(header)
                if (header[0] != 0x50.toByte() || header[1] != 0x4b.toByte() ||
                    header[2] != 0x03.toByte() || header[3] != 0x04.toByte()
                ) break
                val flags = leShort(header, 6)
                require((flags and 0x08) == 0) {
                    "normalizeApkForInstall: запись #$checked использует data descriptor (флаг 0x08) — " +
                        "такой APK не ставится (INSTALL_PARSE_FAILED_NOT_APK)"
                }
                val compSize = leInt(header, 18).toLong() and 0xFFFFFFFFL
                val fnLen = leShort(header, 26)
                val exLen = leShort(header, 28)
                pos += 30 + fnLen + exLen + compSize
                checked++
                if (checked > 200_000) break
            }
            require(checked > 0) { "normalizeApkForInstall: не найдены local headers" }
            Log.d(TAG, "normalize: проверено local headers без дескрипторов: $checked")
        }
    }

    private fun leInt(data: ByteArray, pos: Int): Int =
        (data[pos].toInt() and 0xFF) or ((data[pos + 1].toInt() and 0xFF) shl 8) or
            ((data[pos + 2].toInt() and 0xFF) shl 16) or ((data[pos + 3].toInt() and 0xFF) shl 24)

    private fun leShort(data: ByteArray, pos: Int): Int =
        (data[pos].toInt() and 0xFF) or ((data[pos + 1].toInt() and 0xFF) shl 8)

    private fun computeDexSha256(apk: File): ByteArray? {
        return runCatching {
            java.util.zip.ZipFile(apk).use { zf ->
                val dexEntry = zf.getEntry("classes.dex")
                if (dexEntry == null) return@runCatching null
                val digest = MessageDigest.getInstance("SHA-256")
                val buffer = ByteArray(64 * 1024)
                zf.getInputStream(dexEntry).use { input ->
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        if (read > 0) digest.update(buffer, 0, read)
                    }
                }
                digest.digest()
            }
        }.getOrDefault(null)
    }

    internal fun verifyInstallable(signedApk: File) {
        require(signedApk.exists() && signedApk.length() > 0) {
            "Собранный APK пуст: ${signedApk.absolutePath}"
        }
        ZipFile(signedApk).use { zf ->
            requireNotNull(zf.getEntry("AndroidManifest.xml")) { "В APK нет AndroidManifest.xml" }
            requireNotNull(zf.getEntry("classes.dex")) { "В APK нет classes.dex — шаблон повреждён" }
            val payload = zf.getEntry("assets/$ASSET_PAYLOAD")
            require(payload != null && payload.size != 0L) {
                "В APK нет assets/$ASSET_PAYLOAD — игра не загрузится (payload потерян при сборке)"
            }
            val names = java.util.Collections.list(zf.entries()).map { it.name }
            require(names.any { it.startsWith("assets/nk_") && it.endsWith(".nk") }) {
                "В APK нет ключей шифрования assets/nk_*.nk — игра не загрузится (ключи потеряны при сборке)"
            }
        }
        val result = ApkVerifier.Builder(signedApk).build().verify()
        require(result.isVerified) {
            "Подпись APK не прошла проверку: ${result.errors}. " +
                "Файл ${signedApk.name} (${signedApk.length() / (1024 * 1024)} MB) ставить нельзя."
        }
        Log.i(TAG, "verify: подпись OK, warnings=${result.warnings}")
    }

    internal fun verifyInstallableV4(
        signedApk: File,
        payloadAssetName: String,
        keyMaterial: ByteArray,
        packageName: String,
        versionCode: Long,
        certSha256: ByteArray
    ) {
        require(signedApk.exists() && signedApk.length() > 0) {
            "Собранный APK пуст: ${signedApk.absolutePath}"
        }
        require(payloadAssetName.isNotBlank()) { "Payload asset name is empty" }
        require(PAYLOAD_NAME_REGEX.matches(payloadAssetName)) { "Invalid payload asset name" }
        require(KeyMaterialV4.isValid(keyMaterial)) { "Invalid key material" }

        ZipFile(signedApk).use { zf ->
            requireNotNull(zf.getEntry("AndroidManifest.xml")) { "В APK нет AndroidManifest.xml" }
            requireNotNull(zf.getEntry("classes.dex")) { "В APK нет classes.dex — шаблон повреждён" }
            val payload = zf.getEntry("assets/$payloadAssetName")
            require(payload != null && payload.size != 0L) {
                "В APK нет assets/$payloadAssetName — игра не загрузится (payload потерян при сборке)"
            }

            val names = java.util.Collections.list(zf.entries()).map { it.name }
            require(names.none { it.startsWith("assets/nk_") && it.endsWith(".nk") }) {
                "В APK остались устаревшие ключи assets/nk_*.nk"
            }
            require(names.none { it == "assets/pepper.key" }) {
                "В APK остался pepper.key — ключ должен жить только в libapk_crypto.so"
            }
            require(names.count { PAYLOAD_NAME_REGEX.matches(it.removePrefix("assets/")) } == 1) {
                "В APK должен быть ровно один payload assets/p*.bin"
            }

            val cryptoLibs = names.filter { NativePepperSlot.PEPPER_LIB_REGEX.matches(it) }
            require(cryptoLibs.isNotEmpty()) {
                "В APK нет libapk_crypto.so — подтвердить ключ игры нечем"
            }
            for (libName in cryptoLibs) {
                val libBytes = zf.getInputStream(zf.getEntry(libName)).readBytes()
                require(NativePepperSlot.patchCount(libBytes) == 0) {
                    "Pepper slot в $libName не подменён при сборке — игра не расшифруется"
                }
            }

            val dexSha256 = computeDexSha256(signedApk)
            require(dexSha256 != null) { "Не удалось вычислить SHA-256 classes.dex" }

            zf.getInputStream(payload).use { input ->
                val header = ProjectEncryptorV4.readHeader(input)
                val keyBlob = ProjectEncryptorV4.readKeyBlob(input)
                require(
                    NativeKeyVault.verifyForBuild(
                        certSha256 = certSha256,
                        packageName = packageName,
                        versionCode = versionCode,
                        keyMaterial = keyMaterial,
                        blob = keyBlob
                    )
                ) {
                    "Ключевой блок payload не соответствует ключу подписи или пакету"
                }
                ProjectEncryptorV4.validateChunks(input, keyMaterial, header)
            }
        }

        val result = ApkVerifier.Builder(signedApk).build().verify()
        require(result.isVerified) {
            "Подпись APK не прошла проверку: ${result.errors}. " +
                "Файл ${signedApk.name} (${signedApk.length() / (1024 * 1024)} MB) ставить нельзя."
        }
        Log.i(TAG, "verify v4: payload OK, подпись OK, warnings=${result.warnings}")
    }

    private fun signApk(inputApk: File, outputApk: File, keystoreFile: File, alias: String, password: String,
        deleteKeystoreAfterUse: Boolean = true) {
        doSign(inputApk, outputApk, keystoreFile, alias, password, deleteKeystoreAfterUse)
        Log.i(TAG, "APK подписан: ${outputApk.absolutePath}")
    }

    internal fun generateKeystore(keystoreFile: File, alias: String, password: String) {
        val bc = BouncyCastleProvider()

        val kpGen = KeyPairGenerator.getInstance("RSA", bc)
        kpGen.initialize(2048)
        val kp = kpGen.generateKeyPair()

        val now = Date()
        val validityDays = 3650L * 24 * 60 * 60 * 1000
        val certBuilder = JcaX509v3CertificateBuilder(
            X500Name("CN=NeoCatroid V3, O=NeoCatroid, C=US"),
            BigInteger(64, java.security.SecureRandom()),
            Date(now.time - 1000),
            Date(now.time + validityDays),
            X500Name("CN=NeoCatroid V3, O=NeoCatroid, C=US"),
            kp.public
        )
        val signer = JcaContentSignerBuilder("SHA256WithRSA")
            .setProvider(bc)
            .build(kp.private)
        val certificate = JcaX509CertificateConverter()
            .setProvider(bc)
            .getCertificate(certBuilder.build(signer))

        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(null, null)
        keyStore.setKeyEntry(alias, kp.private, password.toCharArray(), arrayOf(certificate))
        keystoreFile.outputStream().use { keyStore.store(it, password.toCharArray()) }
    }

    internal fun doSign(inputApk: File, outputApk: File, keystoreFile: File, alias: String, password: String,
        deleteKeystoreAfterUse: Boolean = true) {
        if (!keystoreFile.exists()) {
            generateKeystore(keystoreFile, alias, password)
        }
        try {
            val keyStore = KeyStore.getInstance("PKCS12")
            keystoreFile.inputStream().use { keyStore.load(it, password.toCharArray()) }
            val privateKey = keyStore.getKey(alias, password.toCharArray()) as PrivateKey
            val certificate = keyStore.getCertificate(alias) as java.security.cert.X509Certificate
            val signerConfig = SignerConfig.Builder(alias, privateKey, listOf(certificate)).build()
            ApkSigner.Builder(listOf(signerConfig))
                .setInputApk(inputApk)
                .setOutputApk(outputApk)
                .setV1SigningEnabled(true)
                .setV2SigningEnabled(true)
                .setV3SigningEnabled(true)
                .build()
                .sign()
        } finally {
            if (deleteKeystoreAfterUse) {
                keystoreFile.delete()
            }
        }
    }
}
