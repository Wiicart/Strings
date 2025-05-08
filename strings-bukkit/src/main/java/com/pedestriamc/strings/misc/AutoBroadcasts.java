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
        config = strings.files().getBroadcastsFileConfig();
        order = config.getString("sequence");
        if(config.getBoolean("enabled", false)) {
            interval = calculateTicks(config.getString("delay"));
            if(interval == -1L) {
                Bukkit.getLogger().info("[Strings] Invalid delay for auto broadcasts. Defaulting to 3 minutes. ");
                interval = 3600L;
            }

            loadBroadcastList();
            schedule();
        }
    }

    private void schedule() {
        scheduler.runTaskTimer(strings, this::broadcastMessage, 20L, interval);
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
                        if(obj instanceof String string) {
                            messages.add(ChatColor.translateAlternateColorCodes('&', string));
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

    /**
     * Calculates tick equivalent of seconds or minutes. Example: 1m, 1s, etc.
     * @param time the time to be converted
     * @return a long of the tick value. Returns -1 if syntax is invalid.
     */
    private static long calculateTicks(String time) {
        String regex = "^[0-9]+[sm]$";

        if(time == null || !time.matches(regex)) {
            return -1L;
        }

        char units = time.charAt(time.length() - 1);
        time = time.substring(0, time.length() - 1);
        int delayNum = Integer.parseInt(time);

        if(units == 'm') {
            delayNum *= 60;
        }

        return delayNum * 20L;
    }
}
