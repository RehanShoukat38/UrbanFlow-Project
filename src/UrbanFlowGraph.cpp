#include "../include/UrbanFlowGraph.h"
#include <stdexcept>

// ----------------------------------------------
// Constructor
// ----------------------------------------------

UrbanFlowGraph::UrbanFlowGraph(bool directed)
    : graph(0, directed) {}

// ----------------------------------------------
// Node operations
// ----------------------------------------------

NodeId UrbanFlowGraph::addIntersection(
        const std::string& name,
        double x,
        double y,
        bool hasSignal) 
{
    if (nameToId.count(name)) {
        throw std::runtime_error("Intersection with same name already exists: " + name);
    }

    NodeId id = static_cast<NodeId>(nodes.size());
    nodes.emplace_back(id, name, x, y, hasSignal);

    nameToId[name] = id;
    graph.addNode(); // increase node count in graph

    return id;
}

bool UrbanFlowGraph::hasIntersection(const std::string& name) const {
    return nameToId.count(name) != 0;
}

NodeId UrbanFlowGraph::getNodeIdByName(const std::string& name) const {
    auto it = nameToId.find(name);
    if (it == nameToId.end()) {
        throw std::runtime_error("Intersection not found: " + name);
    }
    return it->second;
}

std::optional<NodeId> UrbanFlowGraph::tryGetNodeIdByName(const std::string& name) const {
    if (auto it = nameToId.find(name); it != nameToId.end()) {
        return it->second;
    }
    return std::nullopt;
}

const Node& UrbanFlowGraph::getNode(NodeId id) const {
    validateNode(id);
    return nodes[id];
}

Node& UrbanFlowGraph::getNode(NodeId id) {
    validateNode(id);
    return nodes[id];
}

const std::vector<Node>& UrbanFlowGraph::getNodes() const noexcept {
    return nodes;
}

int UrbanFlowGraph::getNumIntersections() const noexcept {
    return static_cast<int>(nodes.size());
}

// ----------------------------------------------
// Traffic signal operations
// ----------------------------------------------

bool UrbanFlowGraph::nodeHasSignal(NodeId id) const {
    validateNode(id);
    return nodes[id].hasSignal;
}

void UrbanFlowGraph::setSignal(NodeId id, const TrafficSignal& signal) {
    validateNode(id);
    nodes[id].hasSignal = true;
    nodes[id].signal = signal;
}

void UrbanFlowGraph::enableSignal(NodeId id, bool enabled) {
    validateNode(id);
    nodes[id].hasSignal = enabled;
}

const TrafficSignal& UrbanFlowGraph::getSignal(NodeId id) const {
    validateNode(id);
    return nodes[id].signal;
}

TrafficSignal& UrbanFlowGraph::getSignal(NodeId id) {
    validateNode(id);
    return nodes[id].signal;
}

// ----------------------------------------------
// Road operations
// ----------------------------------------------

EdgeId UrbanFlowGraph::addRoad(NodeId from,
                               NodeId to,
                               double length,
                               double baseTravelTime,
                               double capacity,
                               RoadType type,
                               bool bidirectional) 
{
    validateNode(from);
    validateNode(to);

    return graph.addEdge(
        from, to,
        length,
        baseTravelTime,
        capacity,
        type,
        bidirectional
    );
}

EdgeId UrbanFlowGraph::addRoad(const std::string& fromName,
                               const std::string& toName,
                               double length,
                               double baseTravelTime,
                               double capacity,
                               RoadType type,
                               bool bidirectional)
{
    NodeId from = getNodeIdByName(fromName);
    NodeId to   = getNodeIdByName(toName);

    return addRoad(from, to, length, baseTravelTime, capacity, type, bidirectional);
}

std::size_t UrbanFlowGraph::outDegree(NodeId id) const {
    validateNode(id);
    return graph.getAdj(id).size();
}

// ----------------------------------------------
// Graph access
// ----------------------------------------------

const Graph& UrbanFlowGraph::getGraph() const noexcept {
    return graph;
}

Graph& UrbanFlowGraph::getGraph() noexcept {
    return graph;
}

const std::vector<Edge>& UrbanFlowGraph::getOutgoingEdges(NodeId id) const {
    validateNode(id);
    return graph.getAdj(id);
}

std::vector<Edge>& UrbanFlowGraph::getOutgoingEdges(NodeId id) {
    validateNode(id);
    return graph.getAdj(id);
}

void UrbanFlowGraph::resetAllFlows() {
    graph.resetFlows();
}

// ----------------------------------------------
// Validation
// ----------------------------------------------

void UrbanFlowGraph::validateNode(NodeId id) const {
    if (id < 0 || id >= static_cast<NodeId>(nodes.size())) {
        throw std::out_of_range("Invalid NodeId in UrbanFlowGraph");
    }
}
