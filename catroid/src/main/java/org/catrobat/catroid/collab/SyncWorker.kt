package org.catrobat.catroid.collab

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.io.asynctask.ProjectLoader
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.git.GitController
import org.catrobat.catroid.utils.git.TokenManager
import org.catrobat.catroid.utils.git.XStreamUtilGit
import java.io.File
import java.util.Collections
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class JGitOps(private val token: () -> String?) : GitOps {
    override fun commitPush(workTree: File, message: String, authorName: String, authorEmail: String): Boolean {
        val auth = token()?.takeIf { it.isNotBlank() } ?: return false
        return try {
            when (GitController(workTree).commitAndPush(message, authorName, authorEmail, auth)) {
                is org.catrobat.catroid.utils.git.GitResult.Success -> true
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
}

object SyncWorker {
    private const val TAG = "SyncWorker"

    var transport: SyncTransport = SyncTransportFirestore()
    var merger: SyncMerger = ProjectMergerAdapter()
    var gitops: GitOps = JGitOps({ token() })
    var nowProvider: () -> Long = { System.currentTimeMillis() }
    var sessionProvider: () -> SyncIdentity? = {
        if (!CollabSession.isActive || CollabSession.myUid == null) null
        else SyncIdentity(
            CollabSession.myUid!!,
            PresenceRenderer.myName,
            PresenceRenderer.myHue,
            CollabSession.myRole
        )
    }
    var describeXml: (String?, String) -> List<String> = { oldXml, newXml ->
        try {
            val old = oldXml?.let { XStreamUtilGit.fromXML(it) }
            SyncDescriber.describe(old, XStreamUtilGit.fromXML(newXml))
        } catch (e: Exception) {
            emptyList()
        }
    }
    var notifier: (String) -> Unit = { text ->
        try {
            val context = CatroidApplication.getAppContext()
            ToastUtil.showSuccess(context, text)
        } catch (e: Exception) {
            Log.w(TAG, "notify failed", e)
        }
    }

    var onStatus: ((String) -> Unit)? = null
    var onReloadRequested: ((String) -> Unit)? = null

    private val executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "CollabSync")
    }
    private val timer by lazy { Handler(Looper.getMainLooper()) }
    private val lock = Any()

    @Volatile private var running = false
    @Volatile private var sid: String? = null
    @Volatile private var hostMode = false
    @Volatile private var dirty = false
    @Volatile private var lastEdit = 0L
    @Volatile private var pendingGitPush = false
    private val pendingApplyRef = AtomicReference<String?>(null)
    @Volatile private var pendingOpen: File? = null
    @Volatile private var lastAppliedAt: Long = 0L
    private val fullStateIds = Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap<String, Boolean>())
    @Volatile private var lastBroadcastXml: String? = null
    @Volatile private var lastStateId: String? = null
    private val dlRunning = AtomicBoolean(false)
    private var skippedMediaRetryCount = 0

    private val seenPatches = Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap<String, Boolean>())
    private var patchesHandle: Any? = null
    private var statesHandle: Any? = null
    private var snapshotReqHandle: Any? = null
    private val lastSnapshotReqTimes = java.util.concurrent.ConcurrentHashMap<String, Long>()
    private val dirFilesCache = java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.ConcurrentHashMap<String, DirSyncFiles>>()
    private data class RoleRow(val role: String?, val at: Long)
    private val roleCache = java.util.concurrent.ConcurrentHashMap<String, RoleRow>()
    @Volatile private var startedAt: Long = 0L

    private val tick = object : Runnable {
        override fun run() {
            try {
                val session = sid
                if (running && session != null && dirty
                    && nowProvider() - lastEdit >= SyncLimits.IDLE_MS
                    && CollabSession.foregroundActivities > 0
                ) {
                    dirty = false
                    runSync(session)
                }
            } catch (e: Exception) {
                Log.w(TAG, "tick failed", e)
            }
            try {
                timer.postDelayed(this, SyncLimits.IDLE_MS)
            } catch (e: Exception) {
                Log.w(TAG, "reschedule failed", e)
            }
        }
    }

    fun token(): String? {
        return try {
            TokenManager.getToken(CatroidApplication.getAppContext())
        } catch (e: Exception) {
            null
        }
    }

    fun workTreeDir(): File? {
        return try {
            val id = sid ?: return null
            File(CatroidApplication.getAppContext().filesDir, "collab-git/" + id)
        } catch (e: Exception) {
            null
        }
    }

    fun files(): SyncFiles? {
        return try {
            val project = ProjectManager.getInstance().currentProject ?: return null
            val dir = project.directory ?: return null
            val id = sid ?: return null
            dirFilesCache.getOrPut(id) { java.util.concurrent.ConcurrentHashMap<String, DirSyncFiles>() }
                .getOrPut(dir.canonicalPath) { DirSyncFiles(dir, snapshotDir(id)) }
        } catch (e: Exception) {
            null
        }
    }

    private fun snapshotDir(id: String): File {
        return File(CatroidApplication.getAppContext().filesDir, "collab/" + id)
    }

    private fun boundProjectDirFile(sessionId: String): File {
        return File(snapshotDir(sessionId), "project_dir")
    }

    fun boundProjectDir(sessionId: String): File? {
        return try {
            val f = boundProjectDirFile(sessionId)
            if (!f.exists()) null
            else {
                val path = f.readText(Charsets.UTF_8).trim()
                File(path).takeIf { it.isDirectory && File(it, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME).exists() }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun saveBoundProjectDir(sessionId: String, dir: File) {
        try {
            val f = boundProjectDirFile(sessionId)
            f.parentFile?.mkdirs()
            f.writeText(dir.absolutePath, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.w(TAG, "save bound project dir failed", e)
        }
    }

    fun isCurrentProjectMatching(sessionId: String?, sessionName: String?): Boolean {
        val curProj = ProjectManager.getInstance().currentProject ?: return false
        if (sessionName.isNullOrEmpty()) return false
        if (sessionId != null) {
            val bound = boundProjectDir(sessionId)
            if (bound != null && curProj.directory?.canonicalPath == bound.canonicalPath) {
                return true
            }
        }
        return curProj.name == sessionName
    }

    fun projectDir(): File? {
        return try {
            ProjectManager.getInstance().currentProject?.directory
        } catch (e: Exception) {
            null
        }
    }

    fun markDirty() {
        dirty = true
        lastEdit = nowProvider()
    }

    fun start(sessionId: String, host: Boolean) {
        stop()
        sid = sessionId
        hostMode = host
        running = true
        seenPatches.clear()
        fullStateIds.clear()
        lastBroadcastXml = null
        lastStateId = null
        pendingApplyRef.set(null)
        dlRunning.set(false)
        skippedMediaRetryCount = 0
        dirty = true
        lastEdit = 0L
        startedAt = nowProvider()
        if (host) {
            projectDir()?.let { saveBoundProjectDir(sessionId, it) }
        }
        try {
            timer.postDelayed(tick, SyncLimits.IDLE_MS)
            if (host) {
                patchesHandle = transport.listenPatches(sessionId) { pid, payload ->
                    onPatch(pid, payload)
                }
                snapshotReqHandle = transport.listenSnapshotRequests(sessionId) { req ->
                    onSnapshotRequest(req)
                }
            }
            statesHandle = transport.listenStates(sessionId) { stid, payload ->
                onState(stid, payload)
            }
            if (!host) {
                ensureProject()
                snapshotBaseline()
            }
        } catch (e: Exception) {
            Log.w(TAG, "start failed", e)
        }
    }

    fun stop() {
        running = false
        try {
            try {
                timer.removeCallbacks(tick)
            } catch (e: Exception) {
            }
            patchesHandle?.let { transport.unlisten(it) }
            statesHandle?.let { transport.unlisten(it) }
            snapshotReqHandle?.let { transport.unlisten(it) }
        } catch (e: Exception) {
            Log.w(TAG, "stop failed", e)
        }
        patchesHandle = null
        statesHandle = null
        snapshotReqHandle = null
        lastSnapshotReqTimes.clear()
        dirFilesCache.clear()
        roleCache.clear()
        sid = null
        pendingApplyRef.set(null)
        dlRunning.set(false)
        skippedMediaRetryCount = 0
    }

    fun ensureProject() {
        val session = sid ?: return
        if (hostMode || !running) return
        executor.execute {
            try {
                val identity = sessionProvider() ?: return@execute
                val sProject = CollabSession.projectName
                if (sProject.isEmpty()) return@execute
                val curProj = ProjectManager.getInstance().currentProject

                val bound = boundProjectDir(session)
                if (bound != null) {
                    if (curProj != null && curProj.directory?.canonicalPath == bound.canonicalPath) {
                        val hasBaseline = files()?.loadSnapshot() != null
                        if (hasBaseline) return@execute
                    } else {
                        pendingOpen = bound
                        status(identity.name + ": " + appString(R.string.collab_snapshot_received))
                        try {
                            onReloadRequested?.invoke(bound.name)
                        } catch (e: Exception) {
                        }
                        return@execute
                    }
                }

                val hasMatch = isCurrentProjectMatching(session, sProject)
                val hasBaseline = files()?.loadSnapshot() != null
                if (hasMatch && hasBaseline) {
                    curProj?.directory?.let { saveBoundProjectDir(session, it) }
                    return@execute
                }
                val haveMap = buildHaveMap(session)
                transport.requestSnapshot(session, SnapshotRequest(identity.uid, identity.name, nowProvider(), haveMap)) { ok ->
                    if (ok) status(identity.name + ": " + appString(R.string.collab_snapshot_waiting))
                }
            } catch (e: Exception) {
                Log.w(TAG, "ensure project failed", e)
            }
        }
    }

    private fun appString(resId: Int): String {
        return try {
            CatroidApplication.getAppContext().getString(resId)
        } catch (e: Exception) {
            ""
        }
    }

    private fun localHasProject(name: String): Boolean {
        if (name.isEmpty()) return false
        return try {
            val root = org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
            org.catrobat.catroid.utils.FileMetaDataExtractor.getProjectNames(root).contains(name)
        } catch (e: Exception) {
            false
        }
    }

    private fun snapshotBaseline() {
        executor.execute {
            try {
                val files = files() ?: return@execute
                if (files.loadSnapshot() != null) return@execute
                val codeXml = files.readCodeXml() ?: return@execute
                files.saveSnapshot(FileSnapshot(codeXml, SyncEngine.manifestOf(files)))
            } catch (e: Exception) {
                Log.w(TAG, "baseline failed", e)
            }
        }
    }

    fun applyPendingNow(activity: Activity): Boolean {
        pendingOpen?.let { dir ->
            if (PresenceReporter.isInsideSprite()) return false
            pendingOpen = null
            openProject(activity, dir)
            return true
        }
        val pending = pendingApplyRef.getAndSet(null) ?: return false
        if (PresenceReporter.isInsideSprite()) {
            pendingApplyRef.compareAndSet(null, pending)
            return false
        }
        reloadProject(activity, pending)
        return true
    }

    private fun openProject(activity: Activity, dir: File) {
        if (activity.isFinishing || activity.isDestroyed) return
        try {
            ProjectLoader(dir, activity).setListener(object : ProjectLoader.ProjectLoadListener {
                override fun onLoadFinished(success: Boolean) {
                    if (!success) return
                    try {
                        activity.runOnUiThread {
                            if (activity.isFinishing || activity.isDestroyed) return@runOnUiThread
                            val intent = android.content.Intent(activity, ProjectActivity::class.java)
                            intent.putExtra(
                                ProjectActivity.EXTRA_FRAGMENT_POSITION,
                                ProjectActivity.FRAGMENT_SCENES
                            )
                            activity.startActivity(intent)
                            activity.finish()
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "open ui failed", e)
                    }
                }
            }).loadProjectAsync()
        } catch (e: Exception) {
            Log.w(TAG, "open failed", e)
        }
    }

    private fun status(text: String) {
        try {
            onStatus?.invoke(text)
        } catch (e: Exception) {
            Log.w(TAG, "status failed", e)
        }
    }

    private fun runSync(session: String) {
        executor.execute {
            try {
                val files = files()
                val currentProject = ProjectManager.getInstance().currentProject
                if (currentProject != null) {
                    val diskBefore = files?.readCodeXml()
                val hasPendingRemote = pendingApplyRef.get() != null

                    synchronized(currentProject) {
                        saveProjectSerial(currentProject, CatroidApplication.getAppContext())
                    }

                    if (hasPendingRemote && files != null && diskBefore != null) {
                        val localXml = files.readCodeXml()
                        if (localXml != null && localXml != diskBefore) {
                            val snapshot = files.loadSnapshot()
                            val baseXml = snapshot?.codeXml
                            val (merged, _) = merger.mergeXml(baseXml, localXml, diskBefore)
                            files.writeCodeXml(merged)
                            files.saveSnapshot(FileSnapshot(merged, SyncEngine.manifestOf(files)))
                            lastBroadcastXml = merged
                            pendingApplyRef.set(null)
                        }
                    }
                }
                if (hostMode) hostCommit(session) else guestUpload(session)
            } catch (e: Exception) {
                Log.w(TAG, "sync failed", e)
            }
        }
    }

    private fun guestUpload(session: String) {
        if (dlStid != null) return
        val identity = sessionProvider() ?: return
        val files = files() ?: return
        var snapshot = files.loadSnapshot()
        if (snapshot == null) {
            val codeXml = files.readCodeXml() ?: return
            snapshot = FileSnapshot(codeXml, SyncEngine.manifestOf(files))
            files.saveSnapshot(snapshot)
        }
        val sProject = CollabSession.projectName
        if (!isCurrentProjectMatching(session, sProject)) return
        val localName = if (isCurrentProjectMatching(session, sProject)) sProject else projectName()
        SyncFlows.guestUpload(session, identity, files, snapshot, transport,
            describeXml, nowProvider(), localName, sProject) { result ->
            if (!result.uploaded) {
                if (result.blocked == SyncFlows.BLOCKED_MISMATCH) {
                    try {
                        status(CatroidApplication.getAppContext().getString(R.string.collab_project_mismatch))
                    } catch (e: Exception) {
                    }
                }
                return@guestUpload
            }
            result.snapshotToSave?.let { (xml, entries) ->
                executor.execute {
                    files.saveSnapshot(FileSnapshot(xml, entries))
                }
            }
            var text = result.summary
            if (result.skipped.isNotEmpty()) {
                text += " (uploading media: ${result.skipped.size} left)"
                if (skippedMediaRetryCount < 5) {
                    skippedMediaRetryCount++
                    markDirty()
                } else {
                    Log.w(TAG, "skipped media upload reached max retries")
                }
            } else {
                skippedMediaRetryCount = 0
            }
            status(text)
        }
    }

    private fun projectName(): String? {
        return try {
            ProjectManager.getInstance().currentProject?.name
        } catch (e: Exception) {
            null
        }
    }

    private fun projectMissing(): Boolean {
        val sessionName = CollabSession.projectName
        if (sessionName.isEmpty()) return false
        val sid = sid ?: return projectName() != sessionName
        return !isCurrentProjectMatching(sid, sessionName)
    }

    private fun hostCommit(session: String) {
        val identity = sessionProvider() ?: return
        val files = files() ?: return
        if (!isCurrentProjectMatching(session, CollabSession.projectName)) return
        val current = files.readCodeXml() ?: return
        val manifest = SyncEngine.manifestOf(files)
        val snapshot = files.loadSnapshot()
        if (snapshot != null && snapshot.codeXml == current
            && SyncChunks.changedFiles(manifest, snapshot.media).isEmpty()
        ) {
            if (pendingGitPush) pushOnly()
            return
        }
        val lines = try {
            describeXml(snapshot?.codeXml, current)
        } catch (e: Exception) {
            emptyList()
        }
        val summary = SyncEngine.describeForCommit(lines, identity.name)
        val tree = workTreeDir() ?: return
        val dir = projectDir() ?: return
        SyncEngine.syncWorkTree(dir, tree, manifest, current)
        val pushed = gitops.commitPush(tree, summary, identity.name, identity.uid + "@neocatroid.collab")
        files.saveSnapshot(FileSnapshot(current, manifest))
        lastBroadcastXml = current
        if (!pushed) pendingGitPush = true
        broadcast(session, summary, current, manifest, files)
    }

    private fun pushOnly() {
        val identity = sessionProvider() ?: return
        val tree = workTreeDir() ?: return
        if (gitops.commitPush(tree, "sync by " + identity.name, identity.name, identity.uid + "@neocatroid.collab")) {
            pendingGitPush = false
        }
        status("sync by " + identity.name)
    }

    private fun broadcast(session: String, summary: String, codeXml: String, manifest: List<ManifestEntry>, files: SyncFiles) {
        val inline = inlineFor(manifest, files)
        val chunks = SyncEngine.buildChunks(codeXml, inline)
        val previous = lastStateId
        val now = nowProvider()
        val stid = "s_" + now
        val identity = sessionProvider() ?: return
        val payload = SyncPayload(
            fromUid = identity.uid,
            fromName = identity.name,
            at = now,
            summary = summary,
            codeChunks = SyncChunks.split(codeXml).size,
            media = manifest
        )
        transport.publishState(session, stid, payload, chunks) { ok ->
            if (ok) {
                executor.execute {
                    lastStateId = stid
                    files.saveLastState(stid)
                    if (previous != null && !fullStateIds.contains(previous)) {
                        transport.deleteState(session, previous)
                    }
                }
                status(summary)
            }
        }
    }

    private fun inlineFor(manifest: List<ManifestEntry>, files: SyncFiles): List<SyncChunks.MediaBytes> {
        val candidates = ArrayList<SyncChunks.MediaBytes>()
        for (entry in manifest) {
            if (entry.size > SyncLimits.MEDIA_INLINE_BYTES) continue
            val bytes = try {
                files.readMedia(entry.path)
            } catch (e: Exception) {
                null
            } ?: continue
            if (SyncChunks.md5(bytes) == entry.md5) {
                candidates.add(SyncChunks.MediaBytes(entry.path, bytes))
            }
        }
        return SyncChunks.planInline(candidates).included
    }

    private fun onSnapshotRequest(req: SnapshotRequest) {
        val session = sid ?: return
        if (!hostMode || !running) return
        val now = nowProvider()
        var passed = false
        lastSnapshotReqTimes.compute(req.uid) { _, prev ->
            val p = prev ?: 0L
            if (now - p < 5000L) p else { passed = true; now }
        }
        if (!passed) return
        executor.execute {
            try {
                serveSnapshot(session, req)
            } catch (e: Exception) {
                Log.w(TAG, "serve snapshot failed", e)
            }
        }
    }

    private fun serveSnapshot(session: String, req: SnapshotRequest) {
        val identity = sessionProvider() ?: return
        val files = files() ?: return
        if (!isCurrentProjectMatching(session, CollabSession.projectName)) {
            transport.deleteSnapshotRequest(session, req.uid)
            return
        }
        ProjectManager.getInstance().currentProject?.let { curProj ->
            synchronized(curProj) {
                saveProjectSerial(curProj, CatroidApplication.getAppContext())
            }
        }
        val codeXml = files.readCodeXml() ?: return
        val manifest = SyncEngine.manifestOf(files)
        val codeBytes = codeXml.toByteArray(Charsets.UTF_8).size.toLong()
        val uploadBytes = codeBytes + manifest.sumOf { e -> if (req.have[e.path] == e.md5) 0L else e.size }
        val total = codeBytes + manifest.sumOf { it.size }
        if (uploadBytes > SyncLimits.SNAPSHOT_BYTES) {
            transport.deleteSnapshotRequest(session, req.uid)
            status(appString(R.string.collab_snapshot_too_big))
            return
        }
        val now = nowProvider()
        val stid = "s_full_" + now
        val payload = SyncPayload(
            fromUid = identity.uid,
            fromName = identity.name,
            at = now,
            summary = identity.name + ": full snapshot",
            codeChunks = SyncChunks.split(codeXml).size,
            media = manifest,
            full = true
        )
        transport.putMeta(session, "states", stid, payload) { ok ->
            if (!ok) return@putMeta
            executor.execute {
                lastStateId = stid
                fullStateIds.add(stid)
            }
            status(payload.summary)
            executor.execute {
                try {
                    streamUpload(session, stid, codeXml, manifest, files, req)
                } catch (e: Exception) {
                    Log.w(TAG, "stream upload failed", e)
                }
            }
        }
    }

    private fun streamUpload(
        session: String,
        stid: String,
        codeXml: String,
        manifest: List<ManifestEntry>,
        files: SyncFiles,
        req: SnapshotRequest
    ) {
        val codeChunks = SyncChunks.split(codeXml).mapIndexed { index, part ->
            SyncChunk(SyncChunk.KIND_CODE, "", index, part)
        }
        var uploaded = codeXml.toByteArray(Charsets.UTF_8).size.toLong()
        val total = uploaded + manifest.sumOf { e -> if (req.have[e.path] == e.md5) 0L else e.size }
        status(percentOf(uploaded, total))
        transport.putChunkBatch(session, "states", stid, codeChunks) { ok ->
            if (!ok) {
                transport.deleteState(session, stid)
                return@putChunkBatch
            }
            executor.execute {
                uploadNextFile(session, stid, manifest, files, 0, uploaded, total, req)
            }
        }
    }

    private fun percentOf(done: Long, total: Long): String {
        if (total <= 0L) return "100%"
        return ((done * 100L / total).coerceIn(0L, 100L)).toString() + "%"
    }

    private fun uploadNextFile(
        session: String,
        stid: String,
        manifest: List<ManifestEntry>,
        files: SyncFiles,
        index: Int,
        uploaded: Long,
        total: Long,
        req: SnapshotRequest,
        attempt: Int = 0
    ) {
        if (index >= manifest.size) {
            transport.deleteSnapshotRequest(session, req.uid)
            status(appString(R.string.collab_snapshot_ready))
            return
        }
        val entry = manifest[index]
        if (req.have[entry.path] == entry.md5) {
            executor.execute { uploadNextFile(session, stid, manifest, files, index + 1, uploaded + entry.size, total, req) }
            return
        }
        val chunks = ArrayList<SyncChunk>()
        var offset = 0L
        var chunkIndex = 0
        while (offset < entry.size) {
            val slice = try {
                (files as? DirSyncFiles)?.readMediaSlice(entry.path, offset, SyncStream.RAW_BYTES)
                    ?: files.readMedia(entry.path)?.let { all ->
                        if (offset >= all.size) ByteArray(0)
                        else all.copyOfRange(offset.toInt(), minOf(all.size, (offset + SyncStream.RAW_BYTES).toInt()))
                    }
            } catch (e: Exception) {
                null
            } ?: break
            if (slice.isEmpty()) break
            chunks.add(SyncChunk(SyncChunk.KIND_MEDIA, entry.path, chunkIndex, SyncChunks.encode(slice)))
            offset += slice.size
            chunkIndex++
            if (chunks.size >= 12) break
        }
        if (chunks.isEmpty() && entry.size > 0) {
            uploadNextFile(session, stid, manifest, files, index + 1, uploaded, total, req)
            return
        }
        transport.putChunkBatch(session, "states", stid, chunks) { ok ->
            if (!ok) {
                if (attempt < 2) {
                    executor.execute {
                        uploadNextFile(session, stid, manifest, files, index, uploaded, total, req, attempt + 1)
                    }
                } else {
                    status(appString(R.string.collab_snapshot_interrupted))
                }
                return@putChunkBatch
            }
            val done = uploaded + chunks.sumOf { (it.data.length * 3L / 4L) }
            status(percentOf(done, total))
            executor.execute {
                try {
                    if (offset < entry.size) {
                        uploadFileRemainder(session, stid, manifest, files, index, offset, chunkIndex, done, total, req)
                    } else {
                        uploadNextFile(session, stid, manifest, files, index + 1, done, total, req)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "upload chain failed", e)
                }
            }
        }
    }

    private fun uploadFileRemainder(
        session: String,
        stid: String,
        manifest: List<ManifestEntry>,
        files: SyncFiles,
        index: Int,
        offset: Long,
        chunkIndex: Int,
        uploaded: Long,
        total: Long,
        req: SnapshotRequest,
        attempt: Int = 0
    ) {
        val entry = manifest[index]
        val chunks = ArrayList<SyncChunk>()
        var current = offset
        var currentIndex = chunkIndex
        while (current < entry.size && chunks.size < 12) {
            val slice = try {
                (files as? DirSyncFiles)?.readMediaSlice(entry.path, current, SyncStream.RAW_BYTES)
            } catch (e: Exception) {
                null
            } ?: break
            if (slice.isEmpty()) break
            chunks.add(SyncChunk(SyncChunk.KIND_MEDIA, entry.path, currentIndex, SyncChunks.encode(slice)))
            current += slice.size
            currentIndex++
        }
        if (chunks.isEmpty()) {
            executor.execute { uploadNextFile(session, stid, manifest, files, index + 1, uploaded, total, req) }
            return
        }
        transport.putChunkBatch(session, "states", stid, chunks) { ok ->
            if (!ok) {
                if (attempt < 2) {
                    executor.execute {
                        uploadFileRemainder(session, stid, manifest, files, index, offset, chunkIndex, uploaded, total, req, attempt + 1)
                    }
                } else {
                    status(appString(R.string.collab_snapshot_interrupted))
                }
                return@putChunkBatch
            }
            val done = uploaded + chunks.sumOf { (it.data.length * 3L / 4L) }
            status(percentOf(done, total))
            executor.execute {
                try {
                    if (current < entry.size) {
                        uploadFileRemainder(session, stid, manifest, files, index, current, currentIndex, done, total, req)
                    } else {
                        uploadNextFile(session, stid, manifest, files, index + 1, done, total, req)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "upload chain failed", e)
                }
            }
        }
    }

    private fun onPatch(pid: String, payload: SyncPayload) {
        val session = sid ?: return
        if (!hostMode || !running) return
        if (payload.at in 1..<startedAt) return
        if (!seenPatches.add(pid)) return
        if (payload.fromUid == sessionProvider()?.uid) return
        executor.execute {
            try {
                applyPatch(session, pid, payload)
            } catch (e: Exception) {
                Log.w(TAG, "apply patch failed", e)
            }
        }
    }

    private fun applyPatch(session: String, pid: String, payload: SyncPayload) {
        val files = files() ?: return
        ProjectManager.getInstance().currentProject?.let { curProj ->
            synchronized(curProj) {
                saveProjectSerial(curProj, CatroidApplication.getAppContext())
            }
        }
        var role: String? = roleCache[payload.fromUid]
            ?.takeIf { nowProvider() - it.at < 60000L }?.role
        if (role == null) {
            val gate = java.util.concurrent.CountDownLatch(1)
            var fetched: String? = null
            transport.memberRole(session, payload.fromUid) {
                fetched = it
                gate.countDown()
            }
            try {
                gate.await(15, java.util.concurrent.TimeUnit.SECONDS)
            } catch (e: Exception) {
            }
            role = fetched
            roleCache[payload.fromUid] = RoleRow(fetched, nowProvider())
        }
        if (role == null) {
            seenPatches.remove(pid)
            return
        }
        SyncFlows.hostApplyPatch(session, pid, payload, role, files, lastBroadcastXml ?: files.loadSnapshot()?.codeXml,
            transport, merger,
            { collection, id, cb -> transport.fetchChunks(session, collection, id, cb) },
            projectName(), CollabSession.projectName) { result ->
            if (!result.applied) return@hostApplyPatch
            val tree = workTreeDir()
            val dir = projectDir()
            if (tree != null && dir != null) {
                val manifest = files.loadSnapshot()?.media ?: emptyList()
                SyncEngine.syncWorkTree(dir, tree, manifest, files.readCodeXml().orEmpty())
                if (!gitops.commitPush(tree, result.summary, payload.fromName, payload.fromUid + "@neocatroid.collab")) {
                    pendingGitPush = true
                }
            }
            val broadcastXml = files.readCodeXml().orEmpty()
            lastBroadcastXml = broadcastXml
            broadcast(session, result.summary, broadcastXml, files.loadSnapshot()?.media ?: emptyList(), files)
            offerReload(result.summary)
            var text = result.summary
            if (result.conflicts > 0) text += " (" + result.conflicts + " conflicts)"
            status(text)
        }
    }

    private var dlStid: String? = null
    private var dlPayload: SyncPayload? = null
    private var dlDir: File? = null
    private var dlProgress: SyncStream.DownloadProgress? = null
    private var dlMemCursor: String? = null
    private val dlCodeParts = ArrayList<SyncChunk>()
    private var dlOpenRef: String? = null
    private var dlOpenStream: java.io.OutputStream? = null
    private var dlOpenDigest: java.security.MessageDigest? = null
    private var dlEmptyStreak = 0

    private fun progressFile(stid: String): File? {
        val id = sid ?: return null
        return try {
            File(snapshotDir(id), "dl_" + stid + ".json")
        } catch (e: Exception) {
            null
        }
    }

    private fun persistDlProgress(stid: String) {
        try {
            val progress = dlProgress ?: return
            val file = progressFile(stid) ?: return
            val map = progress.toMap().toMutableMap()
            map["dirPath"] = dlDir?.absolutePath ?: progress.dirPath
            val body = map.entries.joinToString("\n") { it.key + "=" + it.value.toString().replace("\n", "\\n") }
            file.writeText(body, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.w(TAG, "persist progress failed", e)
        }
    }

    private fun loadDlProgress(stid: String): SyncStream.DownloadProgress? {
        return try {
            val file = progressFile(stid) ?: return null
            if (!file.isFile) return null
            val map = LinkedHashMap<String, Any?>()
            for (line in file.readLines(Charsets.UTF_8)) {
                val index = line.indexOf("=")
                if (index > 0) map[line.substring(0, index)] = line.substring(index + 1).replace("\\n", "\n")
            }
            SyncStream.DownloadProgress.fromMap(map)
        } catch (e: Exception) {
            null
        }
    }

    private fun materializeState(session: String, stid: String, payload: SyncPayload) {
        if (!dlRunning.compareAndSet(false, true)) return
        executor.execute {
            try {
                startDownload(session, stid, payload)
            } catch (e: Exception) {
                dlRunning.set(false)
                Log.w(TAG, "materialize failed", e)
            }
        }
    }

    private fun startDownload(session: String, stid: String, payload: SyncPayload) {
        val root = org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
        val existing = try {
            org.catrobat.catroid.utils.FileMetaDataExtractor.getProjectNames(root)
        } catch (e: Exception) {
            emptyList()
        }
        val saved = loadDlProgress(stid)
        val dir: File
        val progress: SyncStream.DownloadProgress
        val total = SyncStream.totalBytes(
            payload.codeChunks.toLong() * SyncLimits.CHUNK_CHARS, payload.media)
        if (saved != null && File(saved.dirPath).isDirectory) {
            dir = File(saved.dirPath)
            progress = saved
            for (entry in payload.media) {
                val file = safeProjectFile(dir, entry.path) ?: continue
                if (!verifiedContains(progress, entry) && file.exists()) {
                    try {
                        file.delete()
                    } catch (e: Exception) {
                    }
                }
                if (entry.size == 0L && !file.exists()) {
                    try {
                        file.parentFile?.mkdirs()
                        file.writeBytes(ByteArray(0))
                        progress.verified[entry.path] = entry.md5
                    } catch (e: Exception) {
                    }
                }
            }
        } else {
            val bound = boundProjectDir(session)
            val fresh = if (bound != null && bound.isDirectory) bound else SyncFlows.materializeDir(root, CollabSession.projectName, existing) ?: run {
                dlRunning.set(false)
                return
            }
            dir = fresh
            var alreadyHaveBytes = 0L
            for (entry in payload.media) {
                val file = safeProjectFile(dir, entry.path) ?: continue
                if (entry.size == 0L) {
                    try {
                        file.parentFile?.mkdirs()
                        if (!file.exists()) file.writeBytes(ByteArray(0))
                    } catch (e: Exception) {
                    }
                } else if (file.isFile && file.length() == entry.size) {
                    try {
                        val localMd5 = computeMd5(file)
                        if (localMd5 == entry.md5) alreadyHaveBytes += entry.size
                    } catch (e: Exception) {
                    }
                }
            }
            val effectiveTotal = (total - alreadyHaveBytes).coerceAtLeast(0L)
            progress = SyncStream.DownloadProgress(effectiveTotal)
            progress.dirPath = dir.absolutePath
            for (entry in payload.media) {
                val file = safeProjectFile(dir, entry.path) ?: continue
                if (entry.size == 0L) {
                    progress.verified[entry.path] = entry.md5
                } else if (file.isFile && file.length() == entry.size) {
                    try {
                        val localMd5 = computeMd5(file)
                        if (localMd5 == entry.md5) {
                            progress.verified[entry.path] = entry.md5
                            progress.receivedBytes += entry.size
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
        dlStid = stid
        dlPayload = payload
        dlDir = dir
        dlProgress = progress
        dlMemCursor = progress.lastDocId
        dlCodeParts.clear()
        dlOpenRef = null
        dlOpenStream = null
        dlOpenDigest = null
        dlEmptyStreak = 0
        status("0%")
        fetchNextPage()
    }

    private fun verifiedContains(progress: SyncStream.DownloadProgress, entry: ManifestEntry): Boolean {
        return progress.verified[entry.path] == entry.md5
    }

    private fun safeProjectFile(dir: File, path: String): File? {
        return try {
            if (path.isEmpty() || path.startsWith("/") || path.startsWith("\\")) return null
            val file = File(dir, path)
            if (!file.canonicalPath.startsWith(dir.canonicalPath + File.separator)) null else file
        } catch (e: Exception) {
            null
        }
    }

    private fun computeMd5(file: File): String {
        val digest = java.security.MessageDigest.getInstance("MD5")
        file.inputStream().buffered(65536).use { input ->
            val buffer = ByteArray(65536)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun buildHaveMap(session: String): Map<String, String> {
        return try {
            val dir = boundProjectDir(session)
                ?: ProjectManager.getInstance().currentProject?.directory
                ?: return emptyMap()
            val base = dir.canonicalPath
            val byDir: java.util.concurrent.ConcurrentHashMap<String, DirSyncFiles> =
                dirFilesCache.getOrPut(session) {
                    java.util.concurrent.ConcurrentHashMap<String, DirSyncFiles>()
                }
            val syncFiles: DirSyncFiles =
                byDir.getOrPut(base) { DirSyncFiles(dir, snapshotDir(session)) }
            val result = LinkedHashMap<String, String>()
            dir.walkTopDown().filter { it.isFile }.forEach { file ->
                val rel = try {
                    file.canonicalPath.removePrefix(base).trimStart('/', '\\').replace('\\', '/')
                } catch (e: Exception) { return@forEach }
                val segments = rel.split("/")
                if (segments.any { it == "images" || it == "sounds" || it == "files" || it == "libs" }) {
                    try { syncFiles.md5Of(rel)?.let { result[rel] = it } } catch (e: Exception) {}
                }
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun fetchNextPage() {
        val session = sid ?: return
        val stid = dlStid ?: return
        val cursor = dlMemCursor
        transport.fetchChunksPaged(session, "states", stid, SyncStream.PAGE_DOCS, cursor) { page ->
            executor.execute {
                try {
                    handlePage(session, stid, page)
                } catch (e: Exception) {
                    Log.w(TAG, "download page failed", e)
                }
            }
        }
    }

    private fun handlePage(session: String, stid: String, page: List<SyncChunk>) {
        val progress = dlProgress ?: return
        val payload = dlPayload ?: return
        if (page.isEmpty()) {
            if (isDownloadComplete(payload, progress)) {
                finishDownload(session, stid, payload)
                return
            }
            dlEmptyStreak++
            if (dlEmptyStreak > 30) {
                abortDownload(session, stid)
                return
            }
            try {
                timer.postDelayed({ executor.execute { fetchNextPage() } }, 5000L)
            } catch (e: Exception) {
            }
            return
        }
        dlEmptyStreak = 0
        for (chunk in page) {
            processChunk(payload, progress, chunk)
            dlMemCursor = chunk.docId()
        }
        persistDlProgress(stid)
        status(percentOf(progress.receivedBytes, progress.totalBytes))
        if (isDownloadComplete(payload, progress)) {
            finishDownload(session, stid, payload)
        } else {
            fetchNextPage()
        }
    }

    private fun processChunk(
        payload: SyncPayload,
        progress: SyncStream.DownloadProgress,
        chunk: SyncChunk
    ) {
        val dir = dlDir ?: return
        if (chunk.kind == SyncChunk.KIND_CODE) {
            if (!progress.codeDone) {
                dlCodeParts.add(chunk)
                if (dlCodeParts.size >= payload.codeChunks) {
                    progress.codeDone = true
                    progress.lastDocId = chunk.docId()
                }
            }
            return
        }
        val entry = payload.media.firstOrNull { it.path == chunk.ref } ?: return
        if (verifiedContains(progress, entry)) return
        try {
            if (dlOpenRef != chunk.ref) {
                finalizeOpenFile(payload, progress)
                val file = File(dir, chunk.ref)
                val base = dir.canonicalPath
                if (!file.canonicalPath.startsWith(base + File.separator)) return
                file.parentFile?.mkdirs()
                if (file.exists()) file.delete()
                dlOpenStream = file.outputStream().buffered(65536)
                dlOpenDigest = java.security.MessageDigest.getInstance("MD5")
                dlOpenRef = chunk.ref
            }
            val bytes = SyncChunks.decode(chunk.data)
            dlOpenStream?.write(bytes)
            dlOpenDigest?.update(bytes)
            progress.receivedBytes += bytes.size
        } catch (e: Exception) {
            Log.w(TAG, "chunk write failed", e)
        }
    }

    private fun finalizeOpenFile(payload: SyncPayload, progress: SyncStream.DownloadProgress) {
        val ref = dlOpenRef
        try {
            dlOpenStream?.close()
        } catch (e: Exception) {
        }
        dlOpenStream = null
        dlOpenRef = null
        if (ref == null) return
        val dir = dlDir ?: return
        try {
            val entry = payload.media.firstOrNull { it.path == ref } ?: return
            val hex = dlOpenDigest?.digest()?.joinToString("") { "%02x".format(it) }
            val file = File(dir, ref)
            if (hex == entry.md5 && file.isFile) {
                progress.verified[ref] = entry.md5
                progress.lastDocId = dlMemCursor
            } else {
                try {
                    file.delete()
                } catch (e: Exception) {
                }
            }
            dlOpenDigest = null
        } catch (e: Exception) {
            Log.w(TAG, "finalize failed", e)
        }
    }

    private fun isDownloadComplete(payload: SyncPayload, progress: SyncStream.DownloadProgress): Boolean {
        val codeOk = progress.codeDone || dlCodeParts.size >= payload.codeChunks
        if (!codeOk) return false
        for (entry in payload.media) {
            if (!verifiedContains(progress, entry)) return false
        }
        return true
    }

    private fun finishDownload(session: String, stid: String, payload: SyncPayload) {
        val dir = dlDir ?: return
        val progress = dlProgress ?: return
        try {
            finalizeOpenFile(payload, progress)
            if (!isDownloadComplete(payload, progress)) {
                fetchNextPage()
                return
            }
            val codeXml = SyncChunks.join(
                dlCodeParts.sortedBy { it.index }.distinctBy { it.index }
            )
            File(dir, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME)
                .writeText(codeXml, Charsets.UTF_8)
            val sid = sid ?: return
            val store = DirSyncFiles(dir, snapshotDir(sid))
            val manifest = SyncEngine.manifestOf(store)
            store.saveSnapshot(FileSnapshot(codeXml, manifest))
            store.saveLastState(stid)
            saveBoundProjectDir(sid, dir)
            lastBroadcastXml = codeXml
            lastAppliedAt = payload.at
            try {
                progressFile(stid)?.delete()
            } catch (e: Exception) {
            }
            dlStid = null
            dlPayload = null
            dlDir = null
            dlProgress = null
            dlCodeParts.clear()
            dlRunning.set(false)
            pendingOpen = dir
            status(appString(R.string.collab_snapshot_received))
            try {
                onReloadRequested?.invoke(payload.summary)
            } catch (e: Exception) {
                Log.w(TAG, "reload request failed", e)
            }
        } catch (e: Exception) {
            Log.w(TAG, "finish failed", e)
        }
    }

    private fun abortDownload(session: String, stid: String) {
        try {
            dlOpenStream?.close()
        } catch (e: Exception) {
        }
        dlOpenStream = null
        dlOpenRef = null
        dlStid = null
        dlPayload = null
        dlDir = null
        dlProgress = null
        dlCodeParts.clear()
        dlRunning.set(false)
        try {
            progressFile(stid)?.delete()
        } catch (e: Exception) {
        }
        status(appString(R.string.collab_snapshot_interrupted))
        if (!running) return
        transport.requestSnapshot(session, SnapshotRequest(
            sessionProvider()?.uid.orEmpty(),
            sessionProvider()?.name.orEmpty(),
            nowProvider(),
            buildHaveMap(session)
        )) {}
    }

    private fun onState(stid: String, payload: SyncPayload) {
        val session = sid ?: return
        if (!running) return
        if (payload.fromUid == sessionProvider()?.uid) return
        if (payload.at in 1..<startedAt && !payload.full) return
        val curProj = ProjectManager.getInstance().currentProject
        val sProject = CollabSession.projectName
        val isMatch = isCurrentProjectMatching(session, sProject)
        val hasBaseline = files()?.loadSnapshot() != null
        val needsSnapshot = payload.full && (!isMatch || !hasBaseline)
        if (payload.at <= lastAppliedAt && !needsSnapshot) return
        executor.execute {
            try {
                applyState(session, stid, payload)
            } catch (e: Exception) {
                Log.w(TAG, "apply state failed", e)
            }
        }
    }

    private fun applyState(session: String, stid: String, payload: SyncPayload) {
        if (payload.full) {
            val isMatch = isCurrentProjectMatching(session, CollabSession.projectName)
            val hasBaseline = files()?.loadSnapshot() != null
            if (!isMatch || !hasBaseline) {
                materializeState(session, stid, payload)
                return
            }
        }
        if (payload.at <= lastAppliedAt) return
        val files = files() ?: return
        ProjectManager.getInstance().currentProject?.let { curProj ->
            synchronized(curProj) {
                saveProjectSerial(curProj, CatroidApplication.getAppContext())
            }
        }
        val localName = if (isCurrentProjectMatching(session, CollabSession.projectName)) CollabSession.projectName else projectName()
        SyncFlows.guestApplyState(stid, payload, files, merger,
            { collection, id, cb -> transport.fetchChunks(session, collection, id, cb) },
            localName, CollabSession.projectName) { result ->
            if (!result.applied) return@guestApplyState
            lastBroadcastXml = files.readCodeXml()
            lastAppliedAt = payload.at
            offerReload(result.summary)
            status(result.summary)
        }
    }

    private fun offerReload(summary: String) {
        try {
            if (SyncEngine.shouldAutoApply(PresenceReporter.isInsideSprite())) {
                pendingApplyRef.compareAndSet(null, summary)
                try {
                    onReloadRequested?.invoke(summary)
                } catch (e: Exception) {
                    Log.w(TAG, "reload request failed", e)
                }
            } else {
                pendingApplyRef.set(summary)
                try {
                    notifier(CatroidApplication.getAppContext().getString(R.string.collab_sync_pending, summary))
                } catch (e: Exception) {
                    Log.w(TAG, "pend notify failed", e)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "offer reload failed", e)
        }
    }

    fun isGitPending(): Boolean = pendingGitPush

    fun reloadProject(activity: Activity, summary: String) {
        if (activity.isFinishing || activity.isDestroyed) return
        try {
            val dir = ProjectManager.getInstance().currentProject?.directory ?: return
            ProjectLoader(dir, activity).setListener(object : ProjectLoader.ProjectLoadListener {
                override fun onLoadFinished(success: Boolean) {
                    if (!success) return
                    try {
                        activity.runOnUiThread {
                            if (activity.isFinishing || activity.isDestroyed) return@runOnUiThread
                            ToastUtil.showSuccess(activity, activity.getString(R.string.collab_sync_applied, summary))
                            activity.recreate()
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "reload ui failed", e)
                    }
                }
            }).loadProjectAsync()
        } catch (e: Exception) {
            Log.w(TAG, "reload failed", e)
        }
    }
}

class ProjectMergerAdapter : SyncMerger {
    companion object {
        private const val MAX_XML_CHARS = 25 * 1024 * 1024
    }

    override fun mergeXml(baseXml: String?, localXml: String, remoteXml: String): Pair<String, Int> {
        if ((baseXml?.length ?: 0) > MAX_XML_CHARS ||
            localXml.length > MAX_XML_CHARS ||
            remoteXml.length > MAX_XML_CHARS
        ) {
            Log.e("SyncMerger", "XML payload exceeds safe limit ($MAX_XML_CHARS chars), keeping localXml")
            return Pair(localXml, 0)
        }
        return try {
            val base = if (baseXml == null) {
                XStreamUtilGit.fromXML(localXml)
            } else {
                XStreamUtilGit.fromXML(baseXml)
            }
            val result = org.catrobat.catroid.utils.git.ProjectMerger().merge(
                base,
                XStreamUtilGit.fromXML(localXml),
                XStreamUtilGit.fromXML(remoteXml)
            )
            Pair(XStreamUtilGit.toXML(result.mergedProject), result.conflicts.size)
        } catch (e: Exception) {
            Log.e("SyncMerger", "XML merge failed — keeping localXml to preserve user's work", e)
            Pair(localXml, 0)
        }
    }
}
