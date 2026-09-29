package org.catrobat.catroid.desktop.runner

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.CreateTextFieldBrick
import org.catrobat.catroid.content.bricks.CreateTextLabelBrick
import org.catrobat.catroid.content.bricks.EndBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.HideBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.PreviousLookBrick
import org.catrobat.catroid.content.bricks.ReadBaseBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.SceneStartBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.ShowBrick
import org.catrobat.catroid.content.bricks.StopScriptBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickWithFormula
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WaitUntilBrick
import org.catrobat.catroid.content.bricks.WriteBaseBrick
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserVariable
import java.io.File
import java.io.IOException

interface OverlayCallbacks {
    fun onLabel(viewId: String, text: String, colorHex: String, sizeSp: Float, x: Float, y: Float, w: Float, h: Float)
    fun onTextField(name: String, defaultText: String, x: Float, y: Float, w: Float, h: Float, textSizeSp: Float, variable: UserVariable?)
    fun onSceneStart()
}

class DesktopSpriteState(val project: Project?, val sprite: Sprite) {
    var x = 0f
    var y = 0f
    var visible = true
    var sizePct = 100f
    var rotation = 90f
    var opacity = 1f
    var lookIndex = 0
    val scope = Scope(project, sprite, null)
}

class DesktopStage(val project: Project, val filesDir: File, private val overlays: OverlayCallbacks) {

    val states = HashMap<String, DesktopSpriteState>()
    var activeSceneName = ""
    val logLines = java.util.Collections.synchronizedList(ArrayList<String>())
    private val sceneStates = HashMap<String, HashMap<String, DesktopSpriteState>>()
    private val threads = ArrayList<ScriptThread>()
    private val glides = ArrayList<Glide>()
    private val messageQueue = ArrayDeque<String>()
    private val http = OkHttpClient()
    private val warned = HashSet<String>()

    private class LoopFrame(val bricks: List<Brick>, var pc: Int, var remaining: Int,
        val forever: Boolean, val untilFormula: Formula? = null)

    private class ScriptThread(val state: DesktopSpriteState, val script: Script) {
        val stack = ArrayList<LoopFrame>()
        var pc = 0
        var waiting = false
        var timedWait = false
        var waitUntilNanos = 0L
        var waitingOn = mutableListOf<ScriptThread>()
        var done = false
    }

    private class Glide(val state: DesktopSpriteState, val fromX: Float, val fromY: Float,
        val toX: Float, val toY: Float, val startNanos: Long, val durationNanos: Long)

    init {
        for (scene in project.sceneList) {
            val map = HashMap<String, DesktopSpriteState>()
            for (sprite in scene.spriteList) {
                map[sprite.name] = DesktopSpriteState(project, sprite)
            }
            sceneStates[scene.name] = map
        }
    }

    fun start() {
        val first = project.sceneList.firstOrNull()?.name ?: return
        log("Scenes: " + project.sceneList.map { it.name })
        startScene(first)
    }

    fun nextScene() {
        val names = project.sceneList.map { it.name }
        val idx = names.indexOf(activeSceneName)
        if (idx >= 0 && idx + 1 < names.size) {
            pendingScene = names[idx + 1]
        }
    }

    fun prevScene() {
        val names = project.sceneList.map { it.name }
        val idx = names.indexOf(activeSceneName)
        if (idx > 0) {
            pendingScene = names[idx - 1]
        }
    }

    fun startScene(name: String) {
        val map = sceneStates[name] ?: return
        threads.clear()
        pendingSpawns.clear()
        glides.clear()
        messageQueue.clear()
        states.clear()
        states.putAll(map)
        activeSceneName = name
        overlays.onSceneStart()
        log("SCENE -> " + name)
        for (state in states.values) {
            for (script in state.sprite.scriptList) {
                if (script is StartScript) {
                    log("START " + state.sprite.name + " :: " + script.javaClass.simpleName)
                    spawn(state, script)
                }
            }
        }
    }

    fun tap(wx: Float, wy: Float, lookSize: (spriteName: String, lookIndex: Int) -> Pair<Float, Float>?) {
        val ordered = activeOrder()
        for (name in ordered.asReversed()) {
            val state = states[name] ?: continue
            if (!state.visible) continue
            val size = lookSize(name, state.lookIndex) ?: continue
            val s = state.sizePct / 100f
            val hw = size.first * s / 2f
            val hh = size.second * s / 2f
            if (wx < state.x - hw || wx > state.x + hw || wy < state.y - hh || wy > state.y + hh) {
                continue
            }
            log("TAP " + name)
            for (script in state.sprite.scriptList) {
                if (script is WhenScript) {
                    spawn(state, script)
                }
            }
            return
        }
    }

    private fun activeOrder(): List<String> {
        val scene = project.sceneList.firstOrNull { it.name == activeSceneName } ?: return states.keys.toList()
        return scene.spriteList.map { it.name }
    }

    private var pendingScene: String? = null
    private val pendingSpawns = ArrayList<ScriptThread>()

    fun update() {
        val switchTo = pendingScene
        if (switchTo != null) {
            pendingScene = null
            startScene(switchTo)
        }
        if (pendingSpawns.isNotEmpty()) {
            threads.addAll(pendingSpawns)
            pendingSpawns.clear()
            if (threads.size > 256) {
                warnOnce("thread-cap", "Too many script threads, dropping oldest")
                var overflow = threads.size - 256
                val it = threads.iterator()
                while (overflow-- > 0 && it.hasNext()) {
                    it.next().done = true
                }
            }
        }
        val now = System.nanoTime()
        val glideIt = glides.iterator()
        while (glideIt.hasNext()) {
            val glide = glideIt.next()
            if (glide.durationNanos <= 0L) {
                glide.state.x = glide.toX
                glide.state.y = glide.toY
                glideIt.remove()
                continue
            }
            val t = ((now - glide.startNanos).toDouble() / glide.durationNanos.toDouble()).coerceIn(0.0, 1.0)
            glide.state.x = (glide.fromX + (glide.toX - glide.fromX) * t).toFloat()
            glide.state.y = (glide.fromY + (glide.toY - glide.fromY) * t).toFloat()
            if (t >= 1.0) {
                glideIt.remove()
            }
        }
        var guard = 0
        while (messageQueue.isNotEmpty() && guard++ < 100) {
            val message = messageQueue.removeFirst()
            log("BROADCAST <- " + message)
            spawnBroadcast(message)
        }
        val it = threads.iterator()
        while (it.hasNext()) {
            val thread = it.next()
            if (thread.waiting) {
                if (thread.timedWait && System.nanoTime() < thread.waitUntilNanos) {
                    continue
                }
                if (!thread.timedWait && thread.waitingOn.any { t -> !t.done }) {
                    continue
                }
                thread.waiting = false
                thread.timedWait = false
                thread.waitingOn.clear()
            }
            step(thread)
            if (thread.done) {
                it.remove()
            }
        }
    }

    private fun spawn(state: DesktopSpriteState, script: Script): ScriptThread {
        val thread = ScriptThread(state, script)
        pendingSpawns.add(thread)
        return thread
    }

    private fun spawnBroadcast(message: String): MutableList<ScriptThread> {
        val spawned = mutableListOf<ScriptThread>()
        for (state in states.values) {
            for (script in state.sprite.scriptList) {
                if (script is BroadcastScript && script.broadcastMessage == message) {
                    log("ON-BROADCAST " + state.sprite.name + " :: " + message)
                    spawned.add(spawn(state, script))
                }
            }
        }
        return spawned
    }

    private fun step(thread: ScriptThread) {
        var budget = 5000
        while (budget-- > 0 && !thread.done && !thread.waiting) {
            val frame = thread.stack.lastOrNull()
            val bricks = frame?.bricks ?: thread.script.brickList
            var pc = frame?.pc ?: thread.pc
            if (pc >= bricks.size) {
                if (frame == null) {
                    thread.done = true
                    return
                }
                if (frame.forever) {
                    frame.pc = 0
                    return
                }
                val until = frame.untilFormula
                if (until != null) {
                    if (asBool(evalFormula(thread, until))) {
                        thread.stack.removeAt(thread.stack.size - 1)
                        if (thread.stack.isEmpty()) {
                            thread.pc++
                        } else {
                            thread.stack.last().pc++
                        }
                    } else {
                        frame.pc = 0
                    }
                    continue
                }
                frame.remaining--
                if (frame.remaining > 0) {
                    frame.pc = 0
                    continue
                }
                thread.stack.removeAt(thread.stack.size - 1)
                if (thread.stack.isEmpty()) {
                    thread.pc++
                } else {
                    thread.stack.last().pc++
                }
                continue
            }
            val brick = bricks[pc]
            if (brick is EndBrick) {
                advance(thread, frame)
                continue
            }
            if (brick is IfThenLogicBeginBrick) {
                if (asBool(evalDouble(thread, brick, Brick.BrickField.IF_CONDITION))) {
                    thread.stack.add(LoopFrame(nested(brick), 0, 1, false))
                } else {
                    advance(thread, frame)
                }
                continue
            }
            if (brick is RepeatBrick) {
                val n = evalDouble(thread, brick, Brick.BrickField.TIMES_TO_REPEAT).toInt()
                if (n > 0) {
                    thread.stack.add(LoopFrame(nested(brick), 0, n, false))
                } else {
                    advance(thread, frame)
                }
                continue
            }
            if (brick is RepeatUntilBrick) {
                val cond = field(brick, Brick.BrickField.REPEAT_UNTIL_CONDITION)
                if (cond == null || !asBool(evalFormula(thread, cond))) {
                    thread.stack.add(LoopFrame(nested(brick), 0, Int.MAX_VALUE, false, cond))
                } else {
                    advance(thread, frame)
                }
                continue
            }
            if (brick is IfLogicBeginBrick) {
                if (asBool(evalDouble(thread, brick, Brick.BrickField.IF_CONDITION))) {
                    thread.stack.add(LoopFrame(nested(brick), 0, 1, false))
                } else {
                    val els = secondary(brick)
                    if (els.isNotEmpty()) {
                        thread.stack.add(LoopFrame(els, 0, 1, false))
                    } else {
                        advance(thread, frame)
                    }
                }
                continue
            }
            if (brick is StopScriptBrick) {
                stopByMode(thread, brick)
                advance(thread, frame)
                if (thread.done) {
                    return
                }
                continue
            }
            if (brick is BroadcastWaitBrick) {
                val message = brick.broadcastMessage ?: ""
                log("BROADCAST-WAIT -> " + message + " [" + thread.state.sprite.name + "]")
                val spawned = spawnBroadcast(message)
                advance(thread, frame)
                if (spawned.isNotEmpty()) {
                    thread.waiting = true
                    thread.timedWait = false
                    thread.waitingOn = spawned.toMutableList()
                }
                continue
            }
            if (brick is SceneStartBrick) {
                val target = brick.sceneToStart ?: ""
                advance(thread, frame)
                thread.done = true
                pendingScene = target
                return
            }
            if (brick is ForeverBrick) {
                thread.stack.add(LoopFrame(nested(brick), 0, Int.MAX_VALUE, true))
                continue
            }
            if (brick is WaitUntilBrick) {
                if (asBool(evalDouble(thread, brick, Brick.BrickField.IF_CONDITION))) {
                    advance(thread, frame)
                } else {
                    thread.waiting = true
                    thread.timedWait = false
                }
                continue
            }
            if (brick is WaitBrick) {
                val seconds = evalDouble(thread, brick, Brick.BrickField.TIME_TO_WAIT_IN_SECONDS)
                advance(thread, frame)
                if (seconds > 0.0) {
                    thread.waiting = true
                    thread.timedWait = true
                    thread.waitUntilNanos = System.nanoTime() + (seconds * 1_000_000_000L).toLong()
                }
                continue
            }
            if (brick is BroadcastBrick) {
                val message = brick.broadcastMessage ?: ""
                log("BROADCAST -> " + message + " [" + thread.state.sprite.name + "]")
                messageQueue.addLast(message)
                advance(thread, frame)
                continue
            }
            executeLeaf(thread, brick)
            advance(thread, frame)
        }
    }

    private fun advance(thread: ScriptThread, frame: LoopFrame?) {
        if (frame == null) {
            thread.pc++
        } else {
            frame.pc++
        }
    }

    private fun nested(brick: CompositeBrick): List<Brick> {
        return try {
            brick.nestedBricks ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun secondary(brick: IfLogicBeginBrick): List<Brick> {
        return try {
            brick.secondaryNestedBricks ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun stopByMode(thread: ScriptThread, brick: StopScriptBrick) {
        val mode = try {
            val field = brick.javaClass.getDeclaredField("spinnerSelection")
            field.isAccessible = true
            field.getInt(brick)
        } catch (e: Exception) {
            0
        }
        when (mode) {
            1 -> {
                log("STOP all scripts")
                for (t in threads) t.done = true
                for (t in pendingSpawns) t.done = true
            }
            2 -> {
                for (t in threads) {
                    if (t !== thread && t.state === thread.state) t.done = true
                }
                for (t in pendingSpawns) {
                    if (t !== thread && t.state === thread.state) t.done = true
                }
            }
            else -> thread.done = true
        }
    }

    private fun executeLeaf(thread: ScriptThread, brick: Brick) {
        val state = thread.state
        when (brick) {
            is SetVariableBrick -> {
                val formula = field(brick, Brick.BrickField.VARIABLE) ?: return
                val variable = brick.getUserVariable() ?: return
                val raw = evalObject(thread, formula) ?: return
                variable.value = coerceVariable(raw)
            }
            is ChangeVariableBrick -> {
                val formula = field(brick, Brick.BrickField.VARIABLE_CHANGE) ?: return
                val variable = brick.getUserVariable() ?: return
                val by = evalDouble(thread, brick, Brick.BrickField.VARIABLE_CHANGE)
                variable.value = toDouble(variable.value) + by
            }
            is PlaceAtBrick -> {
                state.x = evalDouble(thread, brick, Brick.BrickField.X_POSITION).toFloat()
                state.y = evalDouble(thread, brick, Brick.BrickField.Y_POSITION).toFloat()
            }
            is ChangeXByNBrick -> state.x += evalDouble(thread, brick, Brick.BrickField.X_POSITION_CHANGE).toFloat()
            is ChangeYByNBrick -> state.y += evalDouble(thread, brick, Brick.BrickField.Y_POSITION_CHANGE).toFloat()
            is HideBrick -> state.visible = false
            is ShowBrick -> state.visible = true
            is SetSizeToBrick -> state.sizePct = evalDouble(thread, brick, Brick.BrickField.SIZE).toFloat()
            is ChangeSizeByNBrick -> state.sizePct += evalDouble(thread, brick, Brick.BrickField.SIZE_CHANGE).toFloat()
            is SetTransparencyBrick ->
                state.opacity = (1f - evalDouble(thread, brick, Brick.BrickField.TRANSPARENCY).toFloat() / 100f).coerceIn(0f, 1f)
            is ChangeTransparencyByNBrick ->
                state.opacity = (state.opacity - evalDouble(thread, brick, Brick.BrickField.TRANSPARENCY_CHANGE).toFloat() / 100f).coerceIn(0f, 1f)
            is TurnLeftBrick -> state.rotation -= evalDouble(thread, brick, Brick.BrickField.TURN_LEFT_DEGREES).toFloat()
            is TurnRightBrick -> state.rotation += evalDouble(thread, brick, Brick.BrickField.TURN_RIGHT_DEGREES).toFloat()
            is PointInDirectionBrick -> state.rotation = evalDouble(thread, brick, Brick.BrickField.DEGREES).toFloat()
            is SetLookBrick -> {
                val want = brick.look?.name ?: ""
                val idx = state.sprite.lookList.indexOfFirst { it.name == want }
                if (idx >= 0) {
                    state.lookIndex = idx
                }
            }
            is NextLookBrick -> {
                val size = state.sprite.lookList.size
                if (size > 0) {
                    state.lookIndex = (state.lookIndex + 1) % size
                }
            }
            is PreviousLookBrick -> {
                val size = state.sprite.lookList.size
                if (size > 0) {
                    state.lookIndex = (state.lookIndex + size - 1) % size
                }
            }
            is GlideToBrick -> {
                val toX = evalDouble(thread, brick, Brick.BrickField.X_DESTINATION).toFloat()
                val toY = evalDouble(thread, brick, Brick.BrickField.Y_DESTINATION).toFloat()
                val dur = evalDouble(thread, brick, Brick.BrickField.DURATION_IN_SECONDS)
                glides.removeAll { it.state === state }
                if (dur <= 0.0) {
                    state.x = toX
                    state.y = toY
                } else {
                    glides.add(Glide(state, state.x, state.y, toX, toY,
                        System.nanoTime(), (dur * 1_000_000_000L).toLong()))
                }
            }
            is ReadBaseBrick -> {
                val base = evalString(thread, brick, Brick.BrickField.FIREBASE_ID)
                val key = evalString(thread, brick, Brick.BrickField.FIREBASE_KEY)
                val variable = brick.getUserVariable()
                if (variable != null) {
                    firebaseGet(base, key) { value ->
                        if (value != null) variable.value = coerceVariable(value)
                    }
                }
            }
            is WriteBaseBrick -> {
                val base = evalString(thread, brick, Brick.BrickField.FIREBASE_ID)
                val key = evalString(thread, brick, Brick.BrickField.FIREBASE_KEY)
                val value = evalString(thread, brick, Brick.BrickField.FIREBASE_VALUE)
                firebasePut(base, key, value)
            }
            is CreateTextLabelBrick -> {
                overlays.onLabel(
                    evalString(thread, brick, Brick.BrickField.VALUE_1),
                    evalString(thread, brick, Brick.BrickField.VALUE_2),
                    evalString(thread, brick, Brick.BrickField.VALUE_3),
                    evalDouble(thread, brick, Brick.BrickField.VALUE_4).toFloat(),
                    evalDouble(thread, brick, Brick.BrickField.X_POSITION).toFloat(),
                    evalDouble(thread, brick, Brick.BrickField.Y_POSITION).toFloat(),
                    evalDouble(thread, brick, Brick.BrickField.WIDTH).toFloat(),
                    evalDouble(thread, brick, Brick.BrickField.HEIGHT).toFloat()
                )
            }
            is CreateTextFieldBrick -> {
                overlays.onTextField(
                    evalString(thread, brick, Brick.BrickField.NAME),
                    evalString(thread, brick, Brick.BrickField.TEXT),
                    evalDouble(thread, brick, Brick.BrickField.X_POSITION).toFloat(),
                    evalDouble(thread, brick, Brick.BrickField.Y_POSITION).toFloat(),
                    evalDouble(thread, brick, Brick.BrickField.WIDTH).toFloat(),
                    evalDouble(thread, brick, Brick.BrickField.HEIGHT).toFloat(),
                    evalDouble(thread, brick, Brick.BrickField.TEXTSIZE).toFloat(),
                    (brick as? UserVariableBrickWithFormula)?.getUserVariable()
                )
            }
            is SetBrightnessBrick -> warnOnce("brightness", "SetBrightness is a no-op on desktop")
            is ReadVariableFromDeviceBrick -> warnOnce("device-var", "ReadVariableFromDevice is a no-op on desktop")
            is WriteVariableOnDeviceBrick -> {
                val variable = brick.getUserVariable()
                if (variable != null && variable.name != null) {
                    try {
                        val outDir = File(filesDir, "desktop-out")
                        outDir.mkdirs()
                        val safe = variable.name.replace(Regex("[^a-zA-Z0-9-_ ]"), "_")
                        File(outDir, "$safe.txt").writeText(variable.value?.toString() ?: "")
                    } catch (e: Exception) {
                        Log.w("DesktopStage", "WriteVariableOnDevice failed: " + e.message)
                    }
                }
            }
            else -> warnOnce("brick:" + brick.javaClass.simpleName,
                "Unsupported brick on desktop: " + brick.javaClass.simpleName)
        }
    }

    private fun field(brick: FormulaBrick, field: Brick.BrickField): Formula? {
        return try {
            brick.getFormulaWithBrickField(field)
        } catch (e: Exception) {
            null
        }
    }

    private fun evalDouble(thread: ScriptThread, brick: FormulaBrick, field: Brick.BrickField): Double {
        val formula = field(brick, field) ?: return 0.0
        return try {
            formula.interpretDouble(thread.state.scope) ?: 0.0
        } catch (e: InterpretationException) {
            0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun evalString(thread: ScriptThread, brick: FormulaBrick, field: Brick.BrickField): String {
        val formula = field(brick, field) ?: return ""
        return try {
            formula.interpretString(thread.state.scope) ?: ""
        } catch (e: InterpretationException) {
            ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun evalObject(thread: ScriptThread, formula: Formula): Any? {
        return try {
            formula.interpretObject(thread.state.scope)
        } catch (e: InterpretationException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun evalFormula(thread: ScriptThread, formula: Formula): Double {
        return try {
            formula.interpretDouble(thread.state.scope) ?: 0.0
        } catch (e: InterpretationException) {
            0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun toDouble(value: Any?): Double {
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: 0.0
            is Boolean -> if (value) 1.0 else 0.0
            else -> 0.0
        }
    }

    private fun asBool(value: Double): Boolean = value != 0.0 && !value.isNaN()

    private fun coerceVariable(raw: Any): Any {
        if (raw is Boolean) return raw
        if (raw is String) {
            return try {
                raw.toDouble()
            } catch (e: NumberFormatException) {
                raw
            }
        }
        return raw
    }

    private fun firebaseGet(base: String, key: String, callback: (String?) -> Unit) {
        val url = restUrl(base, key)
        if (url == null) {
            warnOnce("firebase-url", "Bad Firebase base URL: $base")
            callback(null)
            return
        }
        log("Firebase GET " + url)
        val request = Request.Builder().url(url).get().build()
        http.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log("Firebase GET " + key + " -> FAIL " + e.message)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    log("Firebase GET " + key + " -> " + it.code)
                    if (!it.isSuccessful) {
                        callback(null)
                        return
                    }
                    val body = it.body?.string()?.trim()
                    callback(if (body == null || body == "null") null else body.trim('"'))
                }
            }
        })
    }

    private fun firebasePut(base: String, key: String, value: String) {
        val url = restUrl(base, key)
        if (url == null) {
            warnOnce("firebase-url", "Bad Firebase base URL: $base")
            return
        }
        log("Firebase PUT " + url)
        val body = ("\"" + value.replace("\"", "\\\"") + "\"")
            .toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).put(body).build()
        http.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log("Firebase PUT " + key + " -> FAIL " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                log("Firebase PUT " + key + " -> " + response.code)
                response.close()
            }
        })
    }

    private fun restUrl(base: String, key: String): String? {
        if (base.isBlank() || key.isBlank()) return null
        var root = base.trim().trimEnd('/')
        if (!root.startsWith("http")) return null
        return root + "/" + key.trim('/').replace(" ", "%20") + ".json"
    }

    private fun warnOnce(key: String, message: String) {
        if (warned.add(key)) {
            log("WARN " + message)
        }
    }

    fun log(message: String) {
        val stamp = System.currentTimeMillis() % 100000
        val line = "[$stamp] $message"
        logLines.add(line)
        while (logLines.size > 300) {
            logLines.removeAt(0)
        }
        println(line)
        Log.w("DesktopStage", message)
    }
}
