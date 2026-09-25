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

public class NeoSetObjectVisibleBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    private int visibleModeSelection;

    public NeoSetObjectVisibleBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_set_object_visible_edit_name);
    }

    public NeoSetObjectVisibleBrick(String objectName, int visibleMode) {
        this(new Formula(objectName), visibleMode);
    }

    public NeoSetObjectVisibleBrick(Formula objectName, int visibleMode) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        visibleModeSelection = visibleMode;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_object_visible;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Spinner modeSpinner = view.findViewById(R.id.brick_neo_set_object_visible_mode_spinner);
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(context,
                R.array.brick_neo_visibility_modes, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position,
                    long id) {
                visibleModeSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        modeSpinner.setSelection(visibleModeSelection);
        return view;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetObjectVisibleAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        visibleModeSelection));
    }
}
