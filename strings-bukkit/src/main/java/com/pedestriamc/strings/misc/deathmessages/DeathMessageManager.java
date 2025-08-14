package com.pedestriamc.strings.misc.deathmessages;

import com.pedestriamc.strings.Strings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

// Instantiated by PlayerDeathListener only if custom death messages are enabled
// Vanilla death messages: https://minecraft.wiki/w/Death_messages
public class DeathMessageManager {

    private static final Random RANDOM = new Random(44833L * LocalDateTime.now().getNano());

    private static final String FALLBACK_DEATH_MESSAGE = "&f{player} died";

    private static final Map<DamageCause, String> CONFIG_MAPPINGS = Map.ofEntries(
            Map.entry(DamageCause.KILL, "messages.command"),
            Map.entry(DamageCause.WORLD_BORDER, "messages.world-border"),
            Map.entry(DamageCause.CONTACT, "messages.contact"),
            Map.entry(DamageCause.ENTITY_ATTACK, "messages.attack"),
            Map.entry(DamageCause.ENTITY_SWEEP_ATTACK, "messages.attack"),
            Map.entry(DamageCause.PROJECTILE, "messages.projectile"),
            Map.entry(DamageCause.SUFFOCATION, "messages.suffocation"),
            Map.entry(DamageCause.FALL, "messages.fall"),
            Map.entry(DamageCause.FIRE, "messages.fire"),
            Map.entry(DamageCause.FIRE_TICK, "messages.fire"),
            Map.entry(DamageCause.LAVA, "messages.lava"),
            Map.entry(DamageCause.DROWNING, "messages.drowning"),
            Map.entry(DamageCause.BLOCK_EXPLOSION, "messages.explosion"),
            Map.entry(DamageCause.ENTITY_EXPLOSION, "messages.explosion"),
            Map.entry(DamageCause.VOID, "messages.void"),
            Map.entry(DamageCause.LIGHTNING, "messages.lightning"),
            Map.entry(DamageCause.SUICIDE, "messages.suicide"),
            Map.entry(DamageCause.STARVATION, "messages.starvation"),
            Map.entry(DamageCause.POISON, "messages.poison"),
            Map.entry(DamageCause.MAGIC, "messages.magic"),
            Map.entry(DamageCause.WITHER, "messages.wither"),
            Map.entry(DamageCause.FALLING_BLOCK, "messages.falling-block"),
            Map.entry(DamageCause.THORNS, "messages.thorns"),
            Map.entry(DamageCause.DRAGON_BREATH, "messages.dragon-breath"),
            Map.entry(DamageCause.FLY_INTO_WALL, "messages.fly-into-wall"),
            Map.entry(DamageCause.HOT_FLOOR, "messages.hot-floor"),
            Map.entry(DamageCause.FREEZE, "messages.freeze"),
            Map.entry(DamageCause.SONIC_BOOM, "messages.sonic-boom")
    );

    private final Map<DamageCause, List<String>> messages = new EnumMap<>(DamageCause.class);

    public DeathMessageManager(@NotNull Strings strings) {
        loadMessages(strings.files().getDeathMessagesFileConfig());
    }

    private void loadMessages(@NotNull FileConfiguration config) {
        for (Map.Entry<DamageCause, String> entry : CONFIG_MAPPINGS.entrySet()) {
            messages.put(entry.getKey(), config.getStringList(entry.getValue()));
        }
    }

    /**
     * Generates a death message
     * @param deceased The dead player
     * @param cause The cause of the death
     * @param event The last damage event to the player
     */
    @NotNull
    public String getDeathMessage(@NotNull Player deceased, @NotNull DamageCause cause, @NotNull EntityDamageEvent event) {
        String message = randomMessage(cause);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{player}", deceased.getName());
        placeholders.put("{display-name}", deceased.getDisplayName());
        placeholders.putAll(PlaceholderGenerator.generate(cause, event));

        message = replacePlaceholders(message, placeholders);
        message = ChatColor.translateAlternateColorCodes('&', message);

        return replacePlaceholders(message, placeholders);
    }

    @NotNull
    private String randomMessage(@NotNull DamageCause cause) {
        List<String> list = messages.getOrDefault(cause, List.of());
        if (list.isEmpty()) {
            return FALLBACK_DEATH_MESSAGE;
        }

        if (list.size() == 1) {
            return list.getFirst();
        }

        return list.get((RANDOM.nextInt(list.size())));
    }

    @NotNull
    private String replacePlaceholders(@NotNull String string, @NotNull Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            string = string.replace(entry.getKey(), entry.getValue());
        }

        return string;
    }

}
