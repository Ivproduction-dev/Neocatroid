package org.catrobat.catroid.collab

import java.security.MessageDigest
import java.util.Base64

object SyncChunks {
    fun split(text: String, size: Int = SyncLimits.CHUNK_CHARS): List<String> {
        if (text.isEmpty()) return listOf("")
        val out = ArrayList<String>((text.length + size - 1) / size)
        var start = 0
        while (start < text.length) {
            val end = minOf(start + size, text.length)
            out.add(text.substring(start, end))
            start = end
        }
        return out
    }

    fun join(chunks: List<SyncChunk>): String {
        return chunks.sortedBy { it.index }.joinToString("") { it.data }
    }

    fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

    fun decode(text: String): ByteArray = Base64.getDecoder().decode(text)

    fun md5(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("MD5").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun md5(text: String): String = md5(text.toByteArray(Charsets.UTF_8))

    data class InlinePlan(val included: List<MediaBytes>, val skipped: List<MediaBytes>)

    data class MediaBytes(val path: String, val bytes: ByteArray)

    fun planInline(candidates: List<MediaBytes>, cap: Long = SyncLimits.MEDIA_INLINE_BYTES): InlinePlan {
        val sorted = candidates.sortedBy { it.bytes.size }
        val included = ArrayList<MediaBytes>()
        val skipped = ArrayList<MediaBytes>()
        var total = 0L
        for (candidate in sorted) {
            if (total + candidate.bytes.size <= cap) {
                included.add(candidate)
                total += candidate.bytes.size
            } else {
                skipped.add(candidate)
            }
        }
        return InlinePlan(included, skipped)
    }

    fun changedFiles(current: List<ManifestEntry>, snapshot: List<ManifestEntry>): List<ManifestEntry> {
        val oldByPath = snapshot.associateBy { it.path }
        return current.filter { entry ->
            val old = oldByPath[entry.path]
            old == null || old.md5 != entry.md5
        }
    }
}
