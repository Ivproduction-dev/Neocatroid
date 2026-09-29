package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.Neo3DRaycaster;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class Neo3DRaycasterTest {

    private static List<Neo3DRaycaster.Target> targets(Neo3DRaycaster.Target... items) {
        List<Neo3DRaycaster.Target> list = new ArrayList<>();
        for (Neo3DRaycaster.Target item : items) {
            list.add(item);
        }
        return list;
    }

    @Test
    public void directHitReturnsSortedByDistance() {
        List<Neo3DRaycaster.Hit> hits = Neo3DRaycaster.cast(0f, 0f, 5f, 0f, 0f, -1f, 200f,
                targets(new Neo3DRaycaster.Target("far", 0f, 0f, -10f, 1f),
                        new Neo3DRaycaster.Target("near", 0f, 0f, 0f, 1f)), 50);
        assertEquals(2, hits.size());
        assertEquals("near", hits.get(0).name);
        assertEquals("far", hits.get(1).name);
    }

    @Test
    public void missAndBehindAreSkipped() {
        List<Neo3DRaycaster.Hit> hits = Neo3DRaycaster.cast(0f, 0f, 5f, 0f, 0f, -1f, 200f,
                targets(new Neo3DRaycaster.Target("side", 10f, 0f, 0f, 1f),
                        new Neo3DRaycaster.Target("behind", 0f, 0f, 10f, 1f)), 50);
        assertTrue(hits.isEmpty());
    }

    @Test
    public void maxHitsCapsAndDistanceClips() {
        List<Neo3DRaycaster.Target> many = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            many.add(new Neo3DRaycaster.Target("box" + i, 0f, 0f, -i * 2f, 0.5f));
        }
        List<Neo3DRaycaster.Hit> hits = Neo3DRaycaster.cast(0f, 0f, 5f, 0f, 0f, -1f, 200f,
                many, 3);
        assertEquals(3, hits.size());

        List<Neo3DRaycaster.Hit> clipped = Neo3DRaycaster.cast(0f, 0f, 5f, 0f, 0f, -1f, 4f,
                many, 50);
        for (Neo3DRaycaster.Hit hit : clipped) {
            assertTrue(hit.distance <= 4f);
        }
    }

    @Test
    public void degenerateInputReturnsEmpty() {
        assertTrue(Neo3DRaycaster.cast(0f, 0f, 0f, 0f, 0f, 0f, 200f,
                targets(new Neo3DRaycaster.Target("a", 0f, 0f, -5f, 1f)), 50).isEmpty());
        assertTrue(Neo3DRaycaster.cast(0f, 0f, 5f, 0f, 0f, -1f, 0f,
                targets(new Neo3DRaycaster.Target("a", 0f, 0f, -5f, 1f)), 50).isEmpty());
        assertTrue(Neo3DRaycaster.cast(0f, 0f, 5f, 0f, 0f, -1f, 200f,
                targets(new Neo3DRaycaster.Target("a", 0f, 0f, -5f, 1f)), 0).isEmpty());
    }

    @Test
    public void boundingRadiusIsStable() {
        assertEquals((float) Math.sqrt(3.0), Neo3DRaycaster.boundingRadius(1f, 1f, 1f), 1e-5f);
        assertEquals(0.05f, Neo3DRaycaster.boundingRadius(0f, 0f, 0f), 0f);
    }
}
