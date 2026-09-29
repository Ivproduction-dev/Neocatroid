package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.SessionData;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class ChangeSessionVariableAction extends TemporalAction {
    private Scope scope;
    private Formula variableName;
    private Formula value;

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
            double delta = value != null ? value.interpretDouble(scope) : 0.0;
            SessionData.set(name, SessionData.asDouble(SessionData.get(name)) + delta);
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

    public void setValue(Formula value) {
        this.value = value;
    }
}
