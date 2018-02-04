package io.github.artsok;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Examples how to use @RepeatedIfExceptionsTest
 *
 * @author Artem Sokovets
 */
@Slf4j
class ReRunnerTest {

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    @RepeatedIfExceptionsTest(repeats = 2)
    void runTest() {
        assertTrue(true, () -> "No exception, repeat one time");
    }

    /**
     * Repeated three times if test failed.
     * By default Exception.class will be handled in test
     */

    @RepeatedIfExceptionsTest(repeats = 3)
    void reRunTest() throws IOException {
        throw new IOException("Error in Test");
    }

    /**
     * Repeated two times if test failed. Set IOException.class that will be handled in test
     * @throws IOException - error if occurred
     */
    @Disabled
    @RepeatedIfExceptionsTest(repeats = 2, exceptions = IOException.class)
    void reRunTest2() throws IOException {
        throw new IOException("Exception in I/O operation");
    }

    /**
     * Repeated ten times if test failed. Set IOException.class that will be handled in test
     * Set formatter for test. Like behavior as at {@link org.junit.jupiter.api.RepeatedTest}
     * @throws IOException - error if occurred
     */
    @Disabled
    @RepeatedIfExceptionsTest(repeats = 10, exceptions = IOException.class,
            name = "Rerun failed test. Attempt {currentRepetition} of {totalRepetitions}")
    void reRunTest3() throws IOException {
        throw new IOException("Exception in I/O operation");
    }


    @DisplayName("Name for our test")
    @RepeatedIfExceptionsTest(repeats = 2, exceptions = RuntimeException.class,
            name = "Rerun failed Test. Repetition {currentRepetition} of {totalRepetitions}")
    void reRunTest4() throws IOException {
        if(random.nextInt() % 2 == 0) { //Исключение бросается рандомно
            throw new RuntimeException("Error in Test");
        }
    }

    /**
     * Repeated 100 times with minimum success four times, then disabled all remaining repeats.
     * See image below how it works. Default exception is Exception.class
     */
    @Disabled
    @DisplayName("Test Case Name")
    @RepeatedIfExceptionsTest(repeats = 100, minSuccess = 4)
    void reRunTest5() {
        if(random.nextInt() % 2 == 0) {
            throw new RuntimeException("Error in Test");
        }
    }


    @DisplayName("Test Case Name111")
    @RepeatedIfExceptionsTest(repeats = 2, minSuccess = 1)
    void reRunTest6() {
        if(random.nextInt() % 2 == 0) {
            throw new RuntimeException("Error in Test");
        }
    }
}
