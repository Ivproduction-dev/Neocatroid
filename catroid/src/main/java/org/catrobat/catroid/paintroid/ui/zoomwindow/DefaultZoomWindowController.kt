package org.catrobat.catroid.paintroid.ui.zoomwindow

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.PorterDuffXfermode
import android.graphics.PorterDuff
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Shader
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import org.catrobat.catroid.paintroid.MainActivity
import org.catrobat.catroid.R

import org.catrobat.catroid.paintroid.UserPreferences
import org.catrobat.catroid.paintroid.contract.LayerContracts
import org.catrobat.catroid.paintroid.tools.Tool
import org.catrobat.catroid.paintroid.tools.ToolReference
import org.catrobat.catroid.paintroid.tools.ToolType
import org.catrobat.catroid.paintroid.tools.Workspace
import kotlin.math.roundToInt

class DefaultZoomWindowController
    (val activity: MainActivity,
    val layerModel: LayerContracts.Model,
    val workspace: Workspace,
    val toolReference: ToolReference,
    val sharedPreferences: UserPreferences) :
    ZoomWindowController {

    private val canvasRect = Rect()
    private val checkeredPattern = Paint()
    private val framePaint = Paint()

    private val zoomWindowDiameter = activity.resources.getDimensionPixelSize(R.dimen.pocketpaint_zoom_window_diameter)

    private val chequeredBackgroundBitmap =
        Bitmap.createBitmap(layerModel.width, layerModel.height, Bitmap.Config.ARGB_8888)

    private val greyBackgroundBitmap =
        Bitmap.createBitmap(
            layerModel.width + zoomWindowDiameter,
            layerModel.height + zoomWindowDiameter,
            Bitmap.Config.ARGB_8888
        )

    private val backgroundBitmap =
        Bitmap.createBitmap(
            layerModel.width + zoomWindowDiameter,
            layerModel.height + zoomWindowDiameter,
            Bitmap.Config.ARGB_8888
        )

    private var zoomCropBitmap: Bitmap? = null
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val srcInPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        isFilterBitmap = false
    }

    init {
        framePaint.color = Color.BLACK
        framePaint.style = Paint.Style.STROKE
        framePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        val checkerboard =
            BitmapFactory.decodeResource(activity.resources, R.drawable.pocketpaint_checkeredbg)
        val shader = BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        checkeredPattern.shader = shader
        checkeredPattern.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)

        val backgroundCanvas: Canvas? = chequeredBackgroundBitmap?.let { Canvas(it) }

        canvasRect.set(0, 0, layerModel.width, layerModel.height)

        backgroundCanvas?.drawRect(canvasRect, checkeredPattern)
        backgroundCanvas?.drawRect(canvasRect, framePaint)

        val greyBackgroundCanvas = Canvas(greyBackgroundBitmap)
        greyBackgroundCanvas.drawColor(
            androidx.core.content.ContextCompat.getColor(activity, R.color.pocketpaint_main_drawing_surface_background)
        )

        val canvasBackground = Canvas(backgroundBitmap)

        canvasBackground.drawBitmap(greyBackgroundBitmap, Matrix(), null)
        canvasBackground.drawBitmap(
            chequeredBackgroundBitmap, zoomWindowDiameter / 2f, zoomWindowDiameter / 2f, null)

        chequeredBackgroundBitmap.recycle()
        greyBackgroundBitmap.recycle()
        checkerboard.recycle()
    }

    private val zoomWindow: RelativeLayout =
        activity.findViewById(R.id.pocketpaint_zoom_window)
    private val zoomWindowImage: ImageView =
        activity.findViewById(R.id.pocketpaint_zoom_window_image)
    private var zoomWindowBitmap: Bitmap? = null
    private var coordinates: PointF? = null

    override fun show(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF) {
        if (checkIfToolCompatibleWithZoomWindow(toolReference.tool) == Constants.COMPATIBLE &&
            isPointOnCanvas(drawingSurfaceCoordinates.x, drawingSurfaceCoordinates.y)) {
            setZoomWindowPosition(displayCoordinates)
            zoomWindow.visibility = View.VISIBLE
            coordinates = PointF(drawingSurfaceCoordinates.x, drawingSurfaceCoordinates.y)
            zoomWindowImage.setImageBitmap(cropBitmap(drawingSurfaceCoordinates, null))
        }
    }

    override fun dismiss() {
        zoomWindow.visibility = View.GONE
    }

    override fun dismissOnPinch() {
        zoomWindow.visibility = View.GONE
    }

    override fun onMove(drawingSurfaceCoordinates: PointF, displayCoordinates: PointF) {
        if (checkIfToolCompatibleWithZoomWindow(toolReference.tool) == Constants.COMPATIBLE) {
            setZoomWindowPosition(displayCoordinates)
            if (isPointOnCanvas(drawingSurfaceCoordinates.x, drawingSurfaceCoordinates.y)) {
                if (zoomWindow.visibility == View.GONE) {
                    zoomWindow.visibility = View.VISIBLE
                }
                this.coordinates = drawingSurfaceCoordinates
            } else {
                dismiss()
            }
        }
    }

    private fun setZoomWindowPosition(displayCoordinates: PointF) {
        if (shouldBeInTheRight(coordinates = displayCoordinates)) {
            setLayoutAlignment(right = true)
        } else {
            setLayoutAlignment(right = false)
        }
    }

    override fun setBitmap(bitmap: Bitmap?) {
        zoomWindowImage.setImageBitmap(coordinates?.let { cropBitmap(it, bitmap) })
        zoomWindowBitmap = bitmap
    }

    override fun getBitmap(): Bitmap? = zoomWindowBitmap

    private fun isPointOnCanvas(pointX: Float, pointY: Float): Boolean =
        pointX > 0 && pointX < layerModel.width && pointY > 0 && pointY < layerModel.height

    private fun shouldBeInTheRight(coordinates: PointF): Boolean {
        if (coordinates.x < activity.resources.displayMetrics.widthPixels / 2 &&
            coordinates.y < activity.resources.displayMetrics.heightPixels / 2) {
            return true
        }
        return false
    }

    private fun setLayoutAlignment(right: Boolean) {
        val params: RelativeLayout.LayoutParams =
            zoomWindowImage.layoutParams as RelativeLayout.LayoutParams
        if (right) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }
        zoomWindowImage.layoutParams = params
    }

    private fun cropBitmap(coordinates: PointF, composite: Bitmap?): Bitmap? {
        val size = getSizeOfZoomWindow()
        if (size <= 0) return null
        val cached = zoomCropBitmap
        if (cached == null || cached.isRecycled || cached.width != size) {
            cached?.recycle()
            zoomCropBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
        val bitmap = zoomCropBitmap ?: return null
        bitmap.eraseColor(Color.TRANSPARENT)

        val canvas = Canvas(bitmap)
        val rectF = RectF(0f, 0f, size.toFloat(), size.toFloat())
        canvas.drawOval(rectF, circlePaint)

        val startX = coordinates.x.roundToInt() + zoomWindowDiameter / 2 - size / 2
        val startY = coordinates.y.roundToInt() + zoomWindowDiameter / 2 - size / 2

        canvas.save()
        canvas.translate(-startX.toFloat(), -startY.toFloat())
        canvas.drawBitmap(backgroundBitmap, 0f, 0f, srcInPaint)
        if (composite != null) {
            canvas.drawBitmap(composite, zoomWindowDiameter / 2f, zoomWindowDiameter / 2f, srcInPaint)
        } else {
            for (i in layerModel.layerCount - 1 downTo 0) {
                val layer = layerModel.getLayerAt(i) ?: continue
                if (!layer.isVisible) continue
                srcInPaint.alpha = layer.getValueForOpacityPercentage()
                canvas.drawBitmap(
                    layer.bitmap,
                    zoomWindowDiameter / 2f,
                    zoomWindowDiameter / 2f,
                    srcInPaint
                )
            }
            srcInPaint.alpha = 255
        }
        canvas.restore()
        return bitmap
    }

    private fun getSizeOfZoomWindow(): Int {
        val zoomIndex = (sharedPreferences.preferenceZoomWindowZoomPercentage - initialZoomValue) / zoomPercentStepValue
        return zoomWindowDiameter - zoomIndex * zoomFactor
    }

    override fun checkIfToolCompatibleWithZoomWindow(tool: Tool?): Constants {
        return when (tool?.toolType?.name) {
            ToolType.LINE.name,
            ToolType.CURSOR.name,
            ToolType.WATERCOLOR.name,
            ToolType.SPRAY.name,
            ToolType.BRUSH.name,
            ToolType.ERASER.name,
            ToolType.PIPETTE.name,
            ToolType.CLIP.name,
            ToolType.SMUDGE.name -> Constants.COMPATIBLE
            else -> Constants.NOT_COMPATIBLE
        }
    }

    enum class Constants {
        NOT_COMPATIBLE,
        COMPATIBLE
    }

    companion object {
        const val zoomFactor: Int = 25
        const val initialZoomValue: Int = 100
        const val zoomPercentStepValue = 50
    }
}
