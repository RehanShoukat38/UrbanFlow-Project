#ifndef TYPES_H
#define TYPES_H

#include <cstddef>
#include <string>
#include <vector>

// -----------------------------------------------------
// Basic ID types
// -----------------------------------------------------
using NodeId = std::size_t;
using EdgeId = std::size_t;


// -----------------------------------------------------
// Road classification
// -----------------------------------------------------
enum class RoadType {
    HIGHWAY,
    ARTERIAL,
    LOCAL
};


// -----------------------------------------------------
// Traffic signal type
// -----------------------------------------------------
struct TrafficSignal {
    bool isGreen {true};    // true = allowed to pass
    double cycleTime {60.0};
    double greenRatio {0.6};
};


// -----------------------------------------------------
// Node (intersection) information
// -----------------------------------------------------
struct Node {
    NodeId id {};
    std::string name;
    double x {0};
    double y {0};

    bool hasSignal {false};
    TrafficSignal signal;

    Node() = default;

    Node(NodeId _id,
         const std::string& _name,
         double _x,
         double _y,
         bool _hasSignal=false)
    : id(_id), name(_name), x(_x), y(_y), hasSignal(_hasSignal)
    {}
};


// -----------------------------------------------------
// Edge = road segment
// IMPORTANT:
//   weight() returns baseTravelTime (for algorithms)
//   all congestion logic uses currentFlow / capacity
// -----------------------------------------------------
struct Edge {
    EdgeId     id;
    NodeId     from;
    NodeId     to;

    double     length;
    double     baseTravelTime;
    double     capacity;
    double     currentFlow;
    RoadType   type;

    // compatibility alias
    // DO NOT REMOVE: used by older code sometimes
    double weight() const { return baseTravelTime; }

    Edge(EdgeId _eid,
         NodeId _from,
         NodeId _to,
         double _len,
         double _btime,
         double _cap,
         RoadType _t)
    : id(_eid),
      from(_from),
      to(_to),
      length(_len),
      baseTravelTime(_btime),
      capacity(_cap),
      currentFlow(0.0),
      type(_t)
    {}
};


// -----------------------------------------------------
// Vehicle for simulator
// must contain these fields for TrafficSimulator:
//   currentIndexOnPath
//   timeOnCurrentEdge
//   maxSpeedFactor
// -----------------------------------------------------
struct Vehicle {
    std::string id;
    NodeId      source;
    NodeId      destination;

    std::vector<NodeId> path;

    int    currentIndexOnPath {0};
    double timeOnCurrentEdge  {0.0};
    double maxSpeedFactor     {1.0};

    Vehicle(const std::string& _id,
            NodeId s,
            NodeId d)
    : id(_id), source(s), destination(d)
    {}
};

#endif // TYPES_H
