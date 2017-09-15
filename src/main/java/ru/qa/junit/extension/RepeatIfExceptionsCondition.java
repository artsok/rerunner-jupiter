package ru.qa.junit.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.StringUtils;
import ru.qa.junit.RepeatedIfExceptionsTest;


import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Stream.of;
import static java.util.stream.StreamSupport.stream;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;


/**
 * Condition for
 *
 * @author Boni Garcia (boni.gg@gmail.com)
 * @since 1.0.0
 */
@Slf4j
public class RepeatIfExceptionsCondition implements TestTemplateInvocationContextProvider, AfterTestExecutionCallback {

    private static boolean exceptionAppear = false;

    private int totalRepeats = 0;

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

    /**
     * Предоставление вызова контекста TestTemplateInvocationContext
     * @param extensionContext - Контейнер тестов (Контекст тестового класса)
     * @return {@link Stream<TestTemplateInvocationContext>}
     */
    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
        totalRepeats = extensionContext.getTestMethod()
                .flatMap(testMethods -> findAnnotation(testMethods, RepeatedIfExceptionsTest.class))
                .orElseThrow(() -> new IllegalStateException("The extension should not be executed "
                        + "unless the test class is annotated with @RepeatedIfExceptionsTest."))
                .repeats();

        Method testMethod = Preconditions.notNull(extensionContext.getTestMethod().orElse(null), "test method must not be null");
        String displayName = extensionContext.getDisplayName();
        RepeatedIfExceptionsTest repeatedIfThrowableTest =
                findAnnotation(testMethod, RepeatedIfExceptionsTest.class).get();
        formatter = displayNameFormatter(repeatedIfThrowableTest, testMethod, displayName);

        //Создаем сплитератор, чтобы конвертнуть наш итератор в котором логика обработки повторения теста, вслучае ошибки
        Spliterator<TestTemplateInvocationContext> spliterator =
                spliteratorUnknownSize(new TestTemplateIterator(), Spliterator.NONNULL);
        return stream(spliterator, false);
    }



    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        Class<? extends Exception>[] exceptionPool = extensionContext.getTestMethod()
                .flatMap(testMethods -> findAnnotation(testMethods, RepeatedIfExceptionsTest.class))
                .orElseThrow(() -> new IllegalStateException("The extension should not be executed "))
                .exceptions();
        Class<? extends Throwable> exception = extensionContext.getExecutionException()
                .orElse(new RepeatedIfException("There is no exception in context")).getClass();
        exceptionAppear = exceptionAppear || of(exceptionPool)
                .anyMatch(ex -> ex.isAssignableFrom(exception) && !RepeatedIfException.class.isAssignableFrom(exception));

    }



    private RepeatedIfExceptionsDisplayNameFormatter displayNameFormatter(RepeatedIfExceptionsTest test,
                                                                          Method method, String displayName) {
        String pattern = test.name().trim();
        if (StringUtils.isBlank(pattern)) {
            pattern = AnnotationUtils.getDefaultValue(test, "name", String.class).get();
        }
        return new RepeatedIfExceptionsDisplayNameFormatter(pattern, displayName);
    }


    /**
     * Создаем итератор в котором обрабатывается логика репитов {Повторить тест, если была ошибка в тестовом методе}
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
        public TestTemplateInvocationContext next() {
            currentIndex++;
            return new RepeatedIfExceptionsInvocationContext(currentIndex, totalRepeats,  formatter);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
