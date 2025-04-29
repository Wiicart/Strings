package com.pedestriamc.strings.moderation;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.moderation.listener.ReloadListener;
import com.pedestriamc.strings.moderation.manager.ChatFilter;
import com.pedestriamc.strings.moderation.manager.CooldownManager;
import com.pedestriamc.strings.moderation.manager.LinkFilter;
import com.pedestriamc.strings.moderation.manager.RepetitionManager;
import com.pedestriamc.strings.moderation.listener.ChatListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class StringsModeration extends JavaPlugin {

    private FileConfiguration config;
    private ChatFilter chatFilter;
    private CooldownManager cooldownManager;
    private LinkFilter linkFilter;
    private RepetitionManager repetitionManager;

    @Override
    public void onEnable() {
        try {
            StringsProvider.get();
        } catch (IllegalStateException e) {
            info("Failed to connect to Strings API, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Plugin plugin = getServer().getPluginManager().getPlugin("Strings");
        if(plugin == null) {
            info("Failed to get Strings config file, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        File file = new File(plugin.getDataFolder(), "moderation.yml");
        config = YamlConfiguration.loadConfiguration(file);

        instantiate();
        info("StringsModeration enabled.");
    }

    @Override
    public void onDisable() {
        config = null;
        chatFilter = null;
        cooldownManager = null;
        linkFilter = null;
        repetitionManager = null;
        info("StringsModeration disabled.");
    }

    public void reload() {
        onDisable();
        onLoad();
        onEnable();
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);

    }

    private void instantiate() {
        chatFilter = new ChatFilter(this);
        cooldownManager = new CooldownManager(this);
        linkFilter = new LinkFilter(this);
        repetitionManager = new RepetitionManager(this);
        registerListener(new ChatListener(this));
        registerListener(new ReloadListener(this));
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    @NotNull
    public FileConfiguration getConfig() {
        return config;
    }

    public ChatFilter getChatFilter() {
        return chatFilter;
    }

    public RepetitionManager getRepetitionManager() {
        return repetitionManager;
    }

    public LinkFilter getLinkFilter() {
        return linkFilter;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    /**
     * Calculates tick equivalent of seconds or minutes. Example: 1m, 1s, etc.
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

    public void info(String message) {
        getLogger().info(message);
    }

}
