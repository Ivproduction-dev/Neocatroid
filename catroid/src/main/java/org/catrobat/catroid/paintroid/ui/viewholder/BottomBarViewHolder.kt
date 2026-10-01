package org.catrobat.catroid.paintroid.ui.viewholder

import android.view.View
import org.catrobat.catroid.paintroid.contract.MainActivityContracts
import org.catrobat.catroid.paintroid.tools.ToolType

class BottomBarViewHolder(val layout: View) : MainActivityContracts.BottomBarViewHolder {
    override val isVisible: Boolean
        get() = layout.visibility == View.VISIBLE

    override fun show() {
        layout.visibility = View.VISIBLE
    }

    override fun hide() {
        layout.visibility = View.GONE
    }

    override fun setSelectedTool(toolType: ToolType) {
        val typesByButtonId = HashMap<Int, MutableList<ToolType>>()
        for (type in ToolType.values()) {
            typesByButtonId.getOrPut(type.toolButtonID) { ArrayList() }.add(type)
        }
        for ((buttonId, types) in typesByButtonId) {
            val button = layout.findViewById<View>(buttonId) ?: continue
            button.isSelected = toolType in types
        }
    }

    fun applyUiMode(ui2Enabled: Boolean) {
        for (type in ToolType.values()) {
            val button = layout.findViewById<android.view.ViewGroup>(type.toolButtonID) ?: continue
            for (i in 0 until button.childCount) {
                val child = button.getChildAt(i)
                if (child is android.widget.ImageView) {
                    child.setImageResource(org.catrobat.catroid.paintroid.ui.Ui2Helper.getToolIcon(type, ui2Enabled))
                    break
                }
            }
        }
    }
}
