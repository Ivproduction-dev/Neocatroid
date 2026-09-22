package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.NeoCreatePrimitiveAction;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoCreateCubeBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoCreateCubeBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_create_cube_edit_name);
    }

    public NeoCreateCubeBrick(String objectName) {
        this(new Formula(objectName));
    }

    public NeoCreateCubeBrick(Formula objectName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_create_cube;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCreatePrimitiveAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        NeoCreatePrimitiveAction.PRIMITIVE_CUBE));
    }
}
