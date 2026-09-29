package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoGlideObjectBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public NeoGlideObjectBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_glide_object_edit_name);
        addAllowedBrickField(BrickField.TARGET, R.id.brick_neo_glide_object_edit_target);
        addAllowedBrickField(BrickField.DURATION_IN_SECONDS,
                R.id.brick_neo_glide_object_edit_duration);
    }

    public NeoGlideObjectBrick(String objectName, String targetName, float seconds) {
        this(new Formula(objectName), new Formula(targetName), new Formula(seconds));
    }

    public NeoGlideObjectBrick(Formula objectName, Formula targetName, Formula seconds) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.TARGET, targetName);
        setFormulaWithBrickField(BrickField.DURATION_IN_SECONDS, seconds);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_glide_object;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoGlideObjectAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.TARGET),
                        getFormulaWithBrickField(BrickField.DURATION_IN_SECONDS)));
    }
}
