import javax.swing.*;
import java.awt.*;

/**
 * Main Window for UrbanFlow Visualization.
 * This class is responsible ONLY for:
 *   - creating Swing window
 *   - hosting the UrbanFlowPanel (canvas)
 *   - providing a modern themed frame
 */
public class GraphView extends JFrame {

    private UrbanFlowPanel flowPanel;

    public GraphView() {

        setTitle("UrbanFlow – Traffic Network Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Modern flat theme look
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            System.out.println("Look & Feel not supported, using default.");
        }

        // Main drawing component – passed later to Panel
        flowPanel = new UrbanFlowPanel();
        add(flowPanel, BorderLayout.CENTER);

        // Put window nicely on screen
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphView frame = new GraphView();
            frame.setVisible(true);
        });
    }
}
