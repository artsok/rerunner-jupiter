package io.github.artsok.conditions;

import io.github.artsok.ExtendsWithTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.Optional;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

@Slf4j
public class TestRepeatedCondition implements TestExecutionExceptionHandler {

    //Планирую обработать исключени теста и запустить его через platform api
    //Происходит запуск теста shouldBeReRunUsualTest, который в свою очередь обращается к TestRepeatedCondition и в
    //методе handleTestExecutionException запускает этот же тест и так повторяется бесконечно т.к есть исключение.

    //Сделать ограничитель запусков.
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if(LimitCore.count.get() >= 4) {
            return;
        }
        Optional<Throwable> executionException = context.getExecutionException();
        log.debug("Исключение, '{}'", throwable.getMessage());

        log.debug("Context '{}'", context.getTestMethod());

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectPackage("io.github.artsok")
                        //,selectClass("ExtendsWithTest")
                        //, selectClass(ExtendsWithTest.class)
                        , selectMethod(ExtendsWithTest.class, "shouldBeReRunUsualTest")
                        //, selectMethod("ExtendsWithTest#shouldBeReRunUsualTest()")
                )
                .filters(
                        includeClassNamePatterns(".*Test")
                ).configurationParameter("a" + LimitCore.count.get(), "" + LimitCore.count.get())
                .build();


        Launcher launcher = LauncherFactory.create();



        TestPlan testPlan = launcher.discover(request);
        log.debug("Contains test " + testPlan.containsTests());


        testPlan.getRoots().forEach(str-> {
            log.debug("Выводи информацию, '{}'", str);
        });


        log.debug("Увеличиваем счетчик '{}'", LimitCore.count.getAndIncrement());
        TestExecutionListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

    }
}
