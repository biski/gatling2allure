package com.biski.parser;

import com.biski.processors.RequestProcessor;
import io.qameta.allure.FileSystemResultsWriter;
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
import java.util.function.Function;

import static io.qameta.allure.util.ResultsUtils.getHostName;
import static io.qameta.allure.util.ResultsUtils.getThreadName;

/**
 * Created by Wojciech Biskowski <wbiskowski@gmail.com> on 25.11.17.
 */
public class GatlingToAllure {

    private static final String RESULTS_POSTFIX = "-result.json";
    private static final String REQUEST_START = ">>>>>>>>>>>>>>>>>>>>>>>>>>";
    private static final String REQUEST_END = "<<<<<<<<<<<<<<<<<<<<<<<<<";
    private static final String ALLURE_RESULTS_DIR = "allure-results";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String APPLICATION_JSON = "application/json";

    private static final Boolean ADD_PARENT_STEP_IF_REQUEST_IS_UNDER_GROUP = true;

    private HashMap<String, String> createdTestResults = new HashMap<>();

    private String pathToResults;
    public GatlingToAllure setPathToResults(String path) {
        pathToResults = path;
        return this;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Put path to log file as argument.");
            System.exit(-1);
        }

        Path path = Paths.get(args[0]);
        if (Files.notExists(path)) {
            System.out.println("File not found!");
            System.exit(-1);
        }

        GatlingToAllure gatlingToAllure = new GatlingToAllure();
        gatlingToAllure.convert(path);
    }

    public void convert(Path path) throws IOException {

        BufferedReader bufferedReader = Files.newBufferedReader(path);

        ArrayList<String> buff = new ArrayList<>(500);
        String line;
        Boolean isRequest = false;
        int idOfRequest = 0;
        while ((line = bufferedReader.readLine()) != null) {
            switch (line) {
                case REQUEST_START:
                    isRequest = true;
                    break;
                case REQUEST_END:
                    isRequest = false;
                    createOrUpdateAllureTest(new RequestProcessor(buff), idOfRequest++);
                    buff.clear();
                    break;
                default:
                    if (isRequest) buff.add(line);
            }

        }
    }

    private void createOrUpdateAllureTest(RequestProcessor request, int idOfRequest) {

        System.out.println("Processing request " + idOfRequest);
        String simulationName = request.getSession().getScenarioName() + " [" + request.getSession().getUserId() + "]";
        FileSystemResultsWriter writer = new FileSystemResultsWriter(new File(pathToResults + ALLURE_RESULTS_DIR).toPath());

        String uuid = createdTestResults.computeIfAbsent(simulationName, createNewTestResult(request, simulationName, writer));
        updateTestResult(request, writer, uuid);
    }

    private void updateTestResult(RequestProcessor request, FileSystemResultsWriter writer, String uuid) {
        try {
            Path path = new File(pathToResults + ALLURE_RESULTS_DIR + "/" + uuid + RESULTS_POSTFIX).toPath();
            TestResult testResult = readTestResultFromFile(path);

            testResult.getLabels().add(new Label()
                    .withName(ResultsUtils.FEATURE_LABEL_NAME)
                    .withValue(request.getRequestName()));

            updateStep(request, writer, testResult);

            if (!request.getSuccessful()) {
                testResult.setStatus(Status.FAILED);
                StatusDetails statusDetails = testResult.getStatusDetails();
                statusDetails.setMessage(request.getFailureMessage());

            }
            updateTestResultFile(writer, path, testResult);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateStep(RequestProcessor request, FileSystemResultsWriter writer, TestResult testResult) {
        List<StepResult> parentSteps;
        if(ADD_PARENT_STEP_IF_REQUEST_IS_UNDER_GROUP && request.getGroup().isPresent()) {
            System.out.println("ADD_PARENT_STEP_IF_REQUEST_IS_UNDER_GROUP");

            String gatlingGroupName = request.getGroup().get();
//            System.out.println("Group name: " + gatlingGroupName);
            StepResult groupStep = testResult.getSteps().stream()
                    .filter(x -> gatlingGroupName.equals(x.getName()))
                    .findFirst()
                    .orElseGet(() -> {
                        StepResult newGroupStep = new StepResult()
                                .withName(gatlingGroupName)
                                .withDescription("xxxxxxxxxx");
                        testResult.getSteps().add(newGroupStep);
                        return newGroupStep;
                    });
            parentSteps = groupStep.getSteps();
        } else {
            parentSteps = testResult.getSteps();
        }

        addNewStepToTestCase(request, writer, parentSteps);
    }

    private void addNewStepToTestCase(RequestProcessor request, FileSystemResultsWriter writer, List<StepResult> parentSteps) {
        parentSteps.add(
                new StepResult()
                        .withName(request.getRequestType() + " " + request.getRequestName())
                        .withAttachments(getAttachments(writer, request))
                        .withStatus(request.getSuccessful() ? Status.PASSED : Status.FAILED)
                        .withStatusDetails(new StatusDetails().withMessage(request.getFailureMessage()).withTrace(""))
                        .withStart(request.getSession().getStartDate())
                        .withStop(request.getSession().getStartDate())
        );
    }

    private void updateTestResultFile(FileSystemResultsWriter writer, Path path, TestResult testResult) throws IOException {
        Files.delete(path);
        writer.write(testResult);
    }

    private TestResult readTestResultFromFile(Path path) throws IOException {
        return Allure2ModelJackson
                .createMapper()
                .readValue(
                        Files.newInputStream(path),
                        TestResult.class);
    }

    private Attachment[] getAttachments(FileSystemResultsWriter fileSystemResultsWriter, RequestProcessor request) {
        ArrayList<Attachment> attachments = new ArrayList<>(10);
        attachments.add(createAttachment("Request", TEXT_PLAIN, request.getRequestType() + " " + request.getUrl(), fileSystemResultsWriter));

        if (request.getRequestType().equals("POST")) {
            attachments.add(createAttachment("String body", APPLICATION_JSON, request.getStringBody(), fileSystemResultsWriter));
        }
        attachments.add(createAttachment("Headers", TEXT_PLAIN, request.getHeaders().toString(), fileSystemResultsWriter));
        attachments.add(createAttachment("Session", APPLICATION_JSON, request.getSession().getAttributes(), fileSystemResultsWriter));
        attachments.add(createAttachment("Session buffer", APPLICATION_JSON, request.getSessionBuffer().toString(), fileSystemResultsWriter));
        if (request.getResponseProcessor() != null) {
            attachments.add(createAttachment("Response", APPLICATION_JSON, request.getResponseProcessor().getResponse(), fileSystemResultsWriter));
            attachments.add(createAttachment("Response body", APPLICATION_JSON, request.getResponseProcessor().getResponseBody(), fileSystemResultsWriter));
        }

        Attachment[] attachmentsArr = new Attachment[attachments.size()];
        return attachments.toArray(attachmentsArr);
    }

    private Attachment createAttachment(String attachmentName, String attachmentType, String body, FileSystemResultsWriter fileSystemResultsWriter) {

        String attachmentBody = Optional.ofNullable(body).orElse(" ");

        String attachmentUid = UUID.randomUUID().toString();

        fileSystemResultsWriter.write(attachmentUid, new ByteArrayInputStream(attachmentBody.getBytes()));


        return new Attachment().withName(attachmentName)
                .withSource(attachmentUid)
                .withType(attachmentType);
    }

    private Function<String, String> createNewTestResult(RequestProcessor request, String simulationName, FileSystemResultsWriter writer) {
        return k -> {

            final List<Label> labels = new ArrayList<>(12);
            labels.addAll(Arrays.asList(
                    //Packages grouping
                    new Label().withName("package").withValue(simulationName.split("\\[")[0]),
//                    new Label().withName("testClass").withValue("example test class"),
//                    new Label().withName("testMethod").withValue("example test method"),

                    //xUnit grouping
                    new Label().withName("parentSuite").withValue("Gatling tests"),
                    new Label().withName("suite").withValue(simulationName.split("\\[")[0]),
//                    new Label().withName("subSuite").withValue("default"),

                    //Timeline grouping
                    new Label().withName("host").withValue(getHostName()),
                    new Label().withName("thread").withValue(getThreadName())
//                    new Label().withName(ResultsUtils.EPIC_LABEL_NAME).withValue("Requests"),
//                    new Label().withName(ResultsUtils.OWNER_LABEL_NAME).withValue("OWNER LABEL NAME"),
//                    new Label().withName(ResultsUtils.TAG_LABEL_NAME).withValue("TAG LABEL NAME"),
//                    new Label().withName(ResultsUtils.SEVERITY_LABEL_NAME).withValue(SeverityLevel.BLOCKER.value())
            ));

            String newUuid = UUID.randomUUID().toString();
            TestResult testResult = new TestResult()
                    .withName(simulationName)
                    .withUuid(newUuid)
                    .withStatus(request.getSuccessful() ? Status.PASSED : Status.FAILED)
                    .withStatusDetails(new StatusDetails().withMessage(request.getFailureMessage()).withTrace(""))
                    .withLabels(labels);

            writer.write(testResult);
            return newUuid;
        };
    }
}
