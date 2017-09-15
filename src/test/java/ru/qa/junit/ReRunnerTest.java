package ru.qa.junit;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sbt-sokovets-av
 */
@Slf4j
class ReRunnerTest {

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    @RepeatedIfExceptionsTest(repeats = 3)
    void reRunTest() {
        log.debug("Запущен тестовый метод 'reRunTest()', класса '{1}'", this.getClass());
        if(random.nextInt() % 2 == 0) { //Исключение бросается рандомно
            throw new RuntimeException("Ошибочка в методе reRunTest");
        }
    }

    @RepeatedIfExceptionsTest(repeats = 5, exceptions = IOException.class)
    void reRunTest2() throws IOException {
        log.debug("Запущен тестовый метод 'reRunTest2()', класса '{1}'", this.getClass());
        throw new IOException("Проблема с записью на диск");
    }

    @RepeatedIfExceptionsTest(repeats = 5, exceptions = IOException.class,
            name = "Перезапуск теста. Попытка {currentRepetition} из {totalRepetitions}")
    void reRunTest3() throws IOException {
        log.debug("Запущен тестовый метод 'reRunTest2()', класса '{1}'", this.getClass());
        throw new IOException("Проблема с записью на диск");
    }
}
