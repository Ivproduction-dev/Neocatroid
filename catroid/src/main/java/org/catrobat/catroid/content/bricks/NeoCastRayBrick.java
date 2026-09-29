package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoCastRayBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoCastRayBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_cast_ray_edit_name);
        addAllowedBrickField(BrickField.TARGET, R.id.brick_neo_cast_ray_edit_from);
        addAllowedBrickField(BrickField.VALUE_1, R.id.brick_neo_cast_ray_edit_toward);
        addAllowedBrickField(BrickField.VALUE_2, R.id.brick_neo_cast_ray_edit_distance);
        addAllowedBrickField(BrickField.VALUE_3, R.id.brick_neo_cast_ray_edit_max_hits);
    }

    public NeoCastRayBrick(String rayName, String fromName, String towardName, float distance,
            int maxHits) {
        this(new Formula(rayName), new Formula(fromName), new Formula(towardName),
                new Formula(distance), new Formula(maxHits));
    }

    public NeoCastRayBrick(Formula rayName, Formula fromName, Formula towardName,
            Formula distance, Formula maxHits) {
        this();
        setFormulaWithBrickField(BrickField.NAME, rayName);
        setFormulaWithBrickField(BrickField.TARGET, fromName);
        setFormulaWithBrickField(BrickField.VALUE_1, towardName);
        setFormulaWithBrickField(BrickField.VALUE_2, distance);
        setFormulaWithBrickField(BrickField.VALUE_3, maxHits);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_cast_ray;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCastRayAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.TARGET),
                        getFormulaWithBrickField(BrickField.VALUE_1),
                        getFormulaWithBrickField(BrickField.VALUE_2),
                        getFormulaWithBrickField(BrickField.VALUE_3)));
    }
}
