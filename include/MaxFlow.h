#ifndef MAX_FLOW_H
#define MAX_FLOW_H
#pragma once


#include <algorithm>

#include <vector>
#include <queue>
#include <limits>
#include "Graph.h"
#include "Types.h"

// MaxFlow (Dinic's Algorithm)
// ---------------------------
// Uses the existing Graph as a logical network:
//   - Edge.capacity is used as capacity
//   - We build an internal residual graph
//
// This can be used to:
//   - find bottlenecks between two regions
//   - evaluate max throughput between two intersections
//   - show critical roads on UI (min-cut)

class MaxFlow {
public:
    explicit MaxFlow(const Graph& g);

    // Compute maximum flow between source s and sink t
    double compute(NodeId s, NodeId t);

    // Get value of last computed max flow
    double getMaxFlow() const noexcept;

    // (Optional) get nodes on source side of min-cut after maxflow
    std::vector<bool> getMinCutSet() const;

private:
    struct FlowEdge {
        NodeId to;
        int revIndex;       // index of reverse edge in adjacency list
        double capacity;
    };

    const Graph& graph;

    // Residual network
    std::vector<std::vector<FlowEdge>> residual;

    std::vector<int> level;   // level graph
    std::vector<int> it;      // iterator for DFS

    double lastMaxFlow = 0.0;

    void buildResidualGraph();
    bool bfs(NodeId s, NodeId t);
    double dfs(NodeId v, NodeId t, double f);

    void validate(NodeId id) const;
};

#endif // MAX_FLOW_H
