package com.pedestriamc.strings.log;

import com.pedestriamc.strings.Strings;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;

//TODO test
/**
 * Handles all logging.
 */
public class LogManager {

    private final Strings strings;

    public LogManager(@NotNull Strings strings) {
        this.strings = strings;
        loadTypes(strings.files().getLogsFileConfig());
    }

    /**
     * Logs a message to a file, based on LogType, and if the LogType is enabled.
     * @param type The LogType of the log.
     * @param log The log message.
     */
    void log(@NotNull LogType type, @NotNull String log) {
        if(type.isEnabled()) {
            write(type.getFile(), log);
        }
    }

    /**
     * Writes a log to a file.
     * @param file The file to write the log to.
     * @param log The log message.
     */
    private synchronized void write(@NotNull File file, @NotNull String log) {
        strings.async(() -> {
            try(FileWriter fileWriter = new FileWriter(file, true)) {
                fileWriter.write(log);
                fileWriter.write(System.lineSeparator());
            } catch(Exception e) {
                strings.warning("Failed to write log to file: " + file.getAbsolutePath());
                strings.warning("Error: " + e.getMessage());
            }
        });
    }

    private void loadTypes(@NotNull FileConfiguration config) {
        for(LogType type : LogType.TYPES) {
            type.setEnabled(config.getBoolean(type.getConfigKey()));
            if(type.isEnabled()) {
                register(type.createListener(this));
            }

            File file = new File(strings.getDataFolder(), type.getPath());
            prepareFile(file);
            type.setFile(file);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void prepareFile(@NotNull File file) {
        if(!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch(Exception e) {
                strings.warning("Failed to create file \"" + file.getName() + "\"");
            }
        }
    }

    private void register(@NotNull Listener listener) {
        strings.getServer().getPluginManager().registerEvents(listener, strings);
    }

}
