package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import com.pedestriamc.strings.api.discord.Snowflake;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * User Builder class
 */
public final class UserBuilder {

    final Strings strings;
    final UUID uuid;
    final boolean isNew;

    Channel activeChannel;

    Set<Channel> channels;
    Set<Channel> mutes;
    Set<Monitorable> monitoredChannels;
    Set<UUID> ignored;

    String chatColor;
    String prefix;
    String suffix;
    String displayName;

    boolean mentionsEnabled = true; // default
    boolean msgEnabled = true; // default

    Snowflake discordId = Snowflake.empty();

    UserBuilder(@NotNull Strings strings, @NotNull UUID uuid, boolean isNew) {
        this.strings = strings;
        this.uuid = uuid;
        this.isNew = isNew;
    }

    @NotNull
    public UserBuilder channels(Set<Channel> channels) {
        this.channels = channels;
        return this;
    }

    @NotNull
    public UserBuilder monitoredChannels(Set<Monitorable> monitoredChannels) {
        this.monitoredChannels = monitoredChannels;
        return this;
    }

    @NotNull
    public UserBuilder ignoredPlayers(Set<UUID> ignored) {
        this.ignored = ignored;
        return this;
    }

    @NotNull
    public UserBuilder mutedChannels(Set<Channel> mutes) {
        this.mutes = mutes;
        return this;
    }

    @NotNull
    public UserBuilder chatColor(String chatColor) {
        this.chatColor = chatColor;
        return this;
    }

    @NotNull
    public UserBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @NotNull
    public UserBuilder suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    @NotNull
    public UserBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @NotNull
    public UserBuilder activeChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
        return this;
    }

    @NotNull
    public UserBuilder mentionsEnabled(boolean mentionsEnabled) {
        this.mentionsEnabled = mentionsEnabled;
        return this;
    }

    @NotNull
    public UserBuilder msgEnabled(boolean msgEnabled) {
        this.msgEnabled = msgEnabled;
        return this;
    }

    @NotNull
    public UserBuilder discordId(@NotNull Snowflake discordId) {
        this.discordId = discordId;
        return this;
    }

    @Contract(" -> new")
    public @NotNull User build() {
        return new User(this);
    }
}