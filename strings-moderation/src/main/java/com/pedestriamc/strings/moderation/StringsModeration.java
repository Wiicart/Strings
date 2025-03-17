package com.pedestriamc.strings.moderation;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.moderation.chat.ChatModerationManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class StringsModeration extends JavaPlugin {

    private FileConfiguration config;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private ChatModerationManager chatModerationManager;

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onEnable() {
        try {
            StringsProvider.get();
        } catch (IllegalStateException e) {
            getLogger().info("Failed to connect to Strings API, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Plugin plugin = getServer().getPluginManager().getPlugin("Strings");
        if(plugin == null) {
            getLogger().info("Failed to get Strings config file, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        File file = new File(plugin.getDataFolder(), "moderation.yml");
        config = YamlConfiguration.loadConfiguration(file);

        instantiate();
        getLogger().info("Enabled!");
    }

    private void instantiate() {
        chatModerationManager = new ChatModerationManager(this, config);
    }

    public ChatModerationManager getChatModerationManager() {
        return chatModerationManager;
    }

    @Override
    @NotNull
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Calculates tick equivalent of seconds or minutes. Example: 1m, 1s, etc..
     * @param time the time to be converted
     * @return a long of the tick value. Returns -1 if syntax is invalid.
     */
    public static long calculateTicks(String time) {
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
