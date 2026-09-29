package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoMoveObjectStepsBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoMoveObjectStepsBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_move_object_steps_edit_name);
        addAllowedBrickField(BrickField.STEPS, R.id.brick_neo_move_object_steps_edit_steps);
    }

    public NeoMoveObjectStepsBrick(String objectName, float steps) {
        this(new Formula(objectName), new Formula(steps));
    }

    public NeoMoveObjectStepsBrick(Formula objectName, Formula steps) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.STEPS, steps);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_move_object_steps;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoMoveObjectStepsAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.STEPS)));
    }
}
