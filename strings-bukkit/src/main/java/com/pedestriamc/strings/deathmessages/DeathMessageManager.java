package com.pedestriamc.strings.deathmessages;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.user.User;
import com.pedestriamc.strings.user.util.UserUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

// Instantiated by PlayerDeathListener only if custom death messages are enabled
// Vanilla death messages: https://minecraft.wiki/w/Death_messages
public class DeathMessageManager {

    private final DeathMessageProvider registry;

    private final UserUtil userUtil;

    public DeathMessageManager(@NotNull Strings strings) {
        registry = new DeathMessageProvider(strings);
        userUtil = strings.users();
    }

    /**
     * Generates a death message
     * @param deceased The dead player
     * @param cause The cause of the death
     * @param event The last damage event to the player
     */
    @NotNull
    public String getDeathMessage(@NotNull Player deceased, @NotNull DamageCause cause, @NotNull EntityDamageEvent event) {
        String message;
        Map<String, String> fighterPlaceholders = getFighterPlaceholders(deceased);
        if (!fighterPlaceholders.isEmpty()) {
            message = registry.randomEscapingMessage(cause);
        } else {
            message = registry.randomMessage(cause);
        }

        Map<String, String> placeholders = generateDeceasedPlaceholders(deceased);
        placeholders.putAll(PlaceholderGenerator.generate(cause, event));
        placeholders.putAll(fighterPlaceholders);

        message = replacePlaceholders(message, placeholders);
        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    @NotNull
    private Map<String, String> generateDeceasedPlaceholders(@NotNull Player deceased) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{player}", deceased.getName());
        placeholders.put("{display-name}", deceased.getDisplayName());

        return placeholders;
    }

    @NotNull
    @Unmodifiable
    private Map<String, String> getFighterPlaceholders(@NotNull Player deceased) {
        User user = userUtil.getUser(deceased);
        EntityDamageEvent event = user.getSecondToLastDamage();
        if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            Entity damager = damageByEntityEvent.getDamager();
            if (damager instanceof Player player) {
                return Map.of(
                        "{fighter}", player.getName(),
                        "{fighter-display-name}", player.getDisplayName()
                );
            }

            if (damager instanceof LivingEntity livingEntity) {
                String customName = livingEntity.getCustomName();
                String mob = customName != null ? customName : livingEntity.getName();
                return Map.of(
                        "{fighter}", mob,
                        "{fighter-display-name}", mob
                );
            }
        }

        return Map.of();
    }

    @NotNull
    private String replacePlaceholders(@NotNull String string, @NotNull Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            string = string.replace(entry.getKey(), entry.getValue());
        }

        return string;
    }

}
