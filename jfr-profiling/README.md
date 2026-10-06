# jfr-profiling

`HotSpotFinder.java` records a Java Flight Recorder profile of a workload with a planted hot spot, then reads the recording to find it.

## Goal
Show how to start a JFR recording from code and read its execution samples to see where CPU time goes.

## Run it
```
mvn -q test
mvn -q compile && java -cp target/classes lab.HotSpotFinder
```
Expected: `HotSpotFinderTest` passes, and the program prints sample counts per top frame, for example (counts vary per run):
```
132 samples  lab.HotSpotFinder.slowTotals
1 samples  lab.HotSpotFinder.cheapTotals
```
The same data can be captured from the command line with `java -XX:StartFlightRecording=filename=app.jfr,duration=30s ...` and read with `jfr print --events jdk.ExecutionSample app.jfr` or `jfr summary app.jfr`; those commands were not run here.

## What it proves
- `jdk.jfr.Recording` enables `jdk.ExecutionSample` at a 10 ms period, in process, with no agent.
- `RecordingFile` exposes each sample's stack, so counting top frames shows where CPU goes.
- The naive recursive Fibonacci `slowTotals` dominates the 2-second workload; the test asserts it owns more than 80% of top-frame samples.

## Trade-offs
- Sampling is statistical: short runs give noisy counts, so the test asserts a loose ratio on purpose.
- JIT inlining can attribute samples to another frame than expected; read the full stack, not only the top frame.
- `jdk.ExecutionSample` covers Java frames only; time in native code needs `jdk.NativeMethodSample`.

## When not to use it
- Distributed latency questions (use tracing).
- Micro-level comparisons between two implementations (use JMH, see `gc-tuning-jmh`).
