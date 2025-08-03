package com.pedestriamc.strings.api.message;

import org.jetbrains.annotations.NotNull;

/**
 * All Messages that can be used with the {@link Messenger}.
 */
public enum Message {
    NO_PERMS("no-perms"),
    CHAT_CLEARED("chat-cleared"),
    CHAT_CLEARED_ALL("chat-cleared-all"),
    TOO_MANY_ARGS("too-many-args"),
    COOLDOWN("cool-down"),
    UNKNOWN_PLAYER("unknown-player"),
    PLAYER_OFFLINE("player-offline"),
    NO_REPLY("no-reply"),
    INSUFFICIENT_ARGS("insufficient-args"),
    SELF_MESSAGE("self-message"),
    SOCIAL_SPY_ON("social-spy-on"),
    SOCIAL_SPY_OFF("social-spy-off"),
    CHANNEL_JOINED("channel-joined"),
    CHANNEL_ACTIVE("channel-active"),
    CHANNEL_ACTIVE_OTHER("other-player-channel-active"),
    INVALID_USE_CHANNEL("invalid-use-channel"),
    INVALID_PLAYER("invalid-player"),
    UNKNOWN_CHANNEL("channel-does-not-exist"),
    OTHER_PLAYER_NO_PERMS("other-player-no-perms"),
    OTHER_USER_JOINED_CHANNEL("other-user-joined-channel"),
    LEFT_CHANNEL("left-channel"),
    LEFT_CHANNEL_OTHER("other-user-left-channel"),
    NO_PERMS_CHANNEL("no-perms-channel"),
    NO_PERMS_CHANNEL_BROADCAST("no-perms-channel-broadcast"),
    NOT_CHANNEL_MEMBER("not-channel-member"),
    NOT_CHANNEL_MEMBER_OTHER("not-channel-member-other"),
    LINKS_PROHIBITED("links-prohibited"),
    HELPOP_DISABLED("helpop-disabled"),
    HELPOP_ON("helpop-on"),
    HELPOP_ACTIVE_PROHIBITED("helpop-active-prohibited"),
    CHANNEL_HELP("channel-help"),
    HELPOP_SENT("helpop-sent"),
    HELPOP_UNSUPPORTED_OPERATION("helpop-not-channel"),
    CANT_LEAVE_DEFAULT("cant-leave-global"),
    PROTECTED_CHANNEL_UNSUPPORTED_OPERATION("protected-channel"),
    SERVER_MUST_SPECIFY_PLAYER("server-must-specify-player"),
    UNKNOWN_STYLE_COLOR("unknown-style-color"),
    ONE_COLOR("one-color"),
    CHATCOLOR_SET("chatcolor-set"),
    CHATCOLOR_SET_OTHER("chatcolor-set-other"),
    UNKNOWN_COLOR("unknown-color"),
    CHANNEL_DISABLED("channel-disabled"),
    STRINGS_HELP("strings-help"),
    INVALID_ARGS("invalid-args"),
    MENTIONS_ENABLED("mentions-enabled"),
    MENTIONS_DISABLED("mentions-disabled"),
    ALREADY_MEMBER("already-member"),
    ALREADY_MEMBER_OTHER("already-member-other"),
    ALREADY_ACTIVE("already-active"),
    ALREADY_ACTIVE_OTHER("already-active-other"),
    NO_REPETITION("no-repetition"),
    BANNED_WORD("banned-word"),
    NO_PERM_MONITOR("no-perm-monitor"),
    MONITOR_SUCCESS("monitor-success"),
    MONITOR_SUCCESS_OTHER("monitor-success-other"),
    NOT_MONITORABLE("not-monitorable"),
    NOT_MONITORING("not-monitoring"),
    NOT_MONITORING_OTHER("not-monitoring-other"),
    UNMONITORED("un-monitored"),
    UNMONITORED_OTHER("un-monitored-other"),
    ALREADY_MONITORING("already-monitoring"),
    BROADCAST_SENT("broadcast-sent"),
    INELIGIBLE_SENDER("ineligible-sender"),
    DEFAULT_RESTRICTED("default-restricted"),
    NO_PERMS_MODIFY_OTHER("no-perms-other"),
    CANNOT_USE_ON_SELF("use-on-other"),
    PLAYER_IGNORED("player-ignored"),
    PLAYER_IGNORED_OTHER("ignored-other"),
    CANT_IGNORE("not-ignorable"),
    CANT_IGNORE_SELF("cant-ignore-self"),
    CANT_IGNORE_SELF_OTHER("cant-ignore-self-other"),
    ALREADY_MUTED("already-muted"),
    ALREADY_MUTED_OTHER("already-muted-other"),
    MUTE_SUCCESS("mute-success"),
    MUTE_SUCCESS_OTHER("mute-success-other"),
    NOT_MUTED("not-muted"),
    NOT_MUTED_OTHER("not-muted-other"),
    UNMUTE_SUCCESS("unmute-success"),
    UNMUTE_SUCCESS_OTHER("unmute-success-other"),
    CHANNEL_LIST_HEADER("channel-list-header"),
    CHANNEL_LIST_ENTRY("channel-entry"),
    NO_CHANNELS_AVAILABLE("no-channels-available"),;

    private final String key;

    Message(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public final String getKey() {
        return key;
    }
}
