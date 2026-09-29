package org.catrobat.catroid.content.eventids;

import com.google.common.base.Objects;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

public class Neo3DRayHitEventId extends EventId {
    public final Sprite sprite;
    public final Formula rayName;

    public Neo3DRayHitEventId(Sprite sprite, Formula rayName) {
        this.sprite = sprite;
        this.rayName = rayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Neo3DRayHitEventId)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Neo3DRayHitEventId that = (Neo3DRayHitEventId) o;
        return Objects.equal(sprite, that.sprite)
                && Objects.equal(rayName, that.rayName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), sprite, rayName);
    }
}
