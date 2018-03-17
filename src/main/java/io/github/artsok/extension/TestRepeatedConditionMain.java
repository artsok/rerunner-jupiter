package io.github.artsok.extension;

import io.github.artsok.event.ExecutionEventRecorder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.jupiter.engine.descriptor.TestFactoryTestDescriptor;
import org.junit.jupiter.engine.execution.ExecutableInvoker;
import org.junit.jupiter.engine.extension.ExtensionRegistry;
import org.junit.platform.engine.*;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;

@Slf4j
public class TestRepeatedConditionMain implements TestExecutionExceptionHandler  {


    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {


        System.out.println("===============START");

        if(LimitCore.count.get() >= 4) {
            return;
        }
        Optional<Throwable> executionException = context.getExecutionException();
        log.debug("Исключение, '{}'", throwable.getMessage());
        log.debug("Context '{}'", context.getTestMethod());
        log.debug("Уникальный ID '{}'", context.getUniqueId());

//** invoke
//        Object instance = context.getRequiredTestInstance();
//
//
//
//
//        new ExecutableInvoker().invoke(context.getTestMethod().get(),
//                instance, context.getRoot(), ExtensionRegistry.createRegistryWithDefaultExtensions(new ConfigurationParameters() {
//                    @Override
//                    public Optional<String> get(String key) {
//                        return Optional.of("org.junit.*DisabledCondition");
//                    }
//
//                    @Override
//                    public Optional<Boolean> getBoolean(String key) {
//                        return Optional.of(true);
//                    }
//
//                    @Override
//                    public int size() {
//                        return 1;
//                    }
//                }));
//end imvoke

        JupiterTestEngine engine = new JupiterTestEngine();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectPackage("io.github.artsok")
                        , selectClass("io.github.artsok.ExtendsWithTest")
                        //,selectClass("ExtendsWithTest")
                        //, selectClass(ExtendsWithTest.class)
                        //, selectMethod(ExtendsWithTest.class, "shouldBeReRunUsualTest")
                        //, selectMethod("ExtendsWithTest#shouldBeReRunUsualTest()")
                        //selectFile(new File("src/test/java/io/github/artsok/ExtendsWithTest.java"))
                )
                .build();

        LimitCore.count.getAndIncrement();
        Launcher launcher = LauncherFactory.create();
        TestPlan testPlan = launcher.discover(request);
        log.debug("Contains test " + testPlan.containsTests() + " " + testPlan.getRoots().size());

        TestDescriptor testDescriptor = engine.discover(request, UniqueId.forEngine(engine.getId()));
        ExecutionEventRecorder eventRecorder = new ExecutionEventRecorder();
        engine.execute(new ExecutionRequest(testDescriptor, eventRecorder, request.getConfigurationParameters()));



//        System.out.println("sfdsf " + eventRecorder.getTestStartedCount());
//        System.out.println("sdfsdf  " + eventRecorder.getTestSuccessfulCount());
    }
}
