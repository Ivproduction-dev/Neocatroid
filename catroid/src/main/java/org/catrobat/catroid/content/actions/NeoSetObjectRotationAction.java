package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoSetObjectRotationAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula yaw;
    private Formula pitch;
    private Formula roll;

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
            String name = objectName != null ? objectName.interpretString(scope) : null;
            if (name == null || name.isEmpty()) {
                return;
            }
            float yawValue = yaw != null ? yaw.interpretFloat(scope) : 0f;
            float pitchValue = pitch != null ? pitch.interpretFloat(scope) : 0f;
            float rollValue = roll != null ? roll.interpretFloat(scope) : 0f;

            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                return;
            }
            Neo3DFacade.facadeSetRotation(sceneId, objectId, yawValue, pitchValue, rollValue);
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setObjectName(Formula objectName) {
        this.objectName = objectName;
    }

    public void setYaw(Formula yaw) {
        this.yaw = yaw;
    }

    public void setPitch(Formula pitch) {
        this.pitch = pitch;
    }

    public void setRoll(Formula roll) {
        this.roll = roll;
    }
}
