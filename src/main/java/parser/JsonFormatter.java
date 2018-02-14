package parser;

import org.json.JSONObject;

public class JsonFormatter {
        public String format(String body) {
            try {
                JSONObject json = new JSONObject(body.trim());
                return json.toString(4); // Print it with specified indentation
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }