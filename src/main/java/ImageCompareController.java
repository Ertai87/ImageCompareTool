import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ImageCompareController {

    private Map<String, String> resizedFilenames;
    private ProcessGenerator processGenerator;

    private final static Logger LOGGER = Logger.getLogger(ImageCompareController.class);

    public ImageCompareController(Map<String, String> resizedFilenames, ProcessGenerator processGenerator){
        this.resizedFilenames = resizedFilenames;
        this.processGenerator = processGenerator;
    }

    /*
    Resizing of the files is required for ImageMagick compare; if the files are different
    sizes, then compare will fail completely.

    In the case that the same file is being used for multiple comparisons,
    we use a design pattern which memoizes the resized file to avoid replication
    of work.  The memoized files are deleted when the controller is closed.
    */
    public void addFile(String filename) throws InterruptedException {
        if (resizedFilenames.containsKey(filename)) return;

        String resizedFilename = filename.substring(0, filename.lastIndexOf("."))
                + "-resized-" + UUID.randomUUID()
                + filename.substring(filename.lastIndexOf("."), filename.length());

        Process resizeProcess = processGenerator.generateResizeProcess(filename, resizedFilename);

        resizeProcess.waitFor();

        resizedFilenames.put(filename, resizedFilename);
    }

    public double compare(String file1, String file2) throws InterruptedException {

        Process compareProcess = processGenerator.generateCompareProcess(resizedFilenames.get(file1), resizedFilenames.get(file2));
        Scanner compareOutput = new Scanner(compareProcess.getInputStream());
        compareProcess.waitFor();

        /*
        Image difference is calculated as follows:
        - For each RGB channel in each pixel, that channel either matches (0) or doesn't match (1).
        - The appropriate score is added to the pixel count, for each pixel.
        - The score is then normalized to the number of pixels by dividing by the number of channels (3)
        - The score is then taken as a percentage of the number of pixels in the image by dividing by the
          aspect ratio of the image (640x480)
        - The resulting value between 0 and 1 is the difference score.
         */
        double pixels = 0;
        while (compareOutput.hasNextLine()){
            String[] tokens = compareOutput.nextLine().trim().split("\\s");
            if (tokens[0].startsWith("red") || tokens[0].startsWith("green") || tokens[0].startsWith("blue")){
                pixels += Double.parseDouble(tokens[1]);
            }
        }
        pixels /= 3;
        pixels /= (Constants.ASPECT_RATIO[0] * Constants.ASPECT_RATIO[1]);

        File difference = new File("difference.png");
        difference.delete();

        return pixels;
    }

    public void close() {
        LOGGER.info("Cleaning up environment...");
        for (String filename : resizedFilenames.values()) {
            LOGGER.info("Deleting file " + filename);
            File file = new File(filename);
            file.delete();
        }
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
