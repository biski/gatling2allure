package com.wojtek;

import com.biski.parser.GatlingToAllure;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Wojciech Biskowski <wbiskowski@gmail.com> on 04.04.18.
 */
public class GenerateTestDataForPluginTest {

    @Test
    public void generateTestData() throws IOException {
        new GatlingToAllure().convert(Paths.get(getClass().getResource("/simpleFailedRequest.txt").getPath()));
    }
}
