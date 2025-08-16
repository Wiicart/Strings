package com.pedestriamc.strings.deathmessages;

import com.pedestriamc.strings.Strings;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DeathMessageProvider {

    private static final Random RANDOM = new Random(44833L * LocalDateTime.now().getNano());

    private static final String FALLBACK_DEATH_MESSAGE = "&f{player} died";

    private final Map<DamageCause, DeathMessageEntry> messages;

    DeathMessageProvider(@NotNull Strings strings) {
        FileConfiguration config = strings.files().getDeathMessagesFileConfig();
        Map<DamageCause, DeathMessageEntry> map = new EnumMap<>(DamageCause.class);
        for (Map.Entry<DamageCause, String> entry : DeathMessageKeys.KEYS.entrySet()) {
            List<String> standard = config.getStringList(entry.getValue());
            List<String> escaping = config.getStringList(entry.getValue() + "-escaping");
            map.put(entry.getKey(), new DeathMessageEntry(standard, escaping));
        }

        messages = map;
    }

    @NotNull
    public String randomMessage(@NotNull DamageCause cause) {
        DeathMessageEntry entry = messages.get(cause);
        if (entry != null) {
            return entry.randomStandardMessage();
        } else {
            return FALLBACK_DEATH_MESSAGE;
        }
    }

    @NotNull
    public String randomEscapingMessage(@NotNull DamageCause cause) {
        DeathMessageEntry entry = messages.get(cause);
        if (entry != null) {
            return entry.randomEscapingMessage();
        } else {
            return FALLBACK_DEATH_MESSAGE;
        }
    }

    private record DeathMessageEntry(@NotNull List<String> standardMessages, @NotNull List<String> escapingMessages) {

        @NotNull String randomStandardMessage() {
            String message = randomMessageFromList(standardMessages);
            return message != null ? message : FALLBACK_DEATH_MESSAGE;
        }

        @NotNull String randomEscapingMessage() {
            String message = randomMessageFromList(escapingMessages);
            return message != null ? message : randomStandardMessage();
        }

        private @Nullable String randomMessageFromList(@NotNull List<String> list) {
            return switch(list.size()) {
                case 0 -> null;
                case 1 -> list.getFirst();
                default -> list.get((RANDOM.nextInt(list.size())));
            };
        }

    }
}
