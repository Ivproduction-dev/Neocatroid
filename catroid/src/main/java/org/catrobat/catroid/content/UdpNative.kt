package org.catrobat.catroid.content

import android.util.Log

object UdpNative {
    val available: Boolean

    init {
        var ok = false
        try {
            System.loadLibrary("net_udp")
            ok = true
        } catch (e: UnsatisfiedLinkError) {
            ok = false
        } catch (e: SecurityException) {
            ok = false
        }
        available = ok
        if (!ok) {
            Log.w("UdpNative", "net_udp missing")
        }
    }

    external fun nativeCreate(): Long
    external fun nativeDestroy(handle: Long)
    external fun nativeStart(handle: Long, port: Int): Boolean
    external fun nativeStop(handle: Long)
    external fun nativeConnect(handle: Long, ip: String, port: Int)
    external fun nativeDisconnectPeer(handle: Long)
    external fun nativeGetPeerIp(handle: Long): String
    external fun nativeGetPeerPort(handle: Long): Int
    external fun nativeGetBoundPort(handle: Long): Int
    external fun nativeIsRunning(handle: Long): Boolean
    external fun nativeSendTo(handle: Long, ip: String, port: Int, text: String): Boolean
    external fun nativeSendToPeer(handle: Long, text: String): Boolean
    external fun nativeDrain(handle: Long): Array<UdpMessage>?
}
