package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;
import org.catrobat.catroid.neo3d.Neo3DSoundEngine;

public class NeoPlay3DSoundAction extends TemporalAction {
    private Scope scope;
    private SoundInfo sound;
    private Formula objectName;
    private Formula volume;
    private Formula tone;
    private Formula maxDistance;
    private int loopMode;

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
            if (sound == null || sound.getFile() == null) {
                return;
            }
            String target = objectName != null ? objectName.interpretString(scope) : null;
            float volumeValue = volume != null ? volume.interpretFloat(scope) : 100f;
            float toneValue = tone != null ? tone.interpretFloat(scope) : 100f;
            float maxDistValue = maxDistance != null ? maxDistance.interpretFloat(scope) : 250f;

            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String key = sound.getName() == null ? sound.getFile().getName() : sound.getName();
            if (target == null || target.isEmpty()) {
                float[] cameraPos = Neo3DFacade.facadeGetMainCameraPosition(sceneId);
                if (cameraPos == null) {
                    cameraPos = new float[]{0f, 0f, 0f};
                }
                Neo3DSoundEngine.play(key, sound.getFile().getAbsolutePath(), null, cameraPos,
                        volumeValue, toneValue, loopMode == 1, maxDistValue);
            } else {
                Neo3DSoundEngine.play(key, sound.getFile().getAbsolutePath(), target, null,
                        volumeValue, toneValue, loopMode == 1, maxDistValue);
            }
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setSound(SoundInfo sound) {
        this.sound = sound;
    }

    public void setObjectName(Formula objectName) {
        this.objectName = objectName;
    }

    public void setVolume(Formula volume) {
        this.volume = volume;
    }

    public void setTone(Formula tone) {
        this.tone = tone;
    }

    public void setMaxDistance(Formula maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setLoopMode(int loopMode) {
        this.loopMode = loopMode;
    }
}
