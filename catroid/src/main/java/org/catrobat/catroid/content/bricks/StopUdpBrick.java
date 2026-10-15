package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class StopUdpBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public StopUdpBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_stop_udp_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
	}

	public StopUdpBrick(String serverName) {
		this(new Formula(serverName));
	}

	public StopUdpBrick(Formula serverName) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_stop_udp;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createStopUdpAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT)));
	}
}
