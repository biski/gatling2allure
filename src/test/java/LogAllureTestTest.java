import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import parser.LogParser;

import java.io.IOException;

/**
 * Created by wojciech on 20.01.18.
 */
public class LogAllureTestTest {

    private LogParser logParser;

    @BeforeClass
    public void parseLog() throws IOException {
        logParser = new LogParser();
        logParser.parseLogFile();

    }

    @Test
    public void numberOfRequest() throws IOException {
        Assert.assertEquals(logParser.getRequests().size(), 23);
    }

    @Test
    public void checkRequestName() {
        Assert.assertTrue(logParser.getRequests().stream().anyMatch(x -> x.getRequestName().equals("add 0 item to product order")));

    }

    @Test
    public void checkRequestStatus() {

        Assert.assertEquals(
                logParser.getRequests().stream().filter(r -> r.getRequestName().equals("add 0 item to product order")).findFirst().get().getSuccessful(),
                Boolean.TRUE
        );
    }
}
