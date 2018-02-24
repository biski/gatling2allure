package com.biski.processors;

import com.biski.objects.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wojciech on 13.01.18.
 */
public class SessionProcessor {
    private StringBuilder session;
    private String scenarioName;
    private String userId;
    private String attributes;
    private StringBuilder attributesCsv;
    private HashMap<String, String> attributesMap;
    private Long startDate;

    public SessionProcessor(StringBuilder session) {
        this.session = session;
    }

    public Session parse() {
        /**
         * case class Session(
         scenario:   String,
         userId:     Long,
         attributes: Map[String, Any] = Map.empty,
         startDate:  Long             = nowMillis,
         drift:      Long             = 0L,
         baseStatus: Status           = OK,
         blockStack: List[Block]      = Nil,
         onExit:     Session => Unit  = Session.NothingOnExit
         )
         */

        ArrayList<StringBuffer> parameters = new ArrayList<>(50);
        int i = 0;
        int openBrackets = 0;
        parameters.add(new StringBuffer(500));
        for (char c : session.toString()
                .replace("Session:\nSession(", "")
                .replaceFirst("\\)$", "")
                .toCharArray()) {
//            //System.out.println(c);
            if (c == '(') openBrackets++;
            if (c == ')') {
                openBrackets--;
            }
            if (c == ',' && openBrackets == 0) {
                i++;
                if (parameters.size() <= i) parameters.add(i, new StringBuffer());
                continue;
            }
            parameters.get(i).append(c);

        }

        scenarioName = parameters.get(0).toString();
        userId = parameters.get(1).toString();
        attributes = parameters.get(2).toString();
        startDate = Long.getLong(parameters.get(3).toString());


        attributesMap = new HashMap<>();
        attributesCsv = new StringBuilder(100);


        for (String pair : attributes
                .replace("Map(", "")
                .replaceFirst(".$", "")
                .split(", (?![^(]*\\))")) {
            String[] keyValue = pair.split("->");
            attributesMap.put(keyValue[0].trim(), keyValue[1].trim());
            attributesCsv.append(keyValue[0]) .append( ",\"").append(keyValue[1]).append("\"\n");
        }

        return new Session(scenarioName, userId, attributes, startDate);
    }


    public String getScenarioName() {
        return scenarioName;
    }

    public String getUserId() {
        return userId;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getAttributesCsv() {
        return attributesCsv.toString();
    }

    public Long getStartDate() {
        return startDate;
    }

    public HashMap<String, String> getAttributesMap() {
        return attributesMap;
    }
}
