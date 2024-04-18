//package com.capgemini.allure;
//
//import com.capgemini.mrchecker.test.core.logger.BFLogger;
//import io.qameta.allure.AllureResultsWriteException;
//import io.qameta.allure.listener.TestLifecycleListener;
//import io.qameta.allure.model.Link;
//import io.qameta.allure.model.Status;
//import io.qameta.allure.model.StatusDetails;
//import io.qameta.allure.model.TestResult;
//import io.qameta.allure.util.ResultsUtils;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.NoSuchElementException;
//
//public class AllureKnownIssue implements TestLifecycleListener {
//
//    @Override
//    public void beforeTestWrite(TestResult testResult) {
//
//        BFLogger.logInfo("AllureKnownIssue");
//
//        if (testResult.getStatus() == Status.FAILED || testResult.getStatus() == Status.SKIPPED) {
//            new AllureKnownIssue().handleKnownIssue(testResult);
//        }
//
//    }
//
//    public void handleKnownIssue(TestResult testResult) {
//        String message = testResult.getStatusDetails()
//                .getMessage();
//
//        String test = testResult.getFullName();
//        String hdb = "";
//        Status status = testResult.getStatus();
//
//        String bugsFileName = "src/test/resources/bugs/bugs.json";
//
//        JSONParser jsonParser = new JSONParser();
//        try (FileReader reader = new FileReader(bugsFileName)) {
//            JSONArray bugsList = (JSONArray) jsonParser.parse(reader);
//            for (Object bug : bugsList) {
//                JSONObject bugJSON = (JSONObject) bug;
//                JSONArray testsJSON = (JSONArray) bugJSON.get("tests");
//                List<String> testsJSONList = new ArrayList<>();
//
//                for (Object o : testsJSON) {
//                    testsJSONList.add((String) o);
//                }
//                String issueJSON = (String) bugJSON.get("issue");
//                String issueStatusJSON = MessageFormat.format("[{0}]", bugJSON.get("issueStatus"))
//                        .toUpperCase();
//                String messageJSON = (String) bugJSON.get("message");
//
//                if ((isStringContainsPartFromList(test, testsJSONList) || testsJSONList.contains(hdb)
//                        || testsJSONList.contains("*") || testsJSONList.contains(getXrayTestKey(testResult)))
//                        && (message.contains(messageJSON) || message.replace("\n", "").replace("\r", "").matches(".*" + messageJSON.replace("\n", "") + ".*"))) {
//
//                    String errorMessage = MessageFormat.format("{0} {1}", issueStatusJSON, message);
//                    if (status == Status.FAILED || status == Status.SKIPPED) {
//
//                        StatusDetails statusDetails = testResult.getStatusDetails();
//                        statusDetails.setMessage(errorMessage);
//                        testResult.setStatusDetails(statusDetails);
//
//                        String pattern = "https://devstack.vwgroup.com/jira/browse/";
//                        List<Link> linksList = testResult.getLinks();
//                        linksList.add(new Link().setName(issueJSON)
//                                .setType(ResultsUtils.ISSUE_LINK_TYPE)
//                                .setUrl(pattern + issueJSON));
//
//                        testResult.setLinks(linksList);
//                    }
//                }
//            }
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Fix for parametrized smoke tests which change method name by add some at the end
//    private boolean isStringContainsPartFromList(String text, List<String> parts) {
//        if (text != null && !parts.isEmpty()) {
//            for (String part : parts) {
//                if (text.contains(part.replace("-", ""))) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    private String getXrayTestKey(TestResult result) {
//        return result.getLinks()
//                .stream()
//                .filter(link -> link.getType().equals(ResultsUtils.TMS_LINK_TYPE))
//                .map(Link::getName)
//                .findFirst()
//                .orElseThrow(() -> new AllureResultsWriteException("Test is not linked to TMS", new NoSuchElementException()));
//    }
//}