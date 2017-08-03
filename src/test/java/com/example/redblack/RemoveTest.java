package com.example.redblack;

import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemoveTest extends TestBase {
    @Test
    public void testRemove() {
        Random rand = new Random();
        final Tree<Integer> empty = new Tree<>();

        // remove() should not change empty set
        IntStream.iterate(0, n -> rand.nextInt(100)).limit(10).forEach(n ->
                assertTrue("empty.remove(" + n + ") should be empty", empty.remove(n) == empty));

        // Natural order
        final int N = 1000;
        TestBase.OpChecker checker = new TestBase.OpChecker();
        createTree(IntStream.range(0, N), checker);
        IntStream.range(0, N).forEach(checker::remove);
        assertTrue(checker.tree.isEmpty());
        assertEquals(0, checker.tree.size());

        // Reverse order
        checker = new TestBase.OpChecker();///mv to class
        createTree(IntStream.range(0, N), checker);
        IntStream.iterate(N-1, n -> n-1).limit(N).forEach(checker::remove);
        assertTrue(checker.tree.isEmpty());
        assertEquals(0, checker.tree.size());

        // Random order, no duplicates
        checker = new TestBase.OpChecker();
        createTree(IntStream.range(0, N), checker);
        IntStream.iterate(0, n -> rand.nextInt(N)).distinct().limit(N).forEach(checker::remove);
        assertTrue(checker.tree.isEmpty());
        assertEquals(0, checker.tree.size());

        // Random order with duplicates
        checker = new TestBase.OpChecker();
        createTree(IntStream.range(0, N), checker);
        IntStream.iterate(0, n -> rand.nextInt(N)).limit(N).forEach(checker::remove);///nonfinal

        // Removal of missing items. Tree contains even numbers, and we remove odd ones
        int evenN = N / 2 * 2;
        final TestBase.OpChecker ck = new TestBase.OpChecker();
        createTree(IntStream.iterate(evenN, n -> n+2).limit(N), ck);
        IntStream.iterate(1, n -> rand.nextInt(N * 4)).filter(n -> n % 2 == 1).limit(N).forEach(n -> {
            Tree<Integer> t = ck.tree;
            assertTrue("Removal of a missing item (" + n + ") should be a no-op", ck.remove(n) == t);
        });
    }
}
