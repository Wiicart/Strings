package com.pedestriamc.strings.api.message;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Used to tell Strings to send a message to a player.
 * Only supports messages in {@link Message}
 */
public interface Messenger {

    /**
     * Sends a Message to a Player, replacing placeholders.
     * @param message The Message
     * @param recipient The recipient of the Message
     * @param placeholders The placeholders (all occurrences of the key are replaced by the value)
     */
    void sendMessage(@NotNull Message message, @NotNull Messageable recipient, @NotNull Map<String, String> placeholders);

    /**
     * Sends a Message to a Player
     * @param message The Message
     * @param recipient The recipient of the Message
     */
    void sendMessage(@NotNull Message message, @NotNull Messageable recipient);

    /**
     * Batches message sends together using {@link MessageContext}(s)
     * @param contexts The MessageContexts
     */
    void batchSend(MessageContext @NotNull ... contexts);

}
