package com.capgemini.mrchecker.webapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data

public class RequestBodyModel {
    @JsonProperty("zone")
    private String zone;
    @JsonProperty("queueType")
    private String queueType;
    @JsonProperty("queue")
    private String queue;
    @JsonProperty("jsonData")
    private String jsonData;
    @JsonProperty("authData")
    private String authData;
}

