#include "../include/TrafficSimulator.h"
#include <algorithm>
#include <stdexcept>

// ------------------------------------------------------------
// Constructor
// ------------------------------------------------------------

TrafficSimulator::TrafficSimulator(
        UrbanFlowGraph& g,
        TrafficModel& model_,
        double dt_)
    : urban(g),
      graph(g.getGraph()),
      model(model_),
      simTime(0.0),
      dt(dt_)
{}

// ------------------------------------------------------------
// Add a vehicle to simulation
// ------------------------------------------------------------

void TrafficSimulator::addVehicle(const Vehicle& v) {
    vehicles.push_back(v);
}

// ------------------------------------------------------------
// Main update loop (1 tick)
// ------------------------------------------------------------

void TrafficSimulator::update() {
    // advance simulation time
    simTime += dt;

    // move vehicles
    for (auto& v : vehicles) {
        updateVehicle(v);
    }

    // update flows based on positions
    clearFlows();
    applyFlows();

    // remove vehicles that reached destination
    removeArrived();
}

// ------------------------------------------------------------
// Move a single vehicle
// ------------------------------------------------------------

// Rules:
// - vehicle.path is a list of node IDs
// - vehicle moves along one edge at a time
// - timeOnCurrentEdge accumulates
// - if completed edge, move to next

void TrafficSimulator::updateVehicle(Vehicle& v) {
    if (v.currentIndexOnPath >= (int)v.path.size() - 1)
        return; // already at destination

    NodeId u = v.path[v.currentIndexOnPath];
    NodeId nxt = v.path[v.currentIndexOnPath + 1];

    // find edge u -> nxt
    const auto& edges = graph.getAdj(u);
    const Edge* edgePtr = nullptr;

    for (const auto& e : edges) {
        if (e.to == nxt) {
            edgePtr = &e;
            break;
        }
    }
    if (!edgePtr)
        return; // no edge found (path invalid)

    // how long this edge takes under congestion
    double travelTime = model.computeTravelTime(*edgePtr, v.maxSpeedFactor);

    v.timeOnCurrentEdge += dt;

    if (v.timeOnCurrentEdge >= travelTime) {
        // reached next node
        v.currentIndexOnPath++;
        v.timeOnCurrentEdge = 0.0;
    }
}

// ------------------------------------------------------------
// Apply flow = number of vehicles on an edge
// ------------------------------------------------------------

// We simply walk through all vehicles and count vehicles per edge

void TrafficSimulator::applyFlows() {
    for (const auto& v : vehicles) {
        if (v.currentIndexOnPath >= (int)v.path.size() - 1)
            continue;

        NodeId u = v.path[v.currentIndexOnPath];
        NodeId nxt = v.path[v.currentIndexOnPath + 1];

        auto& edges = graph.getAdj(u);
        for (auto& e : edges) {
            if (e.to == nxt) {
                e.currentFlow += 1.0;
                break;
            }
        }
    }
}

// ------------------------------------------------------------
// Reset all flow values on edges
// ------------------------------------------------------------

void TrafficSimulator::clearFlows() {
    graph.resetFlows();
}

// ------------------------------------------------------------
// Remove vehicles that reached final destination
// ------------------------------------------------------------

void TrafficSimulator::removeArrived() {
    vehicles.erase(
        std::remove_if(
            vehicles.begin(),
            vehicles.end(),
            [&](const Vehicle& v){
                return v.currentIndexOnPath >= (int)v.path.size() - 1;
            }),
        vehicles.end()
    );
}

// ------------------------------------------------------------

const std::vector<Vehicle>& TrafficSimulator::getVehicles() const noexcept {
    return vehicles;
}

double TrafficSimulator::getSimTime() const noexcept {
    return simTime;
}

void TrafficSimulator::reset() {
    vehicles.clear();
    simTime = 0.0;
    clearFlows();
}
