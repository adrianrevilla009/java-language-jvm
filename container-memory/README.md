# container-memory

`MemoryReport.java` prints the heap size and CPU count the JVM sees; a test launches it with different flags, and a Dockerfile runs it under container limits.

## Goal
Show how the JVM sizes its heap from flags and from container limits, and why `-XX:MaxRAMPercentage` is usually better than a hard-coded `-Xmx` in containers.

## Run it
```
mvn -q test
```
Expected: `MemoryFlagsTest` passes (the quiet build prints nothing). It starts child JVMs and checks that `-Xmx256m` caps the heap near 256 MB and that `-XX:MaxRAMPercentage=50` gives about twice the heap of `25`.

Container part (needs Docker):
```
docker build -t container-memory .
docker run --rm -m 512m container-memory
docker run --rm -m 1g --cpus=2 container-memory
docker rmi container-memory
```
Not run end to end: the Docker steps were not executed. The image sets `-XX:MaxRAMPercentage=60`, so the 512 MB run should report `maxHeapMB` near 307. Nothing here involves cloud services or cost.

## What it proves
- `-Xmx` is an absolute cap; `MaxRAMPercentage` scales with the memory the JVM detects, which in a container is the cgroup limit (Java 21 understands cgroup v1 and v2).
- The default is 25% of available memory, so an unconfigured 512 MB container gets about a 128 MB heap.
- Heap is not the whole footprint: metaspace, thread stacks, code cache and direct buffers sit on top, so 60-75% is a sane ceiling. The `Dockerfile` also sets `-XX:+ExitOnOutOfMemoryError`.

## Trade-offs
- The test checks flag arithmetic on the host (where `maxHeapMB` was 1716 with 16 CPUs on the author's machine), not real cgroup detection; only the Docker run exercises that.
- Percentages track the limit, so doubling the limit doubles the heap, which may not suit a latency-sensitive service.
- `ExitOnOutOfMemoryError` lets the orchestrator restart the pod but loses in-flight work.

## When not to use it
- Fixed-size VMs where `-Xmx` is already tuned and tested.
- Debugging native-memory growth: use `-XX:NativeMemoryTracking=summary` and `jcmd` instead.
