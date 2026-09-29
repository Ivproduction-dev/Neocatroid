package org.catrobat.catroid.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ThemeColorMapTest {

    private val palette = ThemePalette(
        name = "X",
        author = null,
        toolbar = 0xFF111111.toInt(),
        background = 0xFF222222.toInt(),
        button = 0xFF333333.toInt(),
        accent = 0xFF444444.toInt(),
        surface = 0xFF555555.toInt(),
        textPrimary = 0xFF666666.toInt(),
        textSecondary = 0xFF777777.toInt()
    )

    @Test
    fun testBackgroundDefaultsMapToPaletteSlots() {
        assertEquals(palette.toolbar, ThemeColorMap.mapBackground(ThemePalette.DEFAULT_TOOLBAR, palette))
        assertEquals(palette.background, ThemeColorMap.mapBackground(ThemePalette.DEFAULT_BACKGROUND, palette))
        assertEquals(palette.button, ThemeColorMap.mapBackground(ThemePalette.DEFAULT_BUTTON, palette))
        assertEquals(palette.surface, ThemeColorMap.mapBackground(ThemePalette.DEFAULT_SURFACE, palette))
    }

    @Test
    fun testWhiteBackgroundMapsToSurface() {
        assertEquals(palette.surface, ThemeColorMap.mapBackground(ThemeColorMap.WHITE, palette))
    }

    @Test
    fun testCustomBackgroundUntouched() {
        assertNull(ThemeColorMap.mapBackground(0xFF123456.toInt(), palette))
        assertNull(ThemeColorMap.mapBackground(ThemeColorMap.BLACK, palette))
    }

    @Test
    fun testTextDefaultsMap() {
        assertEquals(palette.textPrimary, ThemeColorMap.mapText(ThemePalette.DEFAULT_TEXT_PRIMARY, palette))
        assertEquals(palette.textSecondary, ThemeColorMap.mapText(ThemePalette.DEFAULT_TEXT_SECONDARY, palette))
    }

    @Test
    fun testCustomTextUntouched() {
        assertNull(ThemeColorMap.mapText(0xFFFF0000.toInt(), palette))
    }

    @Test
    fun testControlDefaultsMap() {
        assertEquals(palette.button, ThemeColorMap.mapControl(ThemePalette.DEFAULT_BUTTON, palette))
        assertEquals(palette.accent, ThemeColorMap.mapControl(ThemePalette.DEFAULT_ACCENT, palette))
    }

    @Test
    fun testCustomControlUntouched() {
        assertNull(ThemeColorMap.mapControl(0xFF123456.toInt(), palette))
    }

    @Test
    fun testDetailsGreyMapsToTextSecondary() {
        assertEquals(palette.textSecondary, ThemeColorMap.mapText(ThemeColorMap.DETAILS_GREY, palette))
    }

    @Test
    fun testNeutralGrayDetection() {
        assertEquals(true, ThemeColorMap.isNeutralGray(0xFF5A5A5C.toInt()))
        assertEquals(true, ThemeColorMap.isNeutralGray(ThemeColorMap.WHITE))
        assertEquals(false, ThemeColorMap.isNeutralGray(0xFF2E5AAC.toInt()))
        assertEquals(false, ThemeColorMap.isNeutralGray(0xFFFF0000.toInt()))
    }
}
