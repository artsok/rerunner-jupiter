/*
 * (C) Copyright 2017 Artem Sokovets (http://github.com/artsok/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.artsok.extension;


import io.github.artsok.RepeatedIfExceptionsTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Stream.of;
import static java.util.stream.StreamSupport.stream;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;



/**
 * Main condition for extension point @RepeatedIfExceptions
 * All logic in this class. See TestTemplateIterator where handler logic of repeat tests
 *
 * @author Artem Sokovets
 */
@Slf4j
public class RepeatIfExceptionsCondition implements TestTemplateInvocationContextProvider, AfterTestExecutionCallback {

    private boolean exceptionAppear = false;

    private int totalRepeats = 0;

    private int minSuccess = 0;

    List<Boolean> historyExceptionAppear = Collections.synchronizedList(new ArrayList<>());

    private RepeatedIfExceptionsDisplayNameFormatter formatter;

    /**
     * Check that test method contain {@link RepeatedIfExceptionsTest} annotation
     * @param extensionContext - encapsulates the context in which the current test or container is being executed
     * @return true/false
     */
    @Override
    public boolean supportsTestTemplate(ExtensionContext extensionContext) {

        return isAnnotated(extensionContext.getTestMethod(), RepeatedIfExceptionsTest.class);
    }

    //TODO: Check input params
    private void validateParams(Optional<RepeatedIfExceptionsTest> optional) {
        optional.orElseThrow(() -> new RepeatedIfException("The extension should not be executed "
                + "unless the test class is annotated with @RepeatedIfExceptionsTest.")).repeats();
        //Проверить
    }


    /**
     * Context call TestTemplateInvocationContext
     * @param extensionContext - Test Class Context
     * @return Stream of TestTemplateInvocationContext
     */
    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
        totalRepeats = extensionContext.getTestMethod()
                .flatMap(testMethods -> findAnnotation(testMethods, RepeatedIfExceptionsTest.class))
                .orElseThrow(() -> new RepeatedIfException("The extension should not be executed "
                        + "unless the test class is annotated with @RepeatedIfExceptionsTest."))
                .repeats();
        log.debug("Total repeats '{}'", totalRepeats);

         minSuccess= extensionContext.getTestMethod()
                .flatMap(testMethods -> findAnnotation(testMethods, RepeatedIfExceptionsTest.class))
                .orElseThrow(() -> new RepeatedIfException("The extension should not be executed "
                        + "unless the test class is annotated with @RepeatedIfExceptionsTest."))
                .minSuccess();

        System.out.println("Total minSucces '{}'" + minSuccess);

        Method testMethod = Preconditions.notNull(extensionContext.getTestMethod()
                .orElse(null), "test method must not be null");
        String displayName = extensionContext.getDisplayName();
        RepeatedIfExceptionsTest repeatedIfThrowableTest =
                findAnnotation(testMethod, RepeatedIfExceptionsTest.class)
                        .orElseThrow(() -> new RepeatedIfException("Can't find annotation RepeatedIfExceptionsTest on test method"));
        formatter = displayNameFormatter(repeatedIfThrowableTest, displayName);

        //Convert our logic of repeated handler to spliterator
        Spliterator<TestTemplateInvocationContext> spliterator =
                spliteratorUnknownSize(new TestTemplateIterator(), Spliterator.NONNULL);
        return stream(spliterator, false);
    }


    /**
     * Check if exceptions that will appear in test same as we wait
     * @param extensionContext  - Test Class Context
     * @throws Exception - error if occurred
     */
    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        Class<? extends Exception>[] exceptionPool = extensionContext.getTestMethod()
                .flatMap(testMethods -> findAnnotation(testMethods, RepeatedIfExceptionsTest.class))
                .orElseThrow(() -> new IllegalStateException("The extension should not be executed "))
                .exceptions();
        log.info("Exceptions Pool in RepeatedIfExceptionsTest '{}'", exceptionPool);

        Class<? extends Throwable> exception = extensionContext.getExecutionException()
                .orElse(new RepeatedIfException("There is no exception in context")).getClass();
        log.info("Exception in test '{}'", exception);



        exceptionAppear = exceptionAppear || of(exceptionPool)
                .anyMatch(ex -> ex.isAssignableFrom(exception) && !RepeatedIfException.class.isAssignableFrom(exception));

      historyExceptionAppear.add(of(exceptionPool)
              .anyMatch(ex -> ex.isAssignableFrom(exception) && !RepeatedIfException.class.isAssignableFrom(exception)));

        System.out.println("exceptionAppear " + of(exceptionPool)
                .anyMatch(ex -> ex.isAssignableFrom(exception) && !RepeatedIfException.class.isAssignableFrom(exception)));

        System.out.println("Обработка исключения");

    }


//    @Override
//    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
//        log.info("Запуск перед тестом");
//        log.info(context.getUniqueId());
//
//        //Если два предыдущих раза тест прошел
//        System.out.println("! " + historyExceptionAppear);
//
//        //То отключаем все остальные тесты. Помечаем как skip
//        if (false) {
//            return ConditionEvaluationResult.disabled("Turn off the remaining tests that must be performed");
//        } else {
//            return ConditionEvaluationResult.enabled("");
//        }
//    }


    /**
     * Handler for display name
     * @param test - RepeatedIfExceptionsTest annotation
     * @param displayName - Name that will be represent to report
     * @return RepeatedIfExceptionsDisplayNameFormatter {@link RepeatedIfExceptionsDisplayNameFormatter}
     */
    private RepeatedIfExceptionsDisplayNameFormatter displayNameFormatter(RepeatedIfExceptionsTest test, String displayName) {
        String pattern = test.name().trim();
        if (StringUtils.isBlank(pattern)) {
            pattern = AnnotationUtils.getDefaultValue(test, "name", String.class)
                    .orElseThrow(() -> new RepeatedIfException("Exception occurred with name parameter of RepeatedIfExceptionsTest annotation"));
        }
        return new RepeatedIfExceptionsDisplayNameFormatter(pattern, displayName);
    }


    /**
     * TestTemplateIterator (Repeat test if it failed)
     */
    class TestTemplateIterator implements Iterator<TestTemplateInvocationContext> {
        int currentIndex = 0;

        @Override
        public boolean hasNext() {
            if(currentIndex == 0) {
                return true;
            }
            return exceptionAppear && currentIndex < totalRepeats ? true : false;
        }

        @Override
        public TestTemplateInvocationContext next()  {
            if(hasNext()) {
                currentIndex++;
                return new RepeatedIfExceptionsInvocationContext(currentIndex, totalRepeats,  formatter);
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
