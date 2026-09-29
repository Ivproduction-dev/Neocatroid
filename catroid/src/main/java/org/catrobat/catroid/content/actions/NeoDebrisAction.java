package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoDebrisAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula delay;

    private boolean prepared;
    private boolean done;
    private String sceneId;
    private String objectId;

    @Override
    public void restart() {
        super.restart();
        prepared = false;
        done = false;
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
            float seconds = delay != null ? delay.interpretFloat(scope) : 0f;
            setDuration(Math.max(0f, seconds));
            return true;
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    protected void update(float percent) {
        if (done) {
            return;
        }
        if (percent < 1f) {
            return;
        }
        done = true;
        try {
            Neo3DFacade.facadeRemoveObject(sceneId, objectId);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setObjectName(Formula objectName) {
        this.objectName = objectName;
    }

    public void setDelay(Formula delay) {
        this.delay = delay;
    }
}
