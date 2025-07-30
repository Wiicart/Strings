package com.pedestriamc.strings.api.event.channel;

import com.pedestriamc.strings.api.channel.Channel;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a User joins or leaves a Channel.
 * Check the Type with {@link UserChannelEvent#getType()}
 */
@SuppressWarnings("unused")
public final class UserChannelEvent extends Event {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull Channel channel;
    private final @NotNull StringsUser user;
    private final @NotNull Type type;

    /**
     * Sole constructor
     * @param channel The Channel associated with the Event
     * @param user The User associated with the Event
     * @param type The type of Event being called
     */
    @ApiStatus.Internal
    public UserChannelEvent(@NotNull Channel channel, @NotNull StringsUser user, @NotNull Type type) {
        this.channel = channel;
        this.user = user;
        this.type = type;
    }

    /**
     * Provides the {@link Channel} associated with the Event.
     * @return The Channel.
     */
    public @NotNull Channel getChannel() {
        return channel;
    }

    /**
     * Provides the {@link StringsUser} associated with the Event.
     * @return The User
     */
    public @NotNull StringsUser getUser() {
        return user;
    }

    /**
     * Tells what {@link Type} Event this is.
     * @return The Event Type.
     */
    public @NotNull Type getType() {
        return type;
    }

    /**
     * Represents what type of Event has occurred.
     */
    public enum Type {
        /**
         * Used when a Player's active Channel has changed.
         */
        UPDATE_ACTIVE,
        /**
         * Used when a Player has joined a Channel.
         */
        JOIN,
        /**
         * Used when a Player has left a Channel.
         */
        LEAVE,
        /**
         * Used when a Player starts monitoring a Channel.
         */
        MONITOR,
        /**
         * Used when a Player stops monitoring a Channel.
         */
        UNMONITOR,
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
