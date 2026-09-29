package org.catrobat.catroid.desktop

import org.w3c.dom.Element
import java.io.File
import java.util.zip.ZipFile
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.system.exitProcess

object DesktopMain {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            System.err.println("Usage: neocatroid-desktop <project.catrobat|project-dir>")
            exitProcess(2)
        }
        val codeXml = locateCodeXml(File(args[0]))
        if (codeXml == null) {
            System.err.println("code.xml not found in " + args[0])
            exitProcess(2)
        }
        dumpProject(codeXml)
    }

    internal fun locateCodeXml(input: File): File? {
        val root = extractRoot(input) ?: return null
        return File(root, "code.xml").takeIf { it.isFile }
    }

    internal fun extractRoot(input: File): File? {
        if (input.isDirectory) {
            return input
        }
        if (!input.isFile) {
            return null
        }
        if (input.name.endsWith(".xml")) {
            return input.parentFile
        }
        val tmp = createTempDir("neocatroid-proj")
        java.util.zip.ZipFile(input).use { zip ->
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (entry.isDirectory) continue
                val safeName = entry.name.split('/').joinToString("/") {
                    it.trimEnd().trimEnd('.').replace(Regex("[\\x00-\\x1F<>:\"|?*]"), "_")
                }
                if (safeName.isEmpty()) continue
                val out = File(tmp, safeName)
                out.parentFile.mkdirs()
                zip.getInputStream(entry).use { src -> out.outputStream().use { dst -> src.copyTo(dst) } }
            }
        }
        return tmp
    }

    private fun dumpProject(codeXml: File) {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(codeXml)
        doc.documentElement.normalize()
        val brickUse = HashMap<String, Int>()
        var sceneCount = 0
        var spriteCount = 0
        var scriptCount = 0
        var brickCount = 0

        val scenes = doc.getElementsByTagName("scene")
        for (s in 0 until scenes.length) {
            val scene = scenes.item(s) as? Element ?: continue
            sceneCount++
            println("Scene: " + sceneName(scene))
            val sprites = directChildren(scene, "objectList")
                .flatMap { directChildren(it, "object") }
            for (sprite in sprites) {
                spriteCount++
                println("  Object: " + sprite.getAttribute("name") + " [" + sprite.getAttribute("type") + "]")
                val scripts = directChildren(sprite, "scriptList")
                    .flatMap { directChildren(it, "script") }
                for (script in scripts) {
                    scriptCount++
                    val bricks = collectBricks(script)
                    println("    Script: " + script.getAttribute("type") + " [" + bricks.size + " bricks]")
                    for (brick in bricks) {
                        brickCount++
                        val type = brick.getAttribute("type").ifEmpty { brick.tagName }
                        brickUse[type] = (brickUse[type] ?: 0) + 1
                    }
                }
            }
        }
        println()
        println("Scenes: $sceneCount, sprites: $spriteCount, scripts: $scriptCount, bricks: $brickCount")
        println("Brick census:")
        for ((type, count) in brickUse.entries.sortedByDescending { it.value }) {
            println("  $count x $type")
        }
    }

    private fun sceneName(scene: Element): String {
        val name = textOf(scene, "name")
        return name.ifEmpty { "(unnamed)" }
    }

    private fun textOf(parent: Element, tag: String): String {
        val nodes = parent.getElementsByTagName(tag)
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node.parentNode == parent && node is Element) {
                return node.textContent.trim()
            }
        }
        return ""
    }

    private fun collectBricks(script: Element): List<Element> {
        val result = ArrayList<Element>()
        collectBrickElements(script, result)
        return result
    }

    private fun collectBrickElements(node: Element, out: MutableList<Element>) {
        for (child in directChildElements(node)) {
            val tag = child.tagName
            if (tag.endsWith("Brick") || child.getAttribute("type").endsWith("Brick")) {
                out.add(child)
            }
            collectBrickElements(child, out)
        }
    }
    private fun directChildren(parent: Element, tag: String): List<Element> {
        return directChildElements(parent).filter { it.tagName == tag }
    }

    private fun directChildElements(parent: Element): List<Element> {
        val result = ArrayList<Element>()
        val nodes = parent.childNodes
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node is Element) {
                result.add(node)
            }
        }
        return result
    }
}

fun main(args: Array<String>) = DesktopMain.main(args)
