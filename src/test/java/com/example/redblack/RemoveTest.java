package com.example.redblack;

import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;

public class RemoveTest extends TestBase {

    static final int N = 1000;

    // This test delegates actual checks to Checker (via fill() and remove())

    @Test
    public void testEmptyTree() {
        final Tree<Integer> empty = tree();
        randomStream(Integer.MAX_VALUE).limit(10).forEach(n ->
                assertTrue("empty.remove(" + n + ") should be empty", remove(n) == empty));
    }

    @Test
    public void testNaturalOrder() {
        fill(IntStream.range(0, N));
        IntStream.range(0, N).forEach(this::remove);
        assertTrue(tree().isEmpty());
        assertEquals(0, tree().size());
    }

    @Test
    public void testReverseOrder() {
        fill(IntStream.range(0, N));
        IntStream.iterate(N - 1, n -> n - 1).limit(N).forEach(this::remove);
        assertTrue(tree().isEmpty());
        assertEquals(0, tree().size());
    }

    @Test
    public void testRandomOrder() {
        fill(IntStream.range(0, N));
        randomStream(N).distinct().limit(N).forEach(this::remove);
        assertTrue(tree().isEmpty());
        assertEquals(0, tree().size());
    }

    @Test
    public void testRandomOrderWithDuplicates() {
        fill(IntStream.range(0, N));
        randomStream(N).limit(N).forEach(this::remove);
    }

    @Test
    public void testMissingItems() {
        // Tree contains even numbers, and we remove odd ones
        Random rand = new Random();
        int evenN = N / 2 * 2;
        Tree<Integer> t = fill(IntStream.iterate(evenN, n -> n+2).limit(N));
        for (int i = 0; i < N * 2; i++) {
            int n = rand.nextInt(N * 4);
            if (n % 2 == 1) {
                Tree<Integer> tmp = remove(n);
                assertTrue("Removal of a missing item (" + n + ") should be a no-op", tree() == t);
            }
        }
    }
}
