package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class UdpPingNamedBrick extends UserVariableBrickWithFormula {

	private static final long serialVersionUID = 1L;

	public UdpPingNamedBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_udp_ping_named_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
		addAllowedBrickField(BrickField.VALUE, R.id.brick_udp_ping_named_timeout_edit);
		setFormulaWithBrickField(BrickField.VALUE, new Formula("3000"));
	}

	public UdpPingNamedBrick(String serverName) {
		this(new Formula(serverName));
	}

	public UdpPingNamedBrick(Formula serverName) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
	}

	public UdpPingNamedBrick(UserVariable variable) {
		this();
		this.userVariable = variable;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_udp_ping_named;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.brick_udp_ping_named_spinner;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createUdpPingAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT), null, null, userVariable, false,
				getFormulaWithBrickField(BrickField.VALUE)));
	}
}
