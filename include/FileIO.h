#ifndef FILE_IO_H
#define FILE_IO_H

#include <string>
#include <vector>

#include "UrbanFlowGraph.h"
#include "Types.h"

class FileIO {
public:
    static std::vector<Vehicle> loadAll(
        const std::string& folder,
        UrbanFlowGraph& city);

private:
    static void loadIntersections(
        const std::string& file,
        UrbanFlowGraph& city);

    static void loadRoads(
        const std::string& file,
        UrbanFlowGraph& city);

    static std::vector<Vehicle> loadVehicles(
        const std::string& file,
        const UrbanFlowGraph& city);
};

#endif
