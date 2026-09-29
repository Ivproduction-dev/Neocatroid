package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.List;

public class NeoPlay3DSoundAtCameraBrick extends FormulaBrick {

    private static final long serialVersionUID = 1L;

    private SoundInfo sound;
    private int loopSelection;

    private transient BrickSpinner<SoundInfo> soundSpinner;

    public NeoPlay3DSoundAtCameraBrick() {
        addAllowedBrickField(BrickField.VALUE_1, R.id.brick_neo_play_sound_camera_edit_volume);
        addAllowedBrickField(BrickField.VALUE_2, R.id.brick_neo_play_sound_camera_edit_tone);
        addAllowedBrickField(BrickField.VALUE_3,
                R.id.brick_neo_play_sound_camera_edit_max_distance);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_play_3d_sound_at_camera;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        List<Nameable> items = new ArrayList<>();
        Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
        if (sprite != null) {
            items.addAll(sprite.getSoundList());
        }
        soundSpinner = new BrickSpinner<>(R.id.brick_neo_play_sound_camera_spinner, view, items);
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

        Spinner loopSpinner =
                view.findViewById(R.id.brick_neo_play_sound_camera_loop_spinner);
        ArrayAdapter<CharSequence> loopAdapter = ArrayAdapter.createFromResource(context,
                R.array.brick_neo_loop_modes, android.R.layout.simple_spinner_item);
        loopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loopSpinner.setAdapter(loopAdapter);
        loopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position,
                    long id) {
                loopSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        loopSpinner.setSelection(loopSelection);
        return view;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        com.badlogic.gdx.scenes.scene2d.Action created = sprite.getActionFactory()
                .createNeoPlay3DSoundAction(sprite, sequence, null,
                        getFormulaWithBrickField(BrickField.VALUE_1),
                        getFormulaWithBrickField(BrickField.VALUE_2),
                        getFormulaWithBrickField(BrickField.VALUE_3),
                        loopSelection);
        if (created instanceof org.catrobat.catroid.content.actions.NeoPlay3DSoundAction) {
            org.catrobat.catroid.content.actions.NeoPlay3DSoundAction action =
                    (org.catrobat.catroid.content.actions.NeoPlay3DSoundAction) created;
            action.setSound(sound);
            sequence.addAction(action);
        }
    }

    @Override
    public Brick clone() throws CloneNotSupportedException {
        NeoPlay3DSoundAtCameraBrick clone = (NeoPlay3DSoundAtCameraBrick) super.clone();
        clone.soundSpinner = null;
        return clone;
    }
}
