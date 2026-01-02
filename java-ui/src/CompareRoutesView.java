import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.HashMap;


/**
 * =============================================================
 * CompareRoutesView - ADVANCED DATA VISUALIZATION
 * =============================================================
 * - Theme: Dark Mode (High Contrast for Data Focus)
 * - Feature: Dynamic Results Panel (Metrics comparison)
 * - Feature: Interactive route switching (Distance / Time)
 * - Focus: Desktop-grade, information-dense UI.
 * =============================================================
 */
public class CompareRoutesView extends JPanel {

    // --- UI/THEME CONSTANTS ---
    private static final Color BG_DARK = new Color(28, 32, 39);
    private static final Color FG_TEXT = new Color(200, 205, 210);
    private static final Color ACCENT_PRIMARY = new Color(70, 150, 255); // Blue (Data/Control)
    private static final Color ACCENT_DISTANCE = new Color(50, 200, 100); // Green (Shortest Distance)
    private static final Color ACCENT_TIME = new Color(255, 80, 80); // Red (Fastest Time)
    private static final Color CARD_BG = new Color(38, 43, 52);
    private static final Color BORDER_COLOR = new Color(50, 56, 68);

    private static final String[] CITIES = {
            "ISL","RWP","PSH","LHR","GRW","SKT","FSD",
            "SWL","MLT","BWP","DGK","QTA","SUK","HYD","KHI"
    };

    private final UrbanFlowPanel map;
    private JComboBox<String> srcBox;
    private JComboBox<String> dstBox;
    
    // NEW: Dynamic Labels for Results Panel
    private final JLabel distanceTimeLabel = new JLabel("N/A");
    private final JLabel shortestTimeLabel = new JLabel("N/A");
    private final JLabel statusLabel = new JLabel("Select cities to begin comparison.");

    public CompareRoutesView(UrbanFlowPanel map) {
        this.map = map;

        setLayout(new BorderLayout(25, 25));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setBackground(BG_DARK); // Dark Mode Background

        // Map shell uses the ACCENT_PRIMARY as a subtle glow/focus border
        add(buildGraphShell(), BorderLayout.CENTER);
        add(buildControlCard(), BorderLayout.WEST);
        
        // Initial setup for the boxes
        srcBox.setSelectedIndex(0); 
        dstBox.setSelectedIndex(CITIES.length - 1);

        // Run an initial comparison on load (optional)
        // runComparison();
    }

    // =========================================================
    // LEFT CARD — CONTROLS & RESULTS
    // =========================================================
    private JComponent buildControlCard() {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(360, 0)); // Wider card
        card.setBackground(CARD_BG);

        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(30, 25, 25, 25)
        ));
        
        // --- TITLE SECTION ---
        JLabel title = createLabel("ROUTE ANALYST", new Font("Segoe UI", Font.BOLD, 14), ACCENT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 5, 0));
        card.add(title);

        JLabel subtitle = createLabel("Shortest Distance vs Fastest Time", new Font("Segoe UI", Font.BOLD, 28), FG_TEXT);
        subtitle.setBorder(new EmptyBorder(0, 0, 25, 0));
        card.add(subtitle);

        // --- SELECTION SECTION ---
        card.add(createLabel("Source City", 12, new EmptyBorder(0, 0, 5, 0)));
        srcBox = createComboBox();
        card.add(srcBox);

        card.add(Box.createVerticalStrut(15));

        card.add(createLabel("Destination City", 12, new EmptyBorder(0, 0, 5, 0)));
        dstBox = createComboBox();
        card.add(dstBox);

        card.add(Box.createVerticalStrut(30));

        // --- BUTTON ---
        JButton compareBtn = createButton("RUN COMPARISON", ACCENT_PRIMARY);
        compareBtn.addActionListener(e -> runComparison());
        card.add(compareBtn);

        card.add(Box.createVerticalStrut(30));

        // --- DYNAMIC RESULTS PANEL (New Feature) ---
        card.add(buildResultsPanel());

        card.add(Box.createVerticalStrut(30));

        // --- ADVANCED LEGEND ---
        card.add(buildAdvancedLegend());

        card.add(Box.createVerticalGlue());
        
        // --- STATUS FOOTER ---
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(120, 140, 160));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(statusLabel);

        return card;
    }
    
    // --- NEW: Results and Metrics Panel ---
    private JComponent buildResultsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // 1. Shortest Distance (Green)
        panel.add(createMetricBox("Shortest Distance", "Total Distance", distanceTimeLabel, ACCENT_DISTANCE));
        
        // 2. Fastest Time (Red)
        panel.add(createMetricBox("Fastest Time", "Expected Travel Time", shortestTimeLabel, ACCENT_TIME));

        return panel;
    }
    
    // Helper to create the individual metric boxes
    private JComponent createMetricBox(String title, String subtitle, JLabel valueLabel, Color accent) {
        JPanel box = new JPanel(new BorderLayout(10, 0));
        box.setBackground(new Color(45, 51, 62)); // Slightly lighter card background
        box.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Value/Result (Big Number)
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accent);
        
        // Text Container
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = createLabel(title, new Font("Segoe UI", Font.BOLD, 12), FG_TEXT.darker());
        JLabel subtitleLabel = createLabel(subtitle, new Font("Segoe UI", Font.PLAIN, 10), FG_TEXT.darker().darker());
        
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        box.add(textPanel, BorderLayout.WEST);
        box.add(valueLabel, BorderLayout.EAST);
        
        return box;
    }

    // --- NEW: Advanced Legend ---
    private JComponent buildAdvancedLegend() {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setOpaque(false);
        
        box.add(createLabel("VISUAL LEGEND", 12, new EmptyBorder(0, 0, 10, 0)));
        
        box.add(legendRow(ACCENT_DISTANCE, "Shortest Route (Distance)", "Optimized for minimum length."));
        box.add(Box.createVerticalStrut(10));
        box.add(legendRow(ACCENT_TIME, "Fastest Route (Time)", "Optimized for speed (considers traffic)."));

        return box;
    }

    private JComponent legendRow(Color c, String title, String description) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row.setOpaque(false);

        // Color Dot
        JPanel dot = new JPanel();
        dot.setBackground(c);
        dot.setPreferredSize(new Dimension(16, 16));
        dot.setBorder(new LineBorder(c.darker().darker(), 2));
        dot.putClientProperty("JComponent.roundRect", Boolean.TRUE); // Optional: rounded corners

        // Text
        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);
        
        JLabel titleLabel = createLabel(title, new Font("Segoe UI", Font.BOLD, 13), FG_TEXT);
        JLabel descLabel = createLabel(description, new Font("Segoe UI", Font.PLAIN, 10), FG_TEXT.darker());
        
        text.add(titleLabel);
        text.add(descLabel);

        row.add(dot);
        row.add(text);
        return row;
    }


    // =========================================================
    // RIGHT — GRAPH
    // =========================================================
    private JComponent buildGraphShell() {

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BG_DARK.darker()); // Even darker background for map
        
        // Glowing border effect
        shell.setBorder(new CompoundBorder(
                new LineBorder(ACCENT_PRIMARY.darker(), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Ensure the UrbanFlowPanel (the map) has the proper dark mode look
        map.setBackground(BG_DARK.darker());
        shell.add(map, BorderLayout.CENTER);
        
        return shell;
    }

    // =========================================================
    // ACTION & DATA HANDLING
    // =========================================================
    private void runComparison() {

        String src = (String) srcBox.getSelectedItem();
        String dst = (String) dstBox.getSelectedItem();
        
        // Input validation (unchanged)
        if (src == null || dst == null || src.equals(dst)) {
            statusLabel.setText("Status: Please select two different cities.");
            statusLabel.setForeground(ACCENT_TIME);
            return;
        }

        // 1. Trigger map drawing
        map.compareRoutes(src, dst);

        // 2. Fetch and display comparison results (Simulated Data)
        // In a real application, map.compareRoutes would return a result object.
        RouteComparisonResult results = simulateRouteComparison(src, dst);
        
        // 3. Update the Results Panel
        distanceTimeLabel.setText(String.format("%.1f km", results.distanceRouteDistance));
        shortestTimeLabel.setText(String.format("%.1f min", results.timeRouteTime));
        
        // 4. Update the Status Footer
        statusLabel.setText("Status: Comparison complete. Fastest route saves " + 
                            String.format("%.1f min.", results.distanceRouteTime - results.timeRouteTime));
        statusLabel.setForeground(ACCENT_DISTANCE);
    }
    
    // --- NEW: Simulated Data Model ---
    private RouteComparisonResult simulateRouteComparison(String src, String dst) {
        // Simulate based on distance between city indices
        int srcIndex = CITIES_MAP.getOrDefault(src, 0);
        int dstIndex = CITIES_MAP.getOrDefault(dst, CITIES.length - 1);
        double baseDistance = Math.abs(dstIndex - srcIndex) * 150.0 + 50.0;
        
        // Shortest Distance Route Metrics
        double distRouteDist = baseDistance;
        double distRouteTime = baseDistance / 50.0 * 60.0; // Assume 50 km/h

        // Fastest Time Route Metrics (Slightly longer distance, much faster time)
        double timeRouteDist = baseDistance * 1.1; // 10% longer route
        double timeRouteTime = distRouteTime * 0.7; // 30% faster time

        return new RouteComparisonResult(
            distRouteDist, distRouteTime,
            timeRouteDist, timeRouteTime
        );
    }
    
    private static final Map<String, Integer> CITIES_MAP = new HashMap<>();

static {
    CITIES_MAP.put("ISL", 0);
    CITIES_MAP.put("RWP", 1);
    CITIES_MAP.put("PSH", 2);
    CITIES_MAP.put("LHR", 3);
    CITIES_MAP.put("GRW", 4);
    CITIES_MAP.put("SKT", 5);
    CITIES_MAP.put("FSD", 6);
    CITIES_MAP.put("SWL", 7);
    CITIES_MAP.put("MLT", 8);
    CITIES_MAP.put("BWP", 9);
    CITIES_MAP.put("DGK", 10);
    CITIES_MAP.put("QTA", 11);
    CITIES_MAP.put("SUK", 12);
    CITIES_MAP.put("HYD", 13);
    CITIES_MAP.put("KHI", 14);
}

    
    private static class RouteComparisonResult {
        public final double distanceRouteDistance;
        public final double distanceRouteTime;
        public final double timeRouteDistance;
        public final double timeRouteTime;

        public RouteComparisonResult(double drd, double drt, double trd, double trt) {
            this.distanceRouteDistance = drd;
            this.distanceRouteTime = drt;
            this.timeRouteDistance = trd;
            this.timeRouteTime = trt;
        }
    }


    // =========================================================
    // UI HELPERS (Refactored for Dark Mode)
    // =========================================================
    private JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    
    private JLabel createLabel(String text, int fontSize, Border border) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        l.setForeground(FG_TEXT.darker());
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(border);
        return l;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> b = new JComboBox<>(CITIES);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        b.setBackground(CARD_BG);
        b.setForeground(FG_TEXT);
        // Custom look for the dropdown button
        b.setUI(new CustomBasicComboBoxUI()); 
        return b;
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(15, 25, 15, 25));
        b.putClientProperty("JComponent.roundRect", Boolean.TRUE); // Optional: rounded corners
        return b;
    }
    
    // Placeholder for a custom ComboBox UI for a modern look
    private static class CustomBasicComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton();
            button.setBackground(CARD_BG);
            button.setBorder(new EmptyBorder(0, 5, 0, 5));
            button.setIcon(new javax.swing.plaf.metal.MetalComboBoxIcon()); // Replace with a custom arrow icon
            return button;
        }
        
        // You would typically override more painting methods for a true dark mode look
    }
}