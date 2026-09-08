package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.List;

public class Light2DControlBrick extends FormulaBrick
        implements BrickSpinner.OnItemSelectedListener<Sprite> {

    private static final long serialVersionUID = 1L;

    private int actionSelection = 0;
    private String attachSpriteName = "";

    private transient BrickSpinner<Sprite> spriteSpinner;

    public Light2DControlBrick() {
        addAllowedBrickField(BrickField.TEXT, R.id.brick_light_2d_control_name);
        addAllowedBrickField(BrickField.VALUE_1, R.id.brick_light_2d_control_value);
    }

    public Light2DControlBrick(String lightName, int actionSelection, String value,
            String attachSpriteName) {
        this();
        setFormulaWithBrickField(BrickField.TEXT, new Formula(lightName));
        setFormulaWithBrickField(BrickField.VALUE_1, new Formula(value));
        this.actionSelection = actionSelection;
        this.attachSpriteName = attachSpriteName;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_light_2d_control;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Spinner actionSpinner = view.findViewById(R.id.brick_light_2d_control_action_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.light2d_control_actions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(adapter);
        actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                actionSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        actionSpinner.setSelection(actionSelection);

        List<Nameable> items = new ArrayList<>();
        if (ProjectManager.getInstance().getCurrentlyEditedScene() != null) {
            items.addAll(ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList());
        }
        spriteSpinner = new BrickSpinner<>(R.id.brick_light_2d_control_sprite_spinner, view, items);
        spriteSpinner.setOnItemSelectedListener(this);
        spriteSpinner.setSelection(attachSpriteName);

        return view;
    }

    @Override
    public void onNewOptionSelected(Integer spinnerId) {
    }

    @Override
    public void onEditOptionSelected(Integer spinnerId) {
    }

    @Override
    public void onStringOptionSelected(Integer spinnerId, String string) {
        attachSpriteName = "";
    }

    @Override
    public void onItemSelected(Integer spinnerId, Sprite item) {
        attachSpriteName = item != null ? item.getName() : "";
    }

    @Override
    public Brick clone() throws CloneNotSupportedException {
        Light2DControlBrick clone = (Light2DControlBrick) super.clone();
        clone.spriteSpinner = null;
        return clone;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory().createLight2DControlAction(sprite, sequence,
                getFormulaWithBrickField(BrickField.TEXT),
                actionSelection,
                getFormulaWithBrickField(BrickField.VALUE_1),
                attachSpriteName));
    }
}
