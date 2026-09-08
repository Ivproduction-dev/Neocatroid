package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.twodlight.LightManager2D;

public class ControlLight2DAction extends TemporalAction {

    public static final int ACTION_TURN_ON = 0;
    public static final int ACTION_TURN_OFF = 1;
    public static final int ACTION_DELETE = 2;
    public static final int ACTION_SET_RADIUS = 3;
    public static final int ACTION_SET_INTENSITY = 4;
    public static final int ACTION_SET_COLOR = 5;
    public static final int ACTION_SET_X = 6;
    public static final int ACTION_SET_Y = 7;
    public static final int ACTION_ATTACH_SPRITE = 8;
    public static final int ACTION_DETACH_SPRITE = 9;
    public static final int ACTION_SHADOWS_ON = 10;
    public static final int ACTION_SHADOWS_OFF = 11;
    public static final int ACTION_SET_AMBIENT = 12;

    private Scope scope;
    private Formula lightName;
    private int action;
    private Formula value;
    private String attachSpriteName;

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setLightName(Formula lightName) {
        this.lightName = lightName;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public void setValue(Formula value) {
        this.value = value;
    }

    public void setAttachSpriteName(String attachSpriteName) {
        this.attachSpriteName = attachSpriteName;
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
            if (action == ACTION_SET_AMBIENT) {
                float ambient = value == null ? 0.15f : value.interpretFloat(scope);
                manager.setAmbient(ambient);
                return;
            }
            String id = lightName == null ? "" : lightName.interpretString(scope);
            if (id == null || id.isEmpty()) {
                return;
            }
            switch (action) {
                case ACTION_TURN_ON:
                    manager.setEnabled(id, true);
                    break;
                case ACTION_TURN_OFF:
                    manager.setEnabled(id, false);
                    break;
                case ACTION_DELETE:
                    manager.removeLight(id);
                    break;
                case ACTION_SET_RADIUS:
                    manager.setProperty(id, LightManager2D.PROP_RADIUS,
                            value == null ? 300f : value.interpretFloat(scope));
                    break;
                case ACTION_SET_INTENSITY:
                    manager.setProperty(id, LightManager2D.PROP_INTENSITY,
                            value == null ? 1f : value.interpretFloat(scope));
                    break;
                case ACTION_SET_COLOR:
                    manager.setColor(id, value == null ? 0xFFFFFF : value.interpretInteger(scope));
                    break;
                case ACTION_SET_X:
                    manager.setProperty(id, LightManager2D.PROP_X,
                            value == null ? 0f : value.interpretFloat(scope));
                    break;
                case ACTION_SET_Y:
                    manager.setProperty(id, LightManager2D.PROP_Y,
                            value == null ? 0f : value.interpretFloat(scope));
                    break;
                case ACTION_ATTACH_SPRITE:
                    if (attachSpriteName != null && !attachSpriteName.isEmpty()) {
                        manager.attachToSprite(id, attachSpriteName);
                    }
                    break;
                case ACTION_DETACH_SPRITE:
                    manager.detachFromSprite(id);
                    break;
                case ACTION_SHADOWS_ON:
                    manager.setShadowsEnabled(id, true);
                    break;
                case ACTION_SHADOWS_OFF:
                    manager.setShadowsEnabled(id, false);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
