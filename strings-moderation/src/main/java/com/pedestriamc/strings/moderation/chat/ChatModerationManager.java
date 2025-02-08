package com.pedestriamc.strings.moderation.chat;

import com.pedestriamc.strings.moderation.StringsModeration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ChatModerationManager {

    /**
     * Map of Player's previous messages.
     */
    private final Map<Player, String> map;
    private final StringsModeration stringsModeration;
    private final long cooldownLength;
    private final Set<Player> cooldowns;
    private final BukkitScheduler scheduler;

    public ChatModerationManager(StringsModeration stringsModeration, FileConfiguration config) {
        this.stringsModeration = stringsModeration;
        map = new HashMap<>();
        cooldowns = new HashSet<>();
        scheduler = Bukkit.getScheduler();

        boolean forbidRepetition = config.getBoolean("forbid-repetition");

        Set<String> bannedWords = loadBannedWords(config);

        Bukkit.getServer()
                .getPluginManager()
                .registerEvents(new ChatListener(stringsModeration,this, forbidRepetition, bannedWords), stringsModeration);

        long cooldown = StringsModeration.calculateTicks(config.getString("cooldown-time"));
        if(cooldown < 0) {
            cooldownLength = 1200L;
            Bukkit.getLogger().info("[Strings Moderation] Invalid cooldown time in config, using 1m as cooldown time.");
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

    public void setPreviousMessage(Player player, String message) {
        map.put(player, message);
    }

    public boolean isRepeating(Player player, String message) {
        if(map.get(player) == null) {
            return false;
        }
        return map.get(player).equals(message);
    }

    public Set<String> loadBannedWords(FileConfiguration config) {
        HashSet<String> bannedWords = new HashSet<>();
        List<?> bannedWordList = config.getList("banned-words");

        if(bannedWordList == null || bannedWordList.isEmpty()) {
            return Set.of();
        }

        for(Object object : bannedWordList) {
            if(object instanceof String str) {
                bannedWords.add(str);
            }
        }

        return bannedWords;
    }

}
