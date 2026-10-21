package org.catrobat.catroid.content.actions

import android.util.Log
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
            val ipText = ip?.interpretString(scope)
            val ipStr = ipText?.trim().orEmpty()
            val portText = port?.interpretString(scope)
            val portInt = portText?.toIntOrNull()
            if (ipStr.isEmpty() || portInt == null || portInt !in 1..65535) {
                Log.w(TAG, "UDP endpoint '$name': send skipped, invalid target '$ipText' '$portText'")
                return
            }
            if (!node.sendTo(ipStr, portInt, text)) {
                Log.w(TAG, "UDP endpoint '$name': send to $ipStr:$portInt failed")
            }
        } else if (!node.sendToPeer(text)) {
            Log.w(TAG, "UDP endpoint '$name': send failed, no remembered peer (connect first) or socket error")
        }
    }

    companion object {
        private const val TAG = "UdpManager"
    }
}
