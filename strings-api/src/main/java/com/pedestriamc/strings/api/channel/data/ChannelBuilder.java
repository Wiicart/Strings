package com.pedestriamc.strings.api.channel.data;

import com.google.common.base.Preconditions;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.local.LocalChannel;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * A class that holds data to create a new {@link Channel}.
 * Enter all data by using all the set methods, with the exceptions of {@link ChannelBuilder#setDistance(double)} and {@link ChannelBuilder#setWorlds(Set)} if not applicable.
 */
public final class ChannelBuilder {

    // Stores the Strings instance for use in constructing the Channels.
    @ApiStatus.Internal
    private static final JavaPlugin STRINGS = JavaPlugin.getProvidingPlugin(Channel.class);

    // Stores functions to build Channels.
    @ApiStatus.Internal
    private static final Map<String, BiFunction<JavaPlugin, ChannelBuilder, Channel>> BUILD_FUNCTIONS = new HashMap<>();


    // These fields may not be null at any point.
    private @NotNull String name;
    private @NotNull String format;
    private @NotNull Membership membership;

    // Nullable fields, default values should be provided where necessary if null.
    private @Nullable String defaultColor;
    private @Nullable String broadcastFormat;

    // LocalChannel exclusive values
    private @Nullable Set<World> worlds;
    private double distance;

    @Range(from = -1, to = Integer.MAX_VALUE)
    private int priority;
    private boolean doCooldown;
    private boolean doProfanityFilter;
    private boolean doUrlFilter;
    private boolean callEvent;

    // Registers functions for building Channels
    @ApiStatus.Internal
    static void registerBuildable(String identifier, BiFunction<JavaPlugin, ChannelBuilder, Channel> function) {
        BUILD_FUNCTIONS.put(identifier, function);
    }

    /**
     * ChannelBuilder constructor with universally required parameters.
     * @param name The Channel name.
     * @param format The Channel format.
     * @param membership The Channel membership
     */
    public ChannelBuilder(@NotNull String name, @NotNull String format, @NotNull Membership membership) {
        this.name = name;
        this.format = format;
        this.membership = membership;
    }

    /**
     * Builds a Channel
     * @param type The Type of Channel to construct.
     *             Options: {@code stringchannel, proximity, proximity_strict, world, world_strict, helpop}
     *
     * @return A new Channel of the specified type.
     * @throws IllegalArgumentException If not all required values are filled out in this ChannelBuilder,
     * or the Channel type is unknown.
     */
    @Contract("_ -> new")
    public Channel build(@NotNull String type) throws IllegalArgumentException {
        if(!BUILD_FUNCTIONS.containsKey(type)) {
            throw new IllegalArgumentException("Unknown channel type: " + type);
        }

        try {
            return BUILD_FUNCTIONS.get(type).apply(STRINGS, this);
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Provides the Channel name.
     * @return The Chanel name.
     */
    @ApiStatus.Internal
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the Channel's name.
     * @param name The new Channel name.
     * @return this
     */
    public ChannelBuilder setName(@NotNull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        this.name = name;
        return this;
    }

    /**
     * Provides the Channel's default color.
     * @return A String representation of the default color.
     */
    @ApiStatus.Internal
    public @NotNull String getDefaultColor() {
        return defaultColor != null ? defaultColor : "";
    }

    /**
     * Sets the Channel's default color.
     * @param defaultColor The new default color.
     * @return this
     */
    public ChannelBuilder setDefaultColor(String defaultColor) {
        this.defaultColor = defaultColor;
        return this;
    }

    /**
     * Provides the Channel's format when messages are sent in chat.
     * @return A String containing the format.
     */
    @ApiStatus.Internal
    public @NotNull String getFormat() {
        return format;
    }

    /**
     * Sets the format of this Channel. See channels.yml for placeholders.
     * @param format A String representation of the format.
     * @return this
     */
    public ChannelBuilder setFormat(String format) {
        Preconditions.checkNotNull(format, "Format cannot be null");
        this.format = format;
        return this;
    }

    /**
     * Provides the Membership
     * @return The Membership
     */
    @ApiStatus.Internal
    public @NotNull Membership getMembership() {
        return membership;
    }

    /**
     * Sets the Membership
     * @param membership The Membership
     * @return this
     */
    public ChannelBuilder setMembership(Membership membership) {
        Preconditions.checkNotNull(membership, "Membership cannot be null");
        this.membership = membership;
        return this;
    }

    /**
     * Tells if the Channel should do cooldowns.
     * @return If cooldowns should be done.
     */
    @ApiStatus.Internal
    public boolean isDoCooldown() {
        return doCooldown;
    }

    /**
     * Tells if the Channel should do cooldowns (StringsModeration required)
     * @param doCooldown A boolean
     * @return this
     */
    public ChannelBuilder setDoCooldown(boolean doCooldown) {
        this.doCooldown = doCooldown;
        return this;
    }

    /**
     * Tells if the Channel is profanity filtering
     * @return A boolean
     */
    @ApiStatus.Internal
    public boolean isDoProfanityFilter() {
        return doProfanityFilter;
    }

    /**
     * Specifies if the Channel should profanity filter (StringsModeration required)
     * @param doProfanityFilter If the Channel should filter.
     * @return this
     */
    public ChannelBuilder setDoProfanityFilter(boolean doProfanityFilter) {
        this.doProfanityFilter = doProfanityFilter;
        return this;
    }

    /**
     * Tells if the Channel is URL filtering.
     * @return A boolean
     */
    @ApiStatus.Internal
    public boolean isDoUrlFilter() {
        return doUrlFilter;
    }

    /**
     * Sets if the Channel is URL filtering (StringsModeration required)
     * @param doUrlFilter IF the Channel should filter.
     * @return this
     */
    public ChannelBuilder setDoUrlFilter(boolean doUrlFilter) {
        this.doUrlFilter = doUrlFilter;
        return this;
    }

    /**
     * Tells if the Channel calls an event when a message is sent.
     * @return If the Channel calls an event when a message is sent.
     */
    @ApiStatus.Internal
    public boolean isCallEvent() {
        return callEvent;
    }

    /**
     * Sets if the Channel calls an event on a message send.
     * @param callEvent A boolean
     * @return this
     */
    public ChannelBuilder setCallEvent(boolean callEvent) {
        this.callEvent = callEvent;
        return this;
    }

    /**
     * Provides the Channel priority
     * @return An int
     */
    @ApiStatus.Internal
    public int getPriority() {
        return priority;
    }

    /**
     * Sets this Channel's priority
     * @param priority The int priority
     * @return this
     */
    public ChannelBuilder setPriority(@Range(from = -1, to = Integer.MAX_VALUE) int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Provides the worlds this Channel should have in scope.
     * @return A Set of Worlds.
     */
    @ApiStatus.Internal
    public Set<World> getWorlds() {
        return worlds != null ? worlds : Set.of();
    }

    /**
     * Sets the worlds this Channel contains if it's an instance of {@link LocalChannel}
     * @param worlds A Set of the worlds.
     * @return this
     */
    public ChannelBuilder setWorlds(final Set<World> worlds) {
        this.worlds = worlds;
        return this;
    }

    /**
     * Provides the proximity that messages will be sent around a player.
     * @return A double.
     */
    @ApiStatus.Internal
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the proximity that messages will be sent around a player.
     * Only applicable to ProximityChannel.
     * @param distance The double value of the distance.
     */
    public ChannelBuilder setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    /**
     * Provides the broadcast format
     * @return A String representation of the format.
     */
    @ApiStatus.Internal
    public String getBroadcastFormat() {
        return broadcastFormat != null ?
                broadcastFormat : "&8[&c" + this.getName() + "&8] &f{message}";
    }


    /**
     * Sets the broadcast format used with /channel broadcast <channel>
     * @param broadcastFormat The broadcast format
     * @return this
     */
    public ChannelBuilder setBroadcastFormat(String broadcastFormat) {
        this.broadcastFormat = broadcastFormat;
        return this;
    }
}
