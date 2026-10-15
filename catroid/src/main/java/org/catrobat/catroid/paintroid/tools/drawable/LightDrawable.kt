package org.catrobat.catroid.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import kotlin.math.max

class LightDrawable(val type: Type) : ShapeDrawable {
    enum class Type { POINT, AURA, SPOT }

    private val spotPath = Path()

    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        val radius = max(shapeRect.width(), shapeRect.height()) / 2f
        if (radius <= 0f) return
        val baseColor = drawPaint.color
        val bright = Color.argb(drawPaint.alpha, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
        val soft = Color.argb(drawPaint.alpha / 2, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
        val clear = Color.TRANSPARENT
        val centerX = shapeRect.centerX()
        val centerY = when (type) {
            Type.SPOT -> shapeRect.top
            Type.POINT, Type.AURA -> shapeRect.centerY()
        }
        val colors = when (type) {
            Type.POINT -> intArrayOf(bright, soft, clear)
            Type.AURA -> intArrayOf(soft, soft, clear)
            Type.SPOT -> intArrayOf(bright, soft, clear)
        }
        val stops = when (type) {
            Type.POINT -> floatArrayOf(0f, 0.45f, 1f)
            Type.AURA -> floatArrayOf(0f, 0.18f, 1f)
            Type.SPOT -> floatArrayOf(0f, 0.55f, 1f)
        }
        val paint = Paint(drawPaint).apply {
            style = Paint.Style.FILL
            alpha = 255
            shader = RadialGradient(centerX, centerY, radius, colors, stops, Shader.TileMode.CLAMP)
        }
        if (type == Type.SPOT) {
            spotPath.reset()
            spotPath.moveTo(centerX, shapeRect.top)
            spotPath.lineTo(shapeRect.right, shapeRect.bottom)
            spotPath.lineTo(shapeRect.left, shapeRect.bottom)
            spotPath.close()
            canvas.save()
            canvas.clipPath(spotPath)
        }
        canvas.drawRect(shapeRect, paint)
        if (type == Type.SPOT) canvas.restore()
    }
}
