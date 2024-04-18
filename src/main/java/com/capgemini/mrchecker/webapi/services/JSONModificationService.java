package com.capgemini.mrchecker.webapi.services;

import com.capgemini.mrchecker.webapi.models.RequestBodyModel;
import com.capgemini.mrchecker.webapi.utils.JSONReader;

import java.io.IOException;

public class JSONModificationService {
    public static RequestBodyModel modifyJSONFile(String jsonFilePath, String newValue) throws IOException {
        RequestBodyModel requestBody = JSONReader.readJSONFromFile(jsonFilePath, RequestBodyModel.class);

        // Modify the value in the JSON body
        requestBody.setZone(newValue);

        return requestBody;
    }
}
