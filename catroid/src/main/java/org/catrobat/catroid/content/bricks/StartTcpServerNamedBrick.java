package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class StartTcpServerNamedBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public StartTcpServerNamedBrick() {
		addAllowedBrickField(BrickField.TEXT, R.id.brick_start_tcp_server_named_name_edit);
		setFormulaWithBrickField(BrickField.TEXT, new Formula(""));
		addAllowedBrickField(BrickField.PORT, R.id.brick_start_tcp_server_named_port_edit);
	}

	public StartTcpServerNamedBrick(String serverName, String port) {
		this(new Formula(serverName), new Formula(port));
	}

	public StartTcpServerNamedBrick(Formula serverName, Formula port) {
		this();
		setFormulaWithBrickField(BrickField.TEXT, serverName);
		setFormulaWithBrickField(BrickField.PORT, port);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_start_tcp_server_named;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createStartTcpServerNamedAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.TEXT), getFormulaWithBrickField(BrickField.PORT)));
	}
}
