package com.capgemini.allure;

import com.capgemini.mrchecker.test.core.logger.BFLogger;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

public class StepConsoleLogger implements StepLifecycleListener {
    @Override
    public void beforeStepStop(StepResult result) {
        BFLogger.logInfo("[Allure Step Start] ::: " + result.getName());
    }

}
