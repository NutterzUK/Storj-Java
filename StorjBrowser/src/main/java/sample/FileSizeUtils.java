package sample;

/**
 * Created by steve on 19/08/2016.
 */
public class FileSizeUtils {

    /**
     * Turns a number of bytes into a human readable count.
     * Thanks to aioobe on stackoverflow: http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
     * @param bytes the byte count.
     * @param si If si should be used.
     * @return the human readable format.
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
