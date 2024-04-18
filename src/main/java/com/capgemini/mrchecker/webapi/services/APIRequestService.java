package com.capgemini.mrchecker.webapi.services;

import com.capgemini.mrchecker.webapi.models.RequestBodyModel;
import com.capgemini.mrchecker.webapi.utils.JSONReader;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class APIRequestService {
    final static String keyStore = "src/test/resources/certificates/keystore.p12";
    final static String trustStore = "src/test/resources/certificates/truststore.p12";
    final static String certificatePassword = "password";



    @Step("GET on {url} on the endpoint {endpoint}")
    public static Response getRequest(String url, String endpoint) throws IOException {
        RestAssured.baseURI = url;
        return RestAssured.given()
                .keyStore(keyStore, certificatePassword)
                .trustStore(trustStore, certificatePassword)
                .get(endpoint);

    }

    @Step("POST on {url} adding new value: {newValue}")
    public static Response postRequestWithModifiedJSON(String url, String jsonFilePath, String newValue) {
        RestAssured.baseURI = url;

        // Modify JSON content
        RequestBodyModel requestBody = null;
        try {
            requestBody = JSONModificationService.modifyJSONFile(jsonFilePath, newValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Make a POST request with the modified JSON body
        return given()
                .keyStore(keyStore, certificatePassword)
                .trustStore(keyStore, certificatePassword)
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post();
    }

    @Step("POST on {url}")
    public static Response postRequestWithJSON(String url, String jsonFilePath) throws IOException {
        RestAssured.baseURI = url;
        RequestBodyModel requestBody = JSONReader.readJSONFromFile(jsonFilePath, RequestBodyModel.class);;

        // Make a POST request with the JSON body
        return given()
                .keyStore(keyStore, certificatePassword)
                .trustStore(keyStore, certificatePassword)
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post();
    }
}
