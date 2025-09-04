package com.pedestriamc.strings.api.collections.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// https://www.geeksforgeeks.org/java/trie-data-structure-in-java/
@SuppressWarnings("unused")
public class Trie {

    private final Node root = new Node('@');
    private final Set<String> values = new HashSet<>();

    private final boolean ignoreCase;
    private final boolean additionalNormalization;

    public Trie(@NotNull Collection<String> words, @NotNull Condition... conditions) {
        for (String word : words) {
            insert(word);
        }

        boolean tempIgnoreCase = false;
        boolean tempAdditionalNormalization = false;
        for (Condition condition : conditions) {
            switch (condition) {
                case IGNORE_CASE -> tempIgnoreCase = true;
                case ADDITIONAL_NORMALIZATION -> tempAdditionalNormalization = true;
                default -> {}
            }
        }

        ignoreCase = tempIgnoreCase;
        additionalNormalization = tempAdditionalNormalization;
    }

    protected Node root() {
        return root;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public boolean isUsingAdditionalNormalization() {
        return additionalNormalization;
    }

    public boolean search(@NotNull String word) {
        if (word.isEmpty()) {
            return false;
        }

        word = normalize(word);

        Node current = root;
        char[] chars = word.toCharArray();
        for (char c : chars) {
            current = current.get(c);
            if (current == null) {
                return false;
            }
        }

        return current.isLeaf;
    }

    public void insert(@NotNull String word) {
        if (word.isEmpty()) {
            return;
        }

        word = normalize(word);
        values.add(word);

        Node current = root;
        char[] chars = word.toCharArray();
        for (char c : chars) {
            Node next = current.get(c);
            if (next == null) {
                next = new Node(c);
                current.addChild(next);
            }

            current = next;
        }
        current.isLeaf = true;
    }

    public Set<String> values() {
        return Collections.unmodifiableSet(values);
    }

    protected String handleCase(@NotNull String input) {
        return ignoreCase ? input.toUpperCase(Locale.ROOT) : input;
    }

    private String normalize(@NotNull String input) {
        if (isUsingAdditionalNormalization()) {
            input = Normalizer.normalize(input, Normalizer.Form.NFKD);
        }

        return handleCase(input);
    }

    protected static class Node {

        final char val;
        final Map<Character, Node> children;
        boolean isLeaf = false;

        Node(char val) {
            this.val = val;
            this.children = new HashMap<>(2);
        }

        Node(char val, @NotNull Node child) {
            this(val);
            addChild(child);
        }

        void addChild(@NotNull Node child) {
            children.put(child.val, child);
        }

        @Nullable
        Node get(char val) {
            return children.get(val);
        }
    }

    public enum Condition {
        /**
         * Makes all searches case-insensitive
         */
        IGNORE_CASE,
        /**
         * Performs additional normalization on Strings input for searching.<br/>
         * Uses Java's {@link java.text.Normalizer}
         * For example" "he1l0" -> "hello"
         */
        ADDITIONAL_NORMALIZATION,
    }

}
