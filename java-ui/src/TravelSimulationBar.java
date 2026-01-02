import javax.swing.*;
import java.awt.*;

/**
 * TravelSimulationBar
 * -------------------
 * Elegant desktop control bar (INLINE, not popup)
 */
public class TravelSimulationBar extends JPanel {

    private static final String[] CITIES = {
            "ISL","RWP","PSH","LHR","GRW","SKT","FSD",
            "SWL","MLT","BWP","DGK","QTA","SUK","HYD","KHI"
    };

    public TravelSimulationBar(UrbanFlowPanel map) {

        setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));
        setBackground(new Color(245, 248, 252));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                new Color(210, 215, 220)));

        JLabel srcLabel = new JLabel("Source City");
        JComboBox<String> srcBox = new JComboBox<>(CITIES);

        JLabel dstLabel = new JLabel("Destination City");
        JComboBox<String> dstBox = new JComboBox<>(CITIES);

        JButton simulateBtn = new JButton("Simulate Route");
        simulateBtn.setBackground(new Color(0, 123, 255));
        simulateBtn.setForeground(Color.WHITE);
        simulateBtn.setFocusPainted(false);

        simulateBtn.addActionListener(e -> {
            String s = (String) srcBox.getSelectedItem();
            String d = (String) dstBox.getSelectedItem();
            map.startTravelSimulation(s, d);
        });

        Font f = new Font("Segoe UI", Font.PLAIN, 13);
        srcLabel.setFont(f);
        dstLabel.setFont(f);
        srcBox.setFont(f);
        dstBox.setFont(f);
        simulateBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        add(srcLabel);
        add(srcBox);
        add(dstLabel);
        add(dstBox);
        add(simulateBtn);
    }
}
