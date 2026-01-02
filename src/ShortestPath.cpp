#include "../include/ShortestPath.h"
#include <stdexcept>
#include <algorithm>

ShortestPath::ShortestPath(const Graph& graph_)
    : graph(graph_),
      dist(graph_.getNumNodes(), std::numeric_limits<double>::infinity()),
      parent(graph_.getNumNodes(), -1)
{}

// ----------------------------------------------------------------------
// Dijkstra with customizable edge-weight function
// ----------------------------------------------------------------------

void ShortestPath::compute(
        NodeId source,
        std::function<double(const Edge&)> weightFunc)
{
    if (source < 0 || source >= graph.getNumNodes()) {
        throw std::out_of_range("Invalid source node in ShortestPath::compute");
    }

    // Reset arrays
    std::fill(dist.begin(), dist.end(),
              std::numeric_limits<double>::infinity());
    std::fill(parent.begin(), parent.end(), -1);

    // Distance to source = 0
    dist[source] = 0.0;

    // Min-heap (priority queue) of (dist, node)
    using PQItem = std::pair<double, NodeId>;
    std::priority_queue<PQItem,
                        std::vector<PQItem>,
                        std::greater<PQItem>> pq;

    pq.emplace(0.0, source);

    // Standard Dijkstra
    while (!pq.empty()) {
        auto [currDist, u] = pq.top();
        pq.pop();

        // Skip if stale entry
        if (currDist > dist[u])
            continue;

        // Explore outgoing edges
        for (const auto& edge : graph.getAdj(u)) {
            double w = weightFunc(edge);
            double newDist = dist[u] + w;

            if (newDist < dist[edge.to]) {
                dist[edge.to] = newDist;
                parent[edge.to] = u;
                pq.emplace(newDist, edge.to);
            }
        }
    }
}

// ----------------------------------------------------------------------

double ShortestPath::getDistance(NodeId target) const {
    if (target < 0 || target >= graph.getNumNodes()) {
        throw std::out_of_range("Invalid target node in ShortestPath::getDistance");
    }
    return dist[target];
}

// ----------------------------------------------------------------------

const std::vector<double>& ShortestPath::getDistances() const noexcept {
    return dist;
}

// ----------------------------------------------------------------------
// Build path by following parents backwards
// ----------------------------------------------------------------------

std::vector<NodeId> ShortestPath::buildPath(NodeId target) const {
    if (target < 0 || target >= graph.getNumNodes()) {
        throw std::out_of_range("Invalid target in ShortestPath::buildPath");
    }

    if (!reachable(target))
        return {};

    std::vector<NodeId> path;
    for (NodeId v = target; v != -1; v = parent[v]) {
        path.push_back(v);
    }
    std::reverse(path.begin(), path.end());
    return path;
}

// ----------------------------------------------------------------------

bool ShortestPath::reachable(NodeId target) const {
    if (target < 0 || target >= graph.getNumNodes()) {
        return false;
    }
    return dist[target] < std::numeric_limits<double>::infinity();
}
