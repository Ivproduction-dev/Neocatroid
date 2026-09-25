package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.NeoApplyObjectImpulseBrick;
import org.catrobat.catroid.content.bricks.NeoSetGravityBrick;
import org.catrobat.catroid.content.bricks.NeoSetObjectVelocityBrick;
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
public class NeoPhysicsBricksTest {

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

    @Test
    public void velocityBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoSetObjectVelocityBrick("myCube", 1f, 2f, 3f)
                .addActionToSequence(sprite,
                        new ScriptSequenceAction(Mockito.mock(Script.class)));

        verify(actionFactory).createNeoSetObjectVelocityAction(
                eq(sprite),
                any(SequenceAction.class),
                any(Formula.class),
                any(Formula.class),
                any(Formula.class),
                any(Formula.class));
    }

    @Test
    public void impulseBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoApplyObjectImpulseBrick("myCube", 0f, 5f, 0f)
                .addActionToSequence(sprite,
                        new ScriptSequenceAction(Mockito.mock(Script.class)));

        verify(actionFactory).createNeoApplyObjectImpulseAction(
                eq(sprite),
                any(SequenceAction.class),
                any(Formula.class),
                any(Formula.class),
                any(Formula.class),
                any(Formula.class));
    }

    @Test
    public void gravityBrickDelegatesToActionFactory() {
        ActionFactory actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);

        new NeoSetGravityBrick(0f, -9.81f, 0f)
                .addActionToSequence(sprite,
                        new ScriptSequenceAction(Mockito.mock(Script.class)));

        verify(actionFactory).createNeoSetGravityAction(
                eq(sprite),
                any(SequenceAction.class),
                any(Formula.class),
                any(Formula.class),
                any(Formula.class));
    }
}
