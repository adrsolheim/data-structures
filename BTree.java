package no.adrsolheim.impl;

/**
 * B+tree - all values stored in leaf/external nodes
 *
 * M-way Search Tree with some rules:
 *  1. ceil(m/2) children
 *  2. root has min(2) children
 *  3. all leaves at same level
 *  4. create/insert bottom up
 *
 *  Inserts can only occur on ***LEAF NODES***.
 *  If there is no room then we split, bottom up
 *  and increase the height by adding **on top of the pile**.
 *
 *  This BTree is implemented as *left-biased*: Split node M/2 to parent
 *  Right-based would be (M/2)+1
 */
public class BTree<K extends Comparable<K>,V> {
    static int M;     // M-1 keys on each level
    int height;                 // n levels above leaf level
    int size;                   // number of entries
    Node root;

    public BTree() {
        M = 4;
        height = 0;
        size = 0;
        root = new Node(0);
    }


    public BTree(int order) {
        if (order < 4 || order % 2 == 1) {
            throw new IllegalArgumentException("The order M must be an even number and larger or equal to 4");
        }
        M = order;
        height = 0;
        size = 0;
        root = new Node(0);
    }

    public V search(Node current, K key, int h) {
        if (h == 0) {
           for (int i = 0; i < current.n_children; i++) {
               if(key.equals(current.entries[i].key)) {
                   return (V) current.entries[i].value;
               }
           }
        } else {
           for (int i = 0; i < current.n_children; i++) {
               if(i+1 == current.n_children || smaller(key, current.entries[i+1].key)) {
                    return search(current.entries[i].next, key, h-1);
               }
           }

        }
        return null;
    }

    public boolean contains(K key) {
        return search(root,key,height) != null;
    }

    public boolean put(K key, V value) {
        int h = height;
        Node rootRightSplit = insert(root, key, value, height);
        if (rootRightSplit != null) {
            Node rootLeftSplit = root;
            Node r = new Node(2);
            r.entries[0] = new Entry(rootLeftSplit.entries[0].key, rootLeftSplit.entries[0].value, rootLeftSplit);
            r.entries[1] = new Entry(rootRightSplit.entries[0].key, rootRightSplit.entries[0].value, rootRightSplit);
            height++;
            root = r;
        }
        return h != height;
    }

    public Node insert(Node current, K key, V value, int h) {
        Entry lifted = null;
        int i;
        // leaf
        if (h == 0) {
            for(i = 0; i < current.n_children; i++) {
                if (smaller(key, current.entries[i].key)) break;
            }
        // internal - traverse to the correct leaf node
        } else {
            for(i = 0; i < current.n_children; i++) {
                if (i+1 == current.n_children || smaller(key, current.entries[i+1].key)) {
                    // i++ is the new position for a key from a potentially splitted node
                    Node split_node = insert(current.entries[i++].next, key, value, h-1);
                    if (split_node == null)
                        return null;
                    lifted = new Entry(split_node.entries[0].key, split_node.entries[0].value, split_node);
                    break;
                }
            }
        }

        // fill in new entry
        for(int j = current.n_children; j > i; j--)
            current.entries[j] = current.entries[j-1];
        current.entries[i] = (lifted == null) ? new Entry(key, value, null) : lifted;
        size++;
        current.n_children++;
        return (current.n_children < M) ? null : split(current);
    }

    // divide entries between two nodes
    private Node split(Node current) {
        Node rightHalf = new Node(M/2);
        for(int i = 0; i < M/2; i++) {
            rightHalf.entries[i] = current.entries[i+(M/2)];
            //current.entries[i+(M/2)] = null;
        }
        current.n_children = M/2;
        return rightHalf;
    }

    private boolean smaller(Comparable k1, Comparable k2) {
        return k1.compareTo(k2) < 0;
    }

    // internal or leaf with up to M-1 entries
    private static class Node<T extends Comparable<T>> {
        int n_children;
        Entry[] entries = new Entry[M]; // leaves room for one more prior to splitting

        public Node(int n) {
           n_children = n;
        }

    }

    // represents an entry/child of a Node
    private static class Entry {
        Comparable key;
        Object value;
        Node next;

        public Entry(Comparable key, Object value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }


    public static void main(String[] args) {
        BTree btree = new BTree();
        btree.put(6, "June");
        btree.put(7, "July");
        System.out.printf("Size %d ",btree.size);
        System.out.printf("Height %d\n",btree.height);
        btree.put(8, "August");
        btree.put(9, "September");
        btree.put(10, "October");
        btree.put(11, "November");
        btree.put(12, "December");
        btree.put(1, "January");
        btree.put(2, "February");
        btree.put(3, "March");
        btree.put(4, "April");
        btree.put(5, "May");
        System.out.printf("Size %d, ",btree.size);
        System.out.printf("Height %d\n\n",btree.height);
        System.out.printf("Search %d: %s\n",2, btree.search(btree.root,2, btree.height));   // February
        System.out.printf("Search %d: %s\n",13, btree.search(btree.root,13, btree.height));  // null
    }
}
