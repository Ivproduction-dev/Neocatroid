package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoSetObjectStepSizeBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoSetObjectStepSizeBrick() {
        addAllowedBrickField(BrickField.VALUE, R.id.brick_neo_set_object_step_size_edit_value);
    }

    public NeoSetObjectStepSizeBrick(float stepSize) {
        this(new Formula(stepSize));
    }

    public NeoSetObjectStepSizeBrick(Formula stepSize) {
        this();
        setFormulaWithBrickField(BrickField.VALUE, stepSize);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_object_step_size;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetObjectStepSizeAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.VALUE)));
    }
}
