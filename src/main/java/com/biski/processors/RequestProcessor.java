package com.biski.processors;

import com.biski.objects.Request;
import com.biski.objects.Session;
import com.biski.parser.JsonFormatter;

import java.util.ArrayList;
import java.util.Optional;

/**
 * buff.append(Eol).append(">>>>>>>>>>>>>>>>>>>>>>>>>>").append(Eol)
 * buff.append("Request:").append(Eol).append(s"$fullRequestName: $status ${errorMessage.getOrElse("")}").append(Eol)
 * buff.append("=========================").append(Eol)
 * buff.append("Session:").append(Eol).append(tx.session).append(Eol)
 * buff.append("=========================").append(Eol)
 * buff.append("HTTP request:").append(Eol).appendRequest(tx.request.ahcRequest, response.nettyRequest, configuration.core.charset)
 * buff.append("=========================").append(Eol)
 * buff.append("HTTP response:").append(Eol).appendResponse(response).append(Eol)
 * buff.append("<<<<<<<<<<<<<<<<<<<<<<<<<")
 */
public class RequestProcessor {

    private static final Boolean skipSuccessResponseBody = false;
    private static final String BREAK = "=========================";
    private static int cnt;


    private ArrayList<String> buffer;
    private String requestType;
    private String url;
    private String requestName;
    private Boolean isSuccessful;
    private String failureMessage;
    private Session session;
    private String stringBody;
    private ResponseProcessor responseProcessor;
    private StringBuilder headers = new StringBuilder(500);
    private StringBuilder httpRequest = new StringBuilder(5000);
    private StringBuilder sessionBuffer = new StringBuilder(4000);
    private StringBuilder httpResponse = new StringBuilder(5000);

    public RequestProcessor(ArrayList<String> buff) {
        this.buffer = buff;

        parseRequest();
    }

    public StringBuilder getHeaders() {
        return headers;
    }

    private Request parseRequest() {
        requestName = buffer.get(1).split(":")[0];
        isSuccessful = buffer.get(1).split(":")[1].trim().equals("OK");

        if (!isSuccessful) {
            failureMessage = Optional.of(buffer.get(1).split("KO")[1]).orElse("");
        }

        System.out.println("Parsing request " + cnt++ + ": " + requestName);

        for (int i = 0; i < buffer.size(); i++) {
            if (i < buffer.size() && buffer.get(i).equals("HTTP request:")) {
                i = readRequest(i);
            }

            if (i < buffer.size() && buffer.get(i).equals("HTTP response:")) {
                if(skipSuccessResponseBody && isSuccessful) {
                    i++;
                } else {
                    i = readResponse(i);
                }
            }

            if (i < buffer.size() && buffer.get(i).equals("Session:")) {
                i = readSession(i);
            }
        }

        return new Request(session);
    }

    private int readRequest(int i) {
        while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
            httpRequest.append(buffer.get(i++)).append("\n");
        }
        parseRequest(httpRequest);
        return i;
    }

    private int readSession(int i) {
        while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
            sessionBuffer.append(buffer.get(i++)).append("\n");
        }
        session = new SessionProcessor(sessionBuffer).parse();
        return i;
    }

    private int readResponse(int i) {
        while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
            httpResponse.append(buffer.get(i++)).append("\n");
        }
        responseProcessor = new ResponseProcessor(httpResponse);
        return i;
    }

    public void parseRequest(StringBuilder r) {

        String[] request = r.toString().split("\n");

        String[] split = request[1].split(" ");
        requestType = split[0];
        url = split[1];

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < request.length; i++) {
            if (request[i].equals("headers=")) {
                i++;
                while (i < request.length
                        && !request[i].matches("^[a-zA-Z]*=.*")) {
                    headers.append(request[i++]).append("\n");
                }
            }
            if (i < request.length
                    && (request[i].startsWith("stringData")
                    || request[i].startsWith("compositeByteData"))) {

                sb.append(request[i++].replaceAll("(stringData=|compositeByteData=)", ""));
                while (i < request.length
                        && !request[i].matches("^[a-zA-Z]*=.*")
                        ) {

                    sb.append(request[i++]);

                }
                String requestData = sb.toString().trim();
                if (!requestData.equals("")) {
                    stringBody = new JsonFormatter().format(requestData);
                }
                sb.setLength(0);
            }
        }
    }

    public String getRequestType() {
        return requestType;
    }

    public String getRequestName() {
        return requestName;
    }

    public Boolean getSuccessful() {
        return isSuccessful;
    }

    public Session getSession() {
        return session;
    }

    public ResponseProcessor getResponseProcessor() {
        return responseProcessor;
    }

    public String getUrl() {
        return url;
    }

    public String getStringBody() {
        return stringBody;
    }


    public StringBuilder getSessionBuffer() {
        return sessionBuffer;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
