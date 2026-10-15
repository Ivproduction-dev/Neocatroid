package org.catrobat.catroid.content

class UdpNativeTransport : UdpTransport {

    private val handle: Long = UdpNative.nativeCreate()

    override fun start(port: Int): Boolean {
        return UdpNative.nativeStart(handle, port)
    }

    override fun stop() {
        UdpNative.nativeStop(handle)
    }

    override fun connect(ip: String, port: Int) {
        UdpNative.nativeConnect(handle, ip, port)
    }

    override fun disconnectPeer() {
        UdpNative.nativeDisconnectPeer(handle)
    }

    override fun getPeerIp(): String {
        return try {
            UdpNative.nativeGetPeerIp(handle)
        } catch (e: UnsatisfiedLinkError) {
            ""
        }
    }

    override fun getPeerPort(): Int {
        return try {
            UdpNative.nativeGetPeerPort(handle)
        } catch (e: UnsatisfiedLinkError) {
            -1
        }
    }

    override fun sendTo(ip: String, port: Int, text: String): Boolean {
        return UdpNative.nativeSendTo(handle, ip, port, text)
    }

    override fun sendToPeer(text: String): Boolean {
        return UdpNative.nativeSendToPeer(handle, text)
    }

    override fun drain(): List<UdpMessage> {
        return try {
            UdpNative.nativeDrain(handle)?.toList() ?: emptyList()
        } catch (e: UnsatisfiedLinkError) {
            emptyList()
        }
    }

    override fun getPort(): String {
        return try {
            val port = UdpNative.nativeGetBoundPort(handle)
            if (port < 0) "NaN" else port.toString()
        } catch (e: UnsatisfiedLinkError) {
            "NaN"
        }
    }

    override fun isRunning(): Boolean {
        return try {
            UdpNative.nativeIsRunning(handle)
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }
}
