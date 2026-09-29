package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class CreateSessionVariableBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public CreateSessionVariableBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_session_variable_edit_name);
        addAllowedBrickField(BrickField.VALUE, R.id.brick_session_variable_edit_value);
    }

    public CreateSessionVariableBrick(String name, double value) {
        this(new Formula(name), new Formula(value));
    }

    public CreateSessionVariableBrick(Formula name, Formula value) {
        this();
        setFormulaWithBrickField(BrickField.NAME, name);
        setFormulaWithBrickField(BrickField.VALUE, value);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_create_session_variable;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createCreateSessionVariableAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.VALUE)));
    }
}
