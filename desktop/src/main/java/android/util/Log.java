package android.util;

public class Log {
    public static int d(String tag, String message) {
        System.out.println("D/" + tag + ": " + message);
        return 0;
    }

    public static int i(String tag, String message) {
        System.out.println("I/" + tag + ": " + message);
        return 0;
    }

    public static int w(String tag, String message) {
        System.out.println("W/" + tag + ": " + message);
        return 0;
    }

    public static int e(String tag, String message) {
        System.out.println("E/" + tag + ": " + message);
        return 0;
    }

    public static int e(String tag, String message, Throwable throwable) {
        System.out.println("E/" + tag + ": " + message);
        throwable.printStackTrace(System.out);
        return 0;
    }

    public static String getStackTraceString(Throwable throwable) {
        java.io.StringWriter writer = new java.io.StringWriter();
        throwable.printStackTrace(new java.io.PrintWriter(writer));
        return writer.toString();
    }
}
