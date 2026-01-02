// RegisterVehicleDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Modal dialog to register a vehicle.
 * Stores to VehicleStore (CSV append) and in-memory tree.
 */
public class RegisterVehicleDialog extends JDialog {
    private final JTextField nameF = new JTextField(20);
    private final JTextField cnicF = new JTextField(20);
    private final JTextField ageF = new JTextField(4);
    private final JTextField vehicleNumF = new JTextField(12);
    private final JTextField licenseF = new JTextField(12);

    private final VehicleStore store;

    public RegisterVehicleDialog(JFrame owner, VehicleStore store) {
        super(owner, "Register Vehicle", true);
        this.store = store;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8,8));
        JPanel form = new JPanel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.gridx=0; c.gridy=0; form.add(new JLabel("Name:"), c);
        c.gridx=1; form.add(nameF, c);
        c.gridx=0; c.gridy++; form.add(new JLabel("CNIC:"), c);
        c.gridx=1; form.add(cnicF, c);
        c.gridx=0; c.gridy++; form.add(new JLabel("Age:"), c);
        c.gridx=1; form.add(ageF, c);
        c.gridx=0; c.gridy++; form.add(new JLabel("Vehicle No.:"), c);
        c.gridx=1; form.add(vehicleNumF, c);
        c.gridx=0; c.gridy++; form.add(new JLabel("License No.:"), c);
        c.gridx=1; form.add(licenseF, c);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerBtn = new JButton("Register");
        JButton cancelBtn = new JButton("Cancel");
        buttons.add(cancelBtn);
        buttons.add(registerBtn);
        add(buttons, BorderLayout.SOUTH);

        registerBtn.addActionListener(e -> onRegister());
        cancelBtn.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void onRegister() {
        String name = nameF.getText().trim();
        String cnic = cnicF.getText().trim();
        String ageS = ageF.getText().trim();
        String vnum = vehicleNumF.getText().trim();
        String lnum = licenseF.getText().trim();

        if (name.isEmpty() || cnic.isEmpty() || ageS.isEmpty() || vnum.isEmpty() || lnum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int age = 0;
        try { age = Integer.parseInt(ageS); if (age <= 0) throw new Exception(); } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid age.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check duplicate license
        if (store.search(lnum) != null) {
            JOptionPane.showMessageDialog(this, "A record with this license already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehicle v = new Vehicle(name, cnic, age, vnum, lnum);
        boolean ok = store.saveAppend(v);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Registered successfully.", "OK", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save. See console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
