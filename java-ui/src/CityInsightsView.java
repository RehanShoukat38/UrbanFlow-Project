import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.*;

public class CityInsightsView extends JPanel {

    // --- SUPREME PALETTE ---
    private static final Color DARK_VOID    = new Color(5, 6, 8);
    private static final Color NEON_CYAN    = new Color(0, 255, 242);
    private static final Color NEON_PURPLE  = new Color(188, 0, 255);
    private static final Color GLOW_RED     = new Color(255, 30, 80);
    private static final Color TEXT_HUD     = new Color(180, 200, 220);

    private final UrbanFlowPanel map;
    private final JLabel cityHeader = new JLabel("---");
    private final JLabel degreeVal = new JLabel("00");
    private final JLabel centralVal = new JLabel("0.000");
    private final SupremeGauge mainGauge = new SupremeGauge();
    private final JTextArea terminalLog = new JTextArea(6, 20);
    private final StatusBadge currentStatus = new StatusBadge("SYSTEM READY");

    public CityInsightsView(UrbanFlowPanel map) {
        this.map = map;
        setupMainLayout();
    }

    private void setupMainLayout() {
        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(450, 0));
        setBackground(DARK_VOID);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. HEADER WITH SCANLINE EFFECT
        JPanel topArea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new LinearGradientPaint(0, 0, 0, getHeight(), 
                    new float[]{0f, 0.5f, 1f}, 
                    new Color[]{new Color(0,255,242,20), new Color(0,0,0,0), new Color(0,255,242,10)}));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topArea.setLayout(new BoxLayout(topArea, BoxLayout.Y_AXIS));
        topArea.setOpaque(false);

        cityHeader.setFont(new Font("SansSerif", Font.BOLD, 72));
        cityHeader.setForeground(Color.WHITE);
        
        topArea.add(cityHeader);
        topArea.add(Box.createVerticalStrut(5));
        topArea.add(currentStatus);
        topArea.add(Box.createVerticalStrut(30));

        // 2. ANALYTICS CENTER
        JPanel centerBody = new JPanel();
        centerBody.setLayout(new BoxLayout(centerBody, BoxLayout.Y_AXIS));
        centerBody.setOpaque(false);

        centerBody.add(createHudLabel("TOPOLOGY_INTEGRITY"));
        centerBody.add(Box.createVerticalStrut(10));
        centerBody.add(mainGauge);
        centerBody.add(Box.createVerticalStrut(40));

        JPanel statsGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        statsGrid.setOpaque(false);
        statsGrid.add(createMetricBox("DEGREE_INDEX", degreeVal));
        statsGrid.add(createMetricBox("CENTRALITY", centralVal));
        centerBody.add(statsGrid);

        // 3. TERMINAL (BOTTOM)
        JPanel bottomTerminal = new JPanel(new BorderLayout());
        bottomTerminal.setOpaque(false);
        bottomTerminal.setBorder(new TitledBorder(new LineBorder(TEXT_HUD, 1), "SYS_LOG", 0, 0, null, NEON_CYAN));
        
        terminalLog.setBackground(new Color(10, 12, 15));
        terminalLog.setForeground(NEON_CYAN);
        terminalLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        terminalLog.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(terminalLog);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(0, 140));
        bottomTerminal.add(scroll);

        add(topArea, BorderLayout.NORTH);
        add(centerBody, BorderLayout.CENTER);
        add(bottomTerminal, BorderLayout.SOUTH);
    }

    public void onCityClicked(String id) {
        cityHeader.setText(id.toUpperCase());
        int d = map.getCityDegree(id);
        double c = Math.min(1.0, d / 9.0);
        
        degreeVal.setText(String.format("%02d", d));
        centralVal.setText(String.format("%.3f", c));
        mainGauge.updateValue((int)(c * 100));

        if (c > 0.65) {
            currentStatus.refresh("CRITICAL_HUB", GLOW_RED);
        } else {
            currentStatus.refresh("STABLE_NODE", NEON_CYAN);
        }

        terminalLog.append(" > SYNCING NODE [" + id + "]\n");
        terminalLog.append(" > CALCULATING TOPOLOGICAL WEIGHT...\n");
        terminalLog.setCaretPosition(terminalLog.getDocument().getLength());
        
        map.highlightCity(id, true);
    }

    private JPanel createMetricBox(String title, JLabel val) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(20, 25, 35));
        p.setBorder(new CompoundBorder(new LineBorder(new Color(255,255,255,10)), new EmptyBorder(15, 15, 15, 15)));
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Monospaced", Font.BOLD, 10));
        t.setForeground(TEXT_HUD);
        
        val.setFont(new Font("Monospaced", Font.BOLD, 32));
        val.setForeground(Color.WHITE);

        p.add(t, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    private JLabel createHudLabel(String txt) {
        JLabel l = new JLabel("◢ " + txt);
        l.setFont(new Font("Monospaced", Font.BOLD, 12));
        l.setForeground(TEXT_HUD);
        return l;
    }

    // --- CUSTOM COMPONENTS ---

    private class SupremeGauge extends JComponent {
        private double val = 0, target = 0;
        public SupremeGauge() {
            setPreferredSize(new Dimension(240, 240));
            new Timer(16, e -> { if(Math.abs(val-target)>0.1){ val+=(target-val)*0.1; repaint(); }}).start();
        }
        public void updateValue(int v) { this.target = v; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = 200, x = (getWidth()-size)/2, y = (getHeight()-size)/2;

            // Background HUD Ring
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{2, 5}, 0));
            g2.setColor(new Color(255,255,255,20));
            g2.drawOval(x-10, y-10, size+20, size+20);

            // Glow Path
            g2.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(NEON_CYAN.getRed(), NEON_CYAN.getGreen(), NEON_CYAN.getBlue(), 40));
            g2.drawArc(x, y, size, size, 225, (int)(val * -2.7));

            // Core Path
            g2.setPaint(new GradientPaint(x, y, NEON_PURPLE, x+size, y+size, NEON_CYAN));
            g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(x, y, size, size, 225, (int)(val * -2.7));

            // Text
            g2.setFont(new Font("SansSerif", Font.BOLD, 52));
            g2.setColor(Color.WHITE);
            String s = (int)val + "%";
            g2.drawString(s, (getWidth()-g2.getFontMetrics().stringWidth(s))/2, y + size/2 + 20);
            g2.dispose();
        }
    }

    private class StatusBadge extends JLabel {
        private Color bg;
        public StatusBadge(String t) {
            super(t);
            this.bg = NEON_CYAN;
            setForeground(Color.WHITE);
            setFont(new Font("Monospaced", Font.BOLD, 12));
            setOpaque(false);
            setBorder(new EmptyBorder(5, 15, 5, 15));
        }
        public void refresh(String t, Color c) { setText(t); this.bg = c; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}