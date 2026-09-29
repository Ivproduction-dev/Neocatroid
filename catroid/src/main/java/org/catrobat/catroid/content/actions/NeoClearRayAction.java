package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoClearRayAction extends TemporalAction {
    private Scope scope;
    private Formula rayName;

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
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            Neo3DFacade.facadeClearRay(sceneId, ray == null ? "" : ray);
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
}
