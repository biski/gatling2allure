package processors;

import objects.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wojciech on 13.01.18.
 */
public class SessionProcessor {
    private List<String> session;
    private String scenarioName;
    private String userId;
    private String attributes;
    private String attributesCsv;
    private HashMap<String, String> attributesMap;
    private Long startDate;

    public SessionProcessor(List<String> session) {
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
        StringBuilder sessionString = new StringBuilder();
        session.forEach(x -> sessionString.append(x + "\n"));

        ArrayList<StringBuffer> parameters = new ArrayList<>();
        int i = 0;
        int openBrackets = 0;
        parameters.add(new StringBuffer());
        for (char c : sessionString.toString()
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


        for (String pair : attributes
                .replace("Map(", "")
                .replaceFirst(".$", "")
                .split(", (?![^(]*\\))")) {
            String[] keyValue = pair.split("->");
            attributesMap.put(keyValue[0].trim(), keyValue[1].trim());
            attributesCsv += keyValue[0] + ",\"" + keyValue[1] + "\"\n";
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
        return attributesCsv;
    }

    public Long getStartDate() {
        return startDate;
    }

    public HashMap<String, String> getAttributesMap() {
        return attributesMap;
    }
}
