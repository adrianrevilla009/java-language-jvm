package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class UpgradeParityTest {
    @Test
    void sameBehaviourOldAndNew() {
        for (Object status : new Object[] {"PAID", "SHIPPED", "NEW", 42, null}) {
            assertEquals(Legacy8.label(status), Modern21.label(status), String.valueOf(status));
        }
        assertEquals(Legacy8.skus(), Modern21.skus());
        assertEquals(Legacy8.report(Legacy8.skus()), Modern21.report(Modern21.skus()));
        assertEquals(Legacy8.firstOrEmpty(List.of()), Modern21.firstOrEmpty(List.of()));
        assertEquals(Legacy8.firstOrEmpty(List.of("a")), Modern21.firstOrEmpty(List.of("a")));
    }

    @Test
    void bothListsAreImmutable() {
        assertThrows(UnsupportedOperationException.class, () -> Legacy8.skus().add("x"));
        assertThrows(UnsupportedOperationException.class, () -> Modern21.skus().add("x"));
    }
}
