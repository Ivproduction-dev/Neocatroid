package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoGlideCameraToObjectBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoGlideCameraToObjectBrick() {
        addAllowedBrickField(BrickField.TARGET, R.id.brick_neo_glide_camera_edit_target);
        addAllowedBrickField(BrickField.DURATION_IN_SECONDS,
                R.id.brick_neo_glide_camera_edit_duration);
    }

    public NeoGlideCameraToObjectBrick(String targetName, float seconds) {
        this(new Formula(targetName), new Formula(seconds));
    }

    public NeoGlideCameraToObjectBrick(Formula targetName, Formula seconds) {
        this();
        setFormulaWithBrickField(BrickField.TARGET, targetName);
        setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, seconds);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_glide_camera_to_object;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoGlideCameraAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.TARGET), null, null, null,
                        getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
    }
}
