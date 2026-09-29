package org.catrobat.catroid.content;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenNeo3DRayHitBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.Neo3DRayHitEventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.List;

public class WhenNeo3DRayHitScript extends Script {

    private static final long serialVersionUID = 1L;

    private ConcurrentFormulaHashMap formulaMap = new ConcurrentFormulaHashMap();
    private List<UserVariable> hitVariables = new ArrayList<>();
    private int visibleHits = 1;

    public WhenNeo3DRayHitScript() {
        formulaMap.putIfAbsent(Brick.BrickField.NAME, new Formula(""));
    }

    public ConcurrentFormulaHashMap getFormulaMap() {
        return formulaMap;
    }

    public UserVariable getVariable(int index) {
        if (index < 0 || index >= hitVariables.size()) {
            return null;
        }
        return hitVariables.get(index);
    }

    public void setVariable(int index, UserVariable variable) {
        if (index < 0) {
            return;
        }
        while (hitVariables.size() <= index) {
            hitVariables.add(null);
        }
        hitVariables.set(index, variable);
    }

    public int getVisibleHits() {
        return visibleHits;
    }

    public void setVisibleHits(int visibleHits) {
        this.visibleHits = visibleHits;
    }

    public List<UserVariable> getVariables() {
        List<UserVariable> variables = new ArrayList<>();
        for (int i = 0; i < visibleHits; i++) {
            variables.add(getVariable(i));
        }
        return variables;
    }

    @Override
    public Script clone() throws CloneNotSupportedException {
        WhenNeo3DRayHitScript clone = (WhenNeo3DRayHitScript) super.clone();
        clone.formulaMap = formulaMap.clone();
        clone.hitVariables = new ArrayList<>(hitVariables);
        return clone;
    }

    @Override
    public ScriptBrick getScriptBrick() {
        if (scriptBrick == null) {
            scriptBrick = new WhenNeo3DRayHitBrick(this);
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
        WhenNeo3DRayHitBrick brick = (WhenNeo3DRayHitBrick) getScriptBrick();
        return new Neo3DRayHitEventId(sprite, brick.getRayNameFormula());
    }
}
