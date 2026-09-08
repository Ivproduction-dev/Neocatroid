package org.catrobat.catroid.collab

interface SyncTransport {
    fun uploadPatch(sid: String, id: String, payload: SyncPayload, chunks: List<SyncChunk>, callback: (Boolean) -> Unit)
    fun listenPatches(sid: String, callback: (String, SyncPayload) -> Unit): Any?
    fun fetchChunks(sid: String, collection: String, id: String, callback: (List<SyncChunk>) -> Unit)
    fun fetchChunksPaged(
        sid: String,
        collection: String,
        id: String,
        pageSize: Int,
        startAfterId: String?,
        callback: (List<SyncChunk>) -> Unit
    )
    fun putMeta(sid: String, collection: String, id: String, payload: SyncPayload, callback: (Boolean) -> Unit)
    fun putChunkBatch(sid: String, collection: String, id: String, chunks: List<SyncChunk>, callback: (Boolean) -> Unit)
    fun deletePatch(sid: String, id: String, callback: (Boolean) -> Unit = {})
    fun publishState(sid: String, id: String, payload: SyncPayload, chunks: List<SyncChunk>, callback: (Boolean) -> Unit)
    fun listenStates(sid: String, callback: (String, SyncPayload) -> Unit): Any?
    fun deleteState(sid: String, id: String, callback: (Boolean) -> Unit = {})
    fun unlisten(handle: Any?)
    fun memberRole(sid: String, uid: String, callback: (String?) -> Unit)
    fun requestSnapshot(sid: String, request: SnapshotRequest, callback: (Boolean) -> Unit)
    fun listenSnapshotRequests(sid: String, callback: (SnapshotRequest) -> Unit): Any?
    fun deleteSnapshotRequest(sid: String, uid: String, callback: (Boolean) -> Unit = {})
}

interface SyncMerger {
    fun mergeXml(baseXml: String?, localXml: String, remoteXml: String): Pair<String, Int>
}

interface GitOps {
    fun commitPush(workTree: java.io.File, message: String, authorName: String, authorEmail: String): Boolean
}

data class AssembledPayload(
    val codeXml: String,
    val media: Map<String, ByteArray>
)

object SyncEngine {
    fun describeForCommit(lines: List<String>, author: String): String {
        if (lines.isEmpty()) return "sync by $author"
        return author + ": " + SyncDescriber.headline(lines)
    }

    fun shouldUpload(myRole: String?, snapshot: FileSnapshot?, currentXml: String?, current: List<ManifestEntry>): Boolean {
        if (myRole == CollabRoles.VIEWER) return false
        if (currentXml == null) return false
        if (snapshot == null) return true
        if (snapshot.codeXml != currentXml) return true
        return SyncChunks.changedFiles(current, snapshot.media).isNotEmpty()
    }

    fun shouldAutoApply(busyInEditor: Boolean): Boolean = !busyInEditor

    fun shouldApplyPatch(memberRole: String?): Boolean = memberRole != null && memberRole != CollabRoles.VIEWER

    fun manifestOf(files: SyncFiles): List<ManifestEntry> {
        val out = ArrayList<ManifestEntry>()
        for (ref in files.listMedia()) {
            val md5 = try {
                files.md5Of(ref.path)
            } catch (e: Exception) {
                null
            } ?: continue
            out.add(ManifestEntry(ref.path, md5, ref.size))
        }
        return out.sortedBy { it.path }
    }

    fun buildChunks(codeXml: String, inline: List<SyncChunks.MediaBytes>): List<SyncChunk> {
        val out = ArrayList<SyncChunk>()
        SyncChunks.split(codeXml).forEachIndexed { index, part ->
            out.add(SyncChunk(SyncChunk.KIND_CODE, "", index, part))
        }
        for (media in inline) {
            SyncChunks.split(SyncChunks.encode(media.bytes)).forEachIndexed { index, part ->
                out.add(SyncChunk(SyncChunk.KIND_MEDIA, media.path, index, part))
            }
        }
        return out
    }

    fun assemble(payload: SyncPayload, chunks: List<SyncChunk>): AssembledPayload? {
        val codeParts = chunks.filter { it.kind == SyncChunk.KIND_CODE }
            .sortedBy { it.index }.distinctBy { it.index }
        if (codeParts.size < payload.codeChunks) return null
        val codeXml = SyncChunks.join(codeParts)
        if (codeXml.isEmpty()) return null
        val media = LinkedHashMap<String, ByteArray>()
        for (entry in payload.media) {
            val parts = chunks.filter { it.kind == SyncChunk.KIND_MEDIA && it.ref == entry.path }
            if (parts.isEmpty()) continue
            try {
                val bytes = SyncChunks.decode(SyncChunks.join(parts))
                if (SyncChunks.md5(bytes) == entry.md5) {
                    media[entry.path] = bytes
                }
            } catch (e: Exception) {
            }
        }
        return AssembledPayload(codeXml, media)
    }

    fun missingMedia(manifest: List<ManifestEntry>, received: Map<String, ByteArray>, local: List<ManifestEntry>): List<String> {
        val localMd5 = local.associate { it.path to it.md5 }
        val out = ArrayList<String>()
        for (entry in manifest) {
            val got = received[entry.path]
            if (got != null) continue
            if (localMd5[entry.path] == entry.md5) continue
            out.add(entry.path)
        }
        return out
    }

    fun mergeOrTake(
        merger: SyncMerger,
        snapshotXml: String?,
        localXml: String,
        remoteXml: String
    ): Pair<String, Int> {
        if (snapshotXml == null) {
            return merger.mergeXml(localXml, localXml, remoteXml)
        }
        if (localXml == snapshotXml) {
            return Pair(remoteXml, 0)
        }
        return merger.mergeXml(snapshotXml, localXml, remoteXml)
    }

    fun syncWorkTree(projectDir: java.io.File, workTree: java.io.File, media: List<ManifestEntry>, codeXml: String) {
        try {
            workTree.mkdirs()
            java.io.File(workTree, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME)
                .writeText(codeXml, Charsets.UTF_8)
            val projectBase = projectDir.canonicalPath
            val treeBase = workTree.canonicalPath
            for (entry in media) {
                val src = java.io.File(projectDir, entry.path)
                if (!src.isFile) continue
                val dst = java.io.File(workTree, entry.path)
                if (!src.canonicalPath.startsWith(projectBase + java.io.File.separator)) continue
                if (!dst.canonicalPath.startsWith(treeBase + java.io.File.separator)) continue
                if (dst.isFile && dst.length() == src.length()
                    && dst.lastModified() == src.lastModified()
                ) {
                    continue
                }
                dst.parentFile?.mkdirs()
                src.copyTo(dst, overwrite = true)
                dst.setLastModified(src.lastModified())
            }
        } catch (e: Exception) {
        }
    }
}
