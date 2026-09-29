package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.NeoPlay3DSoundAtCameraBrick;
import org.catrobat.catroid.content.bricks.NeoPlay3DSoundBrick;
import org.catrobat.catroid.content.bricks.NeoStop3DSoundBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class NeoBatch4BricksTest {

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
    public void playSoundBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoPlay3DSoundBrick().addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoPlay3DSoundAction(eq(sprite),
                any(SequenceAction.class), any(), any(Formula.class), any(Formula.class),
                any(Formula.class), anyInt());
    }

    @Test
    public void playSoundAtCameraBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoPlay3DSoundAtCameraBrick().addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoPlay3DSoundAction(eq(sprite),
                any(SequenceAction.class), eq(null), any(Formula.class), any(Formula.class),
                any(Formula.class), anyInt());
    }

    @Test
    public void stopSoundBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoStop3DSoundBrick().addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoStop3DSoundAction(eq(sprite),
                any(SequenceAction.class));
    }
}
