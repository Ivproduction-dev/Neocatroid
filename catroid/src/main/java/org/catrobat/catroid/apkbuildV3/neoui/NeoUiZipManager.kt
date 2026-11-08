package org.catrobat.catroid.apkbuildV3.neoui

import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

data class NeoUiImportedScreen(val layout: NeoUiLayout, val bgFile: File?)

data class NeoUiImportBundle(
    val loadingScreen: NeoUiImportedScreen?,
    val compilationScreen: NeoUiImportedScreen?
)

object NeoUiZipManager {
    private const val MAX_ARCHIVE_BYTES = 48L * 1024 * 1024
    private const val MAX_LAYOUT_BYTES = 1024 * 1024
    private const val MAX_IMAGE_BYTES = 20L * 1024 * 1024
    private const val MANIFEST_ENTRY = "neoui_manifest.json"
    private const val LOADING_LAYOUT_ENTRY = "loading/layout.json"
    private const val COMPILATION_LAYOUT_ENTRY = "compilation/layout.json"
    private const val LOADING_BG_ENTRY = "loading/background.png"
    private const val COMPILATION_BG_ENTRY = "compilation/background.png"
    private const val LEGACY_LAYOUT_ENTRY = "layout.json"
    private const val LEGACY_BG_ENTRY = "assets/background.png"

    fun exportBundle(
        loadingLayout: NeoUiLayout,
        loadingBgImageFile: File?,
        compilationLayout: NeoUiLayout,
        compilationBgImageFile: File?,
        output: OutputStream,
        projectName: String = "",
        author: String = "",
        description: String = ""
    ): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US)
        val manifest = NeoUiManifest(
            neoui_version = 2,
            created_at = dateFormat.format(Date()),
            author = author,
            project_name = projectName,
            description = description
        )

        return try {
            loadingLayout.requireValid()
            compilationLayout.requireValid()
            require(validImageFile(loadingBgImageFile) && validImageFile(compilationBgImageFile))
            ZipOutputStream(BufferedOutputStream(output)).use { zip ->
                writeEntry(zip, MANIFEST_ENTRY, manifest.toJson().toString(2).toByteArray(Charsets.UTF_8))
                writeScreen(zip, LOADING_LAYOUT_ENTRY, LOADING_BG_ENTRY, loadingLayout, loadingBgImageFile)
                writeScreen(zip, COMPILATION_LAYOUT_ENTRY, COMPILATION_BG_ENTRY, compilationLayout, compilationBgImageFile)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun importZip(srcZipFile: File, workDir: File): NeoUiImportBundle? {
        if (!srcZipFile.exists() || srcZipFile.length() !in 1..MAX_ARCHIVE_BYTES) return null
        if (!workDir.exists() && !workDir.mkdirs()) return null

        return try {
            ZipFile(srcZipFile).use { zip ->
                val entries = zip.entries().asSequence().toList()
                val allowedEntries = setOf(
                    MANIFEST_ENTRY,
                    LOADING_LAYOUT_ENTRY,
                    COMPILATION_LAYOUT_ENTRY,
                    LOADING_BG_ENTRY,
                    COMPILATION_BG_ENTRY,
                    LEGACY_LAYOUT_ENTRY,
                    LEGACY_BG_ENTRY
                )
                require(entries.size <= 7)
                require(entries.map { it.name }.distinct().size == entries.size)
                require(entries.all { it.name in allowedEntries && !it.isDirectory })

                val loadingLayoutEntry = zip.getEntry(LOADING_LAYOUT_ENTRY)
                val compilationLayoutEntry = zip.getEntry(COMPILATION_LAYOUT_ENTRY)
                if (loadingLayoutEntry != null || compilationLayoutEntry != null) {
                    require(loadingLayoutEntry != null && compilationLayoutEntry != null)
                    NeoUiImportBundle(
                        loadingScreen = readScreen(zip, loadingLayoutEntry, LOADING_BG_ENTRY, File(workDir, "loading_background.png")),
                        compilationScreen = readScreen(zip, compilationLayoutEntry, COMPILATION_BG_ENTRY, File(workDir, "compilation_background.png"))
                    )
                } else {
                    val legacyLayout = zip.getEntry(LEGACY_LAYOUT_ENTRY) ?: return null
                    NeoUiImportBundle(
                        loadingScreen = readScreen(zip, legacyLayout, LEGACY_BG_ENTRY, File(workDir, "legacy_background.png")),
                        compilationScreen = null
                    )
                }
            }
        } catch (e: Exception) {
            workDir.deleteRecursively()
            null
        }
    }

    private fun writeScreen(
        zip: ZipOutputStream,
        layoutEntry: String,
        bgEntry: String,
        layout: NeoUiLayout,
        bgImageFile: File?
    ) {
        val updatedLayout = layout.copy(
            background = layout.background.copy(
                asset = if (validImageFile(bgImageFile)) bgEntry else layout.background.asset
            )
        )
        writeEntry(zip, layoutEntry, updatedLayout.toJsonString().toByteArray(Charsets.UTF_8))
        if (validImageFile(bgImageFile)) {
            zip.putNextEntry(ZipEntry(bgEntry))
            FileInputStream(bgImageFile).use { it.copyTo(zip, 8192) }
            zip.closeEntry()
        }
    }

    private fun writeEntry(zip: ZipOutputStream, name: String, bytes: ByteArray) {
        zip.putNextEntry(ZipEntry(name))
        zip.write(bytes)
        zip.closeEntry()
    }

    private fun readScreen(zip: ZipFile, layoutEntry: ZipEntry, bgEntryName: String, outBg: File): NeoUiImportedScreen {
        require(layoutEntry.size in 1..MAX_LAYOUT_BYTES.toLong())
        val layoutJson = zip.getInputStream(layoutEntry).use { readBounded(it, MAX_LAYOUT_BYTES) }.toString(Charsets.UTF_8)
        val layout = NeoUiLayout.fromJsonString(layoutJson).also { it.requireValid() }
        val bgEntry = zip.getEntry(bgEntryName)
        val importedBg = if (bgEntry == null) {
            null
        } else {
            require(bgEntry.size in 1..MAX_IMAGE_BYTES)
            BufferedInputStream(zip.getInputStream(bgEntry)).use { input ->
                BufferedOutputStream(FileOutputStream(outBg)).use { output -> copyBounded(input, output, MAX_IMAGE_BYTES) }
            }
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(outBg.absolutePath, bounds)
            require(bounds.outWidth in 1..8192 && bounds.outHeight in 1..8192)
            require(bounds.outWidth.toLong() * bounds.outHeight <= 32_000_000)
            outBg
        }
        return NeoUiImportedScreen(layout, importedBg)
    }

    private fun validImageFile(file: File?): Boolean =
        file == null || (file.isFile && file.length() in 1..MAX_IMAGE_BYTES)

    private fun copyBounded(input: InputStream, output: OutputStream, limit: Long) {
        val buffer = ByteArray(8192)
        var total = 0L
        while (true) {
            val count = input.read(buffer)
            if (count < 0) return
            total += count
            require(total <= limit)
            output.write(buffer, 0, count)
        }
    }

    private fun readBounded(input: InputStream, limit: Int): ByteArray {
        val output = ByteArrayOutputStream()
        copyBounded(input, output, limit.toLong())
        return output.toByteArray()
    }
}
