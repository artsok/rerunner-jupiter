package io.github.artsok;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    @Disabled
    @DisplayName("All required all succeed")
    @RepeatedIfExceptionsTest(repeats = 105, exceptions = RuntimeException.class,
            name = "Rerun failed Test. Repetition {currentRepetition} of {totalRepetitions}")
    void reRunTest4() throws IOException {
        if(random.nextInt() % 2 == 0) { //Исключение бросается рандомно
            throw new RuntimeException("Error in Test");
        }
    }

    @Disabled
    @Test
    void test1() {
        List<Integer> a = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        boolean b1 = a.stream().skip(a.size() - 4).allMatch(b -> Objects.equals(b, b));
        System.out.println(b1);
    }



    @RepeatedIfExceptionsTest(repeats = 100, minSuccess = 4) //Т.е если два раза прошли тесты, остальные репиты мы отключаем
    void reRunTest5() {
        System.out.println("Я запустил тест " + 5 + random.nextInt());
        if(random.nextInt() % 2 == 0) { //Исключение бросается рандомно
            throw new RuntimeException("Error in Test");
        }
        //1. Проходит
        //2. Проходит
        //3. Отключаем остальные тесты
    }


    @Disabled
    @RepeatedIfExceptionsTest(repeats = 2)
    void runTest() {
        assertTrue(true, () -> "No exception, repeat one time");
    }

    /**
     * Repeated three times if test failed.
     * By default Exception.class will be handled in test
     */
    @Disabled
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
}
