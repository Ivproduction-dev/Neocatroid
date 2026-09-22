package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.twodlight.LightManager2D;

public class ModifyLight2DPropertyAction extends TemporalAction {

    public static final int PROPERTY_POSITION = 0;
    public static final int PROPERTY_RADIUS = 1;
    public static final int PROPERTY_INTENSITY = 2;
    public static final int PROPERTY_COLOR = 3;
    public static final int PROPERTY_AMBIENT = 4;

    private Scope scope;
    private Formula lightName;
    private Formula objectName;
    private int property;
    private Formula valueX;
    private Formula valueY;
    private Formula value;

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setLightName(Formula lightName) {
        this.lightName = lightName;
    }

    public void setObjectName(Formula objectName) {
        this.objectName = objectName;
    }

    public void setProperty(int property) {
        this.property = property;
    }

    public void setValueX(Formula valueX) {
        this.valueX = valueX;
    }

    public void setValueY(Formula valueY) {
        this.valueY = valueY;
    }

    public void setValue(Formula value) {
        this.value = value;
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
            if (property == PROPERTY_AMBIENT) {
                float ambient = value == null ? 0.15f : value.interpretFloat(scope);
                manager.setAmbient(ambient);
                return;
            }
            String id = lightName == null ? "" : lightName.interpretString(scope);
            if (id == null || id.isEmpty()) {
                return;
            }
            String object = objectName == null ? "" : objectName.interpretString(scope);
            boolean hasObject = object != null && !object.isEmpty();
            switch (property) {
                case PROPERTY_POSITION:
                    if (hasObject) {
                        manager.attachToSprite(id, object);
                    } else {
                        manager.detachFromSprite(id);
                        float x = valueX == null ? 0f : valueX.interpretFloat(scope);
                        float y = valueY == null ? 0f : valueY.interpretFloat(scope);
                        manager.setProperty(id, LightManager2D.PROP_X, x);
                        manager.setProperty(id, LightManager2D.PROP_Y, y);
                    }
                    break;
                case PROPERTY_RADIUS:
                    float r = value == null ? 300f : value.interpretFloat(scope);
                    manager.setProperty(id, LightManager2D.PROP_RADIUS, r);
                    break;
                case PROPERTY_INTENSITY:
                    float i = value == null ? 1f : value.interpretFloat(scope);
                    manager.setProperty(id, LightManager2D.PROP_INTENSITY, i);
                    break;
                case PROPERTY_COLOR:
                    int c = value == null ? 0xFFFFFF : value.interpretInteger(scope);
                    manager.setColor(id, c);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
