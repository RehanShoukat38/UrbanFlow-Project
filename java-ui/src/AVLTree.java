// AVLTree.java
public class AVLTree {
    private AVLNode root;

    public void insert(String key, VehicleRecord value) {
        root = insertRec(root, key, value);
    }

    private AVLNode insertRec(AVLNode node, String key, VehicleRecord value) {
        if (node == null) return new AVLNode(key, value);
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = insertRec(node.left, key, value);
        else if (cmp > 0) node.right = insertRec(node.right, key, value);
        else node.value = value; // update
        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node);
    }

    public VehicleRecord find(String key) {
        AVLNode cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return cur.value;
            if (cmp < 0) cur = cur.left; else cur = cur.right;
        }
        return null;
    }

    private int height(AVLNode n) { return n == null ? 0 : n.height; }

    private int bf(AVLNode n) { return n == null ? 0 : height(n.left) - height(n.right); }

    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;
        x.right = y; y.left = T2;
        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));
        return x;
    }

    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;
        y.left = x; x.right = T2;
        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));
        return y;
    }

    private AVLNode balance(AVLNode node) {
        int balance = bf(node);
        if (balance > 1 && bf(node.left) >= 0) return rotateRight(node);
        if (balance > 1 && bf(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && bf(node.right) <= 0) return rotateLeft(node);
        if (balance < -1 && bf(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }
}
