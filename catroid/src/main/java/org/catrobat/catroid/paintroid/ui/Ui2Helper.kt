package org.catrobat.catroid.paintroid.ui

import org.catrobat.catroid.R
import org.catrobat.catroid.paintroid.tools.ToolType

object Ui2Helper {
    fun getToolIcon(toolType: ToolType, @Suppress("UNUSED_PARAMETER") isUi2: Boolean): Int {
        return when (toolType) {
            ToolType.BRUSH -> R.drawable.ic_pocketpaint_v2_paintbrush
            ToolType.PIXELART -> R.drawable.ic_pocketpaint_v2_pixelart
            ToolType.PATTERN -> R.drawable.ic_pocketpaint_v2_pattern
            ToolType.SYMMETRY -> R.drawable.ic_pocketpaint_v2_symmetry
            ToolType.AUTO_REMOVE_BG -> R.drawable.ic_pocketpaint_v2_auto_remove_bg
            ToolType.COLOR_REPLACE -> R.drawable.ic_pocketpaint_v2_color_replace
            ToolType.WATERCOLOR -> R.drawable.ic_pocketpaint_v2_watercolor
            ToolType.SMUDGE -> R.drawable.ic_pocketpaint_v2_smudge
            ToolType.CLIP -> R.drawable.ic_pocketpaint_v2_clipping
            ToolType.SPRAY -> R.drawable.ic_pocketpaint_v2_spray
            ToolType.ERASER -> R.drawable.ic_pocketpaint_v2_eraser
            ToolType.FILL -> R.drawable.ic_pocketpaint_v2_fill
            ToolType.PIPETTE -> R.drawable.ic_pocketpaint_v2_pipette
            ToolType.SHAPE -> R.drawable.ic_pocketpaint_v2_shapes
            ToolType.TEXT -> R.drawable.ic_pocketpaint_v2_text
            ToolType.LINE -> R.drawable.ic_pocketpaint_v2_line
            ToolType.CURSOR -> R.drawable.ic_pocketpaint_v2_cursor
            ToolType.TRANSFORM -> R.drawable.ic_pocketpaint_v2_transform
            ToolType.HAND -> R.drawable.ic_pocketpaint_v2_hand
            ToolType.UNDO -> R.drawable.ic_pocketpaint_v2_undo
            ToolType.REDO -> R.drawable.ic_pocketpaint_v2_redo
            ToolType.CLIPBOARD -> R.drawable.ic_pocketpaint_v2_stamp
            ToolType.IMPORTPNG -> R.drawable.ic_pocketpaint_v2_import
            ToolType.LASSO -> R.drawable.ic_pocketpaint_v2_lasso
            ToolType.MAGIC_WAND -> R.drawable.ic_pocketpaint_v2_magic_wand
            ToolType.LAYER -> R.drawable.ic_pocketpaint_v2_layers
            ToolType.COLORCHOOSER -> R.drawable.ic_pocketpaint_v2_color
            ToolType.LIGHT2D -> R.drawable.ic_pocketpaint_tool_light2d
            else -> toolType.drawableResource
        }
    }
}
