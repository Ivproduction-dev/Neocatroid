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

import java.util.ArrayList;
import java.util.List;

public class ShadowCasting2DBrick extends FormulaBrick
        implements BrickSpinner.OnItemSelectedListener<Sprite> {

    private static final long serialVersionUID = 1L;

    private String targetSpriteName = "";
    private int castSelection = 0;

    private transient BrickSpinner<Sprite> spriteSpinner;

    public ShadowCasting2DBrick() {
    }

    public ShadowCasting2DBrick(String targetSpriteName, int castSelection) {
        this.targetSpriteName = targetSpriteName;
        this.castSelection = castSelection;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_shadow_casting_2d;
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        List<Nameable> items = new ArrayList<>();
        if (ProjectManager.getInstance().getCurrentlyEditedScene() != null) {
            items.addAll(ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList());
        }
        spriteSpinner = new BrickSpinner<>(R.id.brick_shadow_casting_2d_sprite_spinner, view, items);
        spriteSpinner.setOnItemSelectedListener(this);
        spriteSpinner.setSelection(targetSpriteName);

        Spinner modeSpinner = view.findViewById(R.id.brick_shadow_casting_2d_mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.light2d_shadow_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                castSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        modeSpinner.setSelection(castSelection);

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
        targetSpriteName = "";
    }

    @Override
    public void onItemSelected(Integer spinnerId, Sprite item) {
        targetSpriteName = item != null ? item.getName() : "";
    }

    @Override
    public Brick clone() throws CloneNotSupportedException {
        ShadowCasting2DBrick clone = (ShadowCasting2DBrick) super.clone();
        clone.spriteSpinner = null;
        return clone;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory().createShadowCasting2DAction(sprite, sequence,
                targetSpriteName, castSelection));
    }
}
