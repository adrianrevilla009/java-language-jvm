package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class CheckoutTest {
    @Test
    void structuredScopeCancelsSibling() {
        Checkout.slowTaskFinished.set(0);
        assertThrows(ExecutionException.class, Checkout::structured);
        assertEquals(0, Checkout.slowTaskFinished.get());
    }

    @Test
    void executorLeavesSiblingRunning() {
        Checkout.slowTaskFinished.set(0);
        assertThrows(ExecutionException.class, Checkout::unstructured);
        assertEquals(1, Checkout.slowTaskFinished.get());
    }
}
