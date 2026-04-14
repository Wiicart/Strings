package com.pedestriamc.strings.event;

import com.pedestriamc.strings.api.event.DirectMessageEvent;
import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * An Event that is triggered when someone sends a direct message to a player with the plugin Strings.
 */
public final class BukkitDirectMessageEvent extends Event implements Cancellable, DirectMessageEvent {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private String message;

    private final StringsUser sender;
    private final StringsUser recipient;

    private boolean isCancelled;

    /**
     * Constructor for the PlayerDirectMessageEvent.
     * @param sender The sender of the direct message.
     * @param recipient The recipient of the direct message.
     * @param message The message being sent.
     */
    @Internal
    public BukkitDirectMessageEvent(StringsUser sender, StringsUser recipient, String message) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Provides Handlers
     * @return A HandlerList
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Checks if the Event is canceled.
     * @return A boolean.
     */
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * Sets if the Event should be canceled.
     * @param isCancelled boolean if the event should be canceled.
     */
    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    /**
     * Provides the message.
     * @return A String of the message.
     */
    @NotNull
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the message for the direct message.
     * @param message The message to be set.
     */
    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    /**
     * Provides the sender of the direct message.
     * @return The Player sender.
     */
    @NotNull
    @Override
    public StringsUser getSender() {
        return this.sender;
    }

    /**
     * Provides the recipient of the message.
     * @return The Player recipient.
     */
    @NotNull
    @Override
    public StringsUser getRecipient() {
        return this.recipient;
    }

    /**
     * Tells if the message will be ignored, because the recipient is ignoring the sender.
     * @return true/false
     */
    public boolean isIgnored() {
        return recipient.isIgnoring(sender);
    }
}
