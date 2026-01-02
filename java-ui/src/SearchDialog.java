// SearchDialog.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * SearchDialog: builds AVL index from data/registered_vehicles.csv and allows searching by license.
 */
public class SearchDialog extends JDialog {
    private final JTextField licenseField = new JTextField();
    private final JTextArea resultArea = new JTextArea();
    private final AVLTree index = new AVLTree();

    public SearchDialog(Frame owner) {
        super(owner, "Search by License", true);
        setSize(520, 320);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // header
        JLabel title = new JLabel("Lookup by License", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));
        add(title, BorderLayout.NORTH);

        // center
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        center.setBackground(Color.WHITE);
        JPanel top = new JPanel(new BorderLayout(8,8));
        top.setBackground(Color.WHITE);
        top.add(new JLabel("License No:"), BorderLayout.WEST);
        licenseField.setPreferredSize(new Dimension(220,28));
        top.add(licenseField, BorderLayout.CENTER);
        JButton go = new JButton("Search");
        styleButton(go);
        top.add(go, BorderLayout.EAST);
        center.add(top, BorderLayout.NORTH);

        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(resultArea);
        center.add(sp, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // footer: load index and wire button
        buildIndex();
        go.addActionListener(e -> doSearch());
        licenseField.addActionListener(e -> doSearch());
    }

    private void styleButton(JButton b) {
        b.setBackground(new Color(46, 204, 113));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void buildIndex() {
        File f = new File("data/registered_vehicles.csv");
        if (!f.exists()) {
            resultArea.setText("No registration file found.\nRegister vehicles first.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header maybe
                line = line.trim();
                if (line.isEmpty()) continue;
                VehicleRecord r = VehicleRecord.fromCSV(line);
                if (r != null && r.licenseNumber != null && !r.licenseNumber.isEmpty()) {
                    index.insert(r.licenseNumber, r);
                }
            }
            resultArea.setText("Index built. Enter license and search.\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            resultArea.setText("Failed to build index: " + ex.getMessage());
        }
    }

    private void doSearch() {
        String lic = licenseField.getText().trim();
        if (lic.isEmpty()) {
            resultArea.setText("Enter license number.");
            return;
        }
        VehicleRecord r = index.find(lic);
        if (r == null) {
            resultArea.setText("Not found: " + lic);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Found:\n");
            sb.append("Name : ").append(r.name).append("\n");
            sb.append("CNIC : ").append(r.cnic).append("\n");
            sb.append("Age  : ").append(r.age).append("\n");
            sb.append("Vehicle No: ").append(r.vehicleNumber).append("\n");
            sb.append("License   : ").append(r.licenseNumber).append("\n");
            resultArea.setText(sb.toString());
        }
    }

    public static void open(Frame owner) {
        SearchDialog d = new SearchDialog(owner);
        d.setVisible(true);
    }
}
