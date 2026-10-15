package org.catrobat.catroid.content;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenUdpMessageBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.UdpMessageEventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class WhenUdpMessageScript extends Script {
	private static final long serialVersionUID = 1L;

	private UserVariable messageVariable;

	public WhenUdpMessageScript() {
	}

	public WhenUdpMessageScript(UserVariable variable) {
		this.messageVariable = variable;
	}

	public UserVariable getMessageVariable() {
		return messageVariable;
	}

	public void setMessageVariable(UserVariable variable) {
		this.messageVariable = variable;
	}

	@Override
	public Script clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (scriptBrick == null) {
			scriptBrick = new WhenUdpMessageBrick(this);
		}
		return scriptBrick;
	}

	@Override
	public void addRequiredResources(final Brick.ResourcesSet requiredResourcesSet) {
		for (Brick brick : brickList) {
			brick.addRequiredResources(requiredResourcesSet);
		}
	}

	@Override
	public EventId createEventId(Sprite sprite) {
		return new UdpMessageEventId();
	}
}
