package com.pedestriamc.strings.api.event.moderation;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class PlayerSpamEvent extends Event {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final AsyncPlayerChatEvent event;

    @ApiStatus.Internal
    public PlayerSpamEvent(Player player, AsyncPlayerChatEvent event) {
        this.player = player;
        this.event = event;
    }

    public Player getPlayer() {
        return player;
    }

    public AsyncPlayerChatEvent getChatEvent() {
        return event;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
