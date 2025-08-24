package com.pedestriamc.strings.api.event.channel;

import com.pedestriamc.strings.api.channel.Channel;
import net.kyori.adventure.chat.SignedMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

/**
 * An AsyncPlayerChatEvent with the additional field of a {@link Channel}.
 */
@SuppressWarnings("unused")
public final class ChannelChatEvent extends AsyncPlayerChatEvent {

    private static final boolean IS_PAPER;
    static {
        boolean paperDetected = false;
        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
            paperDetected = true;
        } catch(ClassNotFoundException ignored) {}

        IS_PAPER = paperDetected;
    }

    // If Paper - supply own HandlerList, otherwise resort to super class.
    private static final HandlerList HANDLERS = new HandlerList();

    private final Channel channel;
    private final SignedMessage message;
    private final boolean cancellable;

    /**
     * @param async   This changes the event to a synchronous state.
     * @param who     the chat sender
     * @param message the message sent
     * @param players the players to receive the message.
     * @param channel the channel which the message will be sent to
     */
    @ApiStatus.Internal
    public ChannelChatEvent(
            boolean async,
            @NotNull Player who,
            @NotNull String message,
            @NotNull Set<Player> players,
            @NotNull Channel channel,
            boolean cancellable
    ) {
        super(async, who, message, players);
        this.channel = channel;
        this.cancellable = cancellable;
        this.message = null;
    }

    @ApiStatus.Internal
    public ChannelChatEvent(
            boolean async,
            @NotNull Player who,
            @NotNull String message,
            @NotNull Set<Player> players,
            @NotNull Channel channel,
            @NotNull SignedMessage signedMessage,
            boolean cancellable
    ) {
        super(async, who, message, players);
        this.channel = channel;
        this.cancellable = cancellable;
        this.message = signedMessage;
    }

    /**
     * Provides the Channel the message is being sent to.
     * @return The Channel.
     */
    public Channel getChannel() {
        return channel;
    }

    @NotNull
    @Contract(pure = true)
    public Optional<SignedMessage> getSignedMessage() {
        return Optional.ofNullable(message);
    }

    /**
     * Provides if this Event is cancellable. On Paper servers, this is typically not cancellable.
     * @return If the event can be canceled.
     */
    public boolean cancellable() {
        return cancellable;
    }

    @Override
    public boolean isCancelled() {
        return cancellable && super.isCancelled();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        if (IS_PAPER) {
            return HANDLERS;
        } else {
            return super.getHandlers();
        }
    }

    @NotNull
    public static HandlerList getHandlerList() {
        if (IS_PAPER) {
            return HANDLERS;
        } else {
            return AsyncPlayerChatEvent.getHandlerList();
        }
    }
}
