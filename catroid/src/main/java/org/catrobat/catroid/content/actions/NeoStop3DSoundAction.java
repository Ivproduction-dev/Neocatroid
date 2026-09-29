package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.neo3d.Neo3DSoundEngine;

public class NeoStop3DSoundAction extends TemporalAction {
    private Scope scope;
    private SoundInfo sound;

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
        started = true;

        if (sound == null) {
            return;
        }
        String key = sound.getName() == null
                ? (sound.getFile() == null ? null : sound.getFile().getName())
                : sound.getName();
        if (key == null || key.isEmpty()) {
            return;
        }
        Neo3DSoundEngine.stop(key);
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setSound(SoundInfo sound) {
        this.sound = sound;
    }
}
