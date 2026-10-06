package lab;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicInteger;

/** Structured scope vs plain executor when one of two sub-tasks fails (Java 21 preview API). */
public class Checkout {
    static final AtomicInteger slowTaskFinished = new AtomicInteger();

    static String stock() throws InterruptedException {
        Thread.sleep(Duration.ofMillis(50));
        throw new IllegalStateException("stock service down");
    }

    static String price() throws InterruptedException {
        Thread.sleep(Duration.ofMillis(500));
        slowTaskFinished.incrementAndGet(); // only reached if nobody cancelled us
        return "12.50";
    }

    /** Fail-fast: the failing sub-task cancels its sibling, nothing outlives the scope. */
    static String structured() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var stock = scope.fork(Checkout::stock);
            var price = scope.fork(Checkout::price);
            scope.join().throwIfFailed();
            return stock.get() + "/" + price.get();
        }
    }

    /** Executor: the failure surfaces only when get() is called, the sibling keeps running. */
    static String unstructured() throws Exception {
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> stock = ex.submit(Checkout::stock);
            Future<String> price = ex.submit(Checkout::price);
            return stock.get() + "/" + price.get(); // close() then waits for the leaked slow task
        }
    }

    public static void main(String[] args) throws Exception {
        for (var run : new String[] {"structured", "unstructured"}) {
            slowTaskFinished.set(0);
            long t0 = System.nanoTime();
            try {
                if (run.equals("structured")) structured(); else unstructured();
            } catch (Exception e) {
                System.out.printf("%-12s failed with %s slowTaskRanToEnd=%d elapsed=%dms%n", run,
                        e.getClass().getSimpleName(), slowTaskFinished.get(), (System.nanoTime() - t0) / 1_000_000);
            }
        }
    }
}
