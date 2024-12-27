package com.pedestriamc.strings.api.event;

import com.pedestriamc.strings.api.channels.StringsChannel;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a message is processed with Strings.
 * Not directly cancellable, cancel the AsyncPlayerChatEvent itself.
 */
public final class StringsChatEvent extends Event {

    public static final HandlerList handlerList = new HandlerList();

    private final AsyncPlayerChatEvent asyncPlayerChatEvent;
    private final StringsChannel channel;

    public StringsChatEvent(AsyncPlayerChatEvent asyncPlayerChatEvent, StringsChannel channel) {
        this.asyncPlayerChatEvent = asyncPlayerChatEvent;
        this.channel = channel;
    }

    /**
     * Provides the AsyncPlayerChat event this Event is tied to
     * @return AsyncPlayerChatEvent
     */
    public AsyncPlayerChatEvent getAsyncPlayerChatEvent() {
        return asyncPlayerChatEvent;
    }

    /**
     * Provides the StringsChannel this message is being sent to
     * @return The StringsChannel
     */
    public StringsChannel getChannel() {
        return channel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){ return handlerList; }

}
