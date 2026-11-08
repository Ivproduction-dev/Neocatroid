package org.catrobat.catroid.apkbuildV3.neoui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.View
import kotlin.math.min

class NeoUiRuntimeView @JvmOverloads constructor(
    context: Context,
    private val layout: NeoUiLayout,
    private val bitmap: Bitmap?,
    private val fixedAspect: Boolean = true
) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val frame = RectF()
    var progress: Int = 0
        set(value) {
            field = value.coerceIn(0, 100)
            invalidate()
        }
    var errorText: String? = null
        set(value) {
            field = value
            invalidate()
        }
    var statusText: String = ""
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width.toFloat()
        val height = height.toFloat()
        if (fixedAspect) {
            val aspect = 9f / 16f
            val frameWidth = min(width, height * aspect)
            val frameHeight = frameWidth / aspect
            frame.set((width - frameWidth) / 2f, (height - frameHeight) / 2f, (width + frameWidth) / 2f, (height + frameHeight) / 2f)
        } else {
            frame.set(0f, 0f, width, height)
        }
        canvas.drawColor(Color.BLACK)
        if (bitmap != null && !bitmap.isRecycled) {
            canvas.drawBitmap(bitmap, null, frame, paint)
        } else {
            paint.color = color(layout.background.color, Color.rgb(26, 26, 46))
            canvas.drawRect(frame, paint)
        }
        layout.texts.forEach { text ->
            paint.color = color(text.color, Color.WHITE)
            paint.textSize = text.fontSize.coerceIn(8f, 96f) * resources.displayMetrics.scaledDensity
            paint.textAlign = Paint.Align.CENTER
            val x = frame.left + (text.x + text.width / 2f) * frame.width()
            val y = frame.top + (text.y + text.height / 2f) * frame.height() - (paint.descent() + paint.ascent()) / 2f
            canvas.save()
            canvas.rotate(text.rotation, x, y)
            canvas.clipRect(frame.left + text.x * frame.width(), frame.top + text.y * frame.height(), frame.left + (text.x + text.width) * frame.width(), frame.top + (text.y + text.height) * frame.height())
            val displayText = text.content
                .replace("{status}", statusText)
            paint.typeface = when (text.fontFamily.lowercase()) {
                "sans-serif" -> Typeface.create("sans-serif", Typeface.NORMAL)
                "serif" -> Typeface.create("serif", Typeface.NORMAL)
                "monospace" -> Typeface.create("monospace", Typeface.NORMAL)
                "sans-serif-condensed" -> Typeface.create("sans-serif-condensed", Typeface.NORMAL)
                else -> Typeface.DEFAULT
            }
            canvas.drawText(displayText, x, y, paint)
            canvas.restore()
        }
        if (layout.progressBar.visible) {
            val bar = layout.progressBar
            val rect = RectF(frame.left + bar.x * frame.width(), frame.top + bar.y * frame.height(), frame.left + (bar.x + bar.width) * frame.width(), frame.top + (bar.y + bar.height) * frame.height())
            paint.color = 0x4DFFFFFF
            canvas.drawRoundRect(rect, rect.height() / 2f, rect.height() / 2f, paint)
            paint.color = color(bar.color, Color.RED)
            rect.right = rect.left + rect.width() * progress / 100f
            canvas.drawRoundRect(rect, rect.height() / 2f, rect.height() / 2f, paint)
        }
        errorText?.let {
            paint.color = Color.WHITE
            paint.textSize = 14f * resources.displayMetrics.scaledDensity
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(it.take(240), frame.centerX(), frame.bottom - 24f * resources.displayMetrics.density, paint)
        }
    }

    private fun color(value: String, fallback: Int): Int = runCatching { Color.parseColor(value) }.getOrDefault(fallback)
}
