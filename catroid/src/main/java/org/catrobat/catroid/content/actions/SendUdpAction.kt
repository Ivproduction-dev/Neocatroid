package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.LocalServer
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.UdpManager
import org.catrobat.catroid.formulaeditor.Formula

class SendUdpAction() : TemporalAction() {
    var scope: Scope? = null
    var serverName: Formula? = null
    var ip: Formula? = null
    var port: Formula? = null
    var values: List<Formula>? = null
    var sendMode: Int = 0

    override fun update(percent: Float) {
        val values = values?.mapNotNull { value -> value?.interpretString(scope) }
        if (values.isNullOrEmpty()) {
            return
        }
        val text = values.joinToString(LocalServer.VALUE_SEPARATOR.toString())
        val name = serverName?.interpretString(scope).orEmpty()
        val node = UdpManager.node(name)
        if (sendMode == 1) {
            val ipStr = ip?.interpretString(scope)?.trim().orEmpty()
            val portInt = port?.interpretString(scope)?.toIntOrNull() ?: return
            if (ipStr.isEmpty()) {
                return
            }
            node.sendTo(ipStr, portInt, text)
        } else {
            node.sendToPeer(text)
        }
    }
}
