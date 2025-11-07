package com.pedestriamc.strings.api.settings;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Key.From(path = "config.yml")
public final class Option {

    private Option() {}

    @Internal
    public interface CoreKey<T> extends Key<T> {}

    public enum Bool implements CoreKey<Boolean> {
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
        ENABLE_RESOLUTION_VALIDATION("resolution-validation", true),
        @From(path = "death-messages.yml")
        DEATH_MESSAGES_ENABLE("enable", true),
        @From(path = "death-messages.yml")
        DEATH_MESSAGES_USE_CUSTOM("custom", false),
        ENABLE_RULES_COMMAND("enable-rules", true),
        BROADCAST_SOUND_ENABLE("broadcast-sound.enable", true),
        @From(path = "emojis/emoji-config.yml")
        ENABLE_EMOJI_REPLACEMENT("replacement", true),
        @From(path = "emojis/emoji-config.yml")
        ENABLE_EMOJI_RESOURCE_PACK("resource-pack", true);

        final String key;
        final boolean defaultValue;

        Bool(String key, boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        @NotNull
        public String key() {
            return key;
        }

        @Override
        @NotNull
        public Boolean defaultValue() {
            return defaultValue;
        }
    }

    public enum Text implements CoreKey<String> {
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
        RULES_MESSAGE("rules-message", "<b>Rules\n - Example Rule"),
        BROADCAST_SOUND_NAME("broadcast-sound.name", "block.note_block.bell"),
        @From(path = "emojis/emoji-config.yml")
        TEXTURES_MODRINTH_ID("pack-id", "mh6HfOTP"),
        @From(path = "emojis/emoji-config.yml")
        EMOJI_COMMAND_HEADER("command-header", "<dark_gray><st>+-----------</st>[<yellow>Emoji Menu</yellow>]<st>-----------+</st></dark_gray>"),
        @From(path = "emojis/emoji-config.yml")
        EMOJI_COMMAND_FOOTER("command-footer", "Textures from the <click:open_url:'https://modrinth.com/resourcepack/allure-emoji-pack'><aqua>Allure Emoji Pack</aqua></click>");

        final String key;
        final String defaultValue;

        Text(String key, String defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        @NotNull
        public String key() {
            return key;
        }

        @Override
        @NotNull
        public String defaultValue() {
            return defaultValue;
        }
    }

    public enum Double implements CoreKey<java.lang.Double> {
        MENTION_PITCH("mention-pitch", 0.594604D),
        MENTION_VOLUME("mention-vol", 10D),
        BROADCAST_SOUND_PITCH("broadcast-sound.pitch", 0.5D),
        BROADCAST_SOUND_VOLUME("broadcast-sound.volume", 1.0D);

        final String key;
        final double defaultValue;

        Double(String key, double defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        @NotNull
        public String key() {
            return key;
        }

        @Override
        @NotNull
        public java.lang.Double defaultValue() {
            return defaultValue;
        }
    }

    public enum StringList implements CoreKey<List<String>> {
        MOTD("motd", java.util.List.of("&fWelcome to the server, {username}!", "&fHave fun!")),
        @From(path = "emojis/emoji-config.yml")
        TEXTURED_EMOJIS("textured-list", java.util.List.of());

        final String key;
        final List<String> defaultValue;

        StringList(String key, List<String> defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        @Override
        @NotNull
        public String key() {
            return key;
        }

        @Override
        @NotNull
        public List<String> defaultValue() {
            return defaultValue;
        }
    }

}
