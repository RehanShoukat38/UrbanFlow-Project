// VehiclesListDialog.java
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/** Show all registered vehicles in a JTable. */
public class VehiclesListDialog extends JDialog {
    public VehiclesListDialog(JFrame owner, VehicleStore store) {
        super(owner, "All Registered Vehicles", true);
        List<Vehicle> list = store.inOrderList();
        String[] cols = {"Name", "CNIC", "Age", "Vehicle No.", "License No."};
        Object[][] data = new Object[list.size()][cols.length];
        for (int i=0;i<list.size();i++){
            Vehicle v = list.get(i);
            data[i][0] = v.getName();
            data[i][1] = v.getCnic();
            data[i][2] = v.getAge();
            data[i][3] = v.getVehicleNumber();
            data[i][4] = v.getLicenseNumber();
        }
        JTable table = new JTable(data, cols);
        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        setLayout(new BorderLayout());
        add(sp, BorderLayout.CENTER);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        btn.add(close);
        add(btn, BorderLayout.SOUTH);
        close.addActionListener(e -> dispose());

        setSize(700, 400);
        setLocationRelativeTo(owner);
    }
}
