package com.biski.parser;

import com.biski.processors.RequestProcessor;
import com.sun.org.apache.xpath.internal.SourceTree;
import io.qameta.allure.FileSystemResultsWriter;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.model.*;
import io.qameta.allure.util.ResultsUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.qameta.allure.util.ResultsUtils.getHostName;
import static io.qameta.allure.util.ResultsUtils.getThreadName;

/**
 * Created by wojciech on 25.11.17.
 */
public class GatlingToAllure {

    private static final String REQUEST_START = ">>>>>>>>>>>>>>>>>>>>>>>>>>";
    private static final String REQUEST_END = "<<<<<<<<<<<<<<<<<<<<<<<<<";
    private static final String ALLURE_RESULTS_DIR = "allure-results";
    private Deque<RequestProcessor> requests;
    private HashMap<String, TestResult> simulations = new HashMap<>();

    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.out.println("Put path to log file as argument.");
            System.exit(-1);
        }

        Path path = Paths.get(args[0]);
        if(Files.notExists(path)) {
            System.out.println("File not found!");
            System.exit(-1);
        }

        GatlingToAllure gatlingToAllure = new GatlingToAllure();
        gatlingToAllure.splitLogToRequests(path);
        gatlingToAllure.generateAllureData();
    }

    private void generateAllureData() {
        File resultsFolder = new File(ALLURE_RESULTS_DIR);

        FileSystemResultsWriter fileSystemResultsWriter = new FileSystemResultsWriter(resultsFolder.toPath());

        AtomicInteger requestCnt = new AtomicInteger(0);
        while (!requests.isEmpty()) {
            createOrUpdateAllureTest(requests.pop(), requestCnt, fileSystemResultsWriter);
        }


        fileSystemResultsWriter.write(
                new TestResultContainer()
                        .withChildren(simulations.values().stream().map(TestResult::getUuid).collect(Collectors.toList())));

        simulations.values().forEach(fileSystemResultsWriter::write);

    }

    private void createOrUpdateAllureTest(RequestProcessor request, AtomicInteger requestCnt, FileSystemResultsWriter fileSystemResultsWriter) {

        System.out.println("Processing request " + requestCnt.getAndIncrement() + "/" + requests.size());
        String simulationName = request.getSession().getScenarioName() + request.getSession().getUserId();

        TestResult allureTest = simulations.computeIfAbsent(simulationName, k -> {

            final List<Label> labels = new ArrayList<>(12);
            labels.addAll(Arrays.asList(
                    //Packages grouping
                    new Label().withName("package").withValue("example package"),
                    new Label().withName("testClass").withValue("example test class"),
                    new Label().withName("testMethod").withValue("example test method"),

                    //xUnit grouping
                    new Label().withName("parentSuite").withValue("parent suite"),
                    new Label().withName("suite").withValue("suite name"),
                    new Label().withName("subSuite").withValue("sub suite"),

                    //Timeline grouping
                    new Label().withName("host").withValue(getHostName()),
                    new Label().withName("thread").withValue(getThreadName()),
                    new Label().withName(ResultsUtils.EPIC_LABEL_NAME).withValue("Requests"),
                    new Label().withName(ResultsUtils.OWNER_LABEL_NAME).withValue("OWNER LABEL NAME"),
                    new Label().withName(ResultsUtils.TAG_LABEL_NAME).withValue("TAG LABEL NAME"),
                    new Label().withName(ResultsUtils.SEVERITY_LABEL_NAME).withValue(SeverityLevel.BLOCKER.value())
            ));

            return new TestResult()
                    .withName(simulationName)
                    .withUuid(UUID.randomUUID().toString())
                    .withStatus(request.getSuccessful() ? Status.PASSED : Status.FAILED)
                    .withStatusDetails(new StatusDetails().withMessage(request.getFailureMessage()).withTrace(""))
                    .withLabels(labels);
        });

        allureTest.getLabels().add(new Label()
                .withName(ResultsUtils.FEATURE_LABEL_NAME)
                .withValue(request.getRequestName()));

        allureTest.getSteps().add(
                new StepResult()
                        .withName(request.getRequestType() + " " + request.getRequestName())
                        .withAttachments(
                                getAttachments(fileSystemResultsWriter, request)
                        )
                        .withStatus(request.getSuccessful() ? Status.PASSED : Status.FAILED)
                        .withStatusDetails(new StatusDetails().withMessage(request.getFailureMessage()).withTrace(""))
                        .withStart(request.getSession().getStartDate())

        );

        if (!request.getSuccessful()) {
            allureTest.setStatus(Status.FAILED);
            StatusDetails statusDetails = allureTest.getStatusDetails();
            statusDetails.setMessage(request.getFailureMessage());

        }

    }

    private Attachment[] getAttachments(FileSystemResultsWriter fileSystemResultsWriter, RequestProcessor request) {
        ArrayList<Attachment> attachments = new ArrayList<>(10);
        attachments.add(createAttachment("Request", "text/plain", request.getRequestType() + " " + request.getUrl(), fileSystemResultsWriter));

        if (request.getRequestType().equals("POST")) {
            attachments.add(createAttachment("String body", "application/json", request.getStringBody(), fileSystemResultsWriter));
        }
        attachments.add(createAttachment("Headers", "text/plain", request.getHeaders().toString(), fileSystemResultsWriter));
        attachments.add(createAttachment("Session", "application/json", request.getSession().getAttributes(), fileSystemResultsWriter));
        attachments.add(createAttachment("Session buffer", "application/json", request.getSessionBuffer().toString(), fileSystemResultsWriter));
        if(request.getResponseProcessor() != null) {
            attachments.add(createAttachment("Response", "application/json", request.getResponseProcessor().getResponse(), fileSystemResultsWriter));
            attachments.add(createAttachment("Response body", "application/json", request.getResponseProcessor().getResponseBody(), fileSystemResultsWriter));
        }

        Attachment[] attachmentsArr = new Attachment[attachments.size()];
        return attachments.toArray(attachmentsArr);
    }

    public Attachment createAttachment(String attachmentName, String attachmentType, String body, FileSystemResultsWriter fileSystemResultsWriter) {

        if (body == null) body = " ";

        String attachmentUid = UUID.randomUUID().toString();

        fileSystemResultsWriter.write(attachmentUid, new ByteArrayInputStream(body.getBytes()));


        return new Attachment().withName(attachmentName)
                .withSource(attachmentUid)
                .withType(attachmentType);
    }

    public void splitLogToRequests(Path path) throws IOException {

        requests = new ArrayDeque<>(1000);

        BufferedReader bufferedReader = Files.newBufferedReader(path);
        ArrayList<String> buff = new ArrayList<>(1000);
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
}
