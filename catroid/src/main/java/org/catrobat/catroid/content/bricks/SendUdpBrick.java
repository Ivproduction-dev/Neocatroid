package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.List;

public class SendUdpBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public static final int ROW_COUNT = 12;

	private int visibleFields = 1;
	private int sendModeSelection;
	private boolean[] rowVisible;

	public SendUdpBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_send_udp_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
		addAllowedBrickField(BrickField.IP, R.id.brick_send_udp_ip_edit);
		addAllowedBrickField(BrickField.PORT, R.id.brick_send_udp_port_edit);
		addAllowedBrickField(BrickField.VALUE, R.id.brick_send_udp_edit);
		addAllowedBrickField(BrickField.VALUE_2, R.id.brick_send_udp_edit2);
		addAllowedBrickField(BrickField.VALUE_3, R.id.brick_send_udp_edit3);
		addAllowedBrickField(BrickField.VALUE_4, R.id.brick_send_udp_edit4);
		addAllowedBrickField(BrickField.VALUE_5, R.id.brick_send_udp_edit5);
		addAllowedBrickField(BrickField.VALUE_6, R.id.brick_send_udp_edit6);
		addAllowedBrickField(BrickField.VALUE_7, R.id.brick_send_udp_edit7);
		addAllowedBrickField(BrickField.VALUE_8, R.id.brick_send_udp_edit8);
		addAllowedBrickField(BrickField.VALUE_9, R.id.brick_send_udp_edit9);
		addAllowedBrickField(BrickField.VALUE_10, R.id.brick_send_udp_edit10);
		addAllowedBrickField(BrickField.VALUE_11, R.id.brick_send_udp_edit11);
		addAllowedBrickField(BrickField.VALUE_12, R.id.brick_send_udp_edit12);
		rowVisible = newRowVisible();
	}

	public SendUdpBrick(String value) {
		this(new Formula(value));
	}

	public SendUdpBrick(Formula value) {
		this();
		setFormulaWithBrickField(BrickField.VALUE, value);
	}

	public int getVisibleFields() {
		return visibleFields;
	}

	public void setVisibleFields(int visibleFields) {
		this.visibleFields = visibleFields;
	}

	public int getSendModeSelection() {
		return sendModeSelection;
	}

	public void setSendModeSelection(int sendModeSelection) {
		this.sendModeSelection = sendModeSelection;
	}

	public boolean[] getRowVisible() {
		if (rowVisible == null) {
			rowVisible = newRowVisible();
			int count = Math.max(1, Math.min(ROW_COUNT, visibleFields));
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

	private void syncVisibleFields() {
		int count = 0;
		for (boolean visible : getRowVisible()) {
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
		return R.layout.brick_send_udp;
	}

	@Override
	public View getView(Context context) {
		View view = super.getView(context);
		int[] rowIds = {
				R.id.brick_send_udp_row, R.id.brick_send_udp_row2, R.id.brick_send_udp_row3,
				R.id.brick_send_udp_row4, R.id.brick_send_udp_row5, R.id.brick_send_udp_row6,
				R.id.brick_send_udp_row7, R.id.brick_send_udp_row8, R.id.brick_send_udp_row9,
				R.id.brick_send_udp_row10, R.id.brick_send_udp_row11, R.id.brick_send_udp_row12
		};
		int[] minusIds = {
				R.id.brick_send_udp_minus, R.id.brick_send_udp_minus2, R.id.brick_send_udp_minus3,
				R.id.brick_send_udp_minus4, R.id.brick_send_udp_minus5, R.id.brick_send_udp_minus6,
				R.id.brick_send_udp_minus7, R.id.brick_send_udp_minus8, R.id.brick_send_udp_minus9,
				R.id.brick_send_udp_minus10, R.id.brick_send_udp_minus11, R.id.brick_send_udp_minus12
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
		View addButton = view.findViewById(R.id.brick_send_udp_add);
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
		android.widget.Spinner modeSpinner = view.findViewById(R.id.brick_send_udp_mode_spinner);
		if (modeSpinner != null) {
			android.widget.ArrayAdapter<CharSequence> modeAdapter =
					android.widget.ArrayAdapter.createFromResource(context,
							R.array.udp_send_modes, android.R.layout.simple_spinner_item);
			modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			modeSpinner.setAdapter(modeAdapter);
			modeSpinner.setOnItemSelectedListener(
					new android.widget.AdapterView.OnItemSelectedListener() {
						@Override
						public void onItemSelected(android.widget.AdapterView<?> parent,
								View selectedView, int position, long id) {
							sendModeSelection = position;
							applyModeVisibility(view);
						}

						@Override
						public void onNothingSelected(android.widget.AdapterView<?> parent) {
						}
					});
			modeSpinner.setSelection(sendModeSelection);
			applyModeVisibility(view);
		}
		return view;
	}

	private void applyModeVisibility(View view, int[] rowIds, boolean[] rows) {
		applyRowVisibility(view, rowIds, rows);
		applyModeVisibility(view);
	}

	private void applyModeVisibility(View view) {
		View target = view.findViewById(R.id.brick_send_udp_target_row);
		if (target != null) {
			target.setVisibility(sendModeSelection == 1 ? View.VISIBLE : View.GONE);
		}
	}

	private void applyRowVisibility(View view, int[] rowIds, boolean[] rows) {
		for (int i = 0; i < rowIds.length; i++) {
			View rowView = view.findViewById(rowIds[i]);
			if (rowView != null) {
				rowView.setVisibility(rows[i] ? View.VISIBLE : View.GONE);
			}
		}
		View addButton = view.findViewById(R.id.brick_send_udp_add);
		if (addButton != null) {
			addButton.setVisibility(firstHiddenRow() >= 0 ? View.VISIBLE : View.GONE);
		}
	}

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
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSendUdpAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT), getFormulaWithBrickField(BrickField.IP),
				getFormulaWithBrickField(BrickField.PORT), getValues(), sendModeSelection));
	}
}
