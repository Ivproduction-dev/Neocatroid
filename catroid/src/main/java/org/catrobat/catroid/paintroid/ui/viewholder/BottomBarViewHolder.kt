package org.catrobat.catroid.paintroid.ui.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.FrameLayout
import android.view.Gravity
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import org.catrobat.catroid.R
import org.catrobat.catroid.paintroid.contract.MainActivityContracts
import org.catrobat.catroid.paintroid.tools.ToolType

class BottomBarViewHolder(val layout: View) : MainActivityContracts.BottomBarViewHolder {
    private val pageSwitcher: ToolPageSwitcher
    private val pageIndicator: TextView
    private val previousPage: TextView
    private val nextPage: TextView

    init {
        val toolsTable = layout.findViewById<ViewGroup>(R.id.pocketpaint_tools_layout)
        val parent = toolsTable.parent as ViewGroup
        val index = parent.indexOfChild(toolsTable)
        val originalLayoutParams = toolsTable.layoutParams
        parent.removeView(toolsTable)
        originalLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        originalLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

        pageSwitcher = ToolPageSwitcher(layout.context)
        pageSwitcher.addView(
            toolsTable,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        pageSwitcher.addView(
            LayoutInflater.from(layout.context).inflate(R.layout.pocketpaint_tool_page_light2d, pageSwitcher, false)
        )

        val pagerLayout = LinearLayout(layout.context).apply {
            orientation = LinearLayout.VERTICAL
            background = layout.background
            addView(pageSwitcher, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        val pageControls = LinearLayout(layout.context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 0, 8, 0)
        }
        previousPage = TextView(layout.context).apply {
            text = "‹"
            textSize = 28f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(layout.context, R.color.pocketpaint_icon_tint))
            contentDescription = layout.context.getString(R.string.tools_previous_page)
            setOnClickListener { pageSwitcher.showPage(0) }
        }
        pageIndicator = TextView(layout.context).apply {
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(layout.context, R.color.pocketpaint_icon_tint))
        }
        nextPage = TextView(layout.context).apply {
            text = "›"
            textSize = 28f
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(layout.context, R.color.pocketpaint_icon_tint))
            contentDescription = layout.context.getString(R.string.tools_next_page)
            setOnClickListener { pageSwitcher.showPage(1) }
        }
        val density = layout.resources.displayMetrics.density
        pageControls.addView(previousPage, LinearLayout.LayoutParams((48 * density).toInt(), (44 * density).toInt()))
        pageControls.addView(pageIndicator, LinearLayout.LayoutParams(0, (44 * density).toInt(), 1f))
        pageControls.addView(nextPage, LinearLayout.LayoutParams((48 * density).toInt(), (44 * density).toInt()))
        pagerLayout.addView(pageControls, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (44 * density).toInt()))
        parent.addView(pagerLayout, index, originalLayoutParams)
        pageSwitcher.onPageChanged = ::updatePageControls
        updatePageControls(0)
    }

    override val isVisible: Boolean
        get() = layout.visibility == View.VISIBLE

    override fun show() {
        layout.visibility = View.VISIBLE
        pageSwitcher.hintNextPage()
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
        pageSwitcher.showPage(if (toolType == ToolType.LIGHT2D) 1 else 0)
    }

    private fun updatePageControls(page: Int) {
        previousPage.isEnabled = page > 0
        previousPage.alpha = if (page > 0) 1f else 0.35f
        nextPage.isEnabled = page == 0
        nextPage.alpha = if (page == 0) 1f else 0.35f
        pageIndicator.text = layout.context.getString(R.string.tools_page_indicator, page + 1, 2)
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
