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
package io.github.artsok.internal;

import org.junit.jupiter.api.extension.Extension;
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
    private final int totalTestRuns;
    private final int successfulTestRepetitionsCount;
    private final int minSuccess;
    private final boolean repeatableExceptionAppeared;
    private final RepeatedIfExceptionsDisplayNameFormatter formatter;

    public RepeatedIfExceptionsInvocationContext(int currentRepetition, int totalRepetitions, int successfulTestRepetitionsCount,
                                          int minSuccess, boolean repeatableExceptionAppeared,
                                          RepeatedIfExceptionsDisplayNameFormatter formatter) {
        this.currentRepetition = currentRepetition;
        this.totalTestRuns = totalRepetitions;
        this.successfulTestRepetitionsCount = successfulTestRepetitionsCount;
        this.minSuccess = minSuccess;
        this.repeatableExceptionAppeared = repeatableExceptionAppeared;
        this.formatter = formatter;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return this.formatter.format(this.currentRepetition, this.totalTestRuns);
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return singletonList(new RepeatExecutionCondition(currentRepetition, totalTestRuns, minSuccess,
                successfulTestRepetitionsCount, repeatableExceptionAppeared));
    }
}

