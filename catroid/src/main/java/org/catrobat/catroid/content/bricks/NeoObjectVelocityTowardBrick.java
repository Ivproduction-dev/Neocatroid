package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoObjectVelocityTowardBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoObjectVelocityTowardBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_object_velocity_edit_name);
        addAllowedBrickField(BrickField.TARGET, R.id.brick_neo_object_velocity_edit_target);
        addAllowedBrickField(BrickField.SPEED, R.id.brick_neo_object_velocity_edit_speed);
    }

    public NeoObjectVelocityTowardBrick(String objectName, String targetName, float speed) {
        this(new Formula(objectName), new Formula(targetName), new Formula(speed));
    }

    public NeoObjectVelocityTowardBrick(Formula objectName, Formula targetName, Formula speed) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.TARGET, targetName);
        setFormulaWithBrickField(BrickField.SPEED, speed);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_object_velocity_toward;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoObjectVelocityTowardAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.TARGET),
                        getFormulaWithBrickField(BrickField.SPEED)));
    }
}
