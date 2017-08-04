package com.example.redblack;

import org.junit.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Base class for all tests.
 */
class TestBase {

    private Tree<Integer> tree = new Tree<>();
    private Journal journal = new Journal();
    private Checker checker = new Checker();

    Tree<Integer> tree() {
        return tree;
    }

    Checker checker() {
        return checker;
    }

    /**
     * This class keeps track of inserts and removes and can generate corresponding Java code.
     * For tests that work with random data, this can help create code to reproduce a failure.
     */
    private static class Journal {
        List<String> journal = new LinkedList<>();

        void insert(int n) {
            journal.add(".insert(" + n + ')');
        }

        void remove(int n) {
            journal.add(".remove(" + n + ')');
        }

        String dump() {
            // Convert the journal to a Java method code
            List<String> lines = new LinkedList<>();
            lines.add("void test() {\n    Tree<Integer> t = new Tree<Integer>()");
            final int len = journal.size();
            for (int i = 10; i <= len; i += 10) {
                addLine(lines, i - 10, i);
            }
            addLine(lines, len / 10 * 10, len);
            lines.add("    ;\n}");
            return lines.stream().collect(Collectors.joining("\n"));
        }

        private void addLine(List<String> lines, int from, int to) {
            lines.add("    " + journal.subList(from, to).stream().collect(Collectors.joining()));
        }
    }

    /**
     * This class helps validate correctness of insert and remove operations.
     * After each operation it checks tree contents (as reported by tree iterator)
     * against an expected set (implemented with a java.util.Set).
     */
    class Checker {
        Set<Integer> expected = new TreeSet<>();

        void insert(int n) {
            expected.add(n);
            check();
        }

        void remove(int n) {
            expected.remove(n);
            check();
        }

        private void check() {
            assertSignatureEquals(signature(tree), signature(expected.iterator()));
        }
    }

    Tree<Integer> insert(int n) {
        tree = tree.insert(n);
        journal.insert(n);
        checker.insert(n);
        return tree;
    }

    Tree<Integer> remove(int n) {
        tree = tree.remove(n);
        journal.remove(n);
        checker.remove(n);
        return tree;
    }


    void fail(String message) {
        System.err.println("Check failed: " + message);
        System.err.println("Use the code below to recreate the problematic tree:");
        System.err.println("----------------------------------------------------------------------");
        System.err.println(journal.dump());
        System.err.println("----------------------------------------------------------------------");
        Assert.fail();
    }

    /**
     * A replacement for {@code Assert.assertTrue} that makes use of the Journal.
     */
    void assertTrue(String message, boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    void assertTrue(boolean condition) {
        assertEquals(true, condition);
    }

    void assertEquals(Object expected, Object actual) {
        assertTrue("expected " + expected + " but found " + actual, expected.equals(actual));
    }

    /**
     * Helper method to fill the tree with data.
     */
    Tree<Integer> fill(IntStream stream) {
        stream.forEach(this::insert);
        return tree;
    }

    static IntStream randomStream(int limit) {
        Random rand = new Random();
        return IntStream.iterate(0, n -> rand.nextInt(limit));
    }

    /**
     * Generates a signature from an iterator. Signatures are used to verify contents of a tree.
     */
    private static int[] signature(Iterator<Integer> it) {
        final Iterable<Integer> iterable = () -> it;
        return StreamSupport.stream(iterable.spliterator(), false).mapToInt(Integer::intValue).toArray();
    }

    static int[] signature(Tree<Integer> tree) {
        return signature(tree.iterator());
    }

    void assertSignatureEquals(int[] expected, int[] actual) {
        int diff = actual.length - expected.length;
        if (diff < 0) {
            fail(String.format("%d elements were missing from the tree", -diff));
        } else if (diff > 0) {
            fail(String.format("There were %d extra elements in the tree", diff));
        }
        for (int i = 0; i < actual.length; i++) {
            if (actual[i] != expected[i]) {
                fail(String.format("Wrong value at index %d: expected %d but was %d", i, expected[i], actual[i]));
            }
        }
    }
}
