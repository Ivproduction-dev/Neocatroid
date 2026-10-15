package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.LocalServer
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.TcpNode
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import kotlin.concurrent.thread

class TcpPingAction() : TemporalAction() {
    var scope: Scope? = null
    var serverName: Formula? = null
    var ip: Formula? = null
    var port: Formula? = null
    var variable: UserVariable? = null
    var measure: Boolean = false
    var timeout: Formula? = null
    @Volatile private var started = false

    override fun update(percent: Float) {
        if (started) {
            return
        }
        started = true
        val scope = scope ?: return
        val name = serverName?.interpretString(scope)
        val ipStr = ip?.interpretString(scope)
        val portInt = port?.interpretString(scope)?.toIntOrNull()
        val timeoutMs = timeout?.interpretString(scope)?.toIntOrNull()?.coerceAtLeast(500)
            ?: TcpNode.PING_TIMEOUT_MS
        val target = variable
        thread {
            try {
                val result = if (!name.isNullOrEmpty()) {
                    LocalServer.pingNamed(name, timeoutMs)
                } else if (ipStr != null && portInt != null) {
                    LocalServer.pingHost(ipStr, portInt, timeoutMs)
                } else {
                    null
                }
                if (target != null) {
                    if (measure) {
                        target.value = result?.ms?.toString() ?: "-1"
                    } else {
                        target.value = (result?.ok == true).toString()
                    }
                }
            } catch (e: Exception) {
                if (target != null) {
                    target.value = if (measure) "-1" else "false"
                }
            }
        }
    }
}
