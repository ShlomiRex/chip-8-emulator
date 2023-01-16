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

//        Thread thread = new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(1);
//                    repaint();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        thread.start();

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
        drawGridLines(g); // TODO: Remove

        g.setColor(Color.WHITE);

        int col_width = width / Display.COLS;
        int row_height = height / Display.ROWS;

        for (int row = 0; row < Display.ROWS; row++) {
            for (int col = 0; col < Display.COLS; col++) {
                boolean pixel = display.getPixel(row, col);
                if (!pixel)
                    continue;
                int x = col * col_width;
                int y = row * row_height;
                g.fillRect(x, y, col_width, row_height);
            }
        }
    }

    private void drawGridLines(Graphics g) {
        g.setColor(Color.RED);
        int col_width = width / Display.COLS;
        int row_height = height / Display.ROWS;

        for (int row = 0; row < Display.ROWS; row++) {
            g.drawLine(0, row * row_height, Display.COLS * col_width, row * row_height);
            for (int col = 0; col < Display.COLS; col++) {
                g.drawLine(col * col_width, 0, col * col_width, Display.ROWS * row_height);
            }
        }
        // Draw last lines (to close the grid)
        g.drawLine(0, Display.ROWS * row_height, Display.COLS * col_width, Display.ROWS * row_height);
        g.drawLine(Display.COLS * col_width, 0, Display.COLS * col_width, Display.ROWS * row_height);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
