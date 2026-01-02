import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Premium Register Vehicle Screen
 * Glass-morphism UI + Placeholder fields + Neon button.
 */
public class RegisterVehiclePanel extends JPanel {

    private final VehicleStore store;

    public RegisterVehiclePanel(VehicleStore store) {
        this.store = store;

        setLayout(new GridBagLayout());
        setBackground(new Color(230, 235, 240));

        JPanel card = buildGlassCard();
        add(card);
    }

    // -------------------------------------------------------
    //  GLASS CARD
    // -------------------------------------------------------
    private JPanel buildGlassCard() {

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON);

                // frosted background
                g.setColor(new Color(255, 255, 255, 100));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);

                // soft outline
                g.setColor(new Color(0, 0, 0, 40));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);

                super.paintComponent(g0);
            }
        };

        card.setOpaque(false);
        card.setPreferredSize(new Dimension(480, 550));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        // -------------------------------------------------------
        //  HEADER
        // -------------------------------------------------------
        JLabel title = new JLabel("Register New Vehicle");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(20, 55, 100));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(25));

        // -------------------------------------------------------
        //  INPUT FIELDS
        // -------------------------------------------------------
        PlaceholderField nameField = new PlaceholderField("Full Name");
        PlaceholderField cnicField = new PlaceholderField("CNIC Number");
        PlaceholderField ageField  = new PlaceholderField("Age");
        PlaceholderField vnumField = new PlaceholderField("Vehicle Number");
        PlaceholderField licField  = new PlaceholderField("License Number");

        card.add(nameField);
        card.add(Box.createVerticalStrut(16));
        card.add(cnicField);
        card.add(Box.createVerticalStrut(16));
        card.add(ageField);
        card.add(Box.createVerticalStrut(16));
        card.add(vnumField);
        card.add(Box.createVerticalStrut(16));
        card.add(licField);
        card.add(Box.createVerticalStrut(30));

        // -------------------------------------------------------
        //  SUBMIT BUTTON
        // -------------------------------------------------------
        JButton submit = new NeonButton("Register Vehicle");
        submit.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(submit);

        // -------------------------------------------------------
        //  ACTION LOGIC
        // -------------------------------------------------------
        submit.addActionListener(e -> {

            String name = nameField.getText().trim();
            String cnic = cnicField.getText().trim();
            String ageS = ageField.getText().trim();
            String vnum = vnumField.getText().trim();
            String lic  = licField.getText().trim();

            // Basic validation
            if (name.isEmpty() || cnic.isEmpty() || ageS.isEmpty() ||
                vnum.isEmpty() || lic.isEmpty()) {
                popup("All fields are required!", Color.RED);
                return;
            }

            int age;
            try { age = Integer.parseInt(ageS); }
            catch (Exception ex) {
                popup("Age must be a valid number!", Color.RED);
                return;
            }

            Vehicle v = new Vehicle(name, cnic, age, vnum, lic);
            boolean ok = store.saveAppend(v);

            if (ok) {
                popup("Vehicle Registered Successfully!", new Color(0,150,70));
                nameField.clear();
                cnicField.clear();
                ageField.clear();
                vnumField.clear();
                licField.clear();
            } else {
                popup("Registration Failed!", Color.RED);
            }
        });

        return card;
    }

    // -------------------------------------------------------
    //  FLOATING MESSAGE POPUP
    // -------------------------------------------------------
    private void popup(String msg, Color c) {
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 16));
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // -------------------------------------------------------
    //  GLASS PLACEHOLDER FIELD
    // -------------------------------------------------------
    private static class PlaceholderField extends JTextField {

        private final String placeholder;

        public PlaceholderField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 15));
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(255,255,255,150), 2, true),
                    new EmptyBorder(10, 14, 10, 14)
            ));
        }

        public void clear() { setText(""); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);

            // Background layer
            g.setColor(new Color(255, 255, 255, 180));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            super.paintComponent(g0);

            // Draw placeholder
            if (getText().isEmpty()) {
                g.setColor(new Color(150, 150, 150));
                g.setFont(getFont().deriveFont(Font.ITALIC));
                g.drawString(placeholder, 16, getHeight() / 2 + 5);
            }
        }
    }

    // -------------------------------------------------------
    //  NEON BUTTON COMPONENT
    // -------------------------------------------------------
    private static class NeonButton extends JButton {

        public NeonButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setForeground(Color.WHITE);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(12, 20, 12, 20));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);

            Color c;
            if (getModel().isPressed()) c = new Color(0, 120, 240);
            else if (getModel().isRollover()) c = new Color(0, 140, 255);
            else c = new Color(0, 123, 255);

            g.setColor(c);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

            super.paintComponent(g0);
        }
    }
}
