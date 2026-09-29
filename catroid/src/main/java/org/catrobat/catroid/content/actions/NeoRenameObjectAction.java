package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoRenameObjectAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula newName;

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
            String renamed = newName != null ? newName.interpretString(scope) : null;
            if (name == null || name.isEmpty() || renamed == null || renamed.isEmpty()) {
                return;
            }
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                android.util.Log.w("Neo3D", "Rename: object not found: '" + name + "'");
                return;
            }
            Neo3DFacade.facadeRenameObject(sceneId, objectId, renamed);
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

    public void setNewName(Formula newName) {
        this.newName = newName;
    }
}
