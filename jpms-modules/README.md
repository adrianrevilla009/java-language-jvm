# jpms-modules

A Maven multi-module build with two named Java modules: `orders-core` (exports `lab.orders.api`) and `orders-app` (requires it).

## Goal
Show a module boundary: `orders.core` exports only its API package, `orders.app` uses it, and the internal package stays unreachable, even through reflection.

## Run it
```
mvn -q package
java -p orders-core/target/orders-core-1.0.0.jar:orders-app/target/orders-app-1.0.0.jar -m orders.app/lab.app.Main
```
Expected output:
```
gross(10000) = 12100
module = orders.core (named: true)
api exported to app: true
internal exported to app: false
reflection blocked: IllegalAccessException
```
`Main` exits with status 1 if reflection into the internal package is allowed. Needs Java 21 and Maven.

## What it proves
- `exports lab.orders.api` in `orders-core/src/main/java/module-info.java` makes `PriceService` usable, while `lab.orders.internal` (`TaxTable`) is not exported: `isExported` is false and reflective access throws `IllegalAccessException`.
- Both jars are real named modules, and the app starts only when the module graph resolves from the module path.
- Adding `import lab.orders.internal.TaxTable;` to `orders-app` should fail compilation with a "package is not visible" error. That was not run here; it is easy to try by hand.

## Trade-offs
- Strong encapsulation costs ceremony: every dependency must be a module or an automatic module, and reflection-heavy frameworks need `opens`.
- The compile-time failure is described, not automated.
- There are no tests; the check is the run output above.

## When not to use it
- Applications built on Spring or Hibernate-style reflection, where classpath mode is simpler.
- Small single-jar services with no internal API worth hiding.
