// VehicleStore.java
import java.io.*;
import java.util.*;

/**
 * VehicleStore: Small AVL keyed by license number (String).
 * Stores vehicle records in memory and supports load/save to CSV file.
 *
 * File path used: java-ui/data/registered_vehicles.csv
 */
public class VehicleStore {

    private static final String STORAGE_PATH = "java-ui/data/registered_vehicles.csv";

    private static class Node {
        String key; // license number
        Vehicle value;
        Node left, right;
        int height;
        Node(String k, Vehicle v) { key = k; value = v; height = 1; }
    }

    private Node root = null;

    // Public API
    public synchronized boolean insert(Vehicle v) {
        if (v == null || v.getLicenseNumber() == null) return false;
        if (search(v.getLicenseNumber()) != null) return false; // duplicate license not allowed
        root = insertNode(root, v.getLicenseNumber(), v);
        return true;
    }

    public synchronized Vehicle search(String license) {
        Node cur = root;
        while (cur != null) {
            int cmp = license.compareToIgnoreCase(cur.key);
            if (cmp == 0) return cur.value;
            cur = cmp < 0 ? cur.left : cur.right;
        }
        return null;
    }

    public synchronized List<Vehicle> inOrderList() {
        List<Vehicle> out = new ArrayList<>();
        inOrder(root, out);
        return out;
    }

    // Load from disk (overwrites current store)
    public synchronized void loadFromFile() {
        root = null;
        File f = new File(STORAGE_PATH);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Vehicle v = Vehicle.fromCSV(line);
                if (v != null) insert(v);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Append single vehicle and save (atomic append)
    public synchronized boolean saveAppend(Vehicle v) {
        try {
            File dir = new File("java-ui/data");
            if (!dir.exists()) dir.mkdirs();
            try (FileWriter fw = new FileWriter(STORAGE_PATH, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(v.toCSV());
                bw.newLine();
                bw.flush();
            }
            // insert into memory too
            insert(v);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Save entire store (overwrite)
    public synchronized void saveAllToFile() {
        try {
            File dir = new File("java-ui/data");
            if (!dir.exists()) dir.mkdirs();
            try (FileWriter fw = new FileWriter(STORAGE_PATH, false);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                for (Vehicle v : inOrderList()) {
                    bw.write(v.toCSV());
                    bw.newLine();
                }
                bw.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ----------------- AVL internals -----------------

    private Node insertNode(Node node, String key, Vehicle value) {
        if (node == null) return new Node(key, value);
        int cmp = key.compareToIgnoreCase(node.key);
        if (cmp < 0) node.left = insertNode(node.left, key, value);
        else node.right = insertNode(node.right, key, value);
        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = balance(node);
        // left heavy
        if (balance > 1) {
            if (key.compareToIgnoreCase(node.left.key) < 0) {
                return rotateRight(node);
            } else {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        }
        // right heavy
        if (balance < -1) {
            if (key.compareToIgnoreCase(node.right.key) > 0) {
                return rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }
        return node;
    }

    private int height(Node n) { return n == null ? 0 : n.height; }
    private int balance(Node n) { return n == null ? 0 : height(n.left) - height(n.right); }
    private Node rotateRight(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));
        return x;
    }
    private Node rotateLeft(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));
        return y;
    }

    private void inOrder(Node n, List<Vehicle> out) {
        if (n == null) return;
        inOrder(n.left, out);
        out.add(n.value);
        inOrder(n.right, out);
    }
}
