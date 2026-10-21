package org.catrobat.catroid.content

import android.util.Log

class UdpManager private constructor() {
    companion object {
        const val DEFAULT_NODE = ""

        private val nodes = mutableMapOf<String, UdpEndpoint>()

        @JvmStatic
        fun node(name: String?): UdpEndpoint {
            val key = LocalServer.normServerKey(name)
            synchronized(nodes) {
                return nodes.getOrPut(key) { UdpEndpoint(key) }
            }
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
        fun shutdown() {
            var endpoints: List<UdpEndpoint> = emptyList()
            synchronized(nodes) {
                endpoints = nodes.values.toList()
                nodes.clear()
            }
            for (n in endpoints) {
                n.shutdown()
            }
        }

        @JvmStatic
        fun fireUdpMessageEvent(text: String, fromIp: String, fromPort: Int) {
            fireUdpMessageEvent(text, fromIp, fromPort, null)
        }

        @JvmStatic
        fun fireUdpMessageEvent(text: String, fromIp: String, fromPort: Int, endpointName: String?) {
            val listener = org.catrobat.catroid.stage.StageActivity.getActiveStageListener() ?: return
            val activity = org.catrobat.catroid.stage.StageActivity.activeStageActivity.get() ?: return
            val project = org.catrobat.catroid.ProjectManager.getInstance().currentProject ?: return
            val have = endpointName?.trim().orEmpty()
            activity.runOnUiThread {
                try {
                    for (sprite in listener.spritesFromStage) {
                        for (script in sprite.scriptList) {
                            if (script is WhenUdpMessageScript) {
                                val brick = script.scriptBrick as? org.catrobat.catroid.content.bricks.WhenUdpMessageBrick
                                if (brick != null) {
                                    if (brick.resolveServerName(sprite).trim() != have) {
                                        continue
                                    }
                                    val vars = brick.getVariables().filterNotNull()
                                    if (vars.size > 1 && text.contains(LocalServer.VALUE_SEPARATOR)) {
                                        val parts = text.split(LocalServer.VALUE_SEPARATOR)
                                        for (i in vars.indices) {
                                            parts.getOrNull(i)?.let { vars[i].value = it }
                                        }
                                    } else {
                                        vars.firstOrNull()?.value = text
                                    }
                                }
                                sprite.look.addAction(sprite.createSequenceAction(script))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("UdpManager", e.message.orEmpty())
                }
            }
        }
    }
}
