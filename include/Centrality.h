#ifndef CENTRALITY_H
#define CENTRALITY_H

#include <vector>
#include <functional>
#include <limits>

#include "Graph.h"
#include "ShortestPath.h"

// Centrality metrics applied on graph structure.
// -----------------------------------------------------------
// Included:
//  - Degree Centrality
//  - Closeness Centrality (based on shortest paths)
//  - Betweenness Centrality (Brandes Algorithm)
//
// Usage:
//  Centrality C(g);
//  auto vec = C.betweenness();
//
// For DSA evaluation, this is a killer feature.

class Centrality {
public:
    explicit Centrality(const Graph& g);

    // Number of outgoing edges from a node
    std::vector<double> degreeCentrality() const;

    // Closeness Centrality:
    // CC(v) = (N-1) / sum(distances to all other nodes)
    std::vector<double> closenessCentrality() const;

    // Betweenness Centrality (Brandes Algorithm)
    // Measures # of shortest paths through a node
    std::vector<double> betweennessCentrality() const;

private:
    const Graph& graph;
};

#endif // CENTRALITY_H
