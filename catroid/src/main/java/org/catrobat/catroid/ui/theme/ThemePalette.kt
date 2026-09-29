/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.theme

import org.catrobat.catroid.R

data class ThemePalette(
    val name: String?,
    val author: String?,
    val toolbar: Int,
    val background: Int,
    val button: Int,
    val accent: Int,
    val surface: Int = DEFAULT_SURFACE,
    val textPrimary: Int = DEFAULT_TEXT_PRIMARY,
    val textSecondary: Int = DEFAULT_TEXT_SECONDARY
) {
    val isDefault: Boolean
        get() = name == null && author == null &&
            toolbar == DEFAULT_TOOLBAR &&
            background == DEFAULT_BACKGROUND &&
            button == DEFAULT_BUTTON &&
            accent == DEFAULT_ACCENT &&
            surface == DEFAULT_SURFACE &&
            textPrimary == DEFAULT_TEXT_PRIMARY &&
            textSecondary == DEFAULT_TEXT_SECONDARY

    fun toResourceOverrideMap(): Map<Int, Int> = linkedMapOf(
        R.color.toolbar_background to toolbar,
        R.color.app_background to background,
        R.color.app_background_dark to background,
        R.color.button_background to button,
        R.color.button_bottom_bar to button,
        R.color.accent to accent,
        R.color.advertising_button_background to surface,
        R.color.button_border_top to surface,
        R.color.dialog_title_and_text_view to textPrimary,
        R.color.toolbar_title to textPrimary,
        R.color.view_holder_headline to textPrimary,
        R.color.checkbox_and_radio_button_description to textPrimary,
        R.color.spinner_icon_and_inactive_elements to textPrimary,
        R.color.toolbar_icons to textPrimary,
        R.color.view_holder_item_title to textSecondary,
        R.color.view_holder_item_details to textSecondary
    )

    companion object {
        const val DEFAULT_TOOLBAR = 0xFF1C1C1E.toInt()
        const val DEFAULT_BACKGROUND = 0xFF2C2C2E.toInt()
        const val DEFAULT_BUTTON = 0xFF48484A.toInt()
        const val DEFAULT_ACCENT = 0xFFB0BEC5.toInt()
        const val DEFAULT_SURFACE = 0xFF5A5A5C.toInt()
        const val DEFAULT_TEXT_PRIMARY = 0xFFFFFFFF.toInt()
        const val DEFAULT_TEXT_SECONDARY = 0xFFB0BEC5.toInt()

        @JvmField
        val DEFAULT = ThemePalette(
            name = null,
            author = null,
            toolbar = DEFAULT_TOOLBAR,
            background = DEFAULT_BACKGROUND,
            button = DEFAULT_BUTTON,
            accent = DEFAULT_ACCENT
        )
    }
}
