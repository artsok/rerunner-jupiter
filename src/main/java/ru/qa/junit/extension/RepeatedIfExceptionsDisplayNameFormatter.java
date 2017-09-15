package ru.qa.junit.extension;


import static ru.qa.junit.RepeatedIfExceptionsTest.*;

/**
 * Created by sbt-sokovets-av
 */
class RepeatedIfExceptionsDisplayNameFormatter {

    private final String pattern;
    private final String displayName;

    RepeatedIfExceptionsDisplayNameFormatter(String pattern, String displayName) {
        this.pattern = pattern;
        this.displayName = displayName;
    }

    String format(int currentRepetition, int totalRepetitions) {
        return this.pattern
                .replace(DISPLAY_NAME_PLACEHOLDER, this.displayName)
                .replace(CURRENT_REPETITION_PLACEHOLDER, String.valueOf(currentRepetition))
                .replace(TOTAL_REPETITIONS_PLACEHOLDER, String.valueOf(totalRepetitions));
    }

}