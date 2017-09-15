package ru.qa.junit;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.qa.junit.extension.RepeatIfExceptionsCondition;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Объявляем аннотацию, которую будет вешать на методы.
 * Обработчик данной аннотации класс {@link RepeatIfExceptionsCondition}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(RepeatIfExceptionsCondition.class)
public @interface RepeatedIfExceptionsTest {

    /**
     * Placeholder for the {@linkplain TestInfo#getDisplayName display name} of
     * a {@code @RepeatedTest} method: <code>{displayName}</code>
     */
    String DISPLAY_NAME_PLACEHOLDER = "{displayName}";

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
     * <em>Short</em> display name pattern for a repeated test: {@value #SHORT_DISPLAY_NAME}
     *
     * @see #CURRENT_REPETITION_PLACEHOLDER
     * @see #TOTAL_REPETITIONS_PLACEHOLDER
     * @see #LONG_DISPLAY_NAME
     */
    String SHORT_DISPLAY_NAME = "Repetition if test failed " + CURRENT_REPETITION_PLACEHOLDER + " of " + TOTAL_REPETITIONS_PLACEHOLDER;

    /**
     * <em>Long</em> display name pattern for a repeated test: {@value #LONG_DISPLAY_NAME}
     *
     * @see #DISPLAY_NAME_PLACEHOLDER
     * @see #SHORT_DISPLAY_NAME
     */
    String LONG_DISPLAY_NAME = DISPLAY_NAME_PLACEHOLDER + " :: " + SHORT_DISPLAY_NAME;


    Class<? extends Exception>[] exceptions() default Exception.class;
    int repeats();
    String name() default SHORT_DISPLAY_NAME;

}
