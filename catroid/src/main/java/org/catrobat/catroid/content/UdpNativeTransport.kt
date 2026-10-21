package org.catrobat.catroid.content

class UdpNativeTransport : UdpTransport {

    @Volatile private var handle: Long = try {
        UdpNative.nativeCreate()
    } catch (e: UnsatisfiedLinkError) {
        0L
    }

    override fun start(port: Int): Boolean {
        if (handle == 0L) {
            return false
        }
        return try {
            UdpNative.nativeStart(handle, port)
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }

    override fun stop() {
        if (handle == 0L) {
            return
        }
        try {
            UdpNative.nativeStop(handle)
        } catch (e: UnsatisfiedLinkError) {
        }
    }

    override fun destroy() {
        val current = handle
        handle = 0L
        if (current == 0L) {
            return
        }
        try {
            UdpNative.nativeDestroy(current)
        } catch (e: UnsatisfiedLinkError) {
        }
    }

    override fun connect(ip: String, port: Int) {
        if (handle == 0L) {
            return
        }
        try {
            UdpNative.nativeConnect(handle, ip, port)
        } catch (e: UnsatisfiedLinkError) {
        }
    }

    override fun disconnectPeer() {
        if (handle == 0L) {
            return
        }
        try {
            UdpNative.nativeDisconnectPeer(handle)
        } catch (e: UnsatisfiedLinkError) {
        }
    }

    override fun getPeerIp(): String {
        if (handle == 0L) {
            return ""
        }
        return try {
            UdpNative.nativeGetPeerIp(handle)
        } catch (e: UnsatisfiedLinkError) {
            ""
        }
    }

    override fun getPeerPort(): Int {
        if (handle == 0L) {
            return -1
        }
        return try {
            UdpNative.nativeGetPeerPort(handle)
        } catch (e: UnsatisfiedLinkError) {
            -1
        }
    }

    override fun sendTo(ip: String, port: Int, text: String): Boolean {
        if (handle == 0L) {
            return false
        }
        return try {
            UdpNative.nativeSendTo(handle, ip, port, text)
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }

    override fun sendToPeer(text: String): Boolean {
        if (handle == 0L) {
            return false
        }
        return try {
            UdpNative.nativeSendToPeer(handle, text)
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }

    override fun drain(): List<UdpMessage> {
        if (handle == 0L) {
            return emptyList()
        }
        return try {
            UdpNative.nativeDrain(handle)?.toList() ?: emptyList()
        } catch (e: UnsatisfiedLinkError) {
            emptyList()
        }
    }

    override fun getPort(): String {
        if (handle == 0L) {
            return "NaN"
        }
        return try {
            val port = UdpNative.nativeGetBoundPort(handle)
            if (port < 0) "NaN" else port.toString()
        } catch (e: UnsatisfiedLinkError) {
            "NaN"
        }
    }

    override fun isRunning(): Boolean {
        if (handle == 0L) {
            return false
        }
        return try {
            UdpNative.nativeIsRunning(handle)
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }
}
