/**
 * Display uses 64x32 monochrome panel. Each pixel is 1 or 0.
 */
public class Display {
    public static final int WIDTH = 64;
    public static final int HEIGHT = 32;

    // Rows: 32, Cols: 64
    private final boolean[][] pixels = new boolean[32][64];

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
    }
}
