package org.catrobat.catroid.content

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress

data class UdpMessage(val seq: Long, val text: String, val fromIp: String, val fromPort: Int)

class UdpEndpoint(val name: String) {

    @Volatile var timeoutMs: Int = 3000

    private val transport: UdpTransport =
        if (UdpNative.available) UdpNativeTransport() else UdpJvmTransport()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var pollJob: Job? = null
    private val messages = ArrayDeque<UdpMessage>()

    fun start(port: Int): Boolean {
        pollJob?.cancel()
        pollJob = null
        clearMessages()
        val ok = transport.start(port)
        if (ok) {
            pollJob = scope.launch {
                while (transport.isRunning()) {
                    drainTransport()
                    delay(POLL_MS)
                }
                drainTransport()
            }
        }
        return ok
    }

    fun stop() {
        pollJob?.cancel()
        pollJob = null
        transport.stop()
    }

    fun connect(ip: String, port: Int) {
        transport.connect(ip, port)
    }

    fun disconnectPeer() {
        transport.disconnectPeer()
    }

    fun getPeerIp(): String = transport.getPeerIp()
    fun getPeerPort(): Int = transport.getPeerPort()

    fun sendTo(ip: String, port: Int, text: String): Boolean {
        return transport.sendTo(ip, port, text)
    }

    fun sendToPeer(text: String): Boolean {
        return transport.sendToPeer(text)
    }

    fun getMessages(): List<String> {
        synchronized(messages) {
            return messages.map { it.text }
        }
    }

    fun getValue(): String {
        synchronized(messages) {
            return messages.lastOrNull()?.text ?: ""
        }
    }

    fun clearMessages() {
        synchronized(messages) {
            messages.clear()
        }
    }

    fun getPort(): String = transport.getPort()
    fun isRunning(): Boolean = transport.isRunning()
    fun usesNative(): Boolean = transport is UdpNativeTransport

    fun pingPeer(timeout: Int = timeoutMs): PingResult {
        val ip = transport.getPeerIp()
        val port = transport.getPeerPort()
        if (ip.isEmpty() || port < 0) {
            return PingResult(false, -1)
        }
        return pingHost(ip, port, timeout)
    }

    private suspend fun drainTransport() {
        val fresh = try {
            transport.drain()
        } catch (e: Exception) {
            return
        }
        if (fresh.isEmpty()) {
            return
        }
        synchronized(messages) {
            for (m in fresh) {
                messages.addLast(m)
                while (messages.size > MAX_MESSAGES) {
                    messages.removeFirst()
                }
            }
        }
        for (m in fresh) {
            UdpManager.fireUdpMessageEvent(m.text, m.fromIp, m.fromPort, name)
        }
    }

    companion object {
        const val TAG = "UdpEndpoint"
        const val POLL_MS = 30L
        const val MAX_MESSAGES = 50

        fun pingHost(ip: String, port: Int, timeoutMs: Int = 3000): PingResult {
            val token = System.currentTimeMillis().toString() + "-" + (Math.random() * Int.MAX_VALUE).toInt().toString()
            val start = System.currentTimeMillis()
            var socket: DatagramSocket? = null
            try {
                socket = DatagramSocket()
                socket.broadcast = true
                socket.soTimeout = timeoutMs
                val out = (LocalServer.PING_PREFIX + token).toByteArray(Charsets.UTF_8)
                socket.send(DatagramPacket(out, out.size, InetSocketAddress(ip, port)))
                val buf = ByteArray(256)
                val packet = DatagramPacket(buf, buf.size)
                socket.receive(packet)
                val reply = String(packet.data, 0, packet.length, Charsets.UTF_8)
                if (reply == LocalServer.PONG_PREFIX + token) {
                    return PingResult(true, System.currentTimeMillis() - start)
                }
            } catch (e: Exception) {
                Log.w(TAG, e.message.orEmpty())
            } finally {
                try {
                    socket?.close()
                } catch (e: Exception) {
                    Log.w(TAG, e.message.orEmpty())
                }
            }
            return PingResult(false, -1)
        }
    }
}
