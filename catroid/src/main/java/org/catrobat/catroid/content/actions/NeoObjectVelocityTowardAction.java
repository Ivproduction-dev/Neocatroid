package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoObjectVelocityTowardAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula targetName;
    private Formula speed;

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
            String target = targetName != null ? targetName.interpretString(scope) : null;
            if (name == null || name.isEmpty()) {
                return;
            }
            float speedValue = speed != null ? speed.interpretFloat(scope) : 0f;
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                android.util.Log.w("Neo3D", "VelocityToward: object not found: '" + name + "'");
                return;
            }
            float[] from = Neo3DFacade.facadeGetObjectPosition(sceneId, objectId);
            float[] aim;
            if (target == null || target.isEmpty()) {
                aim = Neo3DFacade.facadeGetMainCameraPosition(sceneId);
            } else {
                aim = Neo3DFacade.facadeGetObjectPositionByName(sceneId, target);
            }
            if (from == null || aim == null) {
                return;
            }
            float dx = aim[0] - from[0];
            float dy = aim[1] - from[1];
            float dz = aim[2] - from[2];
            float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len < 1e-6f) {
                return;
            }
            Neo3DFacade.facadeSetLinearVelocity(sceneId, objectId,
                    dx / len * speedValue, dy / len * speedValue, dz / len * speedValue);
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

    public void setSpeed(Formula speed) {
        this.speed = speed;
    }
}
