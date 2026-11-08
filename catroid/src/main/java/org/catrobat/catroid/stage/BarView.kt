package org.catrobat.catroid.stage

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.View

class BarView(context: Context, private val state: BarState) : View(context) {

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = state.trackColor }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = state.fillColor }
    private val handler = Handler(Looper.getMainLooper())
    private val cornerRadius = 6f * context.resources.displayMetrics.density

    private val ticker = object : Runnable {
        override fun run() {
            invalidate()
            handler.postDelayed(this, POLL_INTERVAL_MS)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (state.autoUpdate) {
            handler.removeCallbacks(ticker)
            handler.post(ticker)
        }
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(ticker)
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        canvas.drawRoundRect(0f, 0f, w, h, cornerRadius, cornerRadius, trackPaint)
        val fraction = state.fraction()
        if (fraction > 0f) {
            val fillW = (w * fraction).coerceAtLeast(h)
            canvas.drawRoundRect(0f, 0f, fillW.coerceAtMost(w), h, cornerRadius, cornerRadius, fillPaint)
        }
    }

    companion object {
        private const val POLL_INTERVAL_MS = 100L
    }
}
