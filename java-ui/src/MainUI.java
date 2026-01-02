import javax.swing.*;
import java.awt.*;

/**
 * =============================================================
 * MainUI (DESKTOP • CLEAN • STABLE)
 * =============================================================
 * - Single UrbanFlowPanel instance
 * - Correct center view switching
 * - City Insights fully wired
 * =============================================================
 */
public class MainUI {

    private static JFrame frame;
    private static JPanel leftMenu;
    private static JPanel titleBar;
    private static JPanel centerView;

    // 🔥 SINGLE shared instances
    private static UrbanFlowPanel map;
    private static VehicleStore store;

    // -------------------------------------------------
    // CENTER VIEW SWITCHER
    // -------------------------------------------------
    private static void setCenterView(JComponent view) {
        centerView.removeAll();
        centerView.add(view, BorderLayout.CENTER);
        centerView.revalidate();
        centerView.repaint();
    }

    // -------------------------------------------------
    // APPLICATION LAUNCH
    // -------------------------------------------------
    public static void launch() {

        SwingUtilities.invokeLater(() -> {

            // -------------------------------------------------
            // Data Store
            // -------------------------------------------------
            store = new VehicleStore();
            store.loadFromFile();

            // -------------------------------------------------
            // Frame
            // -------------------------------------------------
            frame = new JFrame("UrbanFlow — Professional Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 900);
            frame.setLayout(new BorderLayout());

            // -------------------------------------------------
            // Title Bar
            // -------------------------------------------------
            titleBar = new JPanel(new BorderLayout());
            titleBar.setBackground(new Color(10, 90, 170));

            JLabel title = new JLabel("UrbanFlow Simulation");
            title.setForeground(Color.WHITE);
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 12));

            titleBar.add(title, BorderLayout.WEST);
            frame.add(titleBar, BorderLayout.NORTH);

            // -------------------------------------------------
            // Left Sidebar
            // -------------------------------------------------
            leftMenu = new JPanel();
            leftMenu.setLayout(new BoxLayout(leftMenu, BoxLayout.Y_AXIS));
            leftMenu.setPreferredSize(new Dimension(260, 0));
            leftMenu.setBackground(new Color(245, 247, 250));
            leftMenu.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

            ModernButton regBtn        = new ModernButton("Register a Vehicle");
            ModernButton lookupBtn     = new ModernButton("Lookup by License");
            ModernButton travelBtn     = new ModernButton("Travel Simulation");
            ModernButton listBtn       = new ModernButton("Show All Vehicles");
            ModernButton heatmapBtn    = new ModernButton("Live Traffic Heatmap");
            ModernButton compareBtn    = new ModernButton("Compare Routes");
            ModernButton insightsBtn   = new ModernButton("City Insights");
            ModernButton dispatchBtn   = new ModernButton("Emergency Dispatch"); // ✅ ADDED

            JButton[] buttons = {
                    regBtn, lookupBtn, travelBtn,
                    listBtn, heatmapBtn, compareBtn,
                    insightsBtn, dispatchBtn // ✅ ADDED
            };

            Dimension bsize = new Dimension(220, 45);
            for (JButton b : buttons) {
                b.setMaximumSize(bsize);
                b.setPreferredSize(bsize);
                b.setBackground(new Color(0, 123, 255));
                b.setForeground(Color.WHITE);
                b.setFont(new Font("Segoe UI", Font.BOLD, 14));
                b.setFocusPainted(false);
                b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
                leftMenu.add(b);
                leftMenu.add(Box.createRigidArea(new Dimension(0, 12)));
            }

            frame.add(leftMenu, BorderLayout.WEST);

            // -------------------------------------------------
            // Center Container
            // -------------------------------------------------
            centerView = new JPanel(new BorderLayout());
            frame.add(centerView, BorderLayout.CENTER);

            // -------------------------------------------------
            // SINGLE MAP INSTANCE
            // -------------------------------------------------
            map = new UrbanFlowPanel();

            setCenterView(new WelcomePanel());

            // -------------------------------------------------
            // BUTTON ACTIONS
            // -------------------------------------------------

            regBtn.addActionListener(e ->
                    setCenterView(new RegisterVehiclePanel(store))
            );

            lookupBtn.addActionListener(e ->
                    setCenterView(new LookupLicensePanel(store))
            );

            listBtn.addActionListener(e ->
                    setCenterView(new VehiclesListPanel(store))
            );

            // 🚗 Travel Simulation
            travelBtn.addActionListener(e -> {
                map.prepareCleanTravelView();
                setCenterView(new TravelSimulationView(map));
            });

            // 🔥 Live Heatmap
            heatmapBtn.addActionListener(e -> {
                map.prepareCleanHeatmapView();
                map.startLiveHeatmap();

                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                wrapper.add(map, BorderLayout.CENTER);

                setCenterView(wrapper);
            });

            // 🔁 Compare Routes
            compareBtn.addActionListener(e -> {
                map.prepareCompareRoutesView();
                setCenterView(new CompareRoutesView(map));
            });

            // 🧠 City Insights
            insightsBtn.addActionListener(e -> {

                map.clearRoutes();
                map.setHeatmapEnabled(false);
                map.setCityInsightsMode();

                CityInsightsView insights = new CityInsightsView(map);
                map.setCityInsightsView(insights);

                JPanel layout = new JPanel(new BorderLayout());
                layout.add(insights, BorderLayout.WEST);
                layout.add(map, BorderLayout.CENTER);

                setCenterView(layout);
            });

            // 🚑 Emergency Dispatch (Priority Queue)
            dispatchBtn.addActionListener(e ->
                    setCenterView(new EmergencyDispatchView())
            );

            // -------------------------------------------------
            // Finalize
            // -------------------------------------------------
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // -------------------------------------------------
    // MAIN ENTRY POINT
    // -------------------------------------------------
    public static void main(String[] args) {
        launch();
    }
}
