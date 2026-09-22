package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;

import java.io.File;

public class NeoLoadModelAction extends TemporalAction {
    private static final long MAX_GLB_BYTES = 32L * 1024L * 1024L;

    private Scope scope;
    private Formula objectName;
    private Formula filePath;

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
            String path = filePath != null ? filePath.interpretString(scope) : null;
            if (name == null || name.isEmpty() || path == null || path.isEmpty()) {
                return;
            }
            if (scope.getProject() == null) {
                return;
            }
            File file = scope.getProject().getFile(path);
            if (file == null || !file.isFile() || file.length() <= 0
                    || file.length() > MAX_GLB_BYTES) {
                return;
            }
            byte[] bytes = Gdx.files.absolute(file.getAbsolutePath()).readBytes();
            if (bytes == null || bytes.length == 0) {
                return;
            }
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                objectId = Neo3DFacade.facadeCreateObject(sceneId, name);
            }
            Neo3DFacade.facadeSetModelBytes(sceneId, objectId, path, bytes);
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return;
        } catch (RuntimeException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setObjectName(Formula objectName) {
        this.objectName = objectName;
    }

    public void setFilePath(Formula filePath) {
        this.filePath = filePath;
    }
}
