package org.catrobat.catroid.test.twodlight;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ControlLight2DAction;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.actions.ShadowCasting2DAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.twodlight.Light2D;
import org.catrobat.catroid.twodlight.LightManager2D;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.lang.ref.WeakReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Light2DActionsTest {

    private LightManager2D manager;
    private Sprite sprite;

    @Before
    public void setUp() throws Exception {
        Project project = new Project(MockUtil.mockContextForProject(), "Project");
        Scene currentlyPlayingScene = new Scene("Currently playing scene", project);
        sprite = new Sprite("Sprite");
        currentlyPlayingScene.addSprite(sprite);
        project.addScene(currentlyPlayingScene);
        ProjectManager.getInstance().setCurrentProject(project);
        ProjectManager.getInstance().setCurrentlyPlayingScene(currentlyPlayingScene);

        manager = new LightManager2D();
        StageListener mockListener = Mockito.mock(StageListener.class);
        mockListener.lightManager2D = manager;
        StageActivity mockActivity = Mockito.mock(StageActivity.class);
        mockActivity.stageListener = mockListener;
        StageActivity.activeStageActivity = new WeakReference<>(mockActivity);
    }

    @After
    public void tearDown() {
        StageActivity.activeStageActivity = null;
    }

    private ScriptSequenceAction newSequence() {
        return new ScriptSequenceAction(Mockito.mock(Script.class));
    }

    @Test
    public void createLightReachesManagerWithAllParams() {
        sprite.getActionFactory().createLight2DCreateAction(sprite, newSequence(),
                new Formula("torch"),
                new Formula(10.0), new Formula(20.0),
                new Formula(400.0), new Formula(1.0),
                new Formula(0xFFE0B0)).act(1f);

        Light2D light = manager.getLight("torch");
        assertNotNull(light);
        assertEquals(10f, light.getX(), 0.001f);
        assertEquals(20f, light.getY(), 0.001f);
        assertEquals(400f, light.getRadius(), 0.001f);
        assertEquals(1f, light.getIntensity(), 0.001f);
        assertEquals(0xFFE0B0, light.getColor());
        assertTrue(light.isEnabled());

        manager.update(0f, 0f, 384f, 592f, null);
        assertTrue(manager.hasLights());
        assertEquals(1, manager.getActiveLights().size());
        assertEquals("torch", manager.getActiveLights().get(0).getId());
    }

    @Test
    public void turnOffRemovesLightFromActiveSet() {
        sprite.getActionFactory().createLight2DCreateAction(sprite, newSequence(),
                new Formula("torch"),
                new Formula(0.0), new Formula(0.0),
                new Formula(400.0), new Formula(1.0),
                new Formula(0xFFFFFF)).act(1f);
        assertNotNull(manager.getLight("torch"));

        sprite.getActionFactory().createLight2DControlAction(sprite, newSequence(),
                new Formula("torch"), ControlLight2DAction.ACTION_TURN_OFF,
                new Formula(0.0), "").act(1f);

        assertFalse(manager.getLight("torch").isEnabled());
        manager.update(0f, 0f, 384f, 592f, null);
        assertFalse(manager.hasLights());
        assertTrue(manager.getActiveLights().isEmpty());
    }

    @Test
    public void setAmbientReachesManager() {
        sprite.getActionFactory().createLight2DControlAction(sprite, newSequence(),
                new Formula(""), ControlLight2DAction.ACTION_SET_AMBIENT,
                new Formula(0.5), "").act(1f);
        assertEquals(0.5f, manager.getAmbient(), 0.001f);
    }

    @Test
    public void setRadiusReachesManager() {
        sprite.getActionFactory().createLight2DCreateAction(sprite, newSequence(),
                new Formula("torch"),
                new Formula(0.0), new Formula(0.0),
                new Formula(400.0), new Formula(1.0),
                new Formula(0xFFFFFF)).act(1f);

        sprite.getActionFactory().createLight2DControlAction(sprite, newSequence(),
                new Formula("torch"), ControlLight2DAction.ACTION_SET_RADIUS,
                new Formula(123.0), "").act(1f);

        assertEquals(123f, manager.getLight("torch").getRadius(), 0.001f);
    }

    @Test
    public void shadowCastingReachesManager() {
        assertTrue(manager.isSpriteShadowCasting("wall"));
        sprite.getActionFactory().createShadowCasting2DAction(sprite, newSequence(),
                "wall", ShadowCasting2DAction.NO_SHADOW).act(1f);
        assertFalse(manager.isSpriteShadowCasting("wall"));
    }

    @Test
    public void actionsAreNoOpWithoutStage() {
        StageActivity.activeStageActivity = null;
        sprite.getActionFactory().createLight2DCreateAction(sprite, newSequence(),
                new Formula("torch"),
                new Formula(0.0), new Formula(0.0),
                new Formula(400.0), new Formula(1.0),
                new Formula(0xFFFFFF)).act(1f);
        assertEquals(0, manager.getLightCount());
    }
}
