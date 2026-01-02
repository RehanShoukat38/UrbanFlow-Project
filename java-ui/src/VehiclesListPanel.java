import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * VehiclesListPanel
 * ------------------
 * Displays all registered vehicles (from AVL tree) in a modern UI table.
 */
public class VehiclesListPanel extends JPanel {

    private final VehicleStore store;

    public VehiclesListPanel(VehicleStore store) {
        this.store = store;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Registered Vehicles");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(title, BorderLayout.NORTH);

        // Table Model
        String[] cols = {"Name", "CNIC", "Age", "Vehicle Number", "License Number"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        List<Vehicle> all = store.inOrderList();
        for (Vehicle v : all) {
            model.addRow(new Object[]{
                    v.getName(),
                    v.getCnic(),
                    v.getAge(),
                    v.getVehicleNumber(),
                    v.getLicenseNumber()
            });
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane pane = new JScrollPane(table);
        pane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        add(pane, BorderLayout.CENTER);
    }
}
