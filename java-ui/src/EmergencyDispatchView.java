import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.PriorityQueue;

public class EmergencyDispatchView extends JPanel {

    // --- High-End Pro Palette ---
    private static final Color BG_DEEP      = new Color(13, 17, 23);
    private static final Color CARD_DARK    = new Color(21, 27, 35);
    private static final Color ACCENT_RED   = new Color(255, 69, 58);
    private static final Color ACCENT_AMBER = new Color(255, 159, 10);
    private static final Color ACCENT_BLUE  = new Color(10, 132, 255);
    private static final Color TEXT_MAIN    = new Color(240, 246, 252);
    private static final Color TEXT_MUTED   = new Color(139, 148, 158);
    private static final Color BORDER_PRO   = new Color(48, 54, 61, 180);

    private PriorityQueue<EmergencyRequest> pq;
    private DefaultListModel<EmergencyRequest> model;

    public EmergencyDispatchView() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_DEEP);

        pq = new PriorityQueue<>((a, b) -> b.severity - a.severity);
        model = new DefaultListModel<>();

        // ==========================================
        // 1. UNIFIED TOP BAR (TITLE + CONTROLS)
        // ==========================================
        JPanel unifiedTopBar = new JPanel(new BorderLayout(20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dark glass effect
                g2.setColor(CARD_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom border line
                g2.setColor(BORDER_PRO);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        unifiedTopBar.setPreferredSize(new Dimension(0, 120));
        unifiedTopBar.setBorder(new EmptyBorder(15, 40, 15, 40));

        // LEFT: Title and Status
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 0));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("EMERGENCY DISPATCH");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_MAIN);
        JLabel sub = new JLabel("● SYSTEM ACTIVE • REAL-TIME PRIORITY MONITORING");
        sub.setFont(new Font("Monospaced", Font.BOLD, 11));
        sub.setForeground(ACCENT_RED);
        titlePanel.add(title);
        titlePanel.add(sub);

        // RIGHT: Integrated Controls
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 8, 0, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = createStyledTextField();
        JSpinner severitySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        styleSpinner(severitySpinner);
        
        RoundButton addBtn = new RoundButton("ADD TASK", ACCENT_BLUE);
        RoundButton dispatchBtn = new RoundButton("DISPATCH NEXT", ACCENT_RED);

        // Add components to control cluster
        gbc.gridx = 0; controls.add(createInputLabel("UNIT ID"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; controls.add(idField, gbc);
        gbc.gridx = 2; gbc.weightx = 0; controls.add(createInputLabel("PRIORITY"), gbc);
        gbc.gridx = 3; controls.add(severitySpinner, gbc);
        gbc.gridx = 4; controls.add(addBtn, gbc);
        gbc.gridx = 5; controls.add(dispatchBtn, gbc);

        unifiedTopBar.add(titlePanel, BorderLayout.WEST);
        unifiedTopBar.add(controls, BorderLayout.EAST);
        add(unifiedTopBar, BorderLayout.NORTH);

        // ==========================================
        // 2. MAIN LIST AREA (Full height, no bottom bar)
        // ==========================================
        JList<EmergencyRequest> list = new JList<>(model);
        list.setBackground(BG_DEEP);
        list.setCellRenderer(new ProRequestRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(80);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_DEEP);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setBackground(BG_DEEP);
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // ACTIONS
        // ==========================================
        addBtn.addActionListener(e -> {
            if (!idField.getText().trim().isEmpty()) {
                pq.offer(new EmergencyRequest(idField.getText().trim().toUpperCase(), (Integer) severitySpinner.getValue()));
                refresh();
                idField.setText("");
                idField.requestFocus();
            }
        });

        dispatchBtn.addActionListener(e -> {
            EmergencyRequest r = pq.poll();
            if (r != null) {
                showProDialog("UNIT DISPATCHED", "UNIT [" + r.id + "] DEPLOYED.\nPRIORITY: LEVEL " + r.severity);
                refresh();
            }
        });
    }

    private void refresh() {
        model.clear();
        pq.stream().sorted((a, b) -> b.severity - a.severity).forEach(model::addElement);
    }

    // --- Modern Component Factories ---
    private JLabel createInputLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JTextField createStyledTextField() {
        JTextField f = new JTextField(8);
        f.setBackground(new Color(13, 17, 23));
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(ACCENT_BLUE);
        f.setFont(new Font("Segoe UI", Font.BOLD, 13));
        f.setBorder(new CompoundBorder(new LineBorder(BORDER_PRO, 1), new EmptyBorder(5, 10, 5, 10)));
        return f;
    }

    private void styleSpinner(JSpinner s) {
        s.setBorder(new LineBorder(BORDER_PRO, 1));
        s.getEditor().getComponent(0).setBackground(new Color(13, 17, 23));
        s.getEditor().getComponent(0).setForeground(TEXT_MAIN);
        ((JSpinner.DefaultEditor)s.getEditor()).getTextField().setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void showProDialog(String title, String msg) {
        UIManager.put("OptionPane.background", CARD_DARK);
        UIManager.put("Panel.background", CARD_DARK);
        UIManager.put("OptionPane.messageForeground", TEXT_MAIN);
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.PLAIN_MESSAGE);
    }

    class RoundButton extends JButton {
        private Color baseColor;
        public RoundButton(String text, Color bg) {
            super(text);
            this.baseColor = bg;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(120, 35));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? baseColor.darker() : getModel().isRollover() ? baseColor.brighter() : baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    private class ProRequestRenderer extends JPanel implements ListCellRenderer<EmergencyRequest> {
        private JLabel idLbl = new JLabel();
        private JLabel statusLbl = new JLabel();
        private JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };

        public ProRequestRenderer() {
            setLayout(new BorderLayout());
            setOpaque(true);
            setBorder(new EmptyBorder(10, 40, 10, 40));
            idLbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
            idLbl.setForeground(TEXT_MAIN);
            statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            statusLbl.setForeground(Color.WHITE);
            badge.setOpaque(false);
            badge.setBorder(new EmptyBorder(3, 10, 3, 10));
            badge.add(statusLbl);
            add(idLbl, BorderLayout.WEST);
            add(badge, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends EmergencyRequest> list, 
                EmergencyRequest value, int index, boolean isSelected, boolean cellHasFocus) {
            idLbl.setText("UNIT DASH ID • " + value.id);
            if (value.severity >= 8) { statusLbl.setText("CRITICAL"); badge.setBackground(ACCENT_RED); }
            else if (value.severity >= 5) { statusLbl.setText("URGENT"); badge.setBackground(ACCENT_AMBER); }
            else { statusLbl.setText("ROUTINE"); badge.setBackground(ACCENT_BLUE); }
            
            setBackground(isSelected ? new Color(33, 39, 48) : BG_DEEP);
            setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, BORDER_PRO), new EmptyBorder(10, 40, 10, 40)));
            return this;
        }
    }
}