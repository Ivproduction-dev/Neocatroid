package org.catrobat.catroid.paintroid.ui

import org.catrobat.catroid.R
import org.catrobat.catroid.paintroid.tools.ToolType

object Ui2Helper {
    fun getToolIcon(toolType: ToolType, isUi2: Boolean): Int {
        if (!isUi2) return toolType.drawableResource
        return when (toolType) {
            ToolType.BRUSH -> R.drawable.ic_pocketpaint_v2_brush
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
            ToolType.SPRAY -> R.drawable.ic_pocketpaint_v2_spray
            ToolType.CLIPBOARD -> R.drawable.ic_pocketpaint_v2_stamp
            ToolType.IMPORTPNG -> R.drawable.ic_pocketpaint_v2_import
            ToolType.LASSO -> R.drawable.ic_pocketpaint_v2_lasso
            ToolType.MAGIC_WAND -> R.drawable.ic_pocketpaint_v2_magic_wand
            ToolType.LAYER -> R.drawable.ic_pocketpaint_v2_layers
            ToolType.COLORCHOOSER -> R.drawable.ic_pocketpaint_v2_color
            else -> toolType.drawableResource
        }
    }
}
