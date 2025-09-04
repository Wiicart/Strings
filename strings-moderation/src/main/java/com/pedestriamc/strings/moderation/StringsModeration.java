package com.pedestriamc.strings.moderation;

import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.moderation.impl.Registrar;
import com.pedestriamc.strings.moderation.configuration.Configuration;
import com.pedestriamc.strings.moderation.impl.APIImplementation;
import com.pedestriamc.strings.moderation.listener.ReloadListener;
import com.pedestriamc.strings.moderation.listener.SignChangeListener;
import com.pedestriamc.strings.moderation.manager.ChatFilter;
import com.pedestriamc.strings.moderation.manager.CooldownManager;
import com.pedestriamc.strings.moderation.manager.LinkFilter;
import com.pedestriamc.strings.moderation.manager.RepetitionManager;
import com.pedestriamc.strings.moderation.listener.ChatListener;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class StringsModeration extends JavaPlugin {

    private ChatFilter chatFilter;
    private CooldownManager cooldownManager;
    private LinkFilter linkFilter;
    private RepetitionManager repetitionManager;
    private Configuration config;

    @Override
    public void onEnable() {
        info("Enabling...");
        if (!StringsProvider.isEnabled()) {
            getLogger().warning("Failed to connect to Strings API, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Plugin plugin = getServer().getPluginManager().getPlugin("Strings");
        if (plugin == null) {
            getLogger().warning("Failed to get Strings config file, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        config = new Configuration(plugin);

        instantiate();
        registerAPI();

        info("StringsModeration enabled.");
    }

    @Override
    public void onDisable() {
        info("Disabling...");
        config = null;
        chatFilter = null;
        cooldownManager = null;
        linkFilter = null;
        repetitionManager = null;
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        try {
            Registrar.unregister(this);
        } catch(Exception ignored) {}
        info("StringsModeration disabled.");
    }

    public void reload() {
        onDisable();
        onLoad();
        onEnable();
    }

    private void instantiate() {
        chatFilter = new ChatFilter(this);
        cooldownManager = new CooldownManager(this);
        linkFilter = new LinkFilter(this);
        repetitionManager = new RepetitionManager(this);
        registerListener(new ChatListener(this));
        registerListener(new ReloadListener(this));
        registerListener(new SignChangeListener(this));
    }

    private void registerAPI() {
        try {
            Registrar.register(new APIImplementation(this), this);
        } catch(Exception ex) {
            getLogger().warning("Failed to register StringsModeration API");
        }
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
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

    public Configuration getConfiguration() {
        return config;
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

    public void synchronous(@NotNull Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }

}
