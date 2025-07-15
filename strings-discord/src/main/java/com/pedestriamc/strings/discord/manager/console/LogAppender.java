package com.pedestriamc.strings.discord.manager.console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.jetbrains.annotations.NotNull;

/* https://www.spigotmc.org/threads/capturing-console-output.307132/ */
class LogAppender extends AbstractAppender {

    private final ConsoleDiscordManager manager;

    LogAppender(@NotNull ConsoleDiscordManager manager) {
        super("StringsLogAppender", null, null, false, Property.EMPTY_ARRAY);
        this.manager = manager;

        // org.apache.logging.log4j.core.Logger
        ((Logger) LogManager.getRootLogger()).addAppender(this);
    }

    @Override
    public void append(LogEvent event) {
        event = event.toImmutable();
        manager.postMessage(
                "[" +event.getLoggerName() + " | " + event.getLevel().toString() + "]"
                + event.getMessage().getFormattedMessage()
        );
    }
}
