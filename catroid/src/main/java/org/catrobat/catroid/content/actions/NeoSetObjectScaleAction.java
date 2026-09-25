package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoSetObjectScaleAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula scaleX;
    private Formula scaleY;
    private Formula scaleZ;

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
            float x = scaleX != null ? scaleX.interpretFloat(scope) : 1f;
            float y = scaleY != null ? scaleY.interpretFloat(scope) : 1f;
            float z = scaleZ != null ? scaleZ.interpretFloat(scope) : 1f;

            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                return;
            }
            Neo3DFacade.facadeSetScale(sceneId, objectId, x, y, z);
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

    public void setScaleX(Formula scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(Formula scaleY) {
        this.scaleY = scaleY;
    }

    public void setScaleZ(Formula scaleZ) {
        this.scaleZ = scaleZ;
    }
}
