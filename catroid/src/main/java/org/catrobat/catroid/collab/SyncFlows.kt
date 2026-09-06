package org.catrobat.catroid.collab

import android.util.Log
import java.io.File

data class SyncIdentity(
    val uid: String,
    val name: String,
    val hue: Float,
    val role: String
)

data class GuestUploadResult(
    val uploaded: Boolean,
    val summary: String,
    val skipped: List<String>,
    val blocked: String = "",
    val snapshotToSave: Pair<String, List<ManifestEntry>>? = null
)

data class HostApplyResult(
    val applied: Boolean,
    val summary: String,
    val conflicts: Int,
    val missingMedia: List<String>,
    val gitOk: Boolean,
    val blocked: String = ""
)

data class GuestApplyResult(val applied: Boolean, val summary: String, val missingMedia: List<String>, val blocked: String = "")

object SyncFlows {
    const val BLOCKED_MISMATCH = "mismatch"
    const val BLOCKED_VIEWER = "viewer"

    fun projectMismatch(localProject: String?, sessionProject: String?): Boolean {
        return !sessionProject.isNullOrEmpty() && localProject != sessionProject
    }

    fun materializeDir(rootDir: File, projectName: String, existingNames: List<String>): File? {
        return try {
            val unique = org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider()
                .getUniqueName(projectName, existingNames)
            val dir = File(rootDir, org.catrobat.catroid.utils.FileMetaDataExtractor
                .encodeSpecialCharsForFileSystem(unique))
            if (dir.exists() && File(dir, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME).exists()) {
                return null
            }
            dir.mkdirs()
            if (!dir.isDirectory) null else dir
        } catch (e: Exception) {
            null
        }
    }

    fun materialize(
        rootDir: File,
        projectName: String,
        existingNames: List<String>,
        codeXml: String,
        media: Map<String, ByteArray>
    ): File? {
        return try {
            val dir = materializeDir(rootDir, projectName, existingNames) ?: return null
            if (File(dir, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME).exists()) {
                return null
            }
            dir.mkdirs()
            File(dir, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME)
                .writeText(codeXml, Charsets.UTF_8)
            val base = dir.canonicalPath
            for ((path, bytes) in media) {
                val file = File(dir, path)
                if (!file.canonicalPath.startsWith(base + File.separator)) continue
                file.parentFile?.mkdirs()
                file.writeBytes(bytes)
            }
            if (!File(dir, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME).isFile) {
                dir.deleteRecursively()
                return null
            }
            dir
        } catch (e: Exception) {
            null
        }
    }

    fun guestUpload(
        sid: String,
        identity: SyncIdentity,
        files: SyncFiles,
        snapshot: FileSnapshot?,
        transport: SyncTransport,
        describe: (String?, String) -> List<String>,
        now: Long,
        localProject: String? = null,
        sessionProject: String? = null,
        onDone: (GuestUploadResult) -> Unit
    ) {
        if (projectMismatch(localProject, sessionProject)) {
            onDone(GuestUploadResult(false, "", emptyList(), BLOCKED_MISMATCH))
            return
        }
        if (identity.role == CollabRoles.VIEWER) {
            onDone(GuestUploadResult(false, "", emptyList(), BLOCKED_VIEWER))
            return
        }
        val current = files.readCodeXml()
        if (current == null) {
            onDone(GuestUploadResult(false, "", emptyList()))
            return
        }
        val manifest = SyncEngine.manifestOf(files)
        if (!SyncEngine.shouldUpload(identity.role, snapshot, current, manifest)) {
            onDone(GuestUploadResult(false, "", emptyList()))
            return
        }
        val lines = try {
            describe(snapshot?.codeXml, current)
        } catch (e: Exception) {
            emptyList()
        }
        val changed = SyncChunks.changedFiles(manifest, snapshot?.media ?: emptyList())
        val candidates = ArrayList<SyncChunks.MediaBytes>()
        for (entry in changed) {
            val bytes = files.readMedia(entry.path) ?: continue
            if (SyncChunks.md5(bytes) == entry.md5) {
                candidates.add(SyncChunks.MediaBytes(entry.path, bytes))
            }
        }
        val plan = SyncChunks.planInline(candidates)
        val chunks = SyncEngine.buildChunks(current, plan.included)
        val summary = SyncEngine.describeForCommit(lines, identity.name)
        val payload = SyncPayload(
            fromUid = identity.uid,
            fromName = identity.name,
            at = now,
            summary = summary,
            codeChunks = SyncChunks.split(current).size,
            media = manifest
        )
        transport.uploadPatch(sid, "p_" + identity.uid + "_" + now, payload, chunks) { ok ->
            if (ok) {
                val includedPaths = plan.included.map { it.path }.toSet()
                val changedPaths = changed.map { it.path }.toSet()
                val keptOld = (snapshot?.media ?: emptyList()).filter { it.path !in changedPaths }
                val newlySynced = manifest.filter { it.path in includedPaths }
                val newSnapshotEntries = keptOld + newlySynced
                onDone(GuestUploadResult(true, summary, plan.skipped.map { it.path },
                    snapshotToSave = Pair(current, newSnapshotEntries)))
            } else {
                onDone(GuestUploadResult(false, summary, plan.skipped.map { it.path }))
            }
        }
    }

    fun hostApplyPatch(
        sid: String,
        pid: String,
        payload: SyncPayload,
        memberRole: String?,
        files: SyncFiles,
        baseXml: String?,
        transport: SyncTransport,
        merger: SyncMerger,
        fetch: (String, String, (List<SyncChunk>) -> Unit) -> Unit,
        localProject: String? = null,
        sessionProject: String? = null,
        onDone: (HostApplyResult) -> Unit
    ) {
        if (projectMismatch(localProject, sessionProject)) {
            onDone(HostApplyResult(false, "", 0, emptyList(), gitOk = true, blocked = BLOCKED_MISMATCH))
            return
        }
        if (!SyncEngine.shouldApplyPatch(memberRole)) {
            transport.deletePatch(sid, pid)
            onDone(HostApplyResult(false, "", 0, emptyList(), gitOk = true))
            return
        }
        fetch("patches", pid) { chunks ->
            val remote = SyncEngine.assemble(payload, chunks)
            if (remote == null) {
                transport.deletePatch(sid, pid)
                onDone(HostApplyResult(false, "", 0, emptyList(), gitOk = true))
                return@fetch
            }
            val local = files.readCodeXml()
            if (local == null) {
                transport.deletePatch(sid, pid)
                onDone(HostApplyResult(false, "", 0, emptyList(), gitOk = true))
                return@fetch
            }
            val (merged, conflicts) = try {
                SyncEngine.mergeOrTake(merger, baseXml, local, remote.codeXml)
            } catch (e: Exception) {
                Pair(local, 0)
            }
            files.writeCodeXml(merged)
            val written = writeMediaVerified(files, remote.media)
            val manifest = mergeManifest(files, written)
            files.saveSnapshot(FileSnapshot(merged, manifest))
            transport.deletePatch(sid, pid)
            onDone(HostApplyResult(true, payload.summary, conflicts, missingOf(payload.media, written, manifest), gitOk = true))
        }
    }

    fun guestApplyState(
        stid: String,
        payload: SyncPayload,
        files: SyncFiles,
        merger: SyncMerger,
        fetch: (String, String, (List<SyncChunk>) -> Unit) -> Unit,
        localProject: String? = null,
        sessionProject: String? = null,
        onDone: (GuestApplyResult) -> Unit
    ) {
        if (projectMismatch(localProject, sessionProject)) {
            onDone(GuestApplyResult(false, "", emptyList(), BLOCKED_MISMATCH))
            return
        }
        if (files.loadLastState() == stid) {
            onDone(GuestApplyResult(false, "", emptyList()))
            return
        }
        fetch("states", stid) { chunks ->
            val remote = SyncEngine.assemble(payload, chunks)
            if (remote == null) {
                onDone(GuestApplyResult(false, "", emptyList()))
                return@fetch
            }
            val local = files.readCodeXml()
            if (local == null) {
                onDone(GuestApplyResult(false, "", emptyList()))
                return@fetch
            }
            val snapshot = files.loadSnapshot()
            val (merged, _) = try {
                SyncEngine.mergeOrTake(merger, snapshot?.codeXml, local, remote.codeXml)
            } catch (e: Exception) {
                Pair(local, 0)
            }
            files.writeCodeXml(merged)
            val written = writeMediaVerified(files, remote.media)
            val manifest = mergeManifest(files, written)
            files.saveSnapshot(FileSnapshot(merged, manifest))
            files.saveLastState(stid)
            var summary = payload.summary
            val missing = missingOf(payload.media, written, manifest)
            if (missing.isNotEmpty()) summary += " (missing files: " + missing.size + ")"
            onDone(GuestApplyResult(true, summary, missing))
        }
    }

    fun writeMediaVerified(files: SyncFiles, media: Map<String, ByteArray>): Map<String, ByteArray> {
        val out = LinkedHashMap<String, ByteArray>()
        for ((path, bytes) in media) {
            try {
                files.writeMedia(path, bytes)
                out[path] = bytes
            } catch (e: Exception) {
                Log.w("SyncFlows", "write media failed: " + path, e)
            }
        }
        return out
    }

    fun mergeManifest(files: SyncFiles, written: Map<String, ByteArray>): List<ManifestEntry> {
        val manifest = SyncEngine.manifestOf(files).toMutableList()
        for ((path, bytes) in written) {
            manifest.removeAll { it.path == path }
            manifest.add(ManifestEntry(path, SyncChunks.md5(bytes), bytes.size.toLong()))
        }
        return manifest.sortedBy { it.path }
    }

    fun missingOf(
        manifest: List<ManifestEntry>,
        received: Map<String, ByteArray>,
        local: List<ManifestEntry>
    ): List<String> = SyncEngine.missingMedia(manifest, received, local)
}
