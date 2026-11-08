package org.catrobat.catroid.ui.fragment

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class TemplateImportHelperFragment : Fragment() {

    interface Listener {
        fun onTemplatePicked(uri: Uri?)
        fun onHashPicked(uri: Uri?)
        fun onIconPicked(uri: Uri?)
    }

    private var listener: Listener? = null

    private val templatePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        listener?.onTemplatePicked(uri)
    }

    private val hashPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        listener?.onHashPicked(uri)
    }

    private val iconPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        listener?.onIconPicked(uri)
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun launchTemplatePicker() {
        templatePicker.launch(arrayOf("*/*"))
    }

    fun launchHashPicker() {
        hashPicker.launch(arrayOf("*/*"))
    }

    fun launchIconPicker() {
        iconPicker.launch(arrayOf("image/*"))
    }

    companion object {
        const val TAG = "template_import_helper"
    }
}
