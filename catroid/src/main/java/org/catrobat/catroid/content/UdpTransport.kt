package org.catrobat.catroid.content

interface UdpTransport {
    fun start(port: Int): Boolean
    fun stop()
    fun connect(ip: String, port: Int)
    fun disconnectPeer()
    fun getPeerIp(): String
    fun getPeerPort(): Int
    fun sendTo(ip: String, port: Int, text: String): Boolean
    fun sendToPeer(text: String): Boolean
    fun drain(): List<UdpMessage>
    fun getPort(): String
    fun isRunning(): Boolean
}
