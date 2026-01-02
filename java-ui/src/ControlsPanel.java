import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * ControlsPanel
 * 
 * Side panel allowing:
 *  - Pick source & destination
 *  - Trigger route animation
 *  - Clear routes
 *  - Zoom reset
 *  - Highlight node
 * 
 * Works with the updated UrbanFlowPanel APIs.
 */
public class ControlsPanel extends JPanel {

    private final UrbanFlowPanel map;

    public ControlsPanel(UrbanFlowPanel map) {
        this.map = map;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(12,12,12,12));
        setBackground(new Color(240,240,245));

        JLabel title = new JLabel("Map Controls");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(title);
        add(Box.createVerticalStrut(12));

        // Node selector
        JPanel pickPanel = new JPanel();
        pickPanel.setLayout(new GridLayout(2,2,6,6));
        pickPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JComboBox<String> srcBox = new JComboBox<>();
        JComboBox<String> dstBox = new JComboBox<>();

        for (String city : map.getNodes().keySet()) {
            srcBox.addItem(city);
            dstBox.addItem(city);
        }

        pickPanel.add(new JLabel("Source:"));
        pickPanel.add(srcBox);
        pickPanel.add(new JLabel("Destination:"));
        pickPanel.add(dstBox);

        add(pickPanel);
        add(Box.createVerticalStrut(10));

        // Buttons
        JButton routeBtn = new JButton("Animate Route");
        JButton clearBtn = new JButton("Clear Routes");
        JButton resetZoomBtn = new JButton("Reset Zoom");
        JButton highlightBtn = new JButton("Highlight Source");

        routeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        clearBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        resetZoomBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        highlightBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        add(routeBtn);
        add(Box.createVerticalStrut(8));
        add(clearBtn);
        add(Box.createVerticalStrut(8));
        add(resetZoomBtn);
        add(Box.createVerticalStrut(8));
        add(highlightBtn);
        add(Box.createVerticalStrut(8));

        // Actions
        routeBtn.addActionListener(e -> {
            String s = (String) srcBox.getSelectedItem();
            String d = (String) dstBox.getSelectedItem();
            if (s != null && d != null) {
                map.animateRoute(s, d);
            }
        });

        clearBtn.addActionListener(e -> {
            map.stopRouteAnimation();
            map.clearAlternativeRoutes();
        });

        resetZoomBtn.addActionListener(e -> {
            map.setGlobalTrafficLevel(1.0);
        });

        highlightBtn.addActionListener(e -> {
            map.highlightNode((String)srcBox.getSelectedItem());
        });
    }
}
