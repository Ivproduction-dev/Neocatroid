package org.catrobat.catroid.paintroid.ui.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import org.catrobat.catroid.R
import org.catrobat.catroid.paintroid.tools.drawable.DrawableShape
import org.catrobat.catroid.paintroid.tools.drawable.DrawableStyle
import org.catrobat.catroid.paintroid.tools.options.ShapeToolOptionsView

class DefaultLight2DToolOptionsView(rootView: ViewGroup) : ShapeToolOptionsView {
    private val content: View = LayoutInflater.from(rootView.context)
        .inflate(R.layout.dialog_pocketpaint_light2d, rootView)
    private val typeGroup: RadioGroup = content.findViewById(R.id.light_2d_type_group)
    private val intensity: SeekBar = content.findViewById(R.id.light_2d_intensity)
    private val intensityLabel: TextView = content.findViewById(R.id.light_2d_intensity_label)
    private val softness: SeekBar = content.findViewById(R.id.light_2d_softness)
    private val softnessLabel: TextView = content.findViewById(R.id.light_2d_softness_label)
    private val core: SeekBar = content.findViewById(R.id.light_2d_core)
    private val coreLabel: TextView = content.findViewById(R.id.light_2d_core_label)
    private val glowCheckBox: CheckBox = content.findViewById(R.id.light_2d_glow)
    private val resetRatioButton: View = content.findViewById(R.id.light_2d_reset_ratio)
    private val sizeLabel: TextView = content.findViewById(R.id.light_2d_size)
    private var callback: ShapeToolOptionsView.Callback? = null

    init {
        typeGroup.setOnCheckedChangeListener { _, checkedId ->
            callback?.setToolType(
                when (checkedId) {
                    R.id.light_2d_aura -> DrawableShape.AURA_LIGHT
                    R.id.light_2d_spot -> DrawableShape.SPOT_LIGHT
                    else -> DrawableShape.POINT_LIGHT
                }
            )
        }

        intensity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val value = progress.coerceAtLeast(1)
                if (fromUser && progress == 0) seekBar.progress = value
                intensityLabel.text = content.context.getString(R.string.light_2d_intensity_format, value)
                if (fromUser) callback?.setLightIntensity(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        softness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val value = progress.coerceAtLeast(10)
                if (fromUser && progress < 10) seekBar.progress = value
                softnessLabel.text = content.context.getString(R.string.light_2d_softness_format, value)
                if (fromUser) callback?.setLightSoftness(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        core.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val value = progress.coerceIn(0, 80)
                coreLabel.text = content.context.getString(R.string.light_2d_core_format, value)
                if (fromUser) callback?.setLightCoreSize(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        glowCheckBox.setOnCheckedChangeListener { _, isChecked ->
            callback?.setLightGlowEnabled(isChecked)
        }

        resetRatioButton.setOnClickListener {
            callback?.resetSquareAspectRatio()
        }

        setLightIntensity(75)
        setLightSoftness(60)
        setLightCoreSize(20)
        setLightGlowEnabled(true)
    }

    override fun setShapeActivated(shape: DrawableShape) {
        typeGroup.check(
            when (shape) {
                DrawableShape.AURA_LIGHT -> R.id.light_2d_aura
                DrawableShape.SPOT_LIGHT -> R.id.light_2d_spot
                else -> R.id.light_2d_point
            }
        )
    }

    override fun setDrawTypeActivated(drawType: DrawableStyle) = Unit
    override fun setShapeOutlineWidth(outlineWidth: Int) = Unit

    override fun setLightIntensity(intensity: Int) {
        this.intensity.progress = intensity.coerceIn(1, 100)
        intensityLabel.text = content.context.getString(R.string.light_2d_intensity_format, this.intensity.progress)
    }

    override fun setLightSoftness(softness: Int) {
        this.softness.progress = softness.coerceIn(10, 100)
        softnessLabel.text = content.context.getString(R.string.light_2d_softness_format, this.softness.progress)
    }

    override fun setLightCoreSize(coreSize: Int) {
        this.core.progress = coreSize.coerceIn(0, 80)
        coreLabel.text = content.context.getString(R.string.light_2d_core_format, this.core.progress)
    }

    override fun setLightGlowEnabled(enabled: Boolean) {
        glowCheckBox.isChecked = enabled
    }

    override fun setCallback(callback: ShapeToolOptionsView.Callback) {
        this.callback = callback
    }

    override fun setShapeSizeText(shapeSize: String) {
        sizeLabel.text = content.context.getString(R.string.light_2d_resize_hint, shapeSize)
    }

    override fun toggleShapeSizeVisibility(isVisible: Boolean) {
        sizeLabel.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun getShapeToolOptionsLayout(): View = content
}
