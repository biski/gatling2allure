package processors;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wojciech on 13.01.18.
 */
public class ResponseProcessor {
    private String response = "";
    private String responseBody = "";

    public ResponseProcessor(ArrayList fullResponse) {
        StringBuilder stringBuilder = new StringBuilder();
        fullResponse.forEach(stringBuilder::append);
        String[] split = stringBuilder.toString().split("body=");
        if (split.length > 0) {
            response = split[0];
            responseBody = split[1];
        } else {
            response = fullResponse.toString();
        }


        JSONObject json = null; // Convert text to object
        try {
            json = new JSONObject(responseBody);
        } catch (JSONException e) {
            //System.out.println(responseBody);
            e.printStackTrace();
        }
        try {
            responseBody = json.toString(4); // Print it with specified indentation
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        return response;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
