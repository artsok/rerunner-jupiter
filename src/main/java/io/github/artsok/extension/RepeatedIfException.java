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

/**
 * Custom exception of rerunner-jupiter extension.
 *
 * @author Artem Sokovets
 */
public class RepeatedIfException extends RuntimeException {

    private static final long serialVersionUID = -453323380293211043L;

    public RepeatedIfException(String message) {
        super(message);
    }
}
