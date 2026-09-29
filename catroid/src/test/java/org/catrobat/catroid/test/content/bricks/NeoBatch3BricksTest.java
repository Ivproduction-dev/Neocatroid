package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.NeoCastRayBrick;
import org.catrobat.catroid.content.bricks.NeoClearRayBrick;
import org.catrobat.catroid.content.bricks.WhenNeo3DRayHitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class NeoBatch3BricksTest {

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
    public void castRayBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoCastRayBrick("ray", "a", "b", 200f, 50)
                .addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoCastRayAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class),
                any(Formula.class), any(Formula.class), any(Formula.class));
    }

    @Test
    public void clearRayBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoClearRayBrick("ray").addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoClearRayAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class));
    }

    @Test
    public void rayHitBrickLinksScriptAndEventId() {
        WhenNeo3DRayHitBrick brick = new WhenNeo3DRayHitBrick();
        assertTrue(brick.getScript() instanceof org.catrobat.catroid.content.WhenNeo3DRayHitScript);
        assertEquals(brick, brick.getScript().getScriptBrick());

        Sprite host = new Sprite("host");
        org.catrobat.catroid.content.eventids.Neo3DRayHitEventId first =
                (org.catrobat.catroid.content.eventids.Neo3DRayHitEventId) brick.getScript()
                        .createEventId(host);
        org.catrobat.catroid.content.eventids.Neo3DRayHitEventId second =
                (org.catrobat.catroid.content.eventids.Neo3DRayHitEventId) brick.getScript()
                        .createEventId(host);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void rayHitBrickCloneRelinks() throws CloneNotSupportedException {
        WhenNeo3DRayHitBrick brick = new WhenNeo3DRayHitBrick();
        WhenNeo3DRayHitBrick clone = (WhenNeo3DRayHitBrick) brick.clone();
        assertNotSame(brick.getScript(), clone.getScript());
        assertEquals(clone, clone.getScript().getScriptBrick());
    }
}
