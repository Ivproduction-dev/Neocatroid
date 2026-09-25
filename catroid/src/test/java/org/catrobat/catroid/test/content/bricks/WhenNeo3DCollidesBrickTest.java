package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenNeo3DCollidesScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.WhenNeo3DCollidesBrick;
import org.catrobat.catroid.content.eventids.Neo3DCollisionEventId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class WhenNeo3DCollidesBrickTest {

    @Test
    public void brickAndScriptAreLinked() {
        WhenNeo3DCollidesBrick brick = new WhenNeo3DCollidesBrick("box", "ball");
        Script script = brick.getScript();
        assertTrue(script instanceof WhenNeo3DCollidesScript);
        assertEquals(brick, script.getScriptBrick());
        assertEquals(brick.getObjectNameFormula(),
                ((WhenNeo3DCollidesScript) script).getFormulaMap().get(
                        org.catrobat.catroid.content.bricks.Brick.BrickField.NAME));
        assertEquals(brick.getTargetNameFormula(),
                ((WhenNeo3DCollidesScript) script).getFormulaMap().get(
                        org.catrobat.catroid.content.bricks.Brick.BrickField.TARGET));
    }

    @Test
    public void cloneKeepsFormulasAndRelinks() throws CloneNotSupportedException {
        WhenNeo3DCollidesBrick brick = new WhenNeo3DCollidesBrick("box", "ball");
        WhenNeo3DCollidesBrick clone = (WhenNeo3DCollidesBrick) brick.clone();
        assertNotSame(brick.getScript(), clone.getScript());
        assertEquals(clone, clone.getScript().getScriptBrick());
    }

    @Test
    public void eventIdEqualityIsConsistent() {
        Sprite sprite = new Sprite("host");
        WhenNeo3DCollidesBrick brick = new WhenNeo3DCollidesBrick("box", "ball");
        Neo3DCollisionEventId first =
                (Neo3DCollisionEventId) brick.getScript().createEventId(sprite);
        Neo3DCollisionEventId second =
                (Neo3DCollisionEventId) brick.getScript().createEventId(sprite);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(sprite, first.sprite);
    }

    @Test
    public void brickIsNotABackgroundOnlyBlock() {
        Brick brick = new WhenNeo3DCollidesBrick();
        assertTrue(brick instanceof WhenNeo3DCollidesBrick);
    }
}
