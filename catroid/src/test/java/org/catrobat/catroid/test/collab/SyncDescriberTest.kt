package org.catrobat.catroid.test.collab

import org.catrobat.catroid.collab.SyncDescriber
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.common.SoundInfo
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class SyncDescriberTest {

    private lateinit var old: Project
    private lateinit var new: Project

    @Before
    fun setUp() {
        old = Project(MockUtil.mockContextForProject(), "Old")
        new = Project(MockUtil.mockContextForProject(), "New")
        new.defaultScene.sceneId = old.defaultScene.sceneId
        new.defaultScene.backgroundSprite.spriteId = old.defaultScene.backgroundSprite.spriteId
    }

    private fun sprite(name: String, bricks: Int = 0): Sprite {
        val sprite = Sprite(name)
        if (bricks > 0) {
            val script = StartScript()
            repeat(bricks) { script.addBrick(TurnLeftBrick(90.0)) }
            sprite.addScript(script)
        }
        return sprite
    }

    @Test
    fun nullOldListsScenes() {
        val lines = SyncDescriber.describe(null, new)
        assertEquals(1, lines.size)
        assertTrue(lines[0].contains("scenes"))
    }

    @Test
    fun identicalProjectsDescribeNothing() {
        val before = sprite("Cat", 1)
        old.defaultScene.addSprite(before)
        val after = sprite("Cat", 1)
        after.spriteId = before.spriteId
        new.defaultScene.addSprite(after)
        assertTrue(SyncDescriber.describe(old, new).isEmpty())
    }

    @Test
    fun addedAndRemovedSprites() {
        old.defaultScene.addSprite(sprite("Old"))
        new.defaultScene.addSprite(sprite("New"))
        val lines = SyncDescriber.describe(old, new)
        assertTrue(lines.contains("+New"))
        assertTrue(lines.contains("-Old"))
    }

    @Test
    fun renamedSprite() {
        val kept = sprite("A")
        old.defaultScene.addSprite(kept)
        val renamed = sprite("B")
        renamed.spriteId = kept.spriteId
        new.defaultScene.addSprite(renamed)
        val lines = SyncDescriber.describe(old, new)
        assertTrue(lines.contains("A -> B"))
    }

    @Test
    fun brickDelta() {
        val before = sprite("Cat", 1)
        old.defaultScene.addSprite(before)
        val after = sprite("Cat", 3)
        after.spriteId = before.spriteId
        new.defaultScene.addSprite(after)
        val lines = SyncDescriber.describe(old, new)
        assertTrue(lines.any { it.contains("Cat") && it.contains("+2") })
    }

    @Test
    fun lookAdded() {
        val before = sprite("Cat")
        old.defaultScene.addSprite(before)
        val after = sprite("Cat")
        after.spriteId = before.spriteId
        after.lookList.add(LookData("face", File("face.png")))
        new.defaultScene.addSprite(after)
        val lines = SyncDescriber.describe(old, new)
        assertTrue(lines.any { it.contains("face") })
    }

    @Test
    fun soundRemoved() {
        val before = sprite("Cat")
        before.soundList.add(SoundInfo("meow", File("meow.mp3")))
        old.defaultScene.addSprite(before)
        val after = sprite("Cat")
        after.spriteId = before.spriteId
        new.defaultScene.addSprite(after)
        val lines = SyncDescriber.describe(old, new)
        assertTrue(lines.any { it.contains("meow") })
    }

    @Test
    fun longDiffIsCapped() {
        repeat(12) { index ->
            new.defaultScene.addSprite(sprite("Sprite$index"))
        }
        val lines = SyncDescriber.describe(old, new)
        assertEquals(SyncDescriber.MAX_LINES + 1, lines.size)
        assertTrue(lines.last().contains("more"))
    }

    @Test
    fun headlineJoinsFirstLines() {
        assertEquals("sync", SyncDescriber.headline(emptyList()))
        assertEquals("a; b", SyncDescriber.headline(listOf("a", "b", "c")))
    }
}
