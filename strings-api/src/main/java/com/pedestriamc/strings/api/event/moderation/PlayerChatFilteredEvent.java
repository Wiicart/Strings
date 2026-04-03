package com.pedestriamc.strings.api.event.moderation;

import com.pedestriamc.strings.api.event.strings.StringsEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.util.List;

/**
 * This Event will be called if a player's message is changed due to filtering, whether it be for containing a URL,
 * or having restricted words.
 */
public final class PlayerChatFilteredEvent implements StringsEvent {

    private final StringsUser player;
    private final String originalMessage;
    private final String filteredMessage;
    private final List<String> filteredElements;

    @Internal
    public PlayerChatFilteredEvent(StringsUser player, String originalMessage, String filteredMessage, List<String> filteredElements) {
        this.player = player;
        this.originalMessage = originalMessage;
        this.filteredMessage = filteredMessage;
        this.filteredElements = filteredElements;
    }

    /**
     * Provides the message sender.
     * @return The Player instance of the sender.
     */
    public StringsUser getPlayer() {
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

}
