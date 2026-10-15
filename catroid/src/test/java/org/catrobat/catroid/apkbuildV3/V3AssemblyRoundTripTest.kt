package org.catrobat.catroid.apkbuildV3

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.security.SecureRandom
import java.util.zip.ZipFile

class V3AssemblyRoundTripTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun readAsset(apk: File, name: String): ByteArray {
        ZipFile(apk).use { zf ->
            val entry = zf.getEntry(name)
                ?: throw AssertionError("missing $name in ${apk.name}")
            return zf.getInputStream(entry).readBytes()
        }
    }

    @Test
    fun assembly_preservesEncryptedPayloadAndKeys_byteForByte() {
        val candidates = listOf(
            File("src/main/assets/template_runtime.apk"),
            File("C:\\Users\\ivanp\\AppData\\Local\\Temp\\opencode\\runtime_v3_from_phone.apk")
        )
        val base = candidates.firstOrNull { it.exists() }
        assumeTrue("no base APK found", base != null)
        base!!

        val random = SecureRandom()
        val original = ByteArray(280_000).also { random.nextBytes(it) }
        val src = File(tmp.root, "payload_raw.zip").apply { writeBytes(original) }
        val enc = File(tmp.root, "project.ncv3")
        val keyResult = DynamicKeyManager.generateKey("BisectTest")
        ProjectEncryptorV3.encrypt(src, enc, keyResult.selectedKey)
        assertTrue("fresh payload must validate", IntegrityValidator.validate(enc, keyResult.selectedKey, tmp.root))
        val encBytes = enc.readBytes()
        println("enc size=${encBytes.size}")

        val work = File(tmp.root, "work").apply { mkdirs() }

        val injected = File(work, "v3_injected.apk")
        V3ApkAssembler.injectAssets(
            baseApk = base,
            outApk = injected,
            payload = enc,
            keyFileNames = keyResult.keyFileNames,
            keyFileContents = keyResult.keyFileContents,
            templateType = TemplateType.FULL,
            workDir = work
        )
        println("injected size=${injected.length()}")
        assertArrayEquals(
            "payload must survive injectAssets",
            encBytes, readAsset(injected, "assets/project.ncv3")
        )
        for ((idx, name) in keyResult.keyFileNames.withIndex()) {
            assertArrayEquals(
                "key $name must survive injectAssets",
                keyResult.keyFileContents[idx].toByteArray(),
                readAsset(injected, "assets/$name")
            )
        }

        val patched = File(work, "v3_patched.apk")
        V3ApkAssembler.patchManifest(
            injected, patched,
            ApkBuilderV3Config(appName = "Bisect", packageName = "com.example.bisect", templateType = TemplateType.FULL)
        )
        println("patched size=${patched.length()}")
        assertArrayEquals(
            "payload must survive patchManifest",
            encBytes, readAsset(patched, "assets/project.ncv3")
        )

        val aligned = File(work, "v3_aligned.apk")
        V3ApkAssembler.normalizeApkForInstall(patched, aligned, File(work, "norm_tmp"))
        println("aligned size=${aligned.length()}")
        val finalPayload = readAsset(aligned, "assets/project.ncv3")
        assertArrayEquals("payload must survive normalize", encBytes, finalPayload)

        val dec = File(tmp.root, "payload.out.bin")
        assertTrue(
            "final payload must decrypt with original key",
            ProjectEncryptorV3.decryptAll(File(tmp.root, "final.ncv3").apply { writeBytes(finalPayload) }, keyResult.selectedKey, dec)
        )
        assertArrayEquals("round-tripped bytes must match original", original, dec.readBytes())
        println("ASSEMBLY ROUND TRIP OK")
    }
}
