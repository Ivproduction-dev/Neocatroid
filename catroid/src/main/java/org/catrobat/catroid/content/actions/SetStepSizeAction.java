package org.catrobat.catroid.content.actions;

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class SetStepSizeAction extends TemporalAction {
    private Scope scope;
    private Formula stepSize;

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
            float size = stepSize != null ? stepSize.interpretFloat(scope) : 1f;
            if (Float.isNaN(size) || Float.isInfinite(size) || size <= 0f) {
                return;
            }
            MoveNStepsAction.stepSizeMultiplier = size;
        } catch (InterpretationException e) {
            Log.d(getClass().getSimpleName(), "Formula interpretation failed.", e);
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setStepSize(Formula stepSize) {
        this.stepSize = stepSize;
    }
}
