import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

/* ============================================================
 * MAIN APPLICATION FRAME
 * ============================================================ */
public class CityInsightsDemo extends JFrame {

    public CityInsightsDemo() {
        setTitle("CITY INSIGHTS – INTERACTIVE ANALYTICS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        UrbanFlowMap map = new UrbanFlowMap();
        CityInsightsPanel insights = new CityInsightsPanel(map);

        map.setSelectionListener(insights::updateCity);

        setLayout(new BorderLayout());
        add(insights, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CityInsightsDemo::new);
    }
}

/* ============================================================
 * MAP PANEL (CLICKABLE CITIES)
 * ============================================================ */
class UrbanFlowMap extends JPanel {

    private final Map<String, Point> cities = new LinkedHashMap<>();
    private String selectedCity;
    private CitySelectionListener listener;

    interface CitySelectionListener {
        void onCitySelected(String city);
    }

    public UrbanFlowMap() {
        setBackground(new Color(10, 12, 18));

        cities.put("LHR", new Point(200, 200));
        cities.put("DXB", new Point(420, 380));
        cities.put("JFK", new Point(600, 200));
        cities.put("SIN", new Point(520, 460));
        cities.put("HKG", new Point(760, 300));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (var entry : cities.entrySet()) {
                    if (e.getPoint().distance(entry.getValue()) < 22) {
                        selectedCity = entry.getKey();
                        if (listener != null) listener.onCitySelected(selectedCity);
                        repaint();
                        break;
                    }
                }
            }
        });
    }

    public void setSelectionListener(CitySelectionListener l) {
        this.listener = l;
    }

    public int getCityDegree(String city) {
        return city.length() * 2 + new Random().nextInt(5);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Point p : cities.values()) {
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillOval(p.x - 6, p.y - 6, 12, 12);
        }

        for (var e : cities.entrySet()) {
            Point p = e.getValue();
            boolean sel = e.getKey().equals(selectedCity);

            if (sel) {
                g2.setColor(new Color(255, 215, 0, 180));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(p.x - 22, p.y - 22, 44, 44);
            }

            g2.setColor(sel ? Color.WHITE : new Color(0, 255, 242));
            g2.fillOval(p.x - 10, p.y - 10, 20, 20);
            g2.setColor(Color.WHITE);
            g2.drawString(e.getKey(), p.x - 10, p.y - 15);
        }
    }
}

/* ============================================================
 * CITY INSIGHTS PANEL
 * ============================================================ */
class CityInsightsPanel extends JPanel {

    private final UrbanFlowMap map;

    private final JLabel cityLabel = new JLabel("---");
    private final JLabel degreeLabel = new JLabel("00");
    private final JLabel centralityLabel = new JLabel("0.000");
    private final JLabel statusChip = new JLabel("CLICK A CITY");
    private final JProgressBar loadBar = new JProgressBar(0, 100);
    private final Gauge gauge = new Gauge();

    public CityInsightsPanel(UrbanFlowMap map) {
        this.map = map;

        setLayout(new BorderLayout());
        setBackground(new Color(6, 7, 10));

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(420, 0));
        side.setBackground(new Color(15, 18, 24));
        side.setBorder(new EmptyBorder(30, 30, 30, 30));

        cityLabel.setFont(new Font("SansSerif", Font.BOLD, 52));
        cityLabel.setForeground(Color.WHITE);

        statusChip.setOpaque(true);
        statusChip.setBackground(new Color(40, 45, 55));
        statusChip.setForeground(Color.LIGHT_GRAY);
        statusChip.setBorder(new EmptyBorder(6, 15, 6, 15));

        side.add(cityLabel);
        side.add(Box.createVerticalStrut(10));
        side.add(statusChip);
        side.add(Box.createVerticalStrut(30));
        side.add(gauge);
        side.add(Box.createVerticalStrut(30));

        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);
        grid.add(card("DEGREE", degreeLabel));
        grid.add(card("CENTRALITY", centralityLabel));
        side.add(grid);

        side.add(Box.createVerticalStrut(30));
        setupLoadBar();
        side.add(loadBar);

        add(side, BorderLayout.WEST);
        add(map, BorderLayout.CENTER);
    }

    public void updateCity(String city) {
        cityLabel.setText(city);

        int degree = map.getCityDegree(city);
        double centrality = Math.min(1.0, degree / 10.0);
        double risk = Math.min(99, centrality * 100 + Math.random() * 10);

        degreeLabel.setText(String.format("%02d", degree));
        centralityLabel.setText(String.format("%.3f", centrality));
        gauge.animateTo((int) risk);
        loadBar.setValue((int) risk);

        if (risk > 70) {
            statusChip.setText("CRITICAL INFRASTRUCTURE NODE");
            statusChip.setBackground(new Color(255, 30, 80));
            statusChip.setForeground(Color.WHITE);
        } else {
            statusChip.setText("STABLE REGIONAL NODE");
            statusChip.setBackground(new Color(0, 255, 150));
            statusChip.setForeground(Color.BLACK);
        }
    }

    private JPanel card(String title, JLabel value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(22, 26, 35));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel t = new JLabel(title);
        t.setForeground(Color.GRAY);
        t.setFont(new Font("SansSerif", Font.BOLD, 10));
        value.setFont(new Font("Monospaced", Font.BOLD, 24));
        value.setForeground(Color.WHITE);
        p.add(t, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private void setupLoadBar() {
        loadBar.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                int w = c.getWidth(), h = c.getHeight();
                g2.setColor(new Color(30, 35, 45));
                g2.fillRoundRect(0, 0, w, h, h, h);
                g2.setPaint(new GradientPaint(
                        0, 0, new Color(188, 0, 255),
                        w, 0, new Color(0, 255, 242)
                ));
                g2.fillRoundRect(0, 0,
                        (int) (w * loadBar.getPercentComplete()), h, h, h);
            }
        });
    }

    /* ========================================================
     * GAUGE COMPONENT
     * ======================================================== */
    static class Gauge extends JComponent {
        private double cur = 0, tar = 0;

        public Gauge() {
            new javax.swing.Timer(16, e -> {
                if (Math.abs(cur - tar) > 0.5) {
                    cur += (tar - cur) * 0.1;
                    repaint();
                }
            }).start();
        }

        public void animateTo(int v) {
            tar = v;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(40, 45, 55));
            g2.drawArc(10, 10, 200, 200, 225, -270);
            g2.setColor(cur > 70 ? new Color(255, 30, 80) : new Color(0, 255, 242));
            g2.drawArc(10, 10, 200, 200, 225, (int) (cur * -2.7));
            g2.setFont(new Font("SansSerif", Font.BOLD, 42));
            g2.setColor(Color.WHITE);
            String s = (int) cur + "%";
            g2.drawString(s,
                    110 - g2.getFontMetrics().stringWidth(s) / 2, 130);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(220, 220);
        }
    }
}
