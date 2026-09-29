package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.SessionData;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class CreateSessionVariableAction extends TemporalAction {
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
            if (name == null || name.isEmpty() || SessionData.exists(name)) {
                return;
            }
            SessionData.set(name, readValue());
        } catch (InterpretationException e) {
            return;
        }
    }

    private Object readValue() throws InterpretationException {
        if (value == null) {
            return 0.0;
        }
        try {
            return value.interpretDouble(scope);
        } catch (InterpretationException e) {
            return value.interpretString(scope);
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
