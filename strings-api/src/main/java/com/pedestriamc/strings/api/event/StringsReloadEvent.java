package com.pedestriamc.strings.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the Strings plugin reloads.
 */
public final class StringsReloadEvent extends Event {

    @ApiStatus.Internal
    StringsReloadEvent() {

    }

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
