package lab;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

/** Records a JFR profile of a workload with a deliberate hot spot, then finds it from the samples. */
public class HotSpotFinder {

    /** The planted hot spot: naive recursive Fibonacci (exponential CPU, no allocation). */
    static long slowTotals(int n) {
        return n < 2 ? n : slowTotals(n - 1) + slowTotals(n - 2);
    }

    /** Cheap work that should barely show up. */
    static long cheapTotals(int n) {
        long sum = 0;
        for (int i = 0; i < n; i++) sum += i;
        return sum;
    }

    static void workload(long millis) {
        long end = System.nanoTime() + millis * 1_000_000;
        long sink = 0;
        while (System.nanoTime() < end) {
            sink += slowTotals(27) + cheapTotals(1_000);
        }
        if (sink == 42) System.out.println(); // keep the JIT from removing the work
    }

    /** Returns method -> number of ExecutionSample events whose top frame is that method. */
    static Map<String, Integer> topFrames(Path jfr) throws Exception {
        Map<String, Integer> counts = new HashMap<>();
        try (RecordingFile file = new RecordingFile(jfr)) {
            while (file.hasMoreEvents()) {
                RecordedEvent e = file.readEvent();
                if (!e.getEventType().getName().equals("jdk.ExecutionSample") || e.getStackTrace() == null) continue;
                var top = e.getStackTrace().getFrames().get(0).getMethod();
                counts.merge(top.getType().getName() + "." + top.getName(), 1, Integer::sum);
            }
        }
        return counts;
    }

    static Map<String, Integer> profile(long millis) throws Exception {
        Path out = Files.createTempFile("lab", ".jfr");
        try (Recording rec = new Recording()) {
            rec.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(10));
            rec.start();
            workload(millis);
            rec.stop();
            rec.dump(out);
        }
        try {
            return topFrames(out);
        } finally {
            Files.deleteIfExists(out);
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, Integer> counts = profile(2_000);
        counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(en -> System.out.println(en.getValue() + " samples  " + en.getKey()));
    }
}
