package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.NeoCameraFollowBrick;
import org.catrobat.catroid.content.bricks.NeoCameraLookAtBrick;
import org.catrobat.catroid.content.bricks.NeoClearObjectsBrick;
import org.catrobat.catroid.content.bricks.NeoMoveObjectForwardBrick;
import org.catrobat.catroid.content.bricks.NeoSetObjectVisibleBrick;
import org.catrobat.catroid.content.bricks.NeoTurnObjectTowardBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class NeoGameplayBricksTest {

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
    }

    private ScriptSequenceAction newSequence() {
        return new ScriptSequenceAction(Mockito.mock(Script.class));
    }

    @Test
    public void followBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoCameraFollowBrick("myObject", 0f, 3f, 5f, 1)
                .addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoCameraFollowAction(
                eq(sprite),
                any(SequenceAction.class),
                any(Formula.class),
                any(Formula.class),
                any(Formula.class),
                any(Formula.class),
                eq(1));
    }

    @Test
    public void lookAtBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoCameraLookAtBrick("myObject").addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoCameraLookAtAction(
                eq(sprite),
                any(SequenceAction.class),
                any(Formula.class));
    }

    @Test
    public void moveForwardBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoMoveObjectForwardBrick("myObject", 2f).addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoMoveObjectForwardAction(
                eq(sprite),
                any(SequenceAction.class),
                any(Formula.class),
                any(Formula.class));
    }

    @Test
    public void turnTowardBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoTurnObjectTowardBrick("myObject", "myTarget")
                .addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoTurnObjectTowardAction(
                eq(sprite),
                any(SequenceAction.class),
                any(Formula.class),
                any(Formula.class));
    }

    @Test
    public void visibleBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoSetObjectVisibleBrick("myObject", 1).addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoSetObjectVisibleAction(
                eq(sprite),
                any(SequenceAction.class),
                any(Formula.class),
                eq(1));
    }

    @Test
    public void clearObjectsBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoClearObjectsBrick().addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoClearObjectsAction(
                eq(sprite),
                any(SequenceAction.class));
    }
}
