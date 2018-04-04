Dependencies:

- This project is dependent on a library known as ImageMagick.  To install ImageMagick for use with this project*:

On Windows: Please follow the Windows installation instructions located at https://www.imagemagick.org/script/download.php .  If there is an option to add the ImageMagick binaries to your classpath, please make sure to select that option (it should be selected by default).

On Mac: Please install Homebrew using the instructions located at https://docs.brew.sh/Installation .  Once Homebrew is installed, please install ImageMagick using the command: brew install ImageMagic —with-x11 .

* This application was developed on a Linux computer.  I asked friends who have Windows and Mac to help me test this functionality.  This guide is written based on their information, although I have not verified it first-hand.

- This project is written using Java, and will be compiled into a JAR*.  To install Java, please download and install the appropriate JRE (Java Runtime Environment) from http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html .

* If you are receiving this project via source code, please build it using Maven using mvn package to get the JAR.

- To run this project, please execute the following command in your shell:

java -jar ImageCompareTool.jar -f FULLY_QUALIFIED_CSV_FILE -d PATH_TO_IMAGE_FILES [-o OUTPUT_FILE_NAME]

FULLY_QUALIFIED_CSV_FILE must include the path to the CSV file as well as the file name.  Relative path is OK.

The -f and -d flags are required, the -o flag is optional.  If -o is not specified, the output will be written to a file called output.csv.  As alternatives, -f can also be -file, -d can also be -directory, -p, or -path, and -o can also be -output.  Flags (and their respective matching values) can be in any order.
