package org.catrobat.catroid.ui.theme

object ThemeColorMap {
    const val WHITE = 0xFFFFFFFF.toInt()
    const val BLACK = 0xFF000000.toInt()
    const val DETAILS_GREY = 0xFFE0E0E0.toInt()

    fun mapBackground(current: Int, palette: ThemePalette): Int? = when (current) {
        ThemePalette.DEFAULT_TOOLBAR -> palette.toolbar
        ThemePalette.DEFAULT_BACKGROUND -> palette.background
        ThemePalette.DEFAULT_BUTTON -> palette.button
        ThemePalette.DEFAULT_SURFACE -> palette.surface
        WHITE -> palette.surface
        else -> null
    }

    fun mapText(current: Int, palette: ThemePalette): Int? = when (current) {
        ThemePalette.DEFAULT_TEXT_PRIMARY -> palette.textPrimary
        ThemePalette.DEFAULT_TEXT_SECONDARY -> palette.textSecondary
        DETAILS_GREY -> palette.textSecondary
        else -> null
    }

    fun mapControl(current: Int, palette: ThemePalette): Int? = when (current) {
        ThemePalette.DEFAULT_BUTTON -> palette.button
        ThemePalette.DEFAULT_ACCENT -> palette.accent
        else -> null
    }

    fun isNeutralGray(color: Int): Boolean {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        return Math.abs(r - g) < 24 && Math.abs(g - b) < 24 && Math.abs(r - b) < 24
    }
}
