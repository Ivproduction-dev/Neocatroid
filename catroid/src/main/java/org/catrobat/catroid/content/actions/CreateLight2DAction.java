package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.twodlight.LightManager2D;

public class CreateLight2DAction extends TemporalAction {

    private Scope scope;
    private Formula lightName;
    private Formula posX;
    private Formula posY;
    private Formula radius;
    private Formula intensity;
    private Formula color;

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setLightName(Formula lightName) {
        this.lightName = lightName;
    }

    public void setPosX(Formula posX) {
        this.posX = posX;
    }

    public void setPosY(Formula posY) {
        this.posY = posY;
    }

    public void setRadius(Formula radius) {
        this.radius = radius;
    }

    public void setIntensity(Formula intensity) {
        this.intensity = intensity;
    }

    public void setColor(Formula color) {
        this.color = color;
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
            float x = posX == null ? 0f : posX.interpretFloat(scope);
            float y = posY == null ? 0f : posY.interpretFloat(scope);
            float lightRadius = radius == null ? 300f : radius.interpretFloat(scope);
            float lightIntensity = intensity == null ? 1f : intensity.interpretFloat(scope);
            int lightColor = color == null ? 0xFFFFFF : color.interpretInteger(scope);
            manager.createOrUpdateLight(id, x, y, lightRadius, lightIntensity, lightColor);
            manager.detachFromSprite(id);
            manager.setEnabled(id, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
