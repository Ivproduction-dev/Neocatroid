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

public class NeoSetPhysicsStateBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;
    private int stateSelection;
    private int shapeSelection;

    public NeoSetPhysicsStateBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_set_physics_state_id);
        addAllowedBrickField(BrickField.PHYSICS_MASS, R.id.brick_neo_set_physics_state_mass_value);
    }

    public NeoSetPhysicsStateBrick(String objectName, int motionType, int shapeType, double mass) {
        this(new Formula(objectName), motionType, shapeType, new Formula(mass));
    }

    public NeoSetPhysicsStateBrick(Formula objectName, int motionType, int shapeType, Formula mass) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        setFormulaWithBrickField(BrickField.PHYSICS_MASS, mass);
        this.stateSelection = motionType;
        this.shapeSelection = shapeType;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_physics_state;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Spinner stateSpinner = view.findViewById(R.id.brick_neo_set_physics_state_spinner);
        Spinner shapeSpinner = view.findViewById(R.id.brick_neo_set_physics_shape_spinner);

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(context,
                R.array.brick_neo_physics_states, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        ArrayAdapter<CharSequence> shapeAdapter = ArrayAdapter.createFromResource(context,
                R.array.brick_neo_physics_shapes, android.R.layout.simple_spinner_item);
        shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shapeSpinner.setAdapter(shapeAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateSelection = position;
                updateVisibility();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        shapeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shapeSelection = position;
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        stateSpinner.setSelection(stateSelection);
        shapeSpinner.setSelection(shapeSelection);
        updateVisibility();

        return view;
    }

    private void updateVisibility() {
        LinearLayout shapeLayout = view.findViewById(R.id.brick_neo_set_physics_shape_layout);
        LinearLayout massLayout = view.findViewById(R.id.brick_neo_set_physics_state_mass_layout);

        shapeLayout.setVisibility(stateSelection == 0 ? View.GONE : View.VISIBLE);
        massLayout.setVisibility(stateSelection == 3 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetPhysicsStateAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        stateSelection,
                        shapeSelection,
                        getFormulaWithBrickField(BrickField.PHYSICS_MASS)));
    }
}
