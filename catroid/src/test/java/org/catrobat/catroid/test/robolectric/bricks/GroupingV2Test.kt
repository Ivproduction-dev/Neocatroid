package org.catrobat.catroid.test.robolectric.bricks

import android.content.Context
import androidx.preference.PreferenceManager
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SubCategoryHeaderBrick
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.AdmobInitializedEventBrick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.WhenAIResponseBrick
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.WhenBeforeUpdateBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.bricks.WhenBrick
import org.catrobat.catroid.content.bricks.WhenClonedBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.content.bricks.WhenHttpRequestFailedBrick
import org.catrobat.catroid.content.bricks.WhenIntervalBrick
import org.catrobat.catroid.content.bricks.WhenMouseButtonClickedBrick
import org.catrobat.catroid.content.bricks.WhenMqttMessageBrick
import org.catrobat.catroid.content.bricks.WhenNeo3DCollidesBrick
import org.catrobat.catroid.content.bricks.WhenNotificationShownBrick
import org.catrobat.catroid.content.bricks.WhenProjectExitsBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.content.bricks.WhenTcpMessageBrick
import org.catrobat.catroid.content.bricks.WhenTouchingSpriteBrick
import org.catrobat.catroid.content.bricks.WhenWindowResizedBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GroupingV2Test {

    private lateinit var activity: SpriteActivity
    private lateinit var factory: CategoryBricksFactory

    private fun category(resId: Int): String = activity.getString(resId)

    private fun bricks(categoryName: String): List<Brick> =
        factory.getBricks(categoryName, false, activity)

    private fun classes(categoryName: String): Set<Class<out Brick>> =
        bricks(categoryName).map { it.javaClass }.toSet()

    @Before
    fun setUp() {
        val controller = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = controller.get()
        val project = Project(activity, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        script.addBrick(SetXBrick())
        sprite.addScript(script)
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
        controller.create().resume()
        factory = CategoryBricksFactory()
    }

    @After
    fun tearDown() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().clear().commit()
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun defaultMode_keepsEventsInEventCategory() {
        CategoryBricksFactory.setGroupingV2(activity, false)
        val events = classes(category(R.string.category_event))
        assertTrue(events.contains(WhenBackgroundChangesBrick::class.java))
        assertTrue(events.contains(WhenTcpMessageBrick::class.java))
        assertTrue(events.contains(WhenAIResponseBrick::class.java))
        assertTrue(events.contains(AdmobInitializedEventBrick::class.java))
        assertTrue(events.contains(WhenClonedBrick::class.java))
    }

    @Test
    fun v2Mode_eventCategoryKeepsOnlyLaunchEvents() {
        CategoryBricksFactory.setGroupingV2(activity, true)
        val events = classes(category(R.string.category_event))
        assertTrue(events.contains(WhenStartedBrick::class.java))
        assertTrue(events.contains(WhenBrick::class.java))
        assertTrue(events.contains(BroadcastReceiverBrick::class.java))
        assertTrue(events.contains(WhenConditionBrick::class.java))

        val moved = listOf(
            WhenBackgroundChangesBrick::class.java,
            WhenTcpMessageBrick::class.java,
            WhenMqttMessageBrick::class.java,
            WhenHttpRequestFailedBrick::class.java,
            WhenAIResponseBrick::class.java,
            AdmobInitializedEventBrick::class.java,
            WhenClonedBrick::class.java,
            WhenIntervalBrick::class.java,
            WhenBeforeUpdateBrick::class.java,
            WhenProjectExitsBrick::class.java,
            WhenNeo3DCollidesBrick::class.java,
            WhenBounceOffBrick::class.java,
            WhenTouchingSpriteBrick::class.java,
            WhenMouseButtonClickedBrick::class.java,
            WhenWindowResizedBrick::class.java,
            WhenNotificationShownBrick::class.java
        )
        for (clazz in moved) {
            assertFalse("must leave Events in v2: " + clazz.simpleName, events.contains(clazz))
        }
    }

    @Test
    fun v2Mode_movedEventsAppearInTargetCategories() {
        CategoryBricksFactory.setGroupingV2(activity, true)
        assertTrue(classes(category(R.string.category_looks)).contains(WhenBackgroundChangesBrick::class.java))
        assertTrue(classes(category(R.string.category_motion)).contains(WhenBounceOffBrick::class.java))
        assertTrue(classes(category(R.string.category_motion)).contains(WhenTouchingSpriteBrick::class.java))
        assertTrue(classes(category(R.string.category_internet)).contains(WhenTcpMessageBrick::class.java))
        assertTrue(classes(category(R.string.category_internet)).contains(WhenMqttMessageBrick::class.java))
        assertTrue(classes(category(R.string.category_neural)).contains(WhenAIResponseBrick::class.java))
        assertTrue(classes(category(R.string.category_admob)).contains(AdmobInitializedEventBrick::class.java))
        assertTrue(classes(category(R.string.category_neo3d)).contains(WhenNeo3DCollidesBrick::class.java))
        assertTrue(classes(category(R.string.category_control)).contains(WhenIntervalBrick::class.java))
        assertTrue(classes(category(R.string.category_control)).contains(WhenBeforeUpdateBrick::class.java))
        assertTrue(classes(category(R.string.category_control)).contains(WhenProjectExitsBrick::class.java))
        assertTrue(classes(category(R.string.category_device)).contains(WhenMouseButtonClickedBrick::class.java))
        assertTrue(classes(category(R.string.category_device)).contains(WhenWindowResizedBrick::class.java))
        assertTrue(classes(category(R.string.category_device)).contains(WhenNotificationShownBrick::class.java))
    }

    @Test
    fun v2Mode_noDuplicatesWithinTargetCategory() {
        CategoryBricksFactory.setGroupingV2(activity, true)
        for (resId in intArrayOf(
            R.string.category_looks,
            R.string.category_motion,
            R.string.category_control,
            R.string.category_device,
            R.string.category_internet,
            R.string.category_admob,
            R.string.category_neural,
            R.string.category_neo3d
        )) {
            val list = bricks(category(resId))
            val counts = list.filter { it !is SubCategoryHeaderBrick }
                .groupingBy { it.javaClass }.eachCount()
            for ((clazz, count) in counts) {
                assertTrue(
                    "duplicate in " + category(resId) + ": " + clazz.simpleName,
                    count == 1
                )
            }
        }
    }

    @Test
    fun v2Mode_noBrickLost() {
        CategoryBricksFactory.setGroupingV2(activity, true)
        val union = mutableSetOf<Class<out Brick>>()
        for (resId in intArrayOf(
            R.string.category_event,
            R.string.category_control,
            R.string.category_motion,
            R.string.category_looks,
            R.string.category_device,
            R.string.category_neural,
            R.string.category_neo3d,
            R.string.category_internet,
            R.string.category_admob
        )) {
            union.addAll(classes(category(resId)))
        }
        val mustSurvive = listOf(
            WhenBackgroundChangesBrick::class.java,
            WhenTcpMessageBrick::class.java,
            WhenAIResponseBrick::class.java,
            AdmobInitializedEventBrick::class.java,
            WhenClonedBrick::class.java,
            WhenIntervalBrick::class.java,
            WhenNeo3DCollidesBrick::class.java,
            WhenNotificationShownBrick::class.java,
            WhenStartedBrick::class.java
        )
        for (clazz in mustSurvive) {
            assertTrue("lost in v2: " + clazz.simpleName, union.contains(clazz))
        }
    }
}
