import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ImageCompareToolTest {

    private ImageCompareController controller = Mockito.mock(ImageCompareController.class);

    private ImageCompareTool tested = new ImageCompareTool(controller);

    private final String FILENAME1 = "src/test/resources/compareimgs.csv";
    private final String FILENAME2 = "src/test/resources/compareimgs2.csv";
    private final String FILENAME3 = "src/test/resources/compareimgs3.csv";

    private final String IMAGE_FOLDER_PATH = "/";

    private final String OUTPUT_FILENAME = "testOutput.csv";

    @Test
    public void parseArgumentsWithBothArgumentsAvailable(){
        String[] args = new String[]{"-f", FILENAME1, "-p", IMAGE_FOLDER_PATH};
        Map<String, String> argsMap = ImageCompareTool.parseArguments(args);
        assertTrue(argsMap.containsKey("filename"));
        assertTrue(argsMap.containsKey("imageFolderPath"));
        assertEquals(FILENAME1, argsMap.get("filename"));
        assertEquals(IMAGE_FOLDER_PATH, argsMap.get("imageFolderPath"));
    }

    @Test(expected = RuntimeException.class)
    public void parseArgumentsWithOnlyOneArgumentAvailable(){
        String[] args = new String[]{"-f", FILENAME1};
        ImageCompareTool.parseArguments(args);
    }

    @Test(expected = RuntimeException.class)
    public void parseArgumentsWithIncompleteParamsList(){
        String[] args = new String[]{"-f", FILENAME1, "-p"};
        try{
            ImageCompareTool.parseArguments(args);
        }catch(RuntimeException e){
            assertEquals(ArrayIndexOutOfBoundsException.class, e.getCause().getClass());
            throw e;
        }
    }

    @Test
    public void testRunWithOneLineOfInput() throws IOException, InterruptedException {
        Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("filename", FILENAME1);
        argumentsMap.put("imageFolderPath", IMAGE_FOLDER_PATH);
        when(controller.compare(anyString(), anyString())).thenReturn(0.0);
        tested.run(argumentsMap);
        verify(controller, times(2)).addFile(anyString());
        verify(controller).compare(anyString(), anyString());
    }

    @Test
    public void testRunWithMoreThanOneLineOfInput() throws IOException, InterruptedException {
        Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("filename", FILENAME2);
        argumentsMap.put("imageFolderPath", IMAGE_FOLDER_PATH);
        when(controller.compare(anyString(), anyString())).thenReturn(0.0);
        tested.run(argumentsMap);
        verify(controller, times(4)).addFile(anyString());
        verify(controller, times(2)).compare(anyString(), anyString());
    }

    @Test
    public void testRunWithMalformedCSV() throws IOException, InterruptedException {
        Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("filename", FILENAME3);
        argumentsMap.put("imageFolderPath", IMAGE_FOLDER_PATH);
        when(controller.compare(anyString(), anyString())).thenReturn(0.0);
        tested.run(argumentsMap);
        verify(controller, never()).addFile(anyString());
        verify(controller, never()).compare(anyString(), anyString());
    }

    @Test
    public void testRunWithUserDefinedOutputFile() throws IOException, InterruptedException {
        Map<String, String> argumentsMap = new HashMap<>();
        argumentsMap.put("filename", FILENAME2);
        argumentsMap.put("imageFolderPath", IMAGE_FOLDER_PATH);
        argumentsMap.put("outputFile", OUTPUT_FILENAME);
        when(controller.compare(anyString(), anyString())).thenReturn(0.0);
        tested.run(argumentsMap);
        File file = new File(OUTPUT_FILENAME);
        assertTrue(file.exists());
    }
}
