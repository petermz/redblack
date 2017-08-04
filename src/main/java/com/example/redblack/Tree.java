package com.example.redblack;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Tree<T extends Comparable<T>> {
    private static final boolean BLACK = true;
    private static final boolean RED = false;

    private static final boolean LEFT = false;
    private static final boolean RIGHT = true;

    private static class Stack<E> extends LinkedList<E> {}

    private static class Node<T extends Comparable<T>> {
        boolean color = RED;
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
        }

        Node(Node<T> node) {
            this.color = node.color;
            this.value = node.value;
            this.left = node.left;
            this.right = node.right;
        }

        Node<T> repaint(boolean color) {
            Node<T> node = new Node<>(this);
            node.color = color;
            return node;
        }

        Node<T> child(boolean side) {
            return side == LEFT ? left : right;
        }

        void setChild(boolean side, Node<T> child) {
            if (side == LEFT) {
                left = child;
            } else {
                right = child;
            }
        }
    }


    private final Node<T> root;
    private final int size;

    private Tree(Node<T> root, int size) {
        this.root = root;
        this.size = size;
    }

    /**
     * Creates an empty Tree.
     */
    public Tree() {
        root = null;
        size = 0;
    }

    /**
     * Returns size of the tree.
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if the tree is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    private boolean contains(Node<T> node, T value) {
        if (node == null) return false;
        int cmp = value.compareTo(node.value);
        return (cmp == 0) ||
               (cmp < 0 ? contains(node.left, value) : contains(node.right, value));
    }

    /**
     * Returns {@code true} if this element is contained in the tree.
     */
    public boolean contains(T value) {
        return contains(root, value);
    }

    /**
     * Returns an iterator over the contents of the tree.
     */
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Stack<Node<T>> path = new Stack<>();

            {
                for (Node<T> n = root; n != null; n = n.left) {
                    path.push(n);
                }
            }

            public boolean hasNext() {
                return ! path.isEmpty();
            }

            public T next() {
                if (! hasNext()) {
                    throw new NoSuchElementException();
                }
                Node<T> v = path.peek();
                Node<T> n = v.right;
                if (n != null) {
                    path.push(n);
                    for (; n.left != null; n = n.left) {
                        path.push(n.left);
                    }
                } else {
                    Node<T> p;
                    do {
                        n = path.pop();
                        p = path.peek();
                    } while (p != null && p.right == n);
                }
                return v.value;
            }
        };
    }

    // Insert

    private void push(Node<T> n, Stack<Node<T>> path, int dir) {
        if (! path.isEmpty()) {
            Node<T> head = path.peek();
            if (dir < 0) {
                head.left = n;
            } else {
                head.right = n;
            }
        }
        path.push(n);
    }

    private boolean isRed(Node<T> node) {
        return node != null && node.color == RED;
    }

    private void handleOuterCase(Stack<Node<T>> path, Node<T> n, Node<T> p, Node<T> gp) {
        gp.color = RED;
        p.color = BLACK;
        path.set(2, p);
        path.set(1, n);
        reattach3(path, p, gp);
    }

    private void handleInnerCase(Stack<Node<T>> path, Node<T> n, Node<T> p, Node<T> gp) {
        gp.color = RED;
        n.color = BLACK;
        path.set(2, n);
        path.set(1, p);
        reattach3(path, n, gp);
    }

    private void reattach3(Stack<Node<T>> path, Node<T> n, Node<T> gp) {
        if (path.size() > 3) {
            Node<T> ggp = path.get(3);
            if (ggp.right == gp) {
                ggp.right = n;
            } else {
                ggp.left = n;
            }
        }
    }

    /**
     * Returns a copy of the tree with an element inserted.
     * If the element is already in the tree, returns {@code this}.
     */
    public Tree<T> insert(T value) {
        // Build the resulting tree
        Node<T> n = root;
        Stack<Node<T>> path = new Stack<>();

        int dir = 0;
        while (n != null) {
            push(new Node<>(n), path, dir);
            dir = value.compareTo(n.value);
            if (dir == 0) {
                return this;
            } else {
                n = dir < 0 ? n.left : n.right;
            }
        }
        push(new Node<>(value), path, dir);

        // Balance the tree
        for (; path.size() > 2; path.pop()) {
            n = path.peek();
            Node<T> p = path.get(1);
            if (n.color == BLACK || p.color == BLACK) {
                break;
            }
            Node<T> gp = path.get(2);
            boolean left = gp.left == p ? LEFT : RIGHT;
            boolean right = !left;
            // The code below uses `left` and `right` for readability; however, it also handles
            // the opposite case where `left` actually means RIGHT and `right` means LEFT
            Node<T> u = gp.child(right);
            if (isRed(u)) {
                // Red uncle
                p.color = BLACK;
                gp.color = RED;
                gp.setChild(right, u.repaint(BLACK));
                path.pop();
            } else if (p.child(left) == n) {
                // Outer case: Left-Left or Right-Right
                gp.setChild(left, p.child(right));
                p.setChild(right, gp);
                handleOuterCase(path, n, p, gp);
                break;
            } else {
                // Inner case: Left-Right or Right-Left
                p.setChild(right, n.child(left));
                gp.setChild(left, n.child(right));
                n.setChild(left, p);
                n.setChild(right, gp);
                handleInnerCase(path, n, p, gp);
                break;
            }
        }
        Node<T> r = path.getLast();
        r.color = BLACK;
        return new Tree<>(r, size+1);
    }


    // Remove

    private void reattach2(Stack<Node<T>> path, Node<T> n, Node<T> p) {
        if (path.size() > 1) {
            Node<T> gp = path.get(1);
            if (gp.left == p) {
                gp.left = n;
            } else {
                gp.right = n;
            }
        }
        path.set(0, n);
    }

    private Node<T> handleDoubleBlackCase(Stack<Node<T>> path) {
        while (true) {
            Node<T> n = path.pop();
            if (path.isEmpty()) {
                n.color = BLACK;
                return n;
            }
            Node<T> p = path.peek();
            boolean left = p.left == n ? LEFT : RIGHT;
            boolean right = !left;
            // The code below uses `left` and `right` for readability; however, it also handles
            // the opposite case where `left` actually means RIGHT and `right` means LEFT
            Node<T> s = p.child(right);
            if (isRed(s.child(right))) {
                // Outer case
                p.setChild(right, s.child(left));
                s = s.repaint(p.color);
                s.setChild(left, p);
                s.setChild(right, s.child(right).repaint(BLACK));
                reattach2(path, s, p);
                n.color = BLACK;
                p.color = BLACK;
                return path.getLast();
            } else if (isRed(s.child(left))) {
                // Inner case
                p.setChild(right, s.child(left).child(left));
                Node<T> np = s.child(left).repaint(p.color);
                np.setChild(left, p);
                s = s.repaint(BLACK);
                s.setChild(left, s.child(left).child(right));
                np.setChild(right, s);
                reattach2(path, np, p);
                n.color = BLACK;
                p.color = BLACK;
                return path.getLast();
            }
            if (s.color == BLACK) {
                // Black sibling
                p.setChild(right, s.repaint(RED));
                if (p.color == RED) {
                    p.color = BLACK;
                    return path.getLast();
                }
            } else {
                // Red sibling
                p.setChild(right, s.child(left));
                s = s.repaint(p.color);
                s.setChild(left, p);
                reattach2(path, s, p);
                p.color = RED;
                path.push(p);
                path.push(n);
            }
        }
    }

    private Node<T> push(Node<T> n, Stack<Node<T>> path) {
        Node<T> copy = new Node<>(n);
        if (!path.isEmpty()) {
            Node<T> head = path.peek();
            if (head.left == n) {
                head.left = copy;
            } else {
                head.right = copy;
            }
        }
        path.push(copy);
        return copy;
    }

    private void dropChild(Node<T> p, Node<T> n) {
        if (p.left == n) {
            p.left = null;
        } else {
            p.right = null;
        }
    }

    /**
     * Returns a copy of the tree with an element removed.
     * If the tree does not contain this element, returns {@code this}.
     */
    public Tree<T> remove(T value) {
        Node<T> n = root;
        Stack<Node<T>> path = new Stack<>();

        // Build the resulting tree
        while (n != null) {
            push(n, path);
            int dir = value.compareTo(n.value);
            if (dir == 0) {
                break;
            }
            n = dir < 0 ? n.left : n.right;
        }
        if (n == null) {
            return this;
        }

        n = path.peek();
        if (n.left != null && n.right != null) {
            // Inner node, find left neighbour
            Node<T> v = n;
            for (n = n.left; n.right != null; n = n.right) {
                push(n, path);
            }
            push(n, path).value = v.value;
            v.value = n.value;
        }

        // Balance the tree
        n = path.peek();
        if (n.color == RED) {
            // Drop red leaf
            Node<T> p = path.get(1);
            dropChild(p, n);
            return new Tree<>(path.getLast(), size-1);
        } else {
            if (n.left != null || n.right != null) {
                // Replace node with its child
                Node<T> src = n.left != null ? n.left : n.right;
                n.color = BLACK;
                n.value = src.value;
                n.left = src.left;
                n.right = src.right;
                return new Tree<>(path.getLast(), size-1);
            } else if (path.size() == 1) {
                // Remove root
                return new Tree<>();
            } else {
                // Double black
                Node<T> p = path.get(1);
                Node<T> nr = handleDoubleBlackCase(path);
                dropChild(p, n);
                return new Tree<>(nr, size-1);
            }
        }
    }
}
