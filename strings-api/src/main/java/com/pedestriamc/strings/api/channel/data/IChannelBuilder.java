package com.pedestriamc.strings.api.channel.data;

import com.pedestriamc.strings.api.annotation.Platform;
import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.channel.Membership;
import com.pedestriamc.strings.api.channel.Type;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Locale;

import static org.jetbrains.annotations.ApiStatus.Internal;

/**
 * An interface for building a {@link Channel}.<br/>
 * Implemented by {@link ChannelBuilder} and {@link LocalChannelBuilder}
 * @param <B> The ChannelBuilder implementation
 */
@Platform.Agnostic
@Internal
public sealed interface IChannelBuilder<B extends IChannelBuilder<B>> permits AbstractChannelBuilder {

    /**
     * Builds a Channel
     * @param type The Type of Channel to construct. Refer to {@link Identifier}
     *
     * @return A new Channel of the specified type.
     * @throws IllegalArgumentException If not all required values are filled out in this ChannelBuilder,
     * or the Channel type is unknown.
     */
    Channel build(@NotNull Identifier type) throws IllegalArgumentException;

    /**
     * Provides the Channel name.
     * @return The Chanel name.
     */
    @Internal
    @NotNull
    String getName();

    /**
     * Sets the Channel's name.
     * @param name The new Channel name.
     * @return this
     */
    @NotNull
    B setName(@NotNull String name);

    /**
     * Provides the Channel's default color.
     * @return A String representation of the default color.
     */
    @Internal
    @NotNull
    String getDefaultColor();

    /**
     * Sets the Channel's default color.
     * @param defaultColor The new default color.
     * @return this
     */
    @NotNull
    B setDefaultColor(@NotNull String defaultColor);

    /**
     * Provides the Channel's format when messages are sent in chat.
     * @return A String containing the format.
     */
    @Internal
    @NotNull
    String getFormat();

    /**
     * Sets the format of this Channel. See channels.yml for placeholders.
     * @param format A String representation of the format.
     * @return this
     */
    @NotNull
    B setFormat(@NotNull String format);

    /**
     * Provides the Membership
     * @return The Membership
     */
    @Internal
    @NotNull
    Membership getMembership();

    /**
     * Sets the Membership
     * @param membership The Membership
     * @return this
     */
    @NotNull
    B setMembership(@NotNull Membership membership);

    /**
     * Tells if the Channel should do cooldowns.
     * @return If cooldowns should be done.
     */
    @Internal
    boolean isDoCooldown();

    /**
     * Tells if the Channel should do cooldowns (StringsModeration required)
     * @param doCooldown A boolean
     * @return this
     */
    @NotNull
    B setDoCooldown(boolean doCooldown);

    /**
     * Tells if the Channel is profanity filtering
     * @return A boolean
     */
    @Internal
    boolean isDoProfanityFilter();

    /**
     * Specifies if the Channel should profanity filter (StringsModeration required)
     * @param doProfanityFilter If the Channel should filter.
     * @return this
     */
    @NotNull
    B setDoProfanityFilter(boolean doProfanityFilter);

    /**
     * Tells if the Channel is URL filtering.
     * @return A boolean
     */
    @Internal
    boolean isDoUrlFilter();

    /**
     * Sets if the Channel is URL filtering (StringsModeration required)
     * @param doUrlFilter IF the Channel should filter.
     * @return this
     */
    @NotNull
    B setDoUrlFilter(boolean doUrlFilter);

    /**
     * Tells if the Channel calls an event when a message is sent.
     * @return If the Channel calls an event when a message is sent.
     */
    @Internal
    boolean isCallEvent();

    /**
     * Sets if the Channel calls an event on a message send.
     * @param callEvent A boolean
     * @return this
     */
    @NotNull
    B setCallEvent(boolean callEvent);

    /**
     * Sets if this Channel allows message deletion
     * @param allowMessageDeletion If messages should be allowed to be deleted or not
     * @return this
     */
    B setAllowMessageDeletion(boolean allowMessageDeletion);

    /**
     * Tells if this Channel allows message deletion
     * @return A boolean
     */
    @Internal
    boolean allowsMessageDeletion();

    /**
     * Provides the Channel priority
     * @return An int
     */
    @Internal
    int getPriority();

    /**
     * Sets this Channel's priority
     * @param priority The int priority
     * @return this
     */
    @NotNull
    B setPriority(@Range(from = -1, to = Integer.MAX_VALUE) int priority);

    /**
     * Provides the broadcast format
     * @return A String representation of the format.
     */
    @Internal
    String getBroadcastFormat();

    /**
     * Sets the broadcast format used with /channel broadcast (channel)
     * @param broadcastFormat The broadcast format
     * @return this
     */
    @NotNull
    B setBroadcastFormat(@NotNull String broadcastFormat);

    /**
     * Provides the broadcast Sound
     * @return The adventure Sound
     */
    @Internal
    @Nullable
    Sound getBroadcastSound();

    /**
     * Sets the Sound played when a message is broadcast to this Channel
     * @param broadcastSound The {@link Sound}
     * @return this
     */
    B setBroadcastSound(@Nullable Sound broadcastSound);

    /**
     * As some Channel {@link Type}(s) have multiple implementations, this enum exists to specify which one to build.
     */
    enum Identifier {
        /**
         * A standard StringChannel.
         * Corresponds to <code>Type.NORMAL</code>
         */
        NORMAL(false),
        /**
         * A standard WorldChannel.
         * Corresponds to <code>Type.WORLD</code>
         */
        WORLD(true),
        /**
         * A strict WorldChannel.
         * Corresponds to <code>Type.WORLD</code>
         */
        WORLD_STRICT(true),
        /**
         * A standard ProximityChannel.
         * Corresponds to <code>Type.PROXIMITY</code>
         */
        PROXIMITY(true),
        /**
         * A strict ProximityChannel.
         * Corresponds to <code>Type.PROXIMITY</code>
         */
        PROXIMITY_STRICT(true),
        /**
         * A standard ProximityChannel, using horizontal distance.
         * Corresponds to <code>Type.PROXIMITY</code>
         */
        HORIZONTAL_PROXIMITY(true),
        /**
         * A strict ProximityChannel, using horizontal distance.
         * Corresponds to <code>Type.PROXIMITY</code>
         */
        HORIZONTAL_PROXIMITY_STRICT(true),
        /**
         * A HelpOPChannel.
         * Corresponds to <code>Type.PROTECTED</code>
         */
        HELPOP(false);

        public static Identifier of(@NotNull String identifier) {
            return switch(identifier.toLowerCase(Locale.ROOT)) {
                case "stringchannel" -> NORMAL;
                case "proximity" -> PROXIMITY;
                case "proximity_strict" -> PROXIMITY_STRICT;
                case "world" -> WORLD;
                case "world_strict" -> WORLD_STRICT;
                case "helpop" -> HELPOP;
                case "horizontal_proximity" -> HORIZONTAL_PROXIMITY;
                case "horizontal_proximity_strict" -> HORIZONTAL_PROXIMITY_STRICT;
                default -> throw new IllegalArgumentException("Unknown identifier '" + identifier + "'");
            };
        }

        private final boolean isLocal;

        Identifier(boolean isLocal) {
            this.isLocal = isLocal;
        }

        public boolean isLocal() {
            return isLocal;
        }

        @Override
        public String toString() {
            return switch(this) {
                case NORMAL -> "stringchannel";
                case WORLD -> "world";
                case WORLD_STRICT -> "world_strict";
                case PROXIMITY -> "proximity";
                case PROXIMITY_STRICT -> "proximity_strict";
                case HORIZONTAL_PROXIMITY ->  "horizontal_proximity";
                case HORIZONTAL_PROXIMITY_STRICT -> "horizontal_proximity_strict";
                case HELPOP -> "helpop";
            };
        }
    }
}

