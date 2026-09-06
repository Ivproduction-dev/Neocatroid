package org.catrobat.catroid.test.collab

import org.catrobat.catroid.collab.SyncChunk
import org.catrobat.catroid.collab.SyncPayload
import org.catrobat.catroid.collab.SyncStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SyncStreamTest {

    @Test
    fun docIdsSortNumerically() {
        val ids = (0 until 12).map { SyncChunk("media", "a.png", it, "").docId() }
        assertEquals(ids.sorted(), ids)
    }

    @Test
    fun `docIds differPerFileAndKind`() {
        val code = SyncChunk("code", "", 0, "").docId()
        val media = SyncChunk("media", "a.png", 0, "").docId()
        assertTrue(code != media)
        assertTrue(code < media)
    }

    @Test
    fun totalBytesSumsManifest() {
        val manifest = listOf(
            org.catrobat.catroid.collab.ManifestEntry("a.png", "m", 100L),
            org.catrobat.catroid.collab.ManifestEntry("b.png", "m", 200L)
        )
        assertEquals(1300L, SyncStream.totalBytes(1000L, manifest))
    }

    @Test
    fun progressFractionAndRoundTrip() {
        val progress = SyncStream.DownloadProgress(1000L)
        progress.receivedBytes = 250L
        assertEquals(0.25f, progress.fraction())
        progress.verified["a.png"] = "md5a"
        progress.lastDocId = "media_x_000003"
        progress.codeDone = true
        progress.dirPath = "/tmp/proj"
        val restored = SyncStream.DownloadProgress.fromMap(progress.toMap())
        assertEquals(progress, restored)
    }

    @Test
    fun progressEmptyTotalIsComplete() {
        assertEquals(1f, SyncStream.DownloadProgress(0L).fraction())
    }

    @Test
    fun fullFlagRoundTripWithDefault() {
        val original = SyncPayload("u", "n", 1L, "s", 1, emptyList(), full = true)
        assertEquals(true, SyncPayload.fromMap(original.toMap())?.full)
        val legacy = mapOf<String, Any?>(
            "fromUid" to "u", "fromName" to "n", "at" to 1L,
            "summary" to "s", "codeChunks" to 1, "media" to emptyList<Any>()
        )
        assertEquals(false, SyncPayload.fromMap(legacy)?.full)
    }
}
