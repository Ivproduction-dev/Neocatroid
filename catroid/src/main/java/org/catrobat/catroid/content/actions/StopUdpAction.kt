package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.UdpManager
import org.catrobat.catroid.formulaeditor.Formula

class StopUdpAction() : TemporalAction() {
    var scope: Scope? = null
    var serverName: Formula? = null

    override fun update(percent: Float) {
        val name = serverName?.interpretString(scope).orEmpty()
        UdpManager.node(name).stop()
    }
}
