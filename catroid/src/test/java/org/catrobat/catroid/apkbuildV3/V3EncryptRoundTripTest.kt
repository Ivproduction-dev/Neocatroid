package org.catrobat.catroid.apkbuildV3

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.security.SecureRandom

class V3EncryptRoundTripTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun randomKey(): ByteArray = ByteArray(32).also { SecureRandom().nextBytes(it) }

    @Test
    fun multiChunkEncryptDecrypt_roundTripsExactly() {
        val random = SecureRandom()
        val partRandom = ByteArray(2 * 1024 * 1024).also { random.nextBytes(it) }
        val partText = ByteArray(1_500_000) { i -> ('A'.code + (i % 26)).toByte() }
        val original = partRandom + partText

        val src = tmp.newFile("payload.bin").apply { writeBytes(original) }
        val enc = File(tmp.root, "payload.ncv3")
        val dec = File(tmp.root, "payload.out.bin")
        val key = randomKey()

        ProjectEncryptorV3.encrypt(src, enc, key)
        assertTrue("encrypted file must exist and be non-empty", enc.exists() && enc.length() > 0)

        assertTrue("fresh payload must validate", IntegrityValidator.validate(enc, key, tmp.root))
        assertTrue("decryptAll must succeed", ProjectEncryptorV3.decryptAll(enc, key, dec))
        assertArrayEquals("decrypted bytes must match original", original, dec.readBytes())
    }

    @Test
    fun wrongKey_failsValidationAndDecryption() {
        val src = tmp.newFile("small.bin").apply { writeBytes(ByteArray(4096) { it.toByte() }) }
        val enc = File(tmp.root, "small.ncv3")
        ProjectEncryptorV3.encrypt(src, enc, randomKey())

        val wrongKey = randomKey()
        assertFalse("wrong key must fail validation", IntegrityValidator.validate(enc, wrongKey, tmp.root))
        assertFalse(
            "wrong key must fail decryptAll",
            ProjectEncryptorV3.decryptAll(enc, wrongKey, File(tmp.root, "wrong.out"))
        )
    }

    @Test
    fun tamperedCiphertext_failsValidation() {
        val random = SecureRandom()
        val src = tmp.newFile("data.bin").apply {
            writeBytes(ByteArray(700_000).also { random.nextBytes(it) })
        }
        val enc = File(tmp.root, "data.ncv3")
        val key = randomKey()
        ProjectEncryptorV3.encrypt(src, enc, key)
        assertTrue(IntegrityValidator.validate(enc, key, tmp.root))

        val bytes = enc.readBytes()
        val pos = bytes.size - 100
        bytes[pos] = (bytes[pos].toInt() xor 0xFF).toByte()
        enc.writeBytes(bytes)

        assertFalse("tampered payload must fail validation", IntegrityValidator.validate(enc, key, tmp.root))
    }

    @Test
    fun keyManager_generateResolveRoundTrip() {
        val gen = DynamicKeyManager.generateKey("RoundTripProject")
        assertTrue(
            "generated key files must pass integrity",
            DynamicKeyManager.verifyKeyIntegrity(gen.keyFileContents)
        )
        val realParts = gen.keyFileNames
            .filter { it.startsWith(DynamicKeyManager.KEY_FILE_PREFIX) && !it.contains("decoy") }
            .sortedBy { name ->
                name.removePrefix(DynamicKeyManager.KEY_FILE_PREFIX)
                    .removeSuffix(".nk").substringBefore('_').toInt()
            }
            .map { name -> gen.keyFileContents[gen.keyFileNames.indexOf(name)] }
        assertTrue(
            "resolved key must equal generated key",
            DynamicKeyManager.resolveStoredKey(realParts).contentEquals(gen.selectedKey)
        )
    }
}
