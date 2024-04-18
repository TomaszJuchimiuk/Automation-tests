package com.capgemini.mrchecker.webapi;

import com.capgemini.allure.AllureEnvironmentGenerator;
import com.capgemini.mrchecker.test.core.BaseTest;
import org.junit.jupiter.api.BeforeAll;

public class BaseTestWebAPI extends BaseTest {

    @BeforeAll
    public static void mySetUpClass() {
        AllureEnvironmentGenerator allureEnvironmentGenerator = new AllureEnvironmentGenerator();
        allureEnvironmentGenerator.setEnvProperties();

    }
}
