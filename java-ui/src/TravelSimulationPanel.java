import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class TravelSimulationPanel extends JPanel {

    private static final String[] CITY_CODES = {
            // Northern / GB
    "GIL","SKD","HUN",

    // KPK
    "DIR","SWB","PSH","BNU","KHT","MAN","ATK",

    // Upper Punjab
    "ISL","RWP","JLM","MBD","GUJ","GRW","SKT",

    // Central Punjab
    "LHR","SAH","KSR","FSD","JHG","OKA","SWL","KAS",

    // South Punjab
    "MLT","VEH","RYK","BWP","DGK",

    // Balochistan
    "QTA","ZHB","NWS","TUR","KUZ",

    // Sindh
    "SUK","BAD","LKI","KOT","UMK","HYD","MPS","KHI","TBT"

    };

    private UrbanFlowPanel map;

    private JComboBox<String> srcBox, dstBox;
    private JTextField citySearch;
    private JLabel statusLabel;


    private JButton simulateBtn, trafficSimBtn, resetBtn;
    private JButton compareBtn, insightsBtn;





    private JSlider trafficSlider;

    private JPanel comparePanel;
    private JLabel compareInfoLabel;

    public TravelSimulationPanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(245,247,250));

        initUI();
        initActions();
    }

    // -------------------------------------------------
    // UI SETUP
    // -------------------------------------------------
    private void initUI() {

        JPanel left = buildLeftPanel();
        add(left, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(20,20,20,20));
        add(center, BorderLayout.CENTER);

        map = new UrbanFlowPanel();

        JScrollPane scroll = new JScrollPane(map);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(18,24,32));

        center.add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {

        JPanel left = new GlassPanel();
        left.setPreferredSize(new Dimension(320,0));
        left.setBorder(new EmptyBorder(20,20,20,20));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        statusLabel = new JLabel("● Ready");
statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
statusLabel.setForeground(new Color(0, 120, 0));

left.add(Box.createVerticalStrut(10));
left.add(statusLabel);


        JLabel title = new JLabel("Travel Simulation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(title);
        left.add(Box.createVerticalStrut(16));

        citySearch = new JTextField();
        citySearch.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        left.add(citySearch);
        left.add(Box.createVerticalStrut(10));

        left.add(new JLabel("Source City"));
        srcBox = styledCombo();
        left.add(srcBox);

        left.add(new JLabel("Destination City"));
        dstBox = styledCombo();
        left.add(dstBox);
        left.add(Box.createVerticalStrut(12));

        simulateBtn   = styledButton("Simulate Route", new Color(0,123,255));
        trafficSimBtn = styledButton("Traffic Simulation Route", new Color(255,120,40));
        resetBtn      = styledButton("Reset Route", new Color(200,40,40));
        compareBtn    = styledButton("Compare Routes", new Color(20,140,80));
        insightsBtn   = styledButton("City Insights", new Color(90,60,180));

        left.add(simulateBtn);
        left.add(Box.createVerticalStrut(6));
        left.add(trafficSimBtn);
        left.add(Box.createVerticalStrut(6));
        left.add(resetBtn);
        left.add(Box.createVerticalStrut(6));
        left.add(compareBtn);
        left.add(Box.createVerticalStrut(6));
        left.add(insightsBtn);
        left.add(Box.createVerticalStrut(12));

        trafficSlider = new JSlider(0,100,20);
        trafficSlider.setBorder(BorderFactory.createTitledBorder("Traffic Intensity"));
        left.add(trafficSlider);

        comparePanel = new JPanel();
        comparePanel.setBorder(BorderFactory.createTitledBorder("Comparison"));
        comparePanel.setVisible(false);
        compareInfoLabel = new JLabel();
        comparePanel.add(compareInfoLabel);
        left.add(comparePanel);

        statusLabel = new JLabel("● Ready");
statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
statusLabel.setForeground(new Color(0,120,0));
        left.add(Box.createVerticalStrut(10));
        left.add(statusLabel);

        return left;
    }

    // -------------------------------------------------
    // ACTIONS
    // -------------------------------------------------
    private void initActions() {

        citySearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterCities(); }
            public void removeUpdate(DocumentEvent e) { filterCities(); }
            public void changedUpdate(DocumentEvent e) { filterCities(); }
        });

        simulateBtn.addActionListener(e -> simulateRoute());

        // ⭐ OLD TRAFFIC SIMULATION (RESTORED)
        trafficSimBtn.addActionListener(e -> {
            simulateRoute();
            map.setGlobalTrafficLevel(1.5);
            statusLabel.setText("Traffic simulation active");
        });

        resetBtn.addActionListener(e -> {
            map.stopRouteAnimation();
            map.clearAlternativeRoutes();
            statusLabel.setText("Route reset");
        });

        compareBtn.addActionListener(e -> compareRoutes());

        trafficSlider.addChangeListener(e ->
                map.setGlobalTrafficLevel(trafficSlider.getValue()/100.0));
    }

    private void simulateRoute() {

        String s = (String) srcBox.getSelectedItem();
        String d = (String) dstBox.getSelectedItem();

        if (s == null || d == null || s.equals(d)) return;

        map.animateRoute(s, d);
        statusLabel.setText("● Route Active: " + s + " → " + d);
        statusLabel.setForeground(new Color(0,102,204));

    }

    private void compareRoutes() {

        String s = (String) srcBox.getSelectedItem();
        String d = (String) dstBox.getSelectedItem();

        if (s == null || d == null || s.equals(d)) return;

        var r1 = map.findShortestPath(s,d,"distance");
        var r2 = map.findShortestPath(s,d,"time");
        var r3 = map.findShortestPath(s,d,"cost");

        java.util.List<UrbanFlowPanel.RouteResult> list = new java.util.ArrayList<>();
        if (r1 != null) list.add(r1);
        if (r2 != null) list.add(r2);
        if (r3 != null) list.add(r3);

        map.setAlternativeRoutes(list);

        comparePanel.setVisible(true);
        compareInfoLabel.setText("<html>Alternative routes shown on map</html>");
        statusLabel.setText("Comparing routes");
    }

    // -------------------------------------------------
    // HELPERS
    // -------------------------------------------------
    private JComboBox<String> styledCombo() {
    JComboBox<String> b = new JComboBox<>(CITY_CODES);
    b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
    b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    b.setBackground(Color.WHITE);
    b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            BorderFactory.createEmptyBorder(6,10,6,10)
    ));
    return b;
}


    private JButton styledButton(String text, Color color) {
    JButton b = new JButton(text);
    b.setBackground(color);
    b.setForeground(Color.WHITE);
    b.setFont(new Font("Segoe UI", Font.BOLD, 14));
    b.setFocusPainted(false);
    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    b.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

    b.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent e) {
            b.setBackground(color.darker());
        }
        public void mouseExited(java.awt.event.MouseEvent e) {
            b.setBackground(color);
        }
    });
    return b;
}


    private void filterCities() {
        String q = citySearch.getText().toUpperCase();
        DefaultComboBoxModel<String> m = new DefaultComboBoxModel<>();
        for (String c : CITY_CODES)
            if (c.contains(q)) m.addElement(c);
        srcBox.setModel(m);
        dstBox.setModel(m);
    }

    static class GlassPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(255,255,255,220));
            g2.fillRoundRect(0,0,getWidth(),getHeight(),25,25);
        }
    }
}
