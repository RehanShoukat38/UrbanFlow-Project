// -------------------------------------------------------------
// UrbanFlowPanel (END-TO-END REFACTOR • PART 1)
// -------------------------------------------------------------
// • Clean mode-based architecture
// • Travel Simulation / Compare Routes / City Insights isolation
// • Zero regression design
// • Industry-grade structure
// -------------------------------------------------------------

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class UrbanFlowPanel extends JPanel {

    // =============================================================
    // 1. VIEW MODES (CORE REQUIREMENT)
    // =============================================================

    public enum ViewMode {
        DEFAULT,            // Full visualization (heatmap + routes)
        TRAVEL_SIMULATION,  // ONLY graph + best route
        COMPARE_ROUTES,     // Multiple routes (red/green)
        CITY_INSIGHTS       // Analytics mode
    }

    private ViewMode currentMode = ViewMode.DEFAULT;

    // =============================================================
    // 2. DATA MODELS
    // =============================================================

    public static class NodeView {
        public final String name;
        public final double x, y;
        public int screenX, screenY;

        public NodeView(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }
    private Timer heatmapTimer;
private boolean heatmapActive = false;
public void startLiveHeatmap() {

    currentMode = ViewMode.DEFAULT;   // 🔥 THIS IS THE KEY
    heatmapImage = null;
    heatmapActive = true;
    showHeatmap = true;

    if (heatmapTimer != null) {
        heatmapTimer.stop();
    }

    heatmapTimer = new Timer(80, e -> {
        updateTrafficPulse();
        repaint();
    });

    heatmapTimer.start();

    repaint();
}

public void stopLiveHeatmap() {

    heatmapActive = false;
    showHeatmap = false;

    if (heatmapTimer != null) {
        heatmapTimer.stop();
    }

    heatmapImage = null;
    repaint();
}


    public static class EdgeView {
        public final String from;
        public final String to;
        public final String motorway;
        public final double length;
        public final double speed;
        public final double toll;

        public EdgeView(String from, String to,
                        double length, String motorway,
                        double speed, double toll) {
            this.from = from;
            this.to = to;
            this.length = length;
            this.motorway = motorway;
            this.speed = speed;
            this.toll = toll;
        }
         public boolean connects(String cityKey) {
        return from.equals(cityKey) || to.equals(cityKey);
    }
    }

    public static class RouteResult {
        public final String mode;
        public final List<String> path;
        public final double length;
        public final double timeHours;
        public final double cost;

        public RouteResult(String mode, List<String> path,
                           double length, double timeHours, double cost) {
            this.mode = mode;
            this.path = path;
            this.length = length;
            this.timeHours = timeHours;
            this.cost = cost;
        }
    }
    // ================= HEATMAP MODULE =================
private final HeatmapLayer heatmapLayer = new HeatmapLayer();
private boolean heatmapEnabled = false;

    // =============================================================
// TRAVEL SIMULATION MODE (CLEAN ENTRY)
// =============================================================

public void startTravelSimulation(String source, String destination) {

    if (source == null || destination == null) return;
    if (source.equals(destination)) return;

    prepareCleanTravelView();   // 🔥 THIS FIXES ORANGE CIRCLES + ZOOM
    animateRoute(source, destination);
}
// =============================================================
// HEATMAP MODE PREPARATION
// =============================================================
public void prepareCleanHeatmapView() {

    // Stop all route animations
    stopRouteAnimation();

    // Clear alternative routes (compare mode)
    clearAlternativeRoutes();

    // Enable heatmap
    enableHeatmap(true);

    // Force redraw
    repaint();
}

// =============================================================
// HARD RESET VIEW FOR TRAVEL SIMULATION
// =============================================================
public void prepareCleanTravelView() {

    stopLiveHeatmap();        // 🔴 IMPORTANT
    stopRouteAnimation();

    currentMode = ViewMode.TRAVEL_SIMULATION;
    showHeatmap = false;

    zoom = 1.0;
    translateX = 60;
    translateY = 40;

    repaint();
}



    // =============================================================
    // 4. GRAPH DATA
    // =============================================================

    private final Map<String, NodeView> nodes = new LinkedHashMap<>();
    private final List<EdgeView> edges = new ArrayList<>();

    // =============================================================
    // 5. VIEW TRANSFORM (ZOOM & PAN)
    // =============================================================

    private double zoom = 1.0;
    private double translateX = 60;
    private double translateY = 40;
    private Point lastDrag;

    // =============================================================
    // 6. ROUTE ANIMATION STATE
    // =============================================================

    private List<Point2D> activeRoute = null;
    private Timer routeTimer = null;
    private double routePhase = 0.0;
    private boolean routeAnimating = false;

    // =============================================================
    // 7. ALTERNATIVE ROUTES (COMPARE MODE)
    // =============================================================

    private final List<List<Point2D>> alternativeRoutes = new ArrayList<>();

    // =============================================================
    // 8. TRAFFIC / HEATMAP STATE
    // =============================================================

    private boolean showHeatmap = true;
    private double[] trafficIntensity;
    private double heatPulse = 0.0;
    private double globalTrafficLevel = 1.0;

    private BufferedImage heatmapImage = null;
    private final Random random = new Random(1133);

    // =============================================================
    // 9. SELECTION STATE
    // =============================================================

    private String selectedNode = null;

    // =============================================================
    // 10. CONSTRUCTOR
    // =============================================================

    public UrbanFlowPanel() {

        setPreferredSize(new Dimension(1200, 800));
        setBackground(new Color(18, 24, 32));

        buildGraph();
        computeScreenCoordinates();
        initializeTraffic();

        setupInteractions();
        heatmapLayer.initialize(nodes.size());
        new Timer(40, e -> {
    heatmapLayer.updateTraffic();
    repaint();
}).start();


    }
public void highlightCity(String city, boolean insightsMode) {
    selectedNode = city;
    repaint();
}

public void clearRoutes() {
    activeRoute = null;
    alternativeRoutes.clear();
    routeAnimating = false;
    repaint();
}

public void setHeatmapEnabled(boolean enabled) {
    this.heatmapEnabled = enabled;
    repaint();
}

public void prepareCompareRoutesView() {
    stopRouteAnimation();
    alternativeRoutes.clear();
    activeRoute = null;
    showHeatmap = false;
    currentMode = ViewMode.COMPARE_ROUTES;
    repaint();
}
public void compareRoutes(String src, String dst) {

    stopRouteAnimation();        // no animation
    alternativeRoutes.clear();   // reset previous routes
    currentMode = ViewMode.COMPARE_ROUTES;
    showHeatmap = false;

    // 1️⃣ Shortest route (distance) → GREEN
    RouteResult shortest = findShortestPath(src, dst, "distance");

    // 2️⃣ Other routes → RED
    RouteResult fastest  = findShortestPath(src, dst, "time");
    RouteResult cheapest = findShortestPath(src, dst, "cost");

    if (shortest != null) {
        alternativeRoutes.add(convertPathToPoints(shortest.path));
    }

    if (fastest != null && !fastest.path.equals(shortest.path)) {
        alternativeRoutes.add(convertPathToPoints(fastest.path));
    }

    if (cheapest != null && !cheapest.path.equals(shortest.path)) {
        alternativeRoutes.add(convertPathToPoints(cheapest.path));
    }

    repaint();
}


    // =============================================================
    // 11. GRAPH CREATION
    // =============================================================

    private void buildGraph() {

        nodes.clear();
        edges.clear();

        // ---- Nodes ----
        // ==================== NODES (52 — SPREAD OUT) ====================

// ================= CORE HUBS =================
addNode("ISL", 600, 300);
addNode("LHR", 750, 320);
addNode("FSD", 700, 380);
addNode("MLT", 650, 440);
addNode("KHI", 820, 520);
// ================= SECONDARY HUBS =================
addNode("RWP", 560, 280);
addNode("GUJ", 720, 290);
addNode("GRW", 780, 280);
addNode("SKT", 830, 300);

addNode("DGK", 600, 500);
addNode("SUK", 720, 480);
addNode("HYD", 780, 500);

addNode("PSH", 480, 260);
addNode("QTA", 420, 480);
addNode("GIL", 360, 180);
addNode("SKD", 420, 200);
addNode("HUN", 480, 220);
addNode("DIR", 380, 260);
addNode("SWB", 420, 260);
addNode("BNU", 460, 280);
addNode("KHT", 400, 300);
addNode("MAN", 500, 300);
addNode("ATK", 520, 320);
addNode("JLM", 620, 260);
addNode("MBD", 660, 260);
addNode("SAH", 700, 340);
addNode("KSR", 740, 350);
addNode("JHG", 780, 360);
addNode("OKA", 820, 370);
addNode("SWL", 860, 380);
addNode("KAS", 900, 390);
addNode("VEH", 620, 460);
addNode("RYK", 700, 460);
addNode("BWP", 740, 460);
addNode("ZHB", 380, 420);
addNode("NWS", 420, 400);
addNode("TUR", 360, 520);
addNode("KUZ", 320, 540);
addNode("BAD", 680, 520);
addNode("LKI", 640, 540);
addNode("KOT", 700, 540);
addNode("UMK", 760, 560);
addNode("MPS", 800, 560);
addNode("TBT", 860, 560);

        // ---- Edges ----
        // ==================== NEW EDGES (EXPANSION) ====================

// ================= HUB SPINE =================
// === CORE BACKBONE (High-Speed Mesh) ===
// === CORE BACKBONE (High-Speed Mesh) ===
addEdge("ISL", "LHR", 50, "CORE", 110, 0);
addEdge("LHR", "FSD", 40, "CORE", 110, 0);
addEdge("FSD", "MLT", 45, "CORE", 110, 0);
addEdge("MLT", "SUK", 60, "CORE", 110, 0);
addEdge("SUK", "HYD", 50, "CORE", 110, 0);
addEdge("HYD", "KHI", 40, "CORE", 110, 0);

// === NORTHERN & KPK CLUSTER (Green Zone) ===
addEdge("GIL", "SKD", 30, "LOCAL", 40, 0);
addEdge("SKD", "HUN", 30, "LOCAL", 40, 0);
addEdge("HUN", "PSH", 50, "LOCAL", 40, 0);
addEdge("GIL", "PSH", 60, "LOCAL", 40, 0); 
addEdge("DIR", "SWB", 25, "LOCAL", 40, 0);
addEdge("SWB", "PSH", 25, "LOCAL", 40, 0);
addEdge("BNU", "PSH", 30, "LOCAL", 40, 0); // Connected BNU
addEdge("KHT", "BNU", 25, "LOCAL", 40, 0); // Connected KHT
addEdge("KHT", "PSH", 35, "LOCAL", 40, 0); // Mesh for KHT
addEdge("PSH", "ATK", 30, "LOCAL", 50, 0);
addEdge("PSH", "ISL", 45, "M1", 100, 0);  
addEdge("MAN", "PSH", 40, "LOCAL", 40, 0); // Connected MAN
addEdge("ATK", "ISL", 25, "LOCAL", 50, 0);

// === UPPER & CENTRAL PUNJAB (Orange Zone) ===
addEdge("RWP", "ISL", 15, "LOCAL", 40, 0);
addEdge("JLM", "RWP", 35, "LOCAL", 40, 0); // Connected JLM
addEdge("MBD", "JLM", 25, "LOCAL", 40, 0); // Connected MBD
addEdge("MBD", "GUJ", 25, "LOCAL", 40, 0); // Mesh for MBD
addEdge("RWP", "LHR", 60, "M2", 110, 0);
addEdge("GUJ", "LHR", 25, "LOCAL", 50, 0);
addEdge("GRW", "LHR", 25, "LOCAL", 50, 0);
addEdge("GRW", "GUJ", 20, "LOCAL", 50, 0); 
addEdge("SKT", "GRW", 20, "LOCAL", 50, 0);
addEdge("SKT", "LHR", 40, "LOCAL", 50, 0); 
addEdge("FSD", "LHR", 40, "M3", 110, 0);
addEdge("SAH", "LHR", 35, "LOCAL", 40, 0); // Connected SAH
addEdge("JHG", "FSD", 30, "LOCAL", 40, 0); // Connected JHG
addEdge("OKA", "FSD", 30, "LOCAL", 40, 0); // Connected OKA
addEdge("SWL", "OKA", 25, "LOCAL", 40, 0); // Connected SWL
addEdge("KAS", "SWL", 25, "LOCAL", 40, 0); // Connected KAS
addEdge("KAS", "LHR", 40, "LOCAL", 40, 0); // Mesh for KAS

// === SOUTH PUNJAB & SINDH (Yellow/Blue Transition) ===
addEdge("MLT", "VEH", 30, "LOCAL", 50, 0);
addEdge("MLT", "BWP", 30, "LOCAL", 50, 0);
addEdge("MLT", "RYK", 45, "LOCAL", 50, 0);
addEdge("BWP", "RYK", 30, "LOCAL", 50, 0); 
addEdge("DGK", "MLT", 35, "LOCAL", 60, 0);
addEdge("DGK", "SUK", 60, "N55", 80, 0);
addEdge("SUK", "LKI", 25, "LOCAL", 40, 0);
addEdge("SUK", "KOT", 25, "LOCAL", 40, 0);
addEdge("SUK", "BAD", 30, "LOCAL", 40, 0);
addEdge("LKI", "KOT", 20, "LOCAL", 40, 0); 
addEdge("HYD", "UMK", 30, "LOCAL", 40, 0);
addEdge("HYD", "MPS", 30, "LOCAL", 40, 0);
addEdge("KHI", "MPS", 35, "LOCAL", 50, 0);
addEdge("KHI", "TBT", 40, "LOCAL", 50, 0);

// === BALOCHISTAN CLUSTER (Tan Zone) ===
addEdge("QTA", "DGK", 70, "N70", 70, 0);
addEdge("QTA", "ZHB", 35, "LOCAL", 40, 0);
addEdge("QTA", "NWS", 35, "LOCAL", 40, 0);
addEdge("ZHB", "NWS", 25, "LOCAL", 40, 0); 
addEdge("TUR", "QTA", 55, "LOCAL", 40, 0);
addEdge("TUR", "KUZ", 30, "LOCAL", 40, 0);
addEdge("KUZ", "KHI", 60, "N25", 80, 0);





        // ---- Make graph undirected ----
        List<EdgeView> copy = new ArrayList<>(edges);
        for (EdgeView e : copy) {
            if (!hasEdge(e.to, e.from)) {
                addEdge(e.to, e.from,
                        e.length, e.motorway, e.speed, e.toll);
            }
        }
    }

    private void addNode(String name, double x, double y) {
        nodes.put(name, new NodeView(name, x, y));
    }

    private void addEdge(String from, String to,
                         double length, String motorway,
                         double speed, double toll) {
        edges.add(new EdgeView(from, to, length, motorway, speed, toll));
    }

    private boolean hasEdge(String a, String b) {
        for (EdgeView e : edges) {
            if (e.from.equals(a) && e.to.equals(b)) return true;
        }
        return false;
    }

    // =============================================================
    // 12. SCREEN COORDINATE COMPUTATION
    // =============================================================

    private void computeScreenCoordinates() {

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (NodeView n : nodes.values()) {
            minX = Math.min(minX, n.x);
            minY = Math.min(minY, n.y);
        }

        double scale = 2.0;

        for (NodeView n : nodes.values()) {
            n.screenX = (int) ((n.x - minX) * scale + 80);
            n.screenY = (int) ((n.y - minY) * scale + 80);
        }

        heatmapImage = null;
    }

    // =============================================================
    // 13. TRAFFIC INITIALIZATION
    // =============================================================

    private void initializeTraffic() {
        trafficIntensity = new double[nodes.size()];
        for (int i = 0; i < trafficIntensity.length; i++) {
            trafficIntensity[i] = random.nextDouble();
        }
    }
    private CityInsight computeCityInsight(String city) {

    // -----------------------------
    // 1. Connectivity (degree)
    // -----------------------------
    int connectivity = 0;
    for (EdgeView e : edges) {
        if (e.from.equals(city) || e.to.equals(city)) {
            connectivity++;
        }
    }

    // -----------------------------
    // 2. Centrality (normalized 0–1)
    // -----------------------------
    double centrality = Math.min(1.0, connectivity / 8.0);

    // -----------------------------
    // 3. Traffic pressure (0–100)
    // -----------------------------
    int trafficPressure = (int) (30 + centrality * 70);

    // -----------------------------
    // 4. Hub classification
    // -----------------------------
    String hubType;
    if (centrality >= 0.8) {
        hubType = "CRITICAL HUB";
    } else if (centrality >= 0.5) {
        hubType = "MAJOR HUB";
    } else {
        hubType = "MINOR HUB";
    }

    // -----------------------------
    // 5. Return analytics model
    // -----------------------------
    return new CityInsight(
            city,
            connectivity,
            centrality,
            trafficPressure,
            hubType
    );
}

public int getCityDegree(String cityKey) {
    int degree = 0;

    for (EdgeView edge : edges) {
        if (edge.connects(cityKey)) {
            degree++;
        }
    }

    return degree;
}

// =============================================================
// 14. USER INTERACTIONS (ZOOM, PAN, CLICK)
// =============================================================

private void setupInteractions() {

    // -----------------------------
    // Mouse press & click
    // -----------------------------
    addMouseListener(new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            lastDrag = e.getPoint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            selectedNode = null;
            repaint();
        }

@Override
public void mouseClicked(MouseEvent e) {

    if (currentMode != ViewMode.CITY_INSIGHTS) {
        return;
    }

    String node = pickNodeAt(e.getX(), e.getY());
    if (node != null) {
        selectedNode = node;

        // 🔥 THIS LINE IS THE KEY
        if (cityInsightsView != null) {
            cityInsightsView.onCityClicked(node);
        }

        repaint();
    }
}



    });

    // -----------------------------
    // Mouse drag (pan)
    // -----------------------------
    addMouseMotionListener(new MouseMotionAdapter() {

        @Override
        public void mouseDragged(MouseEvent e) {

            translateX += e.getX() - lastDrag.x;
            translateY += e.getY() - lastDrag.y;

            lastDrag = e.getPoint();
            heatmapImage = null;
            repaint();
        }
    });

    // -----------------------------
    // Mouse wheel (zoom)
    // -----------------------------
    addMouseWheelListener(e -> {

        double factor = Math.pow(1.12, -e.getPreciseWheelRotation());
        zoom *= factor;

        zoom = Math.max(0.35, Math.min(3.5, zoom));
        heatmapImage = null;
        repaint();
    });
}

// =============================================================
// 15. NODE PICKING (SCREEN → WORLD)
// =============================================================

private String pickNodeAt(int mouseX, int mouseY) {

    // Convert screen → world coordinates
    double worldX = (mouseX - translateX) / zoom;
    double worldY = (mouseY - translateY) / zoom;

    double radius = 14.0 / zoom;

    for (NodeView n : nodes.values()) {

        double dx = n.screenX - worldX;
        double dy = n.screenY - worldY;

        if (dx * dx + dy * dy <= radius * radius) {
            return n.name;
        }
    }
    return null;
}





// =============================================================
// 18. MODE CONTROL API (CALLED FROM UI BUTTONS)
// =============================================================

public void setDefaultMode() {
    currentMode = ViewMode.DEFAULT;
    showHeatmap = true;
    selectedNode = null;
    repaint();
}

public void setTravelSimulationMode() {
    currentMode = ViewMode.TRAVEL_SIMULATION;
    showHeatmap = false;
    alternativeRoutes.clear();
    selectedNode = null;
    repaint();
}

public void setCompareRoutesMode() {
    currentMode = ViewMode.COMPARE_ROUTES;
    showHeatmap = false;
    selectedNode = null;
    repaint();
}

public void setCityInsightsMode() {
    currentMode = ViewMode.CITY_INSIGHTS;

    // 🔥 TURN OFF HEATMAP COMPLETELY
    showHeatmap = false;
    heatmapEnabled = false;

    if (heatmapTimer != null) {
        heatmapTimer.stop();
    }

    heatmapImage = null;
    repaint();
}



// =============================================================
// 19. GLOBAL TRAFFIC CONTROL (OPTIONAL SLIDER SUPPORT)
// =============================================================

public void setGlobalTrafficLevel(double value) {
    globalTrafficLevel = Math.max(0.1, Math.min(5.0, value));
    heatmapImage = null;
    repaint();
}
// =============================================================
// 20. PAINT ENTRY POINT
// =============================================================

@Override
protected void paintComponent(Graphics g0) {
    super.paintComponent(g0);

    Graphics2D g = (Graphics2D) g0.create();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    // -----------------------------
    // Background
    // -----------------------------
    drawBackground(g);

    // -----------------------------
    // World transform
    // -----------------------------
    g.translate(translateX, translateY);
    g.scale(zoom, zoom);

    // -----------------------------
    // -----------------------------
// Graph rendering
// -----------------------------
drawEdges(g);

// ⭐⭐⭐ ADD THIS BLOCK ⭐⭐⭐
if (currentMode == ViewMode.COMPARE_ROUTES && !alternativeRoutes.isEmpty()) {
    drawAlternativeRoutes(g);
}

// Draw nodes on top
drawNodes(g);

    // -----------------------------
    // Active route animation
    // -----------------------------
    if (routeAnimating && activeRoute != null) {
        drawAnimatedRoute(g, activeRoute);
    }

    g.dispose();

    // -----------------------------
// Heatmap overlay (SCREEN SPACE)
// -----------------------------
if (showHeatmap && currentMode == ViewMode.DEFAULT) {
    drawHeatmapOverlay((Graphics2D) g0);
}


    // -----------------------------
    // Selected node highlight
    // -----------------------------
    if (selectedNode != null) {
        NodeView n = nodes.get(selectedNode);
        if (n != null) {
            drawSelectedNodeHighlight((Graphics2D) g0, n);
        }
    }
    if (heatmapEnabled) {
    heatmapLayer.render(
        (Graphics2D) g0,
        getWidth(),
        getHeight(),
        nodes,
        zoom,
        translateX,
        translateY
    );
}

}
private CityInsightsView cityInsightsView;

public void setCityInsightsView(CityInsightsView view) {
    this.cityInsightsView = view;
}

// =============================================================
// 21. BACKGROUND RENDERING
// =============================================================

private void drawBackground(Graphics2D g) {

    GradientPaint bg = new GradientPaint(
            0, 0, new Color(12, 18, 26),
            0, getHeight(), new Color(22, 30, 42)
    );

    g.setPaint(bg);
    g.fillRect(0, 0, getWidth(), getHeight());
}

// =============================================================
// 22. EDGE RENDERING
// =============================================================

private void drawEdges(Graphics2D g) {

    Stroke localStroke = new BasicStroke(4f);
    Stroke highwayGlow = new BasicStroke(10f);
    Stroke highwayStroke = new BasicStroke(6f);

    for (EdgeView e : edges) {

        NodeView a = nodes.get(e.from);
        NodeView b = nodes.get(e.to);

        boolean highway =
                e.motorway.startsWith("M") ||
                e.motorway.startsWith("N");

        if (highway) {
            // Glow
            g.setStroke(highwayGlow);
            g.setColor(new Color(60, 200, 255, 70));
            g.drawLine(a.screenX, a.screenY, b.screenX, b.screenY);

            // Main
            g.setStroke(highwayStroke);
            g.setColor(new Color(60, 200, 255));
            g.drawLine(a.screenX, a.screenY, b.screenX, b.screenY);

            // Dashed center
            float[] dash = {6f, 8f};
            g.setStroke(new BasicStroke(
                    2f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL,
                    1f,
                    dash,
                    0f
            ));
            g.setColor(Color.WHITE);
            g.drawLine(a.screenX, a.screenY, b.screenX, b.screenY);

        } else {
            g.setStroke(localStroke);
            g.setColor(new Color(130, 145, 170));
            g.drawLine(a.screenX, a.screenY, b.screenX, b.screenY);
        }
    }
}

// =============================================================
// 23. NODE RENDERING
// =============================================================

private void drawNodes(Graphics2D g) {

    for (NodeView n : nodes.values()) {

        int cx = n.screenX;
        int cy = n.screenY;

        // Halo
        RadialGradientPaint halo = new RadialGradientPaint(
                new Point2D.Double(cx, cy),
                22,
                new float[]{0f, 1f},
                new Color[]{
                        new Color(60, 200, 255, 100),
                        new Color(60, 200, 255, 0)
                }
        );

        Paint old = g.getPaint();
        g.setPaint(halo);
        g.fillOval(cx - 22, cy - 22, 44, 44);
        g.setPaint(old);

        // Core
        g.setColor(new Color(20, 26, 32));
        g.fillOval(cx - 9, cy - 9, 18, 18);

        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(60, 200, 255));
        g.drawOval(cx - 11, cy - 11, 22, 22);

        // Label
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.setColor(Color.WHITE);
        g.drawString(n.name, cx + 12, cy + 5);
    }
}

// =============================================================
// 24. SELECTED NODE HIGHLIGHT (SCREEN SPACE)
// =============================================================

private void drawSelectedNodeHighlight(Graphics2D g, NodeView n) {

    int sx = (int) (n.screenX * zoom + translateX);
    int sy = (int) (n.screenY * zoom + translateY);

    g.setColor(new Color(255, 200, 80, 160));
    g.fillOval(sx - 28, sy - 28, 56, 56);
}

// =============================================================
// 25. HEATMAP OVERLAY
// =============================================================

private void drawHeatmapOverlay(Graphics2D g) {

    if (heatmapImage == null) {
        rebuildHeatmap();
    }

    Composite prev = g.getComposite();
    g.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.42f));
    g.drawImage(heatmapImage, 0, 0, null);
    g.setComposite(prev);
}

// =============================================================
// 26. HEATMAP BUILDING
// =============================================================

private void rebuildHeatmap() {

    heatmapImage = new BufferedImage(
            getWidth(),
            getHeight(),
            BufferedImage.TYPE_INT_ARGB
    );

    Graphics2D g = heatmapImage.createGraphics();

    int i = 0;
    for (NodeView n : nodes.values()) {

        double intensity =
                trafficIntensity[i++] *
                (1 + 0.3 * Math.sin(heatPulse)) *
                globalTrafficLevel;

        int cx = (int) (n.screenX * zoom + translateX);
        int cy = (int) (n.screenY * zoom + translateY);

        drawHeatCircle(g, cx, cy,
                (int) (140 * zoom), intensity);
    }

    g.dispose();
}

// =============================================================
// 27. HEAT CIRCLE
// =============================================================

private void drawHeatCircle(
        Graphics2D g,
        int cx, int cy,
        int radius,
        double intensity) {

    int steps = 10;

    for (int i = steps; i >= 1; i--) {

        float t = i / (float) steps;
        int r = (int) (radius * t);
        int alpha = (int) (200 * intensity * Math.pow(t, 1.6));

        g.setColor(new Color(255, 80, 40, Math.max(6, alpha)));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
    }
}
// =============================================================
// BACKWARD COMPATIBILITY: highlightNode (OLD UI SUPPORT)
// =============================================================

public void highlightNode(String node) {
    if (node == null) return;

    // Switch to city insight mode so highlight makes sense
    currentMode = ViewMode.CITY_INSIGHTS;

    selectedNode = node;
    repaint();
}

// =============================================================
// 28. TRAVEL SIMULATION ENTRY POINT (BEST ROUTE ONLY)
// =============================================================

public void animateRoute(String source, String destination) {

    setTravelSimulationMode();

    RouteResult result = findShortestPath(
            source,
            destination,
            "distance"
    );

    if (result == null) return;

    activeRoute = convertPathToPoints(result.path);
    routePhase = 0.0;
    routeAnimating = true;

    if (routeTimer != null) routeTimer.stop();

    routeTimer = new Timer(35, e -> {
        routePhase += 0.012;
        if (routePhase > 1.0) routePhase -= 1.0;
        repaint();
    });

    routeTimer.start();
}

// =============================================================
// 29. STOP ROUTE ANIMATION
// =============================================================

public void stopRouteAnimation() {
    routeAnimating = false;
    activeRoute = null;
    if (routeTimer != null) routeTimer.stop();
    repaint();
}

// =============================================================
// 30. COMPARE ROUTES API (RED / GREEN ROUTES)
// =============================================================

public void setAlternativeRoutes(List<RouteResult> results) {

    setCompareRoutesMode();
    alternativeRoutes.clear();

    for (RouteResult r : results) {
        alternativeRoutes.add(
                convertPathToPoints(r.path)
        );
    }

    repaint();
}

public void clearAlternativeRoutes() {
    alternativeRoutes.clear();
    repaint();
}

// =============================================================
// 31. PATHFINDING (DIJKSTRA ENGINE)
// =============================================================

public RouteResult findShortestPath(
        String src,
        String dst,
        String mode) {

    Map<String, List<EdgeView>> adj = new HashMap<>();
    for (String n : nodes.keySet()) {
        adj.put(n, new ArrayList<>());
    }

    for (EdgeView e : edges) {
        adj.get(e.from).add(e);
    }

    Map<String, Double> dist = new HashMap<>();
    Map<String, String> parent = new HashMap<>();

    for (String n : nodes.keySet()) {
        dist.put(n, Double.POSITIVE_INFINITY);
    }

    dist.put(src, 0.0);

    PriorityQueue<String> pq =
            new PriorityQueue<>(Comparator.comparingDouble(dist::get));
    pq.add(src);

    while (!pq.isEmpty()) {

        String u = pq.poll();
        if (u.equals(dst)) break;

        for (EdgeView e : adj.get(u)) {

            double weight;
            switch (mode) {
                case "time":
                    weight = e.length / e.speed;
                    break;
                case "cost":
                    weight = e.toll;
                    break;
                default:
                    weight = e.length;
            }

            double nd = dist.get(u) + weight;
            if (nd < dist.get(e.to)) {
                dist.put(e.to, nd);
                parent.put(e.to, u);
                pq.add(e.to);
            }
        }
    }

    if (!parent.containsKey(dst) && !src.equals(dst)) {
        return null;
    }

    List<String> path = new ArrayList<>();
    String cur = dst;
    path.add(cur);

    while (!cur.equals(src)) {
        cur = parent.get(cur);
        path.add(cur);
    }

    Collections.reverse(path);

    double totalLen = 0, totalTime = 0, totalCost = 0;

    for (int i = 0; i < path.size() - 1; i++) {
        EdgeView e = findEdge(path.get(i), path.get(i + 1));
        totalLen += e.length;
        totalTime += e.length / e.speed;
        totalCost += e.toll;
    }

    return new RouteResult(
            mode,
            path,
            totalLen,
            totalTime,
            totalCost
    );
}

private EdgeView findEdge(String a, String b) {
    for (EdgeView e : edges) {
        if (e.from.equals(a) && e.to.equals(b)) return e;
    }
    return null;
}

// =============================================================
// 32. ROUTE → SCREEN POINT CONVERSION
// =============================================================

private List<Point2D> convertPathToPoints(List<String> path) {

    List<Point2D> pts = new ArrayList<>();
    for (String n : path) {
        NodeView v = nodes.get(n);
        pts.add(new Point2D.Double(v.screenX, v.screenY));
    }
    return pts;
}

// =============================================================
// 33. ROUTE DRAWING (NEON STYLE)
// =============================================================

private void drawAnimatedRoute(Graphics2D g, List<Point2D> pts) {

    if (pts.size() < 2) return;

    // Outer glow
    g.setStroke(new BasicStroke(12f));
    g.setColor(new Color(60, 200, 255, 60));
    drawPolyline(g, pts);

    // Main line
    g.setStroke(new BasicStroke(6f));
    g.setColor(new Color(60, 200, 255));
    drawPolyline(g, pts);

    // Inner highlight
    g.setStroke(new BasicStroke(2f));
    g.setColor(Color.WHITE);
    drawPolyline(g, pts);

    drawFlowDots(g, pts);
    drawDirectionArrow(g, pts);
}

// =============================================================
// 34. POLYLINE UTILITY
// =============================================================

private void drawPolyline(Graphics2D g, List<Point2D> pts) {

    Path2D path = new Path2D.Double();
    path.moveTo(pts.get(0).getX(), pts.get(0).getY());

    for (int i = 1; i < pts.size(); i++) {
        path.lineTo(
                pts.get(i).getX(),
                pts.get(i).getY()
        );
    }

    g.draw(path);
}

// =============================================================
// 35. FLOW DOTS (MOVING PARTICLES)
// =============================================================

private void drawFlowDots(Graphics2D g, List<Point2D> pts) {

    int count = 12;

    for (int i = 0; i < count; i++) {

        double t = ((i / (double) count) + routePhase) % 1.0;
        double idx = t * (pts.size() - 1);

        int a = (int) Math.floor(idx);
        int b = Math.min(pts.size() - 1, a + 1);

        double d = idx - a;

        double x = pts.get(a).getX() * (1 - d)
                 + pts.get(b).getX() * d;
        double y = pts.get(a).getY() * (1 - d)
                 + pts.get(b).getY() * d;

        g.setColor(Color.WHITE);
        g.fillOval((int) x - 4, (int) y - 4, 8, 8);
    }
}

// =============================================================
// 36. DIRECTION ARROW
// =============================================================

private void drawDirectionArrow(Graphics2D g, List<Point2D> pts) {

    double t = routePhase % 1.0;
    double idx = t * (pts.size() - 1);

    int a = (int) Math.floor(idx);
    int b = Math.min(pts.size() - 1, a + 1);

    double d = idx - a;

    double x = pts.get(a).getX() * (1 - d)
             + pts.get(b).getX() * d;
    double y = pts.get(a).getY() * (1 - d)
             + pts.get(b).getY() * d;

    double dx = pts.get(b).getX() - pts.get(a).getX();
    double dy = pts.get(b).getY() - pts.get(a).getY();

    double angle = Math.atan2(dy, dx);

    Graphics2D g2 = (Graphics2D) g.create();
    g2.translate(x, y);
    g2.rotate(angle);

    g2.setColor(new Color(60, 200, 255, 180));
    Polygon arrow = new Polygon(
            new int[]{0, -12, -12},
            new int[]{0, -6, 6},
            3
    );
    g2.fill(arrow);

    g2.dispose();
}
// =============================================================
// 37. ALTERNATIVE ROUTE COLORS (COMPARE MODE)
// =============================================================

private final List<Color> alternativeColors = Arrays.asList(
        new Color(255, 80, 80),    // Red
        new Color(80, 220, 120),   // Green
        new Color(255, 160, 60)    // Orange
);

// =============================================================
// 38. DRAW ALTERNATIVE ROUTES (COMPARE MODE)
// =============================================================

private void drawAlternativeRoutes(Graphics2D g) {

    if (alternativeRoutes.isEmpty()) return;

    for (int i = 0; i < alternativeRoutes.size(); i++) {

        List<Point2D> pts = alternativeRoutes.get(i);
        if (pts == null || pts.size() < 2) continue;

        Color c = alternativeColors.get(
                i % alternativeColors.size()
        );

        // Glow
        g.setStroke(new BasicStroke(14f));
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 60));
        drawPolyline(g, pts);

        // Main
        g.setStroke(new BasicStroke(6f));
        g.setColor(c);
        drawPolyline(g, pts);

        // Highlight
        g.setStroke(new BasicStroke(2f));
        g.setColor(Color.WHITE);
        drawPolyline(g, pts);
    }
}

// =============================================================
// 39. INTEGRATE ALTERNATIVE ROUTES INTO PAINT PIPELINE
// =============================================================
// 🔴 ADD THIS LINE inside paintComponent()
// 🔴 RIGHT AFTER drawEdges(g) and BEFORE drawNodes(g)

/*
if (currentMode == ViewMode.COMPARE_ROUTES) {
    drawAlternativeRoutes(g);
}
*/

// (This comment is intentional — easy to explain in viva)

// =============================================================
// 40. ROUTE STATISTICS API (USED BY UI PANELS)
// =============================================================

public static class RouteStat {
    public final String mode;
    public final double length;
    public final double timeHours;
    public final double cost;

    public RouteStat(String mode,
                     double length,
                     double timeHours,
                     double cost) {
        this.mode = mode;
        this.length = length;
        this.timeHours = timeHours;
        this.cost = cost;
    }
}

private final List<RouteStat> alternativeStats = new ArrayList<>();

public void setAlternativeRouteResults(List<RouteResult> results) {

    alternativeRoutes.clear();
    alternativeStats.clear();

    setCompareRoutesMode();

    for (RouteResult r : results) {
        alternativeRoutes.add(
                convertPathToPoints(r.path)
        );
        alternativeStats.add(
                new RouteStat(
                        r.mode,
                        r.length,
                        r.timeHours,
                        r.cost
                )
        );
    }
    repaint();
}

public List<RouteStat> getAlternativeStats() {
    return Collections.unmodifiableList(alternativeStats);
}

// =============================================================
// 41. CLEANUP & RESET UTILITIES
// =============================================================

public void resetView() {

    stopRouteAnimation();
    alternativeRoutes.clear();
    alternativeStats.clear();
    selectedNode = null;
    currentMode = ViewMode.DEFAULT;
    showHeatmap = true;

    repaint();
}

// =============================================================
// 42. PUBLIC GRAPH ACCESS (SAFE)
// =============================================================

public Map<String, NodeView> getNodes() {
    return Collections.unmodifiableMap(nodes);
}

public List<EdgeView> getEdges() {
    return Collections.unmodifiableList(edges);
}

// =============================================================
// 43. SMALL PERFORMANCE POLISH
// =============================================================

private void updateTrafficPulse() {
    heatPulse += 0.035;
    if (heatPulse > Math.PI * 2) {
        heatPulse -= Math.PI * 2;
    }
    heatmapImage = null;
}

public void enableHeatmap(boolean enable) {
    this.heatmapEnabled = enable;
    repaint();
}
public void setTrafficLevel(double level) {
    heatmapLayer.setGlobalTrafficLevel(level);
    repaint();
}
public void updateTraffic() {
    heatmapLayer.updateTraffic();
    repaint();
}
public void setHeatmapAnalyticsMode(boolean analytics) {
    if (heatmapLayer != null) {
        heatmapLayer.setAdvancedMode(
            analytics
            ? HeatmapLayer.AdvancedMode.ANALYTICS_BINARY
            : HeatmapLayer.AdvancedMode.DEFAULT_GRADIENT
        );
        repaint();
    }
}


// (Optional timer — enable if you want animated traffic)
// new Timer(80, e -> updateTrafficPulse()).start();

// =============================================================
// 44. CLASS END
// =============================================================

}
