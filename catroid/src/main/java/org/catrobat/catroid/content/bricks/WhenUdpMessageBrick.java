package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenUdpMessageScript;
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

public class WhenUdpMessageBrick extends FormulaBrick implements ScriptBrick, UserVariableBrickInterface,
		BrickSpinner.OnItemSelectedListener<UserVariable> {

	private static final long serialVersionUID = 1L;

	public static final int ROW_COUNT = 12;

	private UserVariable userVariable;
	private List<UserVariable> extraVariables = new ArrayList<>();
	private boolean[] rowVisible;
	private WhenUdpMessageScript script;

	private transient Map<Integer, BrickSpinner<UserVariable>> spinners = new HashMap<>();
	private transient Map<Integer, Integer> spinnerSlots = new HashMap<>();

	public WhenUdpMessageBrick() {
		this(new WhenUdpMessageScript());
	}

	public WhenUdpMessageBrick(WhenUdpMessageScript script) {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_when_udp_message_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
		script.setScriptBrick(this);
		commentedOut = script.isCommentedOut();
		this.script = script;
		rowVisible = newRowVisible();
	}

	public WhenUdpMessageBrick(String serverName) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, new Formula(serverName));
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		WhenUdpMessageBrick clone = (WhenUdpMessageBrick) super.clone();
		clone.script = (WhenUdpMessageScript) script.clone();
		clone.script.setScriptBrick(clone);
		clone.spinners = new HashMap<>();
		clone.spinnerSlots = new HashMap<>();
		return clone;
	}

	public UserVariable getVariable(int index) {
		if (index == 0) {
			return userVariable;
		}
		if (extraVariables == null) {
			extraVariables = new ArrayList<>();
		}
		int extraIndex = index - 1;
		if (extraIndex < extraVariables.size()) {
			return extraVariables.get(extraIndex);
		}
		return null;
	}

	public void setVariable(int index, UserVariable variable) {
		if (index == 0) {
			userVariable = variable;
			return;
		}
		if (extraVariables == null) {
			extraVariables = new ArrayList<>();
		}
		int extraIndex = index - 1;
		while (extraVariables.size() <= extraIndex) {
			extraVariables.add(null);
		}
		extraVariables.set(extraIndex, variable);
	}

	public boolean[] getRowVisible() {
		if (rowVisible == null) {
			rowVisible = newRowVisible();
		}
		return rowVisible;
	}

	public void setRowVisible(boolean[] rowVisible) {
		this.rowVisible = rowVisible;
	}

	private boolean[] newRowVisible() {
		boolean[] rows = new boolean[ROW_COUNT];
		rows[0] = true;
		return rows;
	}

	private int firstHiddenRow() {
		boolean[] rows = getRowVisible();
		for (int i = 0; i < rows.length; i++) {
			if (!rows[i]) {
				return i;
			}
		}
		return -1;
	}

	public List<UserVariable> getVariables() {
		List<UserVariable> variables = new ArrayList<>();
		boolean[] rows = getRowVisible();
		for (int i = 0; i < ROW_COUNT; i++) {
			if (rows[i]) {
				variables.add(getVariable(i));
			}
		}
		return variables;
	}

	public String resolveServerName(Sprite sprite) {
		Formula name = getFormulaWithBrickField(BrickField.TEXT);
		if (name == null) {
			return "";
		}
		try {
			String value = name.interpretString(new Scope(
					ProjectManager.getInstance().getCurrentProject(), sprite, null));
			return org.catrobat.catroid.content.LocalServer.normServerKey(value);
		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_when_udp_message;
	}

	@Override
	public View getView(Context context) {
		View view = super.getView(context);

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

		int[] rowIds = {
				R.id.brick_when_udp_message_row, R.id.brick_when_udp_message_row2,
				R.id.brick_when_udp_message_row3, R.id.brick_when_udp_message_row4,
				R.id.brick_when_udp_message_row5, R.id.brick_when_udp_message_row6,
				R.id.brick_when_udp_message_row7, R.id.brick_when_udp_message_row8,
				R.id.brick_when_udp_message_row9, R.id.brick_when_udp_message_row10,
				R.id.brick_when_udp_message_row11, R.id.brick_when_udp_message_row12
		};
		int[] spinnerIds = {
				R.id.brick_when_udp_message_spinner, R.id.brick_when_udp_message_spinner2,
				R.id.brick_when_udp_message_spinner3, R.id.brick_when_udp_message_spinner4,
				R.id.brick_when_udp_message_spinner5, R.id.brick_when_udp_message_spinner6,
				R.id.brick_when_udp_message_spinner7, R.id.brick_when_udp_message_spinner8,
				R.id.brick_when_udp_message_spinner9, R.id.brick_when_udp_message_spinner10,
				R.id.brick_when_udp_message_spinner11, R.id.brick_when_udp_message_spinner12
		};
		int[] minusIds = {
				R.id.brick_when_udp_message_minus, R.id.brick_when_udp_message_minus2,
				R.id.brick_when_udp_message_minus3, R.id.brick_when_udp_message_minus4,
				R.id.brick_when_udp_message_minus5, R.id.brick_when_udp_message_minus6,
				R.id.brick_when_udp_message_minus7, R.id.brick_when_udp_message_minus8,
				R.id.brick_when_udp_message_minus9, R.id.brick_when_udp_message_minus10,
				R.id.brick_when_udp_message_minus11, R.id.brick_when_udp_message_minus12
		};

		boolean[] rows = getRowVisible();
		spinners.clear();
		spinnerSlots.clear();
		for (int i = 0; i < spinnerIds.length; i++) {
			View rowView = view.findViewById(rowIds[i]);
			if (rowView != null) {
				rowView.setVisibility(rows[i] ? View.VISIBLE : View.GONE);
			}
			if (rows[i]) {
				BrickSpinner<UserVariable> spinner = new BrickSpinner<>(spinnerIds[i], view, items);
				spinner.setOnItemSelectedListener(this);
				spinner.setSelection(getVariable(i));
				spinners.put(spinnerIds[i], spinner);
				spinnerSlots.put(spinnerIds[i], i);
			}
			final int index = i;
			View minusButton = view.findViewById(minusIds[i]);
			if (minusButton != null) {
				minusButton.setOnClickListener(click -> {
					rows[index] = false;
					applyRowVisibility(view, rowIds, rows);
					ElseIfSeparatorBrick.refreshScriptList(view);
				});
			}
		}

		View addButton = view.findViewById(R.id.brick_when_udp_message_add);
		if (addButton != null) {
			addButton.setOnClickListener(click -> {
				int hidden = firstHiddenRow();
				if (hidden >= 0) {
					rows[hidden] = true;
					applyRowVisibility(view, rowIds, rows);
					BrickSpinner<UserVariable> spinner = new BrickSpinner<>(spinnerIds[hidden], view, items);
					spinner.setOnItemSelectedListener(WhenUdpMessageBrick.this);
					spinner.setSelection(getVariable(hidden));
					spinners.put(spinnerIds[hidden], spinner);
					spinnerSlots.put(spinnerIds[hidden], hidden);
					ElseIfSeparatorBrick.refreshScriptList(view);
				}
			});
			applyRowVisibility(view, rowIds, rows);
		}

		return view;
	}

	private void applyRowVisibility(View view, int[] rowIds, boolean[] rows) {
		for (int i = 0; i < rowIds.length; i++) {
			View rowView = view.findViewById(rowIds[i]);
			if (rowView != null) {
				rowView.setVisibility(rows[i] ? View.VISIBLE : View.GONE);
			}
		}
		View addButton = view.findViewById(R.id.brick_when_udp_message_add);
		if (addButton != null) {
			addButton.setVisibility(firstHiddenRow() >= 0 ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}
		BrickSpinner<UserVariable> spinner = spinners.get(spinnerId);
		int slot = spinnerSlots.containsKey(spinnerId) ? spinnerSlots.get(spinnerId) : 0;
		UserVariable current = getVariable(slot);
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserVariableBrickTextInputDialogBuilder builder =
				new UserVariableBrickTextInputDialogBuilder(currentProject, currentSprite, current, activity, spinner);
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
		UserVariable previous = getVariable(slot);
		if (previous != null && previous.isLocked()) {
			AppCompatActivity activity = UiUtils.getActivityFromView(view);
			if (activity != null) {
				LockUtils.requestPassword(activity, R.string.variable_locked_enter_password, password -> {
					if (previous.verifyLock(password)) {
						setVariable(slot, item);
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
		setVariable(slot, item);
	}

	@Override
	public UserVariable getUserVariable() {
		return userVariable;
	}

	@Override
	public void setUserVariable(UserVariable userVariable) {
		this.userVariable = userVariable;
	}

	public WhenUdpMessageScript getWhenUdpMessageScript() {
		return script;
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
