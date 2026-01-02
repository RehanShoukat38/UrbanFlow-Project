#ifndef TRAFFIC_MODEL_H
#define TRAFFIC_MODEL_H

#include <functional>

#include "Types.h"
#include "Graph.h"

// TrafficModel:
// Responsible for computing dynamic travel time for each road.
// Uses formulas involving currentFlow, capacity, baseTravelTime, vehicle speed, etc.
//
// You can pass this model to Dijkstra or A* to simulate real-time traffic routing.
//
// Later TrafficSimulator will update flows and call this model frequently.

class TrafficModel {
public:
    enum class CongestionFunction {
        LINEAR,
        BPR,            // Bureau of Public Roads formula (real-world highway)
        EXPONENTIAL
    };

    explicit TrafficModel(
        CongestionFunction f = CongestionFunction::LINEAR,
        double alpha = 0.15,    // typical BPR parameters
        double beta  = 4.0      // typical BPR parameters
    );

    // Returns travel time for an edge depending on flow.
    //
    // Optional vehicle speed factor can be added, e.g:
    // vehicle.maxSpeedFactor * baseTravelTime.
    double computeTravelTime(const Edge& edge,
                             double vehicleSpeedFactor = 1.0) const;

    // Allow changing formulas at runtime
    void setCongestionFunction(CongestionFunction f);
    void setBPRParameters(double alpha, double beta);

private:
    CongestionFunction func;
    double alpha;
    double beta;

    // congestion formulas
    double linearCongestion(const Edge& edge) const;
    double bprCongestion(const Edge& edge) const;
    double exponentialCongestion(const Edge& edge) const;
};

#endif // TRAFFIC_MODEL_H
