package com.pedestriamc.strings.event;

import com.pedestriamc.strings.api.event.moderation.MessageDeletionEvent;
import net.kyori.adventure.chat.SignedMessage.Signature;
import net.kyori.adventure.chat.SignedMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * Called whenever a message is deleted in Minecraft chat
 */
@SuppressWarnings("unused")
public class BukkitMessageDeletionEvent extends Event implements MessageDeletionEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final SignedMessage message;

    @Internal
    public BukkitMessageDeletionEvent(@NotNull SignedMessage message) {
        super(false);
        this.message = message;
    }

    /**
     * Provides the Adventure SignedMessage.
     * @return The message
     */
    public SignedMessage getMessage() {
        return message;
    }

    /**
     * Provides the SignedMessage's signature
     * @return The signature
     */
    public Signature getSignature() {
        return message.signature();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
