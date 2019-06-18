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

import io.github.artsok.RepeatedIfExceptionsTest;

/**
 * Formatter for extension point @RepeatedIfExceptions
 *
 * @author Artem Sokovets
 */
public class RepeatedIfExceptionsDisplayNameFormatter {

    private final String pattern;
    private final String displayName;

    public RepeatedIfExceptionsDisplayNameFormatter(final String pattern, final String displayName) {
        this.pattern = pattern;
        this.displayName = displayName;
    }

    String format(final int currentRepetition, final int totalRepetitions) {
        if (currentRepetition > 1 && totalRepetitions > 0) {
            final String result = pattern
                    .replace(RepeatedIfExceptionsTest.CURRENT_REPETITION_PLACEHOLDER, String.valueOf(currentRepetition - 1)) //Minus, because first run doesn't mean repetition
                    .replace(RepeatedIfExceptionsTest.TOTAL_REPETITIONS_PLACEHOLDER, String.valueOf(totalRepetitions - 1));
            return this.displayName.concat(" (").concat(result).concat(")");
        } else {
            return this.displayName;
        }
    }

}