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

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import java.util.List;

import static java.util.Collections.singletonList;


/**
 * Context for extension point @RepeatedIfExceptions
 *
 * @author Artem Sokovets
 */
public class RepeatedIfExceptionsInvocationContext implements TestTemplateInvocationContext {

    private final int currentRepetition;
    private final int totalRepetitions;
    private final int successfulTestRepetitionsCount;
    private final int minSuccess;
    private final RepeatedIfExceptionsDisplayNameFormatter formatter;

    RepeatedIfExceptionsInvocationContext(int currentRepetition, int totalRepetitions, int successfulTestRepetitionsCount,
                                          int minSuccess, RepeatedIfExceptionsDisplayNameFormatter formatter) {
        this.currentRepetition = currentRepetition;
        this.totalRepetitions = totalRepetitions;
        this.successfulTestRepetitionsCount = successfulTestRepetitionsCount;
        this.minSuccess = minSuccess;
        this.formatter = formatter;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return this.formatter.format(this.currentRepetition, this.totalRepetitions);
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return singletonList(new RepeatExecutionCondition(currentRepetition, totalRepetitions, minSuccess,
                successfulTestRepetitionsCount));
    }
}


/**
 * Implements ExecutionCondition interface.
 * With one method in this interface, we can control of on/off executing test
 */
class RepeatExecutionCondition implements ExecutionCondition {
    private final int totalRepetitions;
    private final int minSuccess;
    private final int successfulTestRepetitionsCount;
    private final int failedTestRepetitionsCount;

    RepeatExecutionCondition(int currentRepetition, int totalRepetitions, int minSuccess, int successfulTestRepetitionsCount) {
        this.totalRepetitions = totalRepetitions;
        this.minSuccess = minSuccess;
        this.successfulTestRepetitionsCount = successfulTestRepetitionsCount;
        this.failedTestRepetitionsCount = currentRepetition - successfulTestRepetitionsCount - 1;
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (successfulTestRepetitionsCount >= minSuccess) {
            return ConditionEvaluationResult.disabled("Turn off the remaining repetitions as the test ultimately passed");
        } else if (totalRepetitions - failedTestRepetitionsCount < minSuccess) {
            return ConditionEvaluationResult.disabled("Turn off the remaining repetitions as the test ultimately failed");
        } else {
            return ConditionEvaluationResult.enabled("Repeat the tests");
        }
    }
}
