package com.pedestriamc.strings.user;

import com.pedestriamc.strings.Strings;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Monitorable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

/**
 * User Builder class
 */
public final class UserBuilder {

    final @NotNull Strings strings;
    final @NotNull UUID uuid;
    final boolean retained;

    Channel activeChannel;
    Set<Channel> channels;
    Set<Monitorable> monitoredChannels;
    Set<UUID> ignored;
    String chatColor;
    String prefix;
    String suffix;
    String displayName;
    // Mentions are enabled by default
    boolean mentionsEnabled = true;

    public UserBuilder(@NotNull Strings strings, @NotNull UUID uuid, boolean retained) {
        this.strings = strings;
        this.uuid = uuid;
        this.retained = retained;
    }

    public UserBuilder channels(Set<Channel> channels) {
        this.channels = channels;
        return this;
    }

    public UserBuilder monitoredChannels(Set<Monitorable> monitoredChannels) {
        this.monitoredChannels = monitoredChannels;
        return this;
    }

    public UserBuilder ignoredPlayers(Set<UUID> ignored) {
        this.ignored = ignored;
        return this;
    }

    public UserBuilder chatColor(String chatColor) {
        this.chatColor = chatColor;
        return this;
    }

    public UserBuilder prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public UserBuilder suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public UserBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public UserBuilder activeChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
        return this;
    }

    public UserBuilder mentionsEnabled(boolean mentionsEnabled) {
        this.mentionsEnabled = mentionsEnabled;
        return this;
    }

    public User build() {
        return new User(this);
    }
}