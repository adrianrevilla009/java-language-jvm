package lab;

/** Prints what the JVM believes about its limits; run it with different memory flags or in a container. */
public class MemoryReport {
    public static void main(String[] args) {
        Runtime rt = Runtime.getRuntime();
        System.out.println("maxHeapMB=" + rt.maxMemory() / (1024 * 1024));
        System.out.println("cpus=" + rt.availableProcessors());
    }
}
