package com.biski.parser;

import org.json.JSONObject;

public class JsonFormatter {
        public String format(String body) {
            try {
                JSONObject json = new JSONObject(body.trim());
                String formattedJson = json.toString(4);
                json = null;
                return formattedJson; // Print it with specified indentation
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }