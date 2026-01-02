// VehicleTableDialog.java
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/** Displays all registered vehicles in a styled table */
public class VehicleTableDialog extends JDialog {
    public VehicleTableDialog(Frame owner) {
        super(owner, "All Registered Vehicles", true);
        setSize(760, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Registered Vehicles", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));
        add(title, BorderLayout.NORTH);

        String[] cols = {"Name", "CNIC", "Age", "Vehicle No", "License"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        // nice header
        JTableHeader hh = table.getTableHeader();
        hh.setBackground(new Color(240,240,240));
        hh.setFont(hh.getFont().deriveFont(Font.BOLD));

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        add(sp, BorderLayout.CENTER);

        // load rows
        File f = new File("data/registered_vehicles.csv");
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line; boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first) { first = false; continue; }
                    VehicleRecord r = VehicleRecord.fromCSV(line.trim());
                    if (r != null) model.addRow(new Object[]{r.name, r.cnic, r.age, r.vehicleNumber, r.licenseNumber});
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }

        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        close.setBackground(new Color(46,204,113));
        close.setForeground(Color.WHITE);
        footer.add(close);
        add(footer, BorderLayout.SOUTH);
    }

    public static void open(Frame owner) {
        VehicleTableDialog d = new VehicleTableDialog(owner);
        d.setVisible(true);
    }
}
