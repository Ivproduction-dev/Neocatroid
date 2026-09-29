package org.catrobat.catroid.content;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenNeo3DTouchBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.Neo3DTouchEventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class WhenNeo3DTouchScript extends Script {

    private static final long serialVersionUID = 1L;

    private ConcurrentFormulaHashMap formulaMap = new ConcurrentFormulaHashMap();
    private UserVariable touchedVariable;

    public WhenNeo3DTouchScript() {
        formulaMap.putIfAbsent(Brick.BrickField.NAME, new Formula(""));
        formulaMap.putIfAbsent(Brick.BrickField.DISTANCE, new Formula(1));
    }

    public ConcurrentFormulaHashMap getFormulaMap() {
        return formulaMap;
    }

    public UserVariable getTouchedVariable() {
        return touchedVariable;
    }

    public void setTouchedVariable(UserVariable variable) {
        this.touchedVariable = variable;
    }

    @Override
    public Script clone() throws CloneNotSupportedException {
        WhenNeo3DTouchScript clone = (WhenNeo3DTouchScript) super.clone();
        clone.formulaMap = formulaMap.clone();
        return clone;
    }

    @Override
    public ScriptBrick getScriptBrick() {
        if (scriptBrick == null) {
            scriptBrick = new WhenNeo3DTouchBrick(this);
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
        WhenNeo3DTouchBrick brick = (WhenNeo3DTouchBrick) getScriptBrick();
        return new Neo3DTouchEventId(sprite, brick.getObjectNameFormula());
    }
}
