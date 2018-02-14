package processors;

import objects.Request;
import objects.Session;
import parser.JsonFormatter;

import java.util.ArrayList;

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
    ArrayList<String> headers = new ArrayList<>();
    ArrayList<String> httpRequest = new ArrayList<>();
    ArrayList<String> httpResponse = new ArrayList<>();
    ArrayList<String> sessionBuffe = new ArrayList<>();


    public RequestProcessor(ArrayList<String> buff) {
        this.buffer = buff;

        parseRequest();
    }

    private Request parseRequest() {
        requestName = buffer.get(1).split(":")[0];
        isSuccessful = buffer.get(1).split(":")[1].trim().equals("OK");

        for (int i = 0; i < buffer.size(); i++) {
            if(buffer.get(i).equals("HTTP request:")) {
                while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
                    httpRequest.add(buffer.get(i++));
                }
                parseRequest(httpRequest);
            }


            if (i < buffer.size() && buffer.get(i).equals("HTTP response:")) {
                while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
                    httpResponse.add(buffer.get(i++));
                }
                responseProcessor = new ResponseProcessor(httpResponse);
            }

            if (i < buffer.size() && buffer.get(i).equals("Session:")) {
                while (i < buffer.size() && !buffer.get(i).contains(BREAK)) {
                    sessionBuffe.add(buffer.get(i++));
                }
                session = new SessionProcessor(sessionBuffe).parse();
            }
        }

        return new Request(session);
    }

    public void parseRequest(ArrayList<String> request) {

        String[] split = request.get(1).split(" ");
        requestType = split[0];
        url = split[1];

        for (int i = 0; i < request.size(); i++) {
            if (request.get(i).equals("headers=")) {
                i++;
                while (i < request.size()
                        && !request.get(i).matches("^[a-zA-Z]*=.*")) {
                    headers.add(request.get(i++));
                }
            }
            if (request.get(i).startsWith("stringData")
                    || request.get(i).startsWith("compositeByteData")) {
                StringBuilder sb = new StringBuilder();
                i++;
                while (i < request.size()
                        && !request.get(i).matches("^[a-zA-Z]*=.*")
                        ) {

                    sb.append(request.get(i++));

                }
                stringBody = new JsonFormatter().format(sb.toString());
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

    public ArrayList<String> getHeaders() {
        return headers;
    }

    public String getUrl() {
        return url;
    }

    public String getStringBody() {
        return stringBody;
    }

    public ArrayList<String> getHttpRequest() {
        return httpRequest;
    }

    public ArrayList<String> getHttpResponse() {
        return httpResponse;
    }

    public ArrayList<String> getSessionBuffe() {
        return sessionBuffe;
    }
}
