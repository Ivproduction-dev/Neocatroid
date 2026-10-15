package org.catrobat.catroid.paintroid.ui.viewholder

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ViewFlipper
import kotlin.math.abs

class ToolPageSwitcher(context: Context) : ViewFlipper(context) {
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var downX = 0f
    private var downY = 0f
    private var dragDistance = 0f
    private var dragging = false
    var onPageChanged: ((Int) -> Unit)? = null

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                dragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - downX
                val dy = event.y - downY
                if (abs(dx) > touchSlop && abs(dx) > abs(dy)) {
                    dragging = true
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
            }
        }
        return dragging
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                dragDistance = 0f
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                dragDistance = event.x - downX
                val current = getChildAt(displayedChild)
                val canMove = displayedChild == 0 && dragDistance < 0 || displayedChild == 1 && dragDistance > 0
                current.translationX = if (canMove) dragDistance * 0.35f else dragDistance * 0.12f
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val nextPage = when {
                    dragDistance < -width / 5f && displayedChild < childCount - 1 -> displayedChild + 1
                    dragDistance > width / 5f && displayedChild > 0 -> displayedChild - 1
                    else -> displayedChild
                }
                showPage(nextPage)
                parent.requestDisallowInterceptTouchEvent(false)
                dragging = false
                return true
            }
        }
        return true
    }

    fun showPage(page: Int) {
        if (page !in 0 until childCount || page == displayedChild) {
            getChildAt(displayedChild)?.animate()?.translationX(0f)?.setDuration(130L)?.start()
            return
        }
        val oldPage = getChildAt(displayedChild)
        val nextPage = getChildAt(page)
        val direction = if (page > displayedChild) -1f else 1f
        oldPage.animate().translationX(direction * width).setDuration(150L).withEndAction {
            displayedChild = page
            onPageChanged?.invoke(page)
            nextPage.translationX = -direction * width
            nextPage.animate().translationX(0f).setDuration(150L).start()
            oldPage.translationX = 0f
        }.start()
    }

    fun hintNextPage() {
        if (displayedChild != 0 || childCount < 2) return
        val page = getChildAt(0)
        page.animate().translationX(-8f * resources.displayMetrics.density).setDuration(120L)
            .withEndAction { page.animate().translationX(0f).setDuration(180L).start() }
            .start()
    }
}
