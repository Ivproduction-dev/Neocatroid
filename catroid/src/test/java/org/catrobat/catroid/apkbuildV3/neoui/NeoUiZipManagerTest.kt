package org.catrobat.catroid.apkbuildV3.neoui

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.rules.TemporaryFolder
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NeoUiZipManagerTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun exportedBundleRoundTripsBothScreensAndBackgrounds() {
        val loadingLayout = NeoUiLayout.createDefault().apply {
            texts.first().content = "Loading project"
        }
        val compilationLayout = NeoUiLayout.createDefaultCompilation().apply {
            texts.first().content = "Preparing project"
        }
        val loadingBackground = createBackground("loading.png", 0xFFFF0000.toInt())
        val compilationBackground = createBackground("compilation.png", 0xFF0000FF.toInt())
        val archive = File(temporaryFolder.root, "both_screens.neoui")
        val importDirectory = File(temporaryFolder.root, "imported")

        assertTrue(
            NeoUiZipManager.exportBundle(
                loadingLayout,
                loadingBackground,
                compilationLayout,
                compilationBackground,
                FileOutputStream(archive)
            )
        )

        val imported = NeoUiZipManager.importZip(archive, importDirectory)

        assertNotNull(imported)
        assertEquals("Loading project", imported?.loadingScreen?.layout?.texts?.first()?.content)
        assertEquals("Preparing project", imported?.compilationScreen?.layout?.texts?.first()?.content)
        assertTrue(imported?.loadingScreen?.bgFile?.isFile == true)
        assertTrue(imported?.compilationScreen?.bgFile?.isFile == true)
    }

    private fun createBackground(name: String, color: Int): File {
        val file = File(temporaryFolder.root, name)
        val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(color)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        return file
    }
}
