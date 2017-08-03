package com.example.redblack;

import static com.example.redblack.Tree.Side.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Tree<T extends Comparable<T>> {
    private static final byte BLACK = 0;
    private static final byte RED = 1;

    enum Side {
        LEFT, RIGHT;

        Side opposite() {
            return Side.values()[1 - ordinal()];
        }
    }

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

        Node<T> child(Side side) {
            return side == LEFT ? left : right;
        }

        void setChild(Side side, Node<T> child) {
            if (side == LEFT) {
                left = child;
            } else {
                right = child;
            }
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
        return size == 0;
    }

    public Stream<T> stream() {
        return root == null ? Stream.empty() : root.buildStream(Stream.builder()).build();
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
        reattach3(path, i, p, gp);
    }

    private void handleInnerCase(List<Node<T>> path, int i, Node<T> n, Node<T> p, Node<T> gp) {
        gp.color = RED;
        n.color = BLACK;
        path.set(i-2, n);
        path.set(i-1, p);
        reattach3(path, i, n, gp);
    }

    private void reattach3(List<Node<T>> path, int i, Node<T> n, Node<T> gp) {
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
        List<Node<T>> path = new ArrayList<>();///?

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
            Side left = gp.left == p ? LEFT : RIGHT;
            Side right = left.opposite();
            // The code below uses `left` and `right` for readability; however, it also handles
            // the opposite case where `left` actually means RIGHT and `right` means LEFT
            Node<T> u = gp.child(right);
            if (isRed(u)) {
                // Red uncle
                p.color = BLACK;
                gp.color = RED;
                gp.setChild(right, u.repaint(BLACK));
                i--;
            } else if (p.child(left) == n) {
                // Outer case: Left-Left or Right-Right
                gp.setChild(left, p.child(right));
                p.setChild(right, gp);
                handleOuterCase(path, i, n, p, gp);
                break;
            } else {
                // Inner case: Left-Right or Right-Left
                p.setChild(right, n.child(left));
                gp.setChild(left, n.child(right));
                n.setChild(left, p);
                n.setChild(right, gp);
                handleInnerCase(path, i, n, p, gp);
                break;
            }
        }
        Node<T> r = path.get(0);
        r.color = BLACK;
        return new Tree<>(r, size+1);
    }

    private void reattach2(List<Node<T>> path, int i, Node<T> n, Node<T> p) {
        if (i > 1) {
            Node<T> gp = path.get(i-2);
            if (gp.left == p) {
                gp.left = n;
            } else {
                gp.right = n;
            }
        }
        path.set(i-1, n);
    }

    private void handleDoubleBlackCase(List<Node<T>> path) {
        for (int i = path.size()-1; i >= 0; i--) {
            Node<T> n = path.get(i);
            if (i == 0) {
                n.color = BLACK;
                return;
            }
            Node<T> p = path.get(i-1);
            Side left = p.left == n ? LEFT : RIGHT;
            Side right = left.opposite();
            // The code below uses `left` and `right` for readability; however, it also handles
            // the opposite case where `left` actually means RIGHT and `right` means LEFT
            Node<T> s = p.child(right);
            if (isRed(s.child(right))) {
                // Outer case
                p.setChild(right, s.child(left));
                s = s.repaint(p.color);
                s.setChild(left, p);
                s.setChild(right, s.child(right).repaint(BLACK));
                reattach2(path, i, s, p);
                n.color = BLACK;
                p.color = BLACK;
                return;
            } else if (isRed(s.child(left))) {
                // Inner case
                p.setChild(right, s.child(left).child(left));
                Node<T> np = s.child(left).repaint(p.color);
                np.setChild(left, p);
                s = s.repaint(BLACK);
                s.setChild(left, s.child(left).child(right));
                np.setChild(right, s);
                reattach2(path, i, np, p);
                n.color = BLACK;
                p.color = BLACK;
                return;
            }
            if (s.color == BLACK) {
                // Black sibling
                p.setChild(right, s.repaint(RED));
                if (p.color == RED) {
                    p.color = BLACK;
                    return;
                }
            } else {
                // Red sibling
                p.setChild(right, s.child(left));
                s = s.repaint(p.color);
                s.setChild(left, p);
                reattach2(path, i, s, p);
                p.color = RED;
                path.set(i, p);
                if (i < path.size()-1) {
                    path.set(i+1, n);
                } else {
                    path.add(n);
                }
                i += 2;
            }
        }
    }

    private Node<T> append(List<Node<T>> path, Node<T> n) {
        Node<T> copy = new Node<>(n);
        if (!path.isEmpty()) {
            Node<T> last = path.get(path.size()-1);
            if (last.left == n) {
                last.left = copy;
            } else {
                last.right = copy;
            }
        }
        path.add(copy);
        return copy;
    }

    public Tree<T> remove(T val) {
        Node<T> n = root;
        List<Node<T>> path = new ArrayList<>();///type?

        // Build the resulting tree
        while (n != null) {
            append(path, n);
            int dir = val.compareTo(n.value);
            if (dir == 0) {
                break;
            }
            n = dir < 0 ? n.left : n.right;
        }
        if (n == null) {
            return this;
        }

        n = path.get(path.size()-1);
        if (n.left != null && n.right != null) {
            // Inner node, find left neighbour
            Node<T> v = n;
            for (n = n.left; n.right != null; n = n.right) {
                append(path, n);
            }
            append(path, n).value = v.value;
            v.value = n.value;
        }

        // Balance the tree
        n = path.get(path.size()-1);
        if (n.color == RED) {
            // Drop red leaf
            Node<T> p = path.get(path.size()-2);
            drop(p, n);
            return new Tree<>(path.get(0), size-1);
        } else {
            if (n.left != null || n.right != null) {
                // Replace node with its child
                Node<T> src = n.left != null ? n.left : n.right;
                n.color = BLACK;
                n.value = src.value;
                n.left = src.left;
                n.right = src.right;
                return new Tree<>(path.get(0), size-1);
            } else if (path.size() == 1) {
                // Remove root
                return new Tree<>();
            } else {
                // Double black
                Node<T> p = path.get(path.size()-2);
                handleDoubleBlackCase(path);
                drop(p, n);
            }
        }
        return new Tree<>(path.get(0), size-1);
    }

    private void drop(Node<T> p, Node<T> n) {
        if (p.left == n) {
            p.left = null;
        } else {
            p.right = null;
        }
    }
}
