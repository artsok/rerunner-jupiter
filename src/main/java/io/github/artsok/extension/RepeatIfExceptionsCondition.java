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
import io.github.artsok.properties.ReRunConfig;
import lombok.extern.slf4j.Slf4j;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Stream.of;
import static java.util.stream.StreamSupport.stream;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;


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
    private RepeatedIfExceptionsDisplayNameFormatter formatter;
    private ReRunConfig reRunConfig = ConfigFactory.create(ReRunConfig.class);
    static List<Boolean> historyExceptionAppear = Collections.synchronizedList(new ArrayList<>());
    static final String MINIMUM_SUCCESS_KEY = "MINIMUM_SUCCESS_KEY";

    /**
     * Check that test method contain {@link RepeatedIfExceptionsTest} annotation
     *
     * @param extensionContext - encapsulates the context in which the current test or container is being executed
     * @return true
     */
    @Override
    public boolean supportsTestTemplate(ExtensionContext extensionContext) {
        System.out.println("sadsda " + reRunConfig.enable());
        return true;
    }


    /**
     * Context call TestTemplateInvocationContext
     *
     * @param extensionContext - Test Class Context
     * @return Stream of TestTemplateInvocationContext
     */
    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
        int minSuccess;
        System.out.println("aaaaaaaaaaaaaaaaa");

        System.out.println("asd " + AnnotationUtils.isAnnotated(extensionContext.getTestMethod(), Test.class));

        Preconditions.notNull(extensionContext.getTestMethod().orElse(null), "Test method must not be null");
        RepeatedIfExceptionsTest annotationParams = extensionContext.getTestMethod()
                .flatMap(testMethods -> findAnnotation(testMethods, RepeatedIfExceptionsTest.class))
                .orElseThrow(() -> new RepeatedIfException("The extension should not be executed "
                        + "unless the test method is annotated with @RepeatedIfExceptionsTest."));


        if(annotationParams.repeats() > 1) {
            totalRepeats = annotationParams.repeats();
        } else {
            totalRepeats = reRunConfig.totalRepeats();
        }


        if(annotationParams.minSuccess() > 1) {
            minSuccess = annotationParams.minSuccess();
        } else {
          minSuccess = reRunConfig.minSuccess();
        }

        Preconditions.condition(totalRepeats > 0, "Total repeats must be higher than 0");
        Preconditions.condition(minSuccess >= 1, "Total minimum success must be higher or equals than 1");
        extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).put(MINIMUM_SUCCESS_KEY, minSuccess);
        log.debug("Total repeats '{}' and minSuccess", totalRepeats, minSuccess);

        String displayName = extensionContext.getDisplayName();
        formatter = displayNameFormatter(annotationParams, displayName);

        //Convert logic of repeated handler to spliterator
        Spliterator<TestTemplateInvocationContext> spliterator =
                spliteratorUnknownSize(new TestTemplateIterator(), Spliterator.NONNULL);
        return stream(spliterator, false);
    }


    /**
     * Check if exceptions that will appear in test same as we waiting
     *
     * @param extensionContext - Test Class Context
     * @throws Exception - error if occurred
     */
    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {

        System.out.println(reRunConfig.exceptionPool().size() + " dfsf sdf ");


        Class<? extends Exception>[] exceptionPool = (Class<? extends Exception>[]) reRunConfig.exceptionPool().toArray();



//        Class<? extends Exception>[] exceptionPool = extensionContext.getTestMethoexceptionPoold()
//                .flatMap(testMethods -> findAnnotation(testMethods, RepeatedIfExceptionsTest.class))
//                .orElseThrow(() -> new IllegalStateException("The extension should not be executed "))
//                .exceptions();


        log.info("Exceptions Pool in RepeatedIfExceptionsTest '{}'", exceptionPool);

        Class<? extends Throwable> testExecutionException = extensionContext.getExecutionException()
                .orElse(new RepeatedIfException("There is no testExecutionException in context")).getClass();


        //boolean result = exceptionPool.contains(testExecutionException.getSimpleName());

        log.debug("Exception in test '{}'", testExecutionException);
        //log.info("Result of check is actual exception contains in exceptionPool -  " + result);

        boolean result = of(exceptionPool)
                .anyMatch(ex -> ex.isAssignableFrom(testExecutionException) && !RepeatedIfException.class.isAssignableFrom(testExecutionException));
        historyExceptionAppear.add(result);
        exceptionAppear = exceptionAppear || result;
    }

    /**
     * Handler for display name
     *
     * @param test        - RepeatedIfExceptionsTest annotation
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
            if (currentIndex == 0) {
                return true;
            }
            return exceptionAppear && currentIndex < totalRepeats ? true : false;
        }

        @Override
        public TestTemplateInvocationContext next() {
            if (hasNext()) {
                currentIndex++;
                return new RepeatedIfExceptionsInvocationContext(currentIndex, totalRepeats, formatter);
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
