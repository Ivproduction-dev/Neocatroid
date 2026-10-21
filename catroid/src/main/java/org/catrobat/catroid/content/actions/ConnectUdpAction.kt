package org.catrobat.catroid.content.actions

import android.util.Log
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
        val ipText = ip?.interpretString(scope)
        val ipStr = ipText?.trim().orEmpty()
        val portText = port?.interpretString(scope)
        val portInt = portText?.toIntOrNull()
        if (ipStr.isEmpty() || portInt == null || portInt !in 1..65535) {
            Log.w(TAG, "UDP endpoint '$name': invalid peer '$ipText' '$portText'")
            return
        }
        UdpManager.node(name).connect(ipStr, portInt)
    }

    companion object {
        private const val TAG = "UdpManager"
    }
}
