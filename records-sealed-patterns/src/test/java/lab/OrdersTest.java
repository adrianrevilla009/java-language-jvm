package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import lab.Orders.*;
import org.junit.jupiter.api.Test;

class OrdersTest {
    @Test
    void describesEveryEventType() {
        assertEquals("o-1 shipped via DHL", Orders.describe(new Shipped("o-1", "DHL")));
        assertEquals("o-2 created empty", Orders.describe(new Created("o-2", List.of())));
        assertEquals("o-1 paid 25.00 EUR", Orders.describe(new Paid("o-1", Money.eur("25.00"))));
    }

    @Test
    void recordsValidateAndCompareByValue() {
        assertEquals(Money.eur("1.00"), Money.eur("1.00"));
        assertThrows(IllegalArgumentException.class, () -> new Money(new BigDecimal("-1"), "EUR"));
    }
}
