// AVLNode.java
class AVLNode {
    String key;            // license number (unique)
    VehicleRecord value;
    AVLNode left, right;
    int height;

    AVLNode(String k, VehicleRecord v) {
        key = k;
        value = v;
        height = 1;
    }
}
