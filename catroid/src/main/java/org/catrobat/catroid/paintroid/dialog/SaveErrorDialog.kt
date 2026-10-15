package org.catrobat.catroid.paintroid.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.R

class SaveErrorDialog : MainActivityDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.dialog_error_save_title)
            .setMessage(R.string.dialog_error_sdcard_text)
        val isCatroid = arguments?.getBoolean(EXTRA_CATROID) ?: false
        if (isCatroid) {
            builder.setPositiveButton(R.string.save_button_text) { _, _ ->
                dismiss()
                presenter.saveBeforeFinish()
            }
            builder.setNegativeButton(R.string.discard_button_text) { _, _ ->
                dismiss()
                presenter.finishActivity()
            }
        } else {
            builder.setPositiveButton(android.R.string.ok) { _, _ -> dismiss() }
        }
        return builder.create()
    }

    companion object {
        private const val EXTRA_CATROID = "isCatroid"

        fun newInstance(isCatroid: Boolean): SaveErrorDialog {
            return SaveErrorDialog().apply {
                arguments = Bundle().apply { putBoolean(EXTRA_CATROID, isCatroid) }
            }
        }
    }
}
