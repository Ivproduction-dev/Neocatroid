package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.NeoCreatePrimitiveAction;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoCreateSphereBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoCreateSphereBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_create_sphere_edit_name);
    }

    public NeoCreateSphereBrick(String objectName) {
        this(new Formula(objectName));
    }

    public NeoCreateSphereBrick(Formula objectName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_create_sphere;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCreatePrimitiveAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        NeoCreatePrimitiveAction.PRIMITIVE_SPHERE));
    }
}
