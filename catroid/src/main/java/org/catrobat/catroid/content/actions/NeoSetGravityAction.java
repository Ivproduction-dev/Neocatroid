package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

public class NeoSetGravityAction extends TemporalAction {
    private Scope scope;
    private Formula gravityX;
    private Formula gravityY;
    private Formula gravityZ;

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
            float x = gravityX != null ? gravityX.interpretFloat(scope) : 0f;
            float y = gravityY != null ? gravityY.interpretFloat(scope) : -9.81f;
            float z = gravityZ != null ? gravityZ.interpretFloat(scope) : 0f;

            Neo3DFacade.facadeSetGravity(x, y, z);
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setGravityX(Formula gravityX) {
        this.gravityX = gravityX;
    }

    public void setGravityY(Formula gravityY) {
        this.gravityY = gravityY;
    }

    public void setGravityZ(Formula gravityZ) {
        this.gravityZ = gravityZ;
    }
}
