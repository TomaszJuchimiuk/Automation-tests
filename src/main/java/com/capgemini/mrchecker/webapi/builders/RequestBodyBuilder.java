package com.capgemini.mrchecker.webapi.builders;

import com.capgemini.mrchecker.webapi.models.RequestBodyModel;

public class RequestBodyBuilder {
    public static RequestBodyModel buildRequestBody(String key, String value){
        RequestBodyModel requestBody= new RequestBodyModel();
        requestBody.setZone(key);
        requestBody.setZone(value);
        return requestBody;
    }
}
