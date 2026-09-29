package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetStepSizeBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public SetStepSizeBrick() {
        addAllowedBrickField(BrickField.VALUE, R.id.brick_set_step_size_edit_value);
    }

    public SetStepSizeBrick(float stepSize) {
        this(new Formula(stepSize));
    }

    public SetStepSizeBrick(Formula stepSize) {
        this();
        setFormulaWithBrickField(BrickField.VALUE, stepSize);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_set_step_size;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createSetStepSizeAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.VALUE)));
    }
}
