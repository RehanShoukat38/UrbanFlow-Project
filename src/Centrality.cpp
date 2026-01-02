#include "../include/Centrality.h"

#include <queue>
#include <stack>
#include <limits>
#include <cmath>

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------

Centrality::Centrality(const Graph& g)
    : graph(g) {}

// ------------------------------------------------------------
// Degree centrality: out-degree of each node
// ------------------------------------------------------------

std::vector<double> Centrality::degreeCentrality() const {
    int n = graph.getNumNodes();
    std::vector<double> result(n, 0.0);

    for (int u = 0; u < n; ++u) {
        result[u] = static_cast<double>(graph.getAdj(u).size());
    }

    return result;
}

// ------------------------------------------------------------
// Closeness centrality
//
// CC(v) = (reachableCount - 1) / sum(dist(v, u))
// where distances are shortest path distances.
// ------------------------------------------------------------

std::vector<double> Centrality::closenessCentrality() const {
    int n = graph.getNumNodes();
    std::vector<double> result(n, 0.0);

    if (n == 0) return result;

    for (int s = 0; s < n; ++s) {
        ShortestPath sp(graph);
        sp.compute(s);
        const auto& dist = sp.getDistances();

        double sumDist = 0.0;
        int reachable = 0;

        for (int v = 0; v < n; ++v) {
            if (v == s) continue;
            if (dist[v] < std::numeric_limits<double>::infinity()) {
                sumDist += dist[v];
                ++reachable;
            }
        }

        if (reachable > 0 && sumDist > 1e-12) {
            result[s] = static_cast<double>(reachable) / sumDist;
        } else {
            result[s] = 0.0;
        }
    }

    return result;
}

// ------------------------------------------------------------
// Betweenness centrality (Brandes Algorithm for weighted graphs)
//
// For every source s:
//   - Run Dijkstra, tracking number of shortest paths sigma[]
//   - Store predecessors P[v] for each v
//   - Back-propagate dependencies using stack
//
// BC(v) = sum over all pairs s != t (sigma_s,t(v) / sigma_s,t)
// ------------------------------------------------------------

std::vector<double> Centrality::betweennessCentrality() const {
    int n = graph.getNumNodes();
    std::vector<double> BC(n, 0.0);

    if (n == 0) return BC;

    const double INF = std::numeric_limits<double>::infinity();
    const auto& adj = graph.getAdjList();

    // Work arrays
    std::vector<double> dist(n);
    std::vector<double> sigma(n);
    std::vector<double> delta(n);
    std::vector<std::vector<int>> P(n); // predecessors
    std::stack<int> S;

    for (int s = 0; s < n; ++s) {
        // Reset
        while (!S.empty()) S.pop();
        for (int i = 0; i < n; ++i) {
            P[i].clear();
            dist[i] = INF;
            sigma[i] = 0.0;
        }

        dist[s] = 0.0;
        sigma[s] = 1.0;

        using PQItem = std::pair<double, int>;
        std::priority_queue<PQItem, std::vector<PQItem>, std::greater<PQItem>> pq;
        pq.emplace(0.0, s);

        // --------------- Dijkstra ---------------
        while (!pq.empty()) {
            auto [d, v] = pq.top();
            pq.pop();

            if (d > dist[v]) continue;

            S.push(v);

            for (const auto& e : adj[v]) {
                double w = e.baseTravelTime; // using base travel time as weight
                double newDist = dist[v] + w;

                if (newDist < dist[e.to] - 1e-12) {
                    dist[e.to] = newDist;
                    sigma[e.to] = sigma[v];
                    P[e.to].clear();
                    P[e.to].push_back(v);
                    pq.emplace(dist[e.to], e.to);
                } else if (std::fabs(newDist - dist[e.to]) <= 1e-12) {
                    // Another shortest path found
                    sigma[e.to] += sigma[v];
                    P[e.to].push_back(v);
                }
            }
        }

        // --------------- Dependency accumulation ---------------
        for (int i = 0; i < n; ++i) {
            delta[i] = 0.0;
        }

        // Process nodes in order of non-increasing distance
        while (!S.empty()) {
            int w = S.top();
            S.pop();

            for (int v : P[w]) {
                if (sigma[w] > 0.0) {
                    double contrib = (sigma[v] / sigma[w]) * (1.0 + delta[w]);
                    delta[v] += contrib;
                }
            }

            if (w != s) {
                BC[w] += delta[w];
            }
        }
    }

    // Optionally normalize here (not strictly required for DSA project)
    return BC;
}
