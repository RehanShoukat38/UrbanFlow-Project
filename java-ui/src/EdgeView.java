public class EdgeView {

    private final String from;
    private final String to;
    private final double length;

    private final double speed;      // km/h
    private final double toll;       // Rs
    private String motorwayName;     // M2, M9, N5, etc.

    private double congestion = 0.0; // 0.0 – 1.0

    public EdgeView(String from, String to, double length,
                    String motorwayName, double speed, double toll)
    {
        this.from = from;
        this.to = to;
        this.length = length;

        this.motorwayName = (motorwayName == null ? "" : motorwayName.trim());
        this.speed = speed;
        this.toll = toll;
    }

    // -------------------------
    // BASIC GETTERS
    // -------------------------

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getLength() { return length; }

    public double getSpeed() { return speed; }
    public double getToll() { return toll; }

    public String getMotorwayName() {
        return (motorwayName == null ? "" : motorwayName);
    }

    // -------------------------
    // GRAPH SUPPORT
    // -------------------------

    /** Returns true if this edge touches the given city */
    public boolean connects(String cityKey) {
        return from.equals(cityKey) || to.equals(cityKey);
    }

    public void setMotorwayName(String name) {
        if (name == null) name = "";
        this.motorwayName = name.trim();
    }

    // -------------------------
    // CONGESTION SUPPORT
    // -------------------------

    public void setCongestion(double c) {
        congestion = Math.max(0.0, Math.min(1.0, c));
    }

    public double getCongestion() {
        return congestion;
    }

    // -------------------------
    // DEBUG
    // -------------------------

    @Override
    public String toString() {
        return from + " → " + to + " (" + length + " km, " + motorwayName + ")";
    }
    // TEST METHOD
public void __test() {}

}
