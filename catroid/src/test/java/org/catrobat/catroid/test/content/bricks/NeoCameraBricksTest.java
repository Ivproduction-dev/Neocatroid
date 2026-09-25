package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.NeoCameraTouchLookBrick;
import org.catrobat.catroid.content.bricks.NeoSetCameraRotationBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.neo3d.Neo3DEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class NeoCameraBricksTest {

    private Sprite sprite;
    private ActionFactory actionFactory;
    private ScriptSequenceAction sequence;

    @Before
    public void setUp() {
        sprite = new Sprite("TestSprite");
        actionFactory = Mockito.mock(ActionFactory.class);
        sprite.setActionFactory(actionFactory);
        sequence = new ScriptSequenceAction(Mockito.mock(
                org.catrobat.catroid.content.Script.class));
    }

    @Test
    public void rotationBrickDelegatesToActionFactory() {
        new NeoSetCameraRotationBrick(10f, -20f, 30f).addActionToSequence(sprite, sequence);

        verify(actionFactory).createNeoSetCameraRotationAction(
                eq(sprite), any(SequenceAction.class), any(Formula.class),
                any(Formula.class), any(Formula.class));
    }

    @Test
    public void touchLookBrickDelegatesModeAndLimits() {
        new NeoCameraTouchLookBrick(Neo3DEngine.CameraTouchMode.FIRST_PERSON.ordinal(),
                0.4f, -45f, 60f).addActionToSequence(sprite, sequence);

        verify(actionFactory).createNeoCameraTouchLookAction(
                eq(sprite), any(SequenceAction.class),
                eq(Neo3DEngine.CameraTouchMode.FIRST_PERSON.ordinal()),
                any(Formula.class), any(Formula.class), any(Formula.class));
    }
}
