package org.catrobat.catroid.test.ui.theme

import org.catrobat.catroid.ui.theme.NeoThemeException
import org.catrobat.catroid.ui.theme.NeoThemeParser
import org.catrobat.catroid.ui.theme.ThemePalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NeoThemeParserTest {

    @Test
    fun oldFourKeyFileGetsDefaultsForNewSlots() {
        val palette = NeoThemeParser.parse(
            "name=Old\ntoolbar=#FF111111\nbackground=#FF222222\n" +
                "button=#FF333333\naccent=#FF444444\n"
        )
        assertEquals("Old", palette.name)
        assertEquals(0xFF111111.toInt(), palette.toolbar)
        assertEquals(ThemePalette.DEFAULT_SURFACE, palette.surface)
        assertEquals(ThemePalette.DEFAULT_TEXT_PRIMARY, palette.textPrimary)
        assertEquals(ThemePalette.DEFAULT_TEXT_SECONDARY, palette.textSecondary)
        assertFalse(palette.isDefault)
    }

    @Test
    fun allKeysRoundTrip() {
        val original = ThemePalette(
            name = "Test",
            author = "Me",
            toolbar = 0xFF111111.toInt(),
            background = 0xFF222222.toInt(),
            button = 0xFF333333.toInt(),
            accent = 0xFF444444.toInt(),
            surface = 0xFF555555.toInt(),
            textPrimary = 0xFF666666.toInt(),
            textSecondary = 0xFF777777.toInt()
        )
        val restored = NeoThemeParser.parse(NeoThemeParser.serialize(original))
        assertEquals(original, restored)
    }

    @Test
    fun defaultPaletteIsDefault() {
        assertTrue(ThemePalette.DEFAULT.isDefault)
        assertTrue(NeoThemeParser.parse("").isDefault)
        assertTrue(NeoThemeParser.parse("# comment only\n").isDefault)
    }

    @Test
    fun invalidColorThrows() {
        try {
            NeoThemeParser.parse("toolbar=zzz\n")
            fail("expected NeoThemeException")
        } catch (expected: NeoThemeException) {
        }
    }

    @Test
    fun overrideMapCoversNewSlots() {
        val map = ThemePalette.DEFAULT.toResourceOverrideMap()
        assertTrue(map.containsKey(org.catrobat.catroid.R.color.solid_white))
        assertTrue(map.containsKey(org.catrobat.catroid.R.color.view_holder_item_title))
        assertTrue(map.containsKey(org.catrobat.catroid.R.color.advertising_button_background))
        assertEquals(17, map.size)
    }
}
