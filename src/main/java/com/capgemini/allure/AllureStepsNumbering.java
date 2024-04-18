package com.capgemini.allure;

import com.capgemini.mrchecker.test.core.logger.BFLogger;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.TestResult;

public class AllureStepsNumbering implements TestLifecycleListener {

    @Override
    public void beforeTestWrite(TestResult testResult) {
        BFLogger.logInfo("AllureStepsNumbering");
        int counter = 0;

        int stepNumber = 0;

        while (counter < testResult.getSteps()
                .size()) {

            if (!testResult.getSteps()
                    .get(counter)
                    .getName()
                    .equals("--Screenshot--")
                    && !testResult.getSteps()
                    .get(counter)
                    .getName()
                    .equals("Preconditions")) {

                stepNumber++;

                testResult.getSteps()
                        .get(counter)
                        .setName(stepNumber + " | " + testResult.getSteps()
                                .get(counter)
                                .getName());

            }
            counter++;
        }
    }
}
