package com.example.redblack;

import org.junit.Test;

import java.util.stream.IntStream;

public class InsertTest extends TestBase {

    static final int N = 1000;

    // This test delegates actual checks to Checker (via fill() and insert())

    @Test
    public void testNaturalOrder() {
        fill(IntStream.range(0, N));
        assertTrue(! tree().isEmpty());
        assertEquals(N, tree().size());
    }

    @Test
    public void testReverseOrder() {
        fill(IntStream.iterate(N, n -> n - 1).limit(N));
        assertTrue(! tree().isEmpty());
        assertEquals(N, tree().size());
    }

    @Test
    public void testRandomOrder() {
        fill(randomStream(N * 10).distinct().limit(N));
        assertTrue(! tree().isEmpty());
        assertEquals(N, tree().size());
    }

    @Test
    public void testRandomOrderWithDuplicates() {
        fill(randomStream(N).limit(N));
        assertTrue(! tree().isEmpty());
    }

    @Test
    public void testInsertDuplicates() {
        Tree<Integer> t = fill(IntStream.range(0, N));
        for (int i=0; i<N; i++) {
            Tree<Integer> tmp = insert(i);
            assertTrue("Duplicate insert (" + i + ") should be a no-op", tree() == t);
        }
    }
}
