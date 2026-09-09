package org.catrobat.catroid.test.ui.dialogs;

import org.catrobat.catroid.ui.dialogs.AppLogReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class AppLogReaderTest {

    private static final int PID = 7172;

    private static String threadtimeLine(int pid, String tag, String message) {
        return "09-09 10:00:00.000 " + pid + " " + pid + " E " + tag + ": " + message;
    }

    @Test
    public void keepsOnlyMatchingPidLines() {
        List<String> input = Arrays.asList(
                threadtimeLine(PID, "Lightmap2D", "boom"),
                threadtimeLine(9999, "OtherApp", "noise"),
                threadtimeLine(PID, "Light2D", "pass active"));

        List<String> result = AppLogReader.filterThreadtimeLinesForPid(input, PID, 100);

        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("boom"));
        assertTrue(result.get(1).contains("pass active"));
    }

    @Test
    public void capsResultToMaxLinesFromTail() {
        List<String> input = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            input.add(threadtimeLine(PID, "TAG", "line" + i));
        }

        List<String> result = AppLogReader.filterThreadtimeLinesForPid(input, PID, 3);

        assertEquals(3, result.size());
        assertTrue(result.get(0).contains("line7"));
        assertTrue(result.get(2).contains("line9"));
    }

    @Test
    public void handlesNullAndEmptyInput() {
        assertTrue(AppLogReader.filterThreadtimeLinesForPid(null, PID, 10).isEmpty());
        assertTrue(AppLogReader.filterThreadtimeLinesForPid(new ArrayList<String>(), PID, 10).isEmpty());

        List<String> withNulls = Arrays.asList(null, threadtimeLine(PID, "TAG", "ok"), null);
        List<String> result = AppLogReader.filterThreadtimeLinesForPid(withNulls, PID, 10);
        assertEquals(1, result.size());
    }
}
