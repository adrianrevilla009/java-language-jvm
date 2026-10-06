# records-sealed-patterns

Orders events modelled as records under a sealed interface, in `Orders.java`, consumed with an exhaustive pattern-matching `switch`.

## Goal
Model Orders events with records and a sealed interface, and consume them with a `switch` that needs no `default` (record deconstruction, guards, nested patterns).

## Run it
```
mvn -q test
java -cp target/classes lab.Orders
```
Expected: `OrdersTest` passes (the quiet build prints nothing), and `lab.Orders` prints one line per event:
```
o-1 created with 1 line(s)
o-2 created empty
o-1 paid 25.00 EUR
o-1 shipped via DHL
o-2 cancelled: empty
```
Needs Java 21 and Maven.

## What it proves
- `describe` in `Orders.java` switches over `OrderEvent` (four permitted records) with no `default`; adding a fifth permitted type makes that switch fail to compile.
- The `Money` compact constructor rejects a negative amount, and records compare by value (both checked in `OrdersTest`).
- The nested pattern `Paid(var id, Money(var amount, var cur))` and the `when lines.isEmpty()` guard replace getter chains and `if` blocks.

## Trade-offs
- A sealed hierarchy must be declared up front in one module or package, so it suits closed domains, not plugin points.
- Records are shallowly immutable: the `List<Line>` in `Created` can still be mutated unless the caller copies it.
- `Money` uses `BigDecimal` equality, so `1.0` and `1.00` are not equal.

## When not to use it
- Open-ended types that third parties must extend.
- JPA entities: records have no no-arg constructor and cannot be proxied.
