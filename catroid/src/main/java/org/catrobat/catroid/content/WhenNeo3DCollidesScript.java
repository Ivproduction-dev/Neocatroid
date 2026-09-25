package org.catrobat.catroid.content;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenNeo3DCollidesBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.Neo3DCollisionEventId;
import org.catrobat.catroid.formulaeditor.Formula;

public class WhenNeo3DCollidesScript extends Script {

    private static final long serialVersionUID = 1L;

    private ConcurrentFormulaHashMap formulaMap = new ConcurrentFormulaHashMap();

    public WhenNeo3DCollidesScript() {
        formulaMap.putIfAbsent(Brick.BrickField.NAME, new Formula(""));
        formulaMap.putIfAbsent(Brick.BrickField.TARGET, new Formula(""));
    }

    public WhenNeo3DCollidesScript(String objectName, String targetName) {
        this();
        formulaMap.replace(Brick.BrickField.NAME, new Formula(objectName));
        formulaMap.replace(Brick.BrickField.TARGET, new Formula(targetName));
    }

    public ConcurrentFormulaHashMap getFormulaMap() {
        return formulaMap;
    }

    @Override
    public Script clone() throws CloneNotSupportedException {
        WhenNeo3DCollidesScript clone = (WhenNeo3DCollidesScript) super.clone();
        clone.formulaMap = formulaMap.clone();
        return clone;
    }

    @Override
    public ScriptBrick getScriptBrick() {
        if (scriptBrick == null) {
            scriptBrick = new WhenNeo3DCollidesBrick(this);
        }
        return scriptBrick;
    }

    @Override
    public void addRequiredResources(final Brick.ResourcesSet requiredResourcesSet) {
        for (Formula formula : formulaMap.values()) {
            formula.addRequiredResources(requiredResourcesSet);
        }
        for (Brick brick : brickList) {
            brick.addRequiredResources(requiredResourcesSet);
        }
    }

    @Override
    public EventId createEventId(Sprite sprite) {
        WhenNeo3DCollidesBrick brick = (WhenNeo3DCollidesBrick) getScriptBrick();
        return new Neo3DCollisionEventId(sprite, brick.getObjectNameFormula(),
                brick.getTargetNameFormula());
    }
}
