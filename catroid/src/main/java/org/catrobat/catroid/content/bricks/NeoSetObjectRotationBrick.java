package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoSetObjectRotationBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoSetObjectRotationBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_set_object_rotation_edit_name);
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_neo_set_object_rotation_edit_x);
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_neo_set_object_rotation_edit_y);
        addAllowedBrickField(BrickField.Z_POSITION, R.id.brick_neo_set_object_rotation_edit_z);
    }

    public NeoSetObjectRotationBrick(String objectName, float yaw, float pitch, float roll) {
        this(new Formula(objectName), new Formula(yaw), new Formula(pitch), new Formula(roll));
    }

    public NeoSetObjectRotationBrick(Formula objectName, Formula yaw, Formula pitch,
            Formula roll) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.X_POSITION, yaw);
        setFormulaWithBrickField(BrickField.Y_POSITION, pitch);
        setFormulaWithBrickField(BrickField.Z_POSITION, roll);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_object_rotation;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetObjectRotationAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.X_POSITION),
                        getFormulaWithBrickField(BrickField.Y_POSITION),
                        getFormulaWithBrickField(BrickField.Z_POSITION)));
    }
}
