package io.github.artsok;

import io.github.artsok.params.ParameterizedRepeatedTestExtension;
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
 * All logic of this extension at {@link ParameterizedRepeatedTestExtension}
 *
 * @author Artem Sokovets
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(ParameterizedRepeatedTestExtension.class)
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
     * Display name pattern for a repeated test: {@value #REPEATED_DISPLAY_NAME}
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
     * <p>Defaults to {@link #DEFAULT_DISPLAY_NAME}.
     *
     * <h4>Supported placeholders</h4>
     * <ul>
     * <li>{@link #DISPLAY_NAME_PLACEHOLDER}</li>
     * <li>{@link #INDEX_PLACEHOLDER}</li>
     * <li>{@link #ARGUMENTS_PLACEHOLDER}</li>
     * <li><code>{0}</code>, <code>{1}</code>, etc.: an individual argument (0-based)</li>
     * </ul>
     *
     * <p>For the latter, you may use {@link java.text.MessageFormat} patterns
     * to customize formatting.
     *
     * @see java.text.MessageFormat
     */
    String name() default DEFAULT_DISPLAY_NAME;

    /**
     * The display name to be used for individual repeated invocations of the
     * parameterized test; never blank.
     *
     * @see java.text.MessageFormat
     */
    String repeatedName() default  REPEATED_DISPLAY_NAME;


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
    int repeats();

    /**
     * Minimum success
     *
     * @return After n-times of passed tests will disable all remaining repeats.
     */
    int minSuccess() default 1;
}
