package org.catrobat.catroid.content.actions

import android.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.BarManager
import org.catrobat.catroid.stage.BarState

class ShowBarAction : Action() {
    var scope: Scope? = null
    var variable: UserVariable? = null
    var fillColor: Formula? = null
    var trackColor: Formula? = null
    var widthPct: Formula? = null
    var heightPct: Formula? = null
    var minValue: Formula? = null
    var maxValue: Formula? = null
    var xPct: Formula? = null
    var yPct: Formula? = null
    var autoUpdate: Boolean = true
    var pinned: Boolean = true

    private var started = false
    @Volatile private var finished = false

    override fun act(delta: Float): Boolean {
        if (!started) {
            started = true
            runOnce()
        }
        return finished
    }

    private fun runOnce() {
        try {
            val variable = variable ?: run { finished = true; return }
            val id = "bar:" + (scope?.sprite?.name ?: "") + ":" + variable.name
            val state = BarState(
                id = id,
                variable = variable,
                minValue = (minValue?.interpretFloat(scope) ?: 0f),
                maxValue = (maxValue?.interpretFloat(scope) ?: 100f),
                widthPct = (widthPct?.interpretFloat(scope) ?: 50f).coerceIn(1f, 100f),
                heightPct = (heightPct?.interpretFloat(scope) ?: 8f).coerceIn(0.5f, 100f),
                xPct = (xPct?.interpretFloat(scope) ?: 50f).coerceIn(0f, 100f),
                yPct = (yPct?.interpretFloat(scope) ?: 90f).coerceIn(0f, 100f),
                fillColor = parseColor(fillColor?.interpretString(scope), DEFAULT_FILL),
                trackColor = parseColor(trackColor?.interpretString(scope), DEFAULT_TRACK),
                autoUpdate = autoUpdate,
                pinned = pinned
            )
            BarManager.showBar(state)
        } finally {
            finished = true
        }
    }

    private fun parseColor(raw: String?, fallback: Int): Int {
        if (raw.isNullOrBlank()) return fallback
        return try {
            Color.parseColor(raw)
        } catch (e: IllegalArgumentException) {
            fallback
        }
    }

    override fun restart() {
        super.restart()
        started = false
        finished = false
    }

    override fun reset() {
        super.reset()
        started = false
        finished = false
        scope = null
        variable = null
        fillColor = null
        trackColor = null
        widthPct = null
        heightPct = null
        minValue = null
        maxValue = null
        xPct = null
        yPct = null
    }

    companion object {
        private val DEFAULT_FILL = Color.parseColor("#4CAF50")
        private val DEFAULT_TRACK = Color.parseColor("#3A3A3C")
    }
}
