package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.NeoDebrisBrick;
import org.catrobat.catroid.content.bricks.NeoGlideCameraToObjectBrick;
import org.catrobat.catroid.content.bricks.NeoGlideCameraToPositionBrick;
import org.catrobat.catroid.content.bricks.NeoGlideObjectBrick;
import org.catrobat.catroid.content.bricks.NeoObjectVelocityCameraBrick;
import org.catrobat.catroid.content.bricks.NeoObjectVelocityTowardBrick;
import org.catrobat.catroid.content.bricks.NeoSetPhysicsCollisionBrick;
import org.catrobat.catroid.content.bricks.WhenNeo3DTouchBrick;
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
public class NeoBatch2BricksTest {

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
    public void glideObjectBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoGlideObjectBrick("a", "b", 2f).addActionToSequence(sprite, newSequence());

        verify(actionFactory).createNeoGlideObjectAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class),
                any(Formula.class));
    }

    @Test
    public void glideCameraBricksDelegateToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoGlideCameraToObjectBrick("b", 2f).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoGlideCameraAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), eq(null), eq(null), eq(null),
                any(Formula.class));

        new NeoGlideCameraToPositionBrick(1f, 2f, 3f, 2f)
                .addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoGlideCameraAction(eq(sprite),
                any(SequenceAction.class), eq(null), any(Formula.class), any(Formula.class),
                any(Formula.class), any(Formula.class));
    }

    @Test
    public void velocityBricksDelegateToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoObjectVelocityTowardBrick("a", "b", 5f)
                .addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoObjectVelocityTowardAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class),
                any(Formula.class));

        new NeoObjectVelocityCameraBrick("a", 5f).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoObjectVelocityTowardAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), eq(null), any(Formula.class));
    }

    @Test
    public void debrisAndCollisionBricksDelegateToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoDebrisBrick("a", 10f).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoDebrisAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), any(Formula.class));

        new NeoSetPhysicsCollisionBrick("a", 1).addActionToSequence(sprite, newSequence());
        verify(actionFactory).createNeoSetPhysicsCollisionAction(eq(sprite),
                any(SequenceAction.class), any(Formula.class), eq(1));
    }

    @Test
    public void touchBrickLinksScriptAndEventId() {
        WhenNeo3DTouchBrick brick = new WhenNeo3DTouchBrick();
        assertTrue(brick.getScript() instanceof org.catrobat.catroid.content.WhenNeo3DTouchScript);
        assertEquals(brick, brick.getScript().getScriptBrick());

        Sprite host = new Sprite("host");
        org.catrobat.catroid.content.eventids.Neo3DTouchEventId first =
                (org.catrobat.catroid.content.eventids.Neo3DTouchEventId) brick.getScript()
                        .createEventId(host);
        org.catrobat.catroid.content.eventids.Neo3DTouchEventId second =
                (org.catrobat.catroid.content.eventids.Neo3DTouchEventId) brick.getScript()
                        .createEventId(host);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void touchBrickCloneRelinks() throws CloneNotSupportedException {
        WhenNeo3DTouchBrick brick = new WhenNeo3DTouchBrick();
        WhenNeo3DTouchBrick clone = (WhenNeo3DTouchBrick) brick.clone();
        assertNotSame(brick.getScript(), clone.getScript());
        assertEquals(clone, clone.getScript().getScriptBrick());
    }
}
