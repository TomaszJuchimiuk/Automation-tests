package com.capgemini.mrchecker.webapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JSONReader {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readJSONFromFile(String filePath, Class<T> valueType) throws IOException {
        return objectMapper.readValue(new File(filePath), valueType);
    }
}
