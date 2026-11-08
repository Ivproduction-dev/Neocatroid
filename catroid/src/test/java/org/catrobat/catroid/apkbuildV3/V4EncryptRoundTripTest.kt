package org.catrobat.catroid.apkbuildV3

import org.catrobat.catroid.io.ZipArchiver
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.FileInputStream
import java.security.SecureRandom

class V4EncryptRoundTripTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun randomBytes(size: Int): ByteArray = ByteArray(size).also { SecureRandom().nextBytes(it) }

    private fun keyMaterial(): ByteArray = randomBytes(KeyMaterialV4.SIZE)

    private fun keyBlob(): ByteArray = randomBytes(ProjectEncryptorV4.KEY_BLOB_SIZE)

    private fun encrypt(
        source: File,
        dest: File,
        keyMaterial: ByteArray,
        salt: ByteArray = randomBytes(ProjectEncryptorV4.SALT_SIZE),
        blob: ByteArray = keyBlob()
    ) {
        ProjectEncryptorV4.encrypt(source, dest, keyMaterial, salt, blob)
    }

    @Test
    fun multiChunkRoundTrip() {
        val original = randomBytes(2 * 1024 * 1024 + 333_333)
        val source = tmp.newFile("payload.zip").apply { writeBytes(original) }
        val encrypted = File(tmp.root, "payload.bin")
        val decrypted = File(tmp.root, "payload.out")
        val material = keyMaterial()

        encrypt(source, encrypted, material)

        assertTrue(IntegrityValidatorV4.validate(encrypted, material))
        assertTrue(ProjectEncryptorV4.decryptAll(encrypted, material, decrypted))
        assertArrayEquals(original, decrypted.readBytes())
    }

    @Test
    fun emptyPayloadRoundTrip() {
        val source = tmp.newFile("empty.zip")
        val encrypted = File(tmp.root, "empty.bin")
        val decrypted = File(tmp.root, "empty.out")
        val material = keyMaterial()

        encrypt(source, encrypted, material)

        assertTrue(IntegrityValidatorV4.validate(encrypted, material))
        assertTrue(ProjectEncryptorV4.decryptAll(encrypted, material, decrypted))
        assertArrayEquals(ByteArray(0), decrypted.readBytes())
    }

    @Test
    fun wrongKeyFails() {
        val source = tmp.newFile("payload.zip").apply { writeBytes(randomBytes(200_000)) }
        val encrypted = File(tmp.root, "payload.bin")
        encrypt(source, encrypted, keyMaterial())

        val wrong = keyMaterial()
        assertFalse(IntegrityValidatorV4.validate(encrypted, wrong))
        assertFalse(ProjectEncryptorV4.decryptAll(encrypted, wrong, File(tmp.root, "wrong.out")))
    }

    @Test
    fun tamperedHeaderFails() {
        val source = tmp.newFile("payload.zip").apply { writeBytes(randomBytes(150_000)) }
        val encrypted = File(tmp.root, "payload.bin")
        val material = keyMaterial()
        encrypt(source, encrypted, material)
        assertTrue(IntegrityValidatorV4.validate(encrypted, material))

        val bytes = encrypted.readBytes()
        bytes[56] = (bytes[56].toInt() xor 0x01).toByte()
        encrypted.writeBytes(bytes)

        assertFalse(IntegrityValidatorV4.validate(encrypted, material))
    }

    @Test
    fun tamperedCiphertextFails() {
        val source = tmp.newFile("payload.zip").apply { writeBytes(randomBytes(150_000)) }
        val encrypted = File(tmp.root, "payload.bin")
        val material = keyMaterial()
        encrypt(source, encrypted, material)

        val bytes = encrypted.readBytes()
        val pos = bytes.size - 5
        bytes[pos] = (bytes[pos].toInt() xor 0xFF).toByte()
        encrypted.writeBytes(bytes)

        assertFalse(IntegrityValidatorV4.validate(encrypted, material))
        assertFalse(ProjectEncryptorV4.decryptAll(encrypted, material, File(tmp.root, "bad.out")))
    }

    @Test
    fun truncatedPayloadFails() {
        val source = tmp.newFile("payload.zip").apply { writeBytes(randomBytes(150_000)) }
        val encrypted = File(tmp.root, "payload.bin")
        val material = keyMaterial()
        encrypt(source, encrypted, material)

        val bytes = encrypted.readBytes()
        encrypted.writeBytes(bytes.copyOf(bytes.size - 64))

        assertFalse(IntegrityValidatorV4.validate(encrypted, material))
    }

    @Test
    fun keyBlobIsReadableFromPayloadPrefix() {
        val source = tmp.newFile("payload.zip").apply { writeBytes(randomBytes(4096)) }
        val encrypted = File(tmp.root, "payload.bin")
        val material = keyMaterial()
        val blob = keyBlob()
        encrypt(source, encrypted, material, blob = blob)

        FileInputStream(encrypted).use { input ->
            val header = ProjectEncryptorV4.readHeader(input)
            val readBlob = ProjectEncryptorV4.readKeyBlob(input)
            assertArrayEquals(blob, readBlob)
            assertTrue(ProjectEncryptorV4.verifyHeader(header, material))
        }
    }

    @Test
    fun saltChangeFailsHeaderAuthentication() {
        val source = tmp.newFile("payload.zip").apply { writeBytes(randomBytes(4096)) }
        val encrypted = File(tmp.root, "payload.bin")
        val material = keyMaterial()
        encrypt(source, encrypted, material)

        val bytes = encrypted.readBytes()
        bytes[24] = (bytes[24].toInt() xor 0x01).toByte()
        encrypted.writeBytes(bytes)

        assertFalse(IntegrityValidatorV4.validate(encrypted, material))
    }

    @Test
    fun streamingUnzipExtractsProjectAndVerifiesHash() {
        val sourceDir = tmp.newFolder("project")
        File(sourceDir, "code.xml").writeText("<project/>")
        File(sourceDir, "asset.bin").writeBytes(randomBytes(300_000))
        val zip = File(tmp.root, "project.zip")
        ZipArchiver().zip(zip, arrayOf(sourceDir))

        val encrypted = File(tmp.root, "payload.bin")
        val material = keyMaterial()
        encrypt(zip, encrypted, material)

        val outDir = tmp.newFolder("extracted")
        FileInputStream(encrypted).use { input ->
            val header = ProjectEncryptorV4.readHeader(input)
            ProjectEncryptorV4.readKeyBlob(input)
            ProjectEncryptorV4.openDecryptingStream(input, material, header).use { stream ->
                ZipArchiver().unzip(stream, outDir)
            }
        }

        assertArrayEquals(
            File(sourceDir, "asset.bin").readBytes(),
            File(outDir, "project/asset.bin").readBytes()
        )
        assertTrue(File(outDir, "project/code.xml").isFile)
    }
}
