package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class DeleteSessionVariableBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public DeleteSessionVariableBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_delete_session_variable_edit_name);
    }

    public DeleteSessionVariableBrick(String name) {
        this(new Formula(name));
    }

    public DeleteSessionVariableBrick(Formula name) {
        this();
        setFormulaWithBrickField(BrickField.NAME, name);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_delete_session_variable;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createDeleteSessionVariableAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME)));
    }
}
