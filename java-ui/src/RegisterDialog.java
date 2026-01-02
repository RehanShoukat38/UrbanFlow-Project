// RegisterDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RegisterDialog: attractive form, validates and appends to data/registered_vehicles.csv
 */
public class RegisterDialog extends JDialog {
    private final JTextField nameField = new JTextField();
    private final JTextField cnicField = new JTextField();
    private final JTextField ageField = new JTextField();
    private final JTextField vehicleField = new JTextField();
    private final JTextField licenseField = new JTextField();
    private final JLabel status = new JLabel(" ");

    private final File storageFile = new File("data/registered_vehicles.csv");

    public RegisterDialog(Frame owner) {
        super(owner, "Register Vehicle", true);
        setLayout(new BorderLayout());
        setSize(420, 380);
        setLocationRelativeTo(owner);

        // Top: title
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        JLabel title = new JLabel("Register Vehicle", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Form card
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8,8,8,8);

        int row = 0;
        addRow(card, g, row++, "Full name", nameField);
        addRow(card, g, row++, "CNIC", cnicField);
        addRow(card, g, row++, "Age", ageField);
        addRow(card, g, row++, "Vehicle No.", vehicleField);
        addRow(card, g, row++, "License No.", licenseField);

        // status
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        status.setForeground(new Color(80,80,80));
        card.add(status, g);

        add(card, BorderLayout.CENTER);

        // Buttons
        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        styleButton(save);
        styleButton(cancel);
        footer.add(save);
        footer.add(cancel);
        add(footer, BorderLayout.SOUTH);

        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JTextField field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(new JLabel(label), g);
        g.gridx = 1; g.gridy = row; g.weightx = 1.0;
        field.setPreferredSize(new Dimension(220, 28));
        field.setBackground(new Color(250,250,250));
        p.add(field, g);
        g.weightx = 0;
    }

    private void styleButton(JButton b) {
        b.setBackground(new Color(46, 204, 113)); // inDrive green
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String cnic = cnicField.getText().trim();
        String ageS = ageField.getText().trim();
        String vehicle = vehicleField.getText().trim();
        String license = licenseField.getText().trim();

        if (name.isEmpty() || cnic.isEmpty() || ageS.isEmpty() || vehicle.isEmpty() || license.isEmpty()) {
            status.setText("Please fill all fields.");
            return;
        }
        int age;
        try { age = Integer.parseInt(ageS); if (age <= 0) throw new NumberFormatException(); }
        catch (Exception ex) { status.setText("Invalid age."); return; }

        VehicleRecord r = new VehicleRecord(name, cnic, age, vehicle, license);

        try {
            // ensure data dir
            Path dir = Paths.get("data");
            if (!Files.exists(dir)) Files.createDirectories(dir);

            // append header if file doesn't exist
            boolean exists = storageFile.exists();
            try (FileWriter fw = new FileWriter(storageFile, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                if (!exists) bw.write("name,cnic,age,vehicleNumber,licenseNumber\n");
                bw.write(r.toCSVLine());
                bw.write("\n");
            }

            status.setForeground(new Color(0,120,40));
            status.setText("Saved ✓");
            // small delay then close
            Timer t = new Timer(700, ev -> dispose());
            t.setRepeats(false); t.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            status.setForeground(Color.RED);
            status.setText("Failed to save.");
        }
    }

    // Build a simple attractive modal:
    public static void open(Frame owner) {
        RegisterDialog d = new RegisterDialog(owner);
        d.setVisible(true);
    }
}
