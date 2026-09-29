package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoCopyObjectPositionBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoCopyObjectPositionBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_copy_object_position_edit_name);
        addAllowedBrickField(BrickField.TARGET, R.id.brick_neo_copy_object_position_edit_source);
    }

    public NeoCopyObjectPositionBrick(String objectName, String sourceName) {
        this(new Formula(objectName), new Formula(sourceName));
    }

    public NeoCopyObjectPositionBrick(Formula objectName, Formula sourceName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.TARGET, sourceName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_copy_object_position;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCopyObjectPositionAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.TARGET)));
    }
}
