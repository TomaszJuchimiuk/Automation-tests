//package com.capgemini.allure;
//
//import com.capgemini.mrchecker.test.core.utils.StepLogger;
//import io.qameta.allure.model.Status;
//
//public class StepLoggerAllure extends StepLogger {
//    private static final String KEYWORD_FOR_DATA = "With data: ";
//    private static final String ALLURE_LINK_TMS_PATTERN = "https://devstack.vwgroup.com/jira/browse/";
//
//    public static void data(String step) {
//        step(KEYWORD_FOR_DATA + step, Status.PASSED);
//    }
//
//    public static void tmsLink(String name, String url) {
//        if (url != null)
//            StepLogger.tmsLink(name, ALLURE_LINK_TMS_PATTERN + url);
//    }
//}
