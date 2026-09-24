package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.neo3d.Neo3DFacade;
import org.catrobat.catroid.neo3d.Neo3DPhysicsBody;

public class NeoSetPhysicsStateAction extends TemporalAction {
    private Scope scope;
    private Formula objectName;
    private int motionType;
    private int shapeType;
    private Formula mass;

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
            Neo3DPhysicsBody.MotionType[] motions = Neo3DPhysicsBody.MotionType.values();
            Neo3DPhysicsBody.ShapeType[] shapes = Neo3DPhysicsBody.ShapeType.values();
            if (motionType < 0 || motionType >= motions.length
                    || shapeType < 0 || shapeType >= shapes.length) {
                return;
            }
            float massValue = mass != null ? mass.interpretFloat(scope) : 1f;

            String sceneId = Neo3DFacade.facadeEnsureDefaultScene();
            String objectId = Neo3DFacade.facadeFindObjectIdByName(sceneId, name);
            if (objectId == null) {
                return;
            }
            Neo3DFacade.facadeSetPhysicsBody(sceneId, objectId,
                    new Neo3DPhysicsBody(motions[motionType], shapes[shapeType], massValue));
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

    public void setMotionType(int motionType) {
        this.motionType = motionType;
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }

    public void setMass(Formula mass) {
        this.mass = mass;
    }
}
