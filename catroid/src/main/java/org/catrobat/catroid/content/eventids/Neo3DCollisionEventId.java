package org.catrobat.catroid.content.eventids;

import com.google.common.base.Objects;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

public class Neo3DCollisionEventId extends EventId {
    public final Sprite sprite;
    public final Formula objectName;
    public final Formula targetName;

    public Neo3DCollisionEventId(Sprite sprite, Formula objectName, Formula targetName) {
        this.sprite = sprite;
        this.objectName = objectName;
        this.targetName = targetName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Neo3DCollisionEventId)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Neo3DCollisionEventId that = (Neo3DCollisionEventId) o;
        return Objects.equal(sprite, that.sprite)
                && Objects.equal(objectName, that.objectName)
                && Objects.equal(targetName, that.targetName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), sprite, objectName, targetName);
    }
}
