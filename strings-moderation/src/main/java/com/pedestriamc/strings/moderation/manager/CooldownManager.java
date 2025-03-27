package com.pedestriamc.strings.moderation.manager;

import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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

    public CooldownManager(@NotNull StringsModeration stringsModeration) {
        this.stringsModeration = stringsModeration;
        cooldowns = new HashSet<>();
        configure(stringsModeration.getConfig());
        scheduler = stringsModeration.getServer().getScheduler();
    }

    private void configure(FileConfiguration config) {
        long cooldown = StringsModeration.calculateTicks(config.getString("cooldown-time"));
        if(cooldown < 0) {
            cooldownLength = 1200L;
            Bukkit.getLogger().info("[StringsModeration] Invalid cooldown time in config, using 1m as cooldown time.");
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
