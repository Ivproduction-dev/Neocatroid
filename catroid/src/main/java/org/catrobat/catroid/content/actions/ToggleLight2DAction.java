package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.twodlight.LightManager2D;

public class ToggleLight2DAction extends TemporalAction {

    private Scope scope;
    private Formula lightName;

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setLightName(Formula lightName) {
        this.lightName = lightName;
    }

    @Override
    protected void update(float percent) {
        if (scope == null) {
            return;
        }
        LightManager2D manager = LightManager2D.forActiveStage();
        if (manager == null) {
            return;
        }
        try {
            String id = lightName == null ? "" : lightName.interpretString(scope);
            if (id == null || id.isEmpty()) {
                return;
            }
            manager.toggleLight(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
