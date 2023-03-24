package no.adrsolheim.impl;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * AVL Tree height is considered from leaf to root.
 * In other words, root is the highest point of the tree.
 * Height decrease downwards towards leaf nodes (h = 0)
 *      root.height > child.height
 *
 * Rebalance tree when balance factor extends beyond [-1,1] range
 *
 * Number of operations is constant w.r.t. N, roughly
 *    balanceFactor:   2   (parent + child)
 *    rotation:        2   (LR, RL)
 *    updateHeight:    2   (parent + child)
 *
 * Insert:  O(log n)
 * Search:  O(log n)
 * Delete:  O(log n)
 */
public class AVLTree {

    Node root;
    int size;

    public AVLTree() {
        root = null;
        size = 0;
    }

    public void insert(Comparable key, Object value) {
        root = insertNode(root, new Node(key, value));
    }

    public void delete(Comparable key) {
        root = deleteNode(root, key);
    }

    public Node deleteNode(Node current, Comparable key) {
        if (current == null)
            throw new NoSuchElementException(String.format("Tree contains no such element: key=%s", key.toString()));
        if (key.equals(current.key)) {
            // leaf
            if (current.left == null && current.right == null){
                return null;
            }
            // one child
            else if (current.left == null || current.right == null){
                return (current.right == null) ? current.left : current.right;
            }
            // two children
            else {
                Node smallest = removeSmallest(current.right);
                smallest.left = current.left;
                //smallest.right = current.right;
                smallest.height = current.height;
                return smallest;
            }
        } else {
            // left
            if (key.compareTo(current.key) < 0) current.left = deleteNode(current.left, key);
            // right
            else current.right = deleteNode(current.right, key);
        }
        updateHeight(current);
        current = balance(current);
        return current;
    }
    private Node removeSmallest (Node current) {
        Node parent = current;
        while(current.left != null) {
            parent = current;
            current = current.left;
        }
        parent.left = (current.right != null) ? current.right : null;
        updateHeight(parent);
        return current;
    }
    public Node insertNode(Node current, Node node) {
        if (current == null) {
            size++;
            return node;
        }
        if (node.key.compareTo(current.key) < 0) {
            current.left = insertNode(current.left, node);
        } else {
            current.right = insertNode(current.right, node);
        }

        updateHeight(current);
        current = balance(current);
        return current;
    }

    private void updateHeight(Node node) {
        int leftSubtreeHeight = height(node.left);
        int rightSubtreeHeight = height(node.right);

        node.height = 1 + Math.max(leftSubtreeHeight, rightSubtreeHeight);
    }

    private int height(Node node) {
        return (node != null) ? node.height : -1;
    }

    // returns a reference to the new root of subtree
    private Node balance(Node node) {
        int blncFactor = balanceFactor(node);
        // left-heavy
        if (blncFactor < -1) {
            // LL
            if (balanceFactor(node.left) <= 0) {
                node = rotateRight(node);
            }
            // LR
            else {
                node.left = rotateLeft(node.left);
                node = rotateRight(node);
            }
        }
        // right-heavy
        else if (blncFactor > 1){
            // RL
            if (balanceFactor(node.right) <= 0) {
                node.right = rotateRight(node.right);
                node = rotateLeft(node);
            }
            // RR
            else {
                node = rotateLeft(node);
            }
        }
        return node;
    }

    private Node rotateLeft(Node node) {
        Node r = node.right;
        node.right = r.left;
        r.left = node;

        updateHeight(r.left);
        updateHeight(r);
        return r;

    }
    private Node rotateRight(Node node) {
        Node r = node.left;
        node.left = r.right;
        r.right = node;

        updateHeight(r.right);
        updateHeight(r);
        return r;
    }

    private int balanceFactor(Node node) {
        return height(node.right) - height(node.left);
    }

    public int size() {
        return size;
    }

    private static class Node {
        int height;
        Node left;
        Node right;
        Comparable key;
        Object value;
        public Node(Comparable key, Object value) {
            this.height = 0;
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }

        @Override
        public String toString() {
            return String.format("(k=%d, %s)",key, value.toString());
        }
    }

    // inorder
    private void toString(Node current, StringBuilder sb) {
        if (current == null)
            return;
        toString(current.left, sb);
        sb.append(String.format("%s ",current.toString()));
        toString(current.right, sb);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("AVLTree(size=%d, height=%d) -> ", size, root.height));
        toString(root, sb);
        return sb.toString();
    }

    public String levels() {
        if (root == null)
            return "Empty AVL tree";
        StringBuilder sb = new StringBuilder();
        int h = root.height;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.height < h) {
                sb.append(System.getProperty("line.separator"));
                h = current.height;
            }
            sb.append("%d ".formatted(current.key));
            if (current.left != null) queue.add(current.left);
            if (current.right != null) queue.add(current.right);
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        tree.insert(12, "December");
        tree.insert(11, "November");
        tree.insert(10, "October");
        tree.insert(9, "September");
        tree.insert(8, "August");
        tree.insert(7, "July");
        System.out.println(tree.toString());
        System.out.println(tree.levels());

        tree.delete(10);
        tree.delete(9);
        tree.delete(12);
        tree.delete(11);
        System.out.println(tree.toString());
        System.out.println(tree.levels());
    }
}
