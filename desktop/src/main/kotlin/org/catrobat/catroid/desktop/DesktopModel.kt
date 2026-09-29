package org.catrobat.catroid.desktop

import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.io.XstreamSerializer
import java.io.File
import kotlin.system.exitProcess

object DesktopModel {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            System.err.println("Usage: neocatroid-model <project.catrobat|project-dir>")
            exitProcess(2)
        }
        val codeXml = DesktopMain.locateCodeXml(File(args[0]))
        if (codeXml == null) {
            System.err.println("code.xml not found in " + args[0])
            exitProcess(2)
        }
        val project = loadProject(codeXml)
        println("Project: " + project.name)
        for (scene in project.sceneList) {
            println("Scene: " + scene.name)
            for (sprite in scene.spriteList) {
                println("  Sprite: " + sprite.name + " scripts=" + sprite.scriptList.size)
            }
        }
        val firstSprite = project.sceneList.firstOrNull()?.spriteList?.firstOrNull()
        if (firstSprite != null) {
            println("isFunction(WORD) = " + org.catrobat.catroid.formulaeditor.Functions.isFunction("WORD"))
            println("getFunctionByValue(WORD) = " + org.catrobat.catroid.formulaeditor.Functions.getFunctionByValue("WORD"))
            val scope = Scope(project, firstSprite, null)
            val word = FormulaElement(FormulaElement.ElementType.FUNCTION, "WORD", null,
                FormulaElement(FormulaElement.ElementType.NUMBER, "2", null),
                FormulaElement(FormulaElement.ElementType.STRING, "ban uid reason", null))
            println("word(2,'ban uid reason') = " + word.interpretRecursive(scope))
            val split = FormulaElement(FormulaElement.ElementType.FUNCTION, "SPLIT", null,
                FormulaElement(FormulaElement.ElementType.NUMBER, "1", null),
                FormulaElement(FormulaElement.ElementType.STRING, "hello,world", null),
                listOf(FormulaElement(FormulaElement.ElementType.STRING, ",", null)))
            println("split(1,'hello,world',',') = " + split.interpretRecursive(scope))
        }
    }

    fun loadProject(codeXml: File): Project {
        return XstreamSerializer.getInstance().xstream.fromXML(codeXml) as Project
    }
}

fun main(args: Array<String>) = DesktopModel.main(args)
