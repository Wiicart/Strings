package com.pedestriamc.strings.log;

import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import static com.pedestriamc.strings.log.LogListener.*;

/**
 * Represents what type of log is being logged.
 */
class LogType {

    private File file;
    private boolean enabled;
    private final String path;
    private final String configKey;
    private final Function<LogManager, Listener> function;

    /**
     * LogType for when signs are placed.
     */
    static final LogType SIGN = new LogType("/logs/signs.txt", "sign-log",
            SignListener::new);
    /**
     * Log type for when a player sends a direct message to another player via Strings /msg.
     */
    static final LogType DIRECT_MESSAGE = new LogType("/logs/direct-messages.txt", "dm-log",
            DirectMessageListener::new);
    /**
     * Log type for all chat messages sent in Channels that call an event.
     */
    static final LogType CHAT = new LogType("/logs/chat.txt", "chat-log",
            ChatListener::new);
    /**
     * Log type sent when players send commands.
     */
    static final LogType COMMAND = new LogType("/logs/commands.txt", "command-log",
            CommandListener::new);
    /**
     * Log type sent when a chat message is filtered.
     */
    static final LogType FILTER = new LogType("/logs/filtered-messages.txt", "filter-log",
            ChatFilterListener::new);

    /**
     * Provides a List of all LogTypes
     */
    static final List<LogType> TYPES = List.of(SIGN, DIRECT_MESSAGE, CHAT, COMMAND, FILTER);

    protected LogType(String path, String configKey, Function<LogManager, Listener> function) {
        this.path = path;
        this.configKey = configKey;
        this.function = function;
    }

    Listener createListener(LogManager manager) {
        return function.apply(manager);
    }

    File getFile() {
        return file;
    }

    boolean isEnabled() {
        return enabled;
    }

    String getPath() {
        return path;
    }

    String getConfigKey() {
        return configKey;
    }

    void setFile(File file) {
        this.file = file;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
