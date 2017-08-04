package com.example.redblack;

import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemoveTest extends TestBase {

    static final int N = 1000;

    final OpChecker checker = new OpChecker();

    @Test
    public void testEmptyTree() {
        final Tree<Integer> empty = new Tree<>();
        randomStream(Integer.MAX_VALUE).limit(10).forEach(n ->
                assertTrue("empty.remove(" + n + ") should be empty", empty.remove(n) == empty));
    }

    @Test
    public void testNaturalOrder() {
        checker.createTree(IntStream.range(0, N));
        IntStream.range(0, N).forEach(checker::remove);
        assertTrue(checker.tree.isEmpty());
        assertEquals(0, checker.tree.size());
    }

    @Test
    public void testReverseOrder() {
        checker.createTree(IntStream.range(0, N));
        IntStream.iterate(N - 1, n -> n - 1).limit(N).forEach(checker::remove);
        assertTrue(checker.tree.isEmpty());
        assertEquals(0, checker.tree.size());
    }

    @Test
    public void testRandomOrder() {
        checker.createTree(IntStream.range(0, N));
        randomStream(N).distinct().limit(N).forEach(checker::remove);
        assertTrue(checker.tree.isEmpty());
        assertEquals(0, checker.tree.size());
    }

    @Test
    public void testRandomOrderWithDuplicates() {
        checker.createTree(IntStream.range(0, N));
        randomStream(N).limit(N).forEach(checker::remove);
    }

    @Test
    public void testMissingItems() {
        // Tree contains even numbers, and we remove odd ones
        Random rand = new Random();
        int evenN = N / 2 * 2;
        Tree<Integer> tree = checker.createTree(IntStream.iterate(evenN, n -> n+2).limit(N));
        IntStream.iterate(1, n -> rand.nextInt(N * 4)).filter(n -> n % 2 == 1).limit(N).forEach(n ->
            assertTrue("Removal of a missing item (" + n + ") should be a no-op",
                    checker.remove(n) == tree)
        );
    }
}
