package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNeo3DRayHitScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.content.bricks.brickspinner.UserVariableBrickTextInputDialogBuilder;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.utils.LockUtils;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WhenNeo3DRayHitBrick extends FormulaBrick implements ScriptBrick,
        BrickSpinner.OnItemSelectedListener<UserVariable> {

    private static final long serialVersionUID = 1L;

    private static final int MAX_HIT_SLOTS = 4;

    private WhenNeo3DRayHitScript script;

    private transient Map<Integer, BrickSpinner<UserVariable>> spinners = new HashMap<>();
    private transient Map<Integer, Integer> spinnerSlots = new HashMap<>();

    public WhenNeo3DRayHitBrick() {
        this(new WhenNeo3DRayHitScript());
    }

    public WhenNeo3DRayHitBrick(WhenNeo3DRayHitScript script) {
        addAllowedBrickField(BrickField.NAME, R.id.brick_when_neo3d_ray_hit_edit_name);
        script.setScriptBrick(this);
        commentedOut = script.isCommentedOut();
        this.script = script;

        formulaMap = script.getFormulaMap();
    }

    @Override
    public Brick clone() throws CloneNotSupportedException {
        WhenNeo3DRayHitBrick clone = (WhenNeo3DRayHitBrick) super.clone();
        clone.script = (WhenNeo3DRayHitScript) script.clone();
        clone.script.setScriptBrick(clone);
        clone.formulaMap = clone.script.getFormulaMap();
        clone.spinners = new HashMap<>();
        clone.spinnerSlots = new HashMap<>();
        return clone;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_when_neo3d_ray_hit;
    }

    public Formula getRayNameFormula() {
        return getFormulaWithBrickField(BrickField.NAME);
    }

    @Override
    public View getView(Context context) {
        super.getView(context);

        Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
        List<Nameable> items = new ArrayList<>();
        items.add(new NewOption(context.getString(R.string.new_option)));
        if (sprite != null) {
            items.addAll(sprite.getUserVariables());
        }
        Project project = ProjectManager.getInstance().getCurrentProject();
        if (project != null) {
            items.addAll(project.getUserVariables());
            items.addAll(project.getMultiplayerVariables());
        }

        int[] spinnerIds = {
                R.id.brick_when_neo3d_ray_hit_spinner1,
                R.id.brick_when_neo3d_ray_hit_spinner2,
                R.id.brick_when_neo3d_ray_hit_spinner3,
                R.id.brick_when_neo3d_ray_hit_spinner4
        };
        for (int i = 0; i < spinnerIds.length; i++) {
            View spinnerView = view.findViewById(spinnerIds[i]);
            if (spinnerView == null) {
                continue;
            }
            if (i < script.getVisibleHits()) {
                spinnerView.setVisibility(View.VISIBLE);
                BrickSpinner<UserVariable> spinner =
                        new BrickSpinner<>(spinnerIds[i], view, items);
                spinner.setOnItemSelectedListener(this);
                spinner.setSelection(script.getVariable(i));
                spinners.put(spinnerIds[i], spinner);
                spinnerSlots.put(spinnerIds[i], i);
            } else {
                spinnerView.setVisibility(View.GONE);
            }
        }

        View addButton = view.findViewById(R.id.brick_when_neo3d_ray_hit_add);
        if (addButton != null) {
            addButton.setOnClickListener(click -> {
                int visible = script.getVisibleHits();
                if (visible < MAX_HIT_SLOTS) {
                    script.setVisibleHits(visible + 1);
                    View nextView = view.findViewById(spinnerIds[visible]);
                    if (nextView != null) {
                        nextView.setVisibility(View.VISIBLE);
                        BrickSpinner<UserVariable> spinner =
                                new BrickSpinner<>(spinnerIds[visible], view, items);
                        spinner.setOnItemSelectedListener(this);
                        spinner.setSelection(script.getVariable(visible));
                        spinners.put(spinnerIds[visible], spinner);
                        spinnerSlots.put(spinnerIds[visible], visible);
                    }
                    if (script.getVisibleHits() >= MAX_HIT_SLOTS) {
                        click.setVisibility(View.GONE);
                    }
                }
            });
            addButton.setVisibility(
                    script.getVisibleHits() >= MAX_HIT_SLOTS ? View.GONE : View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onNewOptionSelected(Integer spinnerId) {
        AppCompatActivity activity = UiUtils.getActivityFromView(view);
        if (activity == null) {
            return;
        }
        BrickSpinner<UserVariable> spinner = spinners.get(spinnerId);
        int slot = spinnerSlots.containsKey(spinnerId) ? spinnerSlots.get(spinnerId) : 0;
        UserVariableBrickTextInputDialogBuilder builder =
                new UserVariableBrickTextInputDialogBuilder(
                        ProjectManager.getInstance().getCurrentProject(),
                        ProjectManager.getInstance().getCurrentSprite(),
                        script.getVariable(slot), activity, spinner);
        builder.show();
    }

    @Override
    public void onEditOptionSelected(Integer spinnerId) {
    }

    @Override
    public void onStringOptionSelected(Integer spinnerId, String string) {
    }

    @Override
    public void onItemSelected(Integer spinnerId, @Nullable UserVariable item) {
        int slot = spinnerSlots.containsKey(spinnerId) ? spinnerSlots.get(spinnerId) : 0;
        UserVariable previous = script.getVariable(slot);
        if (previous != null && previous.isLocked()) {
            AppCompatActivity activity = UiUtils.getActivityFromView(view);
            if (activity != null) {
                LockUtils.requestPassword(activity, R.string.variable_locked_enter_password,
                        password -> {
                            if (previous.verifyLock(password)) {
                                script.setVariable(slot, item);
                            } else {
                                ToastUtil.showError(activity, R.string.brick_wrong_password);
                                BrickSpinner<UserVariable> spinner = spinners.get(spinnerId);
                                if (spinner != null) {
                                    spinner.setSelection(previous);
                                }
                            }
                        });
            }
            return;
        }
        script.setVariable(slot, item);
    }

    @Override
    public Script getScript() {
        return script;
    }

    @Override
    public int getPositionInScript() {
        return -1;
    }

    @Override
    public void addToFlatList(List<Brick> bricks) {
        super.addToFlatList(bricks);
        for (Brick brick : getScript().getBrickList()) {
            brick.addToFlatList(bricks);
        }
    }

    @Override
    public List<Brick> getDragAndDropTargetList() {
        return getScript().getBrickList();
    }

    @Override
    public int getPositionInDragAndDropTargetList() {
        return -1;
    }

    @Override
    public void setCommentedOut(boolean commentedOut) {
        super.setCommentedOut(commentedOut);
        getScript().setCommentedOut(commentedOut);
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
    }
}
