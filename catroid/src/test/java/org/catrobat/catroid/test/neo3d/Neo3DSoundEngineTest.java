package org.catrobat.catroid.test.neo3d;

import org.catrobat.catroid.neo3d.Neo3DSoundEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class Neo3DSoundEngineTest {

    @Test
    public void gainsAreCenteredWithoutPan() {
        float[] gains = Neo3DSoundEngine.computeGains(1f, 0f);
        assertEquals(1f, gains[0], 1e-6f);
        assertEquals(1f, gains[1], 1e-6f);
    }

    @Test
    public void fullRightPanMutesLeft() {
        float[] gains = Neo3DSoundEngine.computeGains(1f, 1f);
        assertEquals(0f, gains[0], 1e-6f);
        assertEquals(1f, gains[1], 1e-6f);
    }

    @Test
    public void fullLeftPanMutesRight() {
        float[] gains = Neo3DSoundEngine.computeGains(1f, -1f);
        assertEquals(1f, gains[0], 1e-6f);
        assertEquals(0f, gains[1], 1e-6f);
    }

    @Test
    public void gainScalesBothChannels() {
        float[] gains = Neo3DSoundEngine.computeGains(0.5f, 0f);
        assertEquals(0.5f, gains[0], 1e-6f);
        assertEquals(0.5f, gains[1], 1e-6f);
    }

    @Test
    public void rateFromTonePercentClamps() {
        assertEquals(1f, Neo3DSoundEngine.rateFromTonePercent(100f), 0f);
        assertEquals(0.5f, Neo3DSoundEngine.rateFromTonePercent(0f), 0f);
        assertEquals(2f, Neo3DSoundEngine.rateFromTonePercent(500f), 0f);
        assertEquals(1f, Neo3DSoundEngine.rateFromTonePercent(Float.NaN), 0f);
    }

    @Test
    public void gainFromVolumePercentClamps() {
        assertEquals(1f, Neo3DSoundEngine.gainFromVolumePercent(100f), 0f);
        assertEquals(0f, Neo3DSoundEngine.gainFromVolumePercent(0f), 0f);
        assertEquals(1f, Neo3DSoundEngine.gainFromVolumePercent(250f), 0f);
    }
}
