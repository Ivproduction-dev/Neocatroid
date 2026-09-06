package org.catrobat.catroid.collab

import android.util.Log
import java.io.File

data class MediaRef(val path: String, val size: Long)

data class FileSnapshot(val codeXml: String, val media: List<ManifestEntry>)

interface SyncFiles {
    fun readCodeXml(): String?
    fun writeCodeXml(text: String)
    fun listMedia(): List<MediaRef>
    fun readMedia(path: String): ByteArray?
    fun readMediaSlice(path: String, offset: Long, length: Int): ByteArray? {
        val all = readMedia(path) ?: return null
        if (offset >= all.size) return ByteArray(0)
        return all.copyOfRange(offset.toInt(), minOf(all.size, (offset + length).toInt()))
    }
    fun md5Of(path: String): String? {
        return try {
            readMedia(path)?.let { SyncChunks.md5(it) }
        } catch (e: Exception) {
            null
        }
    }
    fun writeMedia(path: String, bytes: ByteArray)
    fun loadSnapshot(): FileSnapshot?
    fun saveSnapshot(snapshot: FileSnapshot)
    fun loadLastState(): String?
    fun saveLastState(id: String)
}

class DirSyncFiles(private val projectDir: File, private val snapshotDir: File) : SyncFiles {
    private data class HashRow(val mtime: Long, val size: Long, val md5: String)

    private val hashCache = LinkedHashMap<String, HashRow>()
    internal var hashRuns = 0

    private fun projectFile(path: String): File? {
        return try {
            val file = File(projectDir, path)
            val base = projectDir.canonicalPath
            if (!file.canonicalPath.startsWith(base + File.separator)) null else file
        } catch (e: Exception) {
            null
        }
    }
    override fun readCodeXml(): String? {
        return try {
            val file = File(projectDir, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME)
            if (!file.exists()) null else file.readText(Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    override fun writeCodeXml(text: String) {
        try {
            val dest = File(projectDir, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME)
            val tmp = File(projectDir, org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME + ".tmp")
            tmp.writeText(text, Charsets.UTF_8)
            if (!tmp.renameTo(dest)) {
                dest.delete()
                tmp.renameTo(dest)
            }
        } catch (e: Exception) {
            Log.w("DirSyncFiles", "writeCodeXml failed", e)
        }
    }

    override fun listMedia(): List<MediaRef> {
        val out = ArrayList<MediaRef>()
        try {
            val base = projectDir.canonicalPath
            projectDir.walkTopDown().filter { it.isFile }.forEach { file ->
                val rel = try {
                    file.canonicalPath.removePrefix(base).trimStart('/', '\\')
                        .replace('\\', '/')
                } catch (e: Exception) {
                    return@forEach
                }
                val segments = rel.split("/")
                if (segments.any { it == "images" || it == "sounds" || it == "files" || it == "libs" }) {
                    out.add(MediaRef(rel, file.length()))
                }
            }
        } catch (e: Exception) {
        }
        return out.sortedBy { it.path }
    }

    override fun readMedia(path: String): ByteArray? {
        return try {
            val file = projectFile(path) ?: return null
            if (!file.isFile) null else file.readBytes()
        } catch (e: Exception) {
            null
        }
    }

    override fun md5Of(path: String): String? {        return try {
            val file = projectFile(path) ?: return null
            if (!file.isFile) return null
            val mtime = file.lastModified()
            val size = file.length()
            synchronized(hashCache) {
                val cached = hashCache[path]
                if (cached != null && cached.mtime == mtime && cached.size == size) {
                    return cached.md5
                }
            }
            val digest = java.security.MessageDigest.getInstance("MD5")
            file.inputStream().buffered(65536).use { input ->
                val buffer = ByteArray(65536)
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    digest.update(buffer, 0, read)
                }
            }
            val md5 = digest.digest().joinToString("") { "%02x".format(it) }
            val mtimeAfter = file.lastModified()
            val sizeAfter = file.length()
            if (mtime == mtimeAfter && size == sizeAfter) {
                synchronized(hashCache) {
                    if (hashCache.size > 4096) hashCache.clear()
                    hashCache[path] = HashRow(mtime, size, md5)
                    hashRuns++
                }
            }
            md5
        } catch (e: Exception) {
            null
        }
    }

    override fun readMediaSlice(path: String, offset: Long, length: Int): ByteArray? {
        return try {
            val file = projectFile(path) ?: return null
            if (!file.isFile) return null
            if (offset >= file.length()) return ByteArray(0)
            java.io.RandomAccessFile(file, "r").use { raf ->
                raf.seek(offset)
                val want = minOf(length.toLong(), file.length() - offset).toInt()
                val buffer = ByteArray(want)
                var filled = 0
                while (filled < want) {
                    val read = raf.read(buffer, filled, want - filled)
                    if (read < 0) break
                    filled += read
                }
                buffer.copyOf(filled)
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun writeMedia(path: String, bytes: ByteArray) {
        try {
            val file = projectFile(path) ?: return
            file.parentFile?.mkdirs()
            file.writeBytes(bytes)
            synchronized(hashCache) {
                hashCache[path] = HashRow(file.lastModified(), file.length(), SyncChunks.md5(bytes))
            }
        } catch (e: Exception) {
        }
    }

    override fun loadSnapshot(): FileSnapshot? {
        return try {
            val xml = File(snapshotDir, "code.xml")
            val manifest = File(snapshotDir, "manifest")
            if (!xml.exists()) return null
            val entries = if (manifest.exists()) {
                manifest.readLines(Charsets.UTF_8).mapNotNull { line ->
                    val parts = line.split("|")
                    if (parts.size != 3) null
                    else ManifestEntry(parts[0], parts[1], parts[2].toLongOrNull() ?: 0L)
                }
            } else emptyList()
            FileSnapshot(xml.readText(Charsets.UTF_8), entries)
        } catch (e: Exception) {
            null
        }
    }

    override fun saveSnapshot(snapshot: FileSnapshot) {
        try {
            snapshotDir.mkdirs()
            val destXml = File(snapshotDir, "code.xml")
            val tmpXml = File(snapshotDir, "code.xml.tmp")
            tmpXml.writeText(snapshot.codeXml, Charsets.UTF_8)
            if (!tmpXml.renameTo(destXml)) {
                destXml.delete()
                tmpXml.renameTo(destXml)
            }
            val body = snapshot.media.joinToString("\n") { it.path + "|" + it.md5 + "|" + it.size }
            val destManifest = File(snapshotDir, "manifest")
            val tmpManifest = File(snapshotDir, "manifest.tmp")
            tmpManifest.writeText(if (body.isEmpty()) "" else body + "\n", Charsets.UTF_8)
            if (!tmpManifest.renameTo(destManifest)) {
                destManifest.delete()
                tmpManifest.renameTo(destManifest)
            }
        } catch (e: Exception) {
            Log.w("DirSyncFiles", "saveSnapshot failed", e)
        }
    }

    override fun loadLastState(): String? {
        return try {
            File(snapshotDir, "last_state").takeIf { it.exists() }
                ?.readText(Charsets.UTF_8)?.trim()?.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            null
        }
    }

    override fun saveLastState(id: String) {
        try {
            snapshotDir.mkdirs()
            File(snapshotDir, "last_state").writeText(id, Charsets.UTF_8)
        } catch (e: Exception) {
        }
    }
}

class MemSyncFiles : SyncFiles {
    var codeXml: String? = null
    val media = LinkedHashMap<String, ByteArray>()
    var snapshot: FileSnapshot? = null
    var lastState: String? = null

    override fun readCodeXml(): String? = codeXml
    override fun writeCodeXml(text: String) {
        codeXml = text
    }

    override fun listMedia(): List<MediaRef> =
        media.entries.sortedBy { it.key }.map { MediaRef(it.key, it.value.size.toLong()) }

    override fun readMedia(path: String): ByteArray? = media[path]
    override fun writeMedia(path: String, bytes: ByteArray) {
        media[path] = bytes
    }

    override fun loadSnapshot(): FileSnapshot? = snapshot
    override fun saveSnapshot(snapshot: FileSnapshot) {
        this.snapshot = snapshot
    }

    override fun loadLastState(): String? = lastState
    override fun saveLastState(id: String) {
        lastState = id
    }
}
