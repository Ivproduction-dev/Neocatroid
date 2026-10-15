package org.catrobat.catroid.paintroid.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

class TriangleDrawable : ShapeDrawable {
    private val path = Path()
    private val paint = Paint()

    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        paint.set(drawPaint)
        path.run {
            reset()
            moveTo(shapeRect.centerX(), shapeRect.top)
            lineTo(shapeRect.right, shapeRect.bottom)
            lineTo(shapeRect.left, shapeRect.bottom)
            close()
        }
        canvas.drawPath(path, paint)
    }
}
