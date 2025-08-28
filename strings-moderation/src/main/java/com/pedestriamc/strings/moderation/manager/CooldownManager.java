package com.pedestriamc.strings.moderation.manager;

import com.pedestriamc.strings.api.moderation.Option;
import com.pedestriamc.strings.configuration.Configuration;
import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CooldownManager {

    private final StringsModeration stringsModeration;

    private final Set<Player> cooldowns;
    private long cooldownLength;
    private final BukkitScheduler scheduler;

    public CooldownManager(@NotNull StringsModeration strings) {
        this.stringsModeration = strings;
        cooldowns = new HashSet<>();
        loadOptions(strings);
        scheduler = strings.getServer().getScheduler();
    }

    private void loadOptions(@NotNull StringsModeration strings) {
        Configuration config = strings.getConfiguration();
        long cooldown = StringsModeration.calculateTicks(config.get(Option.Text.COOLDOWN_DURATION));
        if(cooldown < 0) {
            cooldownLength = 1200L;
            strings.getLogger().info("Invalid cooldown time in config, using 1m as cooldown time.");
        } else {
            cooldownLength = cooldown;
        }
    }

    public void startCooldown(Player player) {
        cooldowns.add(player);
        scheduler.runTaskLater(stringsModeration, () -> cooldowns.remove(player), cooldownLength);
    }

    public boolean isOnCooldown(Player player) {
        return cooldowns.contains(player);
    }
}
