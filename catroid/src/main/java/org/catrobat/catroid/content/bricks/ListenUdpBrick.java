package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
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

public class ListenUdpBrick extends FormulaBrick implements UserVariableBrickInterface {

	private static final long serialVersionUID = 1L;

	public static final int ROW_COUNT = 12;

	private UserVariable userVariable;
	private List<UserVariable> extraVariables = new ArrayList<>();
	private int visibleVariables = 1;
	private int listenModeSelection;
	private boolean[] rowVisible;

	private transient Map<Integer, BrickSpinner<UserVariable>> spinners = new HashMap<>();
	private transient Map<Integer, Integer> spinnerSlots = new HashMap<>();

	public ListenUdpBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.listen_udp_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
		rowVisible = newRowVisible();
	}

	public ListenUdpBrick(UserVariable userVariable) {
		this();
		this.userVariable = userVariable;
	}

	@Override
	public UserVariable getUserVariable() {
		return userVariable;
	}

	@Override
	public void setUserVariable(UserVariable userVariable) {
		this.userVariable = userVariable;
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

	public int getVisibleVariables() {
		return visibleVariables;
	}

	public void setVisibleVariables(int visibleVariables) {
		this.visibleVariables = visibleVariables;
	}

	public int getListenModeSelection() {
		return listenModeSelection;
	}

	public void setListenModeSelection(int listenModeSelection) {
		this.listenModeSelection = listenModeSelection;
	}

	public boolean[] getRowVisible() {
		if (rowVisible == null) {
			rowVisible = newRowVisible();
			int count = Math.max(1, Math.min(ROW_COUNT, visibleVariables));
			for (int i = 0; i < count; i++) {
				rowVisible[i] = true;
			}
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

	private void syncVisibleVariables() {
		int count = 0;
		for (boolean visible : getRowVisible()) {
			if (visible) {
				count++;
			}
		}
		visibleVariables = count;
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

	@Override
	public View getView(Context context) {
		View view = super.getView(context);

		android.widget.Spinner modeSpinner = view.findViewById(R.id.listen_udp_mode_spinner);
		if (modeSpinner != null) {
			android.widget.ArrayAdapter<CharSequence> modeAdapter =
					android.widget.ArrayAdapter.createFromResource(context,
							R.array.listen_tcp_modes, android.R.layout.simple_spinner_item);
			modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			modeSpinner.setAdapter(modeAdapter);
			modeSpinner.setOnItemSelectedListener(
					new android.widget.AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(android.widget.AdapterView<?> parent,
								View selectedView, int position, long id) {
							listenModeSelection = position;
						}

						@Override
						public void onNothingSelected(android.widget.AdapterView<?> parent) {
						}
					});
			modeSpinner.setSelection(listenModeSelection);
		}

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
				R.id.listen_udp_row, R.id.listen_udp_row2, R.id.listen_udp_row3,
				R.id.listen_udp_row4, R.id.listen_udp_row5, R.id.listen_udp_row6,
				R.id.listen_udp_row7, R.id.listen_udp_row8, R.id.listen_udp_row9,
				R.id.listen_udp_row10, R.id.listen_udp_row11, R.id.listen_udp_row12
		};
		int[] spinnerIds = {
				R.id.listen_udp_spinner1, R.id.listen_udp_spinner2, R.id.listen_udp_spinner3,
				R.id.listen_udp_spinner4, R.id.listen_udp_spinner5, R.id.listen_udp_spinner6,
				R.id.listen_udp_spinner7, R.id.listen_udp_spinner8, R.id.listen_udp_spinner9,
				R.id.listen_udp_spinner10, R.id.listen_udp_spinner11, R.id.listen_udp_spinner12
		};
		int[] minusIds = {
				R.id.listen_udp_minus, R.id.listen_udp_minus2, R.id.listen_udp_minus3,
				R.id.listen_udp_minus4, R.id.listen_udp_minus5, R.id.listen_udp_minus6,
				R.id.listen_udp_minus7, R.id.listen_udp_minus8, R.id.listen_udp_minus9,
				R.id.listen_udp_minus10, R.id.listen_udp_minus11, R.id.listen_udp_minus12
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
					syncVisibleVariables();
					applyRowVisibility(view, rowIds, rows);
					ElseIfSeparatorBrick.refreshScriptList(view);
				});
			}
		}

		View addButton = view.findViewById(R.id.brick_listen_udp_add);
		if (addButton != null) {
			addButton.setOnClickListener(click -> {
				int hidden = firstHiddenRow();
				if (hidden >= 0) {
					rows[hidden] = true;
					syncVisibleVariables();
					applyRowVisibility(view, rowIds, rows);
					BrickSpinner<UserVariable> spinner = new BrickSpinner<>(spinnerIds[hidden], view, items);
					spinner.setOnItemSelectedListener(ListenUdpBrick.this);
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
		View addButton = view.findViewById(R.id.brick_listen_udp_add);
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
	public int getViewResource() {
		return R.layout.brick_listen_udp;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createListenUdpAction(sprite, sequence,
				getVariables(), listenModeSelection, getFormulaWithBrickField(BrickField.TEXT)));
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		ListenUdpBrick clone = (ListenUdpBrick) super.clone();
		clone.spinners = new HashMap<>();
		clone.spinnerSlots = new HashMap<>();
		return clone;
	}
}
