package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.LocalServer
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula

class StartTcpServerNamedAction() : TemporalAction() {
    var scope: Scope? = null
    var serverName: Formula? = null
    var port: Formula? = null

    override fun update(percent: Float) {
        val name = serverName?.interpretString(scope).orEmpty()
        val portStr: String = port?.interpretString(scope) ?: "1234"
        LocalServer.startOrJoin(name, null, portStr)
    }
}
