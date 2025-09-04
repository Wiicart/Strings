package com.pedestriamc.strings.moderation.manager;

import com.pedestriamc.strings.api.collections.trie.Trie;
import com.pedestriamc.strings.api.moderation.Option;
import com.pedestriamc.strings.moderation.configuration.Configuration;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ChatFilter {

    private static final LevenshteinDistance LEVENSHTEIN = LevenshteinDistance.getDefaultInstance();

    private final Trie bannedWords;
    private final double maxThreshold;
    private final boolean replaceWithAsterisks;

    public ChatFilter(@NotNull StringsModeration strings) {
        Configuration config = strings.getConfiguration();
        bannedWords = new Trie(config.get(Option.StringList.BANNED_WORDS), Trie.Condition.IGNORE_CASE, Trie.Condition.ADDITIONAL_NORMALIZATION);
        maxThreshold = config.get(Option.Double.LEVENSHTEIN_THRESHOLD);
        replaceWithAsterisks = config.get(Option.Bool.REPLACE_FILTERED_TEXT_WITH_ASTERISKS);
    }

    public FilteredChat filter(@NotNull String message) {
        List<String> filteredElements = new ArrayList<>();
        for(String str : bannedWords.values()) {
            if(StringUtils.containsIgnoreCase(message, str)) {
                message = message.replaceAll("(?i)" + Pattern.quote(str), "");
                filteredElements.add(str);
            }
        }
        return new FilteredChat(message, filteredElements);
    }

    public FilteredChat fuzzyFilter(@NotNull String message) {
        List<String> filteredElements = new ArrayList<>();
        String[] words = message.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (contains(word, bannedWords.values())) {
                filteredElements.add(word);
                builder.append(asAsterisks(word));
                continue;
            }

            if (bannedWords.search(word)) {
                filteredElements.add(word);
                if (replaceWithAsterisks) {
                    builder.append(asAsterisks(word));
                }
                continue;
            }

            builder.append(word);
        }

        for(String str : bannedWords.values()) {
            if (StringUtils.containsIgnoreCase(message, str)) {
                message = message.replaceAll("(?i)" + Pattern.quote(str), "");
                filteredElements.add(str);
            }
        }

        return new FilteredChat(builder.toString(), filteredElements);
    }

    @NotNull
    private String asAsterisks(@NotNull String input) {
        return "*".repeat(input.length());
    }

    private boolean contains(@NotNull String input, @NotNull Collection<String> words) {
        for (String word : words) {
            if (StringUtils.containsIgnoreCase(input, word)) {
                return true;
            }
        }


        return false;
    }

    @SuppressWarnings("unused")
    private boolean fuzzyMatches(@NotNull String input, @NotNull Collection<String> words) {
        for (String word : words) {
            int distance = LEVENSHTEIN.apply(input, word);
            if (distance == 0) {
                return true;
            }

            double highestLength = Math.max(input.length(), word.length());
            if ((distance / highestLength) < maxThreshold) {
                return true;
            }
        }

        return false;
    }


    public record FilteredChat(String message, List<String> filteredElements) {
        public FilteredChat {
            Objects.requireNonNull(message);
            Objects.requireNonNull(filteredElements);
        }
    }
}
