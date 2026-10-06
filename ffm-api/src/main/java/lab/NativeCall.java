package lab;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;

/** Calls libc functions through the Foreign Function & Memory API (preview in Java 21). */
public class NativeCall {
    private static final Linker LINKER = Linker.nativeLinker();

    private static final MethodHandle STRLEN = LINKER.downcallHandle(
            LINKER.defaultLookup().find("strlen").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS));

    private static final MethodHandle GETPID = LINKER.downcallHandle(
            LINKER.defaultLookup().find("getpid").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.JAVA_INT));

    /** C strlen on an off-heap, NUL-terminated copy of the string; memory freed when the arena closes. */
    static long strlen(String s) throws Throwable {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cString = arena.allocateUtf8String(s);
            return (long) STRLEN.invokeExact(cString);
        }
    }

    static int pid() throws Throwable {
        return (int) GETPID.invokeExact();
    }

    public static void main(String[] args) throws Throwable {
        System.out.println("strlen(order-42) = " + strlen("order-42"));
        System.out.println("native pid == Java pid: " + (pid() == ProcessHandle.current().pid()));
    }
}
