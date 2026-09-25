package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoCameraLookAtBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoCameraLookAtBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_camera_look_at_edit_name);
    }

    public NeoCameraLookAtBrick(String objectName) {
        this(new Formula(objectName));
    }

    public NeoCameraLookAtBrick(Formula objectName) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_camera_look_at;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCameraLookAtAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME)));
    }
}
