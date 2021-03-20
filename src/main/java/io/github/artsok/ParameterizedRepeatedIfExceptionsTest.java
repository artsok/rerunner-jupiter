package io.github.artsok;

import io.github.artsok.extension.ParameterizedRepeatedExtension;
import org.apiguardian.api.API;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Annotation which you can put to parameterized test method.
 * Customize the number of repeats and set for what exception you want handled.
 * By default set Throwable.class.
 * All logic of this extension at {@link ParameterizedRepeatedExtension}
 *
 * @author Artem Sokovets
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(ParameterizedRepeatedExtension.class)
public @interface ParameterizedRepeatedIfExceptionsTest {

    /**
     * Placeholder for the current repetition count of a {@code @RepeatedTest}
     * method: <code>{currentRepetition}</code>
     */
    String CURRENT_REPETITION_PLACEHOLDER = "{currentRepetition}";

    /**
     * Placeholder for the total number of repetitions of a {@code @RepeatedTest}
     * method: <code>{totalRepetitions}</code>
     */
    String TOTAL_REPETITIONS_PLACEHOLDER = "{totalRepetitions}";

    /**
     * Display name pattern for a repeated test
     *
     * @see #CURRENT_REPETITION_PLACEHOLDER
     * @see #TOTAL_REPETITIONS_PLACEHOLDER
     */
    String REPEATED_DISPLAY_NAME = " (Repeated if the test failed " + CURRENT_REPETITION_PLACEHOLDER + " of " + TOTAL_REPETITIONS_PLACEHOLDER + ")";

    /**
     * Placeholder for the {@linkplain org.junit.jupiter.api.TestInfo#getDisplayName
     * display name} of a {@code @ParameterizedTest} method: <code>{displayName}</code>
     *
     * @see #name
     * @since 5.3
     */
    @API(status = EXPERIMENTAL, since = "5.3")
    String DISPLAY_NAME_PLACEHOLDER = "{displayName}";

    /**
     * Placeholder for the current invocation index of a {@code @ParameterizedTest}
     * method (1-based): <code>{index}</code>
     *
     * @see #name
     * @since 5.3
     */
    @API(status = EXPERIMENTAL, since = "5.3")
    String INDEX_PLACEHOLDER = "{index}";

    /**
     * Placeholder for the complete, comma-separated arguments list of the
     * current invocation of a {@code @ParameterizedTest} method:
     * <code>{arguments}</code>
     *
     * @see #name
     * @since 5.3
     */
    @API(status = EXPERIMENTAL, since = "5.3")
    String ARGUMENTS_PLACEHOLDER = "{arguments}";

    /**
     * Default display name pattern for the current invocation of a
     * {@code @ParameterizedTest} method: {@value}
     *
     * <p>Note that the default pattern does <em>not</em> include the
     * {@linkplain #DISPLAY_NAME_PLACEHOLDER display name} of the
     * {@code @ParameterizedTest} method.
     *
     * @see #name
     * @see #DISPLAY_NAME_PLACEHOLDER
     * @see #INDEX_PLACEHOLDER
     * @see #ARGUMENTS_PLACEHOLDER
     * @since 5.3
     */
    @API(status = EXPERIMENTAL, since = "5.3")
    String DEFAULT_DISPLAY_NAME = "[" + INDEX_PLACEHOLDER + "] " + ARGUMENTS_PLACEHOLDER;

    /**
     * The display name to be used for individual invocations of the
     * parameterized test; never blank or consisting solely of whitespace.
     *
     * @return {@link String}
     */
    String name() default DEFAULT_DISPLAY_NAME;

    /**
     * The display name to be used for individual repeated invocations of the
     * parameterized test; never blank.
     *
     * @return {@link String}
     */
    String repeatedName() default REPEATED_DISPLAY_NAME;


    /**
     * Pool of exceptions
     *
     * @return Exception that handlered
     */
    Class<? extends Throwable>[] exceptions() default Throwable.class;

    /**
     * Number of repeats
     *
     * @return N-times repeat test if it failed
     */
    int repeats() default 1;

    /**
     * Minimum success
     *
     * @return After n-times of passed tests will disable all remaining repeats.
     */
    int minSuccess() default 1;

    /**
     * Add break (cooldown) to each tests.
     * It matters, when you get some infrastructure problems and you want to run your tests through timeout.
     *
     * @return the length of time to sleep in milliseconds
     */
    long suspend() default 0L;
}
