import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple time-series chart used by AnalyticsPanel.
 * Plots Avg Congestion (line A) and Active Vehicles (line B).
 */
public class TrendChartPanel extends JPanel {

    private final List<Double> congestionData = new ArrayList<>();
    private final List<Integer> vehiclesData = new ArrayList<>();

    public TrendChartPanel() {
        setPreferredSize(new Dimension(240, 200));
        setBackground(Color.WHITE);
    }

    public void addPoint(double congestion, int vehicles) {
        congestionData.add(congestion);
        vehiclesData.add(vehicles);
        if (congestionData.size() > 300) {  // keep memory small
            congestionData.remove(0);
            vehiclesData.remove(0);
        }
        repaint();
    }

    public void clear() {
        congestionData.clear();
        vehiclesData.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);

        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Axes
        g.setColor(Color.GRAY);
        g.drawLine(30, h - 30, w - 10, h - 30);
        g.drawLine(30, 10, 30, h - 30);

        if (congestionData.isEmpty()) return;

        // Scaling
        int n = congestionData.size();
        double maxC = 1.0; // congestion is 0..1
        int maxV = vehiclesData.stream().max(Integer::compareTo).orElse(1);

        double xstep = (double)(w - 40) / Math.max(1, n - 1);

        // Congestion = RED LINE
        g.setColor(new Color(220, 60, 60));
        for (int i = 0; i < n - 1; i++) {
            int x1 = (int)(30 + i * xstep);
            int x2 = (int)(30 + (i+1)*xstep);
            int y1 = (int)(h - 30 - congestionData.get(i) * (h - 50));
            int y2 = (int)(h - 30 - congestionData.get(i+1) * (h - 50));
            g.drawLine(x1, y1, x2, y2);
        }

        // Vehicles = BLUE LINE
        g.setColor(new Color(50, 90, 220));
        for (int i = 0; i < n - 1; i++) {
            int x1 = (int)(30 + i * xstep);
            int x2 = (int)(30 + (i+1)*xstep);
            int y1 = (int)(h - 30 - ((double)vehiclesData.get(i) / maxV) * (h - 50));
            int y2 = (int)(h - 30 - ((double)vehiclesData.get(i+1) / maxV) * (h - 50));
            g.drawLine(x1, y1, x2, y2);
        }

        // Labels
        g.setColor(Color.BLACK);
        g.drawString("Congestion", 40, 20);
        g.drawString("Vehicles", 140, 20);
    }

    /** Export as PNG */
    public void exportPNG() {
        try {
            BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            paint(g);
            g.dispose();

            ImageIO.write(img, "png", new File("chart_export.png"));
            JOptionPane.showMessageDialog(this, "Exported chart_export.png");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
