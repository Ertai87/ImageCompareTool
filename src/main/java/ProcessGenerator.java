import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ProcessGenerator {

    private static Logger LOGGER = Logger.getLogger(ProcessGenerator.class);

    public Process generateResizeProcess(String filename, String resizedFilename) {
        String command = String.format(Constants.RESIZE_COMMAND_FORMAT_STRING, filename, resizedFilename);

        List<String> params = new LinkedList<>(Arrays.asList(
                command.split("\\s")
        ));

        LOGGER.info("Executing command: " + command);

        try {
            return new ProcessBuilder(params)
                    .inheritIO().start();
        }catch (IOException e){
            throw new RuntimeException ("Error occurred when running ImageMagick.  Please verify ImageMagick is installed correctly.", e);
        }
    }

    public Process generateCompareProcess(String file1, String file2) {
        String command = String.format(Constants.COMPARE_COMMAND_FORMAT_STRING, file1, file2);

        List<String> params = new LinkedList<>(Arrays.asList(
                command.split("\\s")
        ));

        LOGGER.info("Executing command: " + command);

        ProcessBuilder compareProcessBuilder = new ProcessBuilder(params);
        compareProcessBuilder.redirectErrorStream(true);

        try {
            return compareProcessBuilder.start();
        } catch (IOException e){
            throw new RuntimeException ("Error occurred when running ImageMagick.  Please verify ImageMagick is installed correctly.", e);

        }
    }
}
