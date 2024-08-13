package com.pedestriamc.strings.directmessage;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerDirectMessageEvent extends Event implements Cancellable {

    public static final HandlerList HANDLER_LIST = new HandlerList();
    private String message;
    private final Player sender;
    private final Player recipient;
    private boolean isCancelled;

    public PlayerDirectMessageEvent(Player sender, Player recipient, String message){
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    public static HandlerList getHandlerList(){
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers(){
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled(){
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled){
        this.isCancelled = isCancelled;
    }

    /**
     * @return event's message
     */
    public String getMessage(){
        return this.message;
    }

    /**
     * @param message message to replace event's message with
     */
    public void setMessage(String message){
        this.message = message;
    }

    /**
     * @return sender of the message
     */
    public Player getSender(){
        return this.sender;
    }

    /**
     * @return recipient of the message
     */
    public Player getRecipient(){
        return this.recipient;
    }
}
