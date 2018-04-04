import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ImageCompareControllerTest {

    private ProcessGenerator generator = Mockito.mock(ProcessGenerator.class);

    private HashMap<String, String> filenames = new HashMap<>();

    private ImageCompareController tested = new ImageCompareController(filenames, generator);

    private Process mockProcess = Mockito.mock(Process.class);

    private final String FILENAME1 = "file1.png";
    private final String FILENAME2 = "file2.png";

    private final String RESIZED_FILE_1 = "resizedFile1.png";
    private final String RESIZED_FILE_2 = "resizedFile2.png";

    @Before
    public void setUp(){
        filenames.clear();
    }

    @Test
    public void addFileWhichDoesNotExist() throws InterruptedException {
        when(generator.generateResizeProcess(anyString(), anyString())).thenReturn(mockProcess);
        when(mockProcess.waitFor()).thenReturn(1);
        tested.addFile(FILENAME1);
        assertTrue(filenames.containsKey(FILENAME1));
        verify(generator).generateResizeProcess(eq(FILENAME1), anyString());
        verify(mockProcess).waitFor();
    }

    @Test
    public void addFileWhichExists() throws InterruptedException {
        filenames.put(FILENAME1, RESIZED_FILE_1);
        tested.addFile(FILENAME1);
        verify(generator, never()).generateResizeProcess(anyString(), anyString());
    }

    @Test
    public void testCompare() throws IOException, InterruptedException {
        filenames.put(FILENAME1, RESIZED_FILE_1);
        filenames.put(FILENAME2, RESIZED_FILE_2);
        when(generator.generateCompareProcess(anyString(), anyString())).thenReturn(mockProcess);
        when(mockProcess.waitFor()).thenReturn(1);
        when(mockProcess.getInputStream()).thenReturn(new FileInputStream(new File("src/test/resources/CompareOutput.txt")));
        double value = tested.compare(FILENAME1, FILENAME2);
        verify(generator).generateCompareProcess(RESIZED_FILE_1, RESIZED_FILE_2);
        verify(mockProcess).waitFor();
        verify(mockProcess).getInputStream();
        assertTrue(value > 0.0);
    }
}
