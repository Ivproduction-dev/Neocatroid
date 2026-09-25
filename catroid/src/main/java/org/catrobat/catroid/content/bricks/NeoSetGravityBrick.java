package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoSetGravityBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoSetGravityBrick() {
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_neo_set_gravity_edit_x);
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_neo_set_gravity_edit_y);
        addAllowedBrickField(BrickField.Z_POSITION, R.id.brick_neo_set_gravity_edit_z);
    }

    public NeoSetGravityBrick(float x, float y, float z) {
        this(new Formula(x), new Formula(y), new Formula(z));
    }

    public NeoSetGravityBrick(Formula x, Formula y, Formula z) {
        this();
        setFormulaWithBrickField(BrickField.X_POSITION, x);
        setFormulaWithBrickField(BrickField.Y_POSITION, y);
        setFormulaWithBrickField(BrickField.Z_POSITION, z);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_gravity;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetGravityAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.X_POSITION),
                        getFormulaWithBrickField(BrickField.Y_POSITION),
                        getFormulaWithBrickField(BrickField.Z_POSITION)));
    }
}
