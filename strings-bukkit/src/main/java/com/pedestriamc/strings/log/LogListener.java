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

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Stores all Listener classes used for logging.
 */
final class LogListener {

    private LogListener() {}

    static final class SignListener implements Listener {

        private static final String TEMPLATE =
                "[{date}] Player {name} updated or placed a sign: \"{content}\"";

        private final LogManager logManager;

        SignListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(@NotNull SignChangeEvent event) {
            String log = TEMPLATE
                    .replace("{date}", LocalDateTime.now().toString())
                    .replace("{name}", event.getPlayer().getName())
                    .replace("{content}", Arrays.toString(event.getLines()));
            logManager.log(LogType.SIGN, log);
        }
    }

    static final class DirectMessageListener implements Listener {

        private static final String TEMPLATE =
                "[{date}] Player {sender} -> {recipient} \"{message}\"";

        private final LogManager logManager;

        DirectMessageListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(@NotNull PlayerDirectMessageEvent event) {
            String log = TEMPLATE
                    .replace("{date}", LocalDateTime.now().toString())
                    .replace("{sender}", event.getSender().getName())
                    .replace("{recipient}", event.getRecipient().getName())
                    .replace("{message}", event.getMessage());
            logManager.log(LogType.DIRECT_MESSAGE, log);
        }
    }

    static final class ChatListener implements Listener {

        private static final String TEMPLATE =
                "[{date}] Player {name} sent a message in channel \"{channel}\": \"{message}\"";

        private final LogManager logManager;

        ChatListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(AsyncPlayerChatEvent event) {
            if(event instanceof ChannelChatEvent chatEvent) {
                String log = TEMPLATE
                        .replace("{date}", LocalDateTime.now().toString())
                        .replace("{name}", event.getPlayer().getName())
                        .replace("{channel}", chatEvent.getChannel().getName())
                        .replace("{message}", event.getMessage());
                logManager.log(LogType.CHAT, log);
            }
        }
    }

    static final class CommandListener implements Listener {

        private static final String TEMPLATE =
                "[{date}] Player {name} issued command: \"{command}\"";

        private final LogManager logManager;

        CommandListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(@NotNull PlayerCommandPreprocessEvent event) {
            String log = TEMPLATE
                    .replace("{date}", LocalDateTime.now().toString())
                    .replace("{name}", event.getPlayer().getName())
                    .replace("{command}", event.getMessage());
            logManager.log(LogType.COMMAND, log);
        }
    }

    static final class ChatFilterListener implements Listener {

        private static final String TEMPLATE =
                "[{date}] Player {name} had a message filtered. Original: \"{original}\", Filtered: \"{filtered}\"";

        private final LogManager logManager;

        ChatFilterListener(LogManager logManager) {
            this.logManager = logManager;
        }

        @EventHandler
        void onEvent(@NotNull PlayerChatFilteredEvent event) {
            String log = TEMPLATE
                    .replace("{date}", LocalDateTime.now().toString())
                    .replace("{name}", event.getPlayer().getName())
                    .replace("{original}", event.getOriginalMessage())
                    .replace("{filtered}", event.getFilteredMessage());
            logManager.log(LogType.FILTER, log);
        }
    }
}
