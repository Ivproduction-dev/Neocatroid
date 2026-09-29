package org.catrobat.catroid.content.eventids;

import com.google.common.base.Objects;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

public class Neo3DTouchEventId extends EventId {
    public final Sprite sprite;
    public final Formula objectName;

    public Neo3DTouchEventId(Sprite sprite, Formula objectName) {
        this.sprite = sprite;
        this.objectName = objectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Neo3DTouchEventId)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Neo3DTouchEventId that = (Neo3DTouchEventId) o;
        return Objects.equal(sprite, that.sprite)
                && Objects.equal(objectName, that.objectName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), sprite, objectName);
    }
}
