package com.pedestriamc.strings.api.event.moderation;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This Event will be called if a player's message is changed due to filtering, whether it be for containing a URL,
 * or having restricted words.
 */
public final class PlayerChatFilteredEvent extends Event {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final String originalMessage;
    private final String filteredMessage;
    private final List<String> filteredElements;

    @ApiStatus.Internal
    public PlayerChatFilteredEvent(Player player, String originalMessage, String filteredMessage, List<String> filteredElements) {
        this.player = player;
        this.originalMessage = originalMessage;
        this.filteredMessage = filteredMessage;
        this.filteredElements = filteredElements;
    }

    /**
     * Provides the message sender.
     * @return The Player instance of the sender.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Provides the original message, before filtering.
     * @return A String.
     */
    public String getOriginalMessage() {
        return originalMessage;
    }

    /**
     * Provides the message post-filtering.
     * @return A string.
     */
    public String getFilteredMessage() {
        return filteredMessage;
    }

    /**
     * Provides a list of all words/links that were filtered out of the message.
     * @return A List of Strings.
     */
    @SuppressWarnings("unused")
    public List<String> getFilteredElements() {
        return filteredElements;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
