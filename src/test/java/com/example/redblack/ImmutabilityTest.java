package com.example.redblack;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class ImmutabilityTest {

    static class Vault {
        private Map<Tree<Integer>, int[]> vault = new HashMap<>();

        private int[] signature(Stream<Integer> stream) {///common
            return stream.mapToInt(Integer::intValue).toArray();
        }

        void store(Tree<Integer> tree) {
            vault.put(tree, signature(tree.stream()));
        }

        void check() {
            vault.forEach((tree, sig) -> assertArrayEquals(sig, signature(tree.stream())));
        }
    }

    @Test
    public void testImmutability() {
        final int N = 1000;
        final Random rand = new Random();

        Tree<Integer> tree = new Tree<>();

        // First grow the tree
        Vault vault = new Vault();
        for (int i=0; i<N; i++) {
            vault.store(tree);
            tree = tree.insert(rand.nextInt(N));
        }
        vault.check();

        // Do some inserts mixed with removals
        vault = new Vault();
        for (int i=0; i<N; i++) {
            vault.store(tree);
            boolean insert = rand.nextBoolean();
            int n = rand.nextInt(N);
            tree = insert ? tree.insert(n) : tree.remove(n);
        }
        vault.check();

        // Shrink the tree
        vault = new Vault();
        for (int i=0; i<N; i++) {
            vault.store(tree);
            tree = tree.remove(rand.nextInt(N));
        }
        vault.check();
    }
}
