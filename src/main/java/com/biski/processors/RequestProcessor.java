package com.biski.processors;

import com.biski.objects.Request;
import com.biski.objects.Session;
import com.biski.parser.JsonFormatter;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 *
 *   buff.append(Eol).append(">>>>>>>>>>>>>>>>>>>>>>>>>>").append(Eol)
 buff.append("Request:").append(Eol).append(s"$fullRequestName: $status ${errorMessage.getOrElse("")}").append(Eol)
 buff.append("=========================").append(Eol)
 buff.append("Session:").append(Eol).append(tx.session).append(Eol)
 buff.append("=========================").append(Eol)
 buff.append("HTTP request:").append(Eol).appendRequest(tx.request.ahcRequest, response.nettyRequest, configuration.core.charset)
 buff.append("=========================").append(Eol)
 buff.append("HTTP response:").append(Eol).appendResponse(response).append(Eol)
 buff.append("<<<<<<<<<<<<<<<<<<<<<<<<<")
 */
public class RequestProcessor {
    public static int cnt;
    public static final String BREAK = "=========================";
    ArrayList<String> buffer;
    String requestType;
    String url;
    String requestName;
    Boolean isSuccessful;
    Session session;
    String stringBody;
    ResponseProcessor responseProcessor;
    StringBuilder response = new StringBuilder();
    StringBuilder request = new StringBuilder();
    StringBuilder headers = new StringBuilder(500);
    StringBuilder httpRequest = new StringBuilder(5000);
    StringBuilder  httpResponse = new StringBuilder(5000);
    StringBuilder sessionBuffe = new StringBuilder(4000);


    public RequestProcessor(ArrayList<String> buff) {
        this.buffer = buff;

        parseRequest();
    }

    private Request parseRequest() {
        requestName = buffer.get(1).split(":")[0];
        isSuccessful = buffer.get(1).split(":")[1].trim().equals("OK");

        System.out.println("Parsing request " + cnt++ + ": " + requestName);

        for (int i = 0; i < buffer.size(); i++) {
            if(buffer.get(i).equals("HTTP request:")) {
                while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
                    httpRequest.append(buffer.get(i++)).append("\n");
                }
                parseRequest(httpRequest);
            }


            if (i < buffer.size() && buffer.get(i).equals("HTTP response:")) {
                while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
                    i++;
//                    httpResponse.append(buffer.get(i++)).append("\n");
//                    httpResponse.append("body=test");
                }
//                responseProcessor = new ResponseProcessor(httpResponse);
                responseProcessor = new ResponseProcessor(new StringBuilder("boody=test"));
            }

            if (i < buffer.size() && buffer.get(i).equals("Session:")) {
                while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
                    sessionBuffe.append(buffer.get(i++)).append("\n");
                }
                session = new SessionProcessor(sessionBuffe).parse();
            }
        }

        return new Request(session);
    }

    public void parseRequest(StringBuilder r) {

        String[] request = r.toString().split("\n");

        String[] split = request[1].split(" ");
        requestType = split[0];
        url = split[1];

        for (int i = 0; i < request.length; i++) {
            if (request[i].equals("headers=")) {
                i++;
                while (i < request.length
                        && !request[i].matches("^[a-zA-Z]*=.*")) {
                    headers.append(request[i++]).append("\n");
                }
            }
            if (request[i].startsWith("stringData")
                    || request[i].startsWith("compositeByteData")) {
                StringBuilder sb = new StringBuilder();
                sb.append(request[i++].replaceAll("(stringData=|compositeByteData=)", ""));
                while (i < request.length
                        && !request[i].matches("^[a-zA-Z]*=.*")
                        ) {

                    sb.append(request[i++]);

                }
                String requestData = sb.toString().trim();
                if(!requestData.equals("")) {
                    stringBody = new JsonFormatter().format(requestData);
                }
            }
        }
    }

    public ArrayList<String> getBuffer() {
        return buffer;
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

    public StringBuilder getResponse() {
        return response;
    }

    public StringBuilder getRequest() {
        return request;
    }

    public StringBuilder getHeaders() {
        return headers;
    }

    public String getUrl() {
        return url;
    }

    public String getStringBody() {
        return stringBody;
    }

    public StringBuilder getHttpRequest() {
        return httpRequest;
    }

    public StringBuilder getHttpResponse() {
        return httpResponse;
    }

    public StringBuilder getSessionBuffe() {
        return sessionBuffe;
    }
}
