package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class ShowSpritesByPrefixBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    public ShowSpritesByPrefixBrick() {
        addAllowedBrickField(BrickField.TEXT, R.id.brick_sprites_by_prefix_edit_text);
    }

    public ShowSpritesByPrefixBrick(String prefix) {
        this(new Formula(prefix));
    }

    public ShowSpritesByPrefixBrick(Formula prefix) {
        this();
        setFormulaWithBrickField(BrickField.TEXT, prefix);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_show_sprites_by_prefix;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createSetSpritesVisibleByPrefixAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.TEXT), true));
    }
}
