package com.pedestriamc.strings.moderation.manager;

import com.pedestriamc.strings.api.moderation.Option;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class ChatFilter {

    //private static final LevenshteinDistance LEVENSHTEIN = LevenshteinDistance.getDefaultInstance();

    private final Set<String> bannedWords;

    public ChatFilter(@NotNull StringsModeration strings) {
        bannedWords = new HashSet<>(strings.getConfiguration().get(Option.StringList.BANNED_WORDS));
    }

    public FilteredChat filter(@NotNull String message) {
        List<String> filteredElements = new ArrayList<>();
        for(String str : bannedWords) {
            if(StringUtils.containsIgnoreCase(message, str)) {
                message = message.replaceAll("(?i)" + Pattern.quote(str), "");
                filteredElements.add(str);
            }
        }
        return new FilteredChat(message, filteredElements);
    }

    public FilteredChat fuzzyFilter(@NotNull String message) {
        List<String> filteredElements = new ArrayList<>();
        for(String str : bannedWords) {
            if(StringUtils.containsIgnoreCase(message, str)) {
                message = message.replaceAll("(?i)" + Pattern.quote(str), "");
                filteredElements.add(str);
            }
        }
        return new FilteredChat(message, filteredElements);

    }


    public record FilteredChat(String message, List<String> filteredElements) {
        public FilteredChat {
            Objects.requireNonNull(message);
            Objects.requireNonNull(filteredElements);
        }
    }
}
