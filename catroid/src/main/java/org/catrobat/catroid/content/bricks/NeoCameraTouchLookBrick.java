package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.neo3d.Neo3DEngine;

public class NeoCameraTouchLookBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    private int modeSelection = Neo3DEngine.CameraTouchMode.FIRST_PERSON.ordinal();

    public NeoCameraTouchLookBrick() {
        addAllowedBrickField(BrickField.VALUE_1, R.id.brick_neo_camera_touch_look_edit_sensitivity);
        addAllowedBrickField(BrickField.VALUE_2, R.id.brick_neo_camera_touch_look_edit_min_pitch);
        addAllowedBrickField(BrickField.VALUE_3, R.id.brick_neo_camera_touch_look_edit_max_pitch);
    }

    public NeoCameraTouchLookBrick(int mode, float sensitivity, float minPitch, float maxPitch) {
        this(new Formula(sensitivity), new Formula(minPitch), new Formula(maxPitch), mode);
    }

    public NeoCameraTouchLookBrick(Formula sensitivity, Formula minPitch, Formula maxPitch,
            int mode) {
        this();
        setFormulaWithBrickField(BrickField.VALUE_1, sensitivity);
        setFormulaWithBrickField(BrickField.VALUE_2, minPitch);
        setFormulaWithBrickField(BrickField.VALUE_3, maxPitch);
        modeSelection = mode;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_camera_touch_look;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Spinner modeSpinner = view.findViewById(R.id.brick_neo_camera_touch_look_mode_spinner);
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(context,
                R.array.brick_neo_camera_touch_modes, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position,
                    long id) {
                modeSelection = position;
                updatePitchLimitVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        modeSpinner.setSelection(modeSelection);
        updatePitchLimitVisibility();
        return view;
    }

    private void updatePitchLimitVisibility() {
        LinearLayout limitsLayout = view.findViewById(
                R.id.brick_neo_camera_touch_look_limits_layout);
        limitsLayout.setVisibility(modeSelection
                == Neo3DEngine.CameraTouchMode.FIRST_PERSON.ordinal()
                ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoCameraTouchLookAction(sprite, sequence, modeSelection,
                        getFormulaWithBrickField(BrickField.VALUE_1),
                        getFormulaWithBrickField(BrickField.VALUE_2),
                        getFormulaWithBrickField(BrickField.VALUE_3)));
    }
}
