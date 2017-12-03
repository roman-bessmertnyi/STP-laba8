package RepListis;

public class Metrics {
    private static long startTime;
    private static long stopTime;

    private Metrics() throws InstantiationException
    {
        throw new InstantiationException("Instances of this type are forbidden.");
    }

    public static boolean start() {
        startTime = System.currentTimeMillis();
        return true;
    }

    public static boolean stop() {
        stopTime = System.currentTimeMillis();
        return true;
    }

    public static void getExecutionTime() {
        long estimatedTime = stopTime - startTime;
        System.out.println("Execution time is " + estimatedTime + " ms");
    }

    public static void getUsedMemory() {
        Runtime.getRuntime().gc();
        long usedBytes = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        System.out.println("Used memory is " + convertToMegabytes(usedBytes) + " mb");
    }

    private static double convertToMegabytes(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    public static boolean getAllMetrics() {
        getExecutionTime();
        getUsedMemory();
        return true;
    }

}
