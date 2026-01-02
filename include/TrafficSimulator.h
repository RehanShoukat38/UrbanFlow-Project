#ifndef TRAFFIC_SIMULATOR_H
#define TRAFFIC_SIMULATOR_H
#pragma once

#include <vector>
#include <queue>
#include <functional>

#include "UrbanFlowGraph.h"
#include "TrafficModel.h"
#include "Types.h"

// TrafficSimulator:
// Moves vehicles along pre-computed paths, updates edge flows,
// applies congestion, and advances time in continuous simulation.
//
// This component connects:
//   - A* or Dijkstra
//   - dynamic travel times from TrafficModel
//   - flow (currentFlow) updates in edges
//   - vehicle state (location, progress)
//   - time-step simulation.
//
// You can update the UI every tick.

class TrafficSimulator {
public:
    explicit TrafficSimulator(
        UrbanFlowGraph& g,
        TrafficModel& model,
        double dt = 1.0    // simulation time step (seconds)
    );

    // Add a vehicle to simulation and assign a route
    // (You can pre-compute route using Dijkstra/AStar and feed "path")
    void addVehicle(const Vehicle& v);

    // One discrete simulation tick
    // - move vehicles
    // - update flows
    // - handle arrivals
    void update();

    // All vehicles currently in simulation
    const std::vector<Vehicle>& getVehicles() const noexcept;

    // Current time in simulation
    double getSimTime() const noexcept;

    // Remove vehicles that reached destination
    void removeArrived();

    // Reset flows and vehicles
    void reset();

private:
    UrbanFlowGraph& urban;
    Graph& graph;
    TrafficModel& model;

    std::vector<Vehicle> vehicles;

    double simTime;  // simulation time
    double dt;       // time step

    void updateVehicle(Vehicle& v);
    void applyFlows();
    void clearFlows();
};

#endif // TRAFFIC_SIMULATOR_H
