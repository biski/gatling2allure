package com.biski.processors;


import com.biski.parser.JsonFormatter;

/**
 * Created by Wojciech Biskowski <wbiskowski@gmail.com> on 13.01.18.
 */
public class ResponseProcessor {
    private String response;
    private String responseBody;

    public ResponseProcessor(StringBuilder fullResponse) {
        String[] split = fullResponse.toString().split("body=");
        if (split.length > 1) {
            response = split[0];
            if (response.contains("application/json")) {
                responseBody = JsonFormatter.format(split[1]);
            } else {
                responseBody = split[1];
            }
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
