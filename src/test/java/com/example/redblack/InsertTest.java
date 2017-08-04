package com.example.redblack;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InsertTest extends TestBase {

    static final int N = 1000;

    final OpChecker checker = new OpChecker();

    @Test
    public void testNaturalOrder() {
        Tree<Integer> tree = checker.createTree(IntStream.range(0, N));
        assertFalse(tree.isEmpty());
        assertEquals(N, tree.size());
    }

    @Test
    public void testReverseOrder() {
        Tree<Integer> tree = checker.createTree(IntStream.iterate(N, n -> n - 1).limit(N));
        assertFalse(tree.isEmpty());
        assertEquals(N, tree.size());
    }

    @Test
    public void testRandomOrder() {
        Tree<Integer> tree = checker.createTree(randomStream(N * 10).distinct().limit(N));
        assertFalse(tree.isEmpty());
        assertEquals(N, tree.size());
    }

    @Test
    public void testRandomOrderWithDuplicates() {
        Tree<Integer> tree = checker.createTree(randomStream(N).limit(N));
        assertFalse(tree.isEmpty());
    }

    @Test
    public void testInsertDuplicates() {
        Tree<Integer> tree = checker.createTree(IntStream.range(0, N));
        IntStream.range(0, N).forEach(n ->
            assertTrue("Duplicate insert (" + n + ") should be a no-op", checker.insert(n) == tree)
        );
    }
}
