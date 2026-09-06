package org.catrobat.catroid.collab

import org.catrobat.catroid.content.Project

object SyncDescriber {
    const val MAX_LINES = 8

    fun describe(old: Project?, new: Project): List<String> {
        val lines = ArrayList<String>()
        if (old == null) {
            lines.add("+" + new.sceneList.size + " scenes")
            return lines.take(MAX_LINES)
        }
        val oldScenes = old.sceneList.associateBy { it.sceneId }
        val newScenes = new.sceneList.associateBy { it.sceneId }
        for ((id, scene) in newScenes) {
            if (!oldScenes.containsKey(id)) lines.add("+" + "scene " + scene.name)
        }
        for ((id, scene) in oldScenes) {
            if (!newScenes.containsKey(id)) lines.add("-" + "scene " + scene.name)
        }
        val oldSprites = old.sceneList.flatMap { it.spriteList }.associateBy { it.spriteId }
        val newSprites = new.sceneList.flatMap { it.spriteList }.associateBy { it.spriteId }
        for ((id, sprite) in newSprites) {
            val prev = oldSprites[id]
            if (prev == null) {
                lines.add("+" + sprite.name)
            } else {
                if (prev.name != sprite.name) lines.add(prev.name + " -> " + sprite.name)
                val brickDelta = spriteBricks(sprite) - spriteBricks(prev)
                if (brickDelta > 0) lines.add(sprite.name + ": +" + brickDelta + " blocks")
                if (brickDelta < 0) lines.add(sprite.name + ": " + brickDelta + " blocks")
                diffNames(lines, sprite.name + " looks", lookNames(prev), lookNames(sprite), "+look ", "-look ")
                diffNames(lines, sprite.name + " sounds", soundNames(prev), soundNames(sprite), "+sound ", "-sound ")
            }
        }
        for ((id, sprite) in oldSprites) {
            if (!newSprites.containsKey(id)) lines.add("-" + sprite.name)
        }
        if (lines.size <= MAX_LINES) return lines
        return lines.take(MAX_LINES) + listOf("+" + (lines.size - MAX_LINES) + " more")
    }

    fun headline(lines: List<String>): String {
        if (lines.isEmpty()) return "sync"
        return lines.take(2).joinToString("; ")
    }

    private fun spriteBricks(sprite: org.catrobat.catroid.content.Sprite): Int {
        return try {
            sprite.scriptList.sumOf { it.brickList.size }
        } catch (e: Exception) {
            0
        }
    }

    private fun lookNames(sprite: org.catrobat.catroid.content.Sprite): Set<String> {
        return try {
            sprite.lookList.map { it.name }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun soundNames(sprite: org.catrobat.catroid.content.Sprite): Set<String> {
        return try {
            sprite.soundList.map { it.name }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun diffNames(
        lines: MutableList<String>,
        scope: String,
        oldNames: Set<String>,
        newNames: Set<String>,
        addPrefix: String,
        removePrefix: String
    ) {
        for (name in newNames - oldNames) lines.add(scope + ": " + addPrefix + name)
        for (name in oldNames - newNames) lines.add(scope + ": " + removePrefix + name)
    }
}
