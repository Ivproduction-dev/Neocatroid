package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DEngine;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoCameraTouchLookAction extends TemporalAction {
    private Scope scope;
    private Formula sensitivity;
    private Formula minPitch;
    private Formula maxPitch;
    private int mode = Neo3DEngine.CameraTouchMode.FIRST_PERSON.ordinal();

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
            float sensitivityValue = sensitivity != null ? sensitivity.interpretFloat(scope) : 0.3f;
            float minPitchValue = minPitch != null ? minPitch.interpretFloat(scope) : -60f;
            float maxPitchValue = maxPitch != null ? maxPitch.interpretFloat(scope) : 60f;

            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            Neo3DFacade.facadeSetCameraTouchLook(sceneId, mode, sensitivityValue,
                    minPitchValue, maxPitchValue);
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setSensitivity(Formula sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setMinPitch(Formula minPitch) {
        this.minPitch = minPitch;
    }

    public void setMaxPitch(Formula maxPitch) {
        this.maxPitch = maxPitch;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
