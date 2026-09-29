package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.NeoCameraPositionToObjectBrick;
import org.catrobat.catroid.content.bricks.NeoCopyObjectPositionBrick;
import org.catrobat.catroid.content.bricks.NeoMoveObjectStepsBrick;
import org.catrobat.catroid.content.bricks.NeoObjectPositionToCameraBrick;
import org.catrobat.catroid.content.bricks.NeoRenameObjectBrick;
import org.catrobat.catroid.content.bricks.NeoSetObjectStepSizeBrick;
import org.catrobat.catroid.content.bricks.NeoSetObjectVariableBrick;
import org.catrobat.catroid.content.bricks.NeoTurnObjectToCameraBrick;
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
public class NeoBatch1BricksTest {

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
    public void renameBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoRenameObjectBrick("a", "b").addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoRenameObjectAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class));
    }

    @Test
    public void copyPositionBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoCopyObjectPositionBrick("a", "b").addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoCopyObjectPositionAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class));
    }

    @Test
    public void cameraPositionBricksDelegateToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoObjectPositionToCameraBrick("a").addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoObjectPositionToCameraAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class));

        new NeoCameraPositionToObjectBrick("a").addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoCameraPositionToObjectAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class));

        new NeoTurnObjectToCameraBrick("a").addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoTurnObjectToCameraAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class));
    }

    @Test
    public void stepsBricksDelegateToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoMoveObjectStepsBrick("a", 3f).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoMoveObjectStepsAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class));

        new NeoSetObjectStepSizeBrick(2f).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoSetObjectStepSizeAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class));
    }

    @Test
    public void objectVariableBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoSetObjectVariableBrick("a", "PlayerID", 1.0)
                .addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoSetObjectVariableAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class),
                any(Formula.class));
    }
}
