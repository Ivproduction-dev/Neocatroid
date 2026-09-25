package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoMoveObjectForwardBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoMoveObjectForwardBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_move_object_forward_edit_name);
        addAllowedBrickField(BrickField.DISTANCE, R.id.brick_neo_move_object_forward_edit_distance);
    }

    public NeoMoveObjectForwardBrick(String objectName, float distance) {
        this(new Formula(objectName), new Formula(distance));
    }

    public NeoMoveObjectForwardBrick(Formula objectName, Formula distance) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.DISTANCE, distance);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_move_object_forward;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoMoveObjectForwardAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.DISTANCE)));
    }
}
