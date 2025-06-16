package com.pedestriamc.strings.manager;

import com.pedestriamc.strings.Strings;
import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * Manages plugin file loading, updating, access, and saving.
 */
@SuppressWarnings("unused")
public final class FileManager {

    private final Strings strings;

    private File broadcastsFile;
    private File messagesFile;
    private File channelsFile;
    private File usersFile;

    private FileConfiguration broadcastsFileConfig;
    private FileConfiguration messagesFileConfig;
    private FileConfiguration usersFileConfig;
    private FileConfiguration channelsFileConfig;
    private FileConfiguration logsFileConfig;
    private FileConfiguration moderationFileConfig;

    public FileManager(Strings strings) {
        this.strings = strings;
        load();
    }

    private void load() {
        strings.saveDefaultConfig();
        setupCustomConfigs();
        updateConfigs();
    }

    private void setupCustomConfigs() {
        broadcastsFile = new File(strings.getDataFolder(), "broadcasts.yml");
        messagesFile = new File(strings.getDataFolder(), "messages.yml");
        usersFile = new File(strings.getDataFolder(), "users.yml");
        channelsFile = new File(strings.getDataFolder(), "channels.yml");
        File logsFile = new File(strings.getDataFolder(), "logs.yml");
        File moderationFile = new File(strings.getDataFolder(), "moderation.yml");

        createIfDoesNotExist(broadcastsFile, "broadcasts.yml");
        createIfDoesNotExist(messagesFile, "messages.yml");
        createIfDoesNotExist(usersFile, "users.yml");
        createIfDoesNotExist(channelsFile, "channels.yml");
        createIfDoesNotExist(logsFile, "logs.yml");
        createIfDoesNotExist(moderationFile, "moderation.yml");

        broadcastsFileConfig = YamlConfiguration.loadConfiguration(broadcastsFile);
        messagesFileConfig = YamlConfiguration.loadConfiguration(messagesFile);
        usersFileConfig = YamlConfiguration.loadConfiguration(usersFile);
        channelsFileConfig = YamlConfiguration.loadConfiguration(channelsFile);
        logsFileConfig = YamlConfiguration.loadConfiguration(logsFile);
        moderationFileConfig = YamlConfiguration.loadConfiguration(moderationFile);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createIfDoesNotExist(@NotNull File file, @NotNull String resourcePath) {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            strings.saveResource(resourcePath, false);
        }
    }

    /**
     * Updates YML files
     * <a href="https://github.com/tchristofferson/Config-Updater">...</a>
     */
    private void updateConfigs() {
        updateIfPresent("config.yml");
        updateIfPresent("messages.yml");
        updateIfPresent("moderation.yml");
    }

    private void updateIfPresent(String resourceName) {
        File file = new File(strings.getDataFolder(), resourceName);
        if(file.exists()) {
            try {
                ConfigUpdater.update(strings, resourceName, file);
            } catch(IOException e) {
                strings.warning("Failed to update file " + resourceName + ". " + e.getMessage());
            }
        }
    }

    public FileConfiguration getConfig() {
        return strings.getConfig();
    }

    public FileConfiguration getUsersFileConfig() {
        return usersFileConfig;
    }

    public FileConfiguration getBroadcastsFileConfig() {
        return broadcastsFileConfig;
    }

    public FileConfiguration getMessagesFileConfig() {
        return messagesFileConfig;
    }

    public FileConfiguration getChannelsFileConfig() {
        return channelsFileConfig;
    }

    public FileConfiguration getLogsFileConfig() {
        return logsFileConfig;
    }

    public FileConfiguration getModerationFileConfig() {
        return moderationFileConfig;
    }

    private static final Object userLock = new Object();
    public void saveUsersFile() {
        strings.async(() -> {
            synchronized(userLock) {
                try {
                    usersFileConfig.save(usersFile);
                } catch(Exception e) {
                    strings.warning("An error occurred while saving the users file: " + e.getMessage());
                }
            }
        });
    }

    private static final Object channelsLock = new Object();
    public synchronized void saveChannelsFile() {
        strings.async(() -> {
            synchronized(channelsLock) {
                try {
                    channelsFileConfig.save(channelsFile);
                } catch(IOException e) {
                    strings.warning("An error occurred while saving the channels file: " + e.getMessage());
                }
            }
        });
    }

    @SuppressWarnings({"unused", "CallToPrintStackTrace"})
    public void saveMessagesFile() {
        try {
            messagesFileConfig.save(messagesFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unused", "CallToPrintStackTrace"})
    public void saveBroadcastsFile() {
        try {
            broadcastsFileConfig.save(broadcastsFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
