package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import org.catrobat.catroid.twodlight.LightManager2D;

public class ShadowCasting2DAction extends TemporalAction {

    public static final int CAST_SHADOW = 0;
    public static final int NO_SHADOW = 1;

    private String targetSpriteName = "";
    private int mode = CAST_SHADOW;

    public void setTargetSpriteName(String targetSpriteName) {
        this.targetSpriteName = targetSpriteName;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    protected void update(float percent) {
        if (targetSpriteName == null || targetSpriteName.isEmpty()) {
            return;
        }
        LightManager2D manager = LightManager2D.forActiveStage();
        if (manager == null) {
            return;
        }
        try {
            manager.setSpriteShadowCasting(targetSpriteName, mode == CAST_SHADOW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
