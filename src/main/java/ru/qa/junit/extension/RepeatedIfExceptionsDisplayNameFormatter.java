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
package ru.qa.junit.extension;


import static ru.qa.junit.RepeatedIfExceptionsTest.*;

/**
 * Formatter for extension point @RepeatedIfExceptions
 *
 * @author Artem Sokovets
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