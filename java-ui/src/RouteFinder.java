import java.util.*;
import java.awt.geom.Point2D;

/**
 * RouteFinder
 *
 * Supports:
 *   - DIJKSTRA              (pure distance)
 *   - ASTAR                 (distance + heuristic)
 *   - CONGESTION_AWARE      (distance * (1 + congestion))
 *
 * Works with EXTERNAL EdgeView class:
 *   from, to, length, speed, toll, motorwayName, congestion
 */
public class RouteFinder {

    public enum Algo { DIJKSTRA, ASTAR, CONGESTION_AWARE }

    /**
     * Computes path of node names from src → dst.
     * Returns empty list if no route exists.
     */
    public static List<String> findPath(Map<String, NodeView> nodes,
                                        List<EdgeView> edges,
                                        String srcName,
                                        String dstName,
                                        Algo algo)
    {
        if (!nodes.containsKey(srcName) || !nodes.containsKey(dstName))
            return Collections.emptyList();

        // Build adjacency: Map<String, List<EdgeEntry>>
        Map<String, List<EdgeEntry>> adj = new HashMap<>();
        for (EdgeView e : edges) {
            adj.computeIfAbsent(e.getFrom(), k -> new ArrayList<>())
               .add(new EdgeEntry(e.getTo(), e.getLength(), e.getCongestion()));
        }

        String start = srcName;
        String goal  = dstName;

        PriorityQueue<NodeRecord> open =
                new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));

        Map<String, Double> gScore = new HashMap<>();
        Map<String, String> cameFrom = new HashMap<>();

        gScore.put(start, 0.0);
        open.add(new NodeRecord(start, 0.0,
                heuristic(nodes, start, goal, algo)));

        while (!open.isEmpty()) {

            NodeRecord cur = open.poll();
            if (cur.processed) continue;
            cur.processed = true;

            String u = cur.name;
            if (u.equals(goal)) break; // reached destination

            for (EdgeEntry e : adj.getOrDefault(u, Collections.emptyList())) {

                double cost = e.length;
                if (algo == Algo.CONGESTION_AWARE) {
                    cost = e.length * (1.0 + e.congestion);
                }

                double tentative = gScore.getOrDefault(u, Double.POSITIVE_INFINITY) + cost;

                if (tentative < gScore.getOrDefault(e.to, Double.POSITIVE_INFINITY)) {
                    gScore.put(e.to, tentative);
                    cameFrom.put(e.to, u);

                    double h = heuristic(nodes, e.to, goal, algo);
                    open.add(new NodeRecord(e.to, tentative, tentative + h));
                }
            }
        }

        // Build final path
        LinkedList<String> path = new LinkedList<>();
        String cur = goal;
        path.addFirst(cur);

        while (!cur.equals(start)) {
            String p = cameFrom.get(cur);
            if (p == null) break;
            path.addFirst(p);
            cur = p;
        }

        if (!path.isEmpty() && path.getFirst().equals(start))
            return path;

        return Collections.emptyList();
    }

    /**
     * A* heuristic = Euclidean distance between nodes.
     * Disabled for pure Dijkstra and congestion mode.
     */
    private static double heuristic(Map<String, NodeView> nodes,
                                    String a, String b, Algo algo)
    {
        if (algo != Algo.ASTAR) return 0.0;

        NodeView na = nodes.get(a);
        NodeView nb = nodes.get(b);
        if (na == null || nb == null) return 0.0;

        double dx = na.getX() - nb.getX();
        double dy = na.getY() - nb.getY();

        return Math.hypot(dx, dy);
    }

    // -----------------------------------------------
    // INTERNAL SUPPORT CLASSES
    // -----------------------------------------------

    private static class EdgeEntry {
        String to;
        double length;
        double congestion;

        EdgeEntry(String t, double l, double c) {
            to = t;
            length = l;
            congestion = c;
        }
    }

    private static class NodeRecord {
        String name;
        double g;
        double f;
        boolean processed = false;

        NodeRecord(String n, double g_, double f_) {
            name = n;
            g = g_;
            f = f_;
        }
    }
}
