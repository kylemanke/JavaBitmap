import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Display {
    public static void DisplayGreyscale(int[][] bmp) throws InterruptedException {
        // Create the frame
        JFrame frame = new JFrame("bitmap");

        // Set frame close callback
        Callbacks callback = new Callbacks();
        frame.addWindowListener(callback);

        // set close operation
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // get size and width
        int width = bmp[0].length;
        int height = bmp.length;

        // Create a buffered image
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Color in the img
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int rgb = 0xFF << 24 | ((bmp[i][j] & 0xFF) << 16) | ((bmp[i][j] & 0xFF) << 8) | (bmp[i][j] & 0xFF);
                img.setRGB(j, i, rgb);
            }
        }

        // Add to jframe
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));

        // Show the frame
        frame.pack();
        frame.setVisible(true);

        // Wait for frame to be closed
        while(!callback.isClosed()) {
            callback.w();
        }
    }

    public static void DisplayRGB(int[][][] bmp) throws InterruptedException {
        // Create the frame
        JFrame frame = new JFrame("bitmap");

        // Set frame close callback
        Callbacks callback = new Callbacks();
        frame.addWindowListener(callback);

        // set close operation
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // get size and width
        int width = bmp[0].length;
        int height = bmp.length;

        // Create a buffered image
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Color in the img
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int rgb = 0xFF << 24 | ((bmp[i][j][0] & 0xFF) << 16) | ((bmp[i][j][1] & 0xFF) << 8) | (bmp[i][j][2] & 0xFF);
                img.setRGB(j, i, rgb);
            }
        }

        // Add to jframe
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));

        // Show the frame
        frame.pack();
        frame.setVisible(true);

        // Wait for frame to be closed
        while(!callback.isClosed()) {
            callback.w();
        }
    }
}

class Callbacks extends WindowAdapter {
    private boolean closed = false;

    public synchronized boolean isClosed() {
        return closed;
    }

    public synchronized void w() throws InterruptedException{
        wait();
    }

    public synchronized void setClosed() {
        closed = true;
        notifyAll();
    }

    public void windowClosing(WindowEvent e) {
        setClosed();
    }
}


