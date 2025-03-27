package com.pedestriamc.strings.moderation.manager;

import com.pedestriamc.strings.moderation.StringsModeration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class ChatFilter {

    private final Set<String> bannedWords;

    public ChatFilter(@NotNull StringsModeration stringsModeration) {
        bannedWords = loadBannedWords(stringsModeration.getConfig());
    }

    private @NotNull Set<String> loadBannedWords(@NotNull FileConfiguration config) {
        HashSet<String> banned = new HashSet<>();
        List<?> bannedWordList = config.getList("banned-words");
        if(bannedWordList == null || bannedWordList.isEmpty()) {
            return Set.of();
        }

        for(Object object : bannedWordList) {
            if(object instanceof String str) {
                banned.add(str.toLowerCase());
            }
        }

        return banned;
    }

    public FilteredChat filter(String message) {
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
