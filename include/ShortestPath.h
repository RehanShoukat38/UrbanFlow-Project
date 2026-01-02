#ifndef SHORTEST_PATH_H
#define SHORTEST_PATH_H

#include <vector>
#include <limits>
#include <queue>
#include <functional>
#include <optional>

#include "Graph.h"
#include "Types.h"

class ShortestPath {
public:
    explicit ShortestPath(const Graph& graph);

    // Run Dijkstra from a single source
    void compute(NodeId source,
                 std::function<double(const Edge&)> weightFunc = defaultWeight);

    // Returns shortest distance to given target
    double getDistance(NodeId target) const;

    // Returns entire distance vector
    const std::vector<double>& getDistances() const noexcept;

    // Reconstruct path from last run
    std::vector<NodeId> buildPath(NodeId target) const;

    // Check if reachable
    bool reachable(NodeId target) const;

    // Default weight = baseTravelTime of edge
    static double defaultWeight(const Edge& e) { return e.baseTravelTime; }

private:
    const Graph& graph;

    std::vector<double> dist;
    std::vector<NodeId> parent;
};

#endif // SHORTEST_PATH_H
