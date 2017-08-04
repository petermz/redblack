package com.example.redblack;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class ContainsTest extends TestBase {
    static final int N = 1000;

    @Test
    public void testEmptyTree() {
        randomStream(Integer.MAX_VALUE).limit(100).forEach(n ->
                assertTrue("contains should be false for " + n, ! tree().contains(n)));
    }

    @Test
    public void testInsert() {
        Set<Integer> inserted = checker().expected;
        randomStream(N).limit(N).forEach(n -> {
            insert(n);
            // All items inserted so far must be present
            inserted.forEach(i ->
                    assertTrue("contains should be true for " + i, tree().contains(i)));
        });
    }

    @Test
    public void testRemove() {
        fill(randomStream(N).distinct().limit(N));

        Random rand = new Random();
        Set<Integer> inserted = checker().expected;
        Set<Integer> removed = new HashSet<>();
        while (! tree().isEmpty()) {
            int d = rand.nextInt(N);
            remove(d);
            removed.add(d);

            // All removed items should not be present
            removed.forEach(n ->
                    assertTrue("contains should be false for " + n, ! tree().contains(n)));
            // All not yet removed items should be present
            inserted.forEach(n ->
                    assertTrue("contains should be true for " + n, tree().contains(n)));
        }
    }

    @Test
    public void testIteratorConsistency() {
        // Test 10 random trees
        for (int i=0; i<10; i++) {
            Tree<Integer> t = fill(randomStream(Integer.MAX_VALUE).limit(N));
            Iterator<Integer> it = t.iterator();
            while (it.hasNext()) {
                int n = it.next();
                assertTrue("contains should be true for " + n, t.contains(n));
            }
        }
    }
}
