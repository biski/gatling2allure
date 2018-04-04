import com.biski.parser.GatlingToAllure;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Wojciech Biskowski <wbiskowski@gmail.com> on 31.03.18.
 */

@Mojo(name = "convertLogsToAllureData")
public class ConvertLogsToAllureData extends AbstractMojo {


    @Parameter(required = true)
    private String pathToLogs;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("start");

        GatlingToAllure converter = new GatlingToAllure();
        try {
            converter.convert(Paths.get(pathToLogs));
        } catch (IOException e) {
            getLog().error("Cannot read logs from path: " + pathToLogs);
        }

        getLog().info("end");

    }
}
