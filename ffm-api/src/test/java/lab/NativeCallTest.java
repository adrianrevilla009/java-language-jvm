package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NativeCallTest {
    @Test
    void strlenCountsBytesNotChars() throws Throwable {
        assertEquals(8, NativeCall.strlen("order-42"));
        assertEquals(2, NativeCall.strlen("é")); // UTF-8 is two bytes
    }

    @Test
    void getpidMatchesJvm() throws Throwable {
        assertEquals(ProcessHandle.current().pid(), NativeCall.pid());
    }
}
