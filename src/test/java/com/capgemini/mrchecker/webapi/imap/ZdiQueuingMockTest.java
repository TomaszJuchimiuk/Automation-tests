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

@Feature("ZDI Queuing Mock Rest API")
@Owner("tjuchimi")
@TmsLink("IMFVA-265")
@Severity(SeverityLevel.CRITICAL)
public class ZdiQueuingMockTest extends BaseTestWebAPI {

    @Test
    @Story("GET")
    @DisplayName("GET ZDI Queuing")
    public void zdiQueuingApi2q() throws IOException {
        GetEnvironmentParam.IMFA_ZDI.getValue();
        Response response = APIRequestService.getRequest(GetEnvironmentParam.IMFA_ZDI.getValue(), "/api/2q");


        Assertions.assertAll(
                // status is 400 or 400 (response also is changed), it has to be investigated
                () -> CustomAssert.assertTrue(response, "Missing parameter(s) / Invalid body format"),
                () -> CustomAssert.assertEquals(400, response.getStatusCode())
        );
    }

    @Test
//    @Issue("21xxx")//jira bug
    @Story("POST")
    @DisplayName("POST ZDI Queuing")
    public void POSTjsonModel() throws IOException {

        Response response = APIRequestService.postRequestWithJSON(GetEnvironmentParam.IMFA_ZDI.getValue() + "/api/2q", "src/resources/testdata/bodyApi2q.json");
        Assertions.assertAll(
                () -> CustomAssert.assertEquals(200, response.getStatusCode()),
                () -> CustomAssert.assertTrue(response, "OK")
        );
    }

    @Test
//    @Issue("21xxx")//jira bug
    @Story("POST")
    @DisplayName("POST ZDI Queuing with modified json")
    public void POSTSetTheValue() throws IOException {

        Response response = APIRequestService.postRequestWithModifiedJSON(GetEnvironmentParam.IMFA_ZDI.getValue() + "/api/2q", "src/resources/testdata/bodyApi2q.json", "test");
        Assertions.assertAll(
                () -> CustomAssert.assertEquals(400, response.getStatusCode()),
                () -> CustomAssert.assertTrue(response, "Missing parameter(s) / Invalid body format")
        );
    }
}
