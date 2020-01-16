package io.github.artsok.extension;

import io.github.artsok.ParameterizedRepeatedIfExceptionsTest;
import io.github.artsok.internal.RepeatedIfException;
import io.github.artsok.internal.ParameterizedRepeatedIfExceptionsTestNameFormatter;
import io.github.artsok.internal.ParameterizedRepeatedMethodContext;
import io.github.artsok.internal.ParameterizedTestInvocationContext;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.AnnotationConsumerInitializer;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.ExceptionUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;
import org.opentest4j.TestAbortedException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.toIntExact;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;
import static org.junit.platform.commons.util.AnnotationUtils.*;

/**
 * Extension for {@link ParameterizedRepeatedIfExceptionsTest}
 */
public class ParameterizedRepeatedExtension implements TestTemplateInvocationContextProvider,
        BeforeTestExecutionCallback, AfterTestExecutionCallback, TestExecutionExceptionHandler {

    private int totalRepeats = 0;
    private int minSuccess = 1;
    private List<Class<? extends Throwable>> repeatableExceptions;
    private boolean repeatableExceptionAppeared = false;
    private final List<Boolean> historyExceptionAppear = Collections.synchronizedList(new ArrayList<>());
    private static final String METHOD_CONTEXT_KEY = "context";
    private long suspend = 0L;

    @Override
    public boolean supportsTestTemplate(ExtensionContext extensionContext) {
        if (!extensionContext.getTestMethod().isPresent()) {
            return false;
        }

        Method testMethod = extensionContext.getTestMethod().get();
        if (!isAnnotated(testMethod, ParameterizedRepeatedIfExceptionsTest.class)) {
            return false;
        }

        ParameterizedRepeatedMethodContext methodContext = new ParameterizedRepeatedMethodContext(testMethod);

        Preconditions.condition(methodContext.hasPotentiallyValidSignature(),
                () -> String.format(
                        "@ParameterizedRepeatedIfExceptionsTest method [%s] declares formal parameters in an invalid order: "
                                + "argument aggregators must be declared after any indexed arguments "
                                + "and before any arguments resolved by another ParameterResolver.",
                        testMethod.toGenericString()));

        getStore(extensionContext).put(METHOD_CONTEXT_KEY, methodContext);
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
        Method templateMethod = extensionContext.getRequiredTestMethod();
        String displayName = extensionContext.getDisplayName();
        ParameterizedRepeatedMethodContext methodContext = getStore(extensionContext)//
                .get(METHOD_CONTEXT_KEY, ParameterizedRepeatedMethodContext.class);
        ParameterizedRepeatedIfExceptionsTestNameFormatter formatter = createNameFormatter(templateMethod, displayName);

        ParameterizedRepeatedIfExceptionsTest annotationParams = extensionContext.getTestMethod()
                .flatMap(testMethods -> findAnnotation(testMethods, ParameterizedRepeatedIfExceptionsTest.class))
                .orElseThrow(() -> new RepeatedIfException("The extension should not be executed "
                        + "unless the test method is annotated with @ParameterizedRepeatedIfExceptionsTest."));

        totalRepeats = annotationParams.repeats();
        minSuccess = annotationParams.minSuccess();
        suspend = annotationParams.suspend();

        Preconditions.condition(totalRepeats > 0, "Total repeats must be higher than 0");
        Preconditions.condition(minSuccess >= 1, "Total minimum success must be higher or equals than 1");


        List<Object[]> collect = findRepeatableAnnotations(templateMethod, ArgumentsSource.class)
                .stream()
                .map(ArgumentsSource::value)
                .map(this::instantiateArgumentsProvider)
                .map(provider -> AnnotationConsumerInitializer.initialize(templateMethod, provider))
                .flatMap(provider -> arguments(provider, extensionContext))
                .map(Arguments::get)
                .map(arguments -> consumedArguments(arguments, methodContext))
                .collect(Collectors.toList());

        Spliterator<TestTemplateInvocationContext> spliterator =
                spliteratorUnknownSize(new TestTemplateIteratorParams(collect, formatter, methodContext), Spliterator.NONNULL);
        return stream(spliterator, false);

    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        repeatableExceptions = Stream.of(context.getTestMethod()
                .flatMap(testMethods -> findAnnotation(testMethods, ParameterizedRepeatedIfExceptionsTest.class))
                .orElseThrow(() -> new IllegalStateException("The extension should not be executed "))
                .exceptions()
        ).collect(Collectors.toList());
        repeatableExceptions.add(TestAbortedException.class);
    }

    //Записываем в historyExceptionAppear по конкретным аргументам!
    @Override
    public void afterTestExecution(ExtensionContext context) {
        boolean exceptionAppeared = exceptionAppeared(context);
        historyExceptionAppear.add(exceptionAppeared);
    }

    private boolean exceptionAppeared(ExtensionContext extensionContext) {
        if (extensionContext.getExecutionException().isPresent()) {
            Class<? extends Throwable> exception = extensionContext.getExecutionException().get().getClass();
            return repeatableExceptions.stream().anyMatch(ex -> ex.isAssignableFrom(exception));
        }
        return false;
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if (appearedExceptionDoesNotAllowRepetitions(throwable)) {
            throw throwable;
        }
        repeatableExceptionAppeared = true;
        throw new TestAbortedException("Do not fail completely, but repeat the test", throwable);
    }

    private boolean appearedExceptionDoesNotAllowRepetitions(Throwable appearedException) {
        return repeatableExceptions.stream().noneMatch(ex -> ex.isAssignableFrom(appearedException.getClass()));
    }

    private ParameterizedRepeatedIfExceptionsTestNameFormatter createNameFormatter(Method templateMethod, String displayName) {
        ParameterizedRepeatedIfExceptionsTest parameterizedTest = findAnnotation(templateMethod, ParameterizedRepeatedIfExceptionsTest.class).get();
        String pattern = Preconditions.notBlank(parameterizedTest.name().trim(),
                () -> String.format(
                        "Configuration error: @ParameterizedRepeatedIfExceptionsTest on method [%s] must be declared with a non-empty name.",
                        templateMethod));

        String repeatedPattern = Preconditions.notBlank(parameterizedTest.repeatedName(), () -> String.format(
                "Configuration error: @ParameterizedRepeatedIfExceptionsTest on method [%s] must be declared with a non-empty repeated name.",
                templateMethod));

        return new ParameterizedRepeatedIfExceptionsTestNameFormatter(pattern, displayName, repeatedPattern);
    }

    protected static Stream<? extends Arguments> arguments(ArgumentsProvider provider, ExtensionContext context) {
        try {
            return provider.provideArguments(context);
        } catch (Exception e) {
            throw ExceptionUtils.throwAsUncheckedException(e);
        }
    }

    private Object[] consumedArguments(Object[] arguments, ParameterizedRepeatedMethodContext methodContext) {
        int parameterCount = methodContext.getParameterCount();
        return methodContext.hasAggregator() ? arguments
                : (arguments.length > parameterCount ? Arrays.copyOf(arguments, parameterCount) : arguments);
    }

    private ArgumentsProvider instantiateArgumentsProvider(Class<? extends ArgumentsProvider> clazz) {
        try {
            return ReflectionUtils.newInstance(clazz);
        } catch (Exception ex) {
            if (ex instanceof NoSuchMethodException) {
                String message = String.format("Failed to find a no-argument constructor for ArgumentsProvider [%s]. "
                                + "Please ensure that a no-argument constructor exists and "
                                + "that the class is either a top-level class or a static nested class",
                        clazz.getName());
                throw new JUnitException(message, ex);
            }
            throw ex;
        }
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(ParameterizedRepeatedExtension.class, context.getRequiredTestMethod()));
    }

    /**
     * TestTemplateIteratorParams (Repeat test if it failed)
     */
    class TestTemplateIteratorParams implements Iterator<TestTemplateInvocationContext> {

        private final List<Object[]> params;
        private final ParameterizedRepeatedIfExceptionsTestNameFormatter formatter;
        private final ParameterizedRepeatedMethodContext methodContext;
        private final AtomicLong invocationCount;
        private final AtomicLong paramsCount;
        private int currentIndex = 0;

        TestTemplateIteratorParams(List<Object[]> arguments, final ParameterizedRepeatedIfExceptionsTestNameFormatter formatter, final ParameterizedRepeatedMethodContext methodContext) {
            this.params = arguments;
            this.formatter = formatter;
            this.methodContext = methodContext;
            this.invocationCount = new AtomicLong(params.size() - 1);
            this.paramsCount = new AtomicLong(0);
        }

        @Override
        public boolean hasNext() {
            if (historyExceptionAppear.stream().anyMatch(ex -> ex) && currentIndex < totalRepeats) {
                return historyExceptionAppear.stream().anyMatch(ex -> ex) && currentIndex < totalRepeats;
            }
            return invocationCount.get() >= paramsCount.get();
        }

        /**
         * Return next ParameterizedTestInvocationContext. Managing several situations:
         * 1) Exception in Parameterized Test appears
         * 2) When the count of tests for one argument (parameter) equal total repeats
         * 3) If no exception appears start to create new  ParameterizedTestInvocationContext
         *
         * @return {@link ParameterizedTestInvocationContext}
         */
        @Override
        public TestTemplateInvocationContext next() {

            if (hasNext()) {
                int currentParam = paramsCount.intValue();
                int errorTestRepetitionsCountForOneArgument = toIntExact(historyExceptionAppear.stream().filter(b -> b).count());
                int successfulTestRepetitionsCountForOneArgument = toIntExact(historyExceptionAppear
                        .stream()
                        .skip(historyExceptionAppear.size() - minSuccess <= 0 ? 0 : historyExceptionAppear.size() - minSuccess)
                        .filter(b -> !b)
                        .count());

                if (errorTestRepetitionsCountForOneArgument >= 1 && currentIndex < totalRepeats && successfulTestRepetitionsCountForOneArgument != minSuccess) {

                    //If exception appeared would wait suspend time
                    if (historyExceptionAppear.stream().anyMatch(ex -> ex) && suspend != 0L) {
                        try {
                            Thread.sleep(suspend);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    currentIndex++;
                    repeatableExceptionAppeared = false;
                    return new ParameterizedTestInvocationContext(currentIndex, totalRepeats, formatter, methodContext, params.get(currentParam - 1));
                }

                if (currentIndex == totalRepeats || !repeatableExceptionAppeared) {
                    paramsCount.incrementAndGet();
                    repeatableExceptionAppeared = false;
                    historyExceptionAppear.clear();
                }

                currentIndex = 0;
                return new ParameterizedTestInvocationContext(0, 0, formatter, methodContext, params.get(currentParam));
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
