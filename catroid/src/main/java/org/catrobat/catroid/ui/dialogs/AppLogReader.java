package org.catrobat.catroid.ui.dialogs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class AppLogReader {

    private static final int PROCESS_TIMEOUT_SECONDS = 5;
    private static final int MAX_RAW_LINES = 4000;

    private AppLogReader() {
    }

    public static final class LogResult {
        public final List<String> lines;
        public final String error;

        LogResult(List<String> lines, String error) {
            this.lines = lines;
            this.error = error;
        }

        public boolean isOk() {
            return error == null;
        }
    }

    public static LogResult readAppErrors(int maxLines) {
        int pid = android.os.Process.myPid();
        try {
            List<String> lines = exec("logcat", "-d", "-v", "threadtime", "--pid=" + pid, "*:E");
            return new LogResult(takeLast(lines, maxLines), null);
        } catch (Exception pidFailed) {
            try {
                List<String> lines = exec("logcat", "-d", "-v", "threadtime", "*:E");
                return new LogResult(filterThreadtimeLinesForPid(lines, pid, maxLines), null);
            } catch (Exception fallbackFailed) {
                return new LogResult(new ArrayList<String>(), String.valueOf(fallbackFailed.getMessage()));
            }
        }
    }

    public static List<String> filterThreadtimeLinesForPid(List<String> lines, int pid, int maxLines) {
        String pidField = " " + pid + " ";
        List<String> matched = new ArrayList<>();
        if (lines != null) {
            for (String line : lines) {
                if (line != null && line.contains(pidField)) {
                    matched.add(line);
                }
            }
        }
        return takeLast(matched, maxLines);
    }

    private static List<String> takeLast(List<String> lines, int maxLines) {
        if (lines.size() <= maxLines) {
            return lines;
        }
        return new ArrayList<>(lines.subList(lines.size() - maxLines, lines.size()));
    }

    private static List<String> exec(String... command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                    if (lines.size() >= MAX_RAW_LINES) {
                        break;
                    }
                }
            } finally {
                try {
                    reader.close();
                } catch (Exception ignored) {
                }
            }
            if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroy();
                throw new Exception("logcat timed out");
            }
            return lines;
        } finally {
            process.destroy();
        }
    }
}
