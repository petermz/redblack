package com.example.redblack;

import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class TreeSpec {

    private static class OpChecker {
        final static int[] INT_ARRAY = new int[0];

        Tree<Integer> tree = new Tree<>();
        Set<Integer> expected = new TreeSet<>();

        Tree insert(int n) {
            tree = tree.insert(n);
            expected.add(n);
            check();
            return tree;
        }

        int[] signature(Stream<Integer> stream) {
            return stream.mapToInt(Integer::intValue).toArray();
        }

        void check() {
            assertArrayEquals(signature(tree.stream()), signature(expected.stream()));
        }
    }

    @Test
    public void testInsert() {
        final int N = 1000;

        // Natural order
        OpChecker checker = new OpChecker();
        IntStream.range(0, N).forEach(checker::insert);
        assertFalse(checker.tree.isEmpty());
        assertEquals(N, checker.tree.size());

        // Reverse order
        checker = new OpChecker();
        IntStream.iterate(N, n -> n-1).limit(N).forEach(checker::insert);
        assertFalse(checker.tree.isEmpty());
        assertEquals(N, checker.tree.size());

        // Random order, no duplicates
        Random rand = new Random();
        checker = new OpChecker();
        IntStream.iterate(N, n -> rand.nextInt(N*10)).distinct().limit(N).forEach(checker::insert);
        assertFalse(checker.tree.isEmpty());
        assertEquals(N, checker.tree.size());

        // Random order with duplicates
        checker = new OpChecker();
        IntStream.iterate(N, n -> rand.nextInt(N)).limit(N).forEach(checker::insert);
        assertFalse(checker.tree.isEmpty());

        // Duplicates only
        final OpChecker ck = new OpChecker();
        IntStream.range(0, N).forEach(ck::insert);
        IntStream.range(0, N).forEach(n -> {
            Tree<Integer> t = ck.tree;
            assertTrue("Insert of a duplicate should be a no-op", ck.insert(n) == t);
        });
    }
}
