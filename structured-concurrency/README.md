# structured-concurrency

`Checkout.java` runs two parallel sub-tasks (stock, price) once with `StructuredTaskScope.ShutdownOnFailure` and once with a virtual-thread executor.

## Goal
Show what happens to the second task when the first one fails: a structured scope cancels it, a plain executor lets it run on.

## Run it
```
mvn -q test
mvn -q compile && java --enable-preview -cp target/classes lab.Checkout
```
Expected: `CheckoutTest` passes, and the program prints:
```
structured   failed with ExecutionException slowTaskRanToEnd=0 elapsed=100ms
unstructured failed with ExecutionException slowTaskRanToEnd=1 elapsed=503ms
```
Java 21 only: `StructuredTaskScope` is a preview API there, hence `--enable-preview` (already set in the pom for compile and tests).

## What it proves
- `stock()` fails after 50 ms; in the structured scope `price()` (500 ms) is interrupted, so `slowTaskFinished` stays 0 and the error surfaces at about 100 ms.
- With the executor, `price()` keeps running and finishes (`slowTaskFinished` is 1), and the try-with-resources `close()` waits for it, so the failure costs about 500 ms.
- Sub-task lifetime is bounded by the `try` block, so no thread outlives its parent scope.

## Trade-offs
- Preview API in 21: it changed in later releases, and running needs `--enable-preview`.
- Cancellation works through interruption; a task that ignores interrupts is not stopped.
- The timings come from `Thread.sleep` stand-ins, not real services.

## When not to use it
- Fire-and-forget background work that must outlive the request.
- Production code that cannot accept preview features.
