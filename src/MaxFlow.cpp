#include "../include/MaxFlow.h"
#include <stdexcept>
#include <algorithm>

MaxFlow::MaxFlow(const Graph& g)
    : graph(g),
      residual(g.getNumNodes()),
      level(g.getNumNodes()),
      it(g.getNumNodes())
{}

// ------------------------------------------------------------
// Build residual graph from original Graph
// ------------------------------------------------------------

void MaxFlow::buildResidualGraph() {
    // Clear old
    for (auto& v : residual)
        v.clear();

    // Build directed residual graph based on Graph edges
    const auto& adj = graph.getAdjList();

    for (NodeId u = 0; u < graph.getNumNodes(); ++u) {
        for (const auto& e : adj[u]) {
            // forward edge
            residual[u].push_back({
                e.to,
                (int)residual[e.to].size(),
                e.capacity
            });

            // reverse edge with 0 capacity initially
            residual[e.to].push_back({
                u,
                (int)residual[u].size() - 1,
                0.0
            });
        }
    }
}

// ------------------------------------------------------------
// BFS builds level graph
// ------------------------------------------------------------

bool MaxFlow::bfs(NodeId s, NodeId t) {
    std::fill(level.begin(), level.end(), -1);
    std::queue<NodeId> q;

    level[s] = 0;
    q.push(s);

    while (!q.empty()) {
        NodeId u = q.front();
        q.pop();

        for (auto& e : residual[u]) {
            if (level[e.to] < 0 && e.capacity > 1e-12) {
                level[e.to] = level[u] + 1;
                q.push(e.to);
            }
        }
    }

    return level[t] >= 0;
}

// ------------------------------------------------------------
// DFS finds blocking flow
// ------------------------------------------------------------

double MaxFlow::dfs(NodeId u, NodeId t, double f) {
    if (u == t)
        return f;

    for (int& i = it[u]; i < (int)residual[u].size(); ++i) {
        auto& e = residual[u][i];

        if (e.capacity > 1e-12 && level[e.to] == level[u] + 1) {
            double pushed = dfs(e.to, t, std::min(f, e.capacity));
            if (pushed > 1e-12) {
                // reduce forward capacity
                e.capacity -= pushed;
                // increase reverse capacity
                residual[e.to][e.revIndex].capacity += pushed;
                return pushed;
            }
        }
    }

    return 0.0;
}

// ------------------------------------------------------------
// Public MaxFlow call
// ------------------------------------------------------------

double MaxFlow::compute(NodeId s, NodeId t) {
    validate(s);
    validate(t);

    buildResidualGraph();

    double maxFlow = 0.0;

    while (bfs(s, t)) {
        std::fill(it.begin(), it.end(), 0);

        while (true) {
            double pushed = dfs(s, t, std::numeric_limits<double>::infinity());
            if (pushed <= 1e-12)
                break;

            maxFlow += pushed;
        }
    }

    lastMaxFlow = maxFlow;
    return maxFlow;
}

// ------------------------------------------------------------

double MaxFlow::getMaxFlow() const noexcept {
    return lastMaxFlow;
}

// ------------------------------------------------------------
// Retrieves nodes reachable from s in residual (min-cut)
// ------------------------------------------------------------

std::vector<bool> MaxFlow::getMinCutSet() const {
    std::vector<bool> visited(graph.getNumNodes(), false);
    std::queue<NodeId> q;

    q.push(0);
    visited[0] = true;

    while (!q.empty()) {
        NodeId u = q.front(); q.pop();
        for (auto& e : residual[u]) {
            if (!visited[e.to] && e.capacity > 1e-12) {
                visited[e.to] = true;
                q.push(e.to);
            }
        }
    }

    return visited;
}

// ------------------------------------------------------------

void MaxFlow::validate(NodeId id) const {
    if (id < 0 || id >= graph.getNumNodes()) {
        throw std::out_of_range("Invalid node in MaxFlow");
    }
}
