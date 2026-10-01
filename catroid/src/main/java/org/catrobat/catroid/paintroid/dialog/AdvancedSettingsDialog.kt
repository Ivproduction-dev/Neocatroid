package org.catrobat.catroid.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import org.catrobat.catroid.R

import org.catrobat.catroid.paintroid.UserPreferences
import org.catrobat.catroid.paintroid.tools.helper.AdvancedSettingsAlgorithms.smoothing
import org.catrobat.catroid.paintroid.tools.implementation.DefaultToolPaint.Companion.antialiasing

class AdvancedSettingsDialog : MainActivityDialogFragment() {
    interface Listener {
        fun onUi2SettingsApplied(enabled: Boolean)
    }

    private var initValueAntialiasing: Boolean = antialiasing
    private var initValueSmoothing: Boolean = smoothing
    private var initValueUi2: Boolean = false

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val antialiasingSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_antialiasing)
        val smoothSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_smoothing)
        val ui2Switch = view.findViewById<SwitchCompat>(R.id.pocketpaint_ui2_switch)

        antialiasingSwitch.isChecked = antialiasing
        smoothSwitch.isChecked = smoothing

        val prefs = UserPreferences(
            requireContext().getSharedPreferences("preferences", android.content.Context.MODE_PRIVATE)
        )
        val tablet = resources.configuration.smallestScreenWidthDp >= 600
        ui2Switch.visibility = if (tablet) android.view.View.VISIBLE else android.view.View.GONE
        initValueUi2 = prefs.preferenceUi2Enabled
        ui2Switch.isChecked = initValueUi2

        antialiasingSwitch.setOnCheckedChangeListener { _, isChecked ->
            antialiasing = isChecked
        }

        smoothSwitch?.setOnCheckedChangeListener { _, isChecked ->
            smoothing = isChecked
        }

        ui2Switch.setOnCheckedChangeListener { _, isChecked ->
            prefs.preferenceUi2Enabled = isChecked
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pocketpaint_advanced_settings, null)
        onViewCreated(layout, savedInstanceState)

        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.menu_advanced)
            .setView(layout)
            .setPositiveButton(R.string.pocketpaint_ok) { _, _ ->
                presenter.setAntialiasingOnOkClicked()
                val enabled = UserPreferences(
                    requireContext().getSharedPreferences("preferences", android.content.Context.MODE_PRIVATE)
                ).preferenceUi2Enabled
                (activity as? Listener)?.onUi2SettingsApplied(enabled)
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ ->
                antialiasing = initValueAntialiasing
                smoothing = initValueSmoothing
                val prefs = UserPreferences(
                    requireContext().getSharedPreferences("preferences", android.content.Context.MODE_PRIVATE)
                )
                prefs.preferenceUi2Enabled = initValueUi2
                dismiss()
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        antialiasing = initValueAntialiasing
        smoothing = initValueSmoothing
        val prefs = UserPreferences(
            requireContext().getSharedPreferences("preferences", android.content.Context.MODE_PRIVATE)
        )
        prefs.preferenceUi2Enabled = initValueUi2
        super.onCancel(dialog)
    }
}
