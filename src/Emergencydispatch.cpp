#include <queue>
#include <string>

using namespace std;

// Emergency request model
struct Emergency {
    string vehicleId;
    int severity;
};

// Comparator: higher severity = higher priority
struct Compare {
    bool operator()(Emergency a, Emergency b) {
        return a.severity < b.severity;
    }
};

class EmergencyDispatch {
private:
    priority_queue<Emergency, vector<Emergency>, Compare> pq;

public:
    // Add emergency request
    void addEmergency(string id, int severity) {
        pq.push({id, severity});
    }

    // Dispatch highest priority emergency
    Emergency dispatchNext() {
        Emergency top = pq.top();
        pq.pop();
        return top;
    }

    // Check if queue is empty
    bool isEmpty() {
        return pq.empty();
    }

    // Get current size of queue
    int size() {
        return pq.size();
    }
};
