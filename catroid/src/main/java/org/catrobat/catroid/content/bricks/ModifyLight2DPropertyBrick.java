package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class ModifyLight2DPropertyBrick extends FormulaBrick {

    private static final long serialVersionUID = 1L;

    private int propertySelection = 0;

    public ModifyLight2DPropertyBrick() {
        addAllowedBrickField(BrickField.TEXT, R.id.brick_modify_light_2d_name);
        addAllowedBrickField(BrickField.OBJECT_NAME, R.id.brick_modify_light_2d_object);
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_modify_light_2d_x);
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_modify_light_2d_y);
        addAllowedBrickField(BrickField.VALUE, R.id.brick_modify_light_2d_value);
    }

    public ModifyLight2DPropertyBrick(String lightName, int propertySelection, String objectName, String x, String y, String value) {
        this();
        setFormulaWithBrickField(BrickField.TEXT, new Formula(lightName));
        setFormulaWithBrickField(BrickField.OBJECT_NAME, new Formula(objectName));
        this.propertySelection = propertySelection;
        setFormulaWithBrickField(BrickField.X_POSITION, new Formula(x));
        setFormulaWithBrickField(BrickField.Y_POSITION, new Formula(y));
        setFormulaWithBrickField(BrickField.VALUE, new Formula(value));
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_modify_light_2d_property;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Spinner spinner = view.findViewById(R.id.brick_modify_light_2d_property_spinner);
        TextView labelX = view.findViewById(R.id.brick_modify_light_2d_label_x);
        TextView editX = view.findViewById(R.id.brick_modify_light_2d_x);
        TextView labelY = view.findViewById(R.id.brick_modify_light_2d_label_y);
        TextView editY = view.findViewById(R.id.brick_modify_light_2d_y);
        TextView labelValue = view.findViewById(R.id.brick_modify_light_2d_label_value);
        TextView editValue = view.findViewById(R.id.brick_modify_light_2d_value);

        Runnable updateVisibility = new Runnable() {
            @Override
            public void run() {
                boolean isPosition = propertySelection == 0;
                int visPos = isPosition ? View.VISIBLE : View.GONE;
                int visVal = isPosition ? View.GONE : View.VISIBLE;
                if (labelX != null) labelX.setVisibility(visPos);
                if (editX != null) editX.setVisibility(visPos);
                if (labelY != null) labelY.setVisibility(visPos);
                if (editY != null) editY.setVisibility(visPos);
                if (labelValue != null) labelValue.setVisibility(visVal);
                if (editValue != null) editValue.setVisibility(visVal);
                if (labelValue != null) {
                    switch (propertySelection) {
                        case 1:
                            labelValue.setText("Radius:");
                            break;
                        case 2:
                            labelValue.setText("Intensity:");
                            break;
                        case 3:
                            labelValue.setText("Color:");
                            break;
                        case 4:
                            labelValue.setText("Ambient:");
                            break;
                        default:
                            labelValue.setText(context.getString(R.string.brick_light_2d_control_value));
                            break;
                    }
                }
            }
        };

        if (spinner != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.light2d_modify_properties, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    propertySelection = position;
                    updateVisibility.run();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spinner.setSelection(propertySelection);
        }
        updateVisibility.run();
        return view;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory().createModifyLight2DPropertyAction(sprite, sequence,
                getFormulaWithBrickField(BrickField.TEXT),
                getFormulaWithBrickField(BrickField.OBJECT_NAME),
                propertySelection,
                getFormulaWithBrickField(BrickField.X_POSITION),
                getFormulaWithBrickField(BrickField.Y_POSITION),
                getFormulaWithBrickField(BrickField.VALUE)));
    }
}
