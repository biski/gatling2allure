package com.biski.parser;

import io.qameta.allure.*;
import io.qameta.allure.model.*;
import io.qameta.allure.util.ResultsUtils;

import java.io.File;
import java.util.*;

import static io.qameta.allure.util.ResultsUtils.getHostName;
import static io.qameta.allure.util.ResultsUtils.getThreadName;

/**
 * Created by wojciech on 04.02.18.
 */
public class dsfvsdaf {

    public static void main(String[] args) {


        StepResult stepResult = new StepResult()
                .withName("step name")
                .withDescription("step description")
                .withStatus(Status.PASSED);


        TestResult testResult = new TestResult()
                .withUuid("TestResultUUID")
                .withStage(Stage.FINISHED)
                .withStatusDetails(new StatusDetails().withMessage("WSZYSTKO OK"))
                .withName("test name")
                .withFullName("fsdafsdafsd")
                .withStatus(Status.PASSED)
                .withDescription("example description")
                .withParameters(new Parameter().withName("parameter name").withValue("parameter value"))
                .withLabels(new Label().withName("label name").withValue("label value"))
                .withHistoryId("history id")
                .withSteps(stepResult);

//        public static final String EPIC_LABEL_NAME = "epic";
//        public static final String FEATURE_LABEL_NAME = "feature";
//        public static final String STORY_LABEL_NAME = "story";
//        public static final String SEVERITY_LABEL_NAME = "severity";
//        public static final String TAG_LABEL_NAME = "tag";
//        public static final String OWNER_LABEL_NAME = "owner";
//        public static final String HOST_LABEL_NAME = "host";
//        public static final String THREAD_LABEL_NAME = "thread";

        final List<Label> labels = new ArrayList<>(15);
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
                new Label().withName(ResultsUtils.EPIC_LABEL_NAME).withValue("EPIC LABEL NAME"),
                new Label().withName(ResultsUtils.OWNER_LABEL_NAME).withValue("OWNER LABEL NAME"),
                new Label().withName(ResultsUtils.TAG_LABEL_NAME).withValue("TAG LABEL NAME"),
                new Label().withName(ResultsUtils.STORY_LABEL_NAME).withValue("STORY LABEL NAME"),
                new Label().withName(ResultsUtils.FEATURE_LABEL_NAME).withValue("FEATURE LABEL NAME"),
                new Label().withName(ResultsUtils.SEVERITY_LABEL_NAME).withValue(SeverityLevel.BLOCKER.value()),
                new Label().withName(ResultsUtils.OWNER_LABEL_NAME).withValue("wojtek")
        ));
        testResult.setLabels(labels);



        TestResultContainer testResultContainer = new TestResultContainer()
                .withName("xxx")

                .withDescriptionHtml("<b>test result containe<br>")
                .withChildren(testResult.getUuid());

        File resultsFolder = new File("allure-results");
        if(resultsFolder.exists()) { resultsFolder.delete(); }
        FileSystemResultsWriter fileSystemResultsWriter = new FileSystemResultsWriter(resultsFolder.toPath());
        fileSystemResultsWriter.write(testResultContainer);
        fileSystemResultsWriter.write(testResult);
    }
}
