package com.pedestriamc.strings.discord.configuration;

import org.jetbrains.annotations.NotNull;

public final class Option {

    private Option() {}

    public enum Text {
        TOKEN("token", "0.0.0"),
        MINECRAFT_FORMAT("minecraft-format", "&8[&9Discord&8] &f{nickname} &7» &f{message}"),
        DISCORD_FORMAT("discord-format", "{display-name} » {message}"),
        MENTION_FORMAT("mention-format", "&e[Discord] {sender} mentioned you."),
        SERVER_ONLINE_MESSAGE("online-message", ":green_circle: **Server Online**"),
        SERVER_OFFLINE_MESSAGE("offline-message", ":red_circle: **Server Offline**"),
        ACTIVITY("activity", "playing"),
        ACTIVITY_ITEM("activity-item", "Minecraft"),
        AVATAR_URL("avatar-url", "https://mineskin.eu/armor/bust/{uuid}/35.png"),
        JOIN_MESSAGE("join-message", "{username} joined the server"),
        LEAVE_MESSAGE("leave-message", "{username} left the server"),
        DISCORD_COMMAND_MESSAGE("discord-cmd-message", "&9discord.com"),
        DISCORD_STATUS("online-status", "online"),
        DISCORD_CONSOLE_ID("console-id", "0"),;

        final String key;
        final String defaultValue;

        Text(@NotNull String key, @NotNull String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }
    }

    public enum Bool {
        GLOBAL("global", true),
        ENABLE_MENTIONS_FROM_GAME("mentions-from-game", true),
        ENABLE_MENTIONS_TO_GAME("mentions-to-game", true),
        ENABLE_JOIN_LEAVE_MESSAGES("send-join-leave", true),
        ENABLE_DEATH_MESSAGES("send-death-messages", true),
        ENABLE_ADVANCEMENT_MESSAGES("send-advancements", true),;

        final String key;
        final boolean defaultValue;

        Bool(@NotNull String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }
    }
}
