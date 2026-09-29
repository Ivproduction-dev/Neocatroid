package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoRenameObjectBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoRenameObjectBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_rename_object_edit_name);
        addAllowedBrickField(BrickField.TARGET, R.id.brick_neo_rename_object_edit_new_name);
    }

    public NeoRenameObjectBrick(String objectName, String newName) {
        this(new Formula(objectName), new Formula(newName));
    }

    public NeoRenameObjectBrick(Formula objectName, Formula newName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.TARGET, newName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_rename_object;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoRenameObjectAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.TARGET)));
    }
}
