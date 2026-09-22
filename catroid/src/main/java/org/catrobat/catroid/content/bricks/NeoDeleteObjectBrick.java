package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoDeleteObjectBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoDeleteObjectBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_delete_object_edit_name);
    }

    public NeoDeleteObjectBrick(String objectName) {
        this(new Formula(objectName));
    }

    public NeoDeleteObjectBrick(Formula objectName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_delete_object;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoDeleteObjectAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME)));
    }
}
