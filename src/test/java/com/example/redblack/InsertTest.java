package com.example.redblack;

import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InsertTest extends TestBase {

    @Test
    public void testInsert() {
        final int N = 1000;
        Tree<Integer> tree;

        // Natural order
        tree = createTree(IntStream.range(0, N), new OpChecker());
        assertFalse(tree.isEmpty());
        assertEquals(N, tree.size());

        // Reverse order
        tree = createTree(IntStream.iterate(N, n -> n-1).limit(N), new OpChecker());
        assertFalse(tree.isEmpty());
        assertEquals(N, tree.size());

        // Random order, no duplicates
        Random rand = new Random();
        tree = createTree(IntStream.iterate(0, n -> rand.nextInt(N*10)).distinct().limit(N), new OpChecker());
        assertFalse(tree.isEmpty());
        assertEquals(N, tree.size());

        // Random order with duplicates
        tree = createTree(IntStream.iterate(0, n -> rand.nextInt(N)).limit(N), new OpChecker());
        assertFalse(tree.isEmpty());

        // Duplicates only
        final OpChecker ck = new OpChecker();///mk class field
        createTree(IntStream.range(0, N), ck);
        IntStream.range(0, N).forEach(n -> {
            Tree<Integer> t = ck.tree;
            assertTrue("Duplicate insert (" + n + ") should be a no-op", ck.insert(n) == t);
        });
    }
}
