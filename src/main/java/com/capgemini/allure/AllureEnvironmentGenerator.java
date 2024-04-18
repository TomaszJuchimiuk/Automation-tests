package com.capgemini.allure;

import com.capgemini.mrchecker.test.core.logger.BFLogger;
import com.capgemini.mrchecker.webapi.example.env.GetEnvironmentParam;
import lombok.Synchronized;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AllureEnvironmentGenerator {
    private static final String ENVIRONMENT_FILE_PATH = "target/allure-results/environment.properties";
    private static final String NEW_LINE = "\n";

    public void setEnvProperties() {
        if (createFile()) {
            addEnvironmentVariableToFile("Operating.System= " + System.getProperty("os.name") + " | " + System.getProperty("os.version"));
            addEnvironmentVariableToFile("Environment= " + System.getProperty("env", "ENV1"));
            addEnvironmentVariableToFile("Base.url= " + GetEnvironmentParam.IMFA_ZDI.getValue());

            GitProperties gitProperties = new GitProperties();
            addEnvironmentVariableToFile("Branch=" + gitProperties.getGitBranchName());
            addEnvironmentVariableToFile("Commit.id=" + gitProperties.getGitCommitID());
        }
    }

    @Synchronized
    private void addEnvironmentVariableToFile(String variable) {
        BFLogger.logInfo("Add Allure environment variable: " + variable);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(ENVIRONMENT_FILE_PATH), StandardOpenOption.APPEND)) {
            writer.write(variable + NEW_LINE);
        } catch (IOException ex) {
            BFLogger.logError("Environment file exists");
        }

    }

    private boolean createFile() {
        File environmentFile = new File(ENVIRONMENT_FILE_PATH);
        if (!environmentFile.exists()) {
            environmentFile.getParentFile()
                    .mkdirs();
            try {
                return environmentFile.createNewFile();
            } catch (IOException e) {
                BFLogger.logError("Environment file not created");
                return false;
            }
        }
        return false;
    }
}