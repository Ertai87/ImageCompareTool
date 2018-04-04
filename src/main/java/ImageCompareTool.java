import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ImageCompareTool {

    ImageCompareController controller;

    private static final Logger LOGGER = Logger.getLogger(ImageCompareTool.class);

    public ImageCompareTool(ImageCompareController controller) {
        this.controller = controller;
    }

    public void run(Map<String, String> argumentsMap) throws IOException, InterruptedException {
        String imageFolderPath = argumentsMap.get("imageFolderPath");
        String filename = argumentsMap.get("filename");
        try(
                Scanner in = new Scanner(new FileReader(filename));
                FileWriter out = new FileWriter(
                        argumentsMap.containsKey("outputFile") ?
                            argumentsMap.get("outputFile") :
                            Constants.OUTPUT_FILENAME
                );
        ){
            out.write("image1,image2,similar,elapsed (ms)\n");
            while (in.hasNextLine()){
                String[] tokens = in.nextLine().split(",");
                try {
                    long startTime = System.currentTimeMillis();
                    String file1 = imageFolderPath + tokens[0].trim();
                    String file2 = imageFolderPath + tokens[1].trim();
                    controller.addFile(file1);
                    controller.addFile(file2);
                    double similarity = controller.compare(file1, file2);
                    long totalTime = System.currentTimeMillis() - startTime;
                    out.append(tokens[0] + "," + tokens[1] + "," + similarity + "," + totalTime + "\n");
                }catch(ArrayIndexOutOfBoundsException e){
                    String errorMessage = "Not enough arguments.  Tokens present: ";
                    for (int i = 0; i < tokens.length; i++){
                        errorMessage += tokens[i] + " ";
                    }
                    errorMessage += ".  Skipping to next line.";
                    LOGGER.error(errorMessage);
                }
            }
        }
        controller.close();
    }

    public static void main (String args[]) throws IOException, InterruptedException {
        Map<String, String> argumentsMap = parseArguments(args);
        ImageCompareTool app = new ImageCompareTool(new ImageCompareController(new HashMap<String, String>(), new ProcessGenerator()));
        app.run(argumentsMap);
    }

    public static Map<String, String> parseArguments(String args[]){
        Map<String, String> argumentsMap = new HashMap<>();
        for (int i = 0; i < args.length; i++){
            try {
                switch (args[i]) {
                    case "-f":
                    case "-file":
                        i++;
                        if (!argumentsMap.containsKey("filename")) {
                            argumentsMap.put("filename", args[i]);
                        } else {
                            throw new RuntimeException("Filename can only be present once.");
                        }
                        break;
                    case "-d":
                    case "-p":
                    case "-directory":
                    case "-path":
                        i++;
                        if (!argumentsMap.containsKey("imageFolderPath")) {
                            if (!args[i].endsWith("/")){
                                args[i] += "/";
                            }
                            argumentsMap.put("imageFolderPath", args[i]);
                        } else {
                            throw new RuntimeException("Path to image folder can only be present once.");
                        }
                        break;
                    case "-o":
                    case "-output":
                        i++;
                        if (!argumentsMap.containsKey("outputFile")){
                            argumentsMap.put("outputFile", args[i]);
                        } else {
                            throw new RuntimeException("Output file can only be present once.");
                        }
                        break;
                }
            }catch(ArrayIndexOutOfBoundsException e){
                throw new RuntimeException("Argument flag present without argument.", e);
            }
        }
        if (!argumentsMap.containsKey("filename") || !argumentsMap.containsKey("imageFolderPath")){
            throw new RuntimeException("Filename and path to image folder must both be present.  Please refer to README for proper invocation instructions.");
        }
        return argumentsMap;
    }
}
