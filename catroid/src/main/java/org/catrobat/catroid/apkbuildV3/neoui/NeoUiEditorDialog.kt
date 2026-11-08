package org.catrobat.catroid.apkbuildV3.neoui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import org.catrobat.catroid.R
import org.catrobat.catroid.utils.ToastUtil
import java.io.File
import java.io.FileOutputStream
import java.io.BufferedOutputStream

class NeoUiEditorDialog(
    private val hostFragment: Fragment,
    initialLayout: NeoUiLayout,
    initialBgImage: File?,
    initialCompilationLayout: NeoUiLayout,
    initialCompilationBgImage: File?,
    private val projectName: String,
    private val themeBgColor: String,
    private val themeAccentColor: String,
    private val onApply: (NeoUiLayout, File?, NeoUiLayout, File?) -> Unit
) {
    private class ScreenState(
        var layout: NeoUiLayout,
        var bgFile: File?,
        var bgBitmap: Bitmap? = null
    )

    private val context: Context = hostFragment.requireContext()
    private val screens = arrayOf(
        ScreenState(copyLayout(initialLayout), initialBgImage),
        ScreenState(copyLayout(initialCompilationLayout), initialCompilationBgImage)
    )
    private var activeScreen = 0
    private var dialog: Dialog? = null
    private var canvasView: NeoUiCanvasView? = null
    private var deleteBtn: View? = null
    private var tabLoading: View? = null
    private var tabCompilation: View? = null

    private val screen: ScreenState
        get() = screens[activeScreen]

    companion object {
        const val REQUEST_CODE_IMAGE = 8841
        const val REQUEST_CODE_NEOUI = 8842
        const val REQUEST_CODE_NEOUI_EXPORT = 8843

        var activeEditor: NeoUiEditorDialog? = null

        fun onImagePicked(uri: Uri?) {
            activeEditor?.handleImageResult(uri)
        }

        fun onNeouiImportPicked(uri: Uri?) {
            activeEditor?.handleNeouiImportResult(uri)
        }

        fun onNeouiExportPicked(uri: Uri?) {
            activeEditor?.handleNeouiExportResult(uri)
        }

        private fun copyLayout(source: NeoUiLayout): NeoUiLayout = source.copy(
            background = source.background.copy(),
            progressBar = source.progressBar.copy(),
            texts = source.texts.map { it.copy() }.toMutableList()
        )
    }

    fun show() {
        activeEditor = this
        dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
            setContentView(R.layout.dialog_neoui_editor)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            setOnDismissListener {
                onApply(screens[0].layout, screens[0].bgFile, screens[1].layout, screens[1].bgFile)
                if (activeEditor === this@NeoUiEditorDialog) {
                    activeEditor = null
                }
            }
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    dismiss()
                    true
                } else {
                    false
                }
            }
        }

        val d = dialog ?: return
        canvasView = d.findViewById(R.id.neoui_canvas)
        deleteBtn = d.findViewById(R.id.neoui_btn_delete)
        tabLoading = d.findViewById(R.id.neoui_tab_loading)
        tabCompilation = d.findViewById(R.id.neoui_tab_compilation)
        val backBtn = d.findViewById<View>(R.id.neoui_btn_back)
        val resetBtn = d.findViewById<View>(R.id.neoui_btn_reset)
        val menuBtn = d.findViewById<View>(R.id.neoui_btn_menu)

        loadInitialBitmaps()

        val canvas = canvasView ?: return
        canvas.layoutData = screen.layout
        canvas.backgroundBitmap = screen.bgBitmap
        updateTabs()

        tabLoading?.setOnClickListener { switchScreen(0) }
        tabCompilation?.setOnClickListener { switchScreen(1) }

        deleteBtn?.setOnClickListener {
            canvas.deleteSelected()
        }

        backBtn?.setOnClickListener {
            d.dismiss()
        }

        resetBtn?.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.v3_neoui_reset)
                .setMessage(R.string.v3_neoui_reset_confirm)
                .setPositiveButton(R.string.yes) { _, _ ->
                    val fresh = if (activeScreen == 0) {
                        NeoUiLayout.createDefault(themeBgColor, themeAccentColor)
                    } else {
                        NeoUiLayout.createDefaultCompilation(themeAccentColor)
                    }
                    screen.layout = fresh
                    screen.bgFile = null
                    screen.bgBitmap?.recycle()
                    screen.bgBitmap = null
                    canvas.backgroundBitmap = null
                    canvas.layoutData = fresh
                    canvas.resetView()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }

        menuBtn?.setOnClickListener { v ->
            showOverflowMenu(v)
        }

        canvas.onSelectionChanged = { type, _ ->
            deleteBtn?.visibility = if (type != NeoUiCanvasView.SelectedType.NONE) View.VISIBLE else View.GONE
        }

        canvas.onTextDoubleTapped = { textItem ->
            showEditTextDialog(textItem)
        }

        canvas.onTextLongPressed = { textItem ->
            showTextOptionsDialog(textItem)
        }

        canvas.onProgressBarDoubleTapped = {
            showProgressBarDialog()
        }

        canvas.onProgressBarLongPressed = {
            showProgressBarDialog()
        }

        canvas.onCanvasLongPressed = { normX, normY ->
            if (canvas.layoutData.texts.size >= 20) {
                ToastUtil.showError(context, context.getString(R.string.v3_neoui_text_limit))
            } else {
                val newText = NeoUiText(
                    id = System.currentTimeMillis(),
                    content = context.getString(if (activeScreen == 0) R.string.v3_loading else R.string.v3_neoui_compiling_text),
                    x = normX.coerceIn(-0.5f, 1.2f),
                    y = normY.coerceIn(-0.5f, 1.2f),
                    width = 0.5f,
                    height = 0.05f,
                    fontSize = 16f,
                    color = "#FFFFFF"
                )
                canvas.layoutData.texts.add(newText)
                canvas.invalidate()
            }
        }

        canvas.onFrameLongPressed = {
            showBackgroundOptionsDialog()
        }

        d.show()
    }

    private fun switchScreen(index: Int) {
        if (index == activeScreen) return
        activeScreen = index
        val canvas = canvasView ?: return
        canvas.layoutData = screen.layout
        canvas.backgroundBitmap = screen.bgBitmap
        canvas.resetView()
        deleteBtn?.visibility = View.GONE
        updateTabs()
    }

    private fun updateTabs() {
        tabLoading?.setBackgroundResource(
            if (activeScreen == 0) R.drawable.bg_neoui_tab_selected else R.drawable.bg_neoui_tab
        )
        tabCompilation?.setBackgroundResource(
            if (activeScreen == 1) R.drawable.bg_neoui_tab_selected else R.drawable.bg_neoui_tab
        )
    }

    private fun loadInitialBitmaps() {
        screens.forEach { state ->
            val file = state.bgFile
            if (file != null && file.exists()) {
                state.bgBitmap = try {
                    BitmapFactory.decodeFile(file.absolutePath)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private fun showTextOptionsDialog(textItem: NeoUiText) {
        val options = arrayOf(
            context.getString(R.string.v3_neoui_edit_text_title),
            context.getString(R.string.v3_neoui_text_style),
            context.getString(R.string.v3_neoui_size_and_position),
            context.getString(R.string.v3_neoui_delete_element)
        )
        AlertDialog.Builder(context)
            .setTitle(R.string.v3_neoui_text_options)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditTextDialog(textItem)
                    1 -> showTextStyleDialog(textItem)
                    2 -> showTextPositionSizeDialog(textItem)
                    3 -> canvasView?.deleteText(textItem)
                }
            }
            .show()
    }

    private fun showTextPositionSizeDialog(textItem: NeoUiText) {
        val container = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(dp(20), dp(12), dp(20), dp(4))
        }
        val xInput = addPercentField(container, R.string.v3_neoui_field_x, textItem.x * 100f)
        val yInput = addPercentField(container, R.string.v3_neoui_field_y, textItem.y * 100f)
        val wInput = addPercentField(container, R.string.v3_neoui_field_width, textItem.width * 100f)
        val hInput = addPercentField(container, R.string.v3_neoui_field_height, textItem.height * 100f)

        AlertDialog.Builder(context)
            .setTitle(R.string.v3_neoui_size_and_position)
            .setView(container)
            .setPositiveButton(R.string.ok) { _, _ ->
                xInput.text.toString().toFloatOrNull()?.let { textItem.x = (it / 100f).coerceIn(-0.5f, 1.2f) }
                yInput.text.toString().toFloatOrNull()?.let { textItem.y = (it / 100f).coerceIn(-0.5f, 1.2f) }
                wInput.text.toString().toFloatOrNull()?.let { textItem.width = (it / 100f).coerceIn(0.01f, 2f) }
                hInput.text.toString().toFloatOrNull()?.let { textItem.height = (it / 100f).coerceIn(0.005f, 1f) }
                canvasView?.invalidate()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showProgressBarDialog() {
        val bar = screen.layout.progressBar
        val container = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(dp(20), dp(12), dp(20), dp(4))
        }

        val colorLabel = android.widget.TextView(context).apply {
            text = context.getString(R.string.v3_neoui_progress_color)
            setTextColor(Color.WHITE)
            textSize = 13f
        }
        container.addView(colorLabel)

        val colors = listOf("#FFFFFF", "#A8DFF4", "#4CAF50", "#F44336", "#FFEB3B", "#000000")
        val row = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            setPadding(0, dp(8), 0, dp(12))
        }
        colors.forEach { hex ->
            val swatch = View(context)
            val params = android.widget.LinearLayout.LayoutParams(0, dp(40), 1f)
            params.setMargins(dp(3), 0, dp(3), 0)
            swatch.layoutParams = params
            swatch.setBackgroundColor(Color.parseColor(hex))
            swatch.setOnClickListener {
                bar.color = hex
                canvasView?.invalidate()
            }
            row.addView(swatch)
        }
        container.addView(row)

        val visibleCheck = android.widget.CheckBox(context).apply {
            text = context.getString(R.string.v3_neoui_progress_visible)
            setTextColor(Color.WHITE)
            isChecked = bar.visible
        }
        container.addView(visibleCheck)

        val xInput = addPercentField(container, R.string.v3_neoui_field_x, bar.x * 100f)
        val yInput = addPercentField(container, R.string.v3_neoui_field_y, bar.y * 100f)
        val wInput = addPercentField(container, R.string.v3_neoui_field_width, bar.width * 100f)
        val hInput = addPercentField(container, R.string.v3_neoui_field_height, bar.height * 100f)

        AlertDialog.Builder(context)
            .setTitle(R.string.v3_neoui_progress_bar)
            .setView(container)
            .setPositiveButton(R.string.ok) { _, _ ->
                bar.visible = visibleCheck.isChecked
                xInput.text.toString().toFloatOrNull()?.let { bar.x = (it / 100f).coerceIn(-0.5f, 1.2f) }
                yInput.text.toString().toFloatOrNull()?.let { bar.y = (it / 100f).coerceIn(-0.5f, 1.2f) }
                wInput.text.toString().toFloatOrNull()?.let { bar.width = (it / 100f).coerceIn(0.01f, 2f) }
                hInput.text.toString().toFloatOrNull()?.let { bar.height = (it / 100f).coerceIn(0.005f, 1f) }
                canvasView?.invalidate()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addPercentField(container: android.widget.LinearLayout, labelRes: Int, value: Float): android.widget.EditText {
        val label = android.widget.TextView(context).apply {
            text = context.getString(labelRes)
            setTextColor(Color.WHITE)
            setPadding(0, dp(6), 0, 0)
            textSize = 13f
        }
        container.addView(label)
        val input = android.widget.EditText(context).apply {
            setText(String.format(java.util.Locale.US, "%.1f", value))
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#171821"))
            setPadding(dp(8), dp(6), dp(8), dp(6))
            textSize = 13f
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)
        }
        container.addView(input)
        return input
    }

    private fun dp(value: Int): Int = (value * context.resources.displayMetrics.density).toInt()

    private fun showEditTextDialog(textItem: NeoUiText) {
        val input = EditText(context).apply {
            setText(textItem.content)
            setSelection(textItem.content.length)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#171821"))
            setPadding(36, 28, 36, 28)
            textSize = 14f
        }

        AlertDialog.Builder(context)
            .setTitle(R.string.v3_neoui_edit_text_title)
            .setView(input)
            .setPositiveButton(R.string.ok) { _, _ ->
                val newContent = input.text.toString().trim()
                if (newContent.isNotEmpty()) {
                    textItem.content = newContent
                    canvasView?.invalidate()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showTextStyleDialog(textItem: NeoUiText) {
        val container = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(dp(20), dp(12), dp(20), dp(4))
        }
        val sizeLabel = android.widget.TextView(context).apply {
            text = context.getString(R.string.v3_neoui_text_size)
            setTextColor(Color.WHITE)
        }
        container.addView(sizeLabel)
        val size = android.widget.SeekBar(context).apply {
            max = 72
            progress = (textItem.fontSize - 6f).toInt().coerceIn(0, max)
        }
        container.addView(size)
        val fontLabel = android.widget.TextView(context).apply {
            text = context.getString(R.string.v3_neoui_text_font)
            setTextColor(Color.WHITE)
            setPadding(0, dp(8), 0, dp(4))
        }
        container.addView(fontLabel)
        val fontNames = arrayOf(
            context.getString(R.string.v3_neoui_font_default),
            context.getString(R.string.v3_neoui_font_sans),
            context.getString(R.string.v3_neoui_font_serif),
            context.getString(R.string.v3_neoui_font_mono)
        )
        val fontKeys = arrayOf("default", "sans-serif", "serif", "monospace")
        val fontSpinner = android.widget.Spinner(context).apply {
            adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, fontNames)
            setSelection(fontKeys.indexOf(textItem.fontFamily).coerceAtLeast(0))
        }
        container.addView(fontSpinner)
        val colorLabel = android.widget.TextView(context).apply {
            text = context.getString(R.string.v3_neoui_text_color)
            setTextColor(Color.WHITE)
            setPadding(0, dp(8), 0, 0)
        }
        container.addView(colorLabel)
        val colors = listOf("#FFFFFF", "#A8DFF4", "#4CAF50", "#F44336", "#FFEB3B", "#000000")
        var selectedColor = textItem.color
        val colorRow = android.widget.LinearLayout(context).apply { orientation = android.widget.LinearLayout.HORIZONTAL }
        colors.forEach { hex ->
            val swatch = View(context).apply {
                setBackgroundColor(Color.parseColor(hex))
                setOnClickListener {
                    selectedColor = hex
                }
            }
            colorRow.addView(swatch, android.widget.LinearLayout.LayoutParams(0, dp(36), 1f).apply {
                setMargins(dp(3), dp(8), dp(3), dp(4))
            })
        }
        container.addView(colorRow)
        AlertDialog.Builder(context)
            .setTitle(R.string.v3_neoui_text_style)
            .setView(container)
            .setPositiveButton(R.string.ok) { _, _ ->
                textItem.fontSize = (size.progress + 6).toFloat()
                textItem.fontFamily = fontKeys[fontSpinner.selectedItemPosition.coerceIn(fontKeys.indices)]
                textItem.color = selectedColor
                canvasView?.invalidate()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showBackgroundOptionsDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_neoui_bg_options, null)
        val previewImg = view.findViewById<ImageView>(R.id.neoui_bg_preview_img)
        val importBtn = view.findViewById<MaterialButton>(R.id.neoui_btn_import_image)
        val clearBtn = view.findViewById<MaterialButton>(R.id.neoui_btn_clear_bg_image)
        val root = view as android.widget.LinearLayout
        val colorRow = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            setPadding(0, dp(10), 0, dp(6))
        }
        listOf("#3A3A3C", "#171821", "#555555", "#000000", "#FFFFFF", "#A8DFF4").forEach { hex ->
            val swatch = View(context).apply {
                setBackgroundColor(Color.parseColor(hex))
                setOnClickListener {
                    screen.layout.background.color = hex
                    screen.layout.background.type = "color"
                    screen.layout.background.asset = null
                    screen.bgFile = null
                    screen.bgBitmap?.recycle()
                    screen.bgBitmap = null
                    canvasView?.backgroundBitmap = null
                    canvasView?.invalidate()
                    previewImg.setImageDrawable(null)
                    previewImg.setBackgroundColor(Color.parseColor(hex))
                    clearBtn.visibility = View.GONE
                }
            }
            colorRow.addView(swatch, android.widget.LinearLayout.LayoutParams(0, dp(36), 1f).apply {
                setMargins(dp(3), 0, dp(3), 0)
            })
        }
        root.addView(android.widget.TextView(context).apply {
            text = context.getString(R.string.v3_neoui_bg_color)
            setTextColor(Color.WHITE)
            setPadding(0, dp(4), 0, 0)
        }, 2)
        root.addView(colorRow, 3)

        if (screen.bgBitmap != null) {
            previewImg.setImageBitmap(screen.bgBitmap)
            clearBtn.visibility = View.VISIBLE
        } else {
            val color = try {
                Color.parseColor(screen.layout.background.color)
            } catch (e: Exception) {
                Color.parseColor("#3A3A3C")
            }
            previewImg.setBackgroundColor(color)
            clearBtn.visibility = View.GONE
        }

        val bgDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        importBtn.setOnClickListener {
            bgDialog.dismiss()
            pickImage()
        }

        clearBtn.setOnClickListener {
            screen.bgFile = null
            screen.bgBitmap?.recycle()
            screen.bgBitmap = null
            screen.layout.background.type = "color"
            screen.layout.background.asset = null
            canvasView?.backgroundBitmap = null
            canvasView?.layoutData?.background?.type = "color"
            canvasView?.layoutData?.background?.asset = null
            canvasView?.invalidate()
            bgDialog.dismiss()
        }

        bgDialog.show()
    }

    private fun showOverflowMenu(anchor: View) {
        val popup = PopupMenu(context, anchor)
        popup.menu.add(0, 1, 0, R.string.v3_neoui_export)
        popup.menu.add(0, 2, 1, R.string.v3_neoui_import)
        popup.menu.add(0, 3, 2, R.string.v3_neoui_progress_bar)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    exportNeoui()
                    true
                }
                2 -> {
                    pickNeouiFile()
                    true
                }
                3 -> {
                    showProgressBarDialog()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun exportNeoui() {
        if (canvasView == null) return
        val safeName = projectName.replace(Regex("""[\\/:*?"<>| ]"""), "_").ifBlank { "game" }
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            putExtra(Intent.EXTRA_TITLE, "${safeName}_ui.neoui")
        }
        hostFragment.startActivityForResult(intent, REQUEST_CODE_NEOUI_EXPORT)
    }

    fun handleNeouiExportResult(uri: Uri?) {
        if (uri == null) return
        val canvas = canvasView ?: return
        screen.layout = copyLayout(canvas.layoutData)
        val loadingLayout = copyLayout(screens[0].layout)
        val loadingBgFile = screens[0].bgFile
        val compilationLayout = copyLayout(screens[1].layout)
        val compilationBgFile = screens[1].bgFile
        Thread {
            val success = runCatching {
                context.contentResolver.openOutputStream(uri, "wt")?.use {
                    NeoUiZipManager.exportBundle(
                        loadingLayout,
                        loadingBgFile,
                        compilationLayout,
                        compilationBgFile,
                        BufferedOutputStream(it),
                        projectName
                    )
                } ?: false
            }.getOrDefault(false)
            hostFragment.activity?.runOnUiThread {
                if (success) ToastUtil.showSuccess(context, "UI exported") else ToastUtil.showError(context, "Export failed")
            }
        }.start()
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        hostFragment.startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    private fun pickNeouiFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        hostFragment.startActivityForResult(intent, REQUEST_CODE_NEOUI)
    }

    fun handleImageResult(uri: Uri?) {
        if (uri == null) return
        try {
            val tempFile = File(context.cacheDir, "neoui_bg_${System.currentTimeMillis()}.png")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyBoundedTo(output, 20L * 1024 * 1024)
                }
            }

            if (tempFile.exists() && tempFile.length() > 0) {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(tempFile.absolutePath, bounds)
                require(bounds.outWidth in 1..8192 && bounds.outHeight in 1..8192 && bounds.outWidth.toLong() * bounds.outHeight <= 32_000_000)
                val bmp = BitmapFactory.decodeFile(tempFile.absolutePath, BitmapFactory.Options().apply { inSampleSize = 2 })
                if (bmp != null) {
                    screen.bgFile = tempFile
                    screen.bgBitmap = bmp
                    screen.layout.background.type = "image"
                    screen.layout.background.asset = "assets/background.png"
                    canvasView?.backgroundBitmap = bmp
                    canvasView?.layoutData?.background?.type = "image"
                    canvasView?.layoutData?.background?.asset = "assets/background.png"
                    canvasView?.invalidate()
                }
            }
        } catch (e: Exception) {
            ToastUtil.showError(context, "Failed to load image")
        }
    }

    fun handleNeouiImportResult(uri: Uri?) {
        if (uri == null) return
        Thread {
            val tempImportZip = File(context.cacheDir, "temp_import.neoui")
            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempImportZip).use { output -> input.copyBoundedTo(output, 48L * 1024 * 1024) }
                }
                val extractDir = File(context.cacheDir, "neoui_import_${System.currentTimeMillis()}")
                val importedBundle = NeoUiZipManager.importZip(tempImportZip, extractDir)
                tempImportZip.delete()

                hostFragment.activity?.runOnUiThread {
                    if (importedBundle != null) {
                        AlertDialog.Builder(context)
                            .setTitle(R.string.v3_neoui_import)
                            .setMessage(
                                if (importedBundle.compilationScreen == null) {
                                    R.string.v3_neoui_import_single_confirm
                                } else {
                                    R.string.v3_neoui_import_confirm
                                }
                            )
                            .setPositiveButton(R.string.yes) { _, _ ->
                                val loadingScreen = importedBundle.loadingScreen
                                val compilationScreen = importedBundle.compilationScreen
                                if (compilationScreen == null && loadingScreen != null) {
                                    applyImportedScreen(screen, loadingScreen)
                                } else {
                                    loadingScreen?.let { applyImportedScreen(screens[0], it) }
                                    compilationScreen?.let { applyImportedScreen(screens[1], it) }
                                }
                                canvasView?.backgroundBitmap = screen.bgBitmap
                                canvasView?.layoutData = screen.layout
                                canvasView?.resetView()
                                ToastUtil.showSuccess(context, "UI imported")
                            }
                            .setNegativeButton(R.string.no) { _, _ -> extractDir.deleteRecursively() }
                            .setOnCancelListener { extractDir.deleteRecursively() }
                            .show()
                    } else {
                        extractDir.deleteRecursively()
                        ToastUtil.showError(context, "Invalid .neoui file")
                    }
                }
            } catch (e: Exception) {
                tempImportZip.delete()
                hostFragment.activity?.runOnUiThread {
                    ToastUtil.showError(context, "Import failed: ${e.message}")
                }
            }
        }.start()
    }

    private fun applyImportedScreen(target: ScreenState, imported: NeoUiImportedScreen) {
        target.layout = imported.layout
        target.bgFile = imported.bgFile
        target.bgBitmap?.recycle()
        target.bgBitmap = imported.bgFile?.let { BitmapFactory.decodeFile(it.absolutePath) }
    }

    private fun java.io.InputStream.copyBoundedTo(output: java.io.OutputStream, limit: Long) {
        val buffer = ByteArray(8192)
        var total = 0L
        while (true) {
            val count = read(buffer)
            if (count < 0) break
            total += count
            require(total <= limit)
            output.write(buffer, 0, count)
        }
    }
}
