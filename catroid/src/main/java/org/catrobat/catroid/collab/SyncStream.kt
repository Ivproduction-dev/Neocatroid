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
            "verified" to verified.entries.joinToString("\n") { java.net.URLEncoder.encode(it.key, "UTF-8") + "|" + it.value }
        )

        companion object {
            fun fromMap(map: Map<String, Any?>?): DownloadProgress? {
                if (map == null) return null
                val total = (map["totalBytes"] as? Number)?.toLong()
                    ?: (map["totalBytes"] as? String)?.toLongOrNull() ?: return null
                val progress = DownloadProgress(total)
                progress.receivedBytes = (map["receivedBytes"] as? Number)?.toLong()
                    ?: (map["receivedBytes"] as? String)?.toLongOrNull() ?: 0L
                progress.lastDocId = (map["lastDocId"] as? String)?.takeIf { it.isNotEmpty() }
                progress.codeDone = (map["codeDone"] as? Boolean)
                    ?: ((map["codeDone"] as? String)?.equals("true", ignoreCase = true) == true)
                progress.dirPath = map["dirPath"] as? String ?: ""
                val raw = map["verified"] as? String ?: ""
                for (line in raw.lines()) {
                    val parts = line.split("|")
                    if (parts.size == 2 && parts[0].isNotEmpty()) {
                        try {
                            progress.verified[java.net.URLDecoder.decode(parts[0], "UTF-8")] = parts[1]
                        } catch (e: Exception) {
                        }
                    }
                }
                return progress
            }
        }
    }
}
