package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.UdpManager
import org.catrobat.catroid.formulaeditor.Formula

class ConnectUdpAction() : TemporalAction() {
    var scope: Scope? = null
    var serverName: Formula? = null
    var ip: Formula? = null
    var port: Formula? = null

    override fun update(percent: Float) {
        val name = serverName?.interpretString(scope).orEmpty()
        val ipStr = ip?.interpretString(scope) ?: return
        val portInt = port?.interpretString(scope)?.toIntOrNull() ?: return
        UdpManager.node(name).connect(ipStr, portInt)
    }
}
