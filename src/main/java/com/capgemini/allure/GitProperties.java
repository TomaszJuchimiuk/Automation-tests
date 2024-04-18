package com.capgemini.allure;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class GitProperties {
    private static final String FILENAME = "target/git.properties";
    private final Properties gitProp = new Properties();

    private boolean loadFile() {
        try (FileInputStream file = new FileInputStream(FILENAME)) {
            this.gitProp.load(file);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    String getGitBranchName() {
        if (!loadFile()) {
            return "";
        }
        return this.gitProp.getProperty("git.branch");
    }

    String getGitCommitID() {
        if (!loadFile()) {
            return "";
        }
        return this.gitProp.getProperty("git.commit.id.full");
    }

}