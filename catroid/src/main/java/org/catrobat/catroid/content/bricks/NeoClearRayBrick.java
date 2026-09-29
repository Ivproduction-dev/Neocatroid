package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoClearRayBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoClearRayBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_clear_ray_edit_name);
    }

    public NeoClearRayBrick(String rayName) {
        this(new Formula(rayName));
    }

    public NeoClearRayBrick(Formula rayName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, rayName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_clear_ray;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoClearRayAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME)));
    }
}
