package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Light2DControlBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class Light2DControlBrickTest {

    private Sprite sprite;

    @Before
    public void setUp() throws Exception {
        Project project = new Project(MockUtil.mockContextForProject(), "Project");
        Scene currentlyPlayingScene = new Scene("Currently playing scene", project);
        sprite = new Sprite("Sprite");
        currentlyPlayingScene.addSprite(sprite);
        project.addScene(currentlyPlayingScene);
        ProjectManager.getInstance().setCurrentProject(project);
        ProjectManager.getInstance().setCurrentlyEditedScene(new Scene());
        ProjectManager.getInstance().setCurrentlyPlayingScene(currentlyPlayingScene);
    }

    @Test
    public void testLight2DControlBrickCreatesAction() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);
        Light2DControlBrick brick = new Light2DControlBrick("torch", 3, "500.0", "hero");

        brick.addActionToSequence(sprite, new ScriptSequenceAction(Mockito.mock(Script.class)));

        verify(actionFactory).createLight2DControlAction(eq(sprite),
                any(ScriptSequenceAction.class),
                any(Formula.class), anyInt(), any(Formula.class), anyString());
    }

    @Test
    public void testLight2DControlBrickReturnsCorrectViewResource() {
        Light2DControlBrick brick = new Light2DControlBrick();
        org.junit.Assert.assertEquals(
                org.catrobat.catroid.R.layout.brick_light_2d_control,
                brick.getViewResource());
    }
}
