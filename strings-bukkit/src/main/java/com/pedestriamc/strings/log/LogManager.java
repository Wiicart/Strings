package com.pedestriamc.strings.log;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.log.listeners.ChatFilterListener;
import com.pedestriamc.strings.log.listeners.CommandListener;
import com.pedestriamc.strings.log.listeners.LogChatListener;
import com.pedestriamc.strings.log.listeners.LogDirectMessageListener;
import com.pedestriamc.strings.log.listeners.SignListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileWriter;

public class LogManager {

    private final Strings strings;

    private final boolean doChatLogging;
    private final boolean doCommandLogging;
    private final boolean doFilterLogging;
    private final boolean doSignLogging;
    private final boolean doDirectMessageLogging;

    private File chatFile;
    private File commandFile;
    private File filterFile;
    private File signFile;
    private File directMessageFile;

    public LogManager(Strings strings) {

        this.strings = strings;
        FileConfiguration config = strings.getLogsFileConfig();

        doChatLogging = load(config, "chat-log", LogType.CHAT);
        if(doChatLogging) {
            register(new LogChatListener(this));
        }

        doCommandLogging = load(config, "command-log", LogType.COMMAND);
        if(doCommandLogging) {
            register(new CommandListener(this));
        }

        doFilterLogging = load(config, "filter-log", LogType.FILTER);
        if(doFilterLogging) {
            register(new ChatFilterListener(this));
        }

        doSignLogging = load(config, "sign-log", LogType.SIGN);
        if(doSignLogging) {
            register(new SignListener(this));
        }

        doDirectMessageLogging = load(config, "dm-log", LogType.DIRECT_MESSAGE);
        if(doDirectMessageLogging) {
            register(new LogDirectMessageListener(this));
        }

    }

    public void log(LogType logType, String log) {

        switch(logType) {

            case CHAT -> {
                if(doChatLogging) {
                    write(chatFile, log);
                }
            }

            case FILTER -> {
                if(doFilterLogging) {
                    write(filterFile, log);
                }
            }

            case SIGN -> {
                if(doSignLogging) {
                    write(signFile, log);
                }
            }

            case COMMAND -> {
                if(doCommandLogging) {
                    write(commandFile, log);
                }
            }

            case DIRECT_MESSAGE -> {
                if(doDirectMessageLogging) {
                    write(directMessageFile, log);
                }
            }

        }

    }

    private void write(File file, String log) {
        try(FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(log);
            fileWriter.write(System.lineSeparator());
        } catch(Exception ignored) {}
    }

    private boolean load(FileConfiguration config, String configOption, LogType type) {
        if(!config.getBoolean(configOption)) {
            return false;
        }

        switch(type) {
            case DIRECT_MESSAGE -> {
                directMessageFile = new File(strings.getDataFolder(), "/logs/direct-messages.txt");
                prepareFile(directMessageFile);
            }

            case FILTER -> {
                filterFile = new File(strings.getDataFolder(), "/logs/filtered-messages.txt");
                prepareFile(filterFile);
            }

            case COMMAND -> {
                commandFile = new File(strings.getDataFolder(), "/logs/commands.txt");
                prepareFile(commandFile);
            }

            case SIGN -> {
                signFile = new File(strings.getDataFolder(), "/logs/signs.txt");
                prepareFile(signFile);
            }

            case CHAT -> {
                chatFile = new File(strings.getDataFolder(), "/logs/chat.txt");
                prepareFile(chatFile);
            }
        }
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void prepareFile(File file) {
        if(!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch(Exception e) {
                Bukkit.getLogger().info("[Strings] Failed to create file \"" + file.getName() + "\"");
            }
        }
    }

    private void register(Listener listener) {
        strings.getServer().getPluginManager().registerEvents(listener, strings);
    }

}
