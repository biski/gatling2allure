package com.wojtek;

import com.biski.processors.RequestProcessor;
import com.biski.processors.ResponseProcessor;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.testng.Assert.assertEquals;

/**
 * Created by Wojciech Biskowski <wbiskowski@gmail.com> on 14.03.18.
 */
public class SimpleRequestTest {

    @Test
    public void simplePassedRequest() throws IOException {

        String expectedBody = "{\n" +
                "    \"age\": 20,\n" +
                "    \"email\": \"john@lenon.com\",\n" +
                "    \"username\": \"john\"\n" +
                "}";
        String expectedResponse = "HTTP response:\n" +
                "status=\n" +
                "200 OK\n" +
                "headers=\n" +
                "Access-Control-Allow-Headers: Content-Type, Authorization, Correlation-Id\n" +
                "Access-Control-Allow-Methods: GET, POST\n" +
                "Access-Control-Allow-Origin: *\n" +
                "API-Version: 1\n" +
                "Content-Type: application/json; charset=utf-8\n" +
                "Processing-Time: 200\n" +
                "Content-Length: 19\n" +
                "\n";
        String expectedResponseBody = "{\"userId\": \"213214\"}";

        Path path = Paths.get("src/test/resources/simplePassedRequest.txt");
        RequestProcessor r = new RequestProcessor(new ArrayList<>(Files.readAllLines(path)));
        assertEquals(r.getRequestType(), "POST");
        assertEquals(r.getUrl(), "http://127.0.0.1:1000/create");
        assertEquals(r.getRequestName(), "simple request");
        assertEquals(r.getSuccessful(), Boolean.TRUE);
        assertEquals(r.getStringBody(), expectedBody);

        ResponseProcessor rp = r.getResponseProcessor();
        assertEquals(rp.getResponse(), expectedResponse);
        assertEquals(rp.getResponseBody(), expectedResponseBody);

    }

    @Test
    public void simpleFailedRequest() throws IOException {

        String expectedBody = "{\n" +
                "    \"age\": 20,\n" +
                "    \"email\": \"john@lenon.com\",\n" +
                "    \"username\": \"john\"\n" +
                "}";
        String expectedResponse = "HTTP response:\n" +
                "status=\n" +
                "200 OK\n" +
                "headers=\n" +
                "Access-Control-Allow-Headers: Content-Type, Authorization, Correlation-Id\n" +
                "Access-Control-Allow-Methods: GET, POST\n" +
                "Access-Control-Allow-Origin: *\n" +
                "API-Version: 1\n" +
                "Content-Type: application/json; charset=utf-8\n" +
                "Processing-Time: 200\n" +
                "Content-Length: 19\n" +
                "\n";
        String expectedResponseBody = "{\"userId\": \"213214\"}";

        Path path = Paths.get("src/test/resources/simpleFailedRequest.txt");
        RequestProcessor r = new RequestProcessor(new ArrayList<>(Files.readAllLines(path)));
        assertEquals(r.getRequestType(), "POST");
        assertEquals(r.getUrl(), "http://127.0.0.1:1000/create");
        assertEquals(r.getRequestName(), "simple failed request");
        assertEquals(r.getSuccessful(), Boolean.FALSE);
        assertEquals(r.getFailureMessage(), "jsonPath($.xxx).find.is(yyy), but actually found uuu");
        assertEquals(r.getStringBody(), expectedBody);

        ResponseProcessor rp = r.getResponseProcessor();
        assertEquals(rp.getResponse(), expectedResponse);
        assertEquals(rp.getResponseBody(), expectedResponseBody);

    }
}
