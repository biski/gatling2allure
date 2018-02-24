package com.biski.parser;

import io.qameta.allure.*;
import io.qameta.allure.model.*;
import io.qameta.allure.model.Attachment;
import io.qameta.allure.util.ResultsUtils;
import com.biski.processors.RequestProcessor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.qameta.allure.util.ResultsUtils.getHostName;
import static io.qameta.allure.util.ResultsUtils.getThreadName;

/**
 * Created by wojciech on 25.11.17.
 */
public class GatlingToAllure {

    private List<RequestProcessor> requests;
    private static final String REQUEST_START = ">>>>>>>>>>>>>>>>>>>>>>>>>>";
    private static final String REQUEST_END = "<<<<<<<<<<<<<<<<<<<<<<<<<";

    private static final String ALLURE_RESULTS_DIR = "allure-results";
    private HashMap<String, TestResult> simulations = new HashMap<>();

    public static void main(String[] args) throws Exception {
        GatlingToAllure gatlingToAllure = new GatlingToAllure();
        gatlingToAllure.splitLogToRequests();
//        gatlingToAllure.debug();
        gatlingToAllure.generateAllureData();
    }

//    private void debug() {
//        requests.forEach(
//                request -> {
//                    System.out.println(request.getSuccessful() + " " + request.getRequestName() + " " + request.getSession().getScenarioName() + " " + request.getUrl())
//                }
//        );
//    }

    private void generateAllureData() {
        File resultsFolder = new File(ALLURE_RESULTS_DIR);
        if (resultsFolder.exists()) {
            resultsFolder.delete();
        }

        FileSystemResultsWriter fileSystemResultsWriter = new FileSystemResultsWriter(resultsFolder.toPath());

        AtomicInteger requestCnt = new AtomicInteger(0);
        requests.forEach(createOrUpdateAllureTest(requestCnt, fileSystemResultsWriter));


        fileSystemResultsWriter.write(
                new TestResultContainer()
                        .withChildren(simulations.values().stream().map(TestResult::getUuid).collect(Collectors.toList())));

        simulations.values().forEach(fileSystemResultsWriter::write);

    }

    private Consumer<RequestProcessor> createOrUpdateAllureTest(AtomicInteger requestCnt, FileSystemResultsWriter fileSystemResultsWriter) {
        return (RequestProcessor request) -> {

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
                        .withLabels(labels);
            });
//                    .withStart(request.getSession().getStartDate())
//                    .withStop(request.getSession().getStartDate()+50));


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
                            .withStart(request.getSession().getStartDate())

            );

            if (!request.getSuccessful()) allureTest.setStatus(Status.FAILED);
        };
    }

    private Attachment[] getAttachments(FileSystemResultsWriter fileSystemResultsWriter, RequestProcessor request) {
        ArrayList<Attachment> attachments = new ArrayList<>(10);
        attachments.add(createAttachment("Request", "text/plain", request.getRequestType() + " " + request.getUrl(), fileSystemResultsWriter));

        if (request.getRequestType().equals("POST")) {
            attachments.add(createAttachment("String body", "application/json", request.getStringBody(), fileSystemResultsWriter));
        }
        attachments.add(createAttachment("Session", "application/json", request.getSession().getAttributes(), fileSystemResultsWriter));
        attachments.add(createAttachment("Session buffer", "application/json", request.getSessionBuffe().toString(), fileSystemResultsWriter));
        attachments.add(createAttachment("Response", "application/json", request.getResponseProcessor().getResponse(), fileSystemResultsWriter));
        attachments.add(createAttachment("Response body", "application/json", request.getResponseProcessor().getResponseBody(), fileSystemResultsWriter));

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

    public void splitLogToRequests() throws IOException {

        requests = new ArrayList<>(1000);

        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get("/home/wojciech/log-20180220T135351.txt"));
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

    public List<RequestProcessor> getRequests() {
        return requests;
    }
}
