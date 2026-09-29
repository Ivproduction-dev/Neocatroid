package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoCastRayAction extends TemporalAction {
    private Scope scope;
    private Formula rayName;
    private Formula fromName;
    private Formula towardName;
    private Formula distance;
    private Formula maxHits;

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
            String ray = rayName != null ? rayName.interpretString(scope) : null;
            String from = fromName != null ? fromName.interpretString(scope) : null;
            String toward = towardName != null ? towardName.interpretString(scope) : null;
            if (ray == null || ray.isEmpty() || from == null || from.isEmpty()
                    || toward == null || toward.isEmpty()) {
                return;
            }
            float dist = distance != null ? distance.interpretFloat(scope) : 200f;
            int hits = maxHits != null ? maxHits.interpretFloat(scope).intValue() : 50;
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            Neo3DFacade.facadeSetRay(sceneId, ray, from, toward, dist, hits);
            Neo3DFacade.facadeCastRay(sceneId, ray);
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setRayName(Formula rayName) {
        this.rayName = rayName;
    }

    public void setFromName(Formula fromName) {
        this.fromName = fromName;
    }

    public void setTowardName(Formula towardName) {
        this.towardName = towardName;
    }

    public void setDistance(Formula distance) {
        this.distance = distance;
    }

    public void setMaxHits(Formula maxHits) {
        this.maxHits = maxHits;
    }
}
