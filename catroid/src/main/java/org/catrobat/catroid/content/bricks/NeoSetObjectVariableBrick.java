package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoSetObjectVariableBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoSetObjectVariableBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_set_object_variable_edit_name);
        addAllowedBrickField(BrickField.VARNAME, R.id.brick_neo_set_object_variable_edit_key);
        addAllowedBrickField(BrickField.VALUE, R.id.brick_neo_set_object_variable_edit_value);
    }

    public NeoSetObjectVariableBrick(String objectName, String key, double value) {
        this(new Formula(objectName), new Formula(key), new Formula(value));
    }

    public NeoSetObjectVariableBrick(Formula objectName, Formula key, Formula value) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.VARNAME, key);
        setFormulaWithBrickField(BrickField.VALUE, value);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_object_variable;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetObjectVariableAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.VARNAME),
                        getFormulaWithBrickField(BrickField.VALUE)));
    }
}
