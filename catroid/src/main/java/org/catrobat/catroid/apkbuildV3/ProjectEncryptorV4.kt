package org.catrobat.catroid.apkbuildV3

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec

object ProjectEncryptorV4 {
    private const val TAG = "ProjectEncryptorV4"

    private val MAGIC = byteArrayOf('N'.code.toByte(), 'C'.code.toByte(), 'V'.code.toByte(), '4'.code.toByte())
    private const val FORMAT_VERSION = 1
    private const val CHUNK_SIZE = 1024 * 1024
    private const val GCM_IV_SIZE = 12
    private const val GCM_TAG_SIZE = 16
    private const val GCM_TAG_LENGTH = 128
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val HMAC_ALGORITHM = "HmacSHA256"

    const val HEADER_SIZE = 120
    const val KEY_BLOB_OFFSET = 120
    const val KEY_BLOB_SIZE = 136
    const val DATA_OFFSET = 256
    const val SALT_SIZE = 16

    private const val AAD_HEADER_SIZE = 56
    private const val MAC_COVERED_SIZE = 88

    data class Header(
        val raw: ByteArray,
        val chunkSize: Int,
        val totalChunks: Int,
        val plaintextSize: Long,
        val salt: ByteArray,
        val keyBlobOffset: Long,
        val keyBlobLength: Int,
        val plaintextSha256: ByteArray,
        val headerMac: ByteArray
    )

    fun encrypt(
        sourceFile: File,
        destFile: File,
        keyMaterial: ByteArray,
        salt: ByteArray,
        keyBlob: ByteArray,
        onProgress: ((Float) -> Unit)? = null
    ) {
        require(keyMaterial.size == KeyMaterialV4.SIZE) { "Invalid key material size: ${keyMaterial.size}" }
        require(salt.size == SALT_SIZE) { "Invalid salt size: ${salt.size}" }
        require(keyBlob.size == KEY_BLOB_SIZE) { "Invalid key blob size: ${keyBlob.size}" }

        val totalSize = sourceFile.length()
        val chunkCount = expectedChunkCount(totalSize, CHUNK_SIZE.toLong())
        require(chunkCount <= Int.MAX_VALUE) { "Project payload is too large" }
        val totalChunks = chunkCount.toInt()
        val plaintextHash = computeSha256(sourceFile)
        val header = buildHeader(CHUNK_SIZE, totalChunks, totalSize, salt, plaintextHash, keyMaterial)

        destFile.parentFile?.mkdirs()
        val random = SecureRandom()

        FileOutputStream(destFile).use { output ->
            output.write(header.raw)
            output.write(keyBlob)

            FileInputStream(sourceFile).use { input ->
                val buffer = ByteArray(CHUNK_SIZE)
                var chunkIndex = 0
                while (true) {
                    val read = readChunk(input, buffer)
                    if (read <= 0) break
                    require(chunkIndex < totalChunks) { "Chunk count overflow" }

                    val iv = ByteArray(GCM_IV_SIZE).also { random.nextBytes(it) }
                    val cipher = Cipher.getInstance(ALGORITHM)
                    cipher.init(Cipher.ENCRYPT_MODE, KeyMaterialV4.encKeySpec(keyMaterial),
                        GCMParameterSpec(GCM_TAG_LENGTH, iv))
                    cipher.updateAAD(buildAad(header.raw, chunkIndex, totalChunks, chunkIndex == totalChunks - 1))

                    val ciphertext = cipher.doFinal(buffer, 0, read)
                    output.write(iv)
                    output.write(ciphertext)

                    chunkIndex++
                    onProgress?.invoke(chunkIndex.toFloat() / maxOf(totalChunks, 1))
                }
                require(chunkIndex == totalChunks) { "Chunk count mismatch: $chunkIndex != $totalChunks" }
            }
        }
    }

    fun readHeader(input: InputStream): Header {
        val raw = ByteArray(HEADER_SIZE)
        readFully(input, raw)
        return parseHeader(raw)
    }

    fun readHeader(file: File): Header {
        return FileInputStream(file).use { readHeader(it) }
    }

    fun readKeyBlob(input: InputStream): ByteArray {
        val blob = ByteArray(KEY_BLOB_SIZE)
        readFully(input, blob)
        return blob
    }

    fun readKeyBlob(file: File): ByteArray {
        return FileInputStream(file).use { input ->
            skipFully(input, KEY_BLOB_OFFSET.toLong())
            readKeyBlob(input)
        }
    }

    fun verifyHeader(header: Header, keyMaterial: ByteArray): Boolean {
        if (keyMaterial.size != KeyMaterialV4.SIZE) return false
        val computed = computeHeaderMac(header.raw, keyMaterial)
        val equal = MessageDigest.isEqual(computed, header.headerMac)
        return equal
    }

    fun openDecryptingStream(
        inputAtDataOffset: InputStream,
        keyMaterial: ByteArray,
        header: Header
    ): InputStream {
        return ChunkedDecryptingInputStream(inputAtDataOffset, keyMaterial, header)
    }

    fun decryptAll(encryptedFile: File, keyMaterial: ByteArray, destFile: File): Boolean {
        return try {
            destFile.parentFile?.mkdirs()
            FileInputStream(encryptedFile).use { input ->
                val header = readHeader(input)
                require(verifyHeader(header, keyMaterial)) { "Header authentication failed" }
                readKeyBlob(input)
                val stream = openDecryptingStream(input, keyMaterial, header)
                FileOutputStream(destFile).use { output ->
                    stream.use { it.copyTo(output) }
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Full decryption failed", e)
            runCatching { destFile.delete() }
            false
        }
    }

    fun validate(encryptedFile: File, keyMaterial: ByteArray): Boolean {
        return try {
            FileInputStream(encryptedFile).use { input ->
                val header = readHeader(input)
                require(verifyHeader(header, keyMaterial)) { "Header authentication failed" }
                readKeyBlob(input)
                validateChunks(input, keyMaterial, header)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Validation failed", e)
            false
        }
    }

    fun validateChunks(inputAtDataOffset: InputStream, keyMaterial: ByteArray, header: Header) {
        val stream = openDecryptingStream(inputAtDataOffset, keyMaterial, header)
        stream.use { input ->
            val scratch = ByteArray(64 * 1024)
            while (input.read(scratch) != -1) {
            }
        }
    }

    fun computePlaintextSha256(file: File): ByteArray = computeSha256(file)

    fun skipFully(input: InputStream, count: Long) {
        var remaining = count
        while (remaining > 0) {
            val skipped = input.skip(remaining)
            if (skipped <= 0) {
                if (input.read() < 0) throw IOException("Unexpected end of stream")
                remaining--
            } else {
                remaining -= skipped
            }
        }
    }

    private fun parseHeader(raw: ByteArray): Header {
        if (raw.size != HEADER_SIZE) throw IOException("Invalid header size: ${raw.size}")
        if (!raw.copyOfRange(0, 4).contentEquals(MAGIC)) throw IOException("Not a V4 payload")
        val buffer = ByteBuffer.wrap(raw).order(ByteOrder.BIG_ENDIAN)
        val version = buffer.getShort(4).toInt() and 0xFFFF
        val flags = buffer.getShort(6).toInt() and 0xFFFF
        val chunkSize = buffer.getInt(8)
        val totalChunks = buffer.getInt(12)
        val plaintextSize = buffer.getLong(16)
        val salt = raw.copyOfRange(24, 40)
        val keyBlobOffset = buffer.getLong(40)
        val keyBlobLength = buffer.getInt(48)
        val plaintextSha = raw.copyOfRange(56, 88)
        val headerMac = raw.copyOfRange(88, 120)

        if (version != FORMAT_VERSION) throw IOException("Unsupported payload version: $version")
        if (flags != 0) throw IOException("Unsupported payload flags: $flags")
        if (chunkSize <= 0 || chunkSize > 16 * 1024 * 1024) throw IOException("Invalid chunk size: $chunkSize")
        if (totalChunks < 0) throw IOException("Invalid chunk count: $totalChunks")
        if (plaintextSize < 0) throw IOException("Invalid plaintext size: $plaintextSize")
        if (keyBlobOffset != KEY_BLOB_OFFSET.toLong()) throw IOException("Invalid key blob offset: $keyBlobOffset")
        if (keyBlobLength != KEY_BLOB_SIZE) throw IOException("Invalid key blob length: $keyBlobLength")
        if (salt.size != SALT_SIZE) throw IOException("Invalid salt size: ${salt.size}")

        val expectedChunks = expectedChunkCount(plaintextSize, chunkSize.toLong())
        if (expectedChunks > Int.MAX_VALUE || totalChunks.toLong() != expectedChunks) {
            throw IOException("Chunk count mismatch: $totalChunks != $expectedChunks")
        }

        return Header(
            raw = raw,
            chunkSize = chunkSize,
            totalChunks = totalChunks,
            plaintextSize = plaintextSize,
            salt = salt,
            keyBlobOffset = keyBlobOffset,
            keyBlobLength = keyBlobLength,
            plaintextSha256 = plaintextSha,
            headerMac = headerMac
        )
    }

    private fun buildHeader(
        chunkSize: Int,
        totalChunks: Int,
        plaintextSize: Long,
        salt: ByteArray,
        plaintextSha256: ByteArray,
        keyMaterial: ByteArray
    ): Header {
        val raw = ByteArray(HEADER_SIZE)
        val buffer = ByteBuffer.wrap(raw).order(ByteOrder.BIG_ENDIAN)
        buffer.put(MAGIC)
        buffer.putShort(FORMAT_VERSION.toShort())
        buffer.putShort(0)
        buffer.putInt(chunkSize)
        buffer.putInt(totalChunks)
        buffer.putLong(plaintextSize)
        buffer.put(salt)
        buffer.putLong(KEY_BLOB_OFFSET.toLong())
        buffer.putInt(KEY_BLOB_SIZE)
        buffer.putInt(0)
        buffer.put(plaintextSha256)
        val mac = computeHeaderMac(raw, keyMaterial)
        System.arraycopy(mac, 0, raw, HEADER_SIZE - mac.size, mac.size)
        return parseHeader(raw)
    }

    private fun computeHeaderMac(raw: ByteArray, keyMaterial: ByteArray): ByteArray {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(KeyMaterialV4.macKeySpec(keyMaterial))
        mac.update(raw, 0, MAC_COVERED_SIZE)
        return mac.doFinal()
    }

    private fun buildAad(rawHeader: ByteArray, chunkIndex: Int, totalChunks: Int, last: Boolean): ByteArray {
        val aad = ByteArray(AAD_HEADER_SIZE + 9)
        System.arraycopy(rawHeader, 0, aad, 0, AAD_HEADER_SIZE)
        val buffer = ByteBuffer.wrap(aad, AAD_HEADER_SIZE, 9).order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(chunkIndex)
        buffer.putInt(totalChunks)
        buffer.put(if (last) 1.toByte() else 0.toByte())
        return aad
    }

    private fun computeSha256(file: File): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(64 * 1024)
        FileInputStream(file).use { input ->
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                if (read > 0) digest.update(buffer, 0, read)
            }
        }
        return digest.digest()
    }

    private fun expectedChunkCount(plaintextSize: Long, chunkSize: Long): Long {
        if (plaintextSize == 0L) return 0L
        return 1L + (plaintextSize - 1L) / chunkSize
    }

    private fun readChunk(input: InputStream, buffer: ByteArray): Int {
        var total = 0
        while (total < buffer.size) {
            val read = input.read(buffer, total, buffer.size - total)
            if (read < 0) break
            total += read
        }
        return total
    }

    private fun readFully(input: InputStream, buffer: ByteArray) {
        var offset = 0
        while (offset < buffer.size) {
            val read = input.read(buffer, offset, buffer.size - offset)
            if (read < 0) throw IOException("Unexpected end of stream")
            offset += read
        }
    }

    private class ChunkedDecryptingInputStream(
        private val input: InputStream,
        keyMaterial: ByteArray,
        private val header: Header
    ) : InputStream() {
        private val aesKey = KeyMaterialV4.encKeySpec(keyMaterial)
        private val digest = MessageDigest.getInstance("SHA-256")
        private var chunkIndex = 0
        private var buffer = ByteArray(0)
        private var bufferOffset = 0
        private var finished = false
        private var closed = false

        override fun read(): Int {
            if (!ensureBuffer()) return -1
            return buffer[bufferOffset++].toInt() and 0xFF
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            if (len == 0) return 0
            if (!ensureBuffer()) return -1
            val available = buffer.size - bufferOffset
            val toCopy = minOf(len, available)
            System.arraycopy(buffer, bufferOffset, b, off, toCopy)
            bufferOffset += toCopy
            return toCopy
        }

        override fun close() {
            if (closed) return
            var failure: Throwable? = null
            try {
                if (!finished) {
                    val scratch = ByteArray(64 * 1024)
                    while (read(scratch) != -1) {
                    }
                }
            } catch (t: Throwable) {
                failure = t
            }
            try {
                input.close()
            } catch (t: Throwable) {
                if (failure == null) failure = t
            }
            closed = true
            if (failure != null) throw failure
        }

        private fun ensureBuffer(): Boolean {
            while (bufferOffset >= buffer.size) {
                if (finished) return false
                if (!fillNextChunk()) return false
            }
            return true
        }

        private fun fillNextChunk(): Boolean {
            if (chunkIndex >= header.totalChunks) {
                finishVerification()
                return false
            }

            val isLast = chunkIndex == header.totalChunks - 1
            val plainLength = if (isLast) {
                (header.plaintextSize - header.chunkSize.toLong() * (header.totalChunks - 1)).toInt()
            } else {
                header.chunkSize
            }
            if (plainLength < 0 || plainLength > header.chunkSize) {
                throw IOException("Invalid chunk length: $plainLength")
            }

            val iv = ByteArray(GCM_IV_SIZE)
            readFully(input, iv)
            val encrypted = ByteArray(plainLength + GCM_TAG_SIZE)
            readFully(input, encrypted)

            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, aesKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
            cipher.updateAAD(buildAad(header.raw, chunkIndex, header.totalChunks, isLast))
            val plaintext = cipher.doFinal(encrypted)
            if (plaintext.size != plainLength) throw IOException("Chunk size mismatch")

            digest.update(plaintext)
            buffer = plaintext
            bufferOffset = 0
            chunkIndex++
            return true
        }

        private fun finishVerification() {
            if (finished) return
            if (chunkIndex != header.totalChunks) throw IOException("Chunk sequence incomplete")
            val computed = digest.digest()
            if (!MessageDigest.isEqual(computed, header.plaintextSha256)) {
                throw IOException("Plaintext hash mismatch")
            }
            if (input.read() != -1) throw IOException("Unexpected trailing payload data")
            finished = true
        }
    }
}
