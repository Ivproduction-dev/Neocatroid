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

public class CreateLight2DBrick extends VisualPlacementBrick {

    private static final long serialVersionUID = 1L;

    private int typeSelection = 0;

    public CreateLight2DBrick() {
        addAllowedBrickField(BrickField.TEXT, R.id.brick_create_light_2d_name);
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_create_light_2d_x);
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_create_light_2d_y);
        addAllowedBrickField(BrickField.VALUE_1, R.id.brick_create_light_2d_intensity);
        addAllowedBrickField(BrickField.VALUE_2, R.id.brick_create_light_2d_radius);
    }

    public CreateLight2DBrick(String name, double x, double y, int typeSelection, double intensity,
            double radius) {
        this();
        setFormulaWithBrickField(BrickField.TEXT, new Formula(name));
        setFormulaWithBrickField(BrickField.X_POSITION, new Formula(x));
        setFormulaWithBrickField(BrickField.Y_POSITION, new Formula(y));
        this.typeSelection = typeSelection;
        setFormulaWithBrickField(BrickField.VALUE_1, new Formula(intensity));
        setFormulaWithBrickField(BrickField.VALUE_2, new Formula(radius));
    }

    public CreateLight2DBrick(String name, double x, double y, double radius, double intensity,
            int color) {
        this(name, x, y, 0, intensity, radius);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_create_light_2d;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Spinner typeSpinner = view.findViewById(R.id.brick_create_light_2d_type_spinner);
        if (typeSpinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.light2d_types, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(adapter);
            typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    typeSelection = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            typeSpinner.setSelection(typeSelection);
        }

        return view;
    }

    @Override
    public BrickField getXBrickField() {
        return BrickField.X_POSITION;
    }

    @Override
    public BrickField getYBrickField() {
        return BrickField.Y_POSITION;
    }

    @Override
    public int getXEditTextId() {
        return R.id.brick_create_light_2d_x;
    }

    @Override
    public int getYEditTextId() {
        return R.id.brick_create_light_2d_y;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory().createLight2DCreateAction(sprite, sequence,
                getFormulaWithBrickField(BrickField.TEXT),
                getFormulaWithBrickField(BrickField.X_POSITION),
                getFormulaWithBrickField(BrickField.Y_POSITION),
                typeSelection,
                getFormulaWithBrickField(BrickField.VALUE_1),
                getFormulaWithBrickField(BrickField.VALUE_2)));
    }
}
