/*
 * Copyright 2015-2019 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package io.github.artsok.params;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @since 5.0 - COPY PAST FROM ORIGINAL JUNIT 5 WITH SEVERAL CORRECTIONS
 */
class ParameterizedTestInvocationContext implements TestTemplateInvocationContext {

    private final int currentRepetition;
    private final int totalRepetitions;

    private final ParameterizedRepeatedIfExceptionsTestNameFormatter formatter;
    private final ParameterizedRepeatedMethodContext methodContext;
    private final Object[] arguments;

    ParameterizedTestInvocationContext(int currentRepetition, int totalRepetitions, ParameterizedRepeatedIfExceptionsTestNameFormatter formatter,
                                       ParameterizedRepeatedMethodContext methodContext, Object[] arguments) {
        this.currentRepetition = currentRepetition;
        this.totalRepetitions = totalRepetitions;
        this.formatter = formatter;
        this.methodContext = methodContext;
        this.arguments = arguments;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return this.formatter.format(invocationIndex, this.currentRepetition, this.totalRepetitions, this.arguments);
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return singletonList(new ParameterizedTestParameterResolver(this.methodContext, this.arguments)
        );
    }
}


