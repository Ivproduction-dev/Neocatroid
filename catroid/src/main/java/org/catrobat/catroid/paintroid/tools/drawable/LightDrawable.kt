package org.catrobat.catroid.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader

class LightDrawable(
    val type: Type,
    var softness: Int = 60,
    var coreSize: Int = 20
) : ShapeDrawable {
    enum class Type { POINT, AURA, SPOT }

    private val spotPath = Path()
    private val shaderMatrix = Matrix()

    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        val w = shapeRect.width()
        val h = shapeRect.height()
        if (w <= 0f || h <= 0f) return

        val baseColor = drawPaint.color
        val r = Color.red(baseColor)
        val g = Color.green(baseColor)
        val b = Color.blue(baseColor)
        val alpha = drawPaint.alpha

        val core = (coreSize / 100f).coerceIn(0f, 0.8f)
        val soft = (softness / 100f).coerceIn(0.1f, 1.0f)

        val (colors, stops) = when (type) {
            Type.POINT -> {
                val bright = Color.argb(alpha, r, g, b)
                val midAlpha = (alpha * soft * 0.65f).toInt().coerceIn(0, 255)
                val midColor = Color.argb(midAlpha, r, g, b)
                val clear = Color.TRANSPARENT
                val midStop = core + (1f - core) * 0.5f
                Pair(
                    intArrayOf(bright, bright, midColor, clear),
                    floatArrayOf(0f, core, midStop, 1f)
                )
            }
            Type.AURA -> {
                val auraAlpha = (alpha * 0.75f).toInt().coerceIn(0, 255)
                val auraMidAlpha = (alpha * 0.35f * soft).toInt().coerceIn(0, 255)
                val clear = Color.TRANSPARENT
                Pair(
                    intArrayOf(
                        Color.argb(auraAlpha, r, g, b),
                        Color.argb((auraAlpha * 0.9f).toInt(), r, g, b),
                        Color.argb(auraMidAlpha, r, g, b),
                        clear
                    ),
                    floatArrayOf(0f, core * 0.4f, 0.55f, 1f)
                )
            }
            Type.SPOT -> {
                val bright = Color.argb(alpha, r, g, b)
                val midAlpha = (alpha * soft * 0.6f).toInt().coerceIn(0, 255)
                val midColor = Color.argb(midAlpha, r, g, b)
                val clear = Color.TRANSPARENT
                val midStop = core + (1f - core) * 0.55f
                Pair(
                    intArrayOf(bright, bright, midColor, clear),
                    floatArrayOf(0f, core, midStop, 1f)
                )
            }
        }

        val centerX = shapeRect.centerX()
        val centerY = when (type) {
            Type.SPOT -> shapeRect.top
            Type.POINT, Type.AURA -> shapeRect.centerY()
        }

        val rx = w / 2f
        val ry = if (type == Type.SPOT) h else h / 2f
        if (rx <= 0f || ry <= 0f) return

        val baseRadius = 100f
        val gradient = RadialGradient(centerX, centerY, baseRadius, colors, stops, Shader.TileMode.CLAMP)
        shaderMatrix.reset()
        shaderMatrix.setScale(rx / baseRadius, ry / baseRadius, centerX, centerY)
        gradient.setLocalMatrix(shaderMatrix)

        val paint = Paint(drawPaint).apply {
            style = Paint.Style.FILL
            this.alpha = 255
            shader = gradient
        }

        if (type == Type.SPOT) {
            spotPath.reset()
            spotPath.moveTo(centerX, shapeRect.top)
            spotPath.lineTo(shapeRect.right, shapeRect.bottom)
            spotPath.lineTo(shapeRect.left, shapeRect.bottom)
            spotPath.close()
            canvas.save()
            canvas.clipPath(spotPath)
            canvas.drawRect(shapeRect, paint)
            canvas.restore()
        } else {
            canvas.drawOval(shapeRect, paint)
        }
    }
}
