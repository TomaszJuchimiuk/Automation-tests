//package com.capgemini.allure;
//
//import com.capgemini.mrchecker.test.core.logger.BFLogger;
//import io.qameta.allure.AllureResultsWriteException;
//import io.qameta.allure.listener.TestLifecycleListener;
//import io.qameta.allure.model.Attachment;
//import io.qameta.allure.model.Link;
//import io.qameta.allure.model.StepResult;
//import io.qameta.allure.model.TestResult;
//import io.qameta.allure.util.PropertiesUtils;
//import io.qameta.allure.util.ResultsUtils;
//import lombok.Synchronized;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class AllureXrayExecutionResults implements TestLifecycleListener {
//    //THis Class creates files according to Xray documentation:
//    //https://docs.getxray.app/display/XRAYCLOUD/Using+Xray+JSON+format+to+import+execution+results#UsingXrayJSONformattoimportexecutionresults-JSONformat
//    private static final String PATH_ALLURE_RESULTS = PropertiesUtils.loadAllureProperties().getProperty("allure.results.directory") + "/";
//    private static final String PATH_XRAY_JSON = "target/XRAY";
//    private static final String COMMENT_ENV_VAR_TEXT_APPNAME =
//            "Test project: https://devstack.vwgroup.com/bitbucket/projects/RWIP/repos/testing-/"
//                    + "\nEnvironment: " + System.getProperty("env", "QA") + "\n";
//
//    private static final String COMMENT_DEFAULT_TEXT_APPNAME =
//            "Test was executed automatically by TQA team.\n\n" + COMMENT_ENV_VAR_TEXT_APPNAME;
//
//    private static final String XRAY_FILE = PATH_XRAY_JSON + "/" + "xrayTestExecution.json";
//
//    private static final String JSON_EMPTY = " {\"tests\":[\n\n\n]}\n";
//    private static final String JSON_END = "\n]}\n";
//    private static final String JSON_COMMA = "\n,\n";
//
//    @Override
//    public void afterTestWrite(TestResult result) {
//
//        if (getXrayTestKey(result).equals("") || getXrayTestKey(result).equals("jiraIssue")) {
//            return;
//        }
//
//        JSONObject oneTestResult = createXrayJsonForCurrentTest(result);
//
//        createDirectory();
//
//        addJsonToAllTestsFile(oneTestResult);
//
//        createSeparateJsonFile(oneTestResult);
//    }
//
//    @Override
//    public void beforeTestWrite(TestResult result) {
//        result.setName(createSummary(result));
//    }
//
//    private String createSummary(TestResult result) {
//        String testName = result.getName();
//        testName = removeJiraIdFromParametrizedTestName(testName);
//        testName = reformatDataDrivenMethodNamesToMatchDisplayedInAllure(testName);
//        return testName;
//    }
//
//    private String removeJiraIdFromParametrizedTestName(String testName) {
//        String jiraId = "RWIP-\\d{5}";
//        String testsParametersDelimiter = ", ";
//        String parameterHeader = "JIRA_ISSUE = ";
//        String regex = String.format("(%s)?(%s|null)(%s)?", parameterHeader, jiraId, testsParametersDelimiter);
//
//        return StringUtils.removePattern(testName, regex);
//    }
//
//    private String reformatDataDrivenMethodNamesToMatchDisplayedInAllure(String testName) {
//        return testName.replace("()", "").replace(",", " | ").trim();
//    }
//
//    private JSONObject createXrayJsonForCurrentTest(TestResult result) {
//
//        String testKey = getXrayTestKey(result);
//
//        String status = getXrayStatus(result);
//
//        String startTimestamp = getXrayTimestamp(result.getStart());
//
//        String stopTimestamp = getXrayTimestamp(result.getStop());
//
//        String comment = getXrayComment(result);
//
//        List<String> defectsList = getBugList(result);
//
//        List<JSONObject> evidenceList = getEvidenceList(result);
//
//        // Bind all together in one JSON
//        JSONObject oneTestResult = new JSONObject();
//
//        oneTestResult.put("testKey", testKey);
//        oneTestResult.put("status", status);
//        oneTestResult.put("start", startTimestamp);
//        oneTestResult.put("finish", stopTimestamp);
//        oneTestResult.put("comment", comment);
//
//        if (!evidenceList.isEmpty()) {
//            oneTestResult.put("evidences", evidenceList);
//        }
//
//        if (!defectsList.isEmpty()) {
//            oneTestResult.put("defects", defectsList);
//        }
//
//        return oneTestResult;
//    }
//
//    private List<String> getBugList(TestResult result) {
//        return result.getLinks()
//                .stream()
//                .filter(link -> link.getType().equals(ResultsUtils.ISSUE_LINK_TYPE))
//                .map(Link::getName)
//                .toList();
//    }
//
//    private String getXrayComment(TestResult result) {
//        String comment = COMMENT_DEFAULT_TEXT_APPNAME;
//
//        if (result.getStatusDetails() != null && result.getStatusDetails()
//                .getMessage() != null) {
//            comment = comment + "\nBug:\n" + result.getStatusDetails()
//                    .getMessage();
//        }
//        return comment;
//    }
//
//    private List<JSONObject> getEvidenceList(TestResult result) {
//        String fileAttchament = "asbase64";
//
//        List<StepResult> stepList = result.getSteps();
//
//        List<Attachment> attachmentList = new ArrayList<>();
//
//        for (int i = 0; i < stepList.size(); i++) {
//            for (int j = 0; j < stepList.get(i).getAttachments().size(); j++) {
//                attachmentList.add(stepList.get(i).getAttachments().get(j));
//            }
//        }
//        List<JSONObject> evidenceList = new ArrayList<>();
//
//        BFLogger.logInfo("attachments: " + attachmentList.size());
//        String filename = "";
//        if (!attachmentList.isEmpty()) {
//
//
//            for (int k = 0; k < attachmentList.size(); k++) {
//                filename = PATH_ALLURE_RESULTS + attachmentList.get(k)
//                        .getSource();
//
//                BFLogger.logInfo("filename=" + filename);
//
//                fileAttchament = encodeFileToBase64Binary(filename);
//
//                if (fileAttchament != null) {
//
//                    JSONObject evidence = new JSONObject();
//                    evidence.put("filename", attachmentList.get(k).getName().replace(" ", "_").replace("/", "_") + ".html");
//                    evidence.put("contentType", "text/html");
//                    evidence.put("data", fileAttchament);
//
//                    evidenceList.add(evidence);
//                }
//            }
//        }
//        return evidenceList;
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
//
//    private String getXrayStatus(TestResult result) {
//        String status = "";
//        switch (result.getStatus()
//                .value()) {
//            case "passed":
//                status = "PASS";
//                break;
//            case "failed", "skipped":
//                status = "FAIL";
//                break;
//            case "broken":
//                status = "ABORTED";
//                break;
//        }
//        return status;
//    }
//
//    private String getXrayTimestamp(Long date) {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+00:00'");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
//        return dateFormat.format(date);
//    }
//
//    private void createSeparateJsonFile(JSONObject oneTestResult) {
//
//        String testKey = oneTestResult.get("testKey")
//                .toString();
//        try (FileWriter file = new FileWriter(PATH_XRAY_JSON + "/" + "xrayTest-" + testKey + ".json")) {
//            file.write(oneTestResult.toString(3));
//            file.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Synchronized
//    private static void addJsonToAllTestsFile(JSONObject oneTestResult) {
//
//        File file = new File(XRAY_FILE);
//        if (!file.exists() && !file.isDirectory()) {
//            createEmptyXrayFile();
//        }
//        addCurrentTestToXrayFile(oneTestResult.toString(5));
//    }
//
//    @Synchronized
//    private static void createEmptyXrayFile() {
//
//        try {
//            Files.write(Paths.get(XRAY_FILE), JSON_EMPTY
//                    .getBytes(), StandardOpenOption.CREATE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Synchronized
//    private static void addCurrentTestToXrayFile(String textToAdd) {
//
//        try (RandomAccessFile xrayFile = new RandomAccessFile(XRAY_FILE, "rw")) {
//            xrayFile.seek(xrayFile.length() - JSON_END.length());
//            if (xrayFile.length() == JSON_EMPTY.length()) {
//                xrayFile.writeBytes(textToAdd + JSON_END);
//            } else {
//                xrayFile.writeBytes(JSON_COMMA + textToAdd + JSON_END);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void createDirectory() {
//        File directory = new File(PATH_XRAY_JSON);
//        directory.mkdirs();
//    }
//
//    private static String encodeFileToBase64Binary(String fileName) {
//        File file = new File(fileName);
//        String encoded = null;
//        try {
//            encoded = Base64.getEncoder()
//                    .encodeToString(FileUtils.readFileToByteArray(file));
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//        return encoded;
//    }
//
//}
