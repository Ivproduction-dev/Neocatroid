package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoCreateObjectAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;

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
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            Neo3DFacade.facadeCreateObject(sceneId, name);
        } catch (InterpretationException | IllegalStateException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setObjectName(Formula objectName) {
        this.objectName = objectName;
    }
}
