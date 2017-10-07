package io.github.artsok;

import io.github.artsok.extension.RepeatedIfException;
import io.github.artsok.extension.RepeatedIfExceptionsInvocationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MockitoTest {

    @Test
    void testRepeatedIfException() {
        Throwable exception = assertThrows(RepeatedIfException.class, () -> {
            throw new RepeatedIfException("RepeatedIfException");
        });
        assertEquals("RepeatedIfException", exception.getMessage());
    }

    @Test
    void testRepeatedIfExceptionsInvocationContext() {
        RepeatedIfExceptionsInvocationContext repeatedIfExceptionsInvocationContext
                = mock(RepeatedIfExceptionsInvocationContext.class);
        when(repeatedIfExceptionsInvocationContext.getDisplayName(2))
                .thenReturn("Repetition if test failed 1 of 2");
        assertSame(repeatedIfExceptionsInvocationContext.getDisplayName(2),
                "Repetition if test failed 1 of 2", "testRepeatedIfExceptionsInvocationContext");
    }

}