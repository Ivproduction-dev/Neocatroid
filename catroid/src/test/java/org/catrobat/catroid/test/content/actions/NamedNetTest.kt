package org.catrobat.catroid.test.content.actions

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.LocalServer
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.UdpEndpoint
import org.catrobat.catroid.content.UdpManager
import org.catrobat.catroid.content.actions.ClearTcpVariableAction
import org.catrobat.catroid.content.actions.ListenTcpServerAction
import org.catrobat.catroid.content.actions.ListenUdpAction
import org.catrobat.catroid.content.actions.SendUdpAction
import org.catrobat.catroid.content.actions.TcpPingAction
import org.catrobat.catroid.content.actions.UdpPingAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.SpriteActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.net.DatagramSocket
import java.net.ServerSocket
import java.net.Socket

@RunWith(RobolectricTestRunner::class)
class NamedNetTest {

    private lateinit var activity: SpriteActivity

    private fun freeTcpPort(): Int {
        ServerSocket(0).use { return it.localPort }
    }

    private fun freeUdpPort(): Int {
        DatagramSocket(null).use {
            it.bind(null)
            return it.localPort
        }
    }

    private fun await(timeoutMillis: Long = 8000, condition: () -> Boolean): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMillis
        while (System.currentTimeMillis() < deadline) {
            if (condition()) {
                return true
            }
            Thread.sleep(20)
        }
        return condition()
    }

    @Before
    fun setUp() {
        val controller = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = controller.get()
        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("s")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
        controller.create()
    }

    @After
    fun tearDown() {
        ListenTcpServerAction.stopAll()
        ListenUdpAction.stopAll()
        UdpManager.stopAll()
        LocalServer.stopAll()
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun namedTcpServers_areIsolated() {
        val portA = freeTcpPort()
        val portB = freeTcpPort()
        LocalServer.startOrJoin("srvA", null, portA.toString())
        LocalServer.startOrJoin("srvB", null, portB.toString())
        assertTrue(await { LocalServer.node("srvA").getPort() == portA.toString() })
        assertTrue(await { LocalServer.node("srvB").getPort() == portB.toString() })

        LocalServer.startOrJoin("a", "127.0.0.1", portA.toString())
        assertTrue(await { LocalServer.node("a").getPort() == portA.toString() })
        LocalServer.node("a").sendAll(listOf("helloA"))

        assertTrue(await { LocalServer.getMessagesNamed("srvA").contains("helloA") })
        assertTrue(LocalServer.getMessagesNamed("srvB").isEmpty())
        assertTrue(LocalServer.getMessages().isEmpty())
    }

    @Test
    fun namedTcpClientLimit_appliesPerNode() {
        val portA = freeTcpPort()
        LocalServer.startOrJoin("srvA", null, portA.toString())
        assertTrue(await { LocalServer.node("srvA").getPort() == portA.toString() })
        LocalServer.node("srvA").clientLimit = 5
        assertEquals(5, LocalServer.node("srvA").clientLimit)
        assertEquals(10, LocalServer.node("srvB").clientLimit)
        LocalServer.stopNamed("srvA")
    }

    @Test
    fun tcpPingLoopback_answersTrue() {
        val port = freeTcpPort()
        LocalServer.startOrJoin("srvA", null, port.toString())
        assertTrue(await { LocalServer.node("srvA").getPort() == port.toString() })
        val result = LocalServer.pingHost("127.0.0.1", port)
        assertTrue(result.ok)
    }

    @Test
    fun tcpPingAction_writesTrueToVariable() {
        val port = freeTcpPort()
        LocalServer.startOrJoin("srvA", null, port.toString())
        assertTrue(await { LocalServer.node("srvA").getPort() == port.toString() })

        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("s")
        project.defaultScene.addSprite(sprite)
        val scope = Scope(project, sprite, null)
        val variable = UserVariable("r")
        val action = TcpPingAction()
        action.scope = scope
        action.ip = Formula("127.0.0.1")
        action.port = Formula(port.toString())
        action.variable = variable
        action.measure = false
        action.act(1f)

        assertTrue(await { variable.value == "true" })
    }

    @Test
    fun tcpPingAction_reusedAfterResetRunsAgain() {
        val port = freeTcpPort()
        LocalServer.startOrJoin("srvA", null, port.toString())
        assertTrue(await { LocalServer.node("srvA").getPort() == port.toString() })

        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("s")
        project.defaultScene.addSprite(sprite)
        val scope = Scope(project, sprite, null)
        val variable = UserVariable("r")
        val action = TcpPingAction()
        action.scope = scope
        action.serverName = Formula("srvA")
        action.variable = variable
        action.measure = false

        action.act(1f)
        assertTrue(await { variable.value == "true" })

        variable.value = ""
        action.reset()
        action.act(1f)
        assertTrue(await { variable.value == "true" })
    }

    @Test
    fun defaultNodeAliasesZeroNameForBothStacks() {
        assertTrue(UdpManager.node("0") === UdpManager.node(""))
        assertTrue(LocalServer.node("0") === LocalServer.node(""))
    }

    @Test
    fun udpEndpointStartTwiceRebindsCleanly() {
        val portA = freeUdpPort()
        val endpoint = UdpManager.node("ua")
        assertTrue(endpoint.start(portA))
        assertTrue(endpoint.start(portA))
        val result = UdpEndpoint.pingHost("127.0.0.1", portA, 1500)
        assertTrue(result.ok)
    }

    @Test
    fun tcpListenOneShotDeliversOnlyNewMessages() {
        val port = freeTcpPort()
        LocalServer.startOrJoin("srvA", null, port.toString())
        assertTrue(await { LocalServer.node("srvA").getPort() == port.toString() })

        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("s")
        project.defaultScene.addSprite(sprite)
        val scope = Scope(project, sprite, null)
        val variable = UserVariable("msg")
        val action = ListenTcpServerAction()
        action.scope = scope
        action.serverName = Formula("srvA")
        action.listenMode = 1
        action.variables = listOf(variable)

        Socket("127.0.0.1", port).use { client ->
            client.getOutputStream().write("first\n".toByteArray())
            client.getOutputStream().flush()
        }
        assertTrue(await { LocalServer.getMessagesNamed("srvA").contains("first") })

        action.act(1f)
        assertTrue(await { variable.value == "first" })

        Socket("127.0.0.1", port).use { client ->
            client.getOutputStream().write("second\n".toByteArray())
            client.getOutputStream().flush()
        }
        assertTrue(await { LocalServer.getMessagesNamed("srvA").contains("second") })

        action.act(1f)
        assertTrue(await { variable.value == "second" })

        variable.value = ""
        action.act(1f)
        Thread.sleep(300)
        assertEquals("", variable.value)
    }

    @Test
    fun clearTcpVariableAction_emptiesVariable() {
        val variable = UserVariable("v")
        variable.value = "m"
        val action = ClearTcpVariableAction()
        action.variable = variable
        action.act(1f)
        assertEquals("", variable.value)
    }

    @Test
    fun udpSendToPeer_deliversPayload() {
        val portA = freeUdpPort()
        val epA = UdpManager.node("ua")
        val epB = UdpManager.node("ub")
        assertTrue(epA.start(portA))
        epB.connect("127.0.0.1", portA)
        assertTrue(epB.sendToPeer("hello"))

        assertTrue(await { epA.getMessages().contains("hello") })
        assertTrue(epB.getMessages().isEmpty())
    }

    @Test
    fun udpSendAction_customMode_deliversRows() {
        val portA = freeUdpPort()
        val epA = UdpManager.node("ua")
        assertTrue(epA.start(portA))

        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("s")
        project.defaultScene.addSprite(sprite)
        val scope = Scope(project, sprite, null)
        val action = SendUdpAction()
        action.scope = scope
        action.serverName = Formula("ub")
        action.ip = Formula("127.0.0.1")
        action.port = Formula(portA.toString())
        action.values = listOf(Formula("lala"), Formula("hmm"))
        action.sendMode = 1
        action.act(1f)

        assertTrue(await { epA.getMessages().contains("lala\u0001hmm") })
    }

    @Test
    fun udpListenAction_mapsRowsToVariables() {
        val portA = freeUdpPort()
        val epA = UdpManager.node("ua")
        epA.clearMessages()
        assertTrue(epA.start(portA))
        UdpManager.node("ub").sendTo("127.0.0.1", portA, "lala\u0001hmm")
        assertTrue(await { epA.getMessages().isNotEmpty() })

        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("s")
        project.defaultScene.addSprite(sprite)
        val scope = Scope(project, sprite, null)
        val first = UserVariable("v1")
        val second = UserVariable("v2")
        val action = ListenUdpAction()
        action.scope = scope
        action.variables = listOf(first, second)
        action.listenMode = 1
        action.serverName = Formula("ua")
        action.act(1f)

        assertTrue(await { first.value == "lala" })
        assertTrue(await { second.value == "hmm" })
        ListenUdpAction.stopAll()
    }

    @Test
    fun udpPingLoopback_answersTrue() {
        val portA = freeUdpPort()
        assertTrue(UdpManager.node("ua").start(portA))
        val result = UdpEndpoint.pingHost("127.0.0.1", portA, 1500)
        assertTrue(result.ok)
    }

    @Test
    fun udpPingAction_writesTrueToVariable() {
        val portA = freeUdpPort()
        assertTrue(UdpManager.node("ua").start(portA))

        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("s")
        project.defaultScene.addSprite(sprite)
        val scope = Scope(project, sprite, null)
        val variable = UserVariable("r")
        val action = UdpPingAction()
        action.scope = scope
        action.ip = Formula("127.0.0.1")
        action.port = Formula(portA.toString())
        action.variable = variable
        action.measure = false
        action.act(1f)

        assertTrue(await { variable.value == "true" })
    }

    @Test
    fun udpPongIsDeliveredAsMessage() {
        val portA = freeUdpPort()
        val portB = freeUdpPort()
        val epA = UdpManager.node("ua")
        val epB = UdpManager.node("ub")
        assertTrue(epA.start(portA))
        assertTrue(epB.start(portB))
        epB.clearMessages()

        assertTrue(epB.sendTo("127.0.0.1", portA, "PING:abc"))
        assertTrue(epB.sendTo("127.0.0.1", portA, "PING:abc"))

        assertTrue(await { epB.getMessages().count { it == "PONG:abc" } >= 2 })
    }

    @Test
    fun udpPortZeroReportsRealBoundPort() {
        val endpoint = UdpManager.node("uzero")
        assertTrue(endpoint.start(0))
        val port = endpoint.getPort()
        assertTrue(port != "0" && port != "NaN")
        assertTrue(port.toInt() > 0)
    }
}
