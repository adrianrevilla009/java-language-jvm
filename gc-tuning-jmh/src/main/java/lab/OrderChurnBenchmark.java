package lab;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Allocation churn over a retained "order book". The garbage collector is not set here:
 * choose it per run with -jvmArgsAppend (see README) so the same code is compared under G1 and ZGC.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class OrderChurnBenchmark {
    record Order(long id, String sku, int qty, byte[] payload) {}

    private final List<Order> retained = new ArrayList<>();
    private long next;

    @Setup
    public void fillOrderBook() {
        for (int i = 0; i < 200_000; i++) retained.add(newOrder());
    }

    private Order newOrder() {
        long id = next++;
        return new Order(id, "sku-" + (id % 100), (int) (id % 7), new byte[128]);
    }

    /** Short-lived garbage only. */
    @Benchmark
    public void youngGarbage(Blackhole bh) {
        for (int i = 0; i < 100; i++) bh.consume(newOrder());
    }

    /** Replaces retained orders, so some objects get promoted to old generation. */
    @Benchmark
    public void promoteSome(Blackhole bh) {
        for (int i = 0; i < 100; i++) {
            retained.set((int) (next % retained.size()), newOrder());
        }
        bh.consume(retained.get(0));
    }
}
