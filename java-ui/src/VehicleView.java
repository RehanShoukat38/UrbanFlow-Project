import java.util.List;
import java.util.Map;

/**
 * Lightweight vehicle animation state.
 * path holds node names, not coordinates (UI resolves positions).
 */
public class VehicleView {
    private final String id;
    private final List<String> path; // node names
    private int currentIndex = 0;    // index of node we are currently at or moving from
    private double progress = 0.0;   // 0..1 progress along current edge
    private double speed = 0.02;     // progress per tick (adjust with simulation speed)

    public VehicleView(String id, List<String> path) {
        this.id = id;
        this.path = path;
    }

    public String getId(){ return id; }
    public List<String> getPath(){ return path; }

    // update position by step (0..1)
    public void advance(double step) {
        progress += step * speed;
        if (progress >= 1.0) {
            progress = 0.0;
            if (currentIndex < path.size()-2) currentIndex++;
            else { // arrived -> cap at last
                currentIndex = path.size()-1;
            }
        }
    }

    // compute screen position using nodes map
    public int getScreenX(Map<String, NodeView> nodes) {
        if (currentIndex >= path.size()-1) {
            NodeView n = nodes.get(path.get(path.size()-1));
            return (n!=null)? n.getScreenX() : 0;
        }
        NodeView a = nodes.get(path.get(currentIndex));
        NodeView b = nodes.get(path.get(currentIndex+1));
        if (a==null || b==null) return 0;
        return (int)(a.getScreenX() * (1.0 - progress) + b.getScreenX()*progress);
    }

    public int getScreenY(Map<String, NodeView> nodes) {
        if (currentIndex >= path.size()-1) {
            NodeView n = nodes.get(path.get(path.size()-1));
            return (n!=null)? n.getScreenY() : 0;
        }
        NodeView a = nodes.get(path.get(currentIndex));
        NodeView b = nodes.get(path.get(currentIndex+1));
        if (a==null || b==null) return 0;
        return (int)(a.getScreenY() * (1.0 - progress) + b.getScreenY()*progress);
    }

    public boolean isArrived(){ return currentIndex >= path.size()-1; }

    public void setSpeedFactor(double factor) {
        this.speed = 0.02 * factor; // base speed scaled
    }
}
