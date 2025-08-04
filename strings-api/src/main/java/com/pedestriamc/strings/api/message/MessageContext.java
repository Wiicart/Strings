package com.pedestriamc.strings.api.message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.jetbrains.annotations.ApiStatus.Internal;

import java.util.Map;

/**
 * Used to batch Message sends with {@link Messenger#batchSend(MessageContext...)}
 */
public final class MessageContext {

    private final Message message;
    private final Messageable recipient;
    private final Map<String, String> placeholders;

    @NotNull
    public static MessageContext of(@NotNull Message message, @NotNull Map<String, String> placeholders, @NotNull Messageable recipient) {
        return new MessageContext(message, placeholders, recipient);
    }

    @NotNull
    public static MessageContext of(Message message, Messageable recipient) {
        return new MessageContext(message, null, recipient);
    }

    private MessageContext(Message message, Map<String, String> placeholders, Messageable recipient) {
        this.message = message;
        this.recipient = recipient;
        this.placeholders = placeholders;
    }

    @NotNull
    @Internal
    public Message message() {
        return message;
    }

    @NotNull
    @Internal
    public Messageable recipient() {
        return recipient;
    }

    @Nullable
    @Internal
    public Map<String, String> placeholders() {
        return placeholders;
    }

}
