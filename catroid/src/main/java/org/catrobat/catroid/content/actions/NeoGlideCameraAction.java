package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoGlideCameraAction extends TemporalAction {
    private Scope scope;
    private Formula targetName;
    private Formula posX;
    private Formula posY;
    private Formula posZ;
    private Formula duration;

    private boolean prepared;
    private String sceneId;
    private float startX;
    private float startY;
    private float startZ;

    @Override
    public void restart() {
        super.restart();
        prepared = false;
    }

    @Override
    public boolean act(float delta) {
        if (!prepared) {
            if (!prepare()) {
                return true;
            }
        }
        return super.act(delta);
    }

    private boolean prepare() {
        prepared = true;
        if (scope == null) {
            return false;
        }
        try {
            sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            float[] pos = Neo3DFacade.facadeGetMainCameraPosition(sceneId);
            if (pos == null) {
                return false;
            }
            startX = pos[0];
            startY = pos[1];
            startZ = pos[2];
            float seconds = duration != null ? duration.interpretFloat(scope) : 0f;
            setDuration(Math.max(0f, seconds));
            return true;
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    protected void update(float percent) {
        try {
            float[] aim = resolveTarget();
            if (aim == null) {
                return;
            }
            float t = Math.max(0f, Math.min(1f, percent));
            Neo3DFacade.facadeSetMainCameraPosition(sceneId,
                    startX + (aim[0] - startX) * t,
                    startY + (aim[1] - startY) * t,
                    startZ + (aim[2] - startZ) * t);
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    private float[] resolveTarget() throws InterpretationException {
        String target = targetName != null ? targetName.interpretString(scope) : null;
        if (target != null && !target.isEmpty()) {
            return Neo3DFacade.facadeGetObjectPositionByName(sceneId, target);
        }
        float x = posX != null ? posX.interpretFloat(scope) : 0f;
        float y = posY != null ? posY.interpretFloat(scope) : 0f;
        float z = posZ != null ? posZ.interpretFloat(scope) : 0f;
        return new float[]{x, y, z};
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setTargetName(Formula targetName) {
        this.targetName = targetName;
    }

    public void setPosX(Formula posX) {
        this.posX = posX;
    }

    public void setPosY(Formula posY) {
        this.posY = posY;
    }

    public void setPosZ(Formula posZ) {
        this.posZ = posZ;
    }

    public void setDurationFormula(Formula duration) {
        this.duration = duration;
    }
}
