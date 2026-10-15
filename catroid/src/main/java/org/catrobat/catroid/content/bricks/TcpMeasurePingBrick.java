package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class TcpMeasurePingBrick extends UserVariableBrickWithFormula {

	private static final long serialVersionUID = 1L;

	public TcpMeasurePingBrick() {
		addAllowedBrickField(BrickField.IP, R.id.brick_tcp_measure_ping_ip_edit);
		addAllowedBrickField(BrickField.PORT, R.id.brick_tcp_measure_ping_port_edit);
		addAllowedBrickField(BrickField.VALUE, R.id.brick_tcp_measure_ping_timeout_edit);
		setFormulaWithBrickField(BrickField.VALUE, new Formula("3000"));
	}

	public TcpMeasurePingBrick(String ip, String port) {
		this(new Formula(ip), new Formula(port));
	}

	public TcpMeasurePingBrick(Formula ip, Formula port) {
		this();
		setFormulaWithBrickField(BrickField.IP, ip);
		setFormulaWithBrickField(BrickField.PORT, port);
	}

	public TcpMeasurePingBrick(UserVariable variable) {
		this();
		this.userVariable = variable;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_tcp_measure_ping;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.brick_tcp_measure_ping_spinner;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createTcpPingAction(sprite, sequence, null,
				getFormulaWithBrickField(BrickField.IP), getFormulaWithBrickField(BrickField.PORT),
				userVariable, true, getFormulaWithBrickField(BrickField.VALUE)));
	}
}
