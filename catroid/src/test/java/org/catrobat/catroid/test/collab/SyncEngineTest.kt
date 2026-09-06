package org.catrobat.catroid.test.collab

import org.catrobat.catroid.collab.CollabRoles
import org.catrobat.catroid.collab.FileSnapshot
import org.catrobat.catroid.collab.ManifestEntry
import org.catrobat.catroid.collab.SyncChunk
import org.catrobat.catroid.collab.SyncEngine
import org.catrobat.catroid.collab.SyncMerger
import org.catrobat.catroid.collab.SyncPayload
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class FakeMerger(var result: String = "merged-xml", var conflicts: Int = 0) : SyncMerger {
    var calls = 0

    override fun mergeXml(baseXml: String?, localXml: String, remoteXml: String): Pair<String, Int> {
        calls++
        return Pair(result, conflicts)
    }
}

@RunWith(JUnit4::class)
class SyncEngineTest {

    @Test
    fun payloadRoundTrip() {
        val original = SyncPayload("u1", "Petya", 5L, "hi", 2,
            listOf(ManifestEntry("a.png", "m", 3L)))
        val restored = SyncPayload.fromMap(original.toMap())
        assertEquals(original, restored)
    }

    @Test
    fun payloadNullOnNull() {
        assertNull(SyncPayload.fromMap(null))
    }

    @Test
    fun chunkDocIdsUnique() {
        val ids = (0 until 5).map { SyncChunk("code", "", it, "x").docId() }.toSet()
        assertEquals(5, ids.size)
    }

    @Test
    fun chunkRoundTrip() {
        val original = SyncChunk("media", "a.png", 3, "zz")
        assertEquals(original, SyncChunk.fromMap(original.toMap()))
    }

    @Test
    fun shouldUploadMatrix() {
        val manifest = listOf(ManifestEntry("a.png", "m", 1L))
        assertFalse(SyncEngine.shouldUpload(CollabRoles.VIEWER, null, "xml", manifest))
        assertFalse(SyncEngine.shouldUpload(CollabRoles.EDITOR, null, null, manifest))
        assertTrue(SyncEngine.shouldUpload(CollabRoles.EDITOR, null, "xml", manifest))
        assertFalse(SyncEngine.shouldUpload(
            CollabRoles.EDITOR, FileSnapshot("xml", manifest), "xml", manifest))
        assertTrue(SyncEngine.shouldUpload(
            CollabRoles.EDITOR, FileSnapshot("xml", manifest), "xml2", manifest))
        assertTrue(SyncEngine.shouldUpload(
            CollabRoles.EDITOR, FileSnapshot("xml", emptyList()), "xml", manifest))
    }

    @Test
    fun autoApplyIsInverseOfBusy() {
        assertTrue(SyncEngine.shouldAutoApply(false))
        assertFalse(SyncEngine.shouldAutoApply(true))
    }

    @Test
    fun patchRoleGate() {
        assertFalse(SyncEngine.shouldApplyPatch(null))
        assertFalse(SyncEngine.shouldApplyPatch(CollabRoles.VIEWER))
        assertTrue(SyncEngine.shouldApplyPatch(CollabRoles.EDITOR))
        assertTrue(SyncEngine.shouldApplyPatch(CollabRoles.HOST))
    }

    @Test
    fun mergeOrTakeFastPaths() {
        val merger = FakeMerger()
        assertEquals(Pair("remote", 0), SyncEngine.mergeOrTake(merger, null, "local", "remote"))
        assertEquals(Pair("remote", 0), SyncEngine.mergeOrTake(merger, "local", "local", "remote"))
        assertEquals(0, merger.calls)
        assertEquals(Pair("merged-xml", 0), SyncEngine.mergeOrTake(merger, "base", "local", "remote"))
        assertEquals(1, merger.calls)
    }

    @Test
    fun assembleHappyPath() {
        val code = "code-xml-body"
        val bytes = byteArrayOf(10, 20, 30)
        val payload = SyncPayload("u", "n", 1L, "s", 1,
            listOf(ManifestEntry("a.png", org.catrobat.catroid.collab.SyncChunks.md5(bytes), 3L)))
        val chunks = SyncEngine.buildChunks(code, listOf(
            org.catrobat.catroid.collab.SyncChunks.MediaBytes("a.png", bytes)))
        val assembled = SyncEngine.assemble(payload, chunks)
        assertEquals(code, assembled?.codeXml)
        assertTrue(assembled?.media?.get("a.png")?.contentEquals(bytes) == true)
    }

    @Test
    fun assembleRejectsIncompleteCode() {
        val payload = SyncPayload("u", "n", 1L, "s", 3, emptyList())
        val chunks = listOf(SyncChunk("code", "", 0, "only-one"))
        assertNull(SyncEngine.assemble(payload, chunks))
    }

    @Test
    fun assembleSkipsCorruptMedia() {
        val payload = SyncPayload("u", "n", 1L, "s", 1,
            listOf(ManifestEntry("a.png", "wrongmd5", 3L)))
        val chunks = SyncEngine.buildChunks("code", listOf(
            org.catrobat.catroid.collab.SyncChunks.MediaBytes("a.png", byteArrayOf(1, 2, 3))))
        val assembled = SyncEngine.assemble(payload, chunks)
        assertEquals("code", assembled?.codeXml)
        assertTrue(assembled?.media?.isEmpty() == true)
    }

    @Test
    fun missingMediaDetection() {
        val manifest = listOf(
            ManifestEntry("a.png", "m1", 1L),
            ManifestEntry("b.png", "m2", 2L),
            ManifestEntry("c.png", "m3", 3L)
        )
        val received = mapOf("a.png" to byteArrayOf(1))
        val local = listOf(ManifestEntry("c.png", "m3", 3L))
        assertEquals(listOf("b.png"), SyncEngine.missingMedia(manifest, received, local))
    }

    @Test
    fun workTreeSyncCopiesOnlyChanged() {
        val project = createTempDir("proj")
        val tree = createTempDir("tree")
        try {
            java.io.File(project, "code.xml").writeText("v1")
            val img = java.io.File(project, "images")
            img.mkdirs()
            java.io.File(img, "a.png").writeBytes(byteArrayOf(1, 2, 3))
            val manifest = listOf(ManifestEntry("images/a.png", "x", 3L))
            SyncEngine.syncWorkTree(project, tree, manifest, "v1")
            assertEquals("v1", java.io.File(tree, "code.xml").readText())
            assertTrue(java.io.File(tree, "images/a.png").readBytes().contentEquals(byteArrayOf(1, 2, 3)))
            val stamp = java.io.File(tree, "images/a.png").lastModified()
            Thread.sleep(1100)
            SyncEngine.syncWorkTree(project, tree, manifest, "v1")
            assertEquals(stamp, java.io.File(tree, "images/a.png").lastModified())
        } finally {
            project.deleteRecursively()
            tree.deleteRecursively()
        }
    }

    @Test
    fun workTreeSyncRejectsTraversal() {
        val project = createTempDir("proj")
        val tree = createTempDir("tree")
        try {
            java.io.File(project, "code.xml").writeText("v1")
            val manifest = listOf(ManifestEntry("../evil.png", "x", 3L))
            SyncEngine.syncWorkTree(project, tree, manifest, "v1")
            assertEquals("v1", java.io.File(tree, "code.xml").readText())
            assertTrue(java.io.File(project.parentFile, "evil.png").exists().not())
        } finally {
            project.deleteRecursively()
            tree.deleteRecursively()
        }
    }

    private fun createTempDir(prefix: String): java.io.File {
        val dir = java.io.File(System.getProperty("java.io.tmpdir"), prefix + System.nanoTime())
        dir.mkdirs()
        return dir
    }
}
