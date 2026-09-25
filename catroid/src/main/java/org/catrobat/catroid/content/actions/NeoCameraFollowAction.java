package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoCameraFollowAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula offX;
    private Formula offY;
    private Formula offZ;
    private int followMode;

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
            float x = offX != null ? offX.interpretFloat(scope) : 0f;
            float y = offY != null ? offY.interpretFloat(scope) : 0f;
            float z = offZ != null ? offZ.interpretFloat(scope) : 0f;

            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            Neo3DFacade.facadeSetCameraFollow(sceneId, name == null ? "" : name, x, y, z,
                    followMode == 1);
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

    public void setOffX(Formula offX) {
        this.offX = offX;
    }

    public void setOffY(Formula offY) {
        this.offY = offY;
    }

    public void setOffZ(Formula offZ) {
        this.offZ = offZ;
    }

    public void setFollowMode(int followMode) {
        this.followMode = followMode;
    }
}
