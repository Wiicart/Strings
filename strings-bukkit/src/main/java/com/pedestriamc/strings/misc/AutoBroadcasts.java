package com.pedestriamc.strings.misc;

import com.pedestriamc.strings.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AutoBroadcasts {

    private final Strings strings;
    private final FileConfiguration config;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final ArrayList<String[]> broadcastList = new ArrayList<>();
    private int pos;
    private long interval;
    private final String order;

    public AutoBroadcasts(@NotNull Strings strings) {
        this.strings = strings;
        this.config = strings.getBroadcastsFileConfig();
        this.order = config.getString("sequence");
        if(config.getBoolean("enabled", false)) {
            this.interval = calculateDelay(config.getString("delay"));
            this.loadBroadcastList();
            schedule();
        }
    }

    private void schedule() {
        scheduler.runTaskTimer(strings, this::broadcastMessage, 20L, interval);
    }

    private long calculateDelay(String delay) {
        String regex = "^[0-9]+[sm]$";
        if(delay == null || !delay.matches(regex)) {
            Bukkit.getLogger().info("[Strings] Invalid broadcast delay in config.  Defaulting to 3m.");
            return 3600L;
        }
        char units = delay.charAt(delay.length() - 1);
        delay = delay.substring(0, delay.length() - 1);
        int delayNum = Integer.parseInt(delay);
        if(units == 'm') {
            delayNum *= 60;
        }

        return delayNum * 20L;

    }

    private void broadcastMessage() {
        if(pos == broadcastList.size()) {
            pos = 0;
            if(order.equalsIgnoreCase("random")) {
                Collections.shuffle(broadcastList);
            }
        }
        for(int i=0; i<broadcastList.get(pos).length; i++) {
            Bukkit.broadcastMessage(broadcastList.get(pos)[i]);
        }
        pos++;
    }

    private void loadBroadcastList() {
        ConfigurationSection section = config.getConfigurationSection("broadcasts");
        if(section != null) {
            for(String key : section.getKeys(false)) {
                List<String> messages = new ArrayList<>();
                List<?> messageList = section.getList(key);
                if(messageList != null) {
                    for(Object obj : messageList) {
                        if(obj instanceof String) {
                            messages.add(ChatColor.translateAlternateColorCodes('&', (String) obj));
                        }
                    }
                }
                broadcastList.add(messages.toArray(new String[0]));
            }
        }
        if(broadcastList.isEmpty()) {
            Bukkit.getLogger().warning("[Strings] No broadcasts found in broadcasts.yml");
        }
    }
}
