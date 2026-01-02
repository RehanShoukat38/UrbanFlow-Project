#include <iostream>
#include <vector>
#include <fstream>

#include "../include/UrbanFlowGraph.h"
#include "../include/ShortestPath.h"
#include "../include/AStar.h"
#include "../include/TrafficModel.h"
#include "../include/TrafficSimulator.h"
#include "../include/Centrality.h"
#include "../include/MaxFlow.h"
#include "../include/FileIO.h"

using namespace std;

// ================================================================
// EXPORT VEHICLE PATHS for Java Visualizer
// ================================================================
void exportVehiclePaths(const string& filename,
                        const vector<Vehicle>& vehicles,
                        const UrbanFlowGraph& city)
{
    ofstream out(filename);
    if (!out) {
        cerr << "[Error] Cannot open file: " << filename << endl;
        return;
    }

    for (const auto& v : vehicles)
    {
        out << v.id << ";";

        for (size_t i = 0; i < v.path.size(); ++i)
        {
            NodeId nid = v.path[i];
            out << city.getNode(nid).name;

            if (i + 1 < v.path.size())
                out << ",";
        }
        out << "\n";
    }

    cout << "[OK] Exported paths -> " << filename << "\n";
}


// ================================================================
// EXPORT EDGE "CONGESTION" (UPDATED – NO weight field)
// ================================================================
void exportEdgeCongestion(const string& filename,
                          const UrbanFlowGraph& city)
{
    ofstream out(filename);
    if (!out) {
        cerr << "[Error] Cannot open file: " << filename << endl;
        return;
    }

    const auto& g   = city.getGraph();
    const auto& adj = g.getAdjList();

    double maxCong = 0.0;

    // find max congestion (currentFlow / capacity)
    for (NodeId u = 0; u < (NodeId)adj.size(); ++u) {
        for (const auto& e : adj[u]) {

            double cong = 0.0;
            if (e.capacity > 0)
                cong = e.currentFlow / e.capacity;

            if (cong > maxCong)
                maxCong = cong;
        }
    }
    if (maxCong <= 0.0)
        maxCong = 1.0;

    // normalize output 0..1
    for (NodeId u = 0; u < (NodeId)adj.size(); ++u) {
        for (const auto& e : adj[u]) {

            double cong = 0.0;
            if (e.capacity > 0)
                cong = e.currentFlow / e.capacity;

            double factor = cong / maxCong;

            out << city.getNode(u).name << ","
                << city.getNode(e.to).name << ","
                << factor << "\n";
        }
    }

    cout << "[OK] Exported congestion -> " << filename << "\n";
}


int main()
{
    try
    {
        cout << "\n=== UrbanFlow Simulation Starting ===\n";

        UrbanFlowGraph city(true);

        auto vehicles = FileIO::loadAll("data", city);

        cout << "Intersections loaded: " << city.getNumIntersections() << "\n";
        cout << "Vehicles loaded:      " << vehicles.size()           << "\n\n";

        ShortestPath sp(city.getGraph());
        sp.compute(0);

        cout << "Dijkstra (from 0):\n";
        for (int i = 0; i < city.getNumIntersections(); i++) {
            double d = sp.getDistance(i);
            cout << "  " << i << " : ";
            if (d < 1e12) cout << d;
            else          cout << "INF";
            cout << "\n";
        }

        if (city.getNumIntersections() > 1)
        {
            AStar ast(city);
            ast.compute(0, city.getNumIntersections() - 1);

            cout << "\nA* 0 -> last distance: "
                 << ast.getDistance(city.getNumIntersections() - 1) << "\n";
        }

        Centrality cen(city.getGraph());

        auto deg = cen.degreeCentrality();
        auto clo = cen.closenessCentrality();
        auto bet = cen.betweennessCentrality();

        cout << "\nDegree Centrality:\n";
        for (int i = 0; i < deg.size(); i++)
            cout << "  " << i << ": " << deg[i] << "\n";

        cout << "\nCloseness Centrality:\n";
        for (int i = 0; i < clo.size(); i++)
            cout << "  " << i << ": " << clo[i] << "\n";

        cout << "\nBetweenness Centrality:\n";
        for (int i = 0; i < bet.size(); i++)
            cout << "  " << i << ": " << bet[i] << "\n";

        if (city.getNumIntersections() > 1)
        {
            MaxFlow mf(city.getGraph());
            double mfval = mf.compute(0, city.getNumIntersections() - 1);
            cout << "\nMaxFlow (0->last): " << mfval << "\n";
        }

        TrafficModel model(TrafficModel::CongestionFunction::BPR);
        TrafficSimulator sim(city, model);

        AStar ast(city);
        for (auto& v : vehicles)
        {
            ast.compute(v.source, v.destination);
            v.path = ast.buildPath(v.destination);
            sim.addVehicle(v);
        }

        exportVehiclePaths("java-ui/vehicle_paths.csv", vehicles, city);

        cout << "\nSimulating traffic...\n";
        for (int t = 0; t < 20; t++)
        {
            sim.update();
            cout << "time=" << sim.getSimTime()
                 << "  vehicles=" << sim.getVehicles().size() << "\n";
        }

        exportEdgeCongestion("java-ui/edge_congestion.csv", city);

        cout << "\n=== UrbanFlow Finished ===\n";
    }
    catch (const std::exception& ex)
    {
        cerr << "Error: " << ex.what() << "\n";
    }

    return 0;
}
