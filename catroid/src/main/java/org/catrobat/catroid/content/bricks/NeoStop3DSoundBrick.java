package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;

import java.util.ArrayList;
import java.util.List;

public class NeoStop3DSoundBrick extends BrickBaseType {

    private static final long serialVersionUID = 1L;

    private SoundInfo sound;

    private transient BrickSpinner<SoundInfo> soundSpinner;

    public NeoStop3DSoundBrick() {
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_stop_3d_sound;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        List<Nameable> items = new ArrayList<>();
        Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
        if (sprite != null) {
            items.addAll(sprite.getSoundList());
        }
        soundSpinner = new BrickSpinner<>(R.id.brick_neo_stop_sound_spinner, view, items);
        soundSpinner.setOnItemSelectedListener(
                new BrickSpinner.OnItemSelectedListener<SoundInfo>() {
                    @Override
                    public void onNewOptionSelected(Integer spinnerId) {
                    }

                    @Override
                    public void onEditOptionSelected(Integer spinnerId) {
                    }

                    @Override
                    public void onStringOptionSelected(Integer spinnerId, String string) {
                    }

                    @Override
                    public void onItemSelected(Integer spinnerId, SoundInfo item) {
                        sound = item;
                    }
                });
        soundSpinner.setSelection(sound);
        return view;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        com.badlogic.gdx.scenes.scene2d.Action created = sprite.getActionFactory()
                .createNeoStop3DSoundAction(sprite, sequence);
        if (created instanceof org.catrobat.catroid.content.actions.NeoStop3DSoundAction) {
            org.catrobat.catroid.content.actions.NeoStop3DSoundAction action =
                    (org.catrobat.catroid.content.actions.NeoStop3DSoundAction) created;
            action.setSound(sound);
            sequence.addAction(action);
        }
    }

    @Override
    public Brick clone() throws CloneNotSupportedException {
        NeoStop3DSoundBrick clone = (NeoStop3DSoundBrick) super.clone();
        clone.soundSpinner = null;
        return clone;
    }
}
