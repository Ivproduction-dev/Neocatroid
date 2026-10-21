package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.LocalServer
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.UdpManager
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ListenUdpAction() : Action() {
    companion object {
        private const val POLL_INTERVAL_MS = 30L
        private const val MAX_TASKS = 32

        private var sharedScheduler: ScheduledExecutorService? = null
        private val tasks = mutableListOf<ScheduledFuture<*>>()

        @Synchronized
        private fun scheduler(): ScheduledExecutorService {
            var scheduler = sharedScheduler
            if (scheduler == null || scheduler.isShutdown) {
                scheduler = Executors.newSingleThreadScheduledExecutor()
                sharedScheduler = scheduler
            }
            return scheduler
        }

        @Synchronized
        private fun pruneTasks() {
            while (tasks.size >= MAX_TASKS) {
                tasks.removeAt(0).cancel(false)
            }
        }

        @Synchronized
        private fun register(action: ListenUdpAction) {
            pruneTasks()
            action.future = scheduler().scheduleAtFixedRate(
                { action.poll() }, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
            action.future?.let { tasks.add(it) }
        }

        @Synchronized
        private fun registerOneShot(action: ListenUdpAction) {
            pruneTasks()
            action.future = scheduler().scheduleAtFixedRate({
                if (action.pollOnce()) {
                    action.future?.cancel(false)
                }
            }, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS)
            action.future?.let { tasks.add(it) }
        }

        @Synchronized
        fun stopAll() {
            for (task in tasks) {
                task.cancel(false)
            }
            tasks.clear()
            sharedScheduler?.shutdownNow()
            sharedScheduler = null
        }
    }

    var scope: Scope? = null
    var variables: List<UserVariable>? = null
    var listenMode: Int = 0
    var serverName: Formula? = null

    @Volatile private var future: ScheduledFuture<*>? = null
    private var deliveredCount = 0L

    override fun act(delta: Float): Boolean {
        val vars = variables?.filterNotNull() ?: return true
        if (vars.isEmpty()) {
            return true
        }
        future?.cancel(false)
        future = null
        if (listenMode == 1) {
            registerOneShot(this)
        } else {
            register(this)
        }
        return true
    }

    fun poll() {
        pollOnce()
    }

    private fun pollOnce(): Boolean {
        val vars = variables?.filterNotNull() ?: return false
        if (vars.isEmpty()) {
            return false
        }
        val name = serverName?.interpretString(scope).orEmpty()
        val endpoint = UdpManager.node(name)
        val total = endpoint.getReceivedCount()
        if (total <= deliveredCount) {
            return false
        }
        val messages = endpoint.getMessages()
        deliveredCount = total
        if (messages.isEmpty()) {
            return false
        }
        val last = messages.last()
        if (last.indexOf(LocalServer.VALUE_SEPARATOR) >= 0) {
            val parts = last.split(LocalServer.VALUE_SEPARATOR)
            for (i in vars.indices) {
                val part = parts.getOrNull(i) ?: continue
                vars[i].value = part
            }
        } else {
            val k = vars.size
            val start = maxOf(0, messages.size - k)
            for (i in 0 until k) {
                val index = start + i
                if (index < messages.size) {
                    vars[i].value = messages[index]
                }
            }
        }
        return true
    }
}
