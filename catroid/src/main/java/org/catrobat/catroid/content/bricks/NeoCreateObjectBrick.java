package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoCreateObjectBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoCreateObjectBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_create_object_edit_name);
    }

    public NeoCreateObjectBrick(String objectName) {
        this(new Formula(objectName));
    }

    public NeoCreateObjectBrick(Formula objectName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_create_object;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCreateObjectAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME)));
    }
}
