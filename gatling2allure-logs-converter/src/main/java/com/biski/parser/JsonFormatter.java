package com.biski.parser;

import org.json.JSONObject;

public class JsonFormatter {
    public static String format(String body) {
        try {
            JSONObject json = new JSONObject(body.trim());
            return json.toString(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}