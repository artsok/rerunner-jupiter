package io.github.artsok.internal;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Implements ExecutionCondition interface.
 * With one method in this interface, we can control of on/off executing test
 */
public class RepeatExecutionCondition implements ExecutionCondition {
    private final int totalTestRuns;
    private final int minSuccess;
    private final int successfulTestRepetitionsCount;
    private final int failedTestRepetitionsCount;
    private final boolean repeatableExceptionAppeared;

    RepeatExecutionCondition(int currentRepetition, int totalRepetitions, int minSuccess,
                             int successfulTestRepetitionsCount, boolean repeatableExceptionAppeared) {
        this.totalTestRuns = totalRepetitions;
        this.minSuccess = minSuccess;
        this.successfulTestRepetitionsCount = successfulTestRepetitionsCount;
        this.failedTestRepetitionsCount = currentRepetition - successfulTestRepetitionsCount - 1;
        this.repeatableExceptionAppeared = repeatableExceptionAppeared;
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (testUltimatelyFailed()) {
            return ConditionEvaluationResult.disabled("Turn off the remaining repetitions as the test ultimately failed");
        } else if (testUltimatelyPassed()) {
            return ConditionEvaluationResult.disabled("Turn off the remaining repetitions as the test ultimately passed");
        } else {
            return ConditionEvaluationResult.enabled("Repeat the tests");
        }
    }

    private boolean testUltimatelyFailed() {
        return aNonRepeatableExceptionAppeared() || minimalRequiredSuccessfulRunsCannotBeReachedAnymore();
    }

    private boolean aNonRepeatableExceptionAppeared() {
        return failedTestRepetitionsCount > 0 && !repeatableExceptionAppeared;
    }

    private boolean minimalRequiredSuccessfulRunsCannotBeReachedAnymore() {
        return totalTestRuns - failedTestRepetitionsCount < minSuccess;
    }

    private boolean testUltimatelyPassed() {
        return successfulTestRepetitionsCount >= minSuccess;
    }
}
