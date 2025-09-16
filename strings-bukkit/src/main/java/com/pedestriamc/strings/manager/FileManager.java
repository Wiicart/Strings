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
    private File emojisFile;

    private FileConfiguration broadcastsFileConfig;
    private FileConfiguration messagesFileConfig;
    private FileConfiguration usersFileConfig;
    private FileConfiguration channelsFileConfig;
    private FileConfiguration logsFileConfig;
    private FileConfiguration moderationFileConfig;
    private FileConfiguration deathMessagesFileConfig;

    public FileManager(@NotNull Strings strings) {
        this.strings = strings;
        load();
    }

    private void load() {
        strings.saveDefaultConfig();
        updateConfigs();
        strings.reloadConfig();
        setupCustomConfigs();
    }

    private void setupCustomConfigs() {
        broadcastsFile = new File(strings.getDataFolder(), "broadcasts.yml");
        messagesFile = new File(strings.getDataFolder(), "messages.yml");
        usersFile = new File(strings.getDataFolder(), "users.yml");
        channelsFile = new File(strings.getDataFolder(), "channels.yml");
        emojisFile = new File(strings.getDataFolder(), "emojis.json");
        File deathMessagesFile = new File(strings.getDataFolder(), "death-messages.yml");
        File logsFile = new File(strings.getDataFolder(), "logs.yml");
        File moderationFile = new File(strings.getDataFolder(), "moderation.yml");
        File discordFile = new File(strings.getDataFolder(), "discord.yml");

        createIfDoesNotExist(broadcastsFile, "broadcasts.yml");
        createIfDoesNotExist(messagesFile, "messages.yml");
        createIfDoesNotExist(usersFile, "users.yml");
        createIfDoesNotExist(channelsFile, "channels.yml");
        createIfDoesNotExist(emojisFile, "emojis.json");
        createIfDoesNotExist(deathMessagesFile, "death-messages.yml");
        createIfDoesNotExist(logsFile, "logs.yml");
        createIfDoesNotExist(moderationFile, "moderation.yml");
        createIfDoesNotExist(discordFile, "discord.yml");

        broadcastsFileConfig = YamlConfiguration.loadConfiguration(broadcastsFile);
        messagesFileConfig = YamlConfiguration.loadConfiguration(messagesFile);
        usersFileConfig = YamlConfiguration.loadConfiguration(usersFile);
        channelsFileConfig = YamlConfiguration.loadConfiguration(channelsFile);
        deathMessagesFileConfig = YamlConfiguration.loadConfiguration(deathMessagesFile);
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
        updateIfPresent("death-messages.yml");
    }

    private void updateIfPresent(String resourceName) {
        File file = new File(strings.getDataFolder(), resourceName);
        if(file.exists()) {
            try {
                ConfigUpdater.update(strings, resourceName, file);
            } catch(IOException e) {
                strings.warning("Failed to update file " + resourceName + ".\n" + e.getMessage());
            }
        }
    }

    @NotNull
    public File getEmojisFile() {
        return emojisFile;
    }

    @NotNull
    public FileConfiguration getConfig() {
        return strings.getConfig();
    }

    @NotNull
    public FileConfiguration getUsersFileConfig() {
        return usersFileConfig;
    }

    @NotNull
    public FileConfiguration getBroadcastsFileConfig() {
        return broadcastsFileConfig;
    }

    @NotNull
    public FileConfiguration getMessagesFileConfig() {
        return messagesFileConfig;
    }

    @NotNull
    public FileConfiguration getChannelsFileConfig() {
        return channelsFileConfig;
    }

    @NotNull
    public FileConfiguration getDeathMessagesFileConfig() {
        return deathMessagesFileConfig;
    }

    @NotNull
    public FileConfiguration getLogsFileConfig() {
        return logsFileConfig;
    }

    @NotNull
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
