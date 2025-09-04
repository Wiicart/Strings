package com.pedestriamc.strings.api.collections.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Collection;

public class FuzzyTrie extends Trie {

    @Range(from = 0, to = 1)
    private double maxSimilarity;

    /**
     * Constructs a FuzzyTrie
     * @param words The collection of banned words
     * @param maxSimilarity The max percent similarity before determining a match
     * @param conditions Optional {@link Trie.Condition}(s)
     */
    public FuzzyTrie(@NotNull Collection<String> words, @Range(from = 0, to = 1) double maxSimilarity, @NotNull Condition... conditions) {
        super(words, conditions);
        setMaxSimilarity(maxSimilarity);
    }

    public void setMaxSimilarity(@Range(from = 0, to = 1) double maxSimilarity) {
        if (maxSimilarity < 0 || maxSimilarity > 1) {
            throw new IllegalArgumentException("New maxSimilarity value " + maxSimilarity + " not within range [0, 1]");
        }

        this.maxSimilarity = maxSimilarity;
    }

    public double getMaxSimilarity() {
        return maxSimilarity;
    }

    @Override
    public boolean search(@NotNull String input) {
        int differences = (int) (input.length() * maxSimilarity);
        return search(root(), input, differences);
    }

    // if the amount of changes needed is within the distance, this will return true
    private boolean search(@NotNull Node current, @NotNull String input, int distance) {
        Node prev = current;
        char[] chars = input.toCharArray();
        for (int i = 0; i < input.length(); i++) {
            char c = chars[i];
            current = prev.get(c);
            if (current != null) {
                prev = current;
                continue;
            }

            if (prev.isLeaf) {
                return true;
            }

            if (distance <= 0) {
                return false;
            }

            for (Node child : prev.children.values()) {
                if (search(child, input.substring(i), distance - 1)) {
                    return true;
                }
            }

        }
        return false;
    }

}
