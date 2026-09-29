package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SessionData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.MoveNStepsAction;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.ChangeSessionVariableBrick;
import org.catrobat.catroid.content.bricks.CreateSessionVariableBrick;
import org.catrobat.catroid.content.bricks.DeleteSessionVariableBrick;
import org.catrobat.catroid.content.bricks.HideSpritesByPrefixBrick;
import org.catrobat.catroid.content.bricks.SetSessionVariableBrick;
import org.catrobat.catroid.content.bricks.SetStepSizeBrick;
import org.catrobat.catroid.content.bricks.ShowSpritesByPrefixBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class SessionPrefixStepBricksTest {

    private Sprite sprite;

    @Before
    public void setUp() throws Exception {
        Project project = new Project(MockUtil.mockContextForProject(), "Project");
        Scene scene = new Scene("Scene", project);
        sprite = new Sprite("TestSprite");
        scene.addSprite(sprite);
        project.addScene(scene);
        ProjectManager.getInstance().setCurrentProject(project);
        ProjectManager.getInstance().setCurrentlyEditedScene(scene);
        ProjectManager.getInstance().setCurrentlyPlayingScene(scene);
        SessionData.clear();
        MoveNStepsAction.stepSizeMultiplier = 1f;
    }

    private ScriptSequenceAction newSequence() {
        return new ScriptSequenceAction(Mockito.mock(Script.class));
    }

    @Test
    public void sessionBricksDelegateToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new CreateSessionVariableBrick("v", 10.0).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createCreateSessionVariableAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class));

        new SetSessionVariableBrick("v", 10.0).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createSetSessionVariableAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class));

        new ChangeSessionVariableBrick("v", 10.0).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createChangeSessionVariableAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class));

        new DeleteSessionVariableBrick("v").addActionToSequence(sprite, newSequence());
        verify(actionFactory).createDeleteSessionVariableAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class));
    }

    @Test
    public void prefixBricksDelegateWithVisibilityFlag() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new ShowSpritesByPrefixBrick("show_").addActionToSequence(sprite, newSequence());
        verify(actionFactory).createSetSpritesVisibleByPrefixAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), eq(true));

        new HideSpritesByPrefixBrick("hide_").addActionToSequence(sprite, newSequence());
        verify(actionFactory).createSetSpritesVisibleByPrefixAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), eq(false));
    }

    @Test
    public void stepSizeBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new SetStepSizeBrick(2f).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createSetStepSizeAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class));
    }

    @Test
    public void sessionDataStoresAndClears() {
        SessionData.set("score", 10.0);
        assertEquals(10.0, SessionData.get("score"));
        assertEquals(0.0, SessionData.get("missing"));
        SessionData.remove("score");
        assertEquals(0.0, SessionData.get("score"));
    }

    @Test
    public void stepSizeMultiplierDefaultsToOne() {
        assertEquals(1f, MoveNStepsAction.stepSizeMultiplier, 0f);
    }
}
