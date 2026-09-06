package org.catrobat.catroid.test.collab

import org.catrobat.catroid.collab.CollabRoles
import org.catrobat.catroid.collab.FileSnapshot
import org.catrobat.catroid.collab.GitOps
import org.catrobat.catroid.collab.SyncIdentity
import org.catrobat.catroid.collab.ManifestEntry
import org.catrobat.catroid.collab.MemSyncFiles
import org.catrobat.catroid.collab.SnapshotRequest
import org.catrobat.catroid.collab.SyncChunk
import org.catrobat.catroid.collab.SyncFlows
import org.catrobat.catroid.collab.SyncPayload
import org.catrobat.catroid.collab.SyncTransport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

class FakeSyncTransport : SyncTransport {
    val patches = LinkedHashMap<String, Pair<SyncPayload, List<SyncChunk>>>()
    val states = LinkedHashMap<String, Pair<SyncPayload, List<SyncChunk>>>()
    val deletedPatches = ArrayList<String>()
    val deletedStates = ArrayList<String>()
    val snapshotReqs = LinkedHashMap<String, SnapshotRequest>()
    val roles = LinkedHashMap<String, String>()
    val patchListeners = ArrayList<(String, SyncPayload) -> Unit>()
    val stateListeners = ArrayList<(String, SyncPayload) -> Unit>()
    val snapshotListeners = ArrayList<(SnapshotRequest) -> Unit>()

    override fun uploadPatch(sid: String, id: String, payload: SyncPayload, chunks: List<SyncChunk>, callback: (Boolean) -> Unit) {
        patches[id] = Pair(payload, chunks)
        for (listener in patchListeners.toList()) listener(id, payload)
        callback(true)
    }

    override fun listenPatches(sid: String, callback: (String, SyncPayload) -> Unit): Any? {
        patchListeners.add(callback)
        for ((id, pair) in patches) callback(id, pair.first)
        return callback
    }

    override fun fetchChunks(sid: String, collection: String, id: String, callback: (List<SyncChunk>) -> Unit) {
        val store = if (collection == "patches") patches else states
        callback(store[id]?.second ?: emptyList())
    }

    override fun fetchChunksPaged(
        sid: String,
        collection: String,
        id: String,
        pageSize: Int,
        startAfterId: String?,
        callback: (List<SyncChunk>) -> Unit
    ) {
        val store = if (collection == "patches") patches else states
        val all = (store[id]?.second ?: emptyList()).sortedBy { it.docId() }
        val start = if (startAfterId == null) 0 else all.indexOfFirst { it.docId() > startAfterId }
            .takeIf { it >= 0 } ?: all.size
        callback(all.drop(start).take(pageSize))
    }

    override fun putMeta(sid: String, collection: String, id: String, payload: SyncPayload, callback: (Boolean) -> Unit) {
        val store = if (collection == "patches") patches else states
        val existing = store[id]?.second ?: emptyList()
        store[id] = Pair(payload, existing)
        callback(true)
    }

    override fun putChunkBatch(
        sid: String,
        collection: String,
        id: String,
        chunks: List<SyncChunk>,
        callback: (Boolean) -> Unit
    ) {
        val store = if (collection == "patches") patches else states
        val existing = store[id]
        val merged = (existing?.second ?: emptyList()) + chunks
        val payload = existing?.first ?: SyncPayload()
        store[id] = Pair(payload, merged)
        if (collection == "patches") {
            for (listener in patchListeners.toList()) listener(id, payload)
        } else {
            for (listener in stateListeners.toList()) listener(id, payload)
        }
        callback(true)
    }

    override fun deletePatch(sid: String, id: String, callback: (Boolean) -> Unit) {
        patches.remove(id)
        deletedPatches.add(id)
        callback(true)
    }

    override fun publishState(sid: String, id: String, payload: SyncPayload, chunks: List<SyncChunk>, callback: (Boolean) -> Unit) {
        states[id] = Pair(payload, chunks)
        for (listener in stateListeners.toList()) listener(id, payload)
        callback(true)
    }

    override fun listenStates(sid: String, callback: (String, SyncPayload) -> Unit): Any? {
        stateListeners.add(callback)
        for ((id, pair) in states) callback(id, pair.first)
        return callback
    }

    override fun deleteState(sid: String, id: String, callback: (Boolean) -> Unit) {
        states.remove(id)
        deletedStates.add(id)
        callback(true)
    }

    override fun unlisten(handle: Any?) {
        patchListeners.remove(handle)
        stateListeners.remove(handle)
        snapshotListeners.remove(handle)
    }

    override fun requestSnapshot(sid: String, request: SnapshotRequest, callback: (Boolean) -> Unit) {
        snapshotReqs[request.uid] = request
        for (listener in snapshotListeners.toList()) listener(request)
        callback(true)
    }

    override fun listenSnapshotRequests(sid: String, callback: (SnapshotRequest) -> Unit): Any? {
        snapshotListeners.add(callback)
        for (req in snapshotReqs.values) callback(req)
        return callback
    }

    override fun deleteSnapshotRequest(sid: String, uid: String, callback: (Boolean) -> Unit) {
        snapshotReqs.remove(uid)
        callback(true)
    }

    override fun memberRole(sid: String, uid: String, callback: (String?) -> Unit) {
        callback(roles[uid])
    }
}

class FakeGitOps(var result: Boolean = true) : GitOps {
    data class Commit(val workTree: File, val message: String, val author: String, val email: String)

    val commits = ArrayList<Commit>()

    override fun commitPush(workTree: File, message: String, authorName: String, authorEmail: String): Boolean {
        commits.add(Commit(workTree, message, authorName, authorEmail))
        return result
    }
}

@RunWith(JUnit4::class)
class SyncFlowsTest {

    private lateinit var transport: FakeSyncTransport
    private lateinit var merger: FakeMerger
    private lateinit var files: MemSyncFiles
    private val guest = SyncIdentity("uid-guest", "Anya", 20f, CollabRoles.EDITOR)

    @Before
    fun setUp() {
        transport = FakeSyncTransport()
        merger = FakeMerger()
        files = MemSyncFiles()
        transport.roles["uid-guest"] = CollabRoles.EDITOR
        transport.roles["uid-me"] = CollabRoles.EDITOR
    }

    @Test
    fun guestUploadsWhenDirty() {
        files.codeXml = "<xml>v2</xml>"
        files.media["images/a.png"] = byteArrayOf(1, 2, 3)
        var done = false
        SyncFlows.guestUpload("sid", guest, files, null, transport, { _, _ -> listOf("line") }, 100L) {
            done = it.uploaded
        }
        assertTrue(done)
        assertEquals(1, transport.patches.size)
        assertEquals("uid-guest", transport.patches.values.first().first.fromUid)
        assertEquals("<xml>v2</xml>", files.snapshot?.codeXml)
    }

    @Test
    fun guestSkipsWhenClean() {
        files.codeXml = "<xml>v1</xml>"
        val snap = FileSnapshot("<xml>v1</xml>", emptyList())
        var done = true
        SyncFlows.guestUpload("sid", guest, files, snap, transport, { _, _ -> emptyList() }, 100L) {
            done = it.uploaded
        }
        assertFalse(done)
        assertTrue(transport.patches.isEmpty())
    }

    @Test
    fun viewerNeverUploads() {
        files.codeXml = "<xml>v2</xml>"
        val viewer = guest.copy(role = CollabRoles.VIEWER)
        var done = true
        SyncFlows.guestUpload("sid", viewer, files, null, transport, { _, _ -> emptyList() }, 100L) {
            done = it.uploaded
        }
        assertFalse(done)
        assertTrue(transport.patches.isEmpty())
    }

    @Test
    fun hostIgnoresViewerPatch() {
        files.codeXml = "<xml>host</xml>"
        transport.roles["uid-guest"] = CollabRoles.VIEWER
        val payload = SyncPayload("uid-guest", "Anya", 1L, "s", 1, emptyList())
        var applied = true
        SyncFlows.hostApplyPatch("sid", "p1", payload, CollabRoles.VIEWER, files, null,
            transport, merger, { c, i, cb -> transport.fetchChunks("sid", c, i, cb) }) {
            applied = it.applied
        }
        assertFalse(applied)
        assertEquals(0, merger.calls)
        assertTrue(transport.deletedPatches.contains("p1"))
    }

    @Test
    fun hostAppliesEditorPatch() {
        files.codeXml = "<xml>host</xml>"
        merger.result = "<xml>merged</xml>"
        val payload = SyncPayload("uid-guest", "Anya", 1L, "s", 1, emptyList())
        transport.patches["p1"] = Pair(payload, listOf(SyncChunk("code", "", 0, "<xml>guest</xml>")))
        var applied = false
        SyncFlows.hostApplyPatch("sid", "p1", payload, CollabRoles.EDITOR, files, "<xml>base</xml>",
            transport, merger, { c, i, cb -> transport.fetchChunks("sid", c, i, cb) }) {
            applied = it.applied
        }
        assertTrue(applied)
        assertEquals(1, merger.calls)
        assertEquals("<xml>merged</xml>", files.codeXml)
        assertTrue(transport.deletedPatches.contains("p1"))
    }

    @Test
    fun guestFastForwardsCleanState() {
        files.codeXml = "<xml>v1</xml>"
        files.snapshot = FileSnapshot("<xml>v1</xml>", emptyList())
        val payload = SyncPayload("uid-host", "Host", 2L, "s", 1, emptyList())
        transport.states["s1"] = Pair(payload, listOf(SyncChunk("code", "", 0, "<xml>v2</xml>")))
        var applied = false
        SyncFlows.guestApplyState("s1", payload, files, merger,
            { c, i, cb -> transport.fetchChunks("sid", c, i, cb) }) {
            applied = it.applied
        }
        assertTrue(applied)
        assertEquals("<xml>v2</xml>", files.codeXml)
        assertEquals("s1", files.lastState)
        assertEquals(0, merger.calls)
    }

    @Test
    fun guestMergesDirtyState() {
        files.codeXml = "<xml>mine</xml>"
        files.snapshot = FileSnapshot("<xml>base</xml>", emptyList())
        merger.result = "<xml>merged</xml>"
        val payload = SyncPayload("uid-host", "Host", 2L, "s", 1, emptyList())
        transport.states["s1"] = Pair(payload, listOf(SyncChunk("code", "", 0, "<xml>theirs</xml>")))
        var applied = false
        SyncFlows.guestApplyState("s1", payload, files, merger,
            { c, i, cb -> transport.fetchChunks("sid", c, i, cb) }) {
            applied = it.applied
        }
        assertTrue(applied)
        assertEquals(1, merger.calls)
        assertEquals("<xml>merged</xml>", files.codeXml)
    }

    @Test
    fun guestSkipsKnownState() {
        files.lastState = "s1"
        var applied = true
        SyncFlows.guestApplyState("s1", SyncPayload(), files, merger,
            { _, _, cb -> cb(emptyList()) }) {
            applied = it.applied
        }
        assertFalse(applied)
    }

    @Test
    fun guestUploadRoundTripThroughTransport() {
        files.codeXml = "<xml>v2</xml>"
        files.media["sounds/a.mp3"] = byteArrayOf(9, 9)
        SyncFlows.guestUpload("sid", guest, files, FileSnapshot("<xml>v1</xml>", emptyList()),
            transport, { _, _ -> listOf("x") }, 100L) {}
        val (payload, chunks) = transport.patches.values.first()
        assertEquals(1, payload.codeChunks)
        assertEquals(1, payload.media.size)
        assertEquals("sounds/a.mp3", payload.media[0].path)
        assertTrue(chunks.size >= 2)
    }

    @Test
    fun uploadBlockedOnProjectMismatch() {
        files.codeXml = "<xml>v2</xml>"
        var blocked = ""
        SyncFlows.guestUpload("sid", guest, files, null, transport, { _, _ -> emptyList() },
            100L, localProject = "Other", sessionProject = "Game") {
            blocked = it.blocked
            assertFalse(it.uploaded)
        }
        assertEquals(SyncFlows.BLOCKED_MISMATCH, blocked)
        assertTrue(transport.patches.isEmpty())
    }

    @Test
    fun applyBlockedOnProjectMismatch() {
        files.codeXml = "<xml>v1</xml>"
        var blocked = ""
        SyncFlows.guestApplyState("s9", SyncPayload(), files, merger,
            { _, _, cb -> cb(emptyList()) }, localProject = "Other", sessionProject = "Game") {
            blocked = it.blocked
            assertFalse(it.applied)
        }
        assertEquals(SyncFlows.BLOCKED_MISMATCH, blocked)
    }

    @Test
    fun snapshotRequestRoundTrip() {
        var seen = 0
        transport.listenSnapshotRequests("sid") { seen++ }
        transport.requestSnapshot("sid", SnapshotRequest("uid-guest", "Anya", 1L)) {}
        assertEquals(1, seen)
        transport.deleteSnapshotRequest("sid", "uid-guest") {}
        assertTrue(transport.snapshotReqs.isEmpty())
    }

    @Test
    fun snapshotRequestModelRoundTrip() {
        val original = SnapshotRequest("u", "n", 7L)
        assertEquals(original, SnapshotRequest.fromMap(original.toMap()))
    }

    @Test
    fun materializeWritesProject() {
        val root = tempDir("mat-root")
        try {
            val media = mapOf("images/a.png" to byteArrayOf(1, 2, 3))
            val dir = SyncFlows.materialize(root, "Game", listOf("Other"), "<xml/>", media)
            assertTrue(dir != null)
            assertEquals("Game", dir!!.name)
            assertEquals("<xml/>", File(dir, "code.xml").readText())
            assertTrue(File(dir, "images/a.png").readBytes().contentEquals(byteArrayOf(1, 2, 3)))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun materializeAvoidsNameCollision() {
        val root = tempDir("mat-collide")
        try {
            File(root, "Game").mkdirs()
            val dir = SyncFlows.materialize(root, "Game", listOf("Game"), "<xml/>", emptyMap())
            assertTrue(dir != null && dir!!.name != "Game")
            assertTrue(File(dir, "code.xml").isFile)
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun materializeRejectsPathTraversal() {
        val root = tempDir("mat-evil")
        try {
            val dir = SyncFlows.materialize(
                root, "Game", emptyList(), "<xml/>",
                mapOf("../evil.png" to byteArrayOf(9)))
            assertTrue(dir != null)
            assertFalse(File(root, "evil.png").exists())
            assertTrue(File(dir!!, "code.xml").isFile)
        } finally {
            root.deleteRecursively()
        }
    }

    private fun tempDir(prefix: String): File {
        val dir = File(System.getProperty("java.io.tmpdir"), prefix + System.nanoTime())
        dir.mkdirs()
        return dir
    }
}
