#include "../include/Graph.h"
#include <stdexcept>

Graph::Graph(int numNodes_, bool directed_)
    : numNodes(numNodes_),
      directed(directed_),
      adjList(numNodes_),
      nextEdgeId(0)
{}

int Graph::getNumNodes() const noexcept { return numNodes; }
bool Graph::isDirected() const noexcept { return directed; }

NodeId Graph::addNode()
{
    adjList.emplace_back();
    return static_cast<NodeId>(numNodes++);
}

EdgeId Graph::addEdge(NodeId from,
                      NodeId to,
                      double length,
                      double baseTravelTime,
                      double capacity,
                      RoadType type,
                      bool bidirectional)
{
    validateNode(from);
    validateNode(to);

    EdgeId eid = nextEdgeId++;

    // Create forward edge
    Edge e(eid, from, to, length, baseTravelTime, capacity, type);
    adjList[from].push_back(e);

    // If graph is directed but bidirectional requested,
    // add reverse edge with new id:
    if (bidirectional)
    {
        EdgeId eid2 = nextEdgeId++;
        Edge e2(eid2, to, from, length, baseTravelTime, capacity, type);
        adjList[to].push_back(e2);
    }

    return eid;
}

const std::vector<Edge>& Graph::getAdj(NodeId node) const
{
    validateNode(node);
    return adjList[node];
}

std::vector<Edge>& Graph::getAdj(NodeId node)
{
    validateNode(node);
    return adjList[node];
}

const std::vector<std::vector<Edge>>& Graph::getAdjList() const noexcept
{
    return adjList;
}

std::vector<Edge> Graph::getAllEdges() const
{
    std::vector<Edge> out;
    for (const auto& v : adjList)
        for (auto& e : v)
            out.push_back(e);
    return out;
}

void Graph::resetFlows()
{
    for (auto& v : adjList)
        for (auto& e : v)
            e.currentFlow = 0.0;
}

void Graph::validateNode(NodeId node) const
{
    if (node >= numNodes)
        throw std::out_of_range("Invalid NodeId");
}
