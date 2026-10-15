package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.LocalServer
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula

class SendToTcpServerAction() : TemporalAction() {
    var scope: Scope? = null
    var values: List<Formula>? = null
    var serverName: Formula? = null

    override fun update(percent: Float) {
        val values = values?.mapNotNull { value -> value?.interpretString(scope) }
        if (!values.isNullOrEmpty()) {
            val name = serverName?.interpretString(scope).orEmpty()
            LocalServer.sendAllTo(name, listOf(values.joinToString(LocalServer.VALUE_SEPARATOR.toString())))
        }
    }
}
