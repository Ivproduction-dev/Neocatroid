package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class ClearTcpVariableBrick extends UserVariableBrick {

	private static final long serialVersionUID = 1L;

	public ClearTcpVariableBrick() {
	}

	public ClearTcpVariableBrick(UserVariable userVariable) {
		this.userVariable = userVariable;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_clear_tcp_variable;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.brick_clear_tcp_variable_spinner;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createClearTcpVariableAction(sprite, sequence, userVariable));
	}
}
