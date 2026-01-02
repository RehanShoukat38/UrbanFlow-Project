#ifndef URBANFLOWGRAPH_H
#define URBANFLOWGRAPH_H

#include <vector>
#include <unordered_map>
#include <string>
#include <optional>

#include "Graph.h"
#include "Types.h"

// High-level city model that wraps Graph, keeps Node information,
// lookup tables, and signal management. Everything backend algorithms
// need is accessible here.
class UrbanFlowGraph {
public:
    explicit UrbanFlowGraph(bool directed = true);

    // -----------------------------
    // Intersections (Nodes)
    // -----------------------------

    NodeId addIntersection(const std::string& name,
                           double x,
                           double y,
                           bool hasSignal = false);

    bool hasIntersection(const std::string& name) const;
    NodeId getNodeIdByName(const std::string& name) const;
    std::optional<NodeId> tryGetNodeIdByName(const std::string& name) const;

    const Node& getNode(NodeId id) const;
    Node&       getNode(NodeId id);

    const std::vector<Node>& getNodes() const noexcept;
    int getNumIntersections() const noexcept;

    // -----------------------------
    // Traffic signal access
    // -----------------------------
    bool nodeHasSignal(NodeId id) const;
    const TrafficSignal& getSignal(NodeId id) const;
    TrafficSignal&       getSignal(NodeId id);

    void setSignal(NodeId id, const TrafficSignal& signal);
    void enableSignal(NodeId id, bool enabled);

    // -----------------------------
    // Roads (Edges)
    // -----------------------------
    EdgeId addRoad(NodeId from,
                   NodeId to,
                   double length,
                   double baseTravelTime,
                   double capacity,
                   RoadType type = RoadType::LOCAL,
                   bool bidirectional = false);

    EdgeId addRoad(const std::string& fromName,
                   const std::string& toName,
                   double length,
                   double baseTravelTime,
                   double capacity,
                   RoadType type = RoadType::LOCAL,
                   bool bidirectional = false);

    std::size_t outDegree(NodeId id) const;

    // -----------------------------
    // Low level graph
    // -----------------------------
    const Graph& getGraph() const noexcept;
    Graph&       getGraph() noexcept;

    const std::vector<Edge>& getOutgoingEdges(NodeId id) const;
    std::vector<Edge>&       getOutgoingEdges(NodeId id);

    void resetAllFlows();

private:
    Graph graph;                                      // adjacency list graph
    std::vector<Node> nodes;                          // node info
    std::unordered_map<std::string, NodeId> nameToId; // name lookup

    void validateNode(NodeId id) const;
};

#endif // URBANFLOWGRAPH_H
