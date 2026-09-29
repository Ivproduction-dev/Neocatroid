package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoSetObjectVariableAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private Formula variableName;
    private Formula value;

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
            String key = variableName != null ? variableName.interpretString(scope) : null;
            if (name == null || name.isEmpty() || key == null || key.isEmpty()) {
                return;
            }
            Object stored;
            try {
                stored = value != null ? value.interpretDouble(scope) : 0.0;
            } catch (InterpretationException e) {
                stored = value != null ? value.interpretString(scope) : "";
            }
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                android.util.Log.w("Neo3D", "SetVariable: object not found: '" + name + "'");
                return;
            }
            Neo3DFacade.facadeSetObjectVariable(sceneId, objectId, key, stored);
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

    public void setVariableName(Formula variableName) {
        this.variableName = variableName;
    }

    public void setValue(Formula value) {
        this.value = value;
    }
}
