package org.catrobat.catroid.apkbuildV3.neoui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

class NeoUiCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class SelectedType {
        NONE,
        PROGRESS_BAR,
        TEXT
    }

    enum class HandleType {
        NONE,
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        RIGHT,
        BOTTOM_RIGHT,
        BOTTOM,
        BOTTOM_LEFT,
        LEFT,
        ROTATION
    }

    var layoutData: NeoUiLayout = NeoUiLayout.createDefault()
        set(value) {
            field = value
            selectedType = SelectedType.NONE
            selectedTextIndex = -1
            invalidate()
        }

    var backgroundBitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    var onTextDoubleTapped: ((NeoUiText) -> Unit)? = null
    var onTextLongPressed: ((NeoUiText) -> Unit)? = null
    var onProgressBarDoubleTapped: (() -> Unit)? = null
    var onProgressBarLongPressed: (() -> Unit)? = null
    var onCanvasLongPressed: ((Float, Float) -> Unit)? = null
    var onFrameLongPressed: (() -> Unit)? = null
    var onSelectionChanged: ((SelectedType, Int) -> Unit)? = null

    private var panX = 0f
    private var panY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f

    private var isDraggingElement = false
    private var isResizingElement = false
    private var isRotatingElement = false
    private var isPanningCanvas = false

    var selectedType = SelectedType.NONE
        private set
    var selectedTextIndex = -1
        private set
    private var activeHandle = HandleType.NONE

    private val density = resources.displayMetrics.density
    private val frameWidthDp = 280f
    private val frameHeightDp = frameWidthDp * (16f / 9f)

    private val handleSizePx = 9f * density
    private val cornerRadiusPx = 4f * density
    private val rotationOffsetPx = 24f * density

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#16171E")
        strokeWidth = 1f * density
    }

    private val frameBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2A2D3A")
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * density
    }

    private val frameBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val notchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1B1C24")
        style = Paint.Style.FILL
    }

    private val progressBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#26FFFFFF")
        style = Paint.Style.FILL
    }

    private val progressFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val selectionBoxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3B82F6")
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * density
        pathEffect = DashPathEffect(floatArrayOf(4f * density, 3f * density), 0f)
    }

    private val handleFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val handleBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E212B")
        style = Paint.Style.STROKE
        strokeWidth = 1.2f * density
    }

    private val rotationLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 1.2f * density
    }

    private val frameRect = RectF()
    private val clipPath = Path()
    private val textBounds = Rect()

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val phoneRect = getFrameRect()
            val textIdx = findTextAtPoint(e.x, e.y, phoneRect)
            if (textIdx != -1) {
                selectedType = SelectedType.TEXT
                selectedTextIndex = textIdx
                onSelectionChanged?.invoke(selectedType, selectedTextIndex)
                onTextDoubleTapped?.invoke(layoutData.texts[textIdx])
                invalidate()
                return true
            }
            if (layoutData.progressBar.visible && getProgressBarTouchRect(phoneRect).contains(e.x, e.y)) {
                selectedType = SelectedType.PROGRESS_BAR
                selectedTextIndex = -1
                onSelectionChanged?.invoke(selectedType, selectedTextIndex)
                onProgressBarDoubleTapped?.invoke()
                invalidate()
                return true
            }
            return false
        }

        override fun onLongPress(e: MotionEvent) {
            val phoneRect = getFrameRect()
            val textIdx = findTextAtPoint(e.x, e.y, phoneRect)

            if (textIdx != -1) {
                selectedType = SelectedType.TEXT
                selectedTextIndex = textIdx
                onSelectionChanged?.invoke(selectedType, selectedTextIndex)
                onTextLongPressed?.invoke(layoutData.texts[textIdx])
                invalidate()
                return
            }

            val pbRect = getProgressBarTouchRect(phoneRect)
            if (pbRect.contains(e.x, e.y)) {
                selectedType = SelectedType.PROGRESS_BAR
                selectedTextIndex = -1
                onSelectionChanged?.invoke(selectedType, selectedTextIndex)
                onProgressBarLongPressed?.invoke()
                invalidate()
                return
            }

            if (phoneRect.contains(e.x, e.y)) {
                onFrameLongPressed?.invoke()
            } else {
                val fw = frameWidthDp * density
                val fh = frameHeightDp * density
                val normX = (e.x - phoneRect.left) / fw
                val normY = (e.y - phoneRect.top) / fh
                onCanvasLongPressed?.invoke(normX, normY)
            }
        }
    })

    fun resetView() {
        panX = 0f
        panY = 0f
        selectedType = SelectedType.NONE
        selectedTextIndex = -1
        invalidate()
    }

    fun deleteSelected() {
        if (selectedType == SelectedType.TEXT && selectedTextIndex in layoutData.texts.indices) {
            layoutData.texts.removeAt(selectedTextIndex)
            selectedType = SelectedType.NONE
            selectedTextIndex = -1
            onSelectionChanged?.invoke(selectedType, selectedTextIndex)
            invalidate()
        } else if (selectedType == SelectedType.PROGRESS_BAR) {
            layoutData.progressBar.visible = false
            selectedType = SelectedType.NONE
            onSelectionChanged?.invoke(selectedType, selectedTextIndex)
            invalidate()
        }
    }

    fun deleteText(text: NeoUiText) {
        val index = layoutData.texts.indexOfFirst { it === text }
        if (index == -1) return
        layoutData.texts.removeAt(index)
        selectedType = SelectedType.NONE
        selectedTextIndex = -1
        onSelectionChanged?.invoke(selectedType, selectedTextIndex)
        invalidate()
    }

    private fun getFrameRect(): RectF {
        val fw = frameWidthDp * density
        val fh = frameHeightDp * density
        val left = (width / 2f) + panX - (fw / 2f)
        val top = (height / 2f) + panY - (fh / 2f)
        frameRect.set(left, top, left + fw, top + fh)
        return frameRect
    }

    private fun getProgressBarRect(phoneRect: RectF): RectF {
        val fw = phoneRect.width()
        val fh = phoneRect.height()
        val pb = layoutData.progressBar
        val left = phoneRect.left + pb.x * fw
        val top = phoneRect.top + pb.y * fh
        val right = left + pb.width * fw
        val bottom = top + pb.height * fh
        return RectF(left, top, right, bottom)
    }

    private fun getProgressBarTouchRect(phoneRect: RectF): RectF {
        val rect = getProgressBarRect(phoneRect)
        val minTouchSize = 36f * density
        val horizontalPadding = max(0f, (minTouchSize - rect.width()) / 2f)
        val verticalPadding = max(0f, (minTouchSize - rect.height()) / 2f)
        return RectF(rect.left - horizontalPadding, rect.top - verticalPadding, rect.right + horizontalPadding, rect.bottom + verticalPadding)
    }

    private fun getTextRect(phoneRect: RectF, text: NeoUiText): RectF {
        val fw = phoneRect.width()
        val fh = phoneRect.height()
        val left = phoneRect.left + text.x * fw
        val top = phoneRect.top + text.y * fh
        val right = left + text.width * fw
        val bottom = top + text.height * fh
        return RectF(left, top, right, bottom)
    }

    private fun rotatePoint(x: Float, y: Float, cx: Float, cy: Float, degrees: Float): PointF {
        if (degrees == 0f) return PointF(x, y)
        val rad = Math.toRadians(degrees.toDouble())
        val dx = (x - cx).toDouble()
        val dy = (y - cy).toDouble()
        val nx = dx * cos(rad) - dy * sin(rad)
        val ny = dx * sin(rad) + dy * cos(rad)
        return PointF((cx + nx).toFloat(), (cy + ny).toFloat())
    }

    private fun findTextAtPoint(x: Float, y: Float, phoneRect: RectF): Int {
        for (i in layoutData.texts.indices.reversed()) {
            val text = layoutData.texts[i]
            val rect = getTextRect(phoneRect, text)
            val point = rotatePoint(x, y, rect.centerX(), rect.centerY(), -text.rotation)
            val touchPadding = 8f * density
            if (RectF(rect.left - touchPadding, rect.top - touchPadding, rect.right + touchPadding, rect.bottom + touchPadding).contains(point.x, point.y)) {
                return i
            }
        }
        return -1
    }

    private fun checkHandleTouch(elementRect: RectF, touchX: Float, touchY: Float, rotation: Float, allowRotation: Boolean): HandleType {
        val slop = 24f * density
        val point = rotatePoint(touchX, touchY, elementRect.centerX(), elementRect.centerY(), -rotation)
        val x = point.x
        val y = point.y
        val cx = elementRect.centerX()
        val cy = elementRect.centerY()
        val rotationY = elementRect.top - rotationOffsetPx

        val handles = listOf(
            HandleType.TOP_LEFT to PointF(elementRect.left, elementRect.top),
            HandleType.TOP_RIGHT to PointF(elementRect.right, elementRect.top),
            HandleType.BOTTOM_LEFT to PointF(elementRect.left, elementRect.bottom),
            HandleType.BOTTOM_RIGHT to PointF(elementRect.right, elementRect.bottom),
            HandleType.TOP to PointF(cx, elementRect.top),
            HandleType.BOTTOM to PointF(cx, elementRect.bottom),
            HandleType.LEFT to PointF(elementRect.left, cy),
            HandleType.RIGHT to PointF(elementRect.right, cy)
        ) + if (allowRotation) listOf(HandleType.ROTATION to PointF(cx, rotationY)) else emptyList()
        val nearest = handles.minByOrNull { (_, point) ->
            (x - point.x).pow(2) + (y - point.y).pow(2)
        } ?: return HandleType.NONE
        val distanceSquared = (x - nearest.second.x).pow(2) + (y - nearest.second.y).pow(2)
        return if (distanceSquared <= slop * slop) nearest.first else HandleType.NONE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)

        val phoneRect = getFrameRect()
        drawPhoneFrame(canvas, phoneRect)
        drawProgressBar(canvas, phoneRect)
        drawTexts(canvas, phoneRect)
        drawSelection(canvas, phoneRect)
    }

    private fun drawGrid(canvas: Canvas) {
        val gridSize = 24f * density
        val startX = (panX % gridSize)
        val startY = (panY % gridSize)

        var x = startX
        while (x < width) {
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
            x += gridSize
        }

        var y = startY
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
            y += gridSize
        }
    }

    private fun drawPhoneFrame(canvas: Canvas, phoneRect: RectF) {
        clipPath.reset()
        clipPath.addRoundRect(phoneRect, cornerRadiusPx, cornerRadiusPx, Path.Direction.CW)

        canvas.save()
        canvas.clipPath(clipPath)

        val bg = layoutData.background
        val bmp = backgroundBitmap
        if (bmp != null && !bmp.isRecycled) {
            val src = Rect(0, 0, bmp.width, bmp.height)
            canvas.drawBitmap(bmp, src, phoneRect, null)
        } else {
            val bgColor = try {
                Color.parseColor(bg.color)
            } catch (e: Exception) {
                Color.parseColor("#3A3A3C")
            }
            frameBgPaint.color = bgColor
            canvas.drawRect(phoneRect, frameBgPaint)
        }

        val punchHoleRadius = 4f * density
        canvas.drawCircle(phoneRect.centerX(), phoneRect.top + 10f * density, punchHoleRadius, notchPaint)

        canvas.restore()
        canvas.drawRoundRect(phoneRect, cornerRadiusPx, cornerRadiusPx, frameBorderPaint)
    }

    private fun drawProgressBar(canvas: Canvas, phoneRect: RectF) {
        if (!layoutData.progressBar.visible) return
        val rect = getProgressBarRect(phoneRect)
        val isInside = phoneRect.contains(rect.centerX(), rect.centerY())

        val alpha = if (isInside) 255 else 90
        val pbColor = try {
            Color.parseColor(layoutData.progressBar.color)
        } catch (e: Exception) {
            Color.parseColor("#E94560")
        }

        progressBgPaint.alpha = if (isInside) 80 else 30
        val pbRadius = rect.height() / 2f
        canvas.drawRoundRect(rect, pbRadius, pbRadius, progressBgPaint)

        val fillRect = RectF(rect.left, rect.top, rect.left + rect.width() * 0.65f, rect.bottom)
        progressFillPaint.color = pbColor
        progressFillPaint.alpha = alpha
        canvas.drawRoundRect(fillRect, pbRadius, pbRadius, progressFillPaint)
    }

    private fun drawTexts(canvas: Canvas, phoneRect: RectF) {
        for (text in layoutData.texts) {
            val rect = getTextRect(phoneRect, text)
            val isInside = phoneRect.contains(rect.centerX(), rect.centerY())

            val textColor = try {
                Color.parseColor(text.color)
            } catch (e: Exception) {
                Color.WHITE
            }

            textPaint.color = textColor
            textPaint.alpha = if (isInside) 255 else 90
            textPaint.textSize = max(10f * density, text.fontSize * density)
            textPaint.typeface = when (text.fontFamily.lowercase()) {
                "sans-serif" -> Typeface.create("sans-serif", Typeface.NORMAL)
                "serif" -> Typeface.create("serif", Typeface.NORMAL)
                "monospace" -> Typeface.create("monospace", Typeface.NORMAL)
                "sans-serif-condensed" -> Typeface.create("sans-serif-condensed", Typeface.NORMAL)
                else -> Typeface.DEFAULT
            }

            val textY = rect.centerY() - ((textPaint.descent() + textPaint.ascent()) / 2f)
            canvas.save()
            canvas.rotate(text.rotation, rect.centerX(), rect.centerY())
            canvas.drawText(text.content, rect.centerX(), textY, textPaint)
            canvas.restore()
        }
    }

    private fun drawSelection(canvas: Canvas, phoneRect: RectF) {
        val targetRect = when (selectedType) {
            SelectedType.PROGRESS_BAR -> getProgressBarRect(phoneRect)
            SelectedType.TEXT -> {
                if (selectedTextIndex in layoutData.texts.indices) {
                    getTextRect(phoneRect, layoutData.texts[selectedTextIndex])
                } else null
            }
            SelectedType.NONE -> null
        } ?: return

        val rotation = if (selectedType == SelectedType.TEXT && selectedTextIndex in layoutData.texts.indices) {
            layoutData.texts[selectedTextIndex].rotation
        } else {
            0f
        }

        val cx = targetRect.centerX()
        val cy = targetRect.centerY()
        canvas.save()
        canvas.rotate(rotation, cx, cy)
        canvas.drawRect(targetRect, selectionBoxPaint)

        drawSquareHandle(canvas, targetRect.left, targetRect.top)
        drawSquareHandle(canvas, targetRect.right, targetRect.top)
        drawSquareHandle(canvas, targetRect.left, targetRect.bottom)
        drawSquareHandle(canvas, targetRect.right, targetRect.bottom)
        drawSquareHandle(canvas, cx, targetRect.top)
        drawSquareHandle(canvas, cx, targetRect.bottom)
        drawSquareHandle(canvas, targetRect.left, cy)
        drawSquareHandle(canvas, targetRect.right, cy)

        if (selectedType == SelectedType.TEXT) {
            val knobY = targetRect.top - rotationOffsetPx
            canvas.drawLine(cx, targetRect.top, cx, knobY, rotationLinePaint)
            canvas.drawCircle(cx, knobY, handleSizePx + 1f * density, handleFillPaint)
            canvas.drawCircle(cx, knobY, handleSizePx + 1f * density, handleBorderPaint)
        }
        canvas.restore()
    }

    private fun drawSquareHandle(canvas: Canvas, cx: Float, cy: Float) {
        val size = handleSizePx
        canvas.drawRect(cx - size, cy - size, cx + size, cy + size, handleFillPaint)
        canvas.drawRect(cx - size, cy - size, cx + size, cy + size, handleBorderPaint)
    }

    private fun selectedRect(phoneRect: RectF): RectF? = when (selectedType) {
        SelectedType.PROGRESS_BAR -> getProgressBarRect(phoneRect)
        SelectedType.TEXT -> {
            if (selectedTextIndex in layoutData.texts.indices) {
                getTextRect(phoneRect, layoutData.texts[selectedTextIndex])
            } else null
        }
        SelectedType.NONE -> null
    }

    private fun selectedRotation(): Float =
        if (selectedType == SelectedType.TEXT && selectedTextIndex in layoutData.texts.indices) {
            layoutData.texts[selectedTextIndex].rotation
        } else {
            0f
        }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }

        val phoneRect = getFrameRect()
        val fw = phoneRect.width()
        val fh = phoneRect.height()

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                isDraggingElement = false
                isResizingElement = false
                isRotatingElement = false
                isPanningCanvas = false
                activeHandle = HandleType.NONE

                val currentSelectedRect = selectedRect(phoneRect)
                if (currentSelectedRect != null) {
                    val handle = checkHandleTouch(currentSelectedRect, event.x, event.y, selectedRotation(), selectedType == SelectedType.TEXT)
                    if (handle == HandleType.ROTATION) {
                        activeHandle = handle
                        isRotatingElement = true
                        return true
                    }
                    if (handle != HandleType.NONE) {
                        activeHandle = handle
                        isResizingElement = true
                        return true
                    }
                }

                val textIdx = findTextAtPoint(event.x, event.y, phoneRect)
                if (textIdx != -1) {
                    selectedType = SelectedType.TEXT
                    selectedTextIndex = textIdx
                    isDraggingElement = true
                    onSelectionChanged?.invoke(selectedType, selectedTextIndex)
                    invalidate()
                    return true
                }

                if (layoutData.progressBar.visible && getProgressBarTouchRect(phoneRect).contains(event.x, event.y)) {
                    selectedType = SelectedType.PROGRESS_BAR
                    selectedTextIndex = -1
                    isDraggingElement = true
                    onSelectionChanged?.invoke(selectedType, selectedTextIndex)
                    invalidate()
                    return true
                }

                selectedType = SelectedType.NONE
                selectedTextIndex = -1
                isPanningCanvas = true
                onSelectionChanged?.invoke(selectedType, selectedTextIndex)
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                val dy = event.y - lastTouchY
                lastTouchX = event.x
                lastTouchY = event.y

                if (isPanningCanvas) {
                    panX += dx
                    panY += dy
                    invalidate()
                    return true
                }

                if (isRotatingElement && selectedType == SelectedType.TEXT && selectedTextIndex in layoutData.texts.indices) {
                    val t = layoutData.texts[selectedTextIndex]
                    val rect = getTextRect(phoneRect, t)
                    val angle = Math.toDegrees(atan2((event.y - rect.centerY()).toDouble(), (event.x - rect.centerX()).toDouble())).toFloat() + 90f
                    t.rotation = normalizeAngle(angle)
                    invalidate()
                    return true
                }

                val normDx = dx / fw
                val normDy = dy / fh

                if (isDraggingElement) {
                    when (selectedType) {
                        SelectedType.PROGRESS_BAR -> {
                            val pb = layoutData.progressBar
                            pb.x += normDx
                            pb.y += normDy
                            invalidate()
                        }
                        SelectedType.TEXT -> {
                            if (selectedTextIndex in layoutData.texts.indices) {
                                val t = layoutData.texts[selectedTextIndex]
                                t.x += normDx
                                t.y += normDy
                                invalidate()
                            }
                        }
                        SelectedType.NONE -> {}
                    }
                    return true
                }

                if (isResizingElement && activeHandle != HandleType.NONE) {
                    when (selectedType) {
                        SelectedType.PROGRESS_BAR -> {
                            resizeProgressBar(normDx, normDy)
                            return true
                        }
                        SelectedType.TEXT -> {
                            resizeText(normDx, normDy, fw, fh)
                            return true
                        }
                        SelectedType.NONE -> {}
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDraggingElement = false
                isResizingElement = false
                isRotatingElement = false
                isPanningCanvas = false
                activeHandle = HandleType.NONE
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun normalizeAngle(angle: Float): Float {
        var result = angle % 360f
        if (result > 180f) result -= 360f
        if (result < -180f) result += 360f
        return result
    }

    private fun resizeProgressBar(normDx: Float, normDy: Float) {
        val pb = layoutData.progressBar
        val handle = activeHandle
        val fromLeft = handle == HandleType.LEFT || handle == HandleType.TOP_LEFT || handle == HandleType.BOTTOM_LEFT
        val fromRight = handle == HandleType.RIGHT || handle == HandleType.TOP_RIGHT || handle == HandleType.BOTTOM_RIGHT
        val fromTop = handle == HandleType.TOP || handle == HandleType.TOP_LEFT || handle == HandleType.TOP_RIGHT
        val fromBottom = handle == HandleType.BOTTOM || handle == HandleType.BOTTOM_LEFT || handle == HandleType.BOTTOM_RIGHT

        if (fromLeft) {
            val newW = (pb.width - normDx).coerceIn(0.005f, 2f)
            pb.x += pb.width - newW
            pb.width = newW
        } else if (fromRight) {
            pb.width = (pb.width + normDx).coerceIn(0.005f, 2f)
        }

        if (fromTop) {
            val newH = (pb.height - normDy).coerceIn(0.005f, 2f)
            pb.y += pb.height - newH
            pb.height = newH
        } else if (fromBottom) {
            pb.height = (pb.height + normDy).coerceIn(0.005f, 2f)
        }
        invalidate()
    }

    private fun resizeText(normDx: Float, normDy: Float, frameWidth: Float, frameHeight: Float) {
        if (selectedTextIndex !in layoutData.texts.indices) return
        val t = layoutData.texts[selectedTextIndex]
        val handle = activeHandle
        val rotationRadians = Math.toRadians(t.rotation.toDouble())
        val deltaX = normDx * frameWidth
        val deltaY = normDy * frameHeight
        val localDeltaX = (deltaX * cos(rotationRadians) + deltaY * sin(rotationRadians)).toFloat() / frameWidth
        val localDeltaY = (-deltaX * sin(rotationRadians) + deltaY * cos(rotationRadians)).toFloat() / frameHeight
        val oldW = t.width
        val oldH = t.height
        val fromLeft = handle == HandleType.LEFT || handle == HandleType.TOP_LEFT || handle == HandleType.BOTTOM_LEFT
        val fromRight = handle == HandleType.RIGHT || handle == HandleType.TOP_RIGHT || handle == HandleType.BOTTOM_RIGHT
        val fromTop = handle == HandleType.TOP || handle == HandleType.TOP_LEFT || handle == HandleType.TOP_RIGHT
        val fromBottom = handle == HandleType.BOTTOM || handle == HandleType.BOTTOM_LEFT || handle == HandleType.BOTTOM_RIGHT

        val targetW = when {
            fromLeft -> oldW - localDeltaX
            fromRight -> oldW + localDeltaX
            else -> oldW
        }
        val targetH = when {
            fromTop -> oldH - localDeltaY
            fromBottom -> oldH + localDeltaY
            else -> oldH
        }

        val factorW = (targetW / oldW).coerceIn(0.2f, 8f)
        val factorH = (targetH / oldH).coerceIn(0.2f, 8f)
        val factor = if (fromLeft || fromRight) {
            if (fromTop || fromBottom) (factorW + factorH) / 2f else factorW
        } else {
            factorH
        }.coerceIn(0.2f, 8f)

        val newW = (oldW * factor).coerceIn(0.01f, 2f)
        val newH = (oldH * factor).coerceIn(0.01f, 2f)

        t.width = newW
        t.height = newH
        t.fontSize = (t.fontSize * factor).coerceIn(6f, 128f)

        if (fromLeft) {
            t.x += oldW - newW
        } else if (!fromRight) {
            t.x += (oldW - newW) / 2f
        }

        if (fromTop) {
            t.y += oldH - newH
        } else if (!fromBottom) {
            t.y += (oldH - newH) / 2f
        }

        invalidate()
    }
}
