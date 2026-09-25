package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoSetObjectVelocityAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula velocityX;
    private Formula velocityY;
    private Formula velocityZ;

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
            float x = velocityX != null ? velocityX.interpretFloat(scope) : 0f;
            float y = velocityY != null ? velocityY.interpretFloat(scope) : 0f;
            float z = velocityZ != null ? velocityZ.interpretFloat(scope) : 0f;

            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                return;
            }
            Neo3DFacade.facadeSetLinearVelocity(sceneId, objectId, x, y, z);
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

    public void setVelocityX(Formula velocityX) {
        this.velocityX = velocityX;
    }

    public void setVelocityY(Formula velocityY) {
        this.velocityY = velocityY;
    }

    public void setVelocityZ(Formula velocityZ) {
        this.velocityZ = velocityZ;
    }
}
