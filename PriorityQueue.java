package no.adrsolheim.impl;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PriorityQueue <T> {
    private Node<T>[] elements;
    private int size;

    PriorityQueue() {
        this.elements = new Node[100];
        this.size = 0;
    }
    PriorityQueue(int cap) {
        this.elements = new Node[cap];
        this.size = 0;
    }

    public boolean add(T element, int priority) {
        if (element == null)
            return false;
        elements[size] = new Node(element, priority);
        moveUpward(size);
        size++;

        if (loadFactor() > 0.75) {
            resizeArray();
        }
        return true;
    }

    private void moveUpward(int idx) {
        if (idx == 0)
            return;
        int p = parent(idx);
        int i = idx;
        Node current = elements[i];
        while(i > 0 && elements[i].compareTo(elements[p]) > 0) {
            Node moveDown = elements[p];
            elements[p] = current;
            elements[i] = moveDown;
            i = p;
            p = parent(p);
        }
    }

    private void moveDownard() {
        if (size < 2)
            return;
        int p = 0;
        int c = larger(leftChild(p), rightChild(p));
        Node current = elements[p];
        while(c != -1 && elements[p].compareTo(elements[c]) < 0) {
            Node moveUp = elements[c];
            elements[c] = current;
            elements[p] = moveUp;
            p = c;
            c = larger(leftChild(p), rightChild(p));
        }
    }

    private int larger(int l, int r) {
        if (l >= size && r >= size)
            return -1;
        if (l >= size || r >= size)
            return r >= size ? l : r;
        return elements[l].compareTo(elements[r]) > 0 ? l : r;


    }

    public T pop() {
        if (size == 0)
            return null;
        T largest = elements[0].value;
        // last element temporarily fills vacant spot
        size--;
        elements[0] = elements[size];
        // move element with highest priority back on top
        moveDownard();
        return largest;
    }


    private int parent(int i) {
        return i/2;
    }
    private int leftChild(int i) {
        return 2*i + 1;
    }
    private int rightChild(int i) {
        return 2*i + 2;
    }


    private void resizeArray() {
        Node<T>[] doubleSizedArr = new Node[elements.length*2];
        for(int i = 0; i < size; i++) {
            doubleSizedArr[i] = elements[i];
        }
        elements = doubleSizedArr;
    }


    private double loadFactor() {
        return elements.length > 0 ? size() / elements.length : 1;
    }

    public int size() {
        return this.size;
    }

    private static class Node<T> implements Comparable<Node<T>> {
        T value;
        int priority;
        public Node(T value, int priority) {
            this.value = value;
            this.priority = priority;
        }

        @Override
        public int compareTo(Node<T> other) {
            if (this.priority == other.priority)
                return 0;
            return this.priority < other.priority ? -1 : 1;
        }

        @Override
        public String toString() {
            return String.format("(%s, prio=%d)", value, priority);
        }

    }

    public static void main(String[] args) {
        PriorityQueue<String> pq = new PriorityQueue<>();
        List<Node<String>> nodes = new ArrayList<>();
        List<Integer> prio = Stream.of(23, 59, 81, 88, 25, 76, 67, 97, 78, 65).collect(Collectors.toList());
        List<String> strings =
                Stream.of("Tyrannosaurus Rex", "Triceratops", "Velociraptor", "Stegosaurus",
                        "«Spinosaurus»", "Archaeopteryx", "Brachiosaurus", "«Allosaurus»",
                        "Apatosaurus", "Dilophosaurus").collect(Collectors.toList());
        for(int i = 0; i < prio.size(); i++){
            pq.add(strings.get(i), prio.get(i));
            nodes.add(new Node(strings.get(i), prio.get(i)));
        }

        Collections.sort(nodes, new Comparator<Node<String>>() {
            @Override
            public int compare(Node<String> o1, Node<String> o2) {
                if (o1.priority == o2.priority)
                    return 0;
                // descending order
                return o1.priority > o2.priority ? -1 : 1;
            }
        });

        System.out.println("Desired order:\n-----------------------------");
        for (Node n : nodes)
            System.out.println(n);
        System.out.println("\nResult:\n-----------------------------");

        String biggest = pq.pop();
        while (biggest != null) {
            System.out.println(biggest);
            biggest = pq.pop();
        }

    }

}
