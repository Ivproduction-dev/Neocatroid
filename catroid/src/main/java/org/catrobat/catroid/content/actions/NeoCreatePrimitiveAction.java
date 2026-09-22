package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;
import org.catrobat.catroid.neo3d.Neo3DPrimitiveMeshes;

public class NeoCreatePrimitiveAction extends TemporalAction {
    public static final int PRIMITIVE_CUBE = 0;
    public static final int PRIMITIVE_SPHERE = 1;
    public static final int PRIMITIVE_CYLINDER = 2;

    private Scope scope;
    private Formula objectName;
    private int primitiveKind = PRIMITIVE_CUBE;

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
            if (name == null || name.isEmpty()) {
                return;
            }
            byte[] bytes;
            String assetKey;
            switch (primitiveKind) {
                case PRIMITIVE_SPHERE:
                    bytes = Neo3DPrimitiveMeshes.buildSphere(1f, 12, 24,
                            new float[]{0.85f, 0.55f, 0.25f, 1f});
                    assetKey = Neo3DPrimitiveMeshes.ASSET_KEY_SPHERE;
                    break;
                case PRIMITIVE_CYLINDER:
                    bytes = Neo3DPrimitiveMeshes.buildCylinder(1f, 2f, 24,
                            new float[]{0.25f, 0.65f, 0.6f, 1f});
                    assetKey = Neo3DPrimitiveMeshes.ASSET_KEY_CYLINDER;
                    break;
                case PRIMITIVE_CUBE:
                default:
                    bytes = Neo3DPrimitiveMeshes.buildCube(2f,
                            new float[]{0.75f, 0.78f, 0.82f, 1f});
                    assetKey = Neo3DPrimitiveMeshes.ASSET_KEY_CUBE;
                    break;
            }
            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeCreateObject(sceneId, name);
            Neo3DFacade.facadeSetModelBytes(sceneId, objectId, assetKey, bytes);
        } catch (InterpretationException | IllegalStateException | IllegalArgumentException e) {
            return;
        }
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public void setObjectName(Formula objectName) {
        this.objectName = objectName;
    }

    public void setPrimitiveKind(int primitiveKind) {
        this.primitiveKind = primitiveKind;
    }
}
