package com.pedestriamc.strings.api.event;

import com.pedestriamc.strings.api.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * An AsyncPlayerChatEvent with the additional field of a Channel.
 */
@SuppressWarnings("unused")
public final class ChannelChatEvent extends AsyncPlayerChatEvent {

    private final Channel channel;
    private final boolean cancellable;

    /**
     * @param async   This changes the event to a synchronous state.
     * @param who     the chat sender
     * @param message the message sent
     * @param players the players to receive the message.
     * @param channel the channel which the message will be sent to
     */
    @ApiStatus.Internal
    public ChannelChatEvent(boolean async, @NotNull Player who, @NotNull String message, @NotNull Set<Player> players, Channel channel, boolean cancellable) {
        super(async, who, message, players);
        this.channel = channel;
        this.cancellable = cancellable;
    }

    /**
     * Provides the Channel the message is being sent to.
     * @return The Channel.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Provides if this Event is able to cancellable. On Paper servers, this is typically not cancellable.
     * @return If the event can be canceled.
     */
    public boolean cancellable() {
        return cancellable;
    }

    @Override
    public boolean isCancelled() {
        return cancellable && super.isCancelled();
    }

}
