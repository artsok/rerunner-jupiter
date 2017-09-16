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
@Slf4j
class ReRunnerTest {

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    @RepeatedIfExceptionsTest(repeats = 3)
    void reRunTest() {
        if(random.nextInt() % 2 == 0) { //Исключение бросается рандомно
            throw new RuntimeException("Error in Test");
        }
    }

    @Disabled
    @RepeatedIfExceptionsTest(repeats = 5, exceptions = IOException.class)
    void reRunTest2() throws IOException {
        log.debug("Запущен тестовый метод 'reRunTest2()', класса '{1}'", this.getClass());
        throw new IOException("Проблема с записью на диск");
    }

    @Disabled
    @RepeatedIfExceptionsTest(repeats = 5, exceptions = IOException.class,
            name = "Перезапуск теста. Попытка {currentRepetition} из {totalRepetitions}")
    void reRunTest3() throws IOException {
        log.debug("Запущен тестовый метод 'reRunTest2()', класса '{1}'", this.getClass());
        throw new IOException("Проблема с записью на диск");
    }
}
