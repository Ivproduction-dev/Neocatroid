package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class NeoCameraFollowBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    private int followModeSelection;

    public NeoCameraFollowBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_camera_follow_edit_name);
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_neo_camera_follow_edit_x);
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_neo_camera_follow_edit_y);
        addAllowedBrickField(BrickField.Z_POSITION, R.id.brick_neo_camera_follow_edit_z);
    }

    public NeoCameraFollowBrick(String objectName, float x, float y, float z, int followMode) {
        this(new Formula(objectName), new Formula(x), new Formula(y), new Formula(z),
                followMode);
    }

    public NeoCameraFollowBrick(Formula objectName, Formula x, Formula y, Formula z,
            int followMode) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.X_POSITION, x);
        setFormulaWithBrickField(BrickField.Y_POSITION, y);
        setFormulaWithBrickField(BrickField.Z_POSITION, z);
        followModeSelection = followMode;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_camera_follow;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Spinner modeSpinner = view.findViewById(R.id.brick_neo_camera_follow_mode_spinner);
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(context,
                R.array.brick_neo_follow_modes, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position,
                    long id) {
                followModeSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        modeSpinner.setSelection(followModeSelection);
        return view;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCameraFollowAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        getFormulaWithBrickField(BrickField.X_POSITION),
                        getFormulaWithBrickField(BrickField.Y_POSITION),
                        getFormulaWithBrickField(BrickField.Z_POSITION),
                        followModeSelection));
    }
}
