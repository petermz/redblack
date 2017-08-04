package com.example.redblack;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class ContainsTest extends TestBase {
    static final int N = 1000;

    final OpChecker checker = new OpChecker();

    @Test
    public void testEmptyTree() {
        Tree<Integer> tree = new Tree<>();
        randomStream(Integer.MAX_VALUE).limit(100).forEach(n -> assertFalse(tree.contains(n)));
    }

    @Test
    public void testInsert() {
        randomStream(N).limit(N).forEach(n -> {
            checker.insert(n);
            // All items inserted so far must be present
            checker.expected.forEach(i -> assertTrue(checker.tree.contains(i)));
        });
    }

    @Test
    public void testRemove() {
        Random rand = new Random();
        checker.createTree(randomStream(N).distinct().limit(N));

        Set<Integer> removed = new HashSet<>();
        while (! checker.tree.isEmpty()) {
            int d = rand.nextInt(N);
            checker.remove(d);
            removed.add(d);

            // All removed items should not be present
            removed.forEach(n -> assertFalse(checker.tree.contains(n)));
            // All not yet removed items should be present
            checker.expected.forEach(n -> assertTrue(checker.tree.contains(n)));
        }
    }

    @Test
    public void testIteratorConsistency() {
        // Test 10 random trees
        for (int i=0; i<10; i++) {
            Tree<Integer> tree = checker.createTree(randomStream(Integer.MAX_VALUE).limit(N));
            Iterator<Integer> it = tree.iterator();
            while (it.hasNext()) {
                assertTrue(tree.contains(it.next()));
            }
        }
    }
}
