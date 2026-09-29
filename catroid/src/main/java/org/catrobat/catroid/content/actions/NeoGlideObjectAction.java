package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoGlideObjectAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula targetName;
    private Formula duration;

    private boolean prepared;
    private String sceneId;
    private String objectId;
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
            String name = objectName != null ? objectName.interpretString(scope) : null;
            if (name == null || name.isEmpty()) {
                return false;
            }
            sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                return false;
            }
            float[] pos = Neo3DFacade.facadeGetObjectPosition(sceneId, objectId);
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
            String target = targetName != null ? targetName.interpretString(scope) : null;
            if (target == null || target.isEmpty()) {
                return;
            }
            float[] aim = Neo3DFacade.facadeGetObjectPositionByName(sceneId, target);
            if (aim == null) {
                return;
            }
            float t = Math.max(0f, Math.min(1f, percent));
            Neo3DFacade.facadeSetPosition(sceneId, objectId,
                    startX + (aim[0] - startX) * t,
                    startY + (aim[1] - startY) * t,
                    startZ + (aim[2] - startZ) * t);
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

    public void setTargetName(Formula targetName) {
        this.targetName = targetName;
    }

    public void setDurationFormula(Formula duration) {
        this.duration = duration;
    }
}
