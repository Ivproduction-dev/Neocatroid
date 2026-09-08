package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class CreateLight2DBrick extends VisualPlacementBrick {

    private static final long serialVersionUID = 1L;

    public CreateLight2DBrick() {
        addAllowedBrickField(BrickField.TEXT, R.id.brick_create_light_2d_name);
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_create_light_2d_x);
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_create_light_2d_y);
        addAllowedBrickField(BrickField.VALUE_1, R.id.brick_create_light_2d_radius);
        addAllowedBrickField(BrickField.VALUE_2, R.id.brick_create_light_2d_intensity);
        addAllowedBrickField(BrickField.VALUE_3, R.id.brick_create_light_2d_color);
    }

    public CreateLight2DBrick(String name, double x, double y, double radius, double intensity,
            int color) {
        this();
        setFormulaWithBrickField(BrickField.TEXT, new Formula(name));
        setFormulaWithBrickField(BrickField.X_POSITION, new Formula(x));
        setFormulaWithBrickField(BrickField.Y_POSITION, new Formula(y));
        setFormulaWithBrickField(BrickField.VALUE_1, new Formula(radius));
        setFormulaWithBrickField(BrickField.VALUE_2, new Formula(intensity));
        setFormulaWithBrickField(BrickField.VALUE_3, new Formula(color));
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_create_light_2d;
    }

    @Override
    public BrickField getXBrickField() {
        return BrickField.X_POSITION;
    }

    @Override
    public BrickField getYBrickField() {
        return BrickField.Y_POSITION;
    }

    @Override
    public int getXEditTextId() {
        return R.id.brick_create_light_2d_x;
    }

    @Override
    public int getYEditTextId() {
        return R.id.brick_create_light_2d_y;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory().createLight2DCreateAction(sprite, sequence,
                getFormulaWithBrickField(BrickField.TEXT),
                getFormulaWithBrickField(BrickField.X_POSITION),
                getFormulaWithBrickField(BrickField.Y_POSITION),
                getFormulaWithBrickField(BrickField.VALUE_1),
                getFormulaWithBrickField(BrickField.VALUE_2),
                getFormulaWithBrickField(BrickField.VALUE_3)));
    }
}
