package com.example.redblack;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class IteratorTest extends TestBase {

    @Test
    public void testEmptyIterator() {
        assertFalse(new Tree<>().iterator().hasNext());
        try {
            new Tree<>().iterator().next();
            fail();
        } catch (NoSuchElementException e) {
            // pass
        }
    }

    @Test
    public void testOrder() {
        final int N = 1000;
        final Random rand = new Random();

        Tree<Integer> tree = new Tree<>();
        for (int i=0; i<N; i++) {
            tree = tree.insert(rand.nextInt());
        }

        int m = Integer.MIN_VALUE;
        Iterator<Integer> it = tree.iterator();
        while (it.hasNext()) {
            int n = it.next();
            assertTrue(m <= n);
            m = n;
        }
    }
}
