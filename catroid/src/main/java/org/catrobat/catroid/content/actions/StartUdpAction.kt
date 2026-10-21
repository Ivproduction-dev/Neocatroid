package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.UdpManager
import org.catrobat.catroid.formulaeditor.Formula

class StartUdpAction() : TemporalAction() {
    var scope: Scope? = null
    var serverName: Formula? = null
    var port: Formula? = null

    override fun update(percent: Float) {
        val name = serverName?.interpretString(scope).orEmpty()
        val portText = port?.interpretString(scope)
        val portInt = portText?.toIntOrNull()
        if (portInt == null || portInt !in 0..65535) {
            Log.w(TAG, "UDP endpoint '$name' was not started: invalid port '$portText'")
            return
        }
        if (!UdpManager.node(name).start(portInt)) {
            Log.w(TAG, "UDP endpoint '$name' failed to start on port $portInt (port already in use?)")
        }
    }

    companion object {
        private const val TAG = "UdpManager"
    }
}
