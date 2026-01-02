// LookupLicenseDialog.java
import javax.swing.*;
import java.awt.*;

public class LookupLicenseDialog extends JDialog {
    private final JTextField licenseF = new JTextField(14);
    private final VehicleStore store;

    public LookupLicenseDialog(JFrame owner, VehicleStore store) {
        super(owner, "Lookup by License", true);
        this.store = store;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8,8));
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.add(new JLabel("License Number:"));
        p.add(licenseF);
        add(p, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton find = new JButton("Find");
        JButton close = new JButton("Close");
        btns.add(close);
        btns.add(find);
        add(btns, BorderLayout.SOUTH);

        find.addActionListener(e -> doFind());
        close.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(getOwner());
    }

    private void doFind() {
        String l = licenseF.getText().trim();
        if (l.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter license number."); return; }
        Vehicle v = store.search(l);
        if (v == null) {
            JOptionPane.showMessageDialog(this, "Not found.", "Lookup", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // show details
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(v.getName()).append("\n");
        sb.append("CNIC: ").append(v.getCnic()).append("\n");
        sb.append("Age: ").append(v.getAge()).append("\n");
        sb.append("Vehicle No.: ").append(v.getVehicleNumber()).append("\n");
        sb.append("License No.: ").append(v.getLicenseNumber()).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "Vehicle Details", JOptionPane.INFORMATION_MESSAGE);
    }
}
