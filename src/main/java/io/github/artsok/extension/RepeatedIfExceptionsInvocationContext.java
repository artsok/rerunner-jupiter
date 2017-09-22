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

import org.junit.jupiter.api.extension.TestTemplateInvocationContext;


/**
 * Context for extension point @RepeatedIfExceptions
 *
 * @author Artem Sokovets
 */
public class RepeatedIfExceptionsInvocationContext implements TestTemplateInvocationContext {

    private final int currentRepetition;
    private final int totalRepetitions;
    private final RepeatedIfExceptionsDisplayNameFormatter formatter;

    RepeatedIfExceptionsInvocationContext(int currentRepetition, int totalRepetitions,
                                          RepeatedIfExceptionsDisplayNameFormatter formatter) {
        this.currentRepetition = currentRepetition;
        this.totalRepetitions = totalRepetitions;
        this.formatter = formatter;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return this.formatter.format(this.currentRepetition, this.totalRepetitions);
    }
}

