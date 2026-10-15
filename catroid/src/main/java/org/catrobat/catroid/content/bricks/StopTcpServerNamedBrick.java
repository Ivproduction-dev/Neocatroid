package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class StopTcpServerNamedBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public StopTcpServerNamedBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_stop_tcp_server_named_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
	}

	public StopTcpServerNamedBrick(String serverName) {
		this(new Formula(serverName));
	}

	public StopTcpServerNamedBrick(Formula serverName) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_stop_tcp_server_named;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createStopTcpServerNamedAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT)));
	}
}
