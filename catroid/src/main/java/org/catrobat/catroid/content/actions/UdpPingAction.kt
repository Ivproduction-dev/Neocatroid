package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.UdpEndpoint
import org.catrobat.catroid.content.UdpManager
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import kotlin.concurrent.thread

class UdpPingAction() : Action() {
    var scope: Scope? = null
    var serverName: Formula? = null
    var ip: Formula? = null
    var port: Formula? = null
    var variable: UserVariable? = null
    var measure: Boolean = false
    var timeout: Formula? = null

    @Volatile private var started = false
    @Volatile private var finished = false
    @Volatile private var generation = 0

    override fun act(delta: Float): Boolean {
        if (!started) {
            started = true
            start()
        }
        return finished
    }

    override fun reset() {
        generation++
        started = false
        finished = false
        super.reset()
    }

    private fun start() {
        val scope = scope
        if (scope == null) {
            finished = true
            return
        }
        val gen = generation
        val name = serverName?.interpretString(scope)
        val ipStr = ip?.interpretString(scope)?.trim()
        val portInt = port?.interpretString(scope)?.toIntOrNull()
        val timeoutMs = timeout?.interpretString(scope)?.toIntOrNull()
            ?.coerceIn(MIN_TIMEOUT_MS, MAX_TIMEOUT_MS) ?: UdpEndpoint.DEFAULT_PING_TIMEOUT_MS
        val target = variable
        val measure = measure
        thread {
            val value = try {
                val result = if (!name.isNullOrEmpty()) {
                    UdpManager.node(name).pingPeer(timeoutMs)
                } else if (!ipStr.isNullOrEmpty() && portInt != null) {
                    UdpEndpoint.pingHost(ipStr, portInt, timeoutMs)
                } else {
                    null
                }
                if (measure) result?.ms?.toString() ?: "-1" else (result?.ok == true).toString()
            } catch (e: Exception) {
                if (measure) "-1" else "false"
            }
            if (gen == generation) {
                target?.value = value
                finished = true
            }
        }
    }

    companion object {
        const val MIN_TIMEOUT_MS = 200
        const val MAX_TIMEOUT_MS = 60000
    }
}
