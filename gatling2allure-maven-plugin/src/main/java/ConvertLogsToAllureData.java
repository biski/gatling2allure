import com.biski.parser.GatlingToAllure;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Wojciech Biskowski <wbiskowski@gmail.com> Biskowski <wbiskowski@gmail.com> on 31.03.18.
 */

@Mojo(name = "convertLogsToAllureData")
public class ConvertLogsToAllureData extends AbstractMojo {

    @Parameter
    private String pathToResults = "target/";

    @Parameter(required = true)
    private String pathToLogs;

    @Parameter(defaultValue = "false")
    private boolean multipleSimulationLogs;

    private GatlingToAllure converter;

    @Override
    public void execute() {
        getLog().info("start");

        converter = new GatlingToAllure();
        converter
                .setPathToResults(pathToResults);
        if (multipleSimulationLogs) {
            try {
                Files.list(Paths.get(pathToLogs))
                        .filter(x -> x.getFileName().toString().endsWith("log"))
                        .forEach(x -> convert(x.toAbsolutePath().toString()));
            } catch (IOException e) {
                getLog().error("Directory [" + pathToLogs + "] not found.", e);
            }
        } else {
            convert(pathToLogs);
        }

        getLog().info("end");
    }

    private void convert(String pathToFile) {
        try {
            converter.convert(Paths.get(pathToFile));
        } catch (Exception e) {
            getLog().error("Cannot read logs from path: " + pathToLogs, e);
        }
    }
}
