package com.capgemini.mrchecker.webapi;

import io.restassured.response.Response;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;


import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
@Slf4j

public class CustomAssert {
    @Step("assert that {actual} is {expected}")
    public static void assertEquals(int expected, int actual) {
        log.info("assert that {actual} is {expected}");
        Awaitility.await()
                .atMost(60, SECONDS)
                .pollInterval(5, SECONDS)
                .untilAsserted(() -> {
                    Assertions.assertEquals(expected, actual, "");
                });
    }

    @Step("assert that {actual} is {expected} at time {timeMinutes} and interval {intervalSeconds}")
    public static void assertEquals(int expected, int actual, int timeMinutes, int intervalSeconds) {
        log.info("assert that {actual} is {expected} at time {timeMinutes} and interval {intervalSeconds}");
        Awaitility.await()
                .atMost(timeMinutes, MINUTES)
                .pollInterval(intervalSeconds, SECONDS)
                .untilAsserted(() -> {
                    Assertions.assertEquals(expected, actual, "");
                });
    }

    @Step("assert that response contains value: {value}")
    public static void assertTrue(Response response, String value) {
        log.info("assert true that {response} is {value}");
        Assertions.assertTrue(response.getBody().asString().contains(value), "");
    }

}
