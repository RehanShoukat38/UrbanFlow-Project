#include "../include/AStar.h"
#include <cmath>
#include <stdexcept>
#include <algorithm>
#include <limits>

AStar::AStar(const UrbanFlowGraph& urbanGraph)
    : urban(urbanGraph),
      graph(urbanGraph.getGraph()),
      dist(graph.getNumNodes(), std::numeric_limits<double>::infinity()),
      fScore(graph.getNumNodes(), std::numeric_limits<double>::infinity()),
      parent(graph.getNumNodes(), -1)
{}

// ----------------------------------------------------------------------
// A* main routine
// ----------------------------------------------------------------------

void AStar::compute(
        NodeId source,
        NodeId target,
        std::function<double(const Edge&)> weightFunc)
{
    validate(source);
    validate(target);

    std::fill(dist.begin(), dist.end(),
              std::numeric_limits<double>::infinity());
    std::fill(fScore.begin(), fScore.end(),
              std::numeric_limits<double>::infinity());
    std::fill(parent.begin(), parent.end(), -1);

    dist[source] = 0.0;
    fScore[source] = heuristic(source, target);

    using PQItem = std::pair<double, NodeId>;

    std::priority_queue<PQItem,
                        std::vector<PQItem>,
                        std::greater<PQItem>> openSet;

    openSet.emplace(fScore[source], source);

    while (!openSet.empty()) {
        auto [currF, u] = openSet.top();
        openSet.pop();

        if (u == target) {
            return; // shortest path found
        }

        if (currF > fScore[u]) {
            continue; // skip stale entry
        }

        for (const auto& edge : graph.getAdj(u)) {
            double w = weightFunc(edge);
            double tentative = dist[u] + w;

            if (tentative < dist[edge.to]) {
                dist[edge.to] = tentative;
                parent[edge.to] = u;
                fScore[edge.to] = tentative + heuristic(edge.to, target);
                openSet.emplace(fScore[edge.to], edge.to);
            }
        }
    }
}

// ----------------------------------------------------------------------

bool AStar::reachable(NodeId target) const {
    validate(target);
    return dist[target] < std::numeric_limits<double>::infinity();
}

// ----------------------------------------------------------------------

double AStar::getDistance(NodeId target) const {
    validate(target);
    return dist[target];
}

// ----------------------------------------------------------------------

const std::vector<double>& AStar::getDistances() const noexcept {
    return dist;
}

// ----------------------------------------------------------------------

std::vector<NodeId> AStar::buildPath(NodeId target) const {
    validate(target);

    if (!reachable(target))
        return {};

    std::vector<NodeId> result;
    for (NodeId v = target; v != -1; v = parent[v]) {
        result.push_back(v);
    }
    std::reverse(result.begin(), result.end());
    return result;
}

// ----------------------------------------------------------------------
// Euclidean heuristic (straight-line distance)
// ----------------------------------------------------------------------

double AStar::heuristic(NodeId a, NodeId b) const {
    validate(a);
    validate(b);

    const Node& na = urban.getNode(a);
    const Node& nb = urban.getNode(b);

    double dx = na.x - nb.x;
    double dy = na.y - nb.y;

    return std::sqrt(dx * dx + dy * dy);
}

// ----------------------------------------------------------------------

void AStar::validate(NodeId id) const {
    if (id < 0 || id >= graph.getNumNodes()) {
        throw std::out_of_range("Invalid NodeId in AStar");
    }
}
