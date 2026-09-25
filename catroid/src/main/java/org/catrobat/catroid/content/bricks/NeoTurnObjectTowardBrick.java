package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoTurnObjectTowardBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoTurnObjectTowardBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_turn_object_toward_edit_name);
        addAllowedBrickField(BrickField.TARGET, R.id.brick_neo_turn_object_toward_edit_target);
    }

    public NeoTurnObjectTowardBrick(String objectName, String targetName) {
        this(new Formula(objectName), new Formula(targetName));
    }

    public NeoTurnObjectTowardBrick(Formula objectName, Formula targetName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.TARGET, targetName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_turn_object_toward;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoTurnObjectTowardAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.TARGET)));
    }
}
