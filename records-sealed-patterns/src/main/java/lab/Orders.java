package lab;

import java.math.BigDecimal;
import java.util.List;

/** Orders domain as records + sealed types, consumed with exhaustive pattern matching. */
public class Orders {
    public record Money(BigDecimal amount, String currency) {
        public Money {
            if (amount.signum() < 0) throw new IllegalArgumentException("negative amount");
        }

        static Money eur(String v) {
            return new Money(new BigDecimal(v), "EUR");
        }
    }

    public record Line(String sku, int qty, Money unitPrice) {}

    public sealed interface OrderEvent permits Created, Paid, Shipped, Cancelled {}

    public record Created(String id, List<Line> lines) implements OrderEvent {}

    public record Paid(String id, Money total) implements OrderEvent {}

    public record Shipped(String id, String carrier) implements OrderEvent {}

    public record Cancelled(String id, String reason) implements OrderEvent {}

    /** No default branch: adding a permitted subtype breaks compilation here. */
    static String describe(OrderEvent e) {
        return switch (e) {
            case Created(var id, var lines) when lines.isEmpty() -> id + " created empty";
            case Created(var id, var lines) -> id + " created with " + lines.size() + " line(s)";
            case Paid(var id, Money(var amount, var cur)) -> id + " paid " + amount + " " + cur;
            case Shipped(var id, var carrier) -> id + " shipped via " + carrier;
            case Cancelled(var id, var reason) -> id + " cancelled: " + reason;
        };
    }

    public static void main(String[] args) {
        List<OrderEvent> events = List.of(
                new Created("o-1", List.of(new Line("book", 2, Money.eur("12.50")))),
                new Created("o-2", List.of()),
                new Paid("o-1", Money.eur("25.00")),
                new Shipped("o-1", "DHL"),
                new Cancelled("o-2", "empty"));
        events.stream().map(Orders::describe).forEach(System.out::println);
    }
}
