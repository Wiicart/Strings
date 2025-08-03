package com.pedestriamc.strings.api.message;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Used to batch Message sends with {@link Messenger#batchSend(MessageContext...)}
 */
public final class MessageContext {

    private final Message message;
    private final CommandSender sender;
    private final Map<String, String> placeholders;

    @NotNull
    public static MessageContext of(@NotNull Message message, @NotNull Map<String, String> placeholders, @NotNull CommandSender recipient) {
        return new MessageContext(message, placeholders, recipient);
    }

    @NotNull
    public static MessageContext of(Message message, CommandSender recipient) {
        return new MessageContext(message, null, recipient);
    }

    private MessageContext(Message message, Map<String, String> placeholders, CommandSender recipient) {
        this.message = message;
        this.sender = recipient;
        this.placeholders = placeholders;
    }

    @NotNull
    Message message() {
        return message;
    }

    @NotNull
    CommandSender recipient() {
        return sender;
    }

    @Nullable
    Map<String, String> placeholders() {
        return placeholders;
    }

}
