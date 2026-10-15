package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class DisconnectTcpServerNamedBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public DisconnectTcpServerNamedBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_disconnect_tcp_server_named_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
	}

	public DisconnectTcpServerNamedBrick(String serverName) {
		this(new Formula(serverName));
	}

	public DisconnectTcpServerNamedBrick(Formula serverName) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_disconnect_tcp_server_named;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createDisconnectTcpServerNamedAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT)));
	}
}
