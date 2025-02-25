package com.pedestriamc.strings.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * An Event that is triggered when someone sends a direct message to a player with the plugin Strings.
 */
public final class PlayerDirectMessageEvent extends Event implements Cancellable {

    public static final HandlerList HANDLER_LIST = new HandlerList();
    private String message;
    private final Player sender;
    private final Player recipient;
    private boolean isCancelled;

    /**
     * Constructor for the PlayerDirectMessageEvent.
     * @param sender The sender of the direct message.
     * @param recipient The recipient of the direct message.
     * @param message The message being sent.
     */
    public PlayerDirectMessageEvent(Player sender, Player recipient, String message){
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
    public String getMessage() {
        return this.message;
    }

    /**
     * Sets the message for the direct message.
     * @param message The message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Provides the sender of the direct message.
     * @return The Player sender.
     */
    public Player getSender() {
        return this.sender;
    }

    /**
     * Provides the recipient of the message.
     * @return The Player recipient.
     */
    public Player getRecipient() {
        return this.recipient;
    }
}
