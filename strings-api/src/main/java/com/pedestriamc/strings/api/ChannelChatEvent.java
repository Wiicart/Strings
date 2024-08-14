package com.pedestriamc.strings.api;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * An AsyncPlayerChatEvent with the additional field of a Channel.
 */
public class ChannelChatEvent extends AsyncPlayerChatEvent {

    private final StringsChannel channel;

    /**
     * @param async   This changes the event to a synchronous state.
     * @param who     the chat sender
     * @param message the message sent
     * @param players the players to receive the message. This may be a lazy
     * @param channel the channel which the message will be sent to
     */
    public ChannelChatEvent(boolean async, @NotNull Player who, @NotNull String message, @NotNull Set<Player> players, StringsChannel channel) {
        super(async, who, message, players);
        this.channel = channel;
    }

    /**
     * Provides the Channel the message is being sent to.
     * @return The Channel.
     */
    public StringsChannel getChannel(){
        return channel;
    }
}
