package io.github.artsok.internal;

import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.StringUtils;

import java.text.Format;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.IntStream;

import static io.github.artsok.ParameterizedRepeatedIfExceptionsTest.ARGUMENTS_PLACEHOLDER;
import static io.github.artsok.ParameterizedRepeatedIfExceptionsTest.CURRENT_REPETITION_PLACEHOLDER;
import static io.github.artsok.ParameterizedRepeatedIfExceptionsTest.DISPLAY_NAME_PLACEHOLDER;
import static io.github.artsok.ParameterizedRepeatedIfExceptionsTest.INDEX_PLACEHOLDER;
import static io.github.artsok.ParameterizedRepeatedIfExceptionsTest.TOTAL_REPETITIONS_PLACEHOLDER;
import static java.util.stream.Collectors.joining;


public class ParameterizedRepeatedIfExceptionsTestNameFormatter {

    private final String pattern;
    private final String displayName;
    private final String repeatedNamePattern;

    public ParameterizedRepeatedIfExceptionsTestNameFormatter(String pattern, String displayName, String repeatedNamePattern) {
        this.pattern = pattern;
        this.displayName = displayName;
        this.repeatedNamePattern = repeatedNamePattern;
    }


    String format(int invocationIndex, int currentRepetition, int totalRepetitions, Object... arguments) {
        try {
            return formatSafely(invocationIndex, currentRepetition, totalRepetitions, arguments);
        } catch (Exception ex) {
            String message = "The display name pattern defined for the parameterized test is invalid. "
                    + "See nested exception for further details.";
            throw new JUnitException(message, ex);
        }
    }

    private String formatSafely(int invocationIndex, int currentRepetition, int totalRepetitions, Object[] arguments) {
        String pattern = prepareMessageFormatPattern(invocationIndex, currentRepetition, totalRepetitions, arguments);
        MessageFormat format = new MessageFormat(pattern);
        Object[] humanReadableArguments = makeReadable(format, arguments);
        return format.format(humanReadableArguments);
    }


    /**
     * Format display message. If exceptions appears use one or other pattern.
     * @param invocationIndex - Index of argument
     * @param currentRepetition - Indicate the current repeating index if exceptions appear
     * @param totalRepetitions - Amount of all repeats
     * @param arguments - Method arguments
     * @return {@link String} - Displayed text
     */
    private String prepareMessageFormatPattern(int invocationIndex, int currentRepetition, int totalRepetitions, Object[] arguments) {
        String result;
        if (currentRepetition != 0 && totalRepetitions != 0) {
            result = pattern
                    .replace(DISPLAY_NAME_PLACEHOLDER, this.displayName)
                    .replace(INDEX_PLACEHOLDER, String.valueOf(invocationIndex))
                    .concat(repeatedNamePattern)
                    .replace(CURRENT_REPETITION_PLACEHOLDER, String.valueOf(currentRepetition))
                    .replace(TOTAL_REPETITIONS_PLACEHOLDER, String.valueOf(totalRepetitions));
        } else {
            result = pattern
                    .replace(DISPLAY_NAME_PLACEHOLDER, this.displayName)
                    .replace(INDEX_PLACEHOLDER, String.valueOf(invocationIndex))
                    .replace(repeatedNamePattern, "");
        }


        if (result.contains(ARGUMENTS_PLACEHOLDER)) {
            // @formatter:off
            String replacement = IntStream.range(0, arguments.length)
                    .mapToObj(index -> "{" + index + "}")
                    .collect(joining(", "));
            // @formatter:on
            result = result.replace(ARGUMENTS_PLACEHOLDER, replacement);
        }
        return result;
    }

    private Object[] makeReadable(MessageFormat format, Object[] arguments) {
        Format[] formats = format.getFormatsByArgumentIndex();
        Object[] result = Arrays.copyOf(arguments, Math.min(arguments.length, formats.length), Object[].class);
        for (int i = 0; i < result.length; i++) {
            if (formats[i] == null) {
                result[i] = StringUtils.nullSafeToString(arguments[i]);
            }
        }
        return result;
    }
}
