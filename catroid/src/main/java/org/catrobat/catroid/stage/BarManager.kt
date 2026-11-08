package org.catrobat.catroid.stage

import android.util.Log
import org.catrobat.catroid.formulaeditor.UserVariable

class BarState(
    val id: String,
    val variable: UserVariable,
    val minValue: Float,
    val maxValue: Float,
    val widthPct: Float,
    val heightPct: Float,
    val xPct: Float,
    val yPct: Float,
    val fillColor: Int,
    val trackColor: Int,
    val autoUpdate: Boolean,
    val pinned: Boolean
) {
    fun fraction(): Float {
        val raw = (variable.value as? Number)?.toFloat()
            ?: variable.value?.toString()?.toFloatOrNull()
            ?: 0f
        val min = minValue
        val max = if (maxValue > minValue) maxValue else minValue + 1f
        return ((raw - min) / (max - min)).coerceIn(0f, 1f)
    }
}

object BarManager {
    private val TAG = "BarManager"
    private val bars = LinkedHashMap<String, BarState>()

    fun showBar(state: BarState) {
        bars[state.id] = state
        if (state.pinned) {
            showNative(state)
            removeGdx(state.id)
        } else {
            removeNative(state.id)
            showGdx(state)
        }
    }

    fun hideBar(id: String) {
        bars.remove(id)
        removeNative(id)
        removeGdx(id)
    }

    fun hideForVariable(variableName: String) {
        val ids = bars.values.filter { it.variable.name == variableName }.map { it.id }
        ids.forEach { hideBar(it) }
    }

    fun hideAllBars() {
        val ids = bars.keys.toList()
        ids.forEach { hideBar(it) }
    }

    fun clear() {
        val stage = StageActivity.activeStageActivity?.get()
        bars.keys.forEach { id ->
            stage?.runOnUiThread { stage.removeViewFromStage(id) }
            removeGdx(id)
        }
        bars.clear()
    }

    private fun showNative(state: BarState) {
        val stage = StageActivity.activeStageActivity?.get() ?: return
        stage.runOnUiThread {
            try {
                val metrics = stage.resources.displayMetrics
                val screenW = metrics.widthPixels
                val screenH = metrics.heightPixels
                val w = (screenW * state.widthPct / 100f).toInt()
                    .coerceAtLeast((12 * metrics.density).toInt())
                val h = (screenH * state.heightPct / 100f).toInt()
                    .coerceAtLeast((6 * metrics.density).toInt())
                val x = (screenW * state.xPct / 100f - w / 2f).toInt().coerceIn(0, screenW - w)
                val y = (screenH * state.yPct / 100f - h / 2f).toInt().coerceIn(0, screenH - h)
                val view = BarView(stage, state)
                val params = android.widget.FrameLayout.LayoutParams(w, h).apply {
                    leftMargin = x
                    topMargin = y
                }
                stage.addViewToStage(state.id, view, params)
            } catch (e: Exception) {
                Log.e(TAG, "Show bar failed", e)
            }
        }
    }

    private fun showGdx(state: BarState) {
        val listener = StageActivity.getActiveStageListener() ?: return
        removeGdx(state.id)
        listener.addActor(BarActor(state))
    }

    private fun removeNative(id: String) {
        val stage = StageActivity.activeStageActivity?.get() ?: return
        stage.runOnUiThread { stage.removeViewFromStage(id) }
    }

    private fun removeGdx(id: String) {
        val listener = StageActivity.getActiveStageListener() ?: return
        val stage = listener.getStage() ?: return
        val actors = stage.actors
        for (actor in actors) {
            if (actor is BarActor && actor.barId == id) {
                actor.remove()
            }
        }
    }
}
