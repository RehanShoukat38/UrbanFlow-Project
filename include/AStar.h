#ifndef ASTAR_H
#define ASTAR_H

#include <vector>
#include <queue>
#include <functional>
#include <optional>

#include "Graph.h"
#include "Types.h"
#include "UrbanFlowGraph.h"

class AStar {
public:
    // A* operates directly on the higher-level city graph
    explicit AStar(const UrbanFlowGraph& urbanGraph);

    // Computes A* from source → target
    void compute(NodeId source,
                 NodeId target,
                 std::function<double(const Edge&)> weightFunc =
                     [](const Edge& e){ return e.baseTravelTime; });

    // Check if target reachable
    bool reachable(NodeId target) const;

    // Result distance
    double getDistance(NodeId target) const;

    // Entire distance vector
    const std::vector<double>& getDistances() const noexcept;

    // Reconstruct path
    std::vector<NodeId> buildPath(NodeId target) const;

private:
    const UrbanFlowGraph& urban;
    const Graph& graph;

    std::vector<double> dist;     // g(n)
    std::vector<double> fScore;   // f(n) = g(n) + h(n)
    std::vector<NodeId> parent;

    // Heuristic using coordinates
    double heuristic(NodeId a, NodeId b) const;

    // Validate node index
    void validate(NodeId id) const;
};

#endif // ASTAR_H
