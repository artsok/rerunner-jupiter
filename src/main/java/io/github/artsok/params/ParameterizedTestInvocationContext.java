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

import io.github.artsok.extension.RepeatIfExceptionsCondition;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;
import java.util.List;

/**
 * @since 5.0
 */
class ParameterizedTestInvocationContext implements TestTemplateInvocationContext {

	private final  ParameterizedTestNameFormatter formatter;
	private final  ParameterizedTestMethodContext methodContext;
	private final Object[] arguments;

	ParameterizedTestInvocationContext( ParameterizedTestNameFormatter formatter,
                                        ParameterizedTestMethodContext methodContext, Object[] arguments) {
		this.formatter = formatter;
		this.methodContext = methodContext;
		this.arguments = arguments;
	}

	@Override
	public String getDisplayName(int invocationIndex) {
		return this.formatter.format(invocationIndex, this.arguments);
	}

	@Override
	public List<Extension> getAdditionalExtensions() {
		return Arrays.asList(new ParameterizedTestParameterResolver(this.methodContext, this.arguments),
				new RepeatIfExceptionsCondition()
		);
		//Надо сюда инджектить дополнительную логику обработки ошибок. Чтобы обработчик был внутри данного темплайте, а не снанужи
	}

}


//Добавлен, чтобы провести эксперемнты. Взят класс из RepeatIfExceptionsCondition
class ParameterizedRepeatExecutionCondition implements ExecutionCondition {
	private final int totalRepetitions;
	private final int minSuccess;
	private final int successfulTestRepetitionsCount;
	private final int failedTestRepetitionsCount;
	private final boolean repeatableExceptionAppeared;

	ParameterizedRepeatExecutionCondition(int currentRepetition, int totalRepetitions, int minSuccess,
							 int successfulTestRepetitionsCount, boolean repeatableExceptionAppeared) {
		this.totalRepetitions = totalRepetitions;
		this.minSuccess = minSuccess;
		this.successfulTestRepetitionsCount = successfulTestRepetitionsCount;
		this.failedTestRepetitionsCount = currentRepetition - successfulTestRepetitionsCount - 1;
		this.repeatableExceptionAppeared = repeatableExceptionAppeared;
	}

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		if (testUltimatelyFailed()) {
			return ConditionEvaluationResult.disabled("Turn off the remaining repetitions as the test ultimately failed"); //отключаем или включаем тесты. Проверено с Parameterized
		} else if (testUltimatelyPassed()) {
			return ConditionEvaluationResult.disabled("Turn off the remaining repetitions as the test ultimately passed");
		} else {
			//включаем тесты
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
		return totalRepetitions - failedTestRepetitionsCount < minSuccess;
	}

	private boolean testUltimatelyPassed() {
		return successfulTestRepetitionsCount >= minSuccess;
	}
}
