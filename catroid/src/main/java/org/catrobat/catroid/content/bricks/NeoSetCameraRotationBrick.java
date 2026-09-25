package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoSetCameraRotationBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoSetCameraRotationBrick() {
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_neo_set_camera_rotation_edit_x);
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_neo_set_camera_rotation_edit_y);
        addAllowedBrickField(BrickField.Z_POSITION, R.id.brick_neo_set_camera_rotation_edit_z);
    }

    public NeoSetCameraRotationBrick(float yaw, float pitch, float roll) {
        this(new Formula(yaw), new Formula(pitch), new Formula(roll));
    }

    public NeoSetCameraRotationBrick(Formula yaw, Formula pitch, Formula roll) {
        this();
        setFormulaWithBrickField(BrickField.X_POSITION, yaw);
        setFormulaWithBrickField(BrickField.Y_POSITION, pitch);
        setFormulaWithBrickField(BrickField.Z_POSITION, roll);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_camera_rotation;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetCameraRotationAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.X_POSITION),
                        getFormulaWithBrickField(BrickField.Y_POSITION),
                        getFormulaWithBrickField(BrickField.Z_POSITION)));
    }
}
