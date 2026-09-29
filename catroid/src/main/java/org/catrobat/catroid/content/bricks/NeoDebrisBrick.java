package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoDebrisBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoDebrisBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_debris_edit_name);
        addAllowedBrickField(BrickField.DURATION_IN_SECONDS, R.id.brick_neo_debris_edit_delay);
    }

    public NeoDebrisBrick(String objectName, float seconds) {
        this(new Formula(objectName), new Formula(seconds));
    }

    public NeoDebrisBrick(Formula objectName, Formula seconds) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, seconds);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_debris;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoDebrisAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
    }
}
