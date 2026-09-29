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

public class NeoSetPhysicsCollisionBrick extends FormulaBrick {
    private static final long serialVersionUID = 1L;

    private int collisionModeSelection;

    public NeoSetPhysicsCollisionBrick() {
        addAllowedBrickField(BrickField.NAME, R.id.brick_neo_set_physics_collision_edit_name);
    }

    public NeoSetPhysicsCollisionBrick(String objectName, int collisionMode) {
        this(new Formula(objectName), collisionMode);
    }

    public NeoSetPhysicsCollisionBrick(Formula objectName, int collisionMode) {
        this();
        setFormulaWithBrickField(BrickField.NAME, objectName);
        collisionModeSelection = collisionMode;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_neo_set_physics_collision;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Spinner modeSpinner = view.findViewById(R.id.brick_neo_set_physics_collision_spinner);
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(context,
                R.array.brick_neo_collision_modes, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position,
                    long id) {
                collisionModeSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        modeSpinner.setSelection(collisionModeSelection);
        return view;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createNeoSetPhysicsCollisionAction(sprite, sequence,
                        getFormulaWithBrickField(BrickField.NAME),
                        collisionModeSelection));
    }
}
