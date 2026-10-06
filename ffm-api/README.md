# ffm-api

`NativeCall.java` calls the libc functions `strlen` and `getpid` from Java through the Foreign Function & Memory API.

## Goal
Show how to bind and call C functions from Java without JNI and without a C compiler.

## Run it
```
mvn -q test
mvn -q compile && java --enable-preview --enable-native-access=ALL-UNNAMED -cp target/classes lab.NativeCall
```
Expected: `NativeCallTest` passes, and the program prints:
```
strlen(order-42) = 8
native pid == Java pid: true
```
Java 21 only: `java.lang.foreign` is a preview API in 21 (final in 22), hence `--enable-preview`. It was run on Linux; other systems need their own libc symbols.

## What it proves
- `Linker.downcallHandle` with a `FunctionDescriptor` turns the libc symbol into a `MethodHandle` (`STRLEN`, `GETPID`).
- `Arena.ofConfined()` scopes the off-heap string: the memory is freed when the `try` block ends.
- C sees bytes, not Java chars: `strlen("é")` is 2 (asserted in `NativeCallTest`), and `getpid` equals `ProcessHandle.current().pid()`.

## Trade-offs
- Preview in 21: names changed in 22 (for example `allocateUtf8String` became `allocateFrom`).
- A wrong `FunctionDescriptor` can crash the JVM instead of throwing; nothing checks it against the C header.
- Windows has no `getpid` (it is `_getpid`), so the lookup would fail there.

## When not to use it
- Pure-Java problems where a library already exists.
- Code that must run on Java 17 or earlier (use JNI or JNA).
