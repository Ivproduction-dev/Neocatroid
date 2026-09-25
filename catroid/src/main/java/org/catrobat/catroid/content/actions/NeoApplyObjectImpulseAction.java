package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoApplyObjectImpulseAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula impulseX;
    private Formula impulseY;
    private Formula impulseZ;

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
            float x = impulseX != null ? impulseX.interpretFloat(scope) : 0f;
            float y = impulseY != null ? impulseY.interpretFloat(scope) : 0f;
            float z = impulseZ != null ? impulseZ.interpretFloat(scope) : 0f;

            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                return;
            }
            Neo3DFacade.facadeAddImpulse(sceneId, objectId, x, y, z);
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

    public void setImpulseX(Formula impulseX) {
        this.impulseX = impulseX;
    }

    public void setImpulseY(Formula impulseY) {
        this.impulseY = impulseY;
    }

    public void setImpulseZ(Formula impulseZ) {
        this.impulseZ = impulseZ;
    }
}
