# gc-tuning-jmh

`OrderChurnBenchmark.java` is a JMH benchmark that allocates Order records, run under G1 and ZGC by changing only a JVM flag.

## Goal
Run the same benchmark under G1 and ZGC to see how the collector changes allocation-heavy code, without touching the benchmark source.

## Run it
```
mvn -q package
java -jar target/benchmarks.jar -f 1 -wi 1 -w 1s -i 2 -r 1s -jvmArgsAppend "-XX:+UseG1GC -Xmx512m"
java -jar target/benchmarks.jar -f 1 -wi 1 -w 1s -i 2 -r 1s -jvmArgsAppend "-XX:+UseZGC -Xmx512m"
```
Expected: each run takes about 30 s and prints two rows (`youngGarbage`, `promoteSome`) in us/op. One run on a WSL2 machine gave:
```
G1:  promoteSome 13.887 us/op   youngGarbage 4.115 us/op
ZGC: promoteSome  6.628 us/op   youngGarbage 7.105 us/op
```
For numbers worth quoting use the defaults (`-f 3 -wi 5 -i 5`) on a quiet machine, and add `-prof gc` for allocation rate and GC time.

## What it proves
- The collector is a JVM flag, not a code change: JMH forks a JVM per run and `-jvmArgsAppend` selects it.
- `youngGarbage` measures short-lived allocation; `promoteSome` overwrites entries in a 200,000-order book so objects survive into the old generation.
- In the run above ZGC was slower on `youngGarbage` and faster on `promoteSome`. With two short iterations this is illustrative only: read it as "the collector matters per workload", not as a ranking.

## Trade-offs
- Average-time benchmarks favour throughput collectors; ZGC targets low pauses, which this does not measure. Use `-Xlog:gc` or JFR for latency questions.
- With 2 iterations the error column is empty, because JMH needs 3 or more to report it, and run-to-run noise is large.
- Heap size changes results as much as the collector does; fix `-Xmx` when comparing.

## When not to use it
- Choosing a production GC: test the real service under real load instead of a microbenchmark.
- Heaps and workloads where the default G1 already meets the latency target.
