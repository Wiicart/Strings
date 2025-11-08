package com.pedestriamc.strings.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.text.EmojiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.FileReader;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiProvider implements EmojiManager {

    private static final Pattern EMOJI_PATTERN = Pattern.compile(":([^\\s:]+):");

    private final StringsPlatform strings;

    @Unmodifiable
    private final Map<String, String> map;

    public EmojiProvider(@NotNull StringsPlatform strings) {
        this.strings = strings;
        map = loadMap();
    }

    @NotNull
    public String applyEmojis(@NotNull String input) {
        Matcher matcher = EMOJI_PATTERN.matcher(input);
        return matcher.replaceAll(result -> map.getOrDefault(
                result.group().toLowerCase(Locale.ROOT),
                result.group()
        ));
    }


    public @NotNull Component applyEmojis(@NotNull Component input) {
        return input.replaceText(config -> config
                .match(EMOJI_PATTERN)
                .replacement((result, builder) -> Component.text(
                        map.getOrDefault(
                            result.group().toLowerCase(Locale.ROOT),
                            result.group())
                        ).color(NamedTextColor.WHITE)
                )
        );
    }

    @Override
    public @Unmodifiable @NotNull Map<String, String> mappings() {
        return map;
    }

    private Map<String, String> loadMap() {
        Map.Entry<String, String>[] entries;
        try (FileReader reader = new FileReader(strings.files().getEmojisFile())) {
            TypeToken<Map<String, String>> type = new TypeToken<>() {};
            Gson gson = new Gson();
            return Collections.unmodifiableMap(gson.fromJson(reader, type));
        } catch(Exception e) {
            strings.warning("Failed to load emojis: " + e.getMessage());
        }

        return Map.of();
    }

}
