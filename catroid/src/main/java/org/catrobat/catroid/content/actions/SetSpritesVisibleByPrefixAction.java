package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;

public class SetSpritesVisibleByPrefixAction extends TemporalAction {
    private Scope scope;
    private Formula prefix;
    private boolean visible;

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
            String prefixValue = prefix != null ? prefix.interpretString(scope) : null;
            if (prefixValue == null || prefixValue.isEmpty()) {
                return;
            }
            Scene scene = ProjectManager.getInstance().getCurrentlyPlayingScene();
            if (scene == null) {
                return;
            }
            for (Sprite sprite : scene.getSpriteList()) {
                if (sprite != null && sprite.getName() != null
                        && sprite.getName().startsWith(prefixValue)
                        && sprite.look != null) {
                    sprite.look.setLookVisible(visible);
                }
            }
        } catch (InterpretationException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setPrefix(Formula prefix) {
        this.prefix = prefix;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
