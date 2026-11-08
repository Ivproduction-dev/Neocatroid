package org.catrobat.catroid.apkbuildV3.neoui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class NeoUiCanvasViewTest {
    @Test
    fun draggingAnyTextCornerOutwardIncreasesTextSize() {
        listOf(-1f to -1f, 1f to -1f, -1f to 1f, 1f to 1f).forEach { (directionX, directionY) ->
            val (oldSize, newSize) = fontSizeAfterCornerDrag(directionX, directionY)
            assertTrue("Dragging corner ($directionX, $directionY) outward should increase font size", newSize > oldSize)
        }
    }

    private fun fontSizeAfterCornerDrag(directionX: Float, directionY: Float): Pair<Float, Float> {
        val context = RuntimeEnvironment.getApplication()
        val view = NeoUiCanvasView(context)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, 400, 800)
        val density = context.resources.displayMetrics.density
        val frameWidth = 280f * density
        val frameHeight = frameWidth * (16f / 9f)
        val frameLeft = (400f - frameWidth) / 2f
        val frameTop = (800f - frameHeight) / 2f
        val text = view.layoutData.texts.first()
        val originalFontSize = text.fontSize
        val centerX = frameLeft + (text.x + text.width / 2f) * frameWidth
        val centerY = frameTop + (text.y + text.height / 2f) * frameHeight
        dispatch(view, MotionEvent.ACTION_DOWN, centerX, centerY, 0L)
        dispatch(view, MotionEvent.ACTION_UP, centerX, centerY, 1L)

        val handleX = frameLeft + (text.x + if (directionX < 0f) 0f else text.width) * frameWidth
        val handleY = frameTop + (text.y + if (directionY < 0f) 0f else text.height) * frameHeight
        dispatch(view, MotionEvent.ACTION_DOWN, handleX, handleY, 500L)
        val dragX = handleX + directionX * 20f * density
        val dragY = handleY + directionY * 20f * density
        dispatch(view, MotionEvent.ACTION_MOVE, dragX, dragY, 520L)
        dispatch(view, MotionEvent.ACTION_UP, dragX, dragY, 530L)

        return originalFontSize to text.fontSize
    }

    @Test
    fun progressBarCanBeSelectedOutsideItsVisibleThickness() {
        val context = RuntimeEnvironment.getApplication()
        val view = NeoUiCanvasView(context)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, 400, 800)
        val density = context.resources.displayMetrics.density
        val frameWidth = 280f * density
        val frameHeight = frameWidth * 16f / 9f
        val frameLeft = (400f - frameWidth) / 2f
        val frameTop = (800f - frameHeight) / 2f
        val progress = view.layoutData.progressBar
        val tapX = frameLeft + (progress.x + progress.width / 2f) * frameWidth
        val tapY = frameTop + (progress.y + progress.height / 2f) * frameHeight + 12f * density
        val down = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, tapX, tapY, 0)
        view.onTouchEvent(down)
        down.recycle()

        assertEquals(NeoUiCanvasView.SelectedType.PROGRESS_BAR, view.selectedType)
    }

    @Test
    fun deletingProgressBarKeepsItHiddenAfterRedraw() {
        val context = RuntimeEnvironment.getApplication()
        val view = NeoUiCanvasView(context)
        view.measure(
            View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, 400, 800)
        val density = context.resources.displayMetrics.density
        val frameWidth = 280f * density
        val frameHeight = frameWidth * 16f / 9f
        val frameLeft = (400f - frameWidth) / 2f
        val frameTop = (800f - frameHeight) / 2f
        val progress = view.layoutData.progressBar
        val tapX = frameLeft + (progress.x + progress.width / 2f) * frameWidth
        val tapY = frameTop + (progress.y + progress.height / 2f) * frameHeight
        val down = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_DOWN, tapX, tapY, 0)
        val up = MotionEvent.obtain(0L, 1L, MotionEvent.ACTION_UP, tapX, tapY, 0)
        view.onTouchEvent(down)
        view.onTouchEvent(up)
        down.recycle()
        up.recycle()

        view.deleteSelected()
        view.draw(Canvas(Bitmap.createBitmap(400, 800, Bitmap.Config.ARGB_8888)))

        assertFalse("Hidden progress bar became visible again after redraw", view.layoutData.progressBar.visible)
    }

    private fun dispatch(view: NeoUiCanvasView, action: Int, x: Float, y: Float, time: Long) {
        val event = MotionEvent.obtain(0L, time, action, x, y, 0)
        view.onTouchEvent(event)
        event.recycle()
    }
}
