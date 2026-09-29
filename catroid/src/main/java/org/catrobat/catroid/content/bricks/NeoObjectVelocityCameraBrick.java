package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoObjectVelocityCameraBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoObjectVelocityCameraBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_object_velocity_camera_edit_name);
        addAllowedBrickField(BrickField.SPEED, R.id.brick_neo_object_velocity_camera_edit_speed);
    }

    public NeoObjectVelocityCameraBrick(String objectName, float speed) {
        this(new Formula(objectName), new Formula(speed));
    }

    public NeoObjectVelocityCameraBrick(Formula objectName, Formula speed) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.SPEED, speed);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_object_velocity_camera;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoObjectVelocityTowardAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME), null,
                        getFormulaWithBrickField(BrickField.SPEED)));
    }
}
