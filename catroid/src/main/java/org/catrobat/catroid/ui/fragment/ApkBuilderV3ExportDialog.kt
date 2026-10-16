package org.catrobat.catroid.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import org.catrobat.catroid.R
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.apkbuildV3.ApkBuilderV3Config
import org.catrobat.catroid.apkbuildV3.ApkBuilderV3Engine
import org.catrobat.catroid.apkbuildV3.AssemblyResult
import org.catrobat.catroid.apkbuildV3.BuildProgressListener
import org.catrobat.catroid.apkbuildV3.FirebaseConfig
import org.catrobat.catroid.apkbuildV3.FirebaseConfigManager
import org.catrobat.catroid.apkbuildV3.TemplateManagerV3
import org.catrobat.catroid.apkbuildV3.TemplateType
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.utils.ToastUtil
import java.io.File
import java.io.FileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApkBuilderV3ExportDialog {
    private val tag = "ApkBuilderV3ExportDialog"
    private var config: ApkBuilderV3Config? = null
    private var projectDir: File? = null
    private var firebaseUri: Uri? = null
    private var firebaseConfig: FirebaseConfig? = null
    private var hostContext: android.content.Context? = null
    private var lastView: android.view.View? = null
    private var hostFragment: Fragment? = null
    private var settingsDialog: AlertDialog? = null
    private var currentStep = 1
    private var templateReady = false

    private var firebaseLauncher: ActivityResultLauncher<Array<String>>? = null

    companion object {
        private const val TOTAL_STEPS = 4

        @Volatile
        var activeInstance: ApkBuilderV3ExportDialog? = null

        fun onFirebaseUriResult(uri: Uri?) {
            activeInstance?.onFirebaseUriResult(uri)
        }
    }

    fun show(activity: Fragment, projectDir: File, firebaseLauncher: ActivityResultLauncher<Array<String>>) {
        this.projectDir = projectDir
        this.hostContext = activity.requireContext()
        this.hostFragment = activity
        this.firebaseLauncher = firebaseLauncher
        activeInstance = this
        val dialog = buildDialog(activity)
        settingsDialog = dialog
        dialog.setOnDismissListener { if (activeInstance === this) activeInstance = null }
        dialog.show()
        val metrics = activity.resources.displayMetrics
        val horizontalMargin = (32 * metrics.density).toInt()
        val maxWidth = (560 * metrics.density).toInt()
        dialog.window?.apply {
            setLayout(
                minOf(metrics.widthPixels - horizontalMargin, maxWidth),
                (metrics.heightPixels * 0.88f).toInt()
            )
            setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    fun onFirebaseUriResult(uri: Uri?) {
        if (uri != null) {
            firebaseUri = uri
            val ctx = hostContext ?: return
            val pkgInput = lastView?.findViewById<TextInputEditText>(R.id.v3_package_input)?.text.toString()
                .ifBlank { "org.neocatroid.runtime.v3" }.lowercase()
            val result = FirebaseConfigManager.processGoogleServicesJson(ctx, uri, pkgInput)
            val firebaseAddButton = lastView?.findViewById<Button>(R.id.v3_firebase_add_button)
            val firebaseStatus = lastView?.findViewById<TextView>(R.id.v3_firebase_status)
            val firebaseRemoveButton = lastView?.findViewById<Button>(R.id.v3_firebase_remove_button)
            if (result.error != null) {
                firebaseConfig = null
                ToastUtil.showError(ctx, result.error.message)
                firebaseStatus?.visibility = android.view.View.GONE
                firebaseRemoveButton?.visibility = android.view.View.GONE
                firebaseAddButton?.text = ctx.getString(R.string.v3_firebase_add_button)
            } else if (result.config != null) {
                firebaseConfig = result.config
                val fileName = result.config.sourceFileName
                firebaseStatus?.text = "${ctx.getString(R.string.v3_firebase_file_added)}: $fileName"
                firebaseStatus?.visibility = android.view.View.VISIBLE
                firebaseRemoveButton?.visibility = android.view.View.VISIBLE
                firebaseAddButton?.text = ctx.getString(R.string.v3_firebase_add_button)
            }
            } else {
            firebaseUri = null
            firebaseConfig = null
        }
    }


    private fun <T : View> findV(root: View, idName: String): T? {
        val id = root.resources.getIdentifier(idName, "id", root.context.packageName)
        return if (id != 0) root.findViewById(id) else null
    }

    private fun buildDialog(host: Fragment): AlertDialog {
        val ctx = host.requireContext()
        val project = ProjectManager.getInstance().currentProject ?: return AlertDialog.Builder(ctx).create()

        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_apk_builder_v3_export, null)
        lastView = view
        currentStep = 1
        templateReady = false

        view.findViewById<TextInputEditText>(R.id.v3_app_name_input)?.setText(project.name)
        view.findViewById<TextInputEditText>(R.id.v3_package_input)?.setText("org.neocatroid.runtime.v3")
        view.findViewById<TextInputEditText>(R.id.v3_version_input)?.setText("1.0")
        view.findViewById<TextInputEditText>(R.id.v3_version_code_input)?.setText("1")
        view.findViewById<TextInputEditText>(R.id.v3_min_sdk_input)?.setText(ApkBuilderV3Config.RUNTIME_MIN_SDK.toString())
        view.findViewById<TextInputEditText>(R.id.v3_target_sdk_input)?.setText("35")

        val permContainer = view.findViewById<LinearLayout>(R.id.v3_permissions_container)
        val permChecks = ApkBuilderV3Config.ALL_PERMISSIONS.map { perm ->
            val cb = CheckBox(ctx).apply {
                text = ctx.getString(permissionLabelResId(perm))
                isChecked = perm in ApkBuilderV3Config.DEFAULT_PERMISSIONS
            }
            permContainer?.addView(cb)
            perm to cb
        }

        val permModeGroup = view.findViewById<android.widget.RadioGroup>(R.id.v3_permissions_mode_group)
        val permAutoRadio = view.findViewById<RadioButton>(R.id.v3_perm_mode_auto)
        val autoPermissions = org.catrobat.catroid.apkbuildV3.ProjectScanner.detectPermissions(project)
        findV<TextView>(view, "v3_permissions_detected_badge")?.text =
            ctx.getString(R.string.v3_perm_detected_count, autoPermissions.size)

        fun updatePermissionsUI(isAuto: Boolean) {
            permChecks.forEach { (perm, cb) ->
                if (isAuto) {
                    cb.isChecked = perm in autoPermissions
                    cb.isEnabled = false
                } else {
                    cb.isEnabled = true
                }
            }
        }

        permAutoRadio?.isChecked = true
        updatePermissionsUI(true)

        permModeGroup?.setOnCheckedChangeListener { _, checkedId ->
            updatePermissionsUI(checkedId == R.id.v3_perm_mode_auto)
        }

        val projectIconFile = listOf(
            File(project.directory, "manual_screenshot.png"),
            File(project.directory, "automatic_screenshot.png")
        ).firstOrNull { it.exists() && it.length() > 0 }
        val hasProjectIcon = projectIconFile != null
        val iconPreview = findV<android.widget.ImageView>(view, "v3_icon_preview_image")
        fun updateIconPreview(useProject: Boolean) {
            if (useProject && projectIconFile != null) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(projectIconFile.absolutePath)
                if (bitmap != null) {
                    iconPreview?.setImageBitmap(bitmap)
                    return
                }
            }
            iconPreview?.setImageResource(R.drawable.logo)
        }
        updateIconPreview(hasProjectIcon)

        view.findViewById<RadioButton>(R.id.v3_icon_project)?.isChecked = hasProjectIcon
        view.findViewById<RadioButton>(R.id.v3_icon_default)?.isChecked = !hasProjectIcon
        view.findViewById<android.widget.RadioGroup>(R.id.v3_icon_radio_group)?.setOnCheckedChangeListener { _, checkedId ->
            updateIconPreview(checkedId == R.id.v3_icon_project)
        }

        val cardFull = findV<View>(view, "v3_card_template_full")
        val cardLight = findV<View>(view, "v3_card_template_light")
        val fullCheck = findV<View>(view, "v3_template_full_check")
        val lightCheck = findV<View>(view, "v3_template_light_check")
        val radioFull = view.findViewById<RadioButton>(R.id.v3_template_full)
        val radioLight = view.findViewById<RadioButton>(R.id.v3_template_light)

        fun selectTemplate(isFull: Boolean) {
            if (isFull) {
                radioFull?.isChecked = true
                cardFull?.setBackgroundResource(R.drawable.bg_v3_card_selected)
                cardLight?.setBackgroundResource(R.drawable.bg_v3_card_selectable)
                fullCheck?.visibility = View.VISIBLE
                lightCheck?.visibility = View.GONE
            } else {
                radioLight?.isChecked = true
                cardLight?.setBackgroundResource(R.drawable.bg_v3_card_selected)
                cardFull?.setBackgroundResource(R.drawable.bg_v3_card_selectable)
                lightCheck?.visibility = View.VISIBLE
                fullCheck?.visibility = View.GONE
            }
        }

        cardFull?.setOnClickListener { selectTemplate(true) }
        cardLight?.setOnClickListener { selectTemplate(false) }
        selectTemplate(true)

        val firebaseAddButton = view.findViewById<Button>(R.id.v3_firebase_add_button)
        val firebaseStatus = view.findViewById<TextView>(R.id.v3_firebase_status)
        val firebaseRemoveButton = view.findViewById<Button>(R.id.v3_firebase_remove_button)

        firebaseAddButton.setOnClickListener {
            firebaseLauncher?.launch(arrayOf("application/json", "*/*"))
        }

        firebaseRemoveButton.setOnClickListener {
            firebaseUri = null
            firebaseConfig = null
            firebaseStatus.visibility = android.view.View.GONE
            firebaseRemoveButton.visibility = android.view.View.GONE
            firebaseAddButton.text = ctx.getString(R.string.v3_firebase_add_button)
        }

        refreshTemplateStatus(view, ctx)

        view.findViewById<Button>(R.id.v3_template_action_button)?.setOnClickListener {
            downloadTemplate(view, ctx)
        }

        findV<View>(view, "v3_btn_close")?.setOnClickListener {
            settingsDialog?.dismiss()
        }
        view.findViewById<Button>(R.id.v3_btn_cancel)?.setOnClickListener {
            settingsDialog?.dismiss()
        }
        view.findViewById<Button>(R.id.v3_btn_back)?.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                updateStepUI(view, ctx)
            }
        }
        view.findViewById<Button>(R.id.v3_btn_next)?.setOnClickListener {
            when (currentStep) {
                1 -> {
                    if (!templateReady) {
                        ToastUtil.showError(ctx, ctx.getString(R.string.v3_template_required))
                        return@setOnClickListener
                    }
                    currentStep = 2
                    updateStepUI(view, ctx)
                }
                2 -> {
                    val pkg = view.findViewById<TextInputEditText>(R.id.v3_package_input)
                        ?.text.toString().ifBlank { "org.neocatroid.runtime.v3" }.lowercase()
                    if (!pkg.matches(Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+$"))) {
                        ToastUtil.showError(ctx, ctx.getString(R.string.v3_invalid_package_name))
                        return@setOnClickListener
                    }
                    currentStep = 3
                    updateStepUI(view, ctx)
                }
                3 -> {
                    currentStep = 4
                    updateStepUI(view, ctx)
                }
                else -> doBuild(host, view, project, permChecks)
            }
        }

        updateStepUI(view, ctx)

        return AlertDialog.Builder(ctx)
            .setView(view)
            .create()
    }

    private fun stepName(ctx: android.content.Context, step: Int): String {
        return when (step) {
            1 -> ctx.getString(R.string.v3_step_template)
            2 -> ctx.getString(R.string.v3_step_app)
            3 -> ctx.getString(R.string.v3_step_permissions)
            else -> ctx.getString(R.string.v3_step_firebase)
        }
    }

    private fun permissionLabelResId(permission: String): Int = when (permission) {
        "android.permission.INTERNET" -> R.string.v3_permission_internet
        "android.permission.ACCESS_NETWORK_STATE" -> R.string.v3_permission_network_state
        "android.permission.ACCESS_WIFI_STATE" -> R.string.v3_permission_wifi_state
        "android.permission.CAMERA" -> R.string.v3_permission_camera
        "android.permission.RECORD_AUDIO" -> R.string.v3_permission_record_audio
        "android.permission.WRITE_EXTERNAL_STORAGE" -> R.string.v3_permission_write_external_storage
        "android.permission.READ_EXTERNAL_STORAGE" -> R.string.v3_permission_read_external_storage
        "android.permission.READ_MEDIA_IMAGES" -> R.string.v3_permission_read_media_images
        "android.permission.READ_MEDIA_AUDIO" -> R.string.v3_permission_read_media_audio
        "android.permission.VIBRATE" -> R.string.v3_permission_vibrate
        "android.permission.WAKE_LOCK" -> R.string.v3_permission_wake_lock
        "android.permission.POST_NOTIFICATIONS" -> R.string.v3_permission_post_notifications
        "android.permission.BLUETOOTH" -> R.string.v3_permission_bluetooth
        "android.permission.BLUETOOTH_ADMIN" -> R.string.v3_permission_bluetooth_admin
        "android.permission.BLUETOOTH_CONNECT" -> R.string.v3_permission_bluetooth_connect
        "android.permission.NFC" -> R.string.v3_permission_nfc
        "android.permission.ACCESS_FINE_LOCATION" -> R.string.v3_permission_fine_location
        "android.permission.ACCESS_COARSE_LOCATION" -> R.string.v3_permission_coarse_location
        else -> R.string.v3_permission_unknown
    }

    private fun updateStepUI(view: android.view.View, ctx: android.content.Context) {
        (view.findViewById<View>(R.id.v3_wizard_root) as? ViewGroup)?.let { root ->
            androidx.transition.TransitionManager.beginDelayedTransition(
                root, androidx.transition.AutoTransition()
            )
        }

        val project = ProjectManager.getInstance().currentProject

        view.findViewById<TextView>(R.id.v3_step_subtitle)?.text =
            ctx.getString(R.string.v3_step_format, currentStep, TOTAL_STEPS, stepName(ctx, currentStep))

        val progressBar = view.findViewById<ProgressBar>(R.id.v3_step_progress)
        if (progressBar != null) {
            val target = currentStep * 100 / TOTAL_STEPS
            android.animation.ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, target)
                .setDuration(300)
                .start()
        }

        val stepCircles = listOf(
            Pair(findV<View>(view, "v3_step1_circle"), Pair(findV<TextView>(view, "v3_step1_num"), findV<View>(view, "v3_step1_done"))),
            Pair(findV<View>(view, "v3_step2_circle"), Pair(findV<TextView>(view, "v3_step2_num"), findV<View>(view, "v3_step2_done"))),
            Pair(findV<View>(view, "v3_step3_circle"), Pair(findV<TextView>(view, "v3_step3_num"), findV<View>(view, "v3_step3_done"))),
            Pair(findV<View>(view, "v3_step4_circle"), Pair(findV<TextView>(view, "v3_step4_num"), findV<View>(view, "v3_step4_done")))
        )
        val stepLines = listOf(
            findV<View>(view, "v3_step_line_1"),
            findV<View>(view, "v3_step_line_2"),
            findV<View>(view, "v3_step_line_3")
        )
        for (i in 1..TOTAL_STEPS) {
            val (circle, pair) = stepCircles.getOrNull(i - 1) ?: continue
            val (numView, doneView) = pair
            when {
                i < currentStep -> {
                    circle?.setBackgroundResource(R.drawable.bg_v3_step_circle_done)
                    numView?.visibility = View.GONE
                    doneView?.visibility = View.VISIBLE
                }
                i == currentStep -> {
                    circle?.setBackgroundResource(R.drawable.bg_v3_step_circle_active)
                    numView?.visibility = View.VISIBLE
                    doneView?.visibility = View.GONE
                }
                else -> {
                    circle?.setBackgroundResource(R.drawable.bg_v3_step_circle)
                    numView?.visibility = View.VISIBLE
                    doneView?.visibility = View.GONE
                }
            }
        }
        for (i in 1..3) {
            val line = stepLines.getOrNull(i - 1) ?: continue
            line?.setBackgroundColor(
                if (i < currentStep) android.graphics.Color.parseColor("#3FB950")
                else android.graphics.Color.parseColor("#383842")
            )
        }

        view.findViewById<View>(R.id.v3_step1_container)?.visibility =
            if (currentStep == 1) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.v3_step2_container)?.visibility =
            if (currentStep == 2) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.v3_step3_container)?.visibility =
            if (currentStep == 3) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.v3_step4_container)?.visibility =
            if (currentStep == 4) View.VISIBLE else View.GONE

        if (currentStep == 4 && project != null) {
            val appName = view.findViewById<TextInputEditText>(R.id.v3_app_name_input)?.text.toString().ifBlank { project.name }
            val pkg = view.findViewById<TextInputEditText>(R.id.v3_package_input)?.text.toString().ifBlank { "org.neocatroid.runtime.v3" }
            val version = view.findViewById<TextInputEditText>(R.id.v3_version_input)?.text.toString().ifBlank { "1.0" }
            val isFull = view.findViewById<RadioButton>(R.id.v3_template_full)?.isChecked == true
            findV<TextView>(view, "v3_summary_app_name")?.text = "$appName  v$version"
            findV<TextView>(view, "v3_summary_package")?.text = "Package: $pkg"
            findV<TextView>(view, "v3_summary_template")?.text =
                if (isFull) ctx.getString(R.string.v3_template_full_title)
                else ctx.getString(R.string.v3_template_light_title)
        }

        view.findViewById<Button>(R.id.v3_btn_back)?.visibility =
            if (currentStep > 1) View.VISIBLE else View.INVISIBLE

        val nextBtn = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.v3_btn_next)
        if (currentStep == TOTAL_STEPS) {
            nextBtn?.text = ctx.getString(R.string.v3_build)
            nextBtn?.setIconResource(R.drawable.ic_v3_rocket)
        } else {
            nextBtn?.text = ctx.getString(R.string.v3_next)
            nextBtn?.icon = null
        }
    }

    private fun formatSizeMb(bytes: Long): String {
        return "${bytes / (1024 * 1024)} MB"
    }

    private fun refreshTemplateStatus(view: android.view.View, ctx: android.content.Context) {
        val status = TemplateManagerV3.getCacheStatus(ctx)
        templateReady = status.cached
        val statusView = view.findViewById<TextView>(R.id.v3_template_status)
        val actionButton = view.findViewById<Button>(R.id.v3_template_action_button)
        if (status.cached) {
            statusView?.text = ctx.getString(R.string.v3_template_status_cached, formatSizeMb(status.sizeBytes))
            actionButton?.text = ctx.getString(R.string.v3_template_refresh)
        } else {
            statusView?.text = ctx.getString(R.string.v3_template_status_none)
            actionButton?.text = ctx.getString(R.string.v3_template_download)
        }
    }

    private fun downloadTemplate(view: android.view.View, ctx: android.content.Context) {
        val statusView = view.findViewById<TextView>(R.id.v3_template_status)
        val actionButton = view.findViewById<Button>(R.id.v3_template_action_button)
        val progressBar = view.findViewById<ProgressBar>(R.id.v3_template_progress)
        val mainHandler = Handler(Looper.getMainLooper())

        val force = templateReady
        actionButton?.isEnabled = false
        progressBar?.visibility = View.VISIBLE
        progressBar?.progress = 0

        val scope = hostFragment?.lifecycleScope
        if (scope == null) {
            actionButton?.isEnabled = true
            progressBar?.visibility = View.GONE
            return
        }

        scope.launch(Dispatchers.IO) {
            val result = TemplateManagerV3.ensureCachedTemplate(ctx, force) { p, _ ->
                mainHandler.post {
                    progressBar?.progress = (p * 100).toInt()
                    statusView?.text = ctx.getString(
                        R.string.v3_template_downloading, (p * 100).toInt()
                    )
                }
            }
            withContext(Dispatchers.Main) {
                actionButton?.isEnabled = true
                progressBar?.visibility = View.GONE
                when (result) {
                    is TemplateManagerV3.TemplateOutcome.Ready -> {
                        templateReady = true
                        val text = if (result.updated) R.string.v3_template_ready
                        else R.string.v3_template_uptodate
                        statusView?.text = ctx.getString(text, formatSizeMb(result.file.length()))
                        actionButton?.text = ctx.getString(R.string.v3_template_refresh)
                    }
                    is TemplateManagerV3.TemplateOutcome.Failed -> {
                        val reason = when (result.failure) {
                            TemplateManagerV3.TemplateFailure.NO_SPACE ->
                                ctx.getString(R.string.v3_template_error_space)
                            TemplateManagerV3.TemplateFailure.NETWORK ->
                                ctx.getString(R.string.v3_template_error_network, result.detail)
                            TemplateManagerV3.TemplateFailure.BAD_FILE ->
                                ctx.getString(R.string.v3_template_error_file)
                        }
                        val kept = result.cachedFile?.takeIf { it.exists() && it.length() > 0 }
                        if (kept != null) {
                            templateReady = true
                            statusView?.text = ctx.getString(
                                R.string.v3_template_status_cached, formatSizeMb(kept.length())
                            )
                            actionButton?.text = ctx.getString(R.string.v3_template_refresh)
                        } else {
                            templateReady = false
                            statusView?.text = ctx.getString(R.string.v3_template_failed)
                            actionButton?.text = ctx.getString(R.string.v3_template_download)
                        }
                        ToastUtil.showError(ctx, reason)
                    }
                }
            }
        }
    }

    private fun doBuild(
        host: Fragment,
        view: android.view.View,
        project: org.catrobat.catroid.content.Project,
        permChecks: List<Pair<String, CheckBox>>
    ) {
        val ctx = host.requireContext()
        val appName = view.findViewById<TextInputEditText>(R.id.v3_app_name_input)?.text.toString()
            .ifBlank { project.name }
        val pkgRaw = view.findViewById<TextInputEditText>(R.id.v3_package_input)?.text.toString()
            .ifBlank { "org.neocatroid.runtime.v3" }
        val pkg = pkgRaw.lowercase()

        if (!pkg.matches(Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+$"))) {
            ToastUtil.showError(ctx, ctx.getString(R.string.v3_invalid_package_name))
            return
        }

        val versionName = view.findViewById<TextInputEditText>(R.id.v3_version_input)?.text.toString()
            .ifBlank { "1.0" }
        val versionCode = view.findViewById<TextInputEditText>(R.id.v3_version_code_input)?.text.toString()
            .toIntOrNull() ?: 1
        val minSdk = view.findViewById<TextInputEditText>(R.id.v3_min_sdk_input)?.text.toString()
            .toIntOrNull() ?: ApkBuilderV3Config.RUNTIME_MIN_SDK
        val targetSdk = view.findViewById<TextInputEditText>(R.id.v3_target_sdk_input)?.text.toString()
            .toIntOrNull() ?: 35

        if (minSdk > targetSdk) {
            ToastUtil.showError(ctx, ctx.getString(R.string.v3_invalid_sdk))
            return
        }

        val templateType = if (view.findViewById<RadioButton>(R.id.v3_template_full)?.isChecked == true)
            TemplateType.FULL else TemplateType.LIGHT

        val permissions = permChecks.filter { it.second.isChecked }.map { it.first }

        val finalFirebaseConfig: FirebaseConfig? = if (firebaseUri != null) {
            val recheck = FirebaseConfigManager.processGoogleServicesJson(ctx, firebaseUri!!, pkg)
            if (recheck.error != null || recheck.config == null) {
                ToastUtil.showError(ctx, recheck.error?.message
                    ?: "Invalid google-services.json")
                return
            }
            recheck.config
        } else null

        val iconFile: File? = if (view.findViewById<RadioButton>(R.id.v3_icon_project)?.isChecked == true) {
            val manual = File(project.directory, "manual_screenshot.png")
            val auto = File(project.directory, "automatic_screenshot.png")
            when {
                manual.exists() -> manual
                auto.exists() -> auto
                else -> null
            }
        } else null

        config = ApkBuilderV3Config(
            appName = appName,
            packageName = pkg,
            versionName = versionName,
            versionCode = versionCode,
            minSdk = minSdk,
            targetSdk = targetSdk,
            iconFile = iconFile,
            permissions = permissions,
            templateType = templateType,
            firebaseConfig = finalFirebaseConfig,
            debuggable = view.findViewById<CheckBox>(R.id.v3_debuggable_checkbox)?.isChecked ?: true
        )

        settingsDialog?.dismiss()
        startBuild(host)
    }

    private fun startBuild(host: Fragment) {
        val ctx = host.requireContext()
        val cfg = config ?: return
        val projDir = projectDir ?: return

        val progressView = LayoutInflater.from(ctx).inflate(R.layout.activity_apk_builder_v3_progress, null)
        val progressBar = progressView.findViewById<ProgressBar>(R.id.v3_build_progress_bar)
        val percentText = progressView.findViewById<TextView>(R.id.v3_progress_percent)
        val stageText = progressView.findViewById<TextView>(R.id.v3_progress_stage)
        val fileText = progressView.findViewById<TextView>(R.id.v3_progress_file)
        val factText = progressView.findViewById<TextView>(R.id.v3_build_fact)

        val buildDialog = AlertDialog.Builder(ctx)
            .setView(progressView)
            .setCancelable(false)
            .create()

        val mainHandler = Handler(Looper.getMainLooper())

        val facts = ctx.resources.getStringArray(R.array.loading_facts).toList()
        var factIndex = if (facts.isNotEmpty()) (facts.indices).random() else 0
        val factRunnable = object : Runnable {
            override fun run() {
                if (!buildDialog.isShowing) return
                if (facts.isNotEmpty()) {
                    factText.text = ctx.getString(R.string.v3_fun_fact_prefix, facts[factIndex % facts.size])
                    factIndex++
                } else {
                    factText.text = ctx.getString(R.string.loading_fact_placeholder)
                }
                mainHandler.postDelayed(this, 10_000)
            }
        }
        buildDialog.setOnDismissListener { mainHandler.removeCallbacks(factRunnable) }
        buildDialog.show()
        mainHandler.post(factRunnable)

        host.lifecycleScope.launch(Dispatchers.IO) {
            try {
                ProjectManager.getInstance().currentProject?.let {
                    saveProjectSerial(it, ctx)
                }

                val result = ApkBuilderV3Engine.build(
                    context = ctx,
                    projectDir = projDir,
                    config = cfg,
                    listener = BuildProgressListener { progress, stage, currentFile ->
                        mainHandler.post {
                            progressBar.progress = progress.toInt()
                            percentText.text = "${progress.toInt()}%"
                            stageText.text = stage
                            fileText.text = if (currentFile.isNotBlank()) {
                                ctx.getString(R.string.v3_current_file, currentFile)
                            } else ""
                        }
                    }
                )

                withContext(Dispatchers.Main) {
                    buildDialog.dismiss()
                    when (result) {
                        is AssemblyResult.Success -> {
                            saveApkToDownloads(ctx, result.apkFile)
                            Log.i(tag, "Build successful: ${result.apkFile.absolutePath} " +
                                    "(${result.totalSizeBytes / (1024 * 1024)} MB, ${result.templateType})")
                        }
                        is AssemblyResult.Failure -> {
                            ToastUtil.showError(ctx, "Build failed: ${result.message}")
                            Log.e(tag, "Build failed: ${result.message}", result.cause)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    buildDialog.dismiss()
                    ToastUtil.showError(ctx, "Build error: ${e.message}")
                    Log.e(tag, "Build error", e)
                }
            }
        }
    }

    private fun saveApkToDownloads(context: android.content.Context, apkFile: File) {
        val apkDir = "NeoCatroidV3"
        val fileName = apkFile.name
        try {
            if (!apkFile.exists() || apkFile.length() < 1024) {
                ToastUtil.showError(context, "Built APK is empty, not saving")
                Log.e(tag, "Refusing to save empty APK: ${apkFile.absolutePath}")
                return
            }
            try {
                java.util.zip.ZipFile(apkFile).use {
                    if (it.getEntry("AndroidManifest.xml") == null) {
                        ToastUtil.showError(context, "Built APK is corrupted (no manifest), not saving")
                        Log.e(tag, "Refusing to save APK without AndroidManifest.xml")
                        return
                    }
                }
            } catch (e: Exception) {
                ToastUtil.showError(context, "Built APK is corrupted: ${e.message}")
                Log.e(tag, "Refusing to save unreadable APK", e)
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.android.package-archive")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$apkDir")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val uri = context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues
                )
                if (uri != null) {
                    try {
                        var copied = 0L
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            FileInputStream(apkFile).use { copied = it.copyTo(out) }
                        }
                        if (copied != apkFile.length()) {
                            Log.e(tag, "APK copy truncated: copied=$copied expected=${apkFile.length()}")
                            runCatching { context.contentResolver.delete(uri, null, null) }
                            ToastUtil.showError(context, "Failed to save APK (copy truncated)")
                            return
                        }
                        val done = android.content.ContentValues().apply {
                            put(MediaStore.MediaColumns.IS_PENDING, 0)
                        }
                        context.contentResolver.update(uri, done, null, null)
                        Log.i(tag, "APK saved to Downloads/$apkDir/$fileName (${copied / (1024 * 1024)} MB)")
                        ToastUtil.showSuccess(context, "APK saved to Downloads/$apkDir/")
                    } catch (e: Exception) {
                        runCatching { context.contentResolver.delete(uri, null, null) }
                        throw e
                    }
                } else {
                    ToastUtil.showError(context, "Failed to save APK")
                }
            } else {
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    apkDir
                )
                dir.mkdirs()
                val dest = File(dir, fileName)
                apkFile.copyTo(dest, true)
                if (dest.length() != apkFile.length()) {
                    dest.delete()
                    ToastUtil.showError(context, "Failed to save APK (copy truncated)")
                    return
                }
                ToastUtil.showSuccess(context, "APK saved: ${dest.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to save APK", e)
            ToastUtil.showError(context, "Error saving APK: ${e.message}")
        }
    }
}
