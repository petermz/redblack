package com.example.redblack;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ImmutabilityTest extends TestBase {

    class ImmChecker {
        private Map<Tree<Integer>, int[]> vault = new HashMap<>();

        void store(Tree<Integer> tree) {
            vault.put(tree, signature(tree));
        }

        void check() {
            vault.forEach((tree, sig) -> assertSignatureEquals(sig, signature(tree)));
        }
    }

    static final int N = 400;

    final ImmChecker immChecker = new ImmChecker();

    @Test
    public void testImmutability() {
        Random rand = new Random();

        // First grow the tree
        for (int i=0; i<N; i++) {
            immChecker.store(tree());
            insert(rand.nextInt(N));
        }
        immChecker.check();

        // Do some inserts mixed with removals
        for (int i=0; i<N; i++) {
            immChecker.store(tree());
            int n = rand.nextInt(N);
            if (rand.nextBoolean()) {
                insert(n);
            } else {
                remove(n);
            }
        }
        immChecker.check();

        // Shrink the tree
        for (int i=0; i<N; i++) {
            immChecker.store(tree());
            remove(rand.nextInt(N));
        }
        immChecker.check();
    }
}
