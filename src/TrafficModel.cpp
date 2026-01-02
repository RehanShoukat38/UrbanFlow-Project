#include "../include/TrafficModel.h"
#include <cmath>

// ----------------------------------------------------------
// Constructor
// ----------------------------------------------------------

TrafficModel::TrafficModel(
    CongestionFunction f,
    double alpha_,
    double beta_)
    : func(f), alpha(alpha_), beta(beta_)
{}

// ----------------------------------------------------------

void TrafficModel::setCongestionFunction(CongestionFunction f) {
    func = f;
}

void TrafficModel::setBPRParameters(double alpha_, double beta_) {
    alpha = alpha_;
    beta  = beta_;
}

// ----------------------------------------------------------
// Main travel time function
// ----------------------------------------------------------

double TrafficModel::computeTravelTime(
        const Edge& edge,
        double vehicleSpeedFactor) const
{
    double base = edge.baseTravelTime / vehicleSpeedFactor;

    switch (func)
    {
    case CongestionFunction::LINEAR:
        return base * linearCongestion(edge);

    case CongestionFunction::BPR:
        return base * bprCongestion(edge);

    case CongestionFunction::EXPONENTIAL:
        return base * exponentialCongestion(edge);

    default:
        return base;
    }
}

// ----------------------------------------------------------
// Linear congestion (simple)
// travelTime = base * (1 + flow/capacity)
// ----------------------------------------------------------

double TrafficModel::linearCongestion(const Edge& edge) const
{
    if (edge.capacity <= 0.0)
        return 1000.0; // saturate

    return 1.0 + (edge.currentFlow / edge.capacity);
}

// ----------------------------------------------------------
// BPR formula (realistic highway congestion model)
//
// travelTime = base * (1 + alpha * (flow/capacity)^beta)
//
// Typical: alpha=0.15, beta=4
// ----------------------------------------------------------

double TrafficModel::bprCongestion(const Edge& edge) const
{
    if (edge.capacity <= 0.0)
        return 1000.0;

    double ratio = edge.currentFlow / edge.capacity;

    return 1.0 + alpha * std::pow(ratio, beta);
}

// ----------------------------------------------------------
// Exponential congestion
//
// travelTime = base * (1 + e^(flow/capacity) - 1)
// ----------------------------------------------------------

double TrafficModel::exponentialCongestion(const Edge& edge) const
{
    if (edge.capacity <= 0.0)
        return 1000.0;

    double ratio = edge.currentFlow / edge.capacity;

    return std::exp(ratio);
}
