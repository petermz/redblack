package com.example.redblack;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;

public class TestBase {
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
            assertArrayEquals(signature(tree.stream()), signature(expected.stream()));
        }
    }

    static int[] signature(Stream<Integer> stream) {
        return stream.mapToInt(Integer::intValue).toArray();
    }

    static Tree<Integer> createTree(IntStream stream, OpChecker checker) {
        stream.forEach(checker::insert);
        return checker.tree;
    }
}
