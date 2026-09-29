package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNeo3DTouchScript;
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
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WhenNeo3DTouchBrick extends FormulaBrick implements ScriptBrick,
        BrickSpinner.OnItemSelectedListener<UserVariable> {

    private static final long serialVersionUID = 1L;

    private WhenNeo3DTouchScript script;
    private UserVariable userVariable;

    private transient BrickSpinner<UserVariable> spinner;

    public WhenNeo3DTouchBrick() {
        this(new WhenNeo3DTouchScript());
    }

    public WhenNeo3DTouchBrick(WhenNeo3DTouchScript script) {
        addAllowedBrickField(BrickField.NAME, R.id.brick_when_neo3d_touch_edit_name);
        addAllowedBrickField(BrickField.DISTANCE, R.id.brick_when_neo3d_touch_edit_distance);
        script.setScriptBrick(this);
        commentedOut = script.isCommentedOut();
        this.script = script;

        formulaMap = script.getFormulaMap();
        userVariable = script.getTouchedVariable();
    }

    @Override
    public Brick clone() throws CloneNotSupportedException {
        WhenNeo3DTouchBrick clone = (WhenNeo3DTouchBrick) super.clone();
        clone.script = (WhenNeo3DTouchScript) script.clone();
        clone.script.setScriptBrick(clone);
        clone.formulaMap = clone.script.getFormulaMap();
        clone.userVariable = clone.script.getTouchedVariable();
        clone.spinner = null;
        return clone;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_when_neo3d_touch;
    }

    public Formula getObjectNameFormula() {
        return getFormulaWithBrickField(BrickField.NAME);
    }

    public Formula getDistanceFormula() {
        return getFormulaWithBrickField(BrickField.DISTANCE);
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

        spinner = new BrickSpinner<>(R.id.brick_when_neo3d_touch_spinner, view, items);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(userVariable);
        return view;
    }

    @Override
    public void onNewOptionSelected(Integer spinnerId) {
        AppCompatActivity activity = UiUtils.getActivityFromView(view);
        if (activity == null) {
            return;
        }
        UserVariableBrickTextInputDialogBuilder builder =
                new UserVariableBrickTextInputDialogBuilder(
                        ProjectManager.getInstance().getCurrentProject(),
                        ProjectManager.getInstance().getCurrentSprite(),
                        userVariable, activity, spinner);
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
        if (userVariable != null && userVariable.isLocked()) {
            AppCompatActivity activity = UiUtils.getActivityFromView(view);
            if (activity != null) {
                final UserVariable previous = userVariable;
                LockUtils.requestPassword(activity, R.string.variable_locked_enter_password,
                        password -> {
                            if (previous.verifyLock(password)) {
                                setVariable(item);
                            } else {
                                ToastUtil.showError(activity, R.string.brick_wrong_password);
                                if (spinner != null) {
                                    spinner.setSelection(previous);
                                }
                            }
                        });
            }
            return;
        }
        setVariable(item);
    }

    private void setVariable(UserVariable item) {
        userVariable = item;
        script.setTouchedVariable(item);
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
