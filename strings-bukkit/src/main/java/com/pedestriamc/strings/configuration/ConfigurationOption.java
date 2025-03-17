package com.pedestriamc.strings.configuration;

import java.util.List;

public enum ConfigurationOption {

    PROCESS_CHATCOLOR("process-in-chat-colors", true, Boolean.class),
    ENABLE_CHATCOLOR_COMMAND("enable-chatcolor", true, Boolean.class),
    PROCESS_PLACEHOLDERS("process-in-chat-placeholders", true, Boolean.class),
    DM_FORMAT_OUT("msg-format-outgoing", "&8(&7me &8» &7{recipient_username}&8) &e{message}", String.class),
    DM_FORMAT_IN("msg-format-receiving", "&8(&7{sender_username} &8» &7me&8) &e{message}", String.class),
    SOCIAL_SPY_FORMAT("social-spy-format", "&8[&cSocialSpy&8] &8(&7{sender_username} &8» &7{recipient_username}&8) &7{message}", String.class),
    JOIN_LEAVE("enable-join-leave-messages", true, Boolean.class),
    CUSTOM_JOIN_LEAVE("custom-join-leave-message", false, Boolean.class),
    JOIN_MESSAGE("join-message", "&8[&a+&8] &f{username} &7has joined the server.", String.class),
    LEAVE_MESSAGE("leave-message", "&8[&4-&8] &f{username} &7has left the server.", String.class ),
    ENABLE_MOTD("enable-motd", false, Boolean.class),
    MOTD("motd", List.of("&fWelcome to the server, {username}!", "&fHave fun!"), List.class),
    BROADCAST_FORMAT("broadcast-format", "&8[&3Broadcast&8] &f", String.class),
    USE_PAPI("placeholder-api", true, Boolean.class),
    ENABLE_MENTIONS("enable-mentions", true, Boolean.class),
    ENABLE_MENTION_EVERYONE("mention-everyone", true, Boolean.class),
    MENTION_CHAT_FORMAT("mention-chat-format", "&7", String.class),
    MENTION_COLOR("mention-color", "&e", String.class),
    MENTION_FORMAT("mention-format", "&e%sender% mentioned you.", String.class),
    MENTION_SOUND("mention-sound", "BLOCK_NOTE_BLOCK_PLING", String.class),
    MENTION_PITCH("mention-pitch", 0.594604, Double.class),
    MENTION_VOLUME("mention-vol", 10D, Double.class),
    ENABLE_HELPOP("enable-helpop", true, Boolean.class),
    DISABLE_HELPOP_COMMAND("other-helpop", false, Boolean.class);


    private final String option;
    private final Object defaultValue;
    private final Class<?> type;

    ConfigurationOption(String option, Object defaultValue, Class<?> type) {
        this.option = option;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public String getIdentifier() {
        return option;
    }

    /**
     * Provides the Config option in the file
     * @return String of the config option name
     */
    @Override
    public String toString() {
        return getIdentifier();
    }

    /**
     * Provides the default option
     * @return An Object of the default option
     */
    public Object getDefault() {
        return defaultValue;
    }

    /**
     * Provides the class type the option returns
     * @return A Class
     */
    public Class<?> getType() {
        return type;
    }
}
