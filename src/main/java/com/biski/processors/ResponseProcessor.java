package com.biski.processors;


import java.util.ArrayList;

/**
 * Created by wojciech on 13.01.18.
 */
public class ResponseProcessor {
    private String response = "";
    private String responseBody = "";

    public ResponseProcessor(StringBuilder fullResponse) {
//        StringBuilder stringBuilder = new StringBuilder();
//        fullResponse.forEach(x -> stringBuilder.append(x).append("\n"));
        String[] split = fullResponse.toString().split("body=");
        if (split.length > 1) {
            response = split[0];
//            responseBody = new JsonFormatter().format(split[1]);
            responseBody = "";
        } else {
            response = fullResponse.toString();
        }

    }

    public String getResponse() {
        return response;
    }

    public String getResponseBody() {
        return responseBody;
    }


}
