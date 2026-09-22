package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoLoadModelBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoLoadModelBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_load_model_edit_name);
        addAllowedBrickField(BrickField.TEXT, R.id.brick_neo_load_model_edit_path);
    }

    public NeoLoadModelBrick(String objectName, String filePath) {
        this(new Formula(objectName), new Formula(filePath));
    }

    public NeoLoadModelBrick(Formula objectName, Formula filePath) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.TEXT, filePath);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_load_model;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoLoadModelAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.TEXT)));
    }
}
