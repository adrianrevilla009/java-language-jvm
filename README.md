# java-language-jvm

Eight small Java 21 labs on the modern language and the JVM: records and pattern matching, structured concurrency, the foreign function API, modules, an 8-to-21 upgrade, JFR profiling, GC comparison with JMH, and container memory flags. They share a tiny Orders domain where a domain is needed.

## What is inside

| Folder | What it shows | Run |
| --- | --- | --- |
| [`records-sealed-patterns`](./records-sealed-patterns) | Records, a sealed interface and exhaustive pattern-matching `switch` | `mvn -q test` |
| [`structured-concurrency`](./structured-concurrency) | `StructuredTaskScope` cancels a sibling task; a plain executor does not | `mvn -q test` |
| [`ffm-api`](./ffm-api) | Calling libc `strlen` and `getpid` through the Foreign Function & Memory API | `mvn -q test` |
| [`jpms-modules`](./jpms-modules) | Two named modules, one exported package, one hidden package | `mvn -q package` then `java -p ... -m orders.app/lab.app.Main` |
| [`java-upgrade-8-to-21`](./java-upgrade-8-to-21) | Java 8 idioms next to their Java 21 equivalents, plus a migration checklist | `mvn -q test` |
| [`jfr-profiling`](./jfr-profiling) | Recording with Java Flight Recorder and finding a planted hot spot in code | `mvn -q test` |
| [`gc-tuning-jmh`](./gc-tuning-jmh) | The same JMH benchmark under G1 and ZGC | `mvn -q package` then `java -jar target/benchmarks.jar ...` |
| [`container-memory`](./container-memory) | Heap sizing with `-Xmx` versus `-XX:MaxRAMPercentage`, plus a Docker run | `mvn -q test` |

## Prerequisites

- Java 21 (JDK) and Maven 3.8 or newer.
- `structured-concurrency` and `ffm-api` use APIs that are preview in Java 21; their poms already pass `--enable-preview`.
- Docker, only for the container part of `container-memory`.

## How to read it

Start with `records-sealed-patterns`, then `structured-concurrency` and `ffm-api` for the newer language and library features. `jfr-profiling`, `gc-tuning-jmh` and `container-memory` form the JVM runtime group and are best read in that order. Each folder is standalone and has its own README.
