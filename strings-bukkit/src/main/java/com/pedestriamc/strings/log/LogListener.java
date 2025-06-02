package com.pedestriamc.strings.log;

import com.pedestriamc.strings.api.event.channel.ChannelChatEvent;
import com.pedestriamc.strings.api.event.moderation.PlayerChatFilteredEvent;
import com.pedestriamc.strings.api.event.moderation.PlayerDirectMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;

/**
 * Stores all Listener classes used for logging.
 */
@SuppressWarnings("ClassCanBeRecord")
class LogListener {

    private LogListener() {

    }

    static class SignListener implements Listener {

        private final LogManager logManager;

        SignListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(@NotNull SignChangeEvent event) {
            String log = "["
                    + new Date() + "] Player "
                    + event.getPlayer().getName()
                    + " updated or placed a sign: "
                    + Arrays.toString(event.getLines());

            logManager.log(LogType.SIGN, log);
        }
    }

    static class DirectMessageListener implements Listener {

        private final LogManager logManager;

        DirectMessageListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(@NotNull PlayerDirectMessageEvent event) {
            String log = "["
                    + new Date() + "] Player "
                    + event.getSender().getName()
                    + " -> "
                    + event.getRecipient().getName()
                    + ": \""
                    + event.getMessage()
                    + "\"";
            logManager.log(LogType.DIRECT_MESSAGE, log);
        }
    }

    static class ChatListener implements Listener {

        private final LogManager logManager;

        ChatListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(AsyncPlayerChatEvent event) {
            if(event instanceof ChannelChatEvent chatEvent) {
                String log = "["
                        + new Date() + "] Player "
                        + event.getPlayer().getName()
                        + " sent a message in channel \""
                        + chatEvent.getChannel().getName()
                        + "\": \"" + chatEvent.getMessage() + "\"";

                logManager.log(LogType.CHAT, log);
            }
        }
    }

    static class CommandListener implements Listener {

        private final LogManager logManager;

        CommandListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(@NotNull PlayerCommandPreprocessEvent event) {
            String log = "["
                    + new Date() + "] Player "
                    + event.getPlayer().getName()
                    + " issued command: \""
                    + event.getMessage()
                    + "\"";
            logManager.log(LogType.COMMAND, log);
        }
    }

    static class ChatFilterListener implements Listener {

        final LogManager logManager;

        ChatFilterListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(@NotNull PlayerChatFilteredEvent event) {
            String log = "["
                    + new Date() + "] Player "
                    + event.getPlayer().getName()
                    + " had a message filtered. Original: \""
                    + event.getOriginalMessage()
                    + "\", Filtered: \" "
                    + event.getFilteredMessage()
                    + "\"";
            logManager.log(LogType.FILTER, log);
        }
    }
}
