package org.catrobat.catroid.content

import android.util.Log
import java.net.InetSocketAddress
import java.net.ServerSocket

class LocalServer private constructor() {
    companion object {
        const val VALUE_SEPARATOR: Char = '\u0001'
        const val PING_PREFIX = "PING:"
        const val PONG_PREFIX = "PONG:"
        const val DEFAULT_NODE = ""

        private val nodes = mutableMapOf<String, TcpNode>()

        @JvmStatic
        fun node(name: String?): TcpNode {
            val key = normServerKey(name)
            synchronized(nodes) {
                return nodes.getOrPut(key) { TcpNode(key) }
            }
        }

        @JvmStatic
        fun normServerKey(name: String?): String {
            val key = name?.trim().orEmpty()
            if (key == "0") {
                return ""
            }
            return key
        }

        var clientLimit: Int
            get() = node(null).clientLimit
            set(value) {
                node(null).clientLimit = value
            }

        var serverTimeoutSeconds: Int
            get() = node(null).serverTimeoutSeconds
            set(value) {
                node(null).serverTimeoutSeconds = value
            }

        @JvmStatic
        fun startOrJoin(ip: String?, port: String) {
            node(null).startOrJoin(ip, port)
        }

        @JvmStatic
        fun startOrJoin(name: String?, ip: String?, port: String) {
            node(name).startOrJoin(ip, port)
        }

        @JvmStatic
        fun send(value: String) {
            node(null).send(value)
        }

        @JvmStatic
        fun sendAll(values: List<String>) {
            node(null).sendAll(values)
        }

        @JvmStatic
        fun sendTo(name: String?, value: String) {
            node(name).send(value)
        }

        @JvmStatic
        fun sendAllTo(name: String?, values: List<String>) {
            node(name).sendAll(values)
        }

        @JvmStatic
        fun stop() {
            node(null).stop()
        }

        @JvmStatic
        fun stopNamed(name: String?) {
            node(name).stop()
        }

        @JvmStatic
        fun stopAll() {
            synchronized(nodes) {
                for (n in nodes.values) {
                    n.stop()
                }
            }
        }

        @JvmStatic
        fun disconnectFromServer() {
            node(null).disconnectFromServer()
        }

        @JvmStatic
        fun disconnectNamed(name: String?) {
            node(name).disconnectFromServer()
        }

        @JvmStatic
        fun getValue(): String = node(null).getValue()

        @JvmStatic
        fun getValueNamed(name: String?): String = node(name).getValue()

        @JvmStatic
        fun getMessages(): List<String> = node(null).getMessages()

        @JvmStatic
        fun getMessagesNamed(name: String?): List<String> = node(name).getMessages()

        @JvmStatic
        fun getMessageCountNamed(name: String?): Long = node(name).getMessageCount()

        @JvmStatic
        fun clearRecentMessages() {
            node(null).clearRecentMessages()
        }

        @JvmStatic
        fun clearRecentMessagesNamed(name: String?) {
            node(name).clearRecentMessages()
        }

        @JvmStatic
        fun getIP(): String = node(null).getIP()

        @JvmStatic
        fun getPort(): String = node(null).getPort()

        @JvmStatic
        fun isPortInUse(port: Int): Boolean {
            return try {
                ServerSocket().use { socket ->
                    socket.reuseAddress = true
                    socket.bind(InetSocketAddress(port))
                    false
                }
            } catch (e: Exception) {
                true
            }
        }

        @JvmStatic
        fun pingHost(ip: String, port: Int, timeoutMs: Int = TcpNode.PING_TIMEOUT_MS): PingResult {
            return TcpNode.pingHost(ip, port, timeoutMs)
        }

        @JvmStatic
        fun pingNamed(name: String?, timeoutMs: Int = TcpNode.PING_TIMEOUT_MS): PingResult {
            return node(name).pingPeer(timeoutMs)
        }

        @JvmStatic
        fun fireTcpMessageEvent(message: String) {
            fireTcpMessageEvent(message, null)
        }

        @JvmStatic
        fun fireTcpMessageEvent(message: String, serverName: String?) {
            val listener = org.catrobat.catroid.stage.StageActivity.getActiveStageListener() ?: return
            val activity = org.catrobat.catroid.stage.StageActivity.activeStageActivity.get() ?: return
            val project = org.catrobat.catroid.ProjectManager.getInstance().currentProject ?: return
            val have = normServerKey(serverName)
            activity.runOnUiThread {
                try {
                    for (sprite in listener.spritesFromStage) {
                        for (script in sprite.scriptList) {
                            if (script is WhenTcpMessageScript) {
                                val brick = script.scriptBrick as? org.catrobat.catroid.content.bricks.WhenTcpMessageBrick
                                if (brick != null) {
                                    if (brick.resolveServerName(sprite).trim() != have) {
                                        continue
                                    }
                                    val vars = brick.getVariables().filterNotNull()
                                    if (vars.size > 1 && message.contains(VALUE_SEPARATOR)) {
                                        val parts = message.split(VALUE_SEPARATOR)
                                        for (i in vars.indices) {
                                            parts.getOrNull(i)?.let { vars[i].value = it }
                                        }
                                    } else {
                                        vars.firstOrNull()?.value = message
                                    }
                                }
                                sprite.look.addAction(sprite.createSequenceAction(script))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("LocalServer", e.message.orEmpty())
                }
            }
        }

        @JvmStatic
        fun fireTcpDisconnectedEvent(reason: String) {
            fireTcpDisconnectedEvent(reason, null)
        }

        @JvmStatic
        fun fireTcpDisconnectedEvent(reason: String, serverName: String?) {
            val listener = org.catrobat.catroid.stage.StageActivity.getActiveStageListener() ?: return
            val activity = org.catrobat.catroid.stage.StageActivity.activeStageActivity.get() ?: return
            val have = normServerKey(serverName)
            activity.runOnUiThread {
                try {
                    for (sprite in listener.spritesFromStage) {
                        for (script in sprite.scriptList) {
                            if (script is WhenTcpDisconnectedScript) {
                                val brick = script.scriptBrick as? org.catrobat.catroid.content.bricks.WhenTcpDisconnectedBrick
                                if (brick != null && brick.resolveServerName(sprite).trim() != have) {
                                    continue
                                }
                                sprite.look.addAction(sprite.createSequenceAction(script))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("LocalServer", e.message.orEmpty())
                }
            }
        }
    }
}
