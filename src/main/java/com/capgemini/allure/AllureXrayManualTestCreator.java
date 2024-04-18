//package com.capgemini.allure;
//
//import com.capgemini.mrchecker.test.core.logger.BFLogger;
//import io.qameta.allure.listener.TestLifecycleListener;
//import io.qameta.allure.model.Label;
//import io.qameta.allure.model.Link;
//import io.qameta.allure.model.StepResult;
//import io.qameta.allure.model.TestResult;
//import io.qameta.allure.util.ResultsUtils;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.StringJoiner;
//
//
//public class AllureXrayManualTestCreator implements TestLifecycleListener {
////THis Class creates files according to Xray documentation:
//// https://docs.getxray.app/display/XRAY/Examples+using+Test+Case+Importer
//
//    private static final String PATH_XRAY_JSON = "target/XRAY";
//    private static final String XRAY_FILE = PATH_XRAY_JSON + "/" + "xrayManualTestList.csv";
//    private static final String NEW_LINE = "\n";
//    private static final String STATUS = "AUTOMATED TEST";
//    private static final String KEYWORD_FOR_EXPECTED = "Assert";
//    private static final String KEYWORD_FOR_DATA = "With data: ";
//    private static final String LABELS = "TestingTeam";
//
//
//    private void addTestToFile(XrayTestCase xrayTestCase) {
//        Path path = Paths.get(XRAY_FILE);
//        File xrayFile = new File(XRAY_FILE);
//        boolean fileCreated = false;
//        synchronized (this) {
//            try {
//                if (xrayFile.createNewFile()) {
//                    fileCreated = true;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
//
//                if (fileCreated) {
//                    writer.write(xrayTestCase.getHeader());
//                }
//                writer.write(xrayTestCase.toString());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void afterTestWrite(TestResult testResult) {
//        BFLogger.logInfo("AllureManualTestCreator");
//
//        if (getXrayTestKey(testResult).equals("")) {
//            AllureXrayManualTestCreator manualTestCreator = new AllureXrayManualTestCreator();
//            XrayTestCase xrayTestCase = manualTestCreator.createTest(testResult);
//            createDirectory();
//            addTestToFile(xrayTestCase);
//        }
//    }
//
//    private XrayTestCase createTest(TestResult testResult) {
//        XrayTestCase xrayTestCase = new XrayTestCase();
//        xrayTestCase.setTcid(String.valueOf(testResult.getName().hashCode()).replace("-", ""));
//        xrayTestCase.setSummary(testResult.getName());
//        xrayTestCase.setDescription(createDescription(testResult));
//        xrayTestCase.setStatus(STATUS);
//        xrayTestCase.setLabels(LABELS);
//        xrayTestCase.setComponent(getFeature(testResult).replace(" ", "-"));
//        xrayTestCase.setRepository(getFeature(testResult).replace(" ", "-"));
//        xrayTestCase.setTestCaseStepsData(testResult.getSteps());
//
//        return xrayTestCase;
//    }
//
//    private String createDescription(TestResult result) {
//        return result.getDescription().isEmpty() ? "Tests for " + getDocumentation(result) : result.getDescription();
//    }
//
//    private String getFeature(TestResult result) {
//        return result.getLabels()
//                .stream()
//                .filter(labels -> labels.getName().equals(ResultsUtils.FEATURE_LABEL_NAME))
//                .map(Label::getValue)
//                .findFirst()
//                .orElse("");
//    }
//
//    private String getDocumentation(TestResult result) {
//        return result.getLinks()
//                .stream()
//                .filter(links -> links.getName().equals("documentation"))
//                .map(Link::getUrl)
//                .findFirst()
//                .orElse("");
//    }
//
//    private void createDirectory() {
//        File directory = new File(PATH_XRAY_JSON);
//        directory.mkdirs();
//    }
//
//    private String getXrayTestKey(TestResult result) {
//        return result.getLinks()
//                .stream()
//                .filter(link -> link.getType().equals(ResultsUtils.TMS_LINK_TYPE))
//                .map(Link::getName)
//                .findFirst()
//                .orElse("");
//    }
//
//    @Data
//    static class XrayTestCase {
//        String tcid;
//        String summary;
//        String description;
//        String status;
//        String labels;
//        String component;
//        String repository;
//        List<XrayTestCaseStepsData> testCaseStepsData = new ArrayList<>();
//
//        void setTestCaseStepsData(List<StepResult> allureSteps) {
//            List<String> stepList = allureSteps
//                    .stream()
//                    .map(step -> step.getName()
//                            .replaceAll("\\[INFO\\] |.+\\| ", "")).toList();
//
//            //create xray actions and xray expected results from stepList
//            for (int i = 0; i < stepList.size(); i++) {
//                StringJoiner expected = new StringJoiner(" & ");
//                StringJoiner withData = new StringJoiner(" & ");
//
//                //decision if it is action of expected
//                if (stepList.get(i).contains(KEYWORD_FOR_EXPECTED) || stepList.get(i).contains(KEYWORD_FOR_DATA)) {
//                    continue;
//                }
//
//                // create data string
//                for (int j = i + 1; j < stepList.size(); j++) {
//                    if (!stepList.get(j).contains(KEYWORD_FOR_DATA)) {
//                        continue;
//                    } else {
//                        withData.add(stepList.get(j).replace("\n", " ").replace(KEYWORD_FOR_DATA, ""));
//                    }
//                }
//
//                // create expected string
//                for (int j = i + 1; j < stepList.size(); j++) {
//                    if (!stepList.get(j).contains(KEYWORD_FOR_EXPECTED)) {
//                        continue;
//                    } else {
//                        expected.add(stepList.get(j).replace("\n", " "));
//                    }
//                }
//                XrayTestCaseStepsData oneStepData = new XrayTestCaseStepsData(stepList.get(i), withData.toString(), expected.toString());
//                this.testCaseStepsData.add(oneStepData);
//            }
//        }
//
//        String getHeader() {
//            Field[] fields = XrayTestCase.class.getDeclaredFields();
//            StringJoiner joiner = new StringJoiner(";");
//
//            for (Field field : fields) {
//                BFLogger.logInfo("declared field: " + field);
//                joiner.add(field.getName());
//            }
//
//            String header = joiner.toString().replace("testCaseStepsData", "step;data;expected");
//            return header + NEW_LINE;
//        }
//
//        @Override
//        public String toString() {
//
//            StringJoiner joiner = new StringJoiner(";");
//            joiner.add(this.tcid);
//            joiner.add(this.summary);
//            joiner.add(this.description);
//            joiner.add(this.status);
//            joiner.add(this.labels);
//            joiner.add(this.component);
//            joiner.add(this.repository);
//            StringBuilder steplines = new StringBuilder();
//            for (int i = 0; i < this.testCaseStepsData.size(); i++) {
//                if (i == 0) {
//                    joiner.add(this.testCaseStepsData.get(i).toString());
//                } else {
//                    steplines
//                            .append(NEW_LINE)
//                            .append(this.tcid)
//                            .append(";".repeat(7))
//                            .append(this.testCaseStepsData.get(i).toString());
//                }
//            }
//            return joiner + steplines.toString() + NEW_LINE;
//        }
//
//        @Data
//        @AllArgsConstructor
//        static class XrayTestCaseStepsData {
//            String step;
//            String data;
//            String expected;
//
//            @Override
//            public String toString() {
//                StringJoiner joiner = new StringJoiner(";");
//                joiner.add(this.step);
//                joiner.add(this.data);
//                joiner.add(this.expected);
//                return joiner.toString();
//            }
//        }
//    }
//
//}