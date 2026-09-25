package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoSetObjectVelocityBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoSetObjectVelocityBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_set_object_velocity_edit_name);
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_neo_set_object_velocity_edit_x);
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_neo_set_object_velocity_edit_y);
        addAllowedBrickField(BrickField.Z_POSITION, R.id.brick_neo_set_object_velocity_edit_z);
    }

    public NeoSetObjectVelocityBrick(String objectName, float x, float y, float z) {
        this(new Formula(objectName), new Formula(x), new Formula(y), new Formula(z));
    }

    public NeoSetObjectVelocityBrick(Formula objectName, Formula x, Formula y, Formula z) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.X_POSITION, x);
        setFormulaWithBrickField(BrickField.Y_POSITION, y);
        setFormulaWithBrickField(BrickField.Z_POSITION, z);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_object_velocity;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetObjectVelocityAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.X_POSITION),
                        getFormulaWithBrickField(BrickField.Y_POSITION),
                        getFormulaWithBrickField(BrickField.Z_POSITION)));
    }
}
