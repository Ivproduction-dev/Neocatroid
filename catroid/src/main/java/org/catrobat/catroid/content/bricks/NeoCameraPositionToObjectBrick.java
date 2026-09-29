package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoCameraPositionToObjectBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoCameraPositionToObjectBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_camera_position_to_object_edit_name);
    }

    public NeoCameraPositionToObjectBrick(String objectName) {
        this(new Formula(objectName));
    }

    public NeoCameraPositionToObjectBrick(Formula objectName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_camera_position_to_object;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCameraPositionToObjectAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME)));
    }
}
