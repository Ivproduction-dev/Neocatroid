package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class StartUdpBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public StartUdpBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_start_udp_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
		addAllowedBrickField(BrickField.PORT, R.id.brick_start_udp_port_edit);
	}

	public StartUdpBrick(String serverName, String port) {
		this(new Formula(serverName), new Formula(port));
	}

	public StartUdpBrick(Formula serverName, Formula port) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
		setFormulaWithBrickField(BrickField.PORT, port);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_start_udp;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createStartUdpAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT), getFormulaWithBrickField(BrickField.PORT)));
	}
}
