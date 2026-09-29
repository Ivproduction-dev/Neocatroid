package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoGlideCameraToPositionBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoGlideCameraToPositionBrick() {
        addAllowedBrickField(BrickField.X_POSITION,
                R.id.brick_neo_glide_camera_position_edit_x);
        addAllowedBrickField(BrickField.Y_POSITION,
                R.id.brick_neo_glide_camera_position_edit_y);
        addAllowedBrickField(BrickField.Z_POSITION,
                R.id.brick_neo_glide_camera_position_edit_z);
        addAllowedBrickField(BrickField.DURATION_IN_SECONDS,
                R.id.brick_neo_glide_camera_position_edit_duration);
    }

    public NeoGlideCameraToPositionBrick(float x, float y, float z, float seconds) {
        this(new Formula(x), new Formula(y), new Formula(z), new Formula(seconds));
    }

    public NeoGlideCameraToPositionBrick(Formula x, Formula y, Formula z, Formula seconds) {
        this();
        setFormulaWithBrickField(BrickField.X_POSITION, x);
        setFormulaWithBrickField(BrickField.Y_POSITION, y);
        setFormulaWithBrickField(BrickField.Z_POSITION, z);
        setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, seconds);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_glide_camera_to_position;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoGlideCameraAction(sprite, sequence, null,
                        getFormulaWithBrickField(BrickField.X_POSITION),
                        getFormulaWithBrickField(BrickField.Y_POSITION),
                        getFormulaWithBrickField(BrickField.Z_POSITION),
                        getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
    }
}
