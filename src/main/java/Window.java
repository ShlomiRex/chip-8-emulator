import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Window extends JPanel implements PropertyChangeListener {
    // Dynamic width, height of the window in pixels. User can resize window.
    private int width = 600;
    private int height = 600;
    private final String title = "CHIP-8 Emulator - By Shlomi Domnenko";

    private Display display;
    private JFrame frame;
    private static Logger logger = LoggerFactory.getLogger(Window.class);

    public Window(Display display) {
        this.display = display;

        JFrame jframe = new JFrame(title);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(this);
        this.setBackground(Color.BLACK);
        jframe.setSize(width, height);
        jframe.setLocationRelativeTo(null);
        jframe.setVisible(true);

        jframe.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                width = getWidth();
                height = getHeight();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        logger.debug("Drawing panel");

        g.setColor(Color.WHITE);

        //g.fillRect(10, 10, 30, 30);

        int col_width = width / Display.COLS;
        int row_height = height / Display.ROWS;

        int num_on_pixels = 0;
        for (int row = 0; row < Display.ROWS; row++) {
            for (int col = 0; col < Display.COLS; col++) {
                boolean pixel = display.getPixel(row, col);
                if (!pixel)
                    continue;
                num_on_pixels++;
                int x = col * col_width;
                int y = row * row_height;
                //logger.debug("Drawing pixel: (" + row + ", " + col+"), at window x,y: ("+x+", "+y+")");
                g.fillRect(x, y, col_width, row_height);
            }
        }
        logger.debug("Number of drawn pixels: " + num_on_pixels);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
