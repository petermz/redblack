package com.example.redblack;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class IteratorTest extends TestBase {

    @Test
    public void testEmptyIterator() {
        assertTrue(! new Tree<>().iterator().hasNext());
        try {
            new Tree<>().iterator().next();
            fail("new Tree().iterator().next() should throw exception");
        } catch (NoSuchElementException e) {
            // pass
        }
    }

    @Test
    public void testOrder() {
        final int N = 1000;
        final Random rand = new Random();
        for (int i=0; i<N; i++) {
            insert(rand.nextInt());
        }

        int m = Integer.MIN_VALUE;
        Iterator<Integer> it = tree().iterator();
        while (it.hasNext()) {
            int n = it.next();
            assertTrue("Bad order: " + m + ", " + n, m <= n);
            m = n;
        }
    }
}
