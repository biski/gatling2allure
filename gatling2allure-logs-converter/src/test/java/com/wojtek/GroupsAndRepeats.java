package com.wojtek;

import com.biski.parser.GatlingToAllure;
import io.qameta.allure.model.Allure2ModelJackson;
import io.qameta.allure.model.TestResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Wojciech Biskowski <wbiskowski@gmail.com> Biskowski <wbiskowski@gmail.com> on 30.04.18.
 */
public class GroupsAndRepeats {


    @Test
    public void groupTest() throws IOException {
        String path1 = "target/groupsAndRepeats/";
        deleteDirectory(new File(path1));
        new GatlingToAllure()
                .setPathToResults(path1)
                .convert(Paths.get(getClass().getResource("/groupsAndRepeats.log").getPath()));

        Path path = Paths.get("target/groupsAndRepeats/allure-results");
        Path pathToResult = Files.list(path)
                .filter(f -> f.toAbsolutePath().toString().endsWith("-result.json"))
                .findFirst()
                .orElseThrow(() -> new Error("File -result.json not found"));

        TestResult testResult = Allure2ModelJackson
                .createMapper()
                .readValue(
                        Files.newInputStream(pathToResult),
                        TestResult.class);

        Assert.assertEquals(testResult.getSteps().size(), 5);
        Assert.assertEquals(testResult.getSteps().stream().filter(x -> "group name".equals(x.getName())).count(), 1);
        Assert.assertEquals(testResult.getSteps().stream().filter(x -> "group name".equals(x.getName())).findFirst().get().getSteps().size(), 10);

    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}
