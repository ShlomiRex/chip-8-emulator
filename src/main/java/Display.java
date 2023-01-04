import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Display uses 64x32 monochrome panel. Each pixel is 1 or 0.
 */
public class Display {
    public static final int COLS = 64;
    public static final int ROWS = 32;

    // Rows: 32, Cols: 64
    private final boolean[][] pixels = new boolean[32][64];

    private static final Logger logger = LoggerFactory.getLogger(Display.class);

    // Clear display
    public void cls() {
        for (int row = 0; row < 32; row ++)
            for (int col = 0; col < 64; col ++)
                pixels[row][col] = false;
    }

    public boolean getPixel(int row, int col) {
        return pixels[row][col];
    }

    public void setPixel(int row, int col, boolean value) {
        pixels[row][col] = value;
        logger.debug("Pixel set: ("+row+", "+col+") = " + value);
    }
}
