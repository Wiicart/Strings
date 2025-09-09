package com.pedestriamc.strings.api.event;

import com.pedestriamc.strings.api.user.StringsUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered after a player has joined and the {@link StringsUser} of the player has been loaded.
 */
public class StringsUserLoadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final StringsUser user;

    public StringsUserLoadEvent(@NotNull StringsUser user) {
        this.user = user;
    }

    @NotNull
    public StringsUser getUser() {
        return user;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
