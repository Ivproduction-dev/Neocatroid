package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.List;

public class SendToTcpClientsBrick extends FormulaBrick implements BrickSpinner.OnItemSelectedListener<StringOption> {

	private static final long serialVersionUID = 1L;

	public static final int ROW_COUNT = 12;

	private int visibleFields = 1;
	private int echoMode = 0;
	private boolean[] rowVisible;

	private transient BrickSpinner<StringOption> echoSpinner;

	public SendToTcpClientsBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_send_tcp_clients_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
		addAllowedBrickField(BrickField.VALUE, R.id.brick_send_tcp_clients_edit);
		addAllowedBrickField(BrickField.VALUE_2, R.id.brick_send_tcp_clients_edit2);
		addAllowedBrickField(BrickField.VALUE_3, R.id.brick_send_tcp_clients_edit3);
		addAllowedBrickField(BrickField.VALUE_4, R.id.brick_send_tcp_clients_edit4);
		addAllowedBrickField(BrickField.VALUE_5, R.id.brick_send_tcp_clients_edit5);
		addAllowedBrickField(BrickField.VALUE_6, R.id.brick_send_tcp_clients_edit6);
		addAllowedBrickField(BrickField.VALUE_7, R.id.brick_send_tcp_clients_edit7);
		addAllowedBrickField(BrickField.VALUE_8, R.id.brick_send_tcp_clients_edit8);
		addAllowedBrickField(BrickField.VALUE_9, R.id.brick_send_tcp_clients_edit9);
		addAllowedBrickField(BrickField.VALUE_10, R.id.brick_send_tcp_clients_edit10);
		addAllowedBrickField(BrickField.VALUE_11, R.id.brick_send_tcp_clients_edit11);
		addAllowedBrickField(BrickField.VALUE_12, R.id.brick_send_tcp_clients_edit12);
		rowVisible = newRowVisible();
	}

	public SendToTcpClientsBrick(String value) {
		this(new Formula(value));
	}

	public SendToTcpClientsBrick(Formula value) {
		this();
		setFormulaWithBrickField(BrickField.VALUE, value);
	}

	public SendToTcpClientsBrick(String serverName, String value) {
		this(new Formula(serverName), new Formula(value));
	}

	public SendToTcpClientsBrick(Formula serverName, Formula value) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
		setFormulaWithBrickField(BrickField.VALUE, value);
	}

	public int getVisibleFields() {
		return visibleFields;
	}

	public void setVisibleFields(int visibleFields) {
		this.visibleFields = visibleFields;
		this.rowVisible = newRowVisible();
		int count = Math.max(1, Math.min(ROW_COUNT, visibleFields));
		for (int i = 0; i < count; i++) {
			rowVisible[i] = true;
		}
	}

	public int getEchoMode() {
		return echoMode;
	}

	public void setEchoMode(int echoMode) {
		this.echoMode = echoMode;
	}

	public boolean[] getRowVisible() {
		if (rowVisible == null || isLegacyRowState()) {
			rowVisible = newRowVisible();
			int count = Math.max(1, Math.min(ROW_COUNT, visibleFields));
			for (int i = 0; i < count; i++) {
				rowVisible[i] = true;
			}
		}
		return rowVisible;
	}

	private boolean isLegacyRowState() {
		if (rowVisible == null || visibleFields <= 1 || rowVisible.length != ROW_COUNT || !rowVisible[0]) {
			return false;
		}
		for (int i = 1; i < ROW_COUNT; i++) {
			if (rowVisible[i]) {
				return false;
			}
		}
		return true;
	}

	public void setRowVisible(boolean[] rowVisible) {
		this.rowVisible = rowVisible;
	}

	private boolean[] newRowVisible() {
		boolean[] rows = new boolean[ROW_COUNT];
		rows[0] = true;
		return rows;
	}

	private void syncVisibleFields() {
		if (rowVisible == null) {
			return;
		}
		int count = 0;
		for (boolean visible : rowVisible) {
			if (visible) {
				count++;
			}
		}
		visibleFields = count;
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

	@Override
	public int getViewResource() {
		return R.layout.brick_send_to_tcp_clients;
	}

	@Override
	public View getView(Context context) {
		View view = super.getView(context);
		int[] rowIds = {
				R.id.brick_send_tcp_clients_row, R.id.brick_send_tcp_clients_row2,
				R.id.brick_send_tcp_clients_row3, R.id.brick_send_tcp_clients_row4,
				R.id.brick_send_tcp_clients_row5, R.id.brick_send_tcp_clients_row6,
				R.id.brick_send_tcp_clients_row7, R.id.brick_send_tcp_clients_row8,
				R.id.brick_send_tcp_clients_row9, R.id.brick_send_tcp_clients_row10,
				R.id.brick_send_tcp_clients_row11, R.id.brick_send_tcp_clients_row12
		};
		int[] minusIds = {
				R.id.brick_send_tcp_clients_minus, R.id.brick_send_tcp_clients_minus2,
				R.id.brick_send_tcp_clients_minus3, R.id.brick_send_tcp_clients_minus4,
				R.id.brick_send_tcp_clients_minus5, R.id.brick_send_tcp_clients_minus6,
				R.id.brick_send_tcp_clients_minus7, R.id.brick_send_tcp_clients_minus8,
				R.id.brick_send_tcp_clients_minus9, R.id.brick_send_tcp_clients_minus10,
				R.id.brick_send_tcp_clients_minus11, R.id.brick_send_tcp_clients_minus12
		};
		boolean[] rows = getRowVisible();
		for (int i = 0; i < rowIds.length; i++) {
			final int index = i;
			View minusButton = view.findViewById(minusIds[i]);
			if (minusButton != null) {
				minusButton.setOnClickListener(click -> {
					rows[index] = false;
					syncVisibleFields();
					applyRowVisibility(view, rowIds, rows);
					try {
						setClickListeners();
					} catch (Throwable ignored) {
					}
					ElseIfSeparatorBrick.refreshScriptList(view);
				});
			}
		}
		View addButton = view.findViewById(R.id.brick_send_tcp_clients_add);
		if (addButton != null) {
			addButton.setOnClickListener(click -> {
				int hidden = firstHiddenRow();
				if (hidden >= 0) {
					rows[hidden] = true;
					syncVisibleFields();
					applyRowVisibility(view, rowIds, rows);
					try {
						setClickListeners();
					} catch (Throwable ignored) {
					}
					ElseIfSeparatorBrick.refreshScriptList(view);
				}
			});
			applyRowVisibility(view, rowIds, rows);
		}
		List<org.catrobat.catroid.common.Nameable> echoItems = new ArrayList<>();
		echoItems.add(new StringOption(context.getString(R.string.brick_send_to_tcp_clients_echo_no)));
		echoItems.add(new StringOption(context.getString(R.string.brick_send_to_tcp_clients_echo_yes)));
		echoSpinner = new BrickSpinner<>(R.id.brick_send_tcp_clients_echo_spinner, view, echoItems);
		echoSpinner.setOnItemSelectedListener(this);
		echoSpinner.setSelection(echoMode == 1 ? 1 : 0);
		return view;
	}

	private void applyRowVisibility(View view, int[] rowIds, boolean[] rows) {
		for (int i = 0; i < rowIds.length; i++) {
			View rowView = view.findViewById(rowIds[i]);
			if (rowView != null) {
				rowView.setVisibility(rows[i] ? View.VISIBLE : View.GONE);
			}
		}
		View addButton = view.findViewById(R.id.brick_send_tcp_clients_add);
		if (addButton != null) {
			addButton.setVisibility(firstHiddenRow() >= 0 ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
		if (string == null || view == null) return;
		Context ctx = view.getContext();
		if (ctx.getString(R.string.brick_send_to_tcp_clients_echo_yes).equals(string)) {
			echoMode = 1;
		} else {
			echoMode = 0;
		}
	}

	@Override
	public void onItemSelected(Integer spinnerId, StringOption item) {}

	private List<Formula> getValues() {
		List<Formula> values = new ArrayList<>();
		BrickField[] fields = {
				BrickField.VALUE, BrickField.VALUE_2, BrickField.VALUE_3, BrickField.VALUE_4,
				BrickField.VALUE_5, BrickField.VALUE_6, BrickField.VALUE_7, BrickField.VALUE_8,
				BrickField.VALUE_9, BrickField.VALUE_10, BrickField.VALUE_11, BrickField.VALUE_12
		};
		boolean[] rows = getRowVisible();
		for (int i = 0; i < fields.length; i++) {
			if (rows[i]) {
				values.add(getFormulaWithBrickField(fields[i]));
			}
		}
		return values;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		SendToTcpClientsBrick clone = (SendToTcpClientsBrick) super.clone();
		clone.echoSpinner = null;
		return clone;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSendToTcpClientsAction(sprite, sequence,
				getValues(), echoMode, getFormulaWithBrickField(BrickField.TEXT)));
	}
}
