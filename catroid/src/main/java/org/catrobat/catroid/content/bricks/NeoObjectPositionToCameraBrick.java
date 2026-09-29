package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoObjectPositionToCameraBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoObjectPositionToCameraBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_object_position_to_camera_edit_name);
    }

    public NeoObjectPositionToCameraBrick(String objectName) {
        this(new Formula(objectName));
    }

    public NeoObjectPositionToCameraBrick(Formula objectName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_object_position_to_camera;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoObjectPositionToCameraAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME)));
    }
}
