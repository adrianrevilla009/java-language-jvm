package lab;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HotSpotFinderTest {
    @Test
    void jfrPointsAtThePlantedHotSpot() throws Exception {
        Map<String, Integer> counts = HotSpotFinder.profile(2_000);
        assertFalse(counts.isEmpty(), "no execution samples recorded");
        int total = counts.values().stream().mapToInt(Integer::intValue).sum();
        int slow = counts.getOrDefault("lab.HotSpotFinder.slowTotals", 0);
        assertTrue(slow * 10 > total * 8, "slowTotals should own >80% of samples: " + counts);
    }
}
