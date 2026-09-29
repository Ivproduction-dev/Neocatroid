package org.catrobat.catroid.ui.theme

import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

object ThemeApplier {

    private val tagBackground = org.catrobat.catroid.R.id.theme_saved_background
    private val tagText = org.catrobat.catroid.R.id.theme_saved_text
    private val tagHint = org.catrobat.catroid.R.id.theme_saved_hint
    private val tagCard = org.catrobat.catroid.R.id.theme_saved_card
    private val tagTint = org.catrobat.catroid.R.id.theme_saved_tint
    private val tagNavText = org.catrobat.catroid.R.id.theme_saved_nav_text
    private val tagNavIcon = org.catrobat.catroid.R.id.theme_saved_nav_icon
    private val tagBgColor = org.catrobat.catroid.R.id.theme_saved_bg_color
    private val tagRoot = org.catrobat.catroid.R.id.theme_saved_root
    private val nullBackground = Any()

    fun apply(activity: Activity) {
        val root = activity.window?.decorView ?: return
        applyToRoot(root)
    }

    fun apply(dialog: Dialog) {
        val root = dialog.window?.decorView ?: return
        applyToRoot(root)
    }

    private fun applyToRoot(root: View) {
        if (ThemeManager.isDefaultSelected) {
            restore(root)
            return
        }
        val palette = ThemeManager.currentPalette
        if (root.getTag(tagRoot) == null) {
            root.setTag(tagRoot, root.background ?: nullBackground)
        }
        root.setBackgroundColor(palette.background)
        if (root is ViewGroup) {
            for (index in 0 until root.childCount) {
                applyToView(root.getChildAt(index), palette)
            }
        }
    }

    private fun applyToView(view: View, palette: ThemePalette) {
        when (view) {
            is Toolbar -> {
                saveBackground(view)
                view.setBackgroundColor(palette.toolbar)
                view.setTitleTextColor(palette.textPrimary)
                view.setSubtitleTextColor(palette.textSecondary)
            }
            is BottomNavigationView -> {
                remapBackground(view, palette)
                view.itemTextColor = navColors(view.getTag(tagNavText) as? ColorStateList
                    ?: view.itemTextColor, palette, tagNavText, view)
                view.itemIconTintList = navColors(view.getTag(tagNavIcon) as? ColorStateList
                    ?: view.itemIconTintList, palette, tagNavIcon, view)
            }
            is NavigationView -> {
                remapBackground(view, palette)
                view.itemTextColor = navColors(view.getTag(tagNavText) as? ColorStateList
                    ?: view.itemTextColor, palette, tagNavText, view)
                view.itemIconTintList = navColors(view.getTag(tagNavIcon) as? ColorStateList
                    ?: view.itemIconTintList, palette, tagNavIcon, view)
            }
            is MaterialCardView -> {
                val saved = view.getTag(tagCard) as? ColorStateList
                val source = saved ?: view.cardBackgroundColor
                if (saved == null && source != null) {
                    view.setTag(tagCard, source)
                }
                val current = source?.defaultColor
                if (current != null) {
                    val mapped = ThemeColorMap.mapBackground(current, palette)
                        ?: if (current != ThemeColorMap.BLACK
                            && ThemeColorMap.isNeutralGray(current)) {
                            palette.surface
                        } else {
                            null
                        }
                    if (mapped != null) {
                        view.setCardBackgroundColor(ColorStateList.valueOf(mapped))
                    }
                }
            }
            is FloatingActionButton -> {
                remapTint(view, view.backgroundTintList, palette) {
                    view.backgroundTintList = it
                }
            }
            is MaterialButton -> {
                remapTint(view, view.backgroundTintList, palette) {
                    view.backgroundTintList = it
                } || remapBackground(view, palette)
                remapText(view, palette)
                if (view is EditText) {
                    remapHint(view, palette)
                }
            }
            is android.widget.Button -> {
                remapBackground(view, palette)
                remapText(view, palette)
            }
            is EditText -> {
                remapBackground(view, palette)
                remapText(view, palette)
                remapHint(view, palette)
            }
            is TextView -> {
                remapText(view, palette)
            }
            else -> {
                remapBackground(view, palette)
            }
        }
        val tag = view.tag
        if (tag is String) {
            saveBackground(view)
            when (tag) {
                "app_background" -> view.setBackgroundColor(palette.background)
                "toolbar_background" -> view.setBackgroundColor(palette.toolbar)
                "button_background" -> view.setBackgroundColor(palette.button)
            }
        }
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                applyToView(view.getChildAt(index), palette)
            }
        }
    }

    private fun remapBackground(view: View, palette: ThemePalette): Boolean {
        val background = view.background ?: return false
        if (background is ColorDrawable) {
            val saved = view.getTag(tagBackground) as? Drawable
            val source = (saved as? ColorDrawable)?.color ?: background.color
            if (saved == null) {
                view.setTag(tagBackground, background)
            }
            val mapped = ThemeColorMap.mapBackground(source, palette) ?: return false
            if (background.color != mapped) {
                view.setBackgroundColor(mapped)
            }
            return true
        }
        if (background is GradientDrawable) {
            val current = background.color?.defaultColor ?: return false
            val saved = view.getTag(tagBgColor) as? Int
            val source = saved ?: current
            if (saved == null) {
                view.setTag(tagBackground, background)
                view.setTag(tagBgColor, source)
            }
            val mapped = ThemeColorMap.mapBackground(source, palette) ?: return false
            if (current != mapped) {
                background.mutate()
                background.setColor(mapped)
            }
            return true
        }
        return false
    }

    private fun remapText(view: TextView, palette: ThemePalette) {
        val saved = view.getTag(tagText) as? Int
        val source = saved ?: view.currentTextColor
        if (saved == null) {
            view.setTag(tagText, source)
        }
        ThemeColorMap.mapText(source, palette)?.let { view.setTextColor(it) }
    }

    private fun remapHint(view: EditText, palette: ThemePalette) {
        val colors = view.hintTextColors ?: return
        val saved = view.getTag(tagHint) as? ColorStateList
        val source = saved ?: colors
        if (saved == null) {
            view.setTag(tagHint, colors)
        }
        ThemeColorMap.mapText(source.defaultColor, palette)?.let { view.setHintTextColor(it) }
    }

    private inline fun remapTint(
        view: View,
        current: ColorStateList?,
        palette: ThemePalette,
        setter: (ColorStateList) -> Unit
    ): Boolean {
        if (current == null) {
            return false
        }
        val saved = view.getTag(tagTint) as? ColorStateList
        val source = saved ?: current
        if (saved == null) {
            view.setTag(tagTint, current)
        }
        val mapped = ThemeColorMap.mapControl(source.defaultColor, palette) ?: return false
        setter(ColorStateList.valueOf(mapped))
        return true
    }

    private fun navColors(
        current: ColorStateList?,
        palette: ThemePalette,
        key: Int,
        view: View
    ): ColorStateList {
        if (view.getTag(key) == null && current != null) {
            view.setTag(key, current)
        }
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        return ColorStateList(states, intArrayOf(palette.accent, palette.textSecondary))
    }

    private fun saveBackground(view: View) {
        if (view.getTag(tagBackground) == null && view.background != null) {
            view.setTag(tagBackground, view.background)
        }
    }

    private fun restore(view: View) {
        if (view.getTag(tagRoot) != null) {
            val savedRoot = view.getTag(tagRoot)
            if (savedRoot is Drawable) {
                view.background = savedRoot
            } else {
                view.background = null
            }
            view.setTag(tagRoot, null)
        }
        val savedBackground = view.getTag(tagBackground) as? Drawable
        if (savedBackground != null) {
            val current = view.background
            if (current is GradientDrawable && current === savedBackground) {
                (view.getTag(tagBgColor) as? Int)?.let { current.setColor(it) }
            } else {
                view.background = savedBackground
            }
            view.setTag(tagBackground, null)
            view.setTag(tagBgColor, null)
        }
        (view.getTag(tagText) as? Int)?.let {
            if (view is TextView) {
                view.setTextColor(it)
            }
            view.setTag(tagText, null)
        }
        (view.getTag(tagHint) as? ColorStateList)?.let {
            if (view is EditText) {
                view.setHintTextColor(it)
            }
            view.setTag(tagHint, null)
        }
        (view.getTag(tagCard) as? ColorStateList)?.let {
            if (view is MaterialCardView) {
                view.setCardBackgroundColor(it)
            }
            view.setTag(tagCard, null)
        }
        (view.getTag(tagTint) as? ColorStateList)?.let {
            when (view) {
                is FloatingActionButton -> view.backgroundTintList = it
                is MaterialButton -> view.backgroundTintList = it
            }
            view.setTag(tagTint, null)
        }
        (view.getTag(tagNavText) as? ColorStateList)?.let {
            when (view) {
                is BottomNavigationView -> view.itemTextColor = it
                is NavigationView -> view.itemTextColor = it
            }
            view.setTag(tagNavText, null)
        }
        (view.getTag(tagNavIcon) as? ColorStateList)?.let {
            when (view) {
                is BottomNavigationView -> view.itemIconTintList = it
                is NavigationView -> view.itemIconTintList = it
            }
            view.setTag(tagNavIcon, null)
        }
        if (view is Toolbar) {
            view.setTitleTextColor(ThemePalette.DEFAULT_TEXT_PRIMARY)
            view.setSubtitleTextColor(ThemePalette.DEFAULT_TEXT_SECONDARY)
        }
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                restore(view.getChildAt(index))
            }
        }
    }
}
