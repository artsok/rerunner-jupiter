package io.github.artsok.params;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;

/**
 * @since 5.0 - FULL COPY PAST FROM ORIGINAL JUNIT 5
 */
class ParameterizedTestParameterResolver implements ParameterResolver {

    private final ParameterizedRepeatedMethodContext methodContext;
    private final Object[] arguments;

    ParameterizedTestParameterResolver(ParameterizedRepeatedMethodContext methodContext, Object[] arguments) {
        this.methodContext = methodContext;
        this.arguments = arguments;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Executable declaringExecutable = parameterContext.getDeclaringExecutable();
        Method testMethod = extensionContext.getTestMethod().orElse(null);

        // Not a @ParameterizedTest method?
        if (!declaringExecutable.equals(testMethod)) {
            return false;
        }

        // Current parameter is an aggregator?
        if (this.methodContext.isAggregator(parameterContext.getIndex())) {
            return true;
        }

        // Ensure that the current parameter is declared before aggregators.
        // Otherwise, a different ParameterResolver should handle it.
        if (this.methodContext.indexOfFirstAggregator() != -1) {
            return parameterContext.getIndex() < this.methodContext.indexOfFirstAggregator();
        }

        // Else fallback to behavior for parameterized test methods without aggregators.
        return parameterContext.getIndex() < this.arguments.length;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return this.methodContext.resolve(parameterContext, this.arguments);
    }

}
