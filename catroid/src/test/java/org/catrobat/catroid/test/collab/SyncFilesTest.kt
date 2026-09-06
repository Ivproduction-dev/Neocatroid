package org.catrobat.catroid.test.collab

import org.catrobat.catroid.collab.DirSyncFiles
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class SyncFilesTest {

    @Test
    fun sliceReads() {
        val root = tempRoot("hash-slice")
        try {
            val project = File(root, "p").apply { mkdirs() }
            File(project, "sounds").mkdirs()
            File(project, "sounds/a.bin").writeBytes(ByteArray(1000) { it.toByte() })
            val files = DirSyncFiles(project, File(root, "snap"))
            val slice = files.readMediaSlice("sounds/a.bin", 100L, 50)
            assertEquals(50, slice?.size)
            assertEquals(100.toByte(), slice?.get(0))
            assertEquals(0, files.readMediaSlice("sounds/a.bin", 5000L, 50)?.size)
            assertEquals(null, files.readMediaSlice("sounds/nope.bin", 0L, 50))
            assertEquals(null, files.readMediaSlice("../evil.bin", 0L, 50))
        } finally {
            root.deleteRecursively()
        }
    }

    private fun tempRoot(prefix: String): File {
        val dir = File(System.getProperty("java.io.tmpdir"), prefix + System.nanoTime())
        dir.mkdirs()
        return dir
    }

    @Test
    fun streamingMd5MatchesKnownVector() {
        val root = tempRoot("hash-vec")
        try {
            val project = File(root, "p").apply { mkdirs() }
            File(project, "images").mkdirs()
            File(project, "images/a.bin").writeBytes("abc".toByteArray())
            val files = DirSyncFiles(project, File(root, "snap"))
            assertEquals("900150983cd24fb0d6963f7d28e17f72", files.md5Of("images/a.bin"))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun md5CacheAvoidsRehash() {
        val root = tempRoot("hash-cache")
        try {
            val project = File(root, "p").apply { mkdirs() }
            File(project, "images").mkdirs()
            val target = File(project, "images/a.bin")
            target.writeBytes(ByteArray(100000) { it.toByte() })
            val files = DirSyncFiles(project, File(root, "snap"))
            val first = files.md5Of("images/a.bin")
            assertEquals(1, files.hashRuns)
            assertEquals(first, files.md5Of("images/a.bin"))
            assertEquals(1, files.hashRuns)
            target.appendBytes(byteArrayOf(1))
            target.setLastModified(System.currentTimeMillis() + 5000)
            val second = files.md5Of("images/a.bin")
            assertEquals(2, files.hashRuns)
            assertTrue(first != second)
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun md5RejectsTraversalAndMissing() {
        val root = tempRoot("hash-evil")
        try {
            val project = File(root, "p").apply { mkdirs() }
            val files = DirSyncFiles(project, File(root, "snap"))
            assertNull(files.md5Of("../evil.bin"))
            assertNull(files.md5Of("images/nope.bin"))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun manifestCoversLargeFilesWithoutReadingThem() {
        val root = tempRoot("hash-big")
        try {
            val project = File(root, "p").apply { mkdirs() }
            File(project, "sounds").mkdirs()
            val big = File(project, "sounds/big.mp3")
            big.outputStream().buffered().use { out ->
                val chunk = ByteArray(1_000_000) { 7 }
                repeat(60) { out.write(chunk) }
            }
            val files = DirSyncFiles(project, File(root, "snap"))
            val manifest = org.catrobat.catroid.collab.SyncEngine.manifestOf(files)
            assertEquals(1, manifest.size)
            assertEquals("sounds/big.mp3", manifest[0].path)
            assertEquals(60_000_000L, manifest[0].size)
            assertEquals(32, manifest[0].md5.length)
        } finally {
            root.deleteRecursively()
        }
    }
}
