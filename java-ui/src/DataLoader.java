import java.util.*;

public class DataLoader {

    private Map<String, NodeView> nodes = new LinkedHashMap<>();
    private List<EdgeView> edges = new ArrayList<>();
    private List<VehicleView> vehicles = new ArrayList<>();

    private int offsetX = 0;
    private int offsetY = 0;

    public Map<String, NodeView> getNodes() { return nodes; }
    public List<EdgeView> getEdges() { return edges; }
    public List<VehicleView> getVehiclesForUI() { return vehicles; }

    public void loadAll() {
        nodes.clear();
        edges.clear();
        vehicles.clear();

        loadFixedCities();
        loadFixedRoads();
        computeScreenCoordinates();

        System.out.println("DATA LOADED: " + nodes.size() + " cities, " + edges.size() + " roads.");
    }

    private void loadFixedCities() {

        nodes.put("ISL", new NodeView("ISL", 100, 500));
        nodes.put("RWP", new NodeView("RWP", 120, 480));
        nodes.put("PSH", new NodeView("PSH", 80, 560));

        nodes.put("LHR", new NodeView("LHR", 200, 450));
        nodes.put("GRW", new NodeView("GRW", 180, 470));
        nodes.put("SKT", new NodeView("SKT", 210, 470));
        nodes.put("FSD", new NodeView("FSD", 210, 430));

        nodes.put("SWL", new NodeView("SWL", 230, 410));
        nodes.put("MLT", new NodeView("MLT", 240, 380));
        nodes.put("BWP", new NodeView("BWP", 250, 350));

        nodes.put("DGK", new NodeView("DGK", 200, 330));
        nodes.put("SUK", new NodeView("SUK", 150, 280));
        nodes.put("QTA", new NodeView("QTA", 80, 250));

        nodes.put("HYD", new NodeView("HYD", 200, 200));
        nodes.put("KHI", new NodeView("KHI", 220, 160));
    }

    private void loadFixedRoads() {

        // DEFAULTS for missing attributes in DataLoader
        String m = "";   // motorwayName
        double s = 60;   // speed km/h
        double t = 0;    // toll Rs

        edges.add(new EdgeView("ISL", "RWP", 15, m, s, t));
        edges.add(new EdgeView("ISL", "PSH", 170, m, s, t));
        edges.add(new EdgeView("RWP", "PSH", 160, m, s, t));

        edges.add(new EdgeView("ISL", "LHR", 380, m, s, t));
        edges.add(new EdgeView("RWP", "LHR", 350, m, s, t));
        edges.add(new EdgeView("LHR", "GRW", 70, m, s, t));
        edges.add(new EdgeView("GRW", "SKT", 40, m, s, t));
        edges.add(new EdgeView("SKT", "FSD", 150, m, s, t));
        edges.add(new EdgeView("LHR", "FSD", 140, m, s, t));

        edges.add(new EdgeView("FSD", "SWL", 110, m, s, t));
        edges.add(new EdgeView("SWL", "MLT", 110, m, s, t));
        edges.add(new EdgeView("MLT", "BWP", 100, m, s, t));
        edges.add(new EdgeView("BWP", "DGK", 90, m, s, t));

        edges.add(new EdgeView("DGK", "QTA", 600, m, s, t));
        edges.add(new EdgeView("QTA", "SUK", 480, m, s, t));

        edges.add(new EdgeView("SUK", "HYD", 480, m, s, t));
        edges.add(new EdgeView("HYD", "KHI", 170, m, s, t));

        edges.add(new EdgeView("MLT", "HYD", 700, m, s, t));
        edges.add(new EdgeView("DGK", "SUK", 450, m, s, t));
    }

    private void computeScreenCoordinates() {

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;

        for (NodeView n : nodes.values()) {
            minX = Math.min(minX, n.getX());
            minY = Math.min(minY, n.getY());
            maxX = Math.max(maxX, n.getX());
            maxY = Math.max(maxY, n.getY());
        }

        double width = maxX - minX;
        double height = maxY - minY;

        double scale = Math.min(900 / width, 600 / height);

        offsetX = (int)((900 - width * scale) / 2 + 40);
        offsetY = (int)((600 - height * scale) / 2 + 40);

        for (NodeView n : nodes.values()) {
            int sx = (int)((n.getX() - minX) * scale + offsetX);
            int sy = (int)((n.getY() - minY) * scale + offsetY);
            n.setScreenPosition(sx, sy);
        }
    }
}
