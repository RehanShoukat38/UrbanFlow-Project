#include "../include/FileIO.h"
#include "../include/UrbanFlowGraph.h"
#include "../include/Types.h"

#include <fstream>
#include <sstream>
#include <iostream>

using namespace std;

// ---------------------------------------------------------------
// Parse roadType token
// ---------------------------------------------------------------
static RoadType parseRoadType(const string& s)
{
    if (s == "HW"  || s == "HIGHWAY")   return RoadType::HIGHWAY;
    if (s == "ART" || s == "ARTERIAL")  return RoadType::ARTERIAL;
    return RoadType::LOCAL;
}

// ---------------------------------------------------------------
// Load intersections (name,x,y,hasSignal)
// Your CSV might have no header, so we ignore only if detected
// ---------------------------------------------------------------
void FileIO::loadIntersections(const std::string& file,
                               UrbanFlowGraph& city)
{
    ifstream fin(file);
    if (!fin) {
        cerr << "FileIO: cannot open intersections file: " << file << endl;
        return;
    }

    string line;
    while (getline(fin, line))
    {
        if (line.empty()) continue;

        // ignore header-like lines if present
        if (line[0] == '#') continue;
        if (line.find("name") != string::npos) continue;

        stringstream ss(line);

        string name, xs, ys, hs;
        getline(ss, name, ',');
        getline(ss, xs,   ',');
        getline(ss, ys,   ',');
        getline(ss, hs,   ',');

        double x = stod(xs);
        double y = stod(ys);
        bool hasSignal = (hs == "1");

        city.addIntersection(name, x, y, hasSignal);
    }
}

// ---------------------------------------------------------------
// Load roads: from, to, length, time, capacity, type, bidirectional
// ---------------------------------------------------------------
void FileIO::loadRoads(const std::string& file,
                       UrbanFlowGraph& city)
{
    ifstream fin(file);
    if (!fin) {
        cerr << "FileIO: cannot open roads file: " << file << endl;
        return;
    }

    string line;
    while (getline(fin, line))
    {
        if (line.empty()) continue;

        if (line[0] == '#') continue;
        if (line.find("from") != string::npos) continue;

        stringstream ss(line);

        string from, to, ls, ts, cs, typ, bd;
        getline(ss, from, ',');
        getline(ss, to,   ',');
        getline(ss, ls,   ',');
        getline(ss, ts,   ',');
        getline(ss, cs,   ',');
        getline(ss, typ,  ',');
        getline(ss, bd,   ',');

        double length = stod(ls);
        double baseTime  = stod(ts);
        double cap    = stod(cs);
        RoadType rt   = parseRoadType(typ);
        bool bid      = (bd == "1");

        // name-based
        city.addRoad(from, to, length, baseTime, cap, rt, bid);
    }
}

// ---------------------------------------------------------------
// Load vehicles : id,sourceName,destinationName
// Example CSV with names, no indices!
// ---------------------------------------------------------------
std::vector<Vehicle> FileIO::loadVehicles(const std::string& file,
                                          const UrbanFlowGraph& city)
{
    vector<Vehicle> out;
    ifstream fin(file);
    if (!fin) {
        cerr << "FileIO: cannot open vehicles file: " << file << endl;
        return out;
    }

    string line;
    while (getline(fin, line))
    {
        if (line.empty()) continue;

        if (line[0] == '#') continue;
        if (line.find("id") != string::npos) continue;

        stringstream ss(line);

        string id, srcName, dstName;
        getline(ss, id,      ',');
        getline(ss, srcName, ',');
        getline(ss, dstName, ',');

        NodeId s = city.getNodeIdByName(srcName);
        NodeId d = city.getNodeIdByName(dstName);

        out.emplace_back(id, s, d);
    }
    return out;
}

// ---------------------------------------------------------------
// Load all three files: intersections, roads, vehicles
// ---------------------------------------------------------------
std::vector<Vehicle> FileIO::loadAll(const std::string& folder,
                                     UrbanFlowGraph& city)
{
    loadIntersections(folder + "/intersections.csv", city);
    loadRoads        (folder + "/roads.csv",         city);
    return loadVehicles(folder + "/vehicles.csv",    city);
}
