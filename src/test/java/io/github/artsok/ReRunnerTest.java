package io.github.artsok;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;



/**
 * Examples how to use @RepeatedIfExceptionsTest
 *
 * @author Artem Sokovets
 */
public class ReRunnerTest {
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    @ProgrammaticTest
    @RepeatedIfExceptionsTest(repeats = 2)
    public void runTest() {
        assertTrue(true, "No exception, repeat one time");
    }

    @Test
    void runRunTest() throws Exception {
        assertTestResults("runTest", true, 1, 0, 0);
    }

    /**
     * Repeated three times if test failed.
     * By default Exception.class will be handled in test
     */
    @ProgrammaticTest
    @RepeatedIfExceptionsTest(repeats = 3)
    public void reRunTest() throws IOException {
        throw new IOException("Error in Test");
    }

    @Test
    void runReRunTest() throws Exception {
        assertTestResults("reRunTest", false, 3, 2, 0);
    }

    /**
     * Repeated two times if test failed. Set IOException.class that will be handled in test
     *
     * @throws IOException - error if occurred
     */
    @ProgrammaticTest
    @RepeatedIfExceptionsTest(repeats = 2, exceptions = IOException.class)
    public void reRunTest2() throws IOException {
        throw new IOException("Exception in I/O operation");
    }

    @Test
    void runReRun2Test() throws Exception {
        assertTestResults("reRunTest2", false, 2, 1, 0);
    }

    /**
     * Repeated ten times if test failed. Set IOException.class that will be handled in test
     * Set formatter for test. Like behavior as at {@link org.junit.jupiter.api.RepeatedTest}
     *
     * @throws IOException - error if occurred
     */
    @ProgrammaticTest
    @RepeatedIfExceptionsTest(repeats = 10, exceptions = IOException.class,
            name = "Rerun failed test. Attempt {currentRepetition} of {totalRepetitions}")
    public void reRunTest3() throws IOException {
        throw new IOException("Exception in I/O operation");
    }

    @Test
    void runReRun3Test() throws Exception {
        assertTestResults("reRunTest3", false, 10, 9, 0);
    }

    @DisplayName("Name for our test")
    @RepeatedIfExceptionsTest(repeats = 105, exceptions = RuntimeException.class,
            name = "Rerun failed Test. Repetition {currentRepetition} of {totalRepetitions}")
    void reRunTest4() throws IOException {
        if (random.nextInt() % 2 == 0) { //Исключение бросается рандомно
            throw new RuntimeException("Error in Test");
        }
    }

    /**
     * Repeated 100 times with minimum success four times, then disabled all remaining repeats.
     * See image below how it works. Default exception is Exception.class
     */
    @ProgrammaticTest
    @DisplayName("Test Case Name")
    @RepeatedIfExceptionsTest(repeats = 100, minSuccess = 4)
    public void reRunTest5() {
        if (random.nextInt() % 2 == 0) {
            throw new RuntimeException("Error in Test");
        }
    }

    @ProgrammaticTest
    @DisplayName("Do not ultimately fail a test if there are still enough repetitions possible.")
    @RepeatedIfExceptionsTest(repeats = 2)
    public void reRunTest6() {
        throw new RuntimeException("Error in Test");
    }

    @Test
    void runReRunTest6() throws Exception {
        assertTestResults("reRunTest6", false, 2, 1, 0);
    }

    @ProgrammaticTest
    @DisplayName("Stop repetitions if 'minSuccess' cannot be reached anymore")
    @RepeatedIfExceptionsTest(repeats = 10, minSuccess = 4)
    public void reRunTest7() {
        throw new RuntimeException("Error in Test");
    }

    @Test
    void runReRunTest7() throws Exception {
        assertTestResults("reRunTest7", false, 7, 6, 3);
    }

    @ProgrammaticTest
    @DisplayName("Ultimately fail a test as soon as an unrepeatable exception occurs.")
    @RepeatedIfExceptionsTest(repeats = 2, exceptions = NumberFormatException.class)
    public void reRunTest8() {
        throw new RuntimeException("Error in Test");
    }

    @Test
    void runReRunTest8() throws Exception {
        assertTestResults("reRunTest8", false, 1, 0, 0);
    }

    @Disabled
    @RepeatedIfExceptionsTest(repeats = 3, exceptions = IOException.class, suspend = 5000L)
    void reRunTestWithSuspendOption() throws IOException {
        throw new IOException("Exception in I/O operation");
    }

    /**
     * By default total repeats = 1 and minimum success = 1.
     * If the test failed by this way start to repeat it by one time with one minimum success.
     *
     * This example without exceptions.
     */
    @Disabled
    @ParameterizedRepeatedIfExceptionsTest
    @ValueSource(ints = {14, 15, 100, -10})
    void successfulParameterizedTest(int argument) {
        System.out.println(argument);
    }

    /**
     * By default total repeats = 1 and minimum success = 1.
     * If the test failed by this way start to repeat it by one time with one minimum success.
     * This example with display name but without exceptions
     */
    @Disabled
    @DisplayName("Example of parameterized repeated without exception")
    @ParameterizedRepeatedIfExceptionsTest
    @ValueSource(ints = {1, 2, 3, 1001})
    void successfulParameterizedTestWithDisplayName(int argument) {
        System.out.println(argument);
    }

    /**
     * By default total repeats = 1 and minimum success = 1.
     * If the test failed by this way start to repeat it by one time with one minimum success.
     *
     * This example with display name but with exception. Exception depends on random number generation.
     */
    @Disabled
    @DisplayName("Example of parameterized repeated with exception")
    @ParameterizedRepeatedIfExceptionsTest
    @ValueSource(strings = {"Hi", "Hello", "Bonjour", "Privet"})
    void errorParameterizedTestWithDisplayName(String argument) {
        if (random.nextInt() % 2 == 0) {
            throw new RuntimeException("Exception " + argument);
        }
    }

    /**
     * By default total repeats = 1 and minimum success = 1.
     * If the test failed by this way start to repeat it by one time with one minimum success.
     *
     * This example with display name, repeated display name, 10 repeats and 2 minimum success with exceptions.
     * Exception depends on random number generation.
     */
    @Disabled
    @ParameterizedRepeatedIfExceptionsTest(name = "Argument was {0}",
            repeatedName = " (Repeat {currentRepetition} of {totalRepetitions})",
            repeats = 10, exceptions = RuntimeException.class, minSuccess = 2)
    @ValueSource(ints = {4, 5, 6, 7})
    void errorParameterizedTestWithDisplayNameAndRepeatedName(int argument) {
        if (random.nextInt() % 2 == 0) {
            throw new RuntimeException("Exception in Test " + argument);
        }
    }

    /**
     * By default total repeats = 1 and minimum success = 1.
     * If the test failed by this way start to repeat it by one time with one minimum success.
     *
     * This example with display name, implicitly repeated display name, 4 repeats and 2 minimum success with exceptions.
     * Exception depends on random number generation. Also use {@link MethodSource}
     */
    @Disabled
    @DisplayName("Display name of container")
    @ParameterizedRepeatedIfExceptionsTest(name = "Year {0} is a leap year.",
            repeats = 4, exceptions = RuntimeException.class, minSuccess = 2)
    @MethodSource("stringIntAndListProvider")
    void errorParameterizedTestWithMultiArgMethodSource(String str, int num, List<String> list)  {
        assertEquals(5, str.length());
        assertTrue(num >= 1 && num <= 2);
        assertEquals(2, list.size());
        if (random.nextInt() % 2 == 0) {
            throw new RuntimeException("Exception in Test");
        }
    }


    /**
     * Example with suspend option for Parameterized Test
     * It matters, when you get some infrastructure problems and you want to run your tests through timeout.
     *
     * Set break to 5 seconds. If exception appeared for any arguments, repeating extension would runs tests with break.
     * If one result failed and other passed, does not matter we would wait 5 seconds throught each arguments of the repeated tests.
     *
     */
    @Disabled
    @DisplayName("Example of parameterized repeated with exception")
    @ParameterizedRepeatedIfExceptionsTest(suspend = 5000L, minSuccess = 2, repeats = 3)
    @ValueSource(strings = {"Hi", "Hello", "Bonjour", "Privet"})
    void errorParameterizedTestWithSuspendOption(String argument) {
        if (random.nextInt() % 2 == 0) {
            throw new RuntimeException(argument);
        }
    }


    static Stream<Arguments> stringIntAndListProvider() {
        return Stream.of(
                arguments("apple", 1, Arrays.asList("a", "b")),
                arguments("lemon", 2, Arrays.asList("x", "y"))
        );
    }

    private void assertTestResults(String methodName, boolean successfulTestRun, int startedTests, int abortedTests,
                                   int skippedTests) throws Exception {
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectMethod(getClass(), getClass().getMethod(methodName)))
                .build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        if (successfulTestRun) {
            assertEquals(1, listener.getSummary().getTestsSucceededCount(), "successful test runs");
            assertEquals(0, listener.getSummary().getTestsFailedCount(), "failed test runs");
        } else {
            assertEquals(0, listener.getSummary().getTestsSucceededCount(), "successful test runs");
            assertEquals(1, listener.getSummary().getTestsFailedCount(), "failed test runs");
        }
        assertEquals(startedTests, listener.getSummary().getTestsStartedCount(), "started test runs");
        assertEquals(abortedTests, listener.getSummary().getTestsAbortedCount(), "aborted test runs");
        assertEquals(skippedTests, listener.getSummary().getTestsSkippedCount(), "skipped test runs");
    }

    @Tag("programmatic-tests")
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ProgrammaticTest {
    }
}
