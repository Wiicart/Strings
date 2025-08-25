package com.pedestriamc.strings.api.settings;

import java.util.List;

public final class Option {

    private Option() {}

    public enum Bool {
        PROCESS_CHATCOLOR("process-in-chat-colors", true),
        ENABLE_CHATCOLOR_COMMAND("enable-chatcolor", true),
        PROCESS_PLACEHOLDERS("process-in-chat-placeholders", true),
        ENABLE_JOIN_LEAVE_MESSAGE("enable-join-leave-messages", true),
        USE_CUSTOM_JOIN_LEAVE("custom-join-leave-message", false),
        ENABLE_MOTD("enable-motd", false),
        USE_PAPI("placeholder-api", true),
        ENABLE_MENTIONS("enable-mentions", true),
        ENABLE_MENTIONING_EVERYONE("mention-everyone", true),
        ENABLE_HELPOP("enable-helpop", true),
        DISABLE_HELPOP_COMMAND("other-helpop", false),
        ENABLE_DIRECT_MESSAGES("msg-enabled", true),
        ENABLE_DEATH_MESSAGES("death-messages.enable", true),
        USE_CUSTOM_DEATH_MESSAGES("death-messages.custom", true),
        ENABLE_RULES_COMMAND("enable-rules", true);

        final String key;
        final boolean defaultValue;

        Bool(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public boolean getDefault() {
            return defaultValue;
        }
    }

    public enum Text {
        DIRECT_MESSAGE_FORMAT_OUT("msg-format-outgoing", "&8(&7me &8» &7{recipient_username}&8) &e{message}"),
        DIRECT_MESSAGE_FORMAT_IN("msg-format-receiving", "&8(&7{sender_username} &8» &7me&8) &e{message}"),
        SOCIAL_SPY_FORMAT("social-spy-format", "&8[&cSocialSpy&8] &8(&7{sender_username} &8» &7{recipient_username}&8) &7{message}"),
        JOIN_MESSAGE("join-message", "&8[&a+&8] &f{username} &7has joined the server."),
        LEAVE_MESSAGE("leave-message", "&8[&4-&8] &f{username} &7has left the server."),
        BROADCAST_FORMAT("broadcast-format", "&8[&3Broadcast&8] &f"),
        MENTION_IN_CHAT_FORMAT("mention-chat-format", "&7"),
        MENTION_COLOR("mention-color", "&e"),
        MENTION_TEXT_ACTION_BAR("mention-format","&e%sender% mentioned you."),
        MENTION_SOUND("mention-sound", "BLOCK_NOTE_BLOCK_PLING"),
        DELETION_BUTTON_FORMAT("deletion-button", "<dark_gray>[<red>×</red>]</dark_gray>"),
        DELETION_BUTTON_HOVER("deletion-hover", "<red>Click this to delete the message.</red>"),
        RULES_MESSAGE("rules-message", "<b>Rules\n - Example Rule");

        final String key;
        final String defaultValue;

        Text(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public String getDefault() {
            return defaultValue;
        }
    }

    public enum Double {
        MENTION_PITCH("mention-pitch", 0.594604),
        MENTION_VOLUME("mention-vol", 10D);

        final String key;
        final double defaultValue;

        Double(String key, double defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public double getDefault() {
            return defaultValue;
        }
    }

    public enum StringList {
        MOTD("motd", java.util.List.of("&fWelcome to the server, {username}!", "&fHave fun!"));

        final String key;
        final List<String> defaultValue;

        StringList(String key, List<String> defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public String getKey() {
            return key;
        }

        public List<String> getDefault() {
            return defaultValue;
        }
    }

}
