public class Constants {
    public static final int[] ASPECT_RATIO = new int[]{640, 480};
    public static final String RESIZE_COMMAND_FORMAT_STRING =
            "magick convert %s -resize " + ASPECT_RATIO[0] + "x" + ASPECT_RATIO[1] + "! %s";
    public static final String COMPARE_COMMAND_FORMAT_STRING =
            "magick compare -verbose -metric ae %s %s difference.png";
    public static final String OUTPUT_FILENAME = "output.csv";
}
