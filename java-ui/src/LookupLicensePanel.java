import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 * LookupLicensePanel - PRESTIGE EDITION
 * -------------------
 * Optimized height with high-contrast buttons and badge-based status indicators.
 */
public class LookupLicensePanel extends JPanel {

    private final VehicleStore store;
    private final JPanel resultCard;
    private final PlaceholderField licField;
    private final JLabel statusLabel;
    private Vehicle currentVehicle = null;

    // Prestige Color Palette
    private static final Color PRIMARY_ACCENT = new Color(0, 102, 255);
    private static final Color DEEP_SLATE = new Color(15, 23, 42);
    private static final Color NEUTRAL_GRAY = new Color(100, 116, 139);
    private static final Color SURFACE_WHITE = new Color(255, 255, 255);
    private static final Color SUCCESS_UI = new Color(16, 185, 129);
    private static final Color DANGER_UI = new Color(239, 68, 68);

    public LookupLicensePanel(VehicleStore store) {
        this.store = store;
        setLayout(new GridBagLayout());
        setOpaque(false);

        // --- MAIN PRESTIGE CARD ---
        JPanel prestigeCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Soft Outer Depth
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 32, 32);
                
                // Main Surface
                g2.setColor(SURFACE_WHITE);
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 30, 30);
                
                // Fine Hairline Border
                g2.setColor(new Color(210, 220, 240));
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(2, 2, getWidth()-5, getHeight()-5, 30, 30);
                g2.dispose();
            }
        };
        prestigeCard.setLayout(new BoxLayout(prestigeCard, BoxLayout.Y_AXIS));
        prestigeCard.setOpaque(false);
        prestigeCard.setBorder(new EmptyBorder(25, 45, 25, 45));

        // --- 1. HEADER ---
        JLabel title = new JLabel("REGISTRY DATABASE");
        title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
        title.setForeground(DEEP_SLATE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("SECURED VEHICLE AUTHENTICATION SYSTEM");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        subtitle.setForeground(NEUTRAL_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 2. SEARCH UNIT ---
        JPanel searchUnit = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        searchUnit.setOpaque(false);
        
        licField = new PlaceholderField("Enter Plate Number...");
        licField.setPreferredSize(new Dimension(280, 48));
        
        JButton verifyBtn = new GlossButton("VERIFY RECORD");
        verifyBtn.setPreferredSize(new Dimension(140, 48));

        searchUnit.add(licField);
        searchUnit.add(verifyBtn);

        // --- 3. STATUS DISPLAY ---
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- 4. RESULT GRID ---
        resultCard = buildDataGrid();
        resultCard.setVisible(false);

        // --- 5. ACTION CONTROL BAR ---
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        controlBar.setOpaque(false);
        
        JButton clearAction = new ActionButton("CLEAR VIEW", false);
        JButton exportAction = new ActionButton("EXPORT DATA", true);
        
        controlBar.add(clearAction);
        controlBar.add(exportAction);
        controlBar.setVisible(false);

        // ASSEMBLY
        prestigeCard.add(title);
        prestigeCard.add(Box.createVerticalStrut(4));
        prestigeCard.add(subtitle);
        prestigeCard.add(Box.createVerticalStrut(22));
        prestigeCard.add(searchUnit);
        prestigeCard.add(Box.createVerticalStrut(12));
        prestigeCard.add(statusLabel);
        prestigeCard.add(Box.createVerticalStrut(12));
        prestigeCard.add(resultCard);
        prestigeCard.add(Box.createVerticalStrut(18));
        prestigeCard.add(controlBar);

        add(prestigeCard);

        // LOGIC
        verifyBtn.addActionListener(e -> handleVerification(controlBar));
        licField.addActionListener(e -> handleVerification(controlBar));
        clearAction.addActionListener(e -> {
            resultCard.setVisible(false);
            controlBar.setVisible(false);
            licField.setText("");
            statusLabel.setText(" ");
            revalidate();
            repaint();
        });
        exportAction.addActionListener(e -> {
            if (currentVehicle != null) {
                JOptionPane.showMessageDialog(this, "Generating secure report for " + currentVehicle.getName());
            }
        });
    }

    private void handleVerification(JPanel controlBar) {
        String input = licField.getText().trim();
        if (input.isEmpty()) return;

        currentVehicle = store.search(input);
        if (currentVehicle != null) {
            statusLabel.setText("STATUS: AUTHENTICATED");
            statusLabel.setForeground(SUCCESS_UI);
            updateGrid(currentVehicle);
            resultCard.setVisible(true);
            controlBar.setVisible(true);
        } else {
            statusLabel.setText("STATUS: NO RECORD FOUND");
            statusLabel.setForeground(DANGER_UI);
            resultCard.setVisible(false);
            controlBar.setVisible(false);
        }
        revalidate();
        repaint();
    }

    private JPanel buildDataGrid() {
        JPanel grid = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(248, 250, 253));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(225, 230, 240));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
                g2.dispose();
            }
        };
        grid.setLayout(new GridLayout(3, 2, 15, 12));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(18, 25, 18, 25));
        grid.setPreferredSize(new Dimension(540, 180));
        for (int i = 0; i < 6; i++) grid.add(new JLabel());
        return grid;
    }

    private void updateGrid(Vehicle v) {
        setCell(0, "OWNER NAME", v.getName());
        setCell(1, "CNIC REFERENCE", v.getCnic());
        setCell(2, "VEHICLE SERIAL", v.getVehicleNumber());
        setCell(3, "LICENSE PERMIT", v.getLicenseNumber());
        setCell(4, "BIOMETRIC AGE", String.valueOf(v.getAge()));
        setCell(5, "VERIFICATION", "SYSTEM VALIDATED");
    }

    private void setCell(int idx, String k, String v) {
        JLabel l = (JLabel) resultCard.getComponent(idx);
        l.setText("<html><font color='#94A3B8' size='3'><b>" + k + "</b></font><br>" +
                 "<font color='#1E293B' size='4'><b>" + v + "</b></font></html>");
    }

    // --- CUSTOM BUTTON: GLOSS ACTION ---
    private static class GlossButton extends JButton {
        public GlossButton(String t) {
            super(t);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color base = getModel().isPressed() ? PRIMARY_ACCENT.darker() : 
                         getModel().isRollover() ? PRIMARY_ACCENT.brighter() : PRIMARY_ACCENT;
            
            g2.setColor(base);
            g2.fillRoundRect(-20, 0, getWidth()+20, getHeight(), 14, 14);
            
            // Gloss Sheen
            GradientPaint sheen = new GradientPaint(0, 0, new Color(255,255,255,60), 0, getHeight()/2, new Color(255,255,255,0));
            g2.setPaint(sheen);
            g2.fillRoundRect(-20, 0, getWidth()+20, getHeight()/2, 14, 14);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- CUSTOM BUTTON: ACTION STYLE ---
    private static class ActionButton extends JButton {
        private boolean isPrimary;
        public ActionButton(String t, boolean primary) {
            super(t);
            this.isPrimary = primary;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setForeground(primary ? Color.WHITE : DEEP_SLATE);
            setPreferredSize(new Dimension(120, 36));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (isPrimary) {
                g2.setColor(DEEP_SLATE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            } else {
                g2.setColor(SURFACE_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(200, 210, 230));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- CUSTOM INPUT FIELD ---
    private static class PlaceholderField extends JTextField {
        private String hint;
        public PlaceholderField(String h) { 
            this.hint = h; 
            setOpaque(false); 
            setFont(new Font("Monospaced", Font.BOLD, 16)); 
            setBorder(new EmptyBorder(0, 18, 0, 18)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(245, 248, 252));
            g2.fillRoundRect(0, 0, getWidth()+20, getHeight(), 14, 14);
            g2.setColor(new Color(215, 225, 240));
            g2.drawRoundRect(0, 0, getWidth()+20, getHeight()-1, 14, 14);
            if (getText().isEmpty()) { 
                g2.setColor(NEUTRAL_GRAY); 
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                g2.drawString(hint, 18, getHeight()/2 + 5); 
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}