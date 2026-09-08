package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.ShadowCasting2DBrick;
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
public class ShadowCasting2DBrickTest {

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
    public void testShadowCasting2DBrickCreatesAction() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);
        ShadowCasting2DBrick brick = new ShadowCasting2DBrick("wall", 1);

        brick.addActionToSequence(sprite, new ScriptSequenceAction(Mockito.mock(Script.class)));

        verify(actionFactory).createShadowCasting2DAction(eq(sprite),
                any(ScriptSequenceAction.class), anyString(), anyInt());
    }

    @Test
    public void testShadowCasting2DBrickReturnsCorrectViewResource() {
        ShadowCasting2DBrick brick = new ShadowCasting2DBrick();
        org.junit.Assert.assertEquals(
                org.catrobat.catroid.R.layout.brick_shadow_casting_2d,
                brick.getViewResource());
    }
}
