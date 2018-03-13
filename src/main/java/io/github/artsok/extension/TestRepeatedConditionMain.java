package io.github.artsok.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.engine.execution.ExecutableInvoker;
import org.junit.jupiter.engine.extension.ExtensionRegistry;
import org.junit.platform.engine.ConfigurationParameters;

import java.util.Optional;

@Slf4j
public class TestRepeatedConditionMain implements TestExecutionExceptionHandler  {


    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Optional<Throwable> executionException = context.getExecutionException();
        log.debug("Исключение, '{}'", throwable.getMessage());
        log.debug("Context '{}'", context.getTestMethod());
        log.debug("Уникальный ID '{}'", context.getUniqueId());

        Object instance = context.getRequiredTestInstance();




        new ExecutableInvoker().invoke(context.getTestMethod().get(),
                instance, context.getRoot(), ExtensionRegistry.createRegistryWithDefaultExtensions(new ConfigurationParameters() {
                    @Override
                    public Optional<String> get(String key) {
                        return Optional.of("org.junit.*DisabledCondition");
                    }

                    @Override
                    public Optional<Boolean> getBoolean(String key) {
                        return Optional.of(true);
                    }

                    @Override
                    public int size() {
                        return 1;
                    }
                }));



        //invokeMethod(context.getTestMethod().get(), null);

        //TODO: Rerun Test
    }
}
