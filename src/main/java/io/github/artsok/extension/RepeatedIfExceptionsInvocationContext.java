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

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.sun.xml.internal.ws.server.provider.SyncProviderInvokerTube;
import org.junit.jupiter.api.extension.*;

import java.util.List;

import static io.github.artsok.extension.RepeatIfExceptionsCondition.historyExceptionAppear;
import static java.util.Collections.singletonList;


/**
 * Context for extension point @RepeatedIfExceptions
 *
 * @author Artem Sokovets
 */
public class RepeatedIfExceptionsInvocationContext implements TestTemplateInvocationContext {

    private final int currentRepetition;
    private final int totalRepetitions;
    private final RepeatedIfExceptionsDisplayNameFormatter formatter;

    RepeatedIfExceptionsInvocationContext(int currentRepetition, int totalRepetitions, RepeatedIfExceptionsDisplayNameFormatter formatter) {
        this.currentRepetition = currentRepetition;
        this.totalRepetitions = totalRepetitions;
        this.formatter = formatter;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return this.formatter.format(this.currentRepetition, this.totalRepetitions);
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return singletonList(new RepeatExecutionCondition());
    }

}



class RepeatExecutionCondition implements ExecutionCondition {



    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if(historyExceptionAppear.size() > 1 ) {
            int size = historyExceptionAppear.size();
            if(!historyExceptionAppear.get(size - 1) && !historyExceptionAppear.get(size - 2)) {
                return ConditionEvaluationResult.disabled("Turn off the remaining tests that must be performed");
            }
        }
        return ConditionEvaluationResult.enabled("");
    }
}