package org.catrobat.catroid.test.collab

import org.catrobat.catroid.collab.ManifestEntry
import org.catrobat.catroid.collab.SyncChunks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SyncChunksTest {

    @Test
    fun splitJoinRoundTrip() {
        val text = "a".repeat(1_000_005)
        val parts = SyncChunks.split(text, 400_000)
        assertEquals(3, parts.size)
        val chunks = parts.mapIndexed { index, part ->
            org.catrobat.catroid.collab.SyncChunk("code", "", index, part)
        }
        assertEquals(text, SyncChunks.join(chunks.shuffled()))
    }

    @Test
    fun splitEmpty() {
        assertEquals(listOf(""), SyncChunks.split(""))
    }

    @Test
    fun splitExactMultiple() {
        assertEquals(listOf("ab", "cd"), SyncChunks.split("abcd", 2))
    }

    @Test
    fun base64RoundTrip() {
        val bytes = byteArrayOf(0, 1, 2, 127, -128, -1, 55)
        assertTrue(SyncChunks.decode(SyncChunks.encode(bytes)).contentEquals(bytes))
    }

    @Test
    fun md5KnownVector() {
        assertEquals("900150983cd24fb0d6963f7d28e17f72", SyncChunks.md5("abc"))
        assertEquals(32, SyncChunks.md5("").length)
    }

    @Test
    fun planInlinePicksSmallestFirst() {
        val small = SyncChunks.MediaBytes("a.png", ByteArray(100))
        val big = SyncChunks.MediaBytes("b.png", ByteArray(900))
        val plan = SyncChunks.planInline(listOf(big, small), 500L)
        assertEquals(listOf("a.png"), plan.included.map { it.path })
        assertEquals(listOf("b.png"), plan.skipped.map { it.path })
    }

    @Test
    fun planInlineEmpty() {
        val plan = SyncChunks.planInline(emptyList())
        assertTrue(plan.included.isEmpty())
        assertTrue(plan.skipped.isEmpty())
    }

    @Test
    fun changedFilesDetectsAddedAndChanged() {
        val old = listOf(
            ManifestEntry("a.png", "md5a", 10L),
            ManifestEntry("b.png", "md5b", 20L),
            ManifestEntry("gone.png", "md5g", 5L)
        )
        val current = listOf(
            ManifestEntry("a.png", "md5a", 10L),
            ManifestEntry("b.png", "md5b2", 21L),
            ManifestEntry("c.png", "md5c", 7L)
        )
        val changed = SyncChunks.changedFiles(current, old).map { it.path }.sorted()
        assertEquals(listOf("b.png", "c.png"), changed)
    }
}
