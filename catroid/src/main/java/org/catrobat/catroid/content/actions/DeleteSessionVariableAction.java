package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.SessionData;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class DeleteSessionVariableAction extends TemporalAction {
    private Scope scope;
    private Formula variableName;

    private boolean started;

    @Override
    public void restart() {
        super.restart();
        started = false;
    }

    @Override
    protected void update(float percent) {
        if (started) {
            return;
        }
        if (scope == null) {
            return;
        }
        started = true;

        try {
            String name = variableName != null ? variableName.interpretString(scope) : null;
            if (name == null || name.isEmpty()) {
                return;
            }
            SessionData.remove(name);
        } catch (InterpretationException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setVariableName(Formula variableName) {
        this.variableName = variableName;
    }
}
