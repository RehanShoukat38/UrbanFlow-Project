#include <algorithm>

// ===============================
// AVL NODE DEFINITION
// ===============================
struct AVLNode {
    int key;
    int height;
    AVLNode* left;
    AVLNode* right;

    AVLNode(int k) {
        key = k;
        height = 1;
        left = nullptr;
        right = nullptr;
    }
};

// ===============================
// AVL TREE CLASS
// ===============================
class AVLTree {

private:
    AVLNode* root;

    // -------- HEIGHT --------
    int height(AVLNode* node) {
        return (node == nullptr) ? 0 : node->height;
    }

    // -------- BALANCE FACTOR --------
    int getBalance(AVLNode* node) {
        if (node == nullptr) return 0;
        return height(node->left) - height(node->right);
    }

    // -------- RIGHT ROTATION --------
    AVLNode* rightRotate(AVLNode* y) {
        AVLNode* x = y->left;
        AVLNode* T2 = x->right;

        x->right = y;
        y->left = T2;

        y->height = 1 + std::max(height(y->left), height(y->right));
        x->height = 1 + std::max(height(x->left), height(x->right));

        return x;
    }

    // -------- LEFT ROTATION --------
    AVLNode* leftRotate(AVLNode* x) {
        AVLNode* y = x->right;
        AVLNode* T2 = y->left;

        y->left = x;
        x->right = T2;

        x->height = 1 + std::max(height(x->left), height(x->right));
        y->height = 1 + std::max(height(y->left), height(y->right));

        return y;
    }

    // -------- INSERT --------
    AVLNode* insert(AVLNode* node, int key) {

        // Normal BST insertion
        if (node == nullptr)
            return new AVLNode(key);

        if (key < node->key)
            node->left = insert(node->left, key);
        else if (key > node->key)
            node->right = insert(node->right, key);
        else
            return node; // duplicate keys not allowed

        // Update height
        node->height = 1 + std::max(height(node->left), height(node->right));

        // Balance factor
        int balance = getBalance(node);

        // ---- ROTATION CASES ----

        // Left Left
        if (balance > 1 && key < node->left->key)
            return rightRotate(node);

        // Right Right
        if (balance < -1 && key > node->right->key)
            return leftRotate(node);

        // Left Right
        if (balance > 1 && key > node->left->key) {
            node->left = leftRotate(node->left);
            return rightRotate(node);
        }

        // Right Left
        if (balance < -1 && key < node->right->key) {
            node->right = rightRotate(node->right);
            return leftRotate(node);
        }

        return node;
    }

    // -------- SEARCH (RECURSIVE) --------
    AVLNode* search(AVLNode* node, int key) {
        if (node == nullptr || node->key == key)
            return node;

        if (key < node->key)
            return search(node->left, key);

        return search(node->right, key);
    }

public:
    // -------- CONSTRUCTOR --------
    AVLTree() {
        root = nullptr;
    }

    // -------- INSERT PUBLIC --------
    void insertKey(int key) {
        root = insert(root, key);
    }

    // -------- SEARCH PUBLIC --------
    bool searchKey(int key) {
        return search(root, key) != nullptr;
    }
};

// ===============================
// MAIN (NO I/O — LOGIC ONLY)
// ===============================
int main() {

    AVLTree tree;

    // Sample insertions
    tree.insertKey(50);
    tree.insertKey(30);
    tree.insertKey(70);
    tree.insertKey(20);
    tree.insertKey(40);
    tree.insertKey(60);
    tree.insertKey(80);

    // Sample searches (results stored, not printed)
    bool found1 = tree.searchKey(40); // true
    bool found2 = tree.searchKey(25); // false

    return 0;
}
