package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class ConnectUdpBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public ConnectUdpBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_connect_udp_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
		addAllowedBrickField(BrickField.IP, R.id.brick_connect_udp_ip_edit);
		addAllowedBrickField(BrickField.PORT, R.id.brick_connect_udp_port_edit);
	}

	public ConnectUdpBrick(String serverName, String ip, String port) {
		this(new Formula(serverName), new Formula(ip), new Formula(port));
	}

	public ConnectUdpBrick(Formula serverName, Formula ip, Formula port) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
		setFormulaWithBrickField(BrickField.IP, ip);
		setFormulaWithBrickField(BrickField.PORT, port);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_connect_udp;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createConnectUdpAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT), getFormulaWithBrickField(BrickField.IP),
				getFormulaWithBrickField(BrickField.PORT)));
	}
}
