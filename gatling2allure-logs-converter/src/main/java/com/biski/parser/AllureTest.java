package com.biski.parser;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Wojciech Biskowski <wbiskowski@gmail.com> on 25.11.17.
 */
public class AllureTest {

    private final AllureLifecycle lifecycle;
    private final String testResultUuid;
    private String testResultContainerUuid;
    private String testName;

    public AllureTest(String testName) {
        this.lifecycle = Allure.getLifecycle();
        testResultContainerUuid = UUID.randomUUID().toString();
        testResultUuid = UUID.randomUUID().toString();
        this.testName = testName;
    }

    public static void main(String[] args) {


        AllureTest allureTest = new AllureTest("x");
        allureTest.onStart();
//        allureTest.addSteps("step 1");
//        allureTest.addSteps(" step 2");

        allureTest.onSuccess();


    }

    public void onStart() {
        final TestResultContainer result = new TestResultContainer()
                .withUuid(testResultContainerUuid)
                .withName(testName)
                .withStart(System.currentTimeMillis());
        getLifecycle().startTestContainer(result);

        final TestResult testResult = new TestResult()
                .withUuid(testResultContainerUuid)
                .withName(testName)
                .withFullName(testName)
                .withStatusDetails(new StatusDetails()
                        .withFlaky(false)
                        .withMuted(false));

        getLifecycle().scheduleTestCase(testResultContainerUuid, testResult);
        getLifecycle().startTestCase(testResultContainerUuid);
    }

    public AllureLifecycle getLifecycle() {
        return lifecycle;
    }

    public void onSuccess() {

        getLifecycle().updateTestCase(testResultContainerUuid, setStatus(Status.PASSED));
        getLifecycle().stopTestCase(testResultContainerUuid);
        getLifecycle().writeTestCase(testResultContainerUuid);
    }

    private Consumer<TestResult> setStatus(final Status status) {
        return result -> result.withStatus(status);
    }

    private Consumer<TestResult> setStatus(final Status status, final StatusDetails details) {
        return result -> {
            result.setStatus(status);
            if (Objects.nonNull(details)) {
                result.getStatusDetails().setTrace(details.getTrace());
                result.getStatusDetails().setMessage(details.getMessage());
            }
        };
    }

    public void addSteps(String stepName, boolean isSuccess) {
        String stepUuid = UUID.randomUUID().toString();

        final StepResult result = new StepResult()
                .withName(stepName)
                .withDescriptionHtml("<b>" + stepName + "</b>");
        getLifecycle().startStep(stepUuid, result);
        getLifecycle().updateStep(stepUuid, s -> {
            if (isSuccess) s.withStatus(Status.PASSED);
            else s.withStatus(Status.FAILED);
        });
        getLifecycle().updateStep(stepUuid, s -> s.setStart(123154l));
    }

    public void addAttachment(String name, String body) {
        getLifecycle().addAttachment(name, "application/json", "json", body.getBytes(StandardCharsets.UTF_8));
    }

    public void addAttachmentCsv(String name, String body) {
        getLifecycle().addAttachment(name, "text/csv", "csv", body.getBytes(StandardCharsets.UTF_8));
    }

    public void stopStep() {
        getLifecycle().stopStep();
    }

    public String getTestName() {
        return testName;
    }
}
