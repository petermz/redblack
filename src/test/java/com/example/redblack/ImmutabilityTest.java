package com.example.redblack;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ImmutabilityTest extends TestBase {

    static class ImmChecker {
        private Map<Tree<Integer>, int[]> vault = new HashMap<>();

        void store(Tree<Integer> tree) {
            vault.put(tree, signature(tree));
        }

        void check() {
            vault.forEach((tree, sig) -> assertArrayEquals(sig, signature(tree)));
        }
    }

    static final int N = 400;

    final ImmChecker checker = new ImmChecker();

    @Test
    public void testImmutability() {
        Random rand = new Random();
        Tree<Integer> tree = new Tree<>();

        // First grow the tree
        for (int i=0; i<N; i++) {
            checker.store(tree);
            tree = tree.insert(rand.nextInt(N));
        }
        checker.check();

        // Do some inserts mixed with removals
        for (int i=0; i<N; i++) {
            checker.store(tree);
            boolean insert = rand.nextBoolean();
            int n = rand.nextInt(N);
            tree = insert ? tree.insert(n) : tree.remove(n);
        }
        checker.check();

        // Shrink the tree
        for (int i=0; i<N; i++) {
            checker.store(tree);
            tree = tree.remove(rand.nextInt(N));
        }
        checker.check();
    }
}
