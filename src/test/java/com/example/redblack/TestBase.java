package com.example.redblack;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertArrayEquals;

class TestBase {
    static class OpChecker {
        Tree<Integer> tree = new Tree<>();
        Set<Integer> expected = new TreeSet<>();

        Tree insert(int n) {
            tree = tree.insert(n);
            expected.add(n);
            check();
            return tree;
        }

        Tree remove(int n) {
            tree = tree.remove(n);
            expected.remove(n);
            check();
            return tree;
        }

        void check() {
            assertArrayEquals(signature(tree), signature(expected.iterator()));
        }

        Tree<Integer> createTree(IntStream stream) {
            stream.forEach(this::insert);
            return tree;
        }
    }

    private static int[] signature(Iterator<Integer> it) {
        final Iterable<Integer> iterable = () -> it;
        return StreamSupport.stream(iterable.spliterator(), false).mapToInt(Integer::intValue).toArray();
    }

    protected static int[] signature(Tree<Integer> tree) {
        return signature(tree.iterator());
    }

    protected static IntStream randomStream(int limit) {
        Random rand = new Random();
        return IntStream.iterate(0, n -> rand.nextInt(limit));
    }
}
