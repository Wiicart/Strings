package com.pedestriamc.strings.api.channel.data;

import com.google.common.base.Preconditions;
import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;

import static org.jetbrains.annotations.ApiStatus.Internal;

/**
 * A class that holds data to create a new {@link Channel}.
 */
@Platform.Agnostic
@Internal
@SuppressWarnings("unchecked")
abstract sealed class AbstractChannelBuilder<B extends IChannelBuilder<B>> implements IChannelBuilder<B> permits ChannelBuilder, LocalChannelBuilder {

    // These fields may not be null at any point.
    private @NotNull String name;
    private @NotNull String format;
    private @NotNull Membership membership;

    // Nullable fields, default values should be provided where necessary if null.
    private @Nullable String defaultColor;
    private @Nullable String broadcastFormat;

    // May remain null after Channel construction
    private @Nullable Sound broadcastSound = null;

    @Range(from = -1, to = Integer.MAX_VALUE)
    private int priority = 0;
    private boolean doCooldown = false;
    private boolean doProfanityFilter = false;
    private boolean doUrlFilter = false;
    private boolean callEvent = true;
    private boolean allowMessageDeletion = true;

    /**
     * ChannelBuilder constructor with universally required parameters.
     * @param name The Channel name.
     * @param format The Channel format.
     * @param membership The Channel membership
     */
    AbstractChannelBuilder(@NotNull String name, @NotNull String format, @NotNull Membership membership) {
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
    @Override
    public Channel build(@NotNull Identifier type) throws IllegalArgumentException {
        return BuilderRegistry.build(this, type);
    }

    /**
     * Provides the Channel name.
     * @return The Chanel name.
     */
    @Override
    public @NotNull String getName() {
        return name;
    }

    /**
     * Sets the Channel's name.
     * @param name The new Channel name.
     * @return this
     */
    @Override
    public @NotNull B setName(@NotNull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        this.name = name;
        return (B) this;
    }

    /**
     * Provides the Channel's default color.
     * @return A String representation of the default color.
     */
    @Override
    public @NotNull String getDefaultColor() {
        return defaultColor != null ? defaultColor : "";
    }

    /**
     * Sets the Channel's default color.
     * @param defaultColor The new default color.
     * @return this
     */
    @Override
    public @NotNull B setDefaultColor(@NotNull String defaultColor) {
        this.defaultColor = defaultColor;
        return (B) this;
    }

    /**
     * Provides the Channel's format when messages are sent in chat.
     * @return A String containing the format.
     */
    @Override
    public @NotNull String getFormat() {
        return format;
    }

    /**
     * Sets the format of this Channel. See channels.yml for placeholders.
     * @param format A String representation of the format.
     * @return this
     */
    @Override
    public @NotNull B setFormat(@NotNull String format) {
        Preconditions.checkNotNull(format, "Format cannot be null");
        this.format = format;
        return (B) this;
    }

    /**
     * Provides the Membership
     * @return The Membership
     */
    @Override
    public @NotNull Membership getMembership() {
        return membership;
    }

    /**
     * Sets the Membership
     * @param membership The Membership
     * @return this
     */
    @Override
    public @NotNull B setMembership(@NotNull Membership membership) {
        Preconditions.checkNotNull(membership, "Membership cannot be null");
        this.membership = membership;
        return (B) this;
    }

    /**
     * Tells if the Channel should do cooldowns.
     * @return If cooldowns should be done.
     */
    @Override
    public boolean isDoCooldown() {
        return doCooldown;
    }

    /**
     * Tells if the Channel should do cooldowns (StringsModeration required)
     * @param doCooldown A boolean
     * @return this
     */
    @Override
    public @NotNull B setDoCooldown(boolean doCooldown) {
        this.doCooldown = doCooldown;
        return (B) this;
    }

    /**
     * Tells if the Channel is profanity filtering
     * @return A boolean
     */
    @Override
    public boolean isDoProfanityFilter() {
        return doProfanityFilter;
    }

    /**
     * Specifies if the Channel should profanity filter (StringsModeration required)
     * @param doProfanityFilter If the Channel should filter.
     * @return this
     */
    @Override
    public @NotNull B setDoProfanityFilter(boolean doProfanityFilter) {
        this.doProfanityFilter = doProfanityFilter;
        return (B) this;
    }

    /**
     * Tells if the Channel is URL filtering.
     * @return A boolean
     */
    @Override
    public boolean isDoUrlFilter() {
        return doUrlFilter;
    }

    /**
     * Sets if the Channel is URL filtering (StringsModeration required)
     * @param doUrlFilter IF the Channel should filter.
     * @return this
     */
    @Override
    public @NotNull B setDoUrlFilter(boolean doUrlFilter) {
        this.doUrlFilter = doUrlFilter;
        return (B) this;
    }

    /**
     * Tells if the Channel calls an event when a message is sent.
     * @return If the Channel calls an event when a message is sent.
     */
    @Override
    public boolean isCallEvent() {
        return callEvent;
    }

    /**
     * Sets if the Channel calls an event on a message send.
     * @param callEvent A boolean
     * @return this
     */
    @Override
    public @NotNull B setCallEvent(boolean callEvent) {
        this.callEvent = callEvent;
        return (B) this;
    }

    @Override
    public B setAllowMessageDeletion(boolean allowMessageDeletion) {
        this.allowMessageDeletion = allowMessageDeletion;
        return (B) this;
    }

    @Override
    public boolean allowsMessageDeletion() {
        return allowMessageDeletion;
    }

    /**
     * Provides the Channel priority
     * @return An int
     */
    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * Sets this Channel's priority
     * @param priority The int priority
     * @return this
     */
    @Override
    public @NotNull B setPriority(@Range(from = -1, to = Integer.MAX_VALUE) int priority) {
        this.priority = priority;
        return (B) this;
    }

    /**
     * Provides the broadcast format
     * @return A String representation of the format.
     */
    @Override
    public String getBroadcastFormat() {
        return broadcastFormat != null ?
                broadcastFormat : "&8[&c" + this.getName() + "&8] &f{message}";
    }


    /**
     * Sets the broadcast format used with /channel broadcast <channel>
     * @param broadcastFormat The broadcast format
     * @return this
     */
    @Override
    public @NotNull B setBroadcastFormat(@NotNull String broadcastFormat) {
        this.broadcastFormat = broadcastFormat;
        return (B) this;
    }

    @Override
    @Nullable
    public Sound getBroadcastSound() {
        return broadcastSound;
    }

    @Override
    public B setBroadcastSound(@Nullable Sound broadcastSound) {
        Objects.requireNonNull(broadcastSound, "Broadcast sound cannot be null.");
        this.broadcastSound = broadcastSound;
        return null;
    }

    static boolean isLocal(@NotNull Identifier identifier) {
        return
                identifier == Identifier.WORLD ||
                identifier == Identifier.PROXIMITY ||
                identifier == Identifier.WORLD_STRICT ||
                identifier == Identifier.PROXIMITY_STRICT;
    }
}
