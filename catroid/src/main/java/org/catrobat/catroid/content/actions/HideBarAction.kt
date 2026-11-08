package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.BarManager

class HideBarAction : Action() {
    var scope: Scope? = null
    var variable: UserVariable? = null
    var hideAll: Boolean = false

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
            if (hideAll) {
                BarManager.hideAllBars()
            } else {
                variable?.name?.let { BarManager.hideForVariable(it) }
            }
        } finally {
            finished = true
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
        hideAll = false
    }
}
