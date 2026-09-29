package org.catrobat.catroid.desktop

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.desktop.runner.DesktopStage
import org.catrobat.catroid.desktop.runner.OverlayCallbacks
import org.catrobat.catroid.formulaeditor.UserVariable
import java.io.File
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min
import kotlin.system.exitProcess

class DesktopWindow : ApplicationAdapter(), OverlayCallbacks {
    private lateinit var batch: SpriteBatch
    private lateinit var shapes: ShapeRenderer
    private lateinit var font: BitmapFont
    private lateinit var camera: OrthographicCamera
    private lateinit var ui: Stage
    private val uiActors = HashMap<String, com.badlogic.gdx.scenes.scene2d.Actor>()
    private val uiDisposables = ArrayList<Texture>()
    private var gameEngine: DesktopStage? = null
    private var showConsole = false

    private data class Loaded(val project: Project, val rootDir: File)

    private val loaded = AtomicReference<Loaded?>(null)
    @Volatile private var stage = "Unpacking..."
    @Volatile private var progress = 0f
    private val lookTextures = HashMap<String, List<Texture>>()
    private var loadThread: Thread? = null

    override fun create() {
        batch = SpriteBatch()
        shapes = ShapeRenderer()
        font = buildFont()
        resetCamera()
        ui = Stage(ScreenViewport())
        val tapper = object : com.badlogic.gdx.InputAdapter() {
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                val engine = gameEngine
                if (engine != null) {
                    val wx = screenX - Gdx.graphics.width / 2f
                    val wy = Gdx.graphics.height / 2f - screenY
                    engine.tap(wx, wy) { name, lookIdx ->
                        val textures = lookTextures[engine.activeSceneName + "::" + name] ?: return@tap null
                        if (textures.isEmpty()) return@tap null
                        val tex = textures[lookIdx.coerceIn(0, textures.size - 1)]
                        Pair(tex.width.toFloat(), tex.height.toFloat())
                    }
                }
                return false
            }

            override fun keyDown(keycode: Int): Boolean {
                if (keycode == com.badlogic.gdx.Input.Keys.F1) {
                    showConsole = !showConsole
                    return true
                }
                if (keycode == com.badlogic.gdx.Input.Keys.F2) {
                    gameEngine?.prevScene()
                    return true
                }
                if (keycode == com.badlogic.gdx.Input.Keys.F3) {
                    gameEngine?.nextScene()
                    return true
                }
                return false
            }
        }
        Gdx.input.inputProcessor = com.badlogic.gdx.InputMultiplexer(ui, tapper)
        val input = File(projectPath)
        loadThread = Thread({
            try {
                stage = "Unpacking..."
                progress = 0.1f
                val rootDir = DesktopMain.extractRoot(input)
                if (rootDir == null) {
                    stage = "Bad input file"
                    return@Thread
                }
                stage = "Loading project..."
                progress = 0.4f
                val codeXml = File(rootDir, "code.xml")
                val project = DesktopModel.loadProject(codeXml)
                stage = "Loading looks..."
                progress = 0.7f
                Gdx.app.postRunnable({
                    try {
                        loadLooks(project, rootDir)
                        loaded.set(Loaded(project, rootDir))
                        Gdx.graphics.setTitle("NeoCatroid Desktop - " + project.name)
                        val engine = DesktopStage(project, rootDir, this)
                        gameEngine = engine
                        engine.start()
                        progress = 1f
                    } catch (e: Exception) {
                        stage = "Error: " + e.message
                    }
                })
            } catch (e: Exception) {
                stage = "Error: " + e.message
            }
        }, "project-loader")
        loadThread!!.isDaemon = true
        loadThread!!.start()
    }

    private fun buildFont(): BitmapFont {
        return try {
            val generator = com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator(
                Gdx.files.absolute("C:/Windows/Fonts/segoeui.ttf"))
            val param = com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter()
            param.size = 20
            param.characters = com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.DEFAULT_CHARS +
                "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяЁё№"
            val result = generator.generateFont(param)
            generator.dispose()
            result
        } catch (e: Exception) {
            BitmapFont()
        }
    }

    private fun resetCamera() {
        camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(0f, 0f, 0f)
        camera.update()
    }

    private fun safeFileName(name: String): String {
        return name.split('/').joinToString("/") {
            it.trimEnd().trimEnd('.').replace(Regex("[\\x00-\\x1F<>:\"|?*]"), "_")
        }
    }

    private fun loadLooks(project: Project, rootDir: File) {
        for (textures in lookTextures.values) {
            for (texture in textures) texture.dispose()
        }
        lookTextures.clear()
        for (scene in project.sceneList) {
            val want = scene.name.trimEnd().trimEnd('.')
            val sceneDir = rootDir.listFiles()?.firstOrNull { it.isDirectory && it.name.trimEnd().trimEnd('.') == want }
                ?: File(rootDir, scene.name)
            val imgDir = File(sceneDir, "images")
            for (sprite: Sprite in scene.spriteList) {
                val textures = ArrayList<Texture>()
                val filesBySafe = HashMap<String, File>()
                imgDir.listFiles()?.forEach { f ->
                    if (f.isFile) filesBySafe[safeFileName(f.name)] = f
                }
                for (look in sprite.lookList) {
                    val file = File(imgDir, look.xstreamFileName).takeIf { it.isFile }
                        ?: filesBySafe[safeFileName(look.xstreamFileName)]
                        ?: continue
                    try {
                        val texture = Texture(Gdx.files.absolute(file.absolutePath))
                        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                        textures.add(texture)
                    } catch (e: Exception) {
                        continue
                    }
                }
                if (textures.isNotEmpty()) {
                    lookTextures[scene.name + "::" + sprite.name] = textures
                }
            }
        }
    }

    override fun render() {
        val ready = loaded.get()
        if (ready == null) {
            renderSplash()
        } else {
            renderScene(ready)
        }
    }

    private fun renderSplash() {
        Gdx.gl.glClearColor(0.17f, 0.17f, 0.18f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.projectionMatrix = camera.combined
        batch.begin()
        font.draw(batch, "Loading...", -40f, 40f)
        font.draw(batch, stage, -40f, 10f)
        batch.end()
        val barW = 400f
        val barX = -barW / 2f
        val barY = -50f
        shapes.projectionMatrix = camera.combined
        shapes.begin(ShapeRenderer.ShapeType.Filled)
        shapes.setColor(0.28f, 0.28f, 0.29f, 1f)
        shapes.rect(barX, barY, barW, 16f)
        shapes.setColor(0.5f, 0.7f, 1f, 1f)
        shapes.rect(barX, barY, barW * progress.coerceIn(0f, 1f), 16f)
        shapes.end()
    }

    private fun renderScene(ready: Loaded) {
        val engine = gameEngine
        if (engine == null) {
            Gdx.gl.glClearColor(0.06f, 0.12f, 0.23f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            batch.projectionMatrix = camera.combined
            batch.begin()
            font.draw(batch, stage, -300f, 0f)
            batch.end()
            return
        }
        engine.update()
        Gdx.gl.glClearColor(0.06f, 0.12f, 0.23f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.projectionMatrix = camera.combined
        batch.begin()
        if (engine != null) {
            for (state in engine.states.values) {
                if (!state.visible) continue
                val textures = lookTextures[engine.activeSceneName + "::" + state.sprite.name] ?: continue
                if (textures.isEmpty()) continue
                val texture = textures[state.lookIndex.coerceIn(0, textures.size - 1)]
                val s = state.sizePct / 100f
                val dw = texture.width * s
                val dh = texture.height * s
                batch.setColor(1f, 1f, 1f, state.opacity.coerceIn(0f, 1f))
                batch.draw(com.badlogic.gdx.graphics.g2d.TextureRegion(texture),
                    state.x - dw / 2f, state.y - dh / 2f,
                    dw / 2f, dh / 2f, dw, dh, 1f, 1f, 90f - state.rotation)
            }
            batch.setColor(com.badlogic.gdx.graphics.Color.WHITE)
        }
        batch.end()
        ui.act(Gdx.graphics.deltaTime)
        ui.draw()
        if (showConsole && engine != null) {
            batch.projectionMatrix = camera.combined
            batch.begin()
            val lines = engine.logLines
            val from = maxOf(0, lines.size - 12)
            var y = -camera.viewportHeight / 2f + 20f + (lines.size - from) * 18f
            font.data.setScale(0.7f)
            for (i in from until lines.size) {
                y -= 18f
                font.draw(batch, lines[i], -camera.viewportWidth / 2f + 10f, y)
            }
            font.data.setScale(1f)
            batch.end()
        }
    }

    override fun resize(width: Int, height: Int) {
        resetCamera()
        if (::ui.isInitialized) {
            ui.viewport.update(width, height, true)
        }
    }

    override fun dispose() {
        batch.dispose()
        shapes.dispose()
        font.dispose()
        if (::ui.isInitialized) {
            ui.dispose()
        }
        for (textures in lookTextures.values) {
            for (texture in textures) texture.dispose()
        }
        for (texture in uiDisposables) texture.dispose()
    }

    override fun onSceneStart() {
        for (actor in uiActors.values) actor.remove()
        uiActors.clear()
    }

    override fun onLabel(viewId: String, text: String, colorHex: String, sizeSp: Float,
        x: Float, y: Float, w: Float, h: Float) {
        uiActors[viewId]?.remove()
        val style = Label.LabelStyle(font, parseColor(colorHex))
        val label = Label(text, style)
        label.setFontScale((if (sizeSp > 0f) sizeSp else 20f) / 20f)
        label.setPosition(toPx(x), toPy(y))
        label.setSize(w, h)
        ui.addActor(label)
        uiActors[viewId] = label
    }

    override fun onTextField(name: String, defaultText: String, x: Float, y: Float,
        w: Float, h: Float, textSizeSp: Float, variable: UserVariable?) {
        (uiActors["field:" + name] as? TextField)?.let {
            it.text = defaultText
            return
        }
        val bg = solidDrawable(0.92f, 0.92f, 0.92f, 1f)
        val cursor = solidDrawable(0f, 0f, 0f, 1f)
        val selection = solidDrawable(0.5f, 0.7f, 1f, 1f)
        val style = TextField.TextFieldStyle(font, com.badlogic.gdx.graphics.Color.BLACK, cursor, selection, bg)
        val field = TextField(defaultText, style)
        field.setPosition(toPx(x), toPy(y))
        field.setSize(if (w > 0f) w else 300f, if (h > 0f) h else 48f)
        field.setTextFieldListener(TextField.TextFieldListener { _, _ ->
            variable?.value = field.text
        })
        ui.addActor(field)
        uiActors["field:" + name] = field
    }

    private fun toPx(x: Float): Float = Gdx.graphics.width / 2f + x

    private fun toPy(y: Float): Float = Gdx.graphics.height / 2f + y

    private fun parseColor(hex: String): com.badlogic.gdx.graphics.Color {
        return try {
            com.badlogic.gdx.graphics.Color.valueOf(hex.trimStart('#'))
        } catch (e: Exception) {
            com.badlogic.gdx.graphics.Color.WHITE
        }
    }

    private fun solidDrawable(r: Float, g: Float, b: Float, a: Float): TextureRegionDrawable {
        val pixmap = Pixmap(2, 2, Pixmap.Format.RGBA8888)
        pixmap.setColor(r, g, b, a)
        pixmap.fill()
        val texture = Texture(pixmap)
        pixmap.dispose()
        uiDisposables.add(texture)
        return TextureRegionDrawable(TextureRegion(texture))
    }

    companion object {
        @Volatile var projectPath = ""
    }
}

object DesktopWindowMain {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            System.err.println("Usage: neocatroid-window <project.catrobat|project-dir>")
            exitProcess(2)
        }
        DesktopWindow.projectPath = args[0]
        val config = Lwjgl3ApplicationConfiguration()
        config.setTitle("NeoCatroid Desktop")
        config.setWindowedMode(800, 600)
        Lwjgl3Application(DesktopWindow(), config)
    }
}

fun main(args: Array<String>) = DesktopWindowMain.main(args)
