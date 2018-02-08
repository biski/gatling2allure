package parser;

import io.qameta.allure.FileSystemResultsWriter;
import io.qameta.allure.model.*;
import processors.RequestProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by wojciech on 25.11.17.
 */
public class LogParser {

    ArrayList<RequestProcessor> requests;
    public static final String REQUEST_START = ">>>>>>>>>>>>>>>>>>>>>>>>>>";
    public static final String REQUEST_END = "<<<<<<<<<<<<<<<<<<<<<<<<<";

    public static void main(String[] args) throws Exception {
        LogParser logParser = new LogParser();
        logParser.parseLogFile();
        logParser.debug();
        logParser.generateAllureData();

    }

    private void debug() {
        requests.forEach(request -> System.out.println(request.getSuccessful() + " " + request.getRequestName() + " " + request.getSession().getScenarioName() + " " + request.getUrl()));
    }

    private void generateAllureData() {
        File resultsFolder = new File("allure-results");

        FileSystemResultsWriter fileSystemResultsWriter = new FileSystemResultsWriter(resultsFolder.toPath());

        HashMap<String, TestResult> simulation = new HashMap<>();

        requests.forEach(request -> {
            TestResult allureTest;
            String simulationName = request.getSession().getScenarioName() + request.getSession().getUserId();

            allureTest = simulation.computeIfAbsent(simulationName, k -> new TestResult()
                    .withName(simulationName)
                    .withUuid(UUID.randomUUID().toString())
                    .withStatus(request.getSuccessful() ? Status.PASSED : Status.FAILED)
                    .withStart(request.getSession().getStartDate())
                    .withStop(request.getSession().getStartDate()+50));

            allureTest.getSteps().add(
                    new StepResult()
                            .withName(request.getRequestType() + " " + request.getRequestName())
                            .withAttachments(
                                    createAttachment("String body", "application/json", request.getStringBody(), fileSystemResultsWriter),
                                    createAttachment("Session", "application/json", request.getSession().getAttributes(), fileSystemResultsWriter),
                                    createAttachment("Session buffer", "application/json", request.getSessionBuffe().toString(), fileSystemResultsWriter),
                                    createAttachment("RequestProcessor", "application/json", request.getRequest().toString(), fileSystemResultsWriter),
                                    createAttachment("Response", "application/json", request.getResponseProcessor().getResponse(), fileSystemResultsWriter),
                                    createAttachment("Response body", "application/json", request.getResponseProcessor().getResponseBody(), fileSystemResultsWriter)
                            )
                            .withStatus(request.getSuccessful() ? Status.PASSED : Status.FAILED)
                            .withStart(request.getSession().getStartDate())

            );

            if (!request.getSuccessful()) allureTest.setStatus(Status.FAILED);
        });

        TestResultContainer testResultContainer = new TestResultContainer()
                .withName("xxx")
                .withDescriptionHtml("<b>test result containe<br>")
                .withChildren(simulation.values().stream().map(TestResult::getUuid).collect(Collectors.toList()));

        if (resultsFolder.exists()) {
            resultsFolder.delete();
        }
        fileSystemResultsWriter.write(testResultContainer);
        simulation.values().forEach(fileSystemResultsWriter::write);

    }

    public Attachment createAttachment(String attachmentName, String attachmentType, String body, FileSystemResultsWriter fileSystemResultsWriter) {

        if (body == null) body = " ";

        String attachmentUid = UUID.randomUUID().toString();

        fileSystemResultsWriter.write(attachmentUid, new ByteArrayInputStream(body.getBytes()));

        return new Attachment().withName(attachmentName)
                .withSource(attachmentUid)
                .withType(attachmentType);
    }

    public void parseLogFile() throws IOException {

        requests = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader("/home/wojciech/Repositories/alluregatling/src/main/resources/debug.log"));
        ArrayList<String> buff = new ArrayList<>();
        String line;
        Boolean isRequest = false;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals(REQUEST_START)) {
                isRequest = true;
                continue;
            } else if (line.equals(REQUEST_END)) {
                isRequest = false;
                requests.add(new RequestProcessor(buff));
                buff.clear();
                continue;
            }
            if (isRequest) {
                buff.add(line);
            }
        }

    }

    public ArrayList<RequestProcessor> getRequests() {
        return requests;
    }
}
