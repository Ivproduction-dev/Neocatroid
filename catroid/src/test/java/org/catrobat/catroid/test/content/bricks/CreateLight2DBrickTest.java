package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.CreateLight2DBrick;
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
public class CreateLight2DBrickTest {

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
    public void testCreateLight2DBrickCreatesAction() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);
        CreateLight2DBrick brick = new CreateLight2DBrick("torch", 10.0, 20.0, 400.0, 1.0, 0xFFE0B0);

        brick.addActionToSequence(sprite, new ScriptSequenceAction(Mockito.mock(Script.class)));

        verify(actionFactory).createLight2DCreateAction(eq(sprite),
                any(ScriptSequenceAction.class),
                any(Formula.class), any(Formula.class), any(Formula.class),
                any(Formula.class), any(Formula.class), any(Formula.class));
    }

    @Test
    public void testCreateLight2DBrickFieldMapping() {
        CreateLight2DBrick brick = new CreateLight2DBrick("torch", 10.0, 20.0, 400.0, 1.0, 0xFFE0B0);
        org.junit.Assert.assertNotNull(brick.getFormulaWithBrickField(Brick.BrickField.TEXT));
        org.junit.Assert.assertNotNull(brick.getFormulaWithBrickField(Brick.BrickField.X_POSITION));
        org.junit.Assert.assertNotNull(brick.getFormulaWithBrickField(Brick.BrickField.Y_POSITION));
        org.junit.Assert.assertNotNull(brick.getFormulaWithBrickField(Brick.BrickField.VALUE_1));
        org.junit.Assert.assertNotNull(brick.getFormulaWithBrickField(Brick.BrickField.VALUE_2));
        org.junit.Assert.assertNotNull(brick.getFormulaWithBrickField(Brick.BrickField.VALUE_3));
    }

    @Test
    public void testCreateLight2DBrickReturnsCorrectViewResource() {
        CreateLight2DBrick brick = new CreateLight2DBrick();
        org.junit.Assert.assertEquals(
                org.catrobat.catroid.R.layout.brick_create_light_2d,
                brick.getViewResource());
    }
}
