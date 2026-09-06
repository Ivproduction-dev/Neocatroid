package org.catrobat.catroid.collab

object SyncStream {
    const val RAW_BYTES = 300_000
    const val PAGE_DOCS = 100

    fun totalBytes(codeBytes: Long, manifest: List<ManifestEntry>): Long {
        var total = codeBytes
        for (entry in manifest) total += entry.size
        return total
    }

    data class DownloadProgress(
        val totalBytes: Long,
        var receivedBytes: Long = 0L,
        val verified: MutableMap<String, String> = LinkedHashMap(),
        var lastDocId: String? = null,
        var codeDone: Boolean = false,
        var dirPath: String = ""
    ) {
        fun fraction(): Float {
            if (totalBytes <= 0L) return 1f
            return (receivedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
        }

        fun toMap(): Map<String, Any> = mapOf(
            "totalBytes" to totalBytes,
            "receivedBytes" to receivedBytes,
            "lastDocId" to (lastDocId ?: ""),
            "codeDone" to codeDone,
            "dirPath" to dirPath,
            "verified" to verified.entries.joinToString("\n") { it.key + "|" + it.value }
        )

        companion object {
            fun fromMap(map: Map<String, Any?>?): DownloadProgress? {
                if (map == null) return null
                val total = (map["totalBytes"] as? Number)?.toLong() ?: return null
                val progress = DownloadProgress(total)
                progress.receivedBytes = (map["receivedBytes"] as? Number)?.toLong() ?: 0L
                progress.lastDocId = (map["lastDocId"] as? String)?.takeIf { it.isNotEmpty() }
                progress.codeDone = map["codeDone"] as? Boolean ?: false
                progress.dirPath = map["dirPath"] as? String ?: ""
                val raw = map["verified"] as? String ?: ""
                for (line in raw.lines()) {
                    val parts = line.split("|")
                    if (parts.size == 2 && parts[0].isNotEmpty()) {
                        progress.verified[parts[0]] = parts[1]
                    }
                }
                return progress
            }
        }
    }
}
