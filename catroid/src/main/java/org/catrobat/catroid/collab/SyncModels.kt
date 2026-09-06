package org.catrobat.catroid.collab

object SyncLimits {
    const val CHUNK_CHARS = 400_000
    const val MEDIA_INLINE_BYTES = 700_000L
    const val SNAPSHOT_BYTES = 2_000_000_000L
    const val IDLE_MS = 5000L
}

data class ManifestEntry(val path: String, val md5: String, val size: Long) {
    fun toMap(): Map<String, Any> = mapOf("path" to path, "md5" to md5, "size" to size)

    companion object {
        fun fromMap(map: Map<String, Any?>?): ManifestEntry? {
            if (map == null) return null
            val path = map["path"] as? String ?: return null
            return ManifestEntry(
                path = path,
                md5 = map["md5"] as? String ?: "",
                size = (map["size"] as? Number)?.toLong() ?: 0L
            )
        }
    }
}

data class SyncChunk(val kind: String, val ref: String, val index: Int, val data: String) {
    fun docId(): String = kind + "_" + ref.hashCode().toString().replace("-", "n") + "_" + index.toString().padStart(6, '0')

    fun toMap(): Map<String, Any> = mapOf(
        "kind" to kind, "ref" to ref, "index" to index, "data" to data
    )

    companion object {
        const val KIND_CODE = "code"
        const val KIND_MEDIA = "media"

        fun fromMap(map: Map<String, Any?>?): SyncChunk? {
            if (map == null) return null
            return SyncChunk(
                kind = map["kind"] as? String ?: return null,
                ref = map["ref"] as? String ?: "",
                index = (map["index"] as? Number)?.toInt() ?: 0,
                data = map["data"] as? String ?: ""
            )
        }
    }
}

data class SyncPayload(
    val fromUid: String = "",
    val fromName: String = "",
    val at: Long = 0L,
    val summary: String = "",
    val codeChunks: Int = 0,
    val media: List<ManifestEntry> = emptyList(),
    val full: Boolean = false
) {
    fun toMap(): Map<String, Any> = mapOf(
        "fromUid" to fromUid,
        "fromName" to fromName,
        "at" to at,
        "summary" to summary,
        "codeChunks" to codeChunks,
        "media" to media.map { it.toMap() },
        "full" to full
    )

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>?): SyncPayload? {
            if (map == null) return null
            val mediaRaw = map["media"] as? List<*> ?: emptyList<Any>()
            return SyncPayload(
                fromUid = map["fromUid"] as? String ?: "",
                fromName = map["fromName"] as? String ?: "",
                at = (map["at"] as? Number)?.toLong() ?: 0L,
                summary = map["summary"] as? String ?: "",
                codeChunks = (map["codeChunks"] as? Number)?.toInt() ?: 0,
                media = mediaRaw.mapNotNull { ManifestEntry.fromMap(it as? Map<String, Any?>) },
                full = map["full"] as? Boolean ?: false
            )
        }
    }
}

data class SnapshotRequest(val uid: String = "", val name: String = "", val at: Long = 0L,
    val have: Map<String, String> = emptyMap()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "uid" to uid, "name" to name, "at" to at,
        "have" to have.entries.joinToString("\n") { it.key + "|" + it.value }
    )

    companion object {
        fun fromMap(map: Map<String, Any?>?): SnapshotRequest? {
            if (map == null) return null
            val haveRaw = map["have"] as? String ?: ""
            val have = LinkedHashMap<String, String>()
            for (line in haveRaw.lines()) {
                val sep = line.indexOf('|')
                if (sep > 0) have[line.substring(0, sep)] = line.substring(sep + 1)
            }
            return SnapshotRequest(
                uid = map["uid"] as? String ?: "",
                name = map["name"] as? String ?: "",
                at = (map["at"] as? Number)?.toLong() ?: 0L,
                have = have
            )
        }
    }
}
