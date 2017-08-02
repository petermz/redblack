package com.example.redblack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Tree<T extends Comparable<T>> {
    private static final byte BLACK = 0;
    private static final byte RED = 1;

    private static class Node<T extends Comparable<T>> {
        byte color = RED;
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
        }

        Node(byte black, T value, Node<T> left, Node<T> right) {
            this.color = black;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        Node(Node<T> node) {
            this.color = node.color;
            this.value = node.value;
            this.left = node.left;
            this.right = node.right;
        }

        Node<T> repaint(byte color) {
            Node<T> node = new Node<>(this);
            node.color = color;
            return node;
        }

        Stream.Builder<T> buildStream(Stream.Builder<T> builder) {
            if (left != null) left.buildStream(builder);
            builder.accept(value);
            if (right != null) right.buildStream(builder);
            return builder;
        }
    }

    private final Node<T> root;
    private final int size;

    private Tree(Node<T> root, int size) {
        this.root = root;
        this.size = size;
    }

    public Tree() {
        root = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Stream<T> stream() {
        if (root == null) return Stream.empty();
        return root.buildStream(Stream.builder()).build();
    }

    private void append(List<Node<T>> path, Node<T> n, int dir) {
        if (! path.isEmpty()) {
            Node<T> last = path.get(path.size()-1);
            if (dir < 0) {
                last.left = n;
            } else {
                last.right = n;
            }
        }
        path.add(n);
    }

    private boolean isRed(Node<T> node) {
        return node != null && node.color == RED;
    }

    private void handleOuterCase(List<Node<T>> path, int i, Node<T> n, Node<T> p, Node<T> gp) {
        gp.color = RED;
        p.color = BLACK;
        path.set(i-2, p);
        path.set(i-1, n);
        reattach(path, i, p, gp);
    }

    private void handleInnerCase(List<Node<T>> path, int i, Node<T> n, Node<T> p, Node<T> gp) {
        gp.color = RED;
        n.color = BLACK;
        path.set(i-2, n);
        path.set(i-1, p);
        reattach(path, i, n, gp);
    }

    private void reattach(List<Node<T>> path, int i, Node<T> n, Node<T> gp) {
        if (i > 2) {
            Node<T> ggp = path.get(i-3);
            if (ggp.right == gp) {
                ggp.right = n;
            } else {
                ggp.left = n;
            }
        }
    }

    public Tree<T> insert(T val) {
        // Build the resulting tree
        Node<T> n = root;
        List<Node<T>> path = new ArrayList<>();

        int dir = 0;
        while (n != null) {
            append(path, new Node<>(n), dir);
            dir = val.compareTo(n.value);
            if (dir == 0) {
                return this;
            } else {
                n = dir < 0 ? n.left : n.right;
            }
        }
        append(path, new Node<>(val), dir);

        // Balance the tree
        for (int i = path.size()-1; i > 1; i--) {
            n = path.get(i);
            Node<T> p = path.get(i-1);
            if (n.color == BLACK || p.color == BLACK) {
                break;
            }
            Node<T> gp = path.get(i-2);
            if (gp.left == p) {
                Node<T> u = gp.right;
                if (isRed(u)) {
                    p.color = BLACK;
                    gp.color = RED;
                    gp.right = u.repaint(BLACK);
                    i--;
                } else if (p.left == n) {
                    // Left-Left case
                    gp.left = p.right;
                    p.right = gp;
                    handleOuterCase(path, i, n, p, gp);
                    break;
                } else {
                    // Left-Right case
                    p.right = n.left;
                    gp.left = n.right;
                    n.left = p;
                    n.right = gp;
                    handleInnerCase(path, i, n, p, gp);
                    break;
                }
            } else {
                Node<T> u = gp.left;
                if (isRed(u)) {
                    p.color = BLACK;
                    gp.color = RED;
                    gp.left = u.repaint(BLACK);
                    i--;
                } else if (p.right == n) {
                    // Right-Right case
                    gp.right = p.left;
                    p.left = gp;
                    handleOuterCase(path, i, n, p, gp);
                    break;
                } else {
                    // Right-Left case
                    p.left = n.right;
                    gp.right = n.left;
                    n.right = p;
                    n.left = gp;
                    handleInnerCase(path, i, n, p, gp);
                    break;
                }
            }
        }
        Node<T> r = path.get(0);
        r.color = BLACK;
        return new Tree<>(r, size+1);
    }
}
