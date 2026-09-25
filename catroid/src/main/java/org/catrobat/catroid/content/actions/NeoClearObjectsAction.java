package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoClearObjectsAction extends TemporalAction {
    private Scope scope;

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
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            Neo3DFacade.facadeClearObjects(sceneId);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}
