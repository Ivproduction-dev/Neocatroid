package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class ToggleLight2DBrick extends FormulaBrick {

    private static final long serialVersionUID = 1L;

    public ToggleLight2DBrick() {
        addAllowedBrickField(BrickField.TEXT, R.id.brick_toggle_light_2d_name);
    }

    public ToggleLight2DBrick(String lightName) {
        this();
        setFormulaWithBrickField(BrickField.TEXT, new Formula(lightName));
    }

    public ToggleLight2DBrick(Formula lightName) {
        this();
        setFormulaWithBrickField(BrickField.TEXT, lightName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_toggle_light_2d;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory().createToggleLight2DAction(sprite, sequence,
                getFormulaWithBrickField(BrickField.TEXT)));
    }
}
