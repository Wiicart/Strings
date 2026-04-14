package com.pedestriamc.strings.common.user;

import com.pedestriamc.strings.api.StringsPlatform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.discord.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * User Builder class
 */
public final class UserBuilder<U> {

    private final Function<UserBuilder<U>, U> buildFunction;

    private final StringsPlatform strings;
    private final UUID uuid;
    private final boolean isNew;

    private Channel activeChannel;

    private Set<Channel> channels;
    private Set<Channel> mutes;
    private Set<Monitorable> monitoredChannels;
    private Set<UUID> ignored;

    private String chatColor;
    private String prefix;
    private String suffix;
    private String displayName;

    private boolean mentionsEnabled = true; // default
    private boolean msgEnabled = true; // default

    private Snowflake discordId = Snowflake.empty();

    public UserBuilder(@NotNull Function<UserBuilder<U>, U> buildFunction, @NotNull StringsPlatform strings, @NotNull UUID uuid, boolean isNew) {
        this.buildFunction = buildFunction;
        this.strings = strings;
        this.uuid = uuid;
        this.isNew = isNew;
    }

    @NotNull
    public UserBuilder<U> channels(Set<Channel> channels) {
        this.channels = channels;
        return this;
    }

    @NotNull
    public UserBuilder<U> monitoredChannels(Set<Monitorable> monitoredChannels) {
        this.monitoredChannels = monitoredChannels;
        return this;
    }

    @NotNull
    public UserBuilder<U> ignoredPlayers(Set<UUID> ignored) {
        this.ignored = ignored;
        return this;
    }

    @NotNull
    public UserBuilder<U> mutedChannels(Set<Channel> mutes) {
        this.mutes = mutes;
        return this;
    }

    @NotNull
    public UserBuilder<U> chatColor(String chatColor) {
        this.chatColor = chatColor;
        return this;
    }

    @NotNull
    public UserBuilder<U> prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @NotNull
    public UserBuilder<U> suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    @NotNull
    public UserBuilder<U> displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @NotNull
    public UserBuilder<U> activeChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
        return this;
    }

    @NotNull
    public UserBuilder<U> mentionsEnabled(boolean mentionsEnabled) {
        this.mentionsEnabled = mentionsEnabled;
        return this;
    }

    @NotNull
    public UserBuilder<U> msgEnabled(boolean msgEnabled) {
        this.msgEnabled = msgEnabled;
        return this;
    }

    @NotNull
    public UserBuilder<U> discordId(@NotNull Snowflake discordId) {
        this.discordId = discordId;
        return this;
    }

    @Contract("-> new")
    public @NotNull U build() {
        return buildFunction.apply(this);
    }

    public StringsPlatform getStrings() {
        return strings;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isNew() {
        return isNew;
    }

    public Channel getActiveChannel() {
        return activeChannel;
    }

    public Set<Channel> getChannels() {
        return channels;
    }

    public Set<Channel> getMutes() {
        return mutes;
    }

    public Set<Monitorable> getMonitoredChannels() {
        return monitoredChannels;
    }

    public Set<UUID> getIgnores() {
        return ignored;
    }

    public String getChatColor() {
        return chatColor;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isMentionsEnabled() {
        return mentionsEnabled;
    }

    public boolean isMsgEnabled() {
        return msgEnabled;
    }

    public Snowflake getDiscordId() {
        return discordId;
    }
}