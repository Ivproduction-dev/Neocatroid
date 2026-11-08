package org.catrobat.catroid.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.catrobat.catroid.R

class BrickTemplatesFragment : Fragment() {
    private var addBrickListener: AddBrickFragment.OnAddBrickListener? = null

    companion object {
        const val BRICK_TEMPLATES_FRAGMENT_TAG = "brick_templates_fragment"
        private const val BUNDLE_ARGUMENTS_SELECTED_CATEGORY = "selected_category"

        @JvmStatic
        fun newInstance(
            selectedCategory: String?,
            addBrickListener: AddBrickFragment.OnAddBrickListener?
        ): BrickTemplatesFragment {
            val fragment = BrickTemplatesFragment()
            val arguments = Bundle()
            arguments.putString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY, selectedCategory)
            fragment.arguments = arguments
            fragment.addBrickListener = addBrickListener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_brick_templates, container, false)
        if (!isWorkspaceMode()) {
            (activity as? AppCompatActivity)?.supportActionBar?.hide()
        }
        view.findViewById<ImageButton>(R.id.templates_close_button)?.setOnClickListener { closeSelf() }
        view.setOnClickListener { closeSelf() }
        setupTemplates(view)
        return view
    }

    override fun onDestroyView() {
        if (!isWorkspaceMode()) {
            (activity as? AppCompatActivity)?.supportActionBar?.show()
        }
        super.onDestroyView()
    }

    private fun isWorkspaceMode(): Boolean {
        val workspace = activity?.findViewById<View>(R.id.workspace_layout)
        return workspace != null && workspace.visibility == View.VISIBLE
    }

    private fun closeSelf() {
        val host = activity ?: return
        if (isWorkspaceMode()) {
            (host.findViewById<View>(R.id.workspace_layout) as? org.catrobat.catroid.ui.workspace.WorkspaceLayout)
                ?.removeWindow(BRICK_TEMPLATES_FRAGMENT_TAG, false)
        } else if (parentFragmentManager.findFragmentByTag(BRICK_TEMPLATES_FRAGMENT_TAG) != null) {
            parentFragmentManager.popBackStack(
                BRICK_TEMPLATES_FRAGMENT_TAG,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    private fun setupTemplates(view: View) {
        val category = arguments?.getString(BUNDLE_ARGUMENTS_SELECTED_CATEGORY)
        val context = requireContext()
        val templates = BrickTemplates.forCategory(category, context)
        val grid = view.findViewById<GridLayout>(R.id.templates_grid) ?: return
        for (template in templates) {
            val inflater = LayoutInflater.from(context)
            val displayMetrics = resources.displayMetrics
            val screenDp = resources.configuration.screenWidthDp - 32
            val cardDp = ((screenDp - 48) / 3).coerceIn(92, 140)
            val cardPx = (cardDp * displayMetrics.density).toInt()
            val card = inflater.inflate(R.layout.item_brick_template, grid, false)
            val cardParams = GridLayout.LayoutParams()
            cardParams.width = cardPx
            cardParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            card.layoutParams = cardParams
            val artContainer = card.findViewById<FrameLayout>(R.id.template_art_container)
            artContainer?.addView(inflater.inflate(template.artLayoutRes, artContainer, false))
            card.findViewById<TextView>(R.id.template_caption)?.setText(template.captionRes)
            card.setOnClickListener { openTemplate(template) }
            card.setOnLongClickListener {
                showTemplateInfoPopup(it, template)
                true
            }
            grid.addView(card)
        }
    }

    private fun openTemplate(template: BrickTemplate) {
        val host = activity ?: return
        val scripts = template.createScripts()
        addTemplateToScript(
            scripts,
            host,
            addBrickListener,
            parentFragmentManager
        )
    }

    private fun showTemplateInfoPopup(anchor: View, template: BrickTemplate) {
        val host = activity ?: return
        val content = LayoutInflater.from(host).inflate(R.layout.popup_template_info, null)
        content.findViewById<TextView>(R.id.popup_template_title)?.setText(template.captionRes)
        content.findViewById<TextView>(R.id.popup_template_description)?.setText(template.descriptionRes)
        val popup = PopupWindow(
            content,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popup.elevation = 16f
        popup.isOutsideTouchable = true
        content.findViewById<ImageButton>(R.id.popup_template_close)?.setOnClickListener {
            popup.dismiss()
        }
        popup.showAtLocation(anchor, Gravity.CENTER, 0, 0)
    }
}
