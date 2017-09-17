package ru.qa.junit;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Examples how to use @RepeatedIfExceptionsTest
 *
 * @author Artem Sokovets
 */
class ReRunnerTest {

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    @Disabled
    @RepeatedIfExceptionsTest(repeats = 3)
    void reRunTest() {
        if (random.nextInt() % 2 == 0) { //In different condition of ThreadLocalRandom will be threw exception
            throw new RuntimeException("Error in Test");
        }
    }

    @Disabled
    @RepeatedIfExceptionsTest(repeats = 2, exceptions = IOException.class)
    void reRunTest2() throws IOException {
        throw new IOException("Exception in I/O operation");
    }

    @Disabled
    @RepeatedIfExceptionsTest(repeats = 10, exceptions = IOException.class,
            name = "Rerun failed test. Attempt {currentRepetition} of {totalRepetitions}")
    void reRunTest3() throws IOException {
        throw new IOException("Exception in I/O operation");
    }
}
