package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNeo3DCollidesScript;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class WhenNeo3DCollidesBrick extends FormulaBrick implements ScriptBrick {

    private static final long serialVersionUID = 1L;

    private WhenNeo3DCollidesScript script;

    public WhenNeo3DCollidesBrick() {
        this(new WhenNeo3DCollidesScript());
    }

    public WhenNeo3DCollidesBrick(WhenNeo3DCollidesScript script) {
        addAllowedBrickField(BrickField.NAME, R.id.brick_when_neo3d_collides_edit_name);
        addAllowedBrickField(BrickField.TARGET, R.id.brick_when_neo3d_collides_edit_target);
        script.setScriptBrick(this);
        commentedOut = script.isCommentedOut();
        this.script = script;

        formulaMap = script.getFormulaMap();
    }

    public WhenNeo3DCollidesBrick(String objectName, String targetName) {
        this(new WhenNeo3DCollidesScript(objectName, targetName));
    }

    @Override
    public Brick clone() throws CloneNotSupportedException {
        WhenNeo3DCollidesBrick clone = (WhenNeo3DCollidesBrick) super.clone();
        clone.script = (WhenNeo3DCollidesScript) script.clone();
        clone.script.setScriptBrick(clone);
        clone.formulaMap = clone.script.getFormulaMap();
        return clone;
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_when_neo3d_collides;
    }

    public Formula getObjectNameFormula() {
        return getFormulaWithBrickField(BrickField.NAME);
    }

    public Formula getTargetNameFormula() {
        return getFormulaWithBrickField(BrickField.TARGET);
    }

    @Override
    public Script getScript() {
        return script;
    }

    @Override
    public int getPositionInScript() {
        return -1;
    }

    @Override
    public void addToFlatList(List<Brick> bricks) {
        super.addToFlatList(bricks);
        for (Brick brick : getScript().getBrickList()) {
            brick.addToFlatList(bricks);
        }
    }

    @Override
    public List<Brick> getDragAndDropTargetList() {
        return getScript().getBrickList();
    }

    @Override
    public int getPositionInDragAndDropTargetList() {
        return -1;
    }

    @Override
    public void setCommentedOut(boolean commentedOut) {
        super.setCommentedOut(commentedOut);
        getScript().setCommentedOut(commentedOut);
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
    }
}
