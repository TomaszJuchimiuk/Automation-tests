package com.capgemini.mrchecker.webapi.imap;

import com.capgemini.mrchecker.webapi.BaseTestWebAPI;
import com.capgemini.mrchecker.webapi.CustomAssert;
import com.capgemini.mrchecker.webapi.example.env.GetEnvironmentParam;
import com.capgemini.mrchecker.webapi.services.APIRequestService;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Feature("Fetaure")
@Owner("tjuchimi")
@TmsLink("xxx")
@Severity(SeverityLevel.CRITICAL)
public class QueuingMockTest extends BaseTestWebAPI {

    @Test
    @Story("GET")
    @DisplayName("GET")
    public void get() throws IOException {
        GetEnvironmentParam.PAGE.getValue();
        Response response = APIRequestService.getRequest(GetEnvironmentParam.PAGE.getValue(), "/api/2q");


        Assertions.assertAll(
                // status is 400 or 400 (response also is changed), it has to be investigated
                () -> CustomAssert.assertTrue(response, "Missing parameter(s) / Invalid body format"),
                () -> CustomAssert.assertEquals(400, response.getStatusCode())
        );
    }

    @Test
//    @Issue("21xxx")//jira bug
    @Story("POST")
    @DisplayName("POST")
    public void POSTjsonModel() throws IOException {

        Response response = APIRequestService.postRequestWithJSON(GetEnvironmentParam.PAGE.getValue() + "/api/2q", "src/resources/testdata/bodyApi2q.json");
        Assertions.assertAll(
                () -> CustomAssert.assertEquals(200, response.getStatusCode()),
                () -> CustomAssert.assertTrue(response, "OK")
        );
    }

    @Test
//    @Issue("21xxx")//jira bug
    @Story("POST")
    @DisplayName("POST with modified json")
    public void POSTSetTheValue() throws IOException {

        Response response = APIRequestService.postRequestWithModifiedJSON(GetEnvironmentParam.PAGE.getValue() + "/api/2q", "src/resources/testdata/bodyApi2q.json", "test");
        Assertions.assertAll(
                () -> CustomAssert.assertEquals(400, response.getStatusCode()),
                () -> CustomAssert.assertTrue(response, "Missing parameter(s) / Invalid body format")
        );
    }
}