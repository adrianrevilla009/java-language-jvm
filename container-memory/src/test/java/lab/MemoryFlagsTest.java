package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Launches child JVMs with different heap flags and reads back what they report. */
class MemoryFlagsTest {
    static long maxHeapMb(String... flags) throws Exception {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        var cmd = new java.util.ArrayList<String>(List.of(java));
        cmd.addAll(List.of(flags));
        cmd.addAll(List.of("-cp", System.getProperty("java.class.path"), "lab.MemoryReport"));
        Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes());
        assertEquals(0, p.waitFor(), out);
        return out.lines().filter(l -> l.startsWith("maxHeapMB=")).map(l -> Long.parseLong(l.substring(10))).findFirst().orElseThrow();
    }

    @Test
    void xmxSetsAnAbsoluteCap() throws Exception {
        long mb = maxHeapMb("-Xmx256m");
        assertTrue(mb >= 200 && mb <= 256, "got " + mb);
    }

    @Test
    void maxRamPercentageScalesWithMachineMemory() throws Exception {
        long quarter = maxHeapMb("-XX:MaxRAMPercentage=25");
        long half = maxHeapMb("-XX:MaxRAMPercentage=50");
        assertTrue(half > quarter * 1.7 && half < quarter * 2.3, quarter + " vs " + half);
    }
}
