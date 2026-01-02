import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class TravelSimulationView extends JPanel {

    private static final String[] CITIES = {
        "GIL", "SKD", "HUN", "DIR", "SWB", "PSH", "BNU", "KHT", "MAN", "ATK",
        "ISL", "RWP", "JLM", "MBD", "GUJ", "GRW", "SKT", "LHR", "SAH", "KSR",
        "FSD", "JHG", "OKA", "SWL", "KAS", "MLT", "VEH", "RYK", "BWP", "DGK",
        "QTA", "ZHB", "NWS", "TUR", "KUZ", "SUK", "BAD", "LKI", "KOT", "UMK",
        "HYD", "MPS", "KHI", "TBT"
    };

    // --- High-End Dark Theme Palette ---
    private static final Color BG_DEEP = new Color(13, 17, 23);       
    private static final Color CARD_DARK = new Color(22, 27, 34);     
    private static final Color ACCENT_BLUE = new Color(30, 144, 255); 
    private static final Color ACCENT_GREEN = new Color(46, 204, 113);
    private static final Color TEXT_BRIGHT = new Color(240, 246, 252);
    private static final Color TEXT_DIM = new Color(139, 148, 158);   
    private static final Color BORDER_GLOW = new Color(48, 54, 61);   

    public TravelSimulationView(UrbanFlowPanel map) {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DEEP);

        // =====================================================
        // LEFT – CONTROL PANEL
        // =====================================================
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(BG_DEEP);
        leftPanel.setPreferredSize(new Dimension(360, 0));
        leftPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // --- Header Section ---
        JLabel analystLabel = createLabel("ROUTE ANALYST", new Font("SansSerif", Font.BOLD, 12), ACCENT_BLUE, SwingConstants.LEFT);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 5, 0);
        leftPanel.add(analystLabel, gbc);

        JLabel mainTitle = createLabel("Route Simulation", new Font("Segoe UI", Font.BOLD, 32), TEXT_BRIGHT, SwingConstants.LEFT);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 40, 0);
        leftPanel.add(mainTitle, gbc);

        // --- Selection Fields ---
        addInputLabel(leftPanel, "Source City", 2);
        JComboBox<String> srcBox = styledCombo();
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 25, 0);
        leftPanel.add(srcBox, gbc);

        addInputLabel(leftPanel, "Destination City", 4);
        JComboBox<String> dstBox = styledCombo();
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 40, 0);
        leftPanel.add(dstBox, gbc);

        // --- Action Button ---
        JButton runBtn = new JButton("Simulate Route") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(ACCENT_BLUE.darker());
                else if (getModel().isRollover()) g2.setColor(new Color(65, 165, 255));
                else g2.setColor(ACCENT_BLUE);
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleMainButton(runBtn);
        gbc.gridy = 6; gbc.ipady = 18; 
        leftPanel.add(runBtn, gbc);

        // --- Visual Legend ---
        JLabel legendTitle = createLabel("VISUAL LEGEND", new Font("Segoe UI", Font.BOLD, 11), TEXT_DIM, SwingConstants.LEFT);
        gbc.gridy = 7; gbc.insets = new Insets(50, 0, 15, 0);
        gbc.ipady = 0;
        leftPanel.add(legendTitle, gbc);

        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 5, 0);
        leftPanel.add(createLegendItem("Shortest Path (Optimized)", ACCENT_GREEN), gbc);

        // Spacer to push content up
        gbc.gridy = 9; gbc.weighty = 1.0;
        leftPanel.add(Box.createVerticalGlue(), gbc);

        add(leftPanel, BorderLayout.WEST);

        // =====================================================
        // RIGHT – MAP VIEW
        // =====================================================
        JPanel mapContainer = new JPanel(new BorderLayout());
        mapContainer.setBackground(BG_DEEP);
        mapContainer.setBorder(new CompoundBorder(
            new EmptyBorder(20, 10, 20, 20),
            new LineBorder(BORDER_GLOW, 1)
        ));
        mapContainer.add(map, BorderLayout.CENTER);
        add(mapContainer, BorderLayout.CENTER);

        // Functionality
        runBtn.addActionListener(e -> {
            String src = (String) srcBox.getSelectedItem();
            String dst = (String) dstBox.getSelectedItem();
            if (src != null && dst != null) {
                map.startTravelSimulation(src, dst);
            }
        });
    }

    private void addInputLabel(JPanel p, String text, int row) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_DIM);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = row; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 2, 8, 0);
        p.add(l, c);
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, 12, 12);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setForeground(TEXT_BRIGHT);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(dot);
        p.add(l);
        return p;
    }

    private JComboBox<String> styledCombo() {
        JComboBox<String> combo = new JComboBox<>(CITIES);
        // Box background is bright enough for black text visibility
        combo.setBackground(TEXT_BRIGHT); 
        combo.setForeground(Color.BLACK);
        combo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        combo.setFocusable(false);

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index == -1) {
                    // This is the item visible in the box when closed
                    setBackground(TEXT_BRIGHT);
                    setForeground(Color.BLACK); // High contrast Black
                } else {
                    // Items inside the dropdown menu
                    setBackground(isSelected ? ACCENT_BLUE : CARD_DARK);
                    setForeground(TEXT_BRIGHT);
                }

                setBorder(new EmptyBorder(8, 12, 8, 12));
                return this;
            }
        });

        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▼");
                btn.setFont(new Font("Arial", Font.PLAIN, 10));
                btn.setForeground(Color.BLACK); // Black arrow to match text
                btn.setContentAreaFilled(false);
                btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                return btn;
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = (BasicComboPopup) super.createPopup();
                popup.setBorder(new LineBorder(BORDER_GLOW, 1));
                return popup;
            }
        });

        combo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_GLOW, 1, true),
                new EmptyBorder(6, 6, 6, 6)
        ));

        return combo;
    }

    private void styleMainButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel createLabel(String text, Font font, Color color, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
}