package org.catrobat.catroid.content

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import java.io.IOException

class UdpJvmTransport : UdpTransport {

    @Volatile private var running = false
    @Volatile private var boundPort: Int? = null
    @Volatile private var peerIp: String? = null
    @Volatile private var peerPort: Int? = null

    private var socket: DatagramSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var recvJob: Job? = null
    private val queue = ArrayDeque<UdpMessage>()
    private var lastSeq = -2L
    private var lastText = ""
    private var sendSeq = 0L

    @Synchronized
    override fun start(port: Int): Boolean {
        stop()
        return try {
            val s = DatagramSocket(null)
            s.reuseAddress = true
            s.broadcast = true
            s.bind(InetSocketAddress(port))
            socket = s
            boundPort = port
            running = true
            recvJob = scope.launch { receiveLoop(s) }
            true
        } catch (e: Exception) {
            Log.w(TAG, e.message.orEmpty())
            try {
                socket?.close()
            } catch (ignored: Exception) {
            }
            socket = null
            running = false
            false
        }
    }

    @Synchronized
    override fun stop() {
        running = false
        recvJob?.cancel()
        recvJob = null
        try {
            socket?.close()
        } catch (e: Exception) {
            Log.w(TAG, e.message.orEmpty())
        }
        socket = null
        boundPort = null
    }

    override fun connect(ip: String, port: Int) {
        peerIp = ip
        peerPort = port
    }

    override fun disconnectPeer() {
        peerIp = null
        peerPort = null
    }

    override fun getPeerIp(): String = peerIp ?: ""
    override fun getPeerPort(): Int = peerPort ?: -1

    override fun sendTo(ip: String, port: Int, text: String): Boolean {
        val payload = UDP_PREFIX + nextSeq() + ":" + text
        val data = payload.toByteArray(Charsets.UTF_8)
        if (data.size > MAX_DATAGRAM) {
            return false
        }
        val target = InetSocketAddress(ip, port)
        val s = socket
        return try {
            if (s != null && running) {
                s.send(DatagramPacket(data, data.size, target))
            } else {
                DatagramSocket().use { tmp ->
                    tmp.broadcast = true
                    tmp.send(DatagramPacket(data, data.size, target))
                }
            }
            true
        } catch (e: Exception) {
            Log.w(TAG, e.message.orEmpty())
            false
        }
    }

    override fun sendToPeer(text: String): Boolean {
        val ip = peerIp
        val port = peerPort
        if (ip == null || port == null) {
            return false
        }
        return sendTo(ip, port, text)
    }

    override fun drain(): List<UdpMessage> {
        synchronized(queue) {
            val taken = queue.toList()
            queue.clear()
            return taken
        }
    }

    override fun getPort(): String = boundPort?.toString() ?: "NaN"
    override fun isRunning(): Boolean = running

    @Synchronized
    private fun nextSeq(): Long {
        sendSeq++
        return sendSeq
    }

    private suspend fun receiveLoop(s: DatagramSocket) = withContext(Dispatchers.IO) {
        val buf = ByteArray(MAX_DATAGRAM)
        while (running) {
            try {
                val packet = DatagramPacket(buf, buf.size)
                s.receive(packet)
                val text = String(packet.data, 0, packet.length, Charsets.UTF_8)
                val addr = packet.socketAddress as? InetSocketAddress
                handleDatagram(text, addr?.hostString ?: "", addr?.port ?: -1, s)
            } catch (e: SocketTimeoutException) {
            } catch (e: IOException) {
                if (running) {
                    Log.w(TAG, e.message.orEmpty())
                }
                break
            } catch (e: Exception) {
                if (running) {
                    Log.w(TAG, e.message.orEmpty())
                }
                break
            }
        }
    }

    private fun handleDatagram(text: String, fromIp: String, fromPort: Int, s: DatagramSocket) {
        if (text.isEmpty()) {
            return
        }
        if (text.startsWith(LocalServer.PING_PREFIX)) {
            try {
                val reply = (LocalServer.PONG_PREFIX + text.removePrefix(LocalServer.PING_PREFIX)).toByteArray(Charsets.UTF_8)
                s.send(DatagramPacket(reply, reply.size, InetSocketAddress(fromIp, fromPort)))
            } catch (e: Exception) {
                Log.w(TAG, e.message.orEmpty())
            }
            return
        }
        if (text.startsWith(LocalServer.PONG_PREFIX)) {
            return
        }
        var seq = -1L
        var payload = text
        if (text.startsWith(UDP_PREFIX)) {
            val cut = text.indexOf(':', UDP_PREFIX.length)
            if (cut > 0) {
                seq = text.substring(UDP_PREFIX.length, cut).toLongOrNull() ?: -1L
                payload = text.substring(cut + 1)
            }
        }
        synchronized(queue) {
            if (seq == lastSeq && payload == lastText) {
                return
            }
            lastSeq = seq
            lastText = payload
            queue.addLast(UdpMessage(seq, payload, fromIp, fromPort))
            while (queue.size > MAX_MESSAGES) {
                queue.removeFirst()
            }
        }
    }

    companion object {
        const val TAG = "UdpJvmTransport"
        const val UDP_PREFIX = "U"
        const val MAX_DATAGRAM = 8192
        const val MAX_MESSAGES = 200
    }
}
