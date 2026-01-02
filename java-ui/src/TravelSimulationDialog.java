import javax.swing.*;
import java.awt.*;

/**
 * TravelSimulationDialog
 * ----------------------
 * Desktop-style dialog for selecting:
 *  - Source City
 *  - Destination City
 *  - Simulate Route
 *
 * NO mobile UI
 * NO embedded map
 */
public class TravelSimulationDialog extends JDialog {

    private static final String[] CITIES = {
            "ISL","RWP","PSH","LHR","GRW","SKT","FSD",
            "SWL","MLT","BWP","DGK","QTA","SUK","HYD","KHI"
    };

    public TravelSimulationDialog(JFrame owner, UrbanFlowPanel map) {

        super(owner, "Travel Simulation", true);
        setSize(360, 220);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JComboBox<String> srcBox = new JComboBox<>(CITIES);
        JComboBox<String> dstBox = new JComboBox<>(CITIES);

        form.add(new JLabel("Source City"));
        form.add(srcBox);
        form.add(new JLabel("Destination City"));
        form.add(dstBox);

        JButton simulateBtn = new JButton("Simulate Route");
        simulateBtn.setBackground(new Color(0, 123, 255));
        simulateBtn.setForeground(Color.WHITE);
        simulateBtn.setFocusPainted(false);

        form.add(new JLabel());
        form.add(simulateBtn);

        add(form, BorderLayout.CENTER);

        // ---------------- ACTION ----------------
        simulateBtn.addActionListener(e -> {
            String src = (String) srcBox.getSelectedItem();
            String dst = (String) dstBox.getSelectedItem();

            map.startTravelSimulation(src, dst);
            dispose();
        });
    }
}
