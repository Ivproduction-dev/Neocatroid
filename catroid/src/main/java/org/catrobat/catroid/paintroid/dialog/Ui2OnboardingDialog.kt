package org.catrobat.catroid.paintroid.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.R
import org.catrobat.catroid.paintroid.UserPreferences

class Ui2OnboardingDialog : MainActivityDialogFragment() {

    interface Listener {
        fun onUi2Accepted()
        fun onUi2Declined()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val prefs = UserPreferences(
            requireContext().getSharedPreferences("preferences", android.content.Context.MODE_PRIVATE)
        )
        prefs.preferenceUi2PromptShown = true

        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.pocketpaint_ui2_onboarding_title)
            .setMessage(R.string.pocketpaint_ui2_onboarding_message)
            .setPositiveButton(R.string.pocketpaint_ui2_onboarding_enable) { _, _ ->
                prefs.preferenceUi2Enabled = true
                (activity as? Listener)?.onUi2Accepted()
                dismiss()
            }
            .setNegativeButton(R.string.pocketpaint_ui2_onboarding_skip) { _, _ ->
                prefs.preferenceUi2Enabled = false
                (activity as? Listener)?.onUi2Declined()
                dismiss()
            }
            .setCancelable(false)
            .create()
    }
}
