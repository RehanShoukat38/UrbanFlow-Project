#ifndef GRAPH_H
#define GRAPH_H

#include <vector>
#include <cstddef>
#include "Types.h"

// ============================================================================
// Graph (Adjacency List)
//
//  - Core data structure used by all UrbanFlow algorithms
//  - Represents intersections (nodes) and roads (edges)
//  - Supports directed or bidirectional roads
// ============================================================================

class Graph {
public:

    // ---------------------------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------------------------
    explicit Graph(int numNodes = 0, bool directed = true);

    // ---------------------------------------------------------------------
    // BASIC INFORMATION
    // ---------------------------------------------------------------------
    int  getNumNodes() const noexcept;
    bool isDirected() const noexcept;

    // ---------------------------------------------------------------------
    // NODE MODIFICATION
    // ---------------------------------------------------------------------
    // add and return new NodeId
    NodeId addNode();


    // ---------------------------------------------------------------------
    // EDGE INSERTION (CORE)
    // ---------------------------------------------------------------------
    //
    // from, to          : node ids
    // length            : physical road distance
    // baseTravelTime    : free-flow travel time
    // capacity          : maximum possible throughput
    // type              : HIGHWAY, ARTERIAL, LOCAL
    //
    // bidirectional=true:
    //      automatically creates reverse edge back-to-from using same params
    //
    // returns forward EdgeId
    //
    EdgeId addEdge(NodeId from,
                   NodeId to,
                   double length,
                   double baseTravelTime,
                   double capacity,
                   RoadType type = RoadType::LOCAL,
                   bool bidirectional = false);


    // ---------------------------------------------------------------------
    // ACCESS ADJACENCY (read-only and modifiable)
    // ---------------------------------------------------------------------
    const std::vector<Edge>& getAdj(NodeId node) const;
    std::vector<Edge>&       getAdj(NodeId node);


    // ---------------------------------------------------------------------
    // FULL ADJ LIST
    // ---------------------------------------------------------------------
    const std::vector<std::vector<Edge>>& getAdjList() const noexcept;


    // ---------------------------------------------------------------------
    // FLATTEN ALL EDGES (returns copy)
    // ---------------------------------------------------------------------
    std::vector<Edge> getAllEdges() const;


    // ---------------------------------------------------------------------
    // FLOW RESET
    // (used by MaxFlow or TrafficSimulator before simulation)
    // ---------------------------------------------------------------------
    void resetFlows();


private:
    int numNodes;
    bool directed;

    // adjacency list: outgoing edges for each node
    std::vector<std::vector<Edge>> adjList;

    // global edge counter (unique per edge)
    EdgeId nextEdgeId;

    // guard node indices
    void validateNode(NodeId node) const;
};

#endif // GRAPH_H
