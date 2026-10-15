package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class DisconnectUdpBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public DisconnectUdpBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_disconnect_udp_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
	}

	public DisconnectUdpBrick(String serverName) {
		this(new Formula(serverName));
	}

	public DisconnectUdpBrick(Formula serverName) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_disconnect_udp;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createDisconnectUdpAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT)));
	}
}
