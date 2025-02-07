package com.pedestriamc.strings.moderation.spam;

import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SpamManager {

    /**
     * Map of Player's previous messages.
     */
    private final Map<Player, String> map;
    private final StringsModeration stringsModeration;
    private long cooldownLength;
    private final Set<Player> cooldowns;
    private final BukkitScheduler scheduler;

    public SpamManager(StringsModeration stringsModeration) {
        this.stringsModeration = stringsModeration;
        map = new HashMap<>();
        cooldowns = new HashSet<>();
        scheduler = Bukkit.getScheduler();
    }

    public void startCooldown(Player player) {
        cooldowns.add(player);
        scheduler.runTaskLater(stringsModeration, () -> cooldowns.remove(player), cooldownLength);
    }

    public boolean isOnCooldown(Player player) {
        return cooldowns.contains(player);
    }

    public void setPreviousMessage(Player player, String message) {
        map.put(player, message);
    }

    public boolean isRepeating(Player player, String message) {
        return map.get(player).equals(message);
    }
}
