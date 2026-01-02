/**
 * =============================================================
 * CityInsight
 * =============================================================
 * - Pure data model (NO UI code)
 * - Represents analytics for a single city
 * - Used by:
 *     • UrbanFlowPanel (computations)
 *     • CityInsightsView (visualization)
 * =============================================================
 */
public class CityInsight {

    // -----------------------------
    // Immutable data fields
    // -----------------------------
    public final String city;          // City code (e.g., LHR)
    public final int connectivity;     // Number of direct connections
    public final double centrality;    // Importance score (0.0 – 1.0)
    public final int trafficPressure;  // Traffic load percentage (0 – 100)
    public final String classification; // CRITICAL / MAJOR / MINOR hub

    // -----------------------------
    // Constructor
    // -----------------------------
    public CityInsight(
            String city,
            int connectivity,
            double centrality,
            int trafficPressure,
            String classification
    ) {
        this.city = city;
        this.connectivity = connectivity;
        this.centrality = centrality;
        this.trafficPressure = trafficPressure;
        this.classification = classification;
    }

    // -----------------------------
    // Optional helper methods
    // -----------------------------
    public boolean isCriticalHub() {
        return "CRITICAL HUB".equalsIgnoreCase(classification);
    }

    public boolean isMajorHub() {
        return "MAJOR HUB".equalsIgnoreCase(classification);
    }

    public boolean isMinorHub() {
        return "MINOR HUB".equalsIgnoreCase(classification);
    }

    @Override
    public String toString() {
        return "CityInsight{" +
                "city='" + city + '\'' +
                ", connectivity=" + connectivity +
                ", centrality=" + centrality +
                ", trafficPressure=" + trafficPressure +
                ", classification='" + classification + '\'' +
                '}';
    }
}
