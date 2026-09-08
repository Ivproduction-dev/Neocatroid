package org.catrobat.catroid.twodlight;

import java.util.Set;

public interface LightRaycaster {
    float castRay(float startX, float startY, float endX, float endY, Set<String> ignoredSpriteNames,
            float[] outHitPoint);
}
