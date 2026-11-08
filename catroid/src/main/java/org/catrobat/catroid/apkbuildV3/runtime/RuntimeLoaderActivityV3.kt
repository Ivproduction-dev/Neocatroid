package org.catrobat.catroid.apkbuildV3.runtime

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import org.catrobat.catroid.R
import org.catrobat.catroid.apkbuildV3.TemplateType
import org.catrobat.catroid.apkbuildV3.neoui.NeoUiLayout
import org.catrobat.catroid.apkbuildV3.neoui.NeoUiRuntimeView
import org.catrobat.catroid.stage.StageActivity
import java.io.File

class RuntimeLoaderActivityV3 : Activity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var factText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var templateType: TemplateType = TemplateType.LIGHT
    private var projectPath: String? = null
    private var isNoUi = false
    private var customLayout: NeoUiLayout? = null
    private var customRuntimeView: NeoUiRuntimeView? = null

    companion object {
        private const val TAG = "RuntimeLoaderV3"
        private const val EXTRA_TEMPLATE_TYPE = "template_type"

        private val FUN_FACTS = arrayOf(
            "The first Android phone was released in 2008.",
            "Catrobat was inspired by Scratch from MIT.",
            "NeoCatroid supports over 390 different brick types.",
            "The first version of Android had no copy-paste support.",
            "Android is based on the Linux kernel.",
            "The Android robot logo was created by Irina Blok.",
            "There are over 2.5 billion active Android devices worldwide.",
            "Visual programming helps children learn logic and creativity."
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_runtime_loader_v3)

        progressBar = findViewById(R.id.v3_progress_bar)
        statusText = findViewById(R.id.v3_status_text)
        factText = findViewById(R.id.v3_fact_text)

        templateType = detectTemplateType(assets)
        setupCustomLoadingUi()

        Thread {
            try {
                var detail: String? = null
                val success = when (templateType) {
                    TemplateType.FULL -> loadFullTemplate().also {
                        if (!it) detail = fullLastError
                    }
                    TemplateType.LIGHT -> loadLightTemplate().also {
                        if (!it) detail = lightLastError
                    }
                }

                handler.post {
                    if (success) {
                        startProject()
                    } else {
                        customRuntimeView?.errorText = getString(R.string.v3_load_failed)
                        statusText.visibility = View.VISIBLE
                        statusText.text = if (detail != null) {
                            "${getString(R.string.v3_load_failed)}: $detail"
                        } else {
                            getString(R.string.v3_load_failed)
                        }
                    }
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Loading failed", t)
                handler.post {
                    customRuntimeView?.errorText = getString(R.string.v3_load_failed)
                    statusText.visibility = View.VISIBLE
                    statusText.text = "${getString(R.string.v3_load_failed)}: ${t.javaClass.simpleName}: ${t.message}"
                }
            }
        }.start()
    }

    private fun setupCustomLoadingUi() {
        val root = findViewById<android.widget.FrameLayout>(R.id.v3_runtime_root)
        val defaultLoader = findViewById<View>(R.id.v3_default_loader)
        val title = findViewById<TextView>(R.id.v3_runtime_title)

        isNoUi = runCatching {
            assets.open("neoui/no_ui.marker").use { true }
        }.getOrDefault(false)

        if (isNoUi) {
            root.setBackgroundColor(Color.BLACK)
            defaultLoader.visibility = View.GONE
            title?.visibility = View.GONE
            progressBar.visibility = View.GONE
            statusText.visibility = View.GONE
            factText.visibility = View.GONE
            return
        }

        customLayout = runCatching {
            assets.open("neoui/layout.json").use { input ->
                val bytes = input.readBounded(1024 * 1024)
                NeoUiLayout.fromJsonString(bytes.toString(Charsets.UTF_8)).also { it.requireValid() }
            }
        }.getOrNull()

        val layout = customLayout
        if (layout != null) {
            val bgBmp = runCatching {
                assets.open("neoui/background.png").use { input ->
                    val bytes = input.readBounded(20 * 1024 * 1024)
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
                    require(bounds.outWidth in 1..8192 && bounds.outHeight in 1..8192 && bounds.outWidth.toLong() * bounds.outHeight <= 32_000_000)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = 2 })
                }
            }.getOrNull()
            customRuntimeView = NeoUiRuntimeView(this, layout, bgBmp)
            defaultLoader.visibility = View.GONE
            root.addView(customRuntimeView, android.widget.FrameLayout.LayoutParams(-1, -1))
        } else {
            statusText.text = getString(R.string.v3_loading)
            showRandomFact()
        }
    }

    private fun detectTemplateType(assets: AssetManager): TemplateType {
        try {
            val files = assets.list("") ?: return TemplateType.LIGHT
            val hasFullMarker = files.any { it == "template_v3_full.marker" }
            return if (hasFullMarker) TemplateType.FULL else TemplateType.LIGHT
        } catch (e: Exception) {
            return TemplateType.LIGHT
        }
    }

    private fun java.io.InputStream.readBounded(limit: Int): ByteArray {
        val output = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        var total = 0
        while (true) {
            val count = read(buffer)
            if (count < 0) break
            total += count
            require(total <= limit)
            output.write(buffer, 0, count)
        }
        return output.toByteArray()
    }

    @Volatile
    private var fullLastError: String? = null

    @Volatile
    private var lightLastError: String? = null

    private fun loadFullTemplate(): Boolean {
        val cacheDir = File(cacheDir, "v3_project_full").apply {
            deleteRecursively()
            mkdirs()
        }

        val strategy = FullTemplateStrategy(this)
        val success = strategy.load(cacheDir) { progress ->
            updateProgress(progress, when {
                progress < 0.2f -> getString(R.string.v3_loading)
                progress < 0.4f -> getString(R.string.v3_decrypting)
                progress < 0.6f -> getString(R.string.v3_extracting)
                progress < 0.8f -> getString(R.string.v3_initializing)
                else -> getString(R.string.v3_starting)
            })
        }

        if (success) {
            projectPath = File(cacheDir, "project_extracted").absolutePath
        } else {
            fullLastError = strategy.lastError
        }
        return success
    }

    private fun loadLightTemplate(): Boolean {
        val cacheDir = File(cacheDir, "v3_project_light").apply {
            deleteRecursively()
            mkdirs()
        }

        val strategy = LightTemplateStrategy(this)
        val success = strategy.initialize(cacheDir) { progress ->
            updateProgress(progress, when {
                progress < 0.2f -> getString(R.string.v3_loading)
                progress < 0.4f -> getString(R.string.v3_decrypting_meta)
                progress < 0.6f -> getString(R.string.v3_preparing)
                progress < 0.8f -> getString(R.string.v3_initializing)
                else -> getString(R.string.v3_starting)
            })
        }

        if (success) {
            projectPath = File(cacheDir, "project_light").absolutePath
        } else {
            lightLastError = strategy.lastError
        }
        return success
    }

    private fun updateProgress(progress: Float, stage: String) {
        if (isNoUi) return
        handler.post {
            customRuntimeView?.progress = (progress * 100).toInt()
            progressBar.progress = (progress * 100).toInt()
            if (customLayout == null) {
                statusText.text = stage
                if (Math.random() < 0.3) {
                    showRandomFact()
                }
            }
        }
    }

    private fun showRandomFact() {
        val fact = FUN_FACTS[java.util.concurrent.ThreadLocalRandom.current().nextInt(FUN_FACTS.size)]
        factText.text = getString(R.string.v3_fun_fact_prefix, fact)
    }

    private fun startProject() {
        val stageIntent = Intent(this, StageActivity::class.java).apply {
            putExtra(StageActivity.EXTRA_PROJECT_PATH, projectPath)
            putExtra(StageActivity.EXTRA_PROJECT_PRELOADED, true)
            putExtra("V3_TEMPLATE_TYPE", templateType.name)
        }
        startActivity(stageIntent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
