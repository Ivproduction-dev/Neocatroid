package org.catrobat.catroid.content

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket

data class PingResult(val ok: Boolean, val ms: Long)

class TcpNode(val name: String) {

    @Volatile var clientLimit: Int = 10
    @Volatile var serverTimeoutSeconds: Int = 30

    @Volatile private var connectedPort: String? = null
    @Volatile private var connectedIP: String? = null
    @Volatile private var isRunning = false
    @Volatile private var clientSocket: Socket? = null

    private var serverSocket: ServerSocket? = null
    private val clients = mutableListOf<Socket>()
    private val outputStreams = mutableListOf<OutputStream>()

    private val recentMessages = ArrayDeque<String>()
    private var lastMessageAt = 0L

    private var serverJob: Job? = null
    private val sendJobs = mutableListOf<Job>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var sessionCounter = 0

    fun startOrJoin(ip: String?, port: String) {
        if (ip.isNullOrEmpty()) {
            stop()
            synchronized(recentMessages) {
                recentMessages.clear()
            }
            val session = ++sessionCounter
            isRunning = true
            serverJob = coroutineScope.launch {
                try {
                    startServer(port, session)
                } catch (e: Exception) {
                    if (isRunning) {
                        Log.e(TAG, e.message.orEmpty(), e)
                    }
                } finally {
                    if (session == sessionCounter) {
                        stop()
                    }
                }
            }
        } else {
            isRunning = true
            val session = sessionCounter
            coroutineScope.launch {
                try {
                    connectToServer(ip, port, session)
                } catch (e: Exception) {
                    Log.e(TAG, e.message.orEmpty())
                }
            }
        }
    }

    fun send(value: String) {
        sendAll(listOf(value))
    }

    fun sendAll(values: List<String>) {
        if (values.any { it.startsWith(ALL_ECHO_PREFIX) } && serverSocket != null) {
            synchronized(recentMessages) {
                for (v in values) {
                    if (v.startsWith(ALL_ECHO_PREFIX)) {
                        val clean = v.removePrefix(ALL_ECHO_PREFIX)
                        recentMessages.addLast(clean)
                        while (recentMessages.size > MAX_MESSAGES) recentMessages.removeFirst()
                        lastMessageAt = System.currentTimeMillis()
                    }
                }
            }
        }
        val job = coroutineScope.launch {
            val deadline = System.currentTimeMillis() + 2000
            val pending = values.toMutableList()
            while (System.currentTimeMillis() < deadline && isRunning) {
                val outs: List<OutputStream>
                synchronized(clients) {
                    outs = outputStreams.toList()
                }
                if (outs.isEmpty()) {
                    delay(10)
                    continue
                }
                for (value in pending) {
                    val data = (value + "\n").toByteArray(Charsets.UTF_8)
                    for (out in outs) {
                        try {
                            out.write(data)
                            out.flush()
                        } catch (e: Exception) {
                            Log.e(TAG, e.message.orEmpty())
                        }
                    }
                }
                pending.clear()
                break
            }
            if (pending.isNotEmpty()) {
                Log.e(TAG, "no connection")
            }
        }
        synchronized(sendJobs) {
            sendJobs.add(job)
            job.invokeOnCompletion {
                synchronized(sendJobs) {
                    sendJobs.remove(job)
                }
            }
        }
    }

    fun pingPeer(timeoutMs: Int = PING_TIMEOUT_MS): PingResult {
        val ip = connectedIP
        val port = connectedPort?.toIntOrNull()
        if (ip == null || port == null) {
            return PingResult(false, -1)
        }
        return pingHost(ip, port, timeoutMs)
    }

    @Synchronized
    fun stop() {
        sessionCounter++
        if (!isRunning) {
            return
        }
        isRunning = false
        try {
            synchronized(sendJobs) {
                for (job in sendJobs) {
                    job.cancel()
                }
                sendJobs.clear()
            }
            synchronized(clients) {
                for (out in outputStreams) {
                    try {
                        out.close()
                    } catch (e: IOException) {
                        Log.w(TAG, e.message.orEmpty())
                    }
                }
                for (socket in clients) {
                    try {
                        socket.close()
                    } catch (e: IOException) {
                        Log.w(TAG, e.message.orEmpty())
                    }
                }
                clients.clear()
                outputStreams.clear()
            }
            serverSocket?.close()
        } catch (e: IOException) {
            Log.w(TAG, e.message.orEmpty())
        } finally {
            serverSocket = null
            serverJob?.cancel()
            serverJob = null
            connectedIP = null
            connectedPort = null
            clientSocket = null
        }
    }

    fun disconnectFromServer() {
        val socket = clientSocket
        if (socket == null) {
            return
        }
        clientSocket = null
        try {
            socket.getOutputStream().write("nil\n".toByteArray(Charsets.UTF_8))
            socket.getOutputStream().flush()
        } catch (e: IOException) {
            Log.w(TAG, e.message.orEmpty())
        }
        removeClient(socket)
    }

    fun getValue(): String {
        synchronized(recentMessages) {
            return recentMessages.lastOrNull() ?: ""
        }
    }

    fun getMessages(): List<String> {
        synchronized(recentMessages) {
            return recentMessages.toList()
        }
    }

    fun clearRecentMessages() {
        synchronized(recentMessages) {
            recentMessages.clear()
        }
    }

    fun getIP(): String = connectedIP ?: "NaN"
    fun getPort(): String = connectedPort ?: "NaN"

    private suspend fun startServer(port: String, session: Int) = withContext(Dispatchers.IO) {
        if (session != sessionCounter) {
            return@withContext
        }
        val server = ServerSocket(port.toInt())
        serverSocket = server
        server.soTimeout = serverTimeoutSeconds * 1000
        val ip = getLocalIPAddress()
        if (session != sessionCounter || !isRunning) {
            try {
                server.close()
            } catch (e: IOException) {
                Log.w(TAG, e.message.orEmpty())
            }
            serverSocket = null
            return@withContext
        }
        connectedPort = port
        connectedIP = ip
        while (isRunning && session == sessionCounter) {
            try {
                val socket = server.accept()
                if (clients.size >= clientLimit) {
                    socket.close()
                    continue
                }
                addClient(socket)
                launch { setupConnection(socket) }
            } catch (e: java.net.SocketTimeoutException) {
                break
            }
        }
    }

    private suspend fun connectToServer(ip: String, port: String, session: Int) = withContext(Dispatchers.IO) {
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(ip, port.toInt()), 10000)
        } catch (e: Exception) {
            try {
                socket.close()
            } catch (closeError: IOException) {
                Log.w(TAG, closeError.message.orEmpty())
            }
            throw e
        }
        if (session != sessionCounter || !isRunning) {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.w(TAG, e.message.orEmpty())
            }
            return@withContext
        }
        connectedIP = ip
        connectedPort = port
        addClient(socket)
        clientSocket = socket
        setupConnection(socket)
    }

    private fun addClient(socket: Socket) {
        synchronized(clients) {
            clients.add(socket)
            outputStreams.add(socket.getOutputStream())
        }
    }

    private fun removeClient(socket: Socket) {
        synchronized(clients) {
            val index = clients.indexOf(socket)
            if (index >= 0) {
                clients.removeAt(index)
                if (index < outputStreams.size) {
                    outputStreams.removeAt(index)
                }
            }
        }
        try {
            socket.close()
        } catch (e: IOException) {
            Log.w(TAG, e.message.orEmpty())
        }
    }

    private suspend fun setupConnection(socket: Socket) = withContext(Dispatchers.IO) {
        socket.soTimeout = serverTimeoutSeconds * 1000
        try {
            val input = BufferedInputStream(socket.getInputStream())
            while (isRunning && !socket.isClosed) {
                val message = readFramedLine(input)
                if (message == null) {
                    break
                }
                if (message.isEmpty()) {
                    continue
                }
                if (message == "nil") {
                    clearRecentMessages()
                    continue
                }
                if (message.startsWith(LocalServer.PING_PREFIX)) {
                    try {
                        val out = socket.getOutputStream()
                        out.write((LocalServer.PONG_PREFIX + message.removePrefix(LocalServer.PING_PREFIX) + "\n").toByteArray(Charsets.UTF_8))
                        out.flush()
                    } catch (e: Exception) {
                        break
                    }
                    continue
                }
                if (message.startsWith(LocalServer.PONG_PREFIX)) {
                    continue
                }
                val isBroadcastEcho = message.startsWith(ALL_ECHO_PREFIX)
                val isBroadcast = isBroadcastEcho || message.startsWith(ALL_PREFIX)
                val cleanMessage = when {
                    isBroadcastEcho -> message.removePrefix(ALL_ECHO_PREFIX)
                    isBroadcast -> message.removePrefix(ALL_PREFIX)
                    else -> message
                }
                val isDuplicate: Boolean
                synchronized(recentMessages) {
                    val now = System.currentTimeMillis()
                    isDuplicate = recentMessages.lastOrNull() == cleanMessage &&
                            now - lastMessageAt < DUPLICATE_WINDOW_MS
                    if (!isDuplicate) {
                        recentMessages.addLast(cleanMessage)
                        while (recentMessages.size > MAX_MESSAGES) {
                            recentMessages.removeFirst()
                        }
                        lastMessageAt = now
                    }
                }
                if (!isDuplicate) {
                    LocalServer.fireTcpMessageEvent(cleanMessage, name)
                    if (isBroadcast && serverSocket != null) {
                        val data = (cleanMessage + "\n").toByteArray(Charsets.UTF_8)
                        val outs: List<OutputStream>
                        val senderIndex: Int
                        synchronized(clients) {
                            outs = outputStreams.toList()
                            senderIndex = clients.indexOf(socket)
                        }
                        for (i in outs.indices) {
                            if (!isBroadcastEcho && i == senderIndex) continue
                            try {
                                outs[i].write(data)
                                outs[i].flush()
                            } catch (e: Exception) {
                                Log.e(TAG, e.message.orEmpty())
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            if (isRunning && e is java.net.SocketTimeoutException) {
                LocalServer.fireTcpDisconnectedEvent("timeout", name)
            } else if (isRunning) {
                Log.e(TAG, e.message.orEmpty())
            }
        } finally {
            removeClient(socket)
        }
    }

    private fun readFramedLine(input: InputStream, maxBytes: Int = MAX_LINE_BYTES): String? {
        while (true) {
            val buf = ByteArrayOutputStream(512)
            var anyByte = false
            var discarded = false
            while (true) {
                val b = input.read()
                if (b == -1) {
                    if (!anyByte && buf.size() == 0) {
                        return null
                    }
                    break
                }
                anyByte = true
                if (b == 10) {
                    break
                }
                buf.write(b)
                if (buf.size() > maxBytes) {
                    var d = input.read()
                    while (d != -1 && d != 10) {
                        d = input.read()
                    }
                    if (d == -1) {
                        return null
                    }
                    discarded = true
                    break
                }
            }
            if (discarded) {
                continue
            }
            var bytes = buf.toByteArray()
            if (bytes.isNotEmpty() && bytes[bytes.size - 1] == 13.toByte()) {
                bytes = bytes.copyOf(bytes.size - 1)
            }
            return String(bytes, Charsets.UTF_8)
        }
    }

    private fun getLocalIPAddress(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .flatMap { it.inetAddresses.toList() }
                .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
                ?.hostAddress
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        const val TAG = "LocalServer"
        const val DUPLICATE_WINDOW_MS = 100L
        const val MAX_LINE_BYTES = 262144
        const val MAX_MESSAGES = 50
        const val ALL_PREFIX = "ALL:"
        const val ALL_ECHO_PREFIX = "ALL_ECHO:"
        const val PING_TIMEOUT_MS = 3000

        fun pingHost(ip: String, port: Int, timeoutMs: Int = PING_TIMEOUT_MS): PingResult {
            val token = System.currentTimeMillis().toString() + "-" + (Math.random() * Int.MAX_VALUE).toInt().toString()
            val start = System.currentTimeMillis()
            var socket: Socket? = null
            try {
                socket = Socket()
                socket.connect(InetSocketAddress(ip, port), timeoutMs)
                socket.soTimeout = timeoutMs
                val out = socket.getOutputStream()
                out.write((LocalServer.PING_PREFIX + token + "\n").toByteArray(Charsets.UTF_8))
                out.flush()
                val reply = readSingleLine(socket.getInputStream(), 256, timeoutMs)
                if (reply == LocalServer.PONG_PREFIX + token) {
                    return PingResult(true, System.currentTimeMillis() - start)
                }
            } catch (e: Exception) {
                Log.w(TAG, e.message.orEmpty())
            } finally {
                try {
                    socket?.close()
                } catch (e: IOException) {
                    Log.w(TAG, e.message.orEmpty())
                }
            }
            return PingResult(false, -1)
        }

        private fun readSingleLine(input: InputStream, maxBytes: Int, timeoutMs: Int): String? {
            val deadline = System.currentTimeMillis() + timeoutMs
            val buf = ByteArrayOutputStream(64)
            while (true) {
                if (System.currentTimeMillis() > deadline) {
                    return null
                }
                val b = try {
                    input.read()
                } catch (e: IOException) {
                    return if (buf.size() == 0) null else String(buf.toByteArray(), Charsets.UTF_8).trimEnd('\r')
                }
                if (b == -1) {
                    return if (buf.size() == 0) null else String(buf.toByteArray(), Charsets.UTF_8).trimEnd('\r')
                }
                if (b == 10) {
                    break
                }
                buf.write(b)
                if (buf.size() > maxBytes) {
                    return null
                }
            }
            var bytes = buf.toByteArray()
            if (bytes.isNotEmpty() && bytes[bytes.size - 1] == 13.toByte()) {
                bytes = bytes.copyOf(bytes.size - 1)
            }
            return String(bytes, Charsets.UTF_8)
        }
    }
}
