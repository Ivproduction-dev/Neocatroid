package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.UserVariable

class ClearTcpVariableAction() : TemporalAction() {
    var scope: Scope? = null
    var variable: UserVariable? = null

    override fun update(percent: Float) {
        variable?.value = ""
    }
}
