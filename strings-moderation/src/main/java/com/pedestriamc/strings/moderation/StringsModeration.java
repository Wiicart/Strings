package com.pedestriamc.strings.moderation;

import com.pedestriamc.strings.api.StringsAPI;
import com.pedestriamc.strings.api.StringsProvider;
import com.pedestriamc.strings.moderation.spam.SpamManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class StringsModeration extends JavaPlugin {

    private StringsAPI stringsAPI;
    private FileConfiguration config;

    @SuppressWarnings("FieldCanBeLocal")
    private SpamManager spamManager;

    @Override
    public void onEnable() {

        try {
            stringsAPI = StringsProvider.get();
        } catch (IllegalStateException e) {
            getLogger().info("Failed to connect to Strings API, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            config = getServer().getPluginManager().getPlugin("Strings").getConfig();
        } catch (NullPointerException e) {
            getLogger().info("Failed to get Strings config file, disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

    }

    private void instantiate() {
        spamManager = new SpamManager(this);
    }

    public StringsAPI getStringsAPI() {
        return stringsAPI;
    }

    @Override
    @NotNull
    public FileConfiguration getConfig() {
        return config;
    }


}
